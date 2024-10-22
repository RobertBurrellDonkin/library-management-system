package name.robertburrelldonkin.library.controllers;

import name.robertburrelldonkin.library.domain.Book;
import name.robertburrelldonkin.library.services.LibraryManagementService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static name.robertburrelldonkin.library.domain.Book.aBook;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BooksController.class)
class BooksControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LibraryManagementService libraryManagementService;

    @Nested
    class AddBook {
        final Book book = aBook()
                .withIsbn("some-isbn")
                .withTitle("some-title")
                .withAuthor("some-author")
                .withPublicationYear(2001)
                .withAvailableCopies(4)
                .build();

        final String json = """
                {
                  "isbn": "some-isbn",
                  "title": "some-title",
                  "author": "some-author",
                  "publicationYear": 2001,
                  "availableCopies": 4
                }
                """;

        @Test
        void whenBookIsNotPresent() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(true);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(json))
                    .andExpect(status().isCreated());

            verify(libraryManagementService).addBook(
                    aBook()
                            .withIsbn("some-isbn")
                            .withTitle("some-title")
                            .withAuthor("some-author")
                            .withPublicationYear(2001)
                            .withAvailableCopies(4)
                            .build());
        }

        @Test
        void whenBookIsPresent() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(false);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(json))
                    .andExpect(status().isConflict());

            verify(libraryManagementService).addBook(book);
        }
    }

    @Nested
    class RemoveBook {
        @Test
        void whenBookIsNotPresent() throws Exception {
            when(libraryManagementService.removeBook("some-isbn")).thenReturn(true);

            mvc.perform(delete("/api/books/some-isbn"))
                    .andExpect(status().isOk());

            verify(libraryManagementService).removeBook("some-isbn");
        }

        @Test
        void whenBookIsPresent() throws Exception {
            when(libraryManagementService.removeBook("some-isbn")).thenReturn(false);

            mvc.perform(delete("/api/books/some-isbn"))
                    .andExpect(status().isNotFound());

            verify(libraryManagementService).removeBook("some-isbn");
        }
    }
}