package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;

import java.util.List;
import java.util.Optional;

public abstract class DelegateLibraryManagementService implements LibraryManagementService {
    private final LibraryManagementService delegate;

    public DelegateLibraryManagementService(LibraryManagementService delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean addBook(Book book) {
        return delegate.addBook(book);
    }

    @Override
    public boolean removeBook(String isbn) {
        return delegate.removeBook(isbn);
    }

    @Override
    public Optional<Book> findBookByISBN(String isbn) {
        return delegate.findBookByISBN(isbn);
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        return delegate.findBooksByAuthor(author);
    }

    @Override
    public boolean borrowBook(String isbn) {
        return delegate.borrowBook(isbn);
    }

    @Override
    public boolean returnBook(String isbn) {
        return delegate.returnBook(isbn);
    }
}
