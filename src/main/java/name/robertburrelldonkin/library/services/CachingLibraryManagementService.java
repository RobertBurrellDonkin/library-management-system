package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.caches.BookCache;
import name.robertburrelldonkin.library.domain.Book;

import java.util.Optional;

public class CachingLibraryManagementService extends AbstractDelegateLibraryManagementService {

    private final BookCache bookCache;

    public CachingLibraryManagementService(LibraryManagementService delegate, BookCache bookCache) {
        super(delegate);
        this.bookCache = bookCache;
    }

    @Override
    public Optional<Book> findBookByISBN(String isbn) {
        return bookCache.get(isbn).or(() -> {
            final var book = super.findBookByISBN(isbn);
            book.ifPresent(bookCache::add);
            return book;
        });
    }

    @Override
    public boolean removeBook(String isbn) {
        bookCache.invalidate(isbn);
        return super.removeBook(isbn);
    }

    @Override
    public boolean borrowBook(String isbn) {
        bookCache.invalidate(isbn);
        return super.borrowBook(isbn);
    }

    @Override
    public boolean returnBook(String isbn) {
        bookCache.invalidate(isbn);
        return super.returnBook(isbn);
    }
}

