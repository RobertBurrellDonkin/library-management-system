package name.robertburrelldonkin.library.services;

import name.robertburrelldonkin.library.domain.Book;

import java.util.Optional;

public interface BookCache {

    void invalidate(String isbn);

    void add(Book book);

    Optional<Book> get(String isbn);
}
