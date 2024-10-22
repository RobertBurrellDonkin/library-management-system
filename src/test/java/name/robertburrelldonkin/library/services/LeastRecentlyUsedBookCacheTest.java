package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.createRandomBook;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class LeastRecentlyUsedBookCacheTest {

    LeastRecentlyUsedBookCache cache;
    Book aBook = createRandomBook();
    Book anotherBook = createRandomBook();
    Book yetAnotherBook = createRandomBook();

    @BeforeEach
    void setUp() {
        cache = new LeastRecentlyUsedBookCache(2, 2, 0.75f);
    }

    @Nested
    class Invalidate {
        @Test
        void whenBookIsCachedThenInvalidateShouldRemoveBookFromCache() {
            cache.add(aBook);

            cache.invalidate(aBook.isbn());

            assertThat(cache.get(aBook.isbn()), is(Optional.empty()));
        }

        @Test
        void whenBookIsNotCachedThenInvalidateShouldSilentlyDoNothing() {
            cache.invalidate(aBook.isbn());

            assertThat(cache.get(aBook.isbn()), is(Optional.empty()));
        }
    }

    @Nested
    class Add {
        @Test
        void whenCacheIsWithinLimitThenAddCachesBook() {
            cache.add(aBook);
            cache.add(anotherBook);

            assertThat(cache.get(aBook.isbn()), is(Optional.of(aBook)));
            assertThat(cache.get(anotherBook.isbn()), is(Optional.of(anotherBook)));
        }

        @Test
        void whenCacheIsAtLimitThenAddCachesBookAndRemovesFirstAdded() {
            cache.add(aBook);
            cache.add(anotherBook);
            cache.add(yetAnotherBook);

            assertThat(cache.get(aBook.isbn()), is(Optional.empty()));
            assertThat(cache.get(anotherBook.isbn()), is(Optional.of(anotherBook)));
            assertThat(cache.get(yetAnotherBook.isbn()), is(Optional.of(yetAnotherBook)));
        }

        @Test
        void whenCacheIsAtLimitThenAddCachesBookAndRemovesLastAccessed() {
            cache.add(aBook);
            cache.add(anotherBook);
            cache.get(aBook.isbn());
            cache.add(yetAnotherBook);

            assertThat(cache.get(aBook.isbn()), is(Optional.of(aBook)));
            assertThat(cache.get(anotherBook.isbn()), is(Optional.empty()));
            assertThat(cache.get(yetAnotherBook.isbn()), is(Optional.of(yetAnotherBook)));
        }
    }

    @Nested
    class Get {
        @Test
        void whenBookIsNotCachedThenGetReturnsEmpty() {
            assertThat(cache.get(aBook.isbn()), is(Optional.empty()));
        }

        @Test
        void whenBookIsCachedThenGetReturnsBook() {
            cache.add(aBook);

            assertThat(cache.get(aBook.isbn()), is(Optional.of(aBook)));
        }
    }

}