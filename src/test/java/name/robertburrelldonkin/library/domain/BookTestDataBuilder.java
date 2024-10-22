package name.robertburrelldonkin.library.domain;

import java.util.Random;

import static name.robertburrelldonkin.library.domain.Book.aBook;

/**
 * Test data builder pattern TODO
 */
public class BookTestDataBuilder {

    private static final Random RANDOM = new Random();

    public static Book createBookWithOneCopy() {
        return someBook().withAvailableCopies(1).build();
    }

    public static Book createBookWithNoCopies() {
        return someBook().withAvailableCopies(1).build();
    }

    public static Book createRandomBookBy(String author) {
        return someBook().withAuthor(author).withIsbn("isbn-" + RANDOM.nextInt()).build();
    }

    public static Book createABook() {
        return someBook().build();
    }

    public static Book.Builder someBook() {
        return aBook()
                .withAuthor("some-author")
                .withTitle("some-title")
                .withIsbn("some-isbn")
                .withPublicationYear(2000)
                .withAvailableCopies(1);
    }
}
