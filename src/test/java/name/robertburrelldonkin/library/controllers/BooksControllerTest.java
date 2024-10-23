package name.robertburrelldonkin.library.controllers;

import name.robertburrelldonkin.library.domain.Book;
import name.robertburrelldonkin.library.services.LibraryManagementService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static name.robertburrelldonkin.library.domain.Book.aBook;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BooksController.class)
@ActiveProfiles("insecure")
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
        void whenBookIsMissingIsbn() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(true);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(
                                            """
                                                    {
                                                      "title": "some-title",
                                                      "author": "some-author",
                                                      "publicationYear": 2001,
                                                      "availableCopies": 4
                                                    }
                                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.isbn", is("isbn is mandatory")))
                    .andExpect(content().contentType(APPLICATION_JSON));
        }

        @Test
        void whenBookIsMissingTitle() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(true);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(
                                            """
                                                    {
                                                      "isbn": "some-isbn",
                                                      "author": "some-author",
                                                      "publicationYear": 2001,
                                                      "availableCopies": 4
                                                    }
                                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title", is("title is mandatory")))
                    .andExpect(content().contentType(APPLICATION_JSON));
        }

        @Test
        void whenBookIsMissingAuthor() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(true);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(
                                            """
                                                    {
                                                      "isbn": "some-isbn",
                                                      "title": "some-title",
                                                      "publicationYear": 2001,
                                                      "availableCopies": 4
                                                    }
                                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.author", is("author is mandatory")))
                    .andExpect(content().contentType(APPLICATION_JSON));
        }

        @Test
        void whenBookIsPublicationYearIsNegative() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(true);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(
                                            """
                                                    {
                                                      "isbn": "some-isbn",
                                                      "title": "some-title",
                                                      "author": "some-author",
                                                      "publicationYear":-1,
                                                      "availableCopies": 4
                                                    }
                                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.publicationYear", is("publicationYear must be positive")))
                    .andExpect(content().contentType(APPLICATION_JSON));
        }

        @Test
        void whenBookIsPublicationYearIsZero() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(true);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(
                                            """
                                                    {
                                                      "isbn": "some-isbn",
                                                      "title": "some-title",
                                                      "author": "some-author",
                                                      "publicationYear": 0,
                                                      "availableCopies": 4
                                                    }
                                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.publicationYear", is("publicationYear must be positive")))
                    .andExpect(content().contentType(APPLICATION_JSON));
        }


        @Test
        void whenBookAvailableCopiesIsNegative() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(true);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(
                                            """
                                                    {
                                                      "isbn": "some-isbn",
                                                      "title": "some-title",
                                                      "author": "some-author",
                                                      "publicationYear": 2001,
                                                      "availableCopies":-1
                                                    }                                                    
                                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.availableCopies", is("availableCopies must be positive")))
                    .andExpect(content().contentType(APPLICATION_JSON));
        }


        @Test
        void whenBookAvailableCopiesIsZero() throws Exception {
            when(libraryManagementService.addBook(book)).thenReturn(true);

            mvc.perform(
                            post("/api/books")
                                    .contentType(APPLICATION_JSON)
                                    .content(
                                            """
                                                    {
                                                      "isbn": "some-isbn",
                                                      "title": "some-title",
                                                      "author": "some-author",
                                                      "publicationYear": 2001,
                                                      "availableCopies":0
                                                    }                                                    
                                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.availableCopies", is("availableCopies must be positive")))
                    .andExpect(content().contentType(APPLICATION_JSON));
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

    @Nested
    class FindBookByIsbn {

        final Book book = aBook()
                .withIsbn("some-isbn")
                .withTitle("some-title")
                .withAuthor("some-author")
                .withPublicationYear(2001)
                .withAvailableCopies(4)
                .build();

        @Test
        void whenBookIsNotPresent() throws Exception {
            when(libraryManagementService.findBookByISBN("some-isbn")).thenReturn(Optional.empty());

            mvc.perform(get("/api/books/some-isbn"))
                    .andExpect(status().isNotFound());

            verify(libraryManagementService).findBookByISBN("some-isbn");
        }

        @Test
        void whenBookIsPresent() throws Exception {
            when(libraryManagementService.findBookByISBN("some-isbn")).thenReturn(Optional.of(book));

            mvc.perform(get("/api/books/some-isbn"))
                    .andExpect(jsonPath("$.isbn", is("some-isbn")))
                    .andExpect(jsonPath("$.title", is("some-title")))
                    .andExpect(jsonPath("$.author", is("some-author")))
                    .andExpect(jsonPath("$.publicationYear", is(2001)))
                    .andExpect(jsonPath("$.availableCopies", is(4)))
                    .andExpect(status().isOk());

            verify(libraryManagementService).findBookByISBN("some-isbn");

        }
    }

    @Nested
    class FindBookByAuthor {

        final Book book = aBook()
                .withIsbn("some-isbn")
                .withTitle("some-title")
                .withAuthor("some-author")
                .withPublicationYear(2001)
                .withAvailableCopies(4)
                .build();

        @Test
        void whenNoBooksHaveGivenAuthor() throws Exception {
            when(libraryManagementService.findBooksByAuthor("some-author")).thenReturn(List.of());

            mvc.perform(get("/api/books")
                            .queryParam("author", "some-author"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(libraryManagementService).findBooksByAuthor("some-author");
        }

        @Test
        void whenBooksHaveGivenAuthor() throws Exception {
            when(libraryManagementService.findBooksByAuthor("some-author")).thenReturn(List.of(book));

            mvc.perform(get("/api/books")
                            .queryParam("author", "some-author"))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].isbn", is("some-isbn")))
                    .andExpect(jsonPath("$[0].title", is("some-title")))
                    .andExpect(jsonPath("$[0].author", is("some-author")))
                    .andExpect(jsonPath("$[0].publicationYear", is(2001)))
                    .andExpect(jsonPath("$[0].availableCopies", is(4)))
                    .andExpect(status().isOk());

            verify(libraryManagementService).findBooksByAuthor("some-author");
        }
    }

    @Nested
    class BorrowBook {

        @Test
        void whenBookIsNotPresent() throws Exception {
            when(libraryManagementService.borrowBook("some-isbn")).thenReturn(false);

            mvc.perform(post("/api/books/some-isbn/borrow"))
                    .andExpect(status().isNotFound());

            verify(libraryManagementService).borrowBook("some-isbn");
        }

        @Test
        void whenBookIsPresent() throws Exception {
            when(libraryManagementService.borrowBook("some-isbn")).thenReturn(true);

            mvc.perform(post("/api/books/some-isbn/borrow"))
                    .andExpect(status().isOk());

            verify(libraryManagementService).borrowBook("some-isbn");
        }
    }

    @Nested
    class ReturnBook {

        @Test
        void whenBookIsNotPresent() throws Exception {
            when(libraryManagementService.returnBook("some-isbn")).thenReturn(false);

            mvc.perform(post("/api/books/some-isbn/return"))
                    .andExpect(status().isNotFound());

            verify(libraryManagementService).returnBook("some-isbn");
        }

        @Test
        void whenBookIsPresent() throws Exception {
            when(libraryManagementService.returnBook("some-isbn")).thenReturn(true);

            mvc.perform(post("/api/books/some-isbn/return"))
                    .andExpect(status().isOk());

            verify(libraryManagementService).returnBook("some-isbn");
        }
    }
}