package name.robertburrelldonkin.library;

import name.robertburrelldonkin.library.caches.LeastRecentlyUsedBookCache;
import name.robertburrelldonkin.library.interceptors.RateLimitingHandlerInterceptor;
import name.robertburrelldonkin.library.services.CachingLibraryManagementService;
import name.robertburrelldonkin.library.services.Library;
import name.robertburrelldonkin.library.services.LibraryManagementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@SpringBootApplication
public class LibraryApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

	/**
	 * Rate limit for API endpoints.
	 */
	@Value("${app.api.max-concurrent-requests}")
	private int maxConcurrentRequests;

	/**
	 * Least recently used cache for books.
	 *
	 * @param maxSize maximum number of books to be cached
	 * @param initialCapacity capacity to allocate on startup
	 * @param loadFactor indicates the load when cache capacity should be scaled up
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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// TODO: Secure using JWT later on
		http.authorizeHttpRequests(
						expressionInterceptUrlRegistry ->
								expressionInterceptUrlRegistry
										.anyRequest()
										.permitAll())
				.csrf(AbstractHttpConfigurer::disable);
		return http.build();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
				.addInterceptor(new RateLimitingHandlerInterceptor(maxConcurrentRequests))
				.addPathPatterns("/api/books");
	}
}
