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
 * <p>Thread safe library management service.
 * </p>
 * <p>Based on weakly consistent ConcurrentSkipListMap which indexed books by ISBN.</p>
 * <ul>
 *     <li>Operations by ISBN are expected to be efficient.</li>
 *     <li>Addition and removal less so.</li>
 *     <li>Traversals (including searches by author) are expected to be slow. An upstream
 * cache by author should be considered.</li>
 * </ul>
 * <p>
 *     BorrowBook and ReturnBook mutate the internal LibraryBook representation using non-blocking
 *     algorithms after retrieval by ISBN. This is expected to be efficient under most realistic loads.
 * </p>
 */
public class Library implements LibraryManagementService {

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
        bookByISBN.ifPresent(this::decrementAvailableCopies);
        return bookByISBN.isPresent();
    }

    /**
     * Decrements available copies using CAS approach.
     * This is non-blocking approach to safely conditionally decrementing available copies.
     * Avoids the need to lock.
     *
     * @param libraryBook not null
     * @throws NoAvailableCopiesException if the available copies are zero at any stage
     */
    private void decrementAvailableCopies(LibraryBook libraryBook) throws NoAvailableCopiesException {
        // We loop until the value of available copies is unchanged during our validation check
        var success = false;
        while (!success) {
            final var availableCopies = libraryBook.availableCopies.get();
            // validation check
            if (availableCopies <= 0) {
                throw new NoAvailableCopiesException();
            }
            // Decrement the copies available provided that the value has not changed since the prior read
            // When the conditional decrement succeeds, exit the loop
            success = libraryBook.availableCopies.compareAndSet(availableCopies, availableCopies - 1);
        }
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
