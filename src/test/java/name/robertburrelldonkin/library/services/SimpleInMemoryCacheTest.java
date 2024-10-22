package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static name.robertburrelldonkin.library.domain.BookTestDataBuilder.createABook;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SimpleInMemoryCacheTest {

    @Mock
    LibraryManagementService mockLibraryManagementService;
    SimpleInMemoryCache simpleInMemoryCache;
    Book aBook = createABook();;

    @BeforeEach
    void setUp() {
        simpleInMemoryCache = new SimpleInMemoryCache(mockLibraryManagementService);
    }

    @Nested
    class AddBook {
        @Test
        void addBookShouldDelegate() {
            simpleInMemoryCache.addBook(aBook);

            verify(mockLibraryManagementService).addBook(aBook);
        }
    }

    @Nested
    class RemoveBook {
        @Test
        void removeBookShouldDelegate() {
            simpleInMemoryCache.removeBook("some-isbn");

            verify(mockLibraryManagementService).removeBook("some-isbn");
        }
    }

    @Nested
    class FindBookByISBN {
        @Test
        void findBookByISBNShouldDelegate() {
            simpleInMemoryCache.findBookByISBN("some-isbn");

            verify(mockLibraryManagementService).findBookByISBN("some-isbn");
        }
    }

    @Nested
    class FindBookByAuthor {
        @Test
        void findBooksByAuthorShouldDelegate() {
            simpleInMemoryCache.findBooksByAuthor("some-author");

            verify(mockLibraryManagementService).findBooksByAuthor("some-author");
        }
    }
    @Nested
    class BorrowBook {
        @Test
        void borrowBookShouldDelegate() {
            simpleInMemoryCache.borrowBook("some-isbn");

            verify(mockLibraryManagementService).borrowBook("some-isbn");
        }
    }

    @Nested
    class ReturnBook {
        @Test
        void returnBookShouldDelegate() {
            simpleInMemoryCache.returnBook("some-isbn");

            verify(mockLibraryManagementService).returnBook("some-isbn");
        }
    }
}