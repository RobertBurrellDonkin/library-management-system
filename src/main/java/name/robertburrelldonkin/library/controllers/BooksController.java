package name.robertburrelldonkin.library.controllers;

import jakarta.validation.Valid;
import name.robertburrelldonkin.library.domain.Book;
import name.robertburrelldonkin.library.services.LibraryManagementService;
import name.robertburrelldonkin.library.services.NoAvailableCopiesException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    private final LibraryManagementService libraryManagementService;

    public BooksController(LibraryManagementService libraryManagementService) {
        this.libraryManagementService = libraryManagementService;
    }

    /**
     * Adds a book to the library.
     *
     * @param book not null
     * @return 201 when the book has been successfully added,
     * 409 when the book already exists
     */
    @PostMapping
    public ResponseEntity<String> addBook(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(libraryManagementService.addBook(book) ? CREATED : CONFLICT);
    }

    /**
     * Removes a book from the library.
     *
     * @param isbn not null
     * @return 200 when the book has been successfully removed,
     * 404 when a book with the given isbn is not in the library
     */
    @DeleteMapping("/{isbn}")
    public ResponseEntity<String> removeBook(@PathVariable String isbn) {
        return new ResponseEntity<>(libraryManagementService.removeBook(isbn) ? OK : NOT_FOUND);
    }

    /**
     * Gets details about a book from the library.
     *
     * @param isbn not null
     * @return 200 when the book is in the library
     * 404 when a book with the given isbn is not in the library
     */
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> findBookByISBN(@PathVariable String isbn) {
        return libraryManagementService
                .findBookByISBN(isbn)
                .map(book -> new ResponseEntity<>(book, OK))
                .orElse(new ResponseEntity<>(NOT_FOUND));
    }

    /**
     * Searches for books in the library by an author.
     *
     * @param author not null
     * @return list of books associated with the given author
     */
    @GetMapping()
    public List<Book> findBookByAuthor(@RequestParam("author") String author) {
        return libraryManagementService.findBooksByAuthor(author);

    }

    /**
     * Borrows a book from the library.
     *
     * @param isbn not null
     * @return 200 when the book has been successfully borrowed,
     * 404 when a book with the given isbn is not in the library
     */
    @PostMapping("/{isbn}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable String isbn) {
        return new ResponseEntity<>(libraryManagementService.borrowBook(isbn) ? OK : NOT_FOUND);
    }

    /**
     * Returns a book from the library.
     *
     * @param isbn not null
     * @return 200 when the book has been successfully returned,
     * 404 when a book with the given isbn is not in the library
     */
    @PostMapping("/{isbn}/return")
    public ResponseEntity<String> returnBook(@PathVariable String isbn) {
        return new ResponseEntity<>(libraryManagementService.returnBook(isbn) ? OK : NOT_FOUND);
    }

    @ResponseStatus(value = BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException methodArgumentNotValidException) {
        final Map<String, String> errors = new HashMap<>();
        methodArgumentNotValidException
                .getFieldErrors()
                .forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return errors;
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(NoAvailableCopiesException.class)
    public Map<String, String> handleNoAvailableCopiesException(NoAvailableCopiesException noAvailableCopiesException) {
        return Map.of("errorMessage", noAvailableCopiesException.getMessage());
    }
}
