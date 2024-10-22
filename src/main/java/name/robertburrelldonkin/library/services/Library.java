package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static name.robertburrelldonkin.library.domain.Book.aBook;

/**
 * Thread safe library management service.
 */
public class Library implements LibraryManagementService {

    /**
     * Books indexed by unique ISBN.
     * ConcurrentSkipListMap is efficient but weakly consistent. TODO
     * A front side cache could be used to optimize slower searches.
     */
    private final ConcurrentMap<String, LibraryBook> books = new ConcurrentSkipListMap<>();

    @Override
    public boolean addBook(Book book) {
        return isNull(books.put(book.isbn(), toLibraryBook(book)));
    }

    @Override
    public boolean removeBook(String isbn) {
        return nonNull(books.remove(isbn));
    }

    @Override
    public Optional<Book> findBookByISBN(String isbn) {
        return Optional.ofNullable(books.get(isbn)).map(this::toBook);
    }


    @Override
    public List<Book> findBooksByAuthor(String author) {
        return books.values().stream()
                .filter(book -> author.equals(book.author()))
                .map(this::toBook)
                .toList();
    }

    @Override
    public boolean borrowBook(String isbn) {
        final var bookByISBN = Optional.ofNullable(books.get(isbn));
        bookByISBN.ifPresent(libraryBook -> libraryBook.availableCopies.decrementAndGet());
        return bookByISBN.isPresent();
    }

    @Override
    public boolean returnBook(String isbn) {
        final var bookByISBN = Optional.ofNullable(books.get(isbn));
        bookByISBN.ifPresent(libraryBook -> libraryBook.availableCopies.incrementAndGet());
        return bookByISBN.isPresent();
    }

    private LibraryBook toLibraryBook(Book book) {
        return new LibraryBook(
                book.isbn(),
                book.title(),
                book.author(),
                book.publicationYear(),
                new AtomicInteger(book.availableCopies()));
    }

    private Book toBook(LibraryBook libraryBook) {
        return aBook()
                .withIsbn(libraryBook.isbn)
                .withAuthor(libraryBook.author)
                .withTitle(libraryBook.title)
                .withPublicationYear(libraryBook.publicationYear)
                .withAvailableCopies(libraryBook.availableCopies.get())
                .build();
    }

    private record LibraryBook(String isbn,
                               String title,
                               String author,
                               int publicationYear,
                               AtomicInteger availableCopies) {
    }
}
