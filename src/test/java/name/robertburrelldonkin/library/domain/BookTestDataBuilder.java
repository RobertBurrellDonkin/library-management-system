package name.robertburrelldonkin.library.domain;

import static name.robertburrelldonkin.library.domain.Book.aBook;

/**
 * Test data builder pattern TODO
 */
public class BookTestDataBuilder {
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
