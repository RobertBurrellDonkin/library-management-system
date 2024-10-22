package name.robertburrelldonkin.library.domain;

import java.util.Objects;

public final class Book {

    public static Builder aBook() {
        return new Builder();
    }

    private final String isbn;
    private final String title;
    private final String author;
    private final int publicationYear;
    private final int availableCopies;

    private Book(final String isbn,
                 final String title,
                 final String author,
                 final int publicationYear,
                 final int availableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.availableCopies = availableCopies;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(getIsbn(), book.getIsbn());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIsbn());
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                ", availableCopies=" + availableCopies +
                '}';
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
