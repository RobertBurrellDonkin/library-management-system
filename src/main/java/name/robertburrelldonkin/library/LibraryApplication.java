package name.robertburrelldonkin.library;

import name.robertburrelldonkin.library.caches.LeastRecentlyUsedBookCache;
import name.robertburrelldonkin.library.interceptors.RateLimitingHandlerInterceptor;
import name.robertburrelldonkin.library.services.CachingLibraryManagementService;
import name.robertburrelldonkin.library.services.Library;
import name.robertburrelldonkin.library.services.LibraryManagementService;
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

	@Bean
	public LeastRecentlyUsedBookCache cache() {
		//TODO: externalise configuration
		return new LeastRecentlyUsedBookCache(128, 24 , 0.75f);
	}

	@Bean
	public LibraryManagementService libraryManagementService(LeastRecentlyUsedBookCache cache) {
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
		// TODO externalise configuration
		registry.addInterceptor(new RateLimitingHandlerInterceptor(1)).addPathPatterns("/api/books");
	}
}
