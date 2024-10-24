package name.robertburrelldonkin.library;

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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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

    /**
     * Adds rate limiter for books endpoints.
     *
     * @param registry not null
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new RateLimitingHandlerInterceptor(maxConcurrentRequests))
                .addPathPatterns("/api/**");
    }

    /**
     * Extracts bearer tokens from the authorization header.
     *
     * @return not null
     */
    @Bean
    public TokenExtractor tokenExtractor() {
        return new BearerTokenExtractor();
    }

    /**
     * Builds a TokenAuthenticator based on configuration.
     * For the exercise, configuration is supported only for public key based signed JWT tokens.
     * Only RSA based public keys generated in Java have been tested.
     * SecretKeys and other algorithms are supported by the implementation.
     *
     * @param algorithm        the Java standard name for the algorithm used to generate the public key, or null
     * @param publicKeyEncoded the public key encoded in Base64 format, or null
     * @return when both parameters are not empty then a validator that verifies signed JWT tokens using the given
     * public key, otherwise a reject all authenticator
     */
    @Bean
    public TokenAuthenticator tokenAuthenticator(
            @Value("${app.security.jwt.algorithm:}") final String algorithm,
            @Value("${app.security.jwt.public-key:}") final String publicKeyEncoded) {
        if (isEmpty(algorithm) || isEmpty(publicKeyEncoded)) {
            logger.warn("Public key and algorithm must be configure to support JWT. Disabling authentication.");
            return token -> Optional.empty();
        }

        try {
            final PublicKey publicKey = KeyFactory.getInstance(algorithm)
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyEncoded)));
            logger.info("JWT Validated With:\n{}", publicKey.toString());
            return new JwtTokenAuthenticator(publicKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

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
            final TokenPreAuthenticationFilter tokenPreAuthenticationFilter
    ) throws Exception {
        http
                // Force JWT authentication
                .httpBasic(AbstractHttpConfigurer::disable)
                // Cross-Site Request Forgery protection is appropriate only for microservices with a user interface
                .csrf(AbstractHttpConfigurer::disable)
                // CORS is appropriate only for microservices with a user interface
                .cors(AbstractHttpConfigurer::disable)
                // Stateless sessions force the JWT token passed in to be verified each time.
                // This ensures that the expiry claim is verified on every call.
                .sessionManagement((session) -> session.sessionCreationPolicy(STATELESS))
                // This filter tries to extract and verify a token.
                // In Spring Security, pre-authentication refers to cases where authentication has been
                // performed by a third party. The claims in JWT tokens signed by the third party trusted
                // by this microservice should be accepted without further ado.
                .addFilterBefore(tokenPreAuthenticationFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(
                        registry -> {
                            if (authentication) {
                                logger.info("Require requests to be authenticated.");
                                // Note that authorization would be added at this point.
                                // This exercise ends with authentication.
                                // So we allow access to any principals bearing tokens
                                // singed by the third party trusted by this microservice.
                                registry.anyRequest().authenticated();
                            } else {
                                // This configuration is useful for manual and automated testing
                                // where minting valid tokens may be inconvenient.
                                // This should not be used in production.
                                logger.info("Allowing all requests");
                                registry.anyRequest().permitAll();
                            }
                        });

        return http.build();
    }
}
