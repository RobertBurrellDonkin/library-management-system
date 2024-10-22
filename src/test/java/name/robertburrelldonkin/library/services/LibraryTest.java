package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class LibraryTest {

    Library library;
    Book aBook;
    Book aBookByAnAuthor;
    Book anotherBookByAnAuthor;
    Book aBookByAnotherAuthor;
    Book aBookWithOneCopy;
    Book aBookWithNoCopies;

    @BeforeEach
    void setUp() {
        library = new Library();
        aBook = createABook();
        aBookByAnAuthor = createRandomBookBy("an-author");
        anotherBookByAnAuthor = createRandomBookBy("an-author");
        aBookByAnotherAuthor = createRandomBookBy("another-author");
        aBookWithOneCopy = createBookWithOneCopy();
        aBookWithNoCopies = createBookWithNoCopies();
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

    @Nested
    class FindBookByAuthor {
        @Test
        void whenABookIsInTheLibraryThenFindBookByAuthorShouldReturnTheBook() {
            library.addBook(aBook);

            assertThat(library.findBooksByAuthor(aBook.getAuthor()), contains(aBook));
        }

        @Test
        void whenABookIsNotInTheLibraryThenFindBookByAuthorShouldReturnEmpty() {
            assertThat(library.findBooksByAuthor(aBook.getAuthor()), empty());
        }

        @Test
        void findBookByAuthorShouldReturnAllBooksByAuthorAndNoOthers() {
            library.addBook(aBookByAnAuthor);
            library.addBook(anotherBookByAnAuthor);
            library.addBook(aBookByAnotherAuthor);

            assertThat(library.findBooksByAuthor(aBookByAnAuthor.getAuthor()), containsInAnyOrder(aBookByAnAuthor, anotherBookByAnAuthor));
        }
    }

    @Nested
    class BorrowBook {
        @Test
        void whenABookIsInTheLibraryThenBorrowBookShouldDecreaseAvailableCopiesByOne() {
            library.addBook(aBookWithOneCopy);

            assertThat(library.borrowBook(aBookWithOneCopy.getIsbn()), is(true));

            assertThat(library.findBookByISBN(aBookWithOneCopy.getIsbn()).orElseThrow().getAvailableCopies(), is(0));
        }

        @Test
        void whenABookIsNotInTheLibraryThenBorrowBookShouldReturnFalse() {
            assertThat(library.borrowBook(aBookWithOneCopy.getIsbn()), is(false));
        }
    }

    @Nested
    class ReturnBook {
        @Test
        void whenABookIsInTheLibraryThenReturnBookShouldIncreaseAvailableCopiesByOne() {
            library.addBook(aBookWithOneCopy);

            assertThat(library.returnBook(aBookWithOneCopy.getIsbn()), is(true));

            assertThat(library.findBookByISBN(aBookWithOneCopy.getIsbn()).orElseThrow().getAvailableCopies(), is(2));
        }

        @Test
        void whenABookIsNotInTheLibraryThenBorrowBookShouldReturnFalse() {
            assertThat(library.returnBook(aBookWithOneCopy.getIsbn()), is(false));
        }
    }
}