package name.robertburrelldonkin.library;

import io.jsonwebtoken.Jwts;
import name.robertburrelldonkin.library.authenticators.TokenAuthenticator;
import name.robertburrelldonkin.library.authenticators.TokenExtractor;
import name.robertburrelldonkin.library.authenticators.TokenPreAuthenticationFilter;
import name.robertburrelldonkin.library.authenticators.jwt.BearerTokenExtractor;
import name.robertburrelldonkin.library.authenticators.jwt.JwtTokenAuthenticator;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.util.ObjectUtils.isEmpty;

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
    public TokenExtractor tokenExtractor() {
        return new BearerTokenExtractor();
    }

    @Bean
    public TokenAuthenticator tokenAuthenticator(
            @Value("${app.security.jwt.algorithm:}") final String algorithm,
            @Value("${app.security.jwt.public-key:}") final String publicKeyEncoded) throws Exception {
        if (isEmpty(algorithm) || isEmpty(publicKeyEncoded)) {
            logger.warn("Public key and algorithm must be configure to support JWT. Disabling authentication.");
            return token -> Optional.empty();
        }
        final var publicKey = KeyFactory.getInstance(algorithm)
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyEncoded)));
        logger.info("JWT Validated With:\n{}", publicKey.toString());
        return new JwtTokenAuthenticator(publicKey);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        final var provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(
                token ->
                        new User(token.getPrincipal().toString(), token.getCredentials().toString(), token.getAuthorities()));
        return new ProviderManager(List.of(provider));
    }

    @Bean
    public TokenPreAuthenticationFilter tokenPreAuthenticationFilter(
            TokenAuthenticator tokenAuthenticator,
            TokenExtractor tokenExtractor,
            AuthenticationManager authenticationManager) {
        final var filter = new TokenPreAuthenticationFilter(tokenAuthenticator, tokenExtractor);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(
            @Value("${app.security.authentication}") final boolean authentication,
            final HttpSecurity http,
            final TokenPreAuthenticationFilter preAuthenticationFilter
    ) throws Exception {
        http
                // TODO: Disable basic auth
                .httpBasic(AbstractHttpConfigurer::disable)
                // Cross-Site Request Forgery protection is appropriate only for microservices with a user interface
                .csrf(AbstractHttpConfigurer::disable)
                // TODO:
                .cors(AbstractHttpConfigurer::disable)
                //TODO: stateless
                .sessionManagement((session) -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(preAuthenticationFilter, BasicAuthenticationFilter.class)
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
