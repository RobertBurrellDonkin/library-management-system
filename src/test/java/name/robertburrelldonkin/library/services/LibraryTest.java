package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.someBook;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.createABook;
import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    Library library;
    Book aBook;

    @BeforeEach
    void setUp() {
        library = new Library();
        aBook = createABook();
    }

    @Nested
    class AddBook {
        @Test
        void whenABookIsNotInTheLibraryThenAddBookShouldAddTheBookToTheLibrary() {
            assertThat(library.addBook(aBook), is(true));

            assertThat(library.findBookByISBN(aBook.getIsbn()), is(Optional.of(aBook)));
        }

        @Test
        void whenABookIsInTheLibraryThenAddBookShouldTheBookShouldNotBeAddedTwice() {
            library.addBook(aBook);

            assertThat(library.addBook(someBook().withIsbn(aBook.getIsbn()).build()), is(false));
        }
    }

    @Nested
    class RemoveBook {
        @Test
        void whenABookIsInTheLibraryThenRemoveBookShouldRemoveTheBookFromTheLibrary() {
            library.addBook(aBook);

            assertThat(library.removeBook(aBook.getIsbn()), is(true));

            assertThat(library.findBookByISBN(aBook.getIsbn()), is(Optional.empty()));
        }

        @Test
        void whenABookIsNotInTheLibraryThenRemoveBookShouldReturnFalse() {
            assertThat(library.removeBook(aBook.getIsbn()), is(false));
        }
    }

    @Nested
    class FindBookByISBN {

        @Test
        void whenABookIsInTheLibraryThenFindBookByISBNShouldReturnTheBook() {
            library.addBook(aBook);

            assertThat(library.findBookByISBN(aBook.getIsbn()), is(Optional.of(aBook)));
        }
        @Test
        void whenABookIsNotInTheLibraryThenFindBookByISBNShouldReturnEmpty() {
            assertThat(library.findBookByISBN(aBook.getIsbn()), is(Optional.empty()));
        }
    }
}