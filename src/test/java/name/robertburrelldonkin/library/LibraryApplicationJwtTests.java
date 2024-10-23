package name.robertburrelldonkin.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static name.robertburrelldonkin.library.authenticators.jwt.JwtTokenBuilder.createAValidToken;
import static name.robertburrelldonkin.library.authenticators.jwt.JwtTokenBuilder.createAnExpiredToken;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("secure")
class LibraryApplicationJwtTests {

    @LocalServerPort
    int port;

    WebTestClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void contextLoads() {
    }

    @Nested
    class NoToken {
        @Test
        void addBookIsForbidden() {
            addBook()
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void removeBookIsForbidden() {
            removeBook()
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void findBookByISBNsForbidden() {
            findBookByISBN()
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void findBooksByAuthorIsForbidden() {
            findBookByAuthor()
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void borrowBookIsForbidden() {
            borrowBook()
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void returnBookIsForbidden() {
            returnBook()
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }
    }

    @Nested
    class InvalidToken {
        @Test
        void addBookIsForbidden() {
            addBook()
                    .header(AUTHORIZATION, "Bearer invalid")
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void removeBookIsForbidden() {
            removeBook()
                    .header(AUTHORIZATION, "Bearer invalid")
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void findBookByISBNsForbidden() {
            findBookByISBN()
                    .header(AUTHORIZATION, "Bearer invalid")
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void findBooksByAuthorIsForbidden() {
            findBookByAuthor()
                    .header(AUTHORIZATION, "Bearer invalid")
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void borrowBookIsForbidden() {
            borrowBook()
                    .header(AUTHORIZATION, "Bearer invalid")
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void returnBookIsForbidden() {
            returnBook()
                    .header(AUTHORIZATION, "Bearer invalid")
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }
    }

    @Nested
    class ExpiredToken {
        @Test
        void addBookIsForbidden() {
            addBook()
                    .header(AUTHORIZATION, "Bearer " + createAnExpiredToken())
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void removeBookIsForbidden() {
            removeBook()
                    .header(AUTHORIZATION, "Bearer " + createAnExpiredToken())
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void findBookByISBNsForbidden() {
            findBookByISBN()
                    .header(AUTHORIZATION, "Bearer " + createAnExpiredToken())
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void findBooksByAuthorIsForbidden() {
            findBookByAuthor()
                    .header(AUTHORIZATION, "Bearer " + createAnExpiredToken())
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void borrowBookIsForbidden() {
            borrowBook()
                    .header(AUTHORIZATION, "Bearer " + createAnExpiredToken())
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }

        @Test
        void returnBookIsForbidden() {
            returnBook()
                    .header(AUTHORIZATION, "Bearer " + createAnExpiredToken())
                    .exchange()
                    .expectStatus()
                    .isForbidden();
        }
    }

    @Nested
    class ValidToken {

        String token;

        @BeforeEach
        void setUp() {
            token = createAValidToken();
        }

        @Test
        void endToEndRoundTrip() {
            webClient.get()
                    .uri("/api/books/some-isbn")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    .expectStatus()
                    .isNotFound();

            webClient.post()
                    .uri("/api/books")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 4
                            }
                            """)
                    .exchange()
                    .expectStatus()
                    .isCreated();

            webClient.get()
                    .uri("/api/books/some-isbn")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            {
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 4
                            }
                            """);

            webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/books")
                            .queryParam("author", "some-author")
                            .build())
                    .header(AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            [{
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 4
                            }]
                            """);

            webClient.post()
                    .uri("/api/books/some-isbn/borrow")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    .expectStatus()
                    .isOk();

            webClient.get()
                    .uri("/api/books/some-isbn")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            {
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 3
                            }
                            """);

            webClient.post()
                    .uri("/api/books/some-isbn/return")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    .expectStatus()
                    .isOk();

            webClient.get()
                    .uri("/api/books/some-isbn")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            {
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 4
                            }
                            """);

            webClient.delete()
                    .uri("/api/books/some-isbn")
                    .header(AUTHORIZATION, "Bearer " + token)
                    .exchange()
                    .expectStatus()
                    .isOk();
        }
    }

    @Nested
    class ValidTokenWithMultipleAuthorizationHeaders {

        String token;

        @BeforeEach
        void setUp() {
            token = createAValidToken();
        }

        @Test
        void endToEndRoundTrip() {
            webClient.get()
                    .uri("/api/books/some-isbn")
                    .headers(this::setAuthorizationHeaders)
                    .exchange()
                    .expectStatus()
                    .isNotFound();

            webClient.post()
                    .uri("/api/books")
                    .headers(this::setAuthorizationHeaders)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 4
                            }
                            """)
                    .exchange()
                    .expectStatus()
                    .isCreated();

            webClient.get()
                    .uri("/api/books/some-isbn")
                    .headers(this::setAuthorizationHeaders)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            {
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 4
                            }
                            """);

            webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/books")
                            .queryParam("author", "some-author")
                            .build())
                    .headers(this::setAuthorizationHeaders)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            [{
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 4
                            }]
                            """);

            webClient.post()
                    .uri("/api/books/some-isbn/borrow")
                    .headers(this::setAuthorizationHeaders)
                    .exchange()
                    .expectStatus()
                    .isOk();

            webClient.get()
                    .uri("/api/books/some-isbn")
                    .headers(this::setAuthorizationHeaders)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            {
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 3
                            }
                            """);

            webClient.post()
                    .uri("/api/books/some-isbn/return")
                    .headers(this::setAuthorizationHeaders)
                    .exchange()
                    .expectStatus()
                    .isOk();

            webClient.get()
                    .uri("/api/books/some-isbn")
                    .headers(this::setAuthorizationHeaders)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .json("""
                            {
                              "isbn": "some-isbn",
                              "title": "some-title",
                              "author": "some-author",
                              "publicationYear": 2001,
                              "availableCopies": 4
                            }
                            """);

            webClient.delete()
                    .uri("/api/books/some-isbn")
                    .headers(this::setAuthorizationHeaders)
                    .exchange()
                    .expectStatus()
                    .isOk();
        }

        private void setAuthorizationHeaders(HttpHeaders httpHeaders) {
            httpHeaders.addAll(AUTHORIZATION, List.of(
                    "Bearer InvalidToken",
                    "Bearer " + token,
                    "Basic SomeBasicAuth"
            ));
        }
    }

    private WebTestClient.RequestBodySpec returnBook() {
        return webClient.post()
                .uri("/api/books/some-isbn/return");
    }

    private WebTestClient.RequestBodySpec borrowBook() {
        return webClient.post()
                .uri("/api/books/some-isbn/borrow");
    }

    private WebTestClient.RequestHeadersSpec<?> findBookByAuthor() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/books")
                        .queryParam("author", "some-author")
                        .build());
    }

    private WebTestClient.RequestHeadersSpec<?> findBookByISBN() {
        return webClient.get()
                .uri("/api/books/some-isbn");
    }

    private WebTestClient.RequestHeadersSpec<?> removeBook() {
        return webClient.delete()
                .uri("/api/books/some-isbn");
    }

    private WebTestClient.RequestHeadersSpec<?> addBook() {
        return webClient.post()
                .uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "isbn": "some-isbn",
                          "title": "some-title",
                          "author": "some-author",
                          "publicationYear": 2001,
                          "availableCopies": 4
                        }
                        """);
    }
}
