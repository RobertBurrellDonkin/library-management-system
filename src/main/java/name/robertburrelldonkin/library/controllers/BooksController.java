package name.robertburrelldonkin.library.controllers;

import name.robertburrelldonkin.library.domain.Book;
import name.robertburrelldonkin.library.services.LibraryManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @return
     * 201 when the book has been successfully added,
     * 409 when the book already exists
     */
    @PostMapping
    @ResponseStatus(code = CREATED)
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        return new ResponseEntity<>(libraryManagementService.addBook(book) ? CREATED : CONFLICT);
    }

    /**
     * Removes a book from the library.
     *
     * @param isbn not null
     * @return
     * 200 when the book has been successfully removed,
     * 404 when a book with the given isbn is not in the library
     */
    @DeleteMapping("/{isbn}")
    @ResponseStatus(code = CREATED)
    public ResponseEntity<String> removeBook(@PathVariable String isbn) {
        return new ResponseEntity<>(libraryManagementService.removeBook(isbn) ? OK : NOT_FOUND);
    }
}
