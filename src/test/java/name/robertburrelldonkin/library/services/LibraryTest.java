package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LibraryTest {

    Library library;
    Book aBook = createABook();
    Book aBookByAnAuthor = createRandomBookBy("an-author");
    Book anotherBookByAnAuthor = createRandomBookBy("an-author");
    Book aBookByAnotherAuthor = createRandomBookBy("another-author");
    Book aBookWithOneCopy = createBookWithOneCopy();
    Book aBookWithNoCopies = createBookWithNoCopies();

    @BeforeEach
    void setUp() {
        library = new Library();
    }

    @Nested
    class AddBook {
        @Test
        void whenABookIsNotInTheLibraryThenAddBookShouldAddTheBookToTheLibrary() {
            assertThat(library.addBook(aBook), is(true));

            assertThat(library.findBookByISBN(aBook.isbn()), is(Optional.of(aBook)));
        }

        @Test
        void whenABookIsInTheLibraryThenAddBookShouldTheBookShouldNotBeAddedTwice() {
            library.addBook(aBook);

            assertThat(library.addBook(someBook().withIsbn(aBook.isbn()).build()), is(false));
        }
    }

    @Nested
    class RemoveBook {
        @Test
        void whenABookIsInTheLibraryThenRemoveBookShouldRemoveTheBookFromTheLibrary() {
            library.addBook(aBook);

            assertThat(library.removeBook(aBook.isbn()), is(true));

            assertThat(library.findBookByISBN(aBook.isbn()), is(Optional.empty()));
        }

        @Test
        void whenABookIsNotInTheLibraryThenRemoveBookShouldReturnFalse() {
            assertThat(library.removeBook(aBook.isbn()), is(false));
        }
    }

    @Nested
    class FindBookByISBN {
        @Test
        void whenABookIsInTheLibraryThenFindBookByISBNShouldReturnTheBook() {
            library.addBook(aBook);

            assertThat(library.findBookByISBN(aBook.isbn()), is(Optional.of(aBook)));
        }

        @Test
        void whenABookIsNotInTheLibraryThenFindBookByISBNShouldReturnEmpty() {
            assertThat(library.findBookByISBN(aBook.isbn()), is(Optional.empty()));
        }
    }

    @Nested
    class FindBookByAuthor {
        @Test
        void whenABookIsInTheLibraryThenFindBookByAuthorShouldReturnTheBook() {
            library.addBook(aBook);

            assertThat(library.findBooksByAuthor(aBook.author()), contains(aBook));
        }

        @Test
        void whenABookIsNotInTheLibraryThenFindBookByAuthorShouldReturnEmpty() {
            assertThat(library.findBooksByAuthor(aBook.author()), empty());
        }

        @Test
        void findBookByAuthorShouldReturnAllBooksByAuthorAndNoOthers() {
            library.addBook(aBookByAnAuthor);
            library.addBook(anotherBookByAnAuthor);
            library.addBook(aBookByAnotherAuthor);

            assertThat(library.findBooksByAuthor(aBookByAnAuthor.author()), containsInAnyOrder(aBookByAnAuthor, anotherBookByAnAuthor));
        }
    }

    @Nested
    class BorrowBook {
        @Test
        void whenABookIsInTheLibraryThenBorrowBookShouldDecreaseAvailableCopiesByOne() {
            library.addBook(aBookWithOneCopy);

            assertThat(library.borrowBook(aBookWithOneCopy.isbn()), is(true));

            assertThat(library.findBookByISBN(aBookWithOneCopy.isbn()).orElseThrow().availableCopies(), is(0));
        }

        @Test
        void whenABookIsInTheLibraryWithNoCopiesAvailableThenBorrowBookShouldThrowException() {
            library.addBook(aBookWithNoCopies);

            assertThrows(
                    NoAvailableCopiesException.class,
                    () -> library.borrowBook(aBookWithOneCopy.isbn()));

            assertThat(library.findBookByISBN(aBookWithOneCopy.isbn()).orElseThrow().availableCopies(), is(0));
        }

        @Test
        void whenABookIsNotInTheLibraryThenBorrowBookShouldReturnFalse() {
            assertThat(library.borrowBook(aBookWithOneCopy.isbn()), is(false));
        }
    }

    @Nested
    class ReturnBook {
        @Test
        void whenABookIsInTheLibraryThenReturnBookShouldIncreaseAvailableCopiesByOne() {
            library.addBook(aBookWithOneCopy);

            assertThat(library.returnBook(aBookWithOneCopy.isbn()), is(true));

            assertThat(library.findBookByISBN(aBookWithOneCopy.isbn()).orElseThrow().availableCopies(), is(2));
        }

        @Test
        void whenABookIsNotInTheLibraryThenBorrowBookShouldReturnFalse() {
            assertThat(library.returnBook(aBookWithOneCopy.isbn()), is(false));
        }
    }
}