package name.robertburrelldonkin.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class LibraryApplicationTests {

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

    @Test
    void endToEndRoundTrip() {
        webClient.get()
                .uri("/api/books/some-isbn")
                .exchange()
                .expectStatus()
                .isNotFound();

        webClient.post()
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
                        """)
                .exchange()
                .expectStatus()
                .isCreated();

        webClient.get()
                .uri("/api/books/some-isbn")
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
                .exchange()
                .expectStatus()
                .isOk();

        webClient.get()
                .uri("/api/books/some-isbn")
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
                .exchange()
                .expectStatus()
                .isOk();

        webClient.get()
                .uri("/api/books/some-isbn")
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
                .exchange()
                .expectStatus()
                .isOk();
    }
}
