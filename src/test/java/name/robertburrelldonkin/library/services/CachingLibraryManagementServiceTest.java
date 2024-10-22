package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Optional.empty;
import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.createABook;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachingLibraryManagementServiceTest {

    @Mock
    LibraryManagementService mockLibraryManagementService;
    @Mock
    BookCache bookCache;
    CachingLibraryManagementService cachingLibraryManagementService;
    Book aBook = createABook();

    @BeforeEach
    void setUp() {
        cachingLibraryManagementService =
                new CachingLibraryManagementService(mockLibraryManagementService, bookCache);
    }

    @Nested
    class AddBook {
        @Test
        void addBookShouldDelegate() {
            cachingLibraryManagementService.addBook(aBook);

            verify(mockLibraryManagementService).addBook(aBook);
        }

    }

    @Nested
    class RemoveBook {
        @Test
        void removeBookShouldDelegate() {
            cachingLibraryManagementService.removeBook("some-isbn");

            verify(mockLibraryManagementService).removeBook("some-isbn");
        }

        @Test
        void removeBookShouldInvalidateCache() {
            cachingLibraryManagementService.removeBook("some-isbn");

            verify(bookCache).invalidate("some-isbn");
        }
    }

    @Nested
    class FindBookByISBN {
        @Test
        void whenBookIsNotInCacheThenFindBookByISBNShouldDelegateAndUpdateCache() {
            when(bookCache.get("some-isbn")).thenReturn(empty());
            when(mockLibraryManagementService.findBookByISBN("some-isbn")).thenReturn(Optional.of(aBook));
            assertThat(cachingLibraryManagementService.findBookByISBN("some-isbn"), is(Optional.of(aBook)));

            verify(bookCache).get("some-isbn");
            verify(bookCache).add(aBook);
            verify(mockLibraryManagementService).findBookByISBN("some-isbn");
        }

        @Test
        void whenBookIsInCacheThenFindBookByISBNShouldReturnCachedBook() {
            when(bookCache.get("some-isbn")).thenReturn(Optional.of(aBook));
            assertThat(cachingLibraryManagementService.findBookByISBN("some-isbn"), is(Optional.of(aBook)));

            verify(bookCache).get("some-isbn");
            verify(mockLibraryManagementService, never()).findBookByISBN("some-isbn");
        }
    }

    @Nested
    class FindBookByAuthor {
        @Test
        void findBooksByAuthorShouldDelegate() {
            cachingLibraryManagementService.findBooksByAuthor("some-author");

            verify(mockLibraryManagementService).findBooksByAuthor("some-author");
        }
    }

    @Nested
    class BorrowBook {
        @Test
        void borrowBookShouldDelegate() {
            cachingLibraryManagementService.borrowBook("some-isbn");

            verify(mockLibraryManagementService).borrowBook("some-isbn");
        }

        @Test
        void borrowBookShouldInvalidateCache() {
            cachingLibraryManagementService.borrowBook("some-isbn");

            verify(bookCache).invalidate("some-isbn");
        }
    }

    @Nested
    class ReturnBook {
        @Test
        void returnBookShouldDelegate() {
            cachingLibraryManagementService.returnBook("some-isbn");

            verify(mockLibraryManagementService).returnBook("some-isbn");
        }

        @Test
        void returnBookShouldInvalidateCache() {
            cachingLibraryManagementService.returnBook("some-isbn");

            verify(bookCache).invalidate("some-isbn");
        }
    }
}