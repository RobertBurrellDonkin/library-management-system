package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Thread safe library management service
 */
public class Library {

    /**
     * Books indexed by unique ISBN.
     * ConcurrentSkipListMap is efficient but weakly consistent. TODO
     * A front side cache could be used to optimize slower searches.
     */
    private final ConcurrentMap<String, Book> books = new ConcurrentSkipListMap<>();

    /**
     * Adds a book to the library when it is not already present.
     *
     * @param book not null
     * @return true when the book is not already present in the library,
     * false when the book is already present in the library.
     */
    public boolean addBook(Book book) {
        return isNull(books.put(book.getIsbn(), book));
    }

    /**
     * Removes a book from the library when it is present.
     * @param isbn not null
     * @return true when the book is already present in the library,
     * false when the book is not already present in the library.
     */
    public boolean removeBook(String isbn) {
        return nonNull(books.remove(isbn));
    }

    /**
     * Finds a book by unique ISBN.
     * @param isbn not null
     * @return the book when it is in the library, otherwise empty.
     */
    public Optional<Book> findBookByISBN(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }
}
