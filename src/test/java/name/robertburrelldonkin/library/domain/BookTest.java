package name.robertburrelldonkin.library.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.createABook;
import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.someBook;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BookTest {

    Book aBook;

    @BeforeEach
    void setUp() {
        aBook = createABook();
    }

    @Nested
    class Equals {
        @Test
        void twoBooksWithTheSameIsbnAreEqual() {
            assertThat(someBook().withIsbn("some-isbn").build(), is(equalTo(someBook().withIsbn("some-isbn").build())));
        }

        @Test
        void twoBooksWithDifferentIsbnsAreNotEqual() {
            assertThat(someBook().withIsbn("some-isbn").build(), is(not(equalTo(someBook().withIsbn("another-isbn").build()))));
        }
    }

    @Nested
    class HashCode {
        @Test
        void twoBooksWithTheSameIsbnHaveTheSameHashCode() {
            assertThat(someBook().withIsbn("some-isbn").build().hashCode(), is(equalTo(someBook().withIsbn("some-isbn").build().hashCode())));
        }
    }

    @Nested
    class IncrementAndGetAvailableCopies {

        @Test
        void incrementAvailableCopiesShouldIncreaseNumberOfCopiesByOne() {
            final var numberOfCopies = aBook.getAvailableCopies();

            assertThat(aBook.incrementAndGetAvailableCopies(), is(equalTo(numberOfCopies + 1)));
            assertThat(aBook.getAvailableCopies(), is(equalTo(numberOfCopies + 1)));
        }
    }

    @Nested
    class DecrementAndGetAvailableCopies {
        @Test
        void decrementAvailableCopiesShouldDecreaseNumberOfCopiesByOne() {
            final var numberOfCopies = aBook.getAvailableCopies();

            assertThat(aBook.decrementAndGetAvailableCopies(), is(equalTo(numberOfCopies - 1)));
            assertThat(aBook.getAvailableCopies(), is(equalTo(numberOfCopies - 1)));
        }
    }
}