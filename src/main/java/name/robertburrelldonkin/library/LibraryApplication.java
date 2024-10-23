package name.robertburrelldonkin.library;

import name.robertburrelldonkin.library.caches.LeastRecentlyUsedBookCache;
import name.robertburrelldonkin.library.interceptors.RateLimitingHandlerInterceptor;
import name.robertburrelldonkin.library.services.CachingLibraryManagementService;
import name.robertburrelldonkin.library.services.Library;
import name.robertburrelldonkin.library.services.LibraryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class LibraryApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    private final Logger logger = LoggerFactory.getLogger(LibraryApplication.class);

    /**
     * Rate limit for API endpoints.
     */
    @Value("${app.api.max-concurrent-requests}")
    private int maxConcurrentRequests;

    /**
     * Least recently used cache for books.
     *
     * @param maxSize         maximum number of books to be cached
     * @param initialCapacity capacity to allocate on startup
     * @param loadFactor      indicates the load when cache capacity should be scaled up
     * @return the book cache, not null
     */
    @Bean
    public LeastRecentlyUsedBookCache cache(
            @Value("${app.cache.books.max-size}") final int maxSize,
            @Value("${app.cache.books.initial-capacity}") final int initialCapacity,
            @Value("${app.cache.books.load-factor}") final float loadFactor) {
        return new LeastRecentlyUsedBookCache(maxSize, initialCapacity, loadFactor);
    }

    @Bean
    public LibraryManagementService libraryManagementService(final LeastRecentlyUsedBookCache cache) {
        return new CachingLibraryManagementService(new Library(), cache);
    }

    @Bean
    public SecurityFilterChain filterChain(
            @Value("${app.security.authentication}") final boolean authentication,
            final HttpSecurity http
    ) throws Exception {


        http
                // Cross-Site Request Forgery protection is appropriate only for microservices with a user interface
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        registry -> {
                            if (authentication) {
                                logger.info("Enabling authentication");
                                registry.anyRequest().authenticated();
                            } else {
                                logger.info("Disabling authentication");
                                registry.anyRequest().permitAll();
                            }
                        });
        return http.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new RateLimitingHandlerInterceptor(maxConcurrentRequests))
                .addPathPatterns("/api/books");
    }
}
