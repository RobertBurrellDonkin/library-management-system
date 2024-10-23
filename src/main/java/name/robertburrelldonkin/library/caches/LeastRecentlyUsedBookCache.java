package name.robertburrelldonkin.library.caches;

import name.robertburrelldonkin.library.domain.Book;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Simple thread safe LRU cache.
 */
public class LeastRecentlyUsedBookCache implements BookCache {

    /**
     * LinkedHashMap is not thread safe.
     * All public access must be synchronized.
     */
    private final Cache cache;

    public LeastRecentlyUsedBookCache(final int maxSize, int initialCapacity, float loadFactor) {
        this.cache = new Cache(maxSize, initialCapacity, loadFactor);
    }

    @Override
    public synchronized void invalidate(String isbn) {
        this.cache.remove(isbn);
    }

    @Override
    public synchronized void add(Book book) {
        this.cache.put(book.isbn(), book);
    }

    @Override
    public synchronized Optional<Book> get(String isbn) {
        return Optional.ofNullable(this.cache.get(isbn));
    }

    /**
     * Specialises LinkedHashMap to move the eldest entry (by access order) whenever the number of
     * cached books exceeds the configured max size.
     * Not thread safe.
     *
     * @see LinkedHashMap
     */
    private static final class Cache extends LinkedHashMap<String, Book> {
        private final int maxSize;

        private Cache(final int maxSize, int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Book> eldest) {
            return size() > maxSize;
        }
    }
}
