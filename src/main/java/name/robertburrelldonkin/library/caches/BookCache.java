package name.robertburrelldonkin.library.caches;

import name.robertburrelldonkin.library.domain.Book;

import java.util.Optional;

/**
 * Caches books by unique ISBN.
 */
public interface BookCache {

    /**
     * Clears the cached version for this book.
     * @param isbn not null
     */
    void invalidate(String isbn);

    /**
     * Caches the given book.
     * @param book not null
     */
    void add(Book book);

    /**
     * Gets the cached version of this book.
     *
     * @param isbn not null
     * @return the book if cached, otherwise empty
     */
    Optional<Book> get(String isbn);
}
