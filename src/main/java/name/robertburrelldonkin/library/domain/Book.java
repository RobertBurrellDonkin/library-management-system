package name.robertburrelldonkin.library.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record Book(@NotBlank(message = "isbn is mandatory") String isbn,
                   @NotBlank(message = "title is mandatory") String title,
                   @NotBlank(message = "author is mandatory") String author,
                   @Positive(message = "publicationYear must be positive") int publicationYear,
                   @Positive(message = "availableCopies must be positive") int availableCopies) {

    public static Builder aBook() {
        return new Builder();
    }

    public static final class Builder {
        private String isbn;
        private String title;
        private String author;
        private int publicationYear;
        private int availableCopies;

        public Builder withIsbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder withPublicationYear(int publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        public Builder withAvailableCopies(int availableCopies) {
            this.availableCopies = availableCopies;
            return this;
        }

        public Book build() {
            return new Book(isbn, title, author, publicationYear, availableCopies);
        }
    }
}
