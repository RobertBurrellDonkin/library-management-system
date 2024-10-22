package name.robertburrelldonkin.library.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.someBook;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.junit.jupiter.api.Assertions.*;
class BookTest {

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
}