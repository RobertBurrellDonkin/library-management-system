package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;

import java.util.List;
import java.util.Optional;

/**
 * Manages books in a library.
 */
public interface LibraryManagementService {
    /**
     * Adds a book to the library when it is not already present.
     *
     * @param book not null
     * @return true when the book is not already present in the library,
     * false when the book is already present in the library.
     */
    boolean addBook(Book book);

    /**
     * Removes a book from the library when it is present.
     *
     * @param isbn not null
     * @return true when the book is already present in the library,
     * false when the book is not already present in the library.
     */
    boolean removeBook(String isbn);

    /**
     * Finds a book by unique ISBN.
     *
     * @param isbn not null
     * @return the book when it is in the library, otherwise empty.
     */
    Optional<Book> findBookByISBN(String isbn);

    /**
     * Finds all books in the library by the given author.
     *
     * @param author not null
     * @return books in the library by the given author, otherwise empty
     */
    List<Book> findBooksByAuthor(String author);

    /**
     * Borrows a book, decreasing the available copies by one.
     *
     * @param isbn not null
     * @return true when a book with the given ISBN is present in the library,
     * false otherwise
     * @throws NoAvailableCopiesException when there are no copies available to borrow
     */
    boolean borrowBook(String isbn) throws NoAvailableCopiesException;

    /**
     * Returns a book, incrementing the available copies by one.
     *
     * @param isbn not null
     * @return true when a book with the given ISBN is present in the library,
     * false otherwise
     */
    boolean returnBook(String isbn);
}
