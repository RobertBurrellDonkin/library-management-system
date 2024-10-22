package name.robertburrelldonkin.library;

import name.robertburrelldonkin.library.caches.LeastRecentlyUsedBookCache;
import name.robertburrelldonkin.library.services.CachingLibraryManagementService;
import name.robertburrelldonkin.library.services.Library;
import name.robertburrelldonkin.library.services.LibraryManagementService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

	@Bean
	public Library library() {
		return new Library();
	}

	@Bean
	public LeastRecentlyUsedBookCache cache() {
		//TODO: externalise configuration
		return new LeastRecentlyUsedBookCache(128, 24 , 0.75f);
	}

	@Bean
	public LibraryManagementService libraryManagementService(Library library, LeastRecentlyUsedBookCache cache) {
		return new CachingLibraryManagementService(library, cache);
	}
}
