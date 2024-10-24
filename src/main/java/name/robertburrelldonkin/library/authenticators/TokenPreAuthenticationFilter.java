package name.robertburrelldonkin.library.authenticators;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.util.Optional;

/**
 * Authenticates requests based on JWT tokens pre-authorised by third parties.
 */
public class TokenPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final Logger logger = LoggerFactory.getLogger(TokenPreAuthenticationFilter.class);

    private final TokenAuthenticator tokenAuthenticator;
    private final TokenExtractor tokenExtractor;

    public TokenPreAuthenticationFilter(TokenAuthenticator tokenAuthenticator, TokenExtractor tokenExtractor) {
        this.tokenAuthenticator = tokenAuthenticator;
        this.tokenExtractor = tokenExtractor;
    }

    /**
     * Extracts and authorises a token, returning the subject as principal.
     * When multiple tokens are extracted, the first authorised subject will be returned as principal
     *
     * @param request not null
     * @return the subject of an authenticated token, or null otherwise
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        final var principal = tokenExtractor
                .extractToken(request)
                .stream()
                .map(tokenAuthenticator::authenticate)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Subject::name)
                .findFirst()
                .orElse(null);
        logger.info("Principal is: {}", principal);
        return principal;
    }

    /**
     * Always returns a dummy value (as required when credentials are not supported).
     * Credentials are not expected to be supplied in the token.
     * Should a token contain credentials, there is no need for this microservice to know them
     * and for security reasons they should not be pass on to the Spring Security filter chain.
     *
     * @param request not null
     * @return empty string ""
     */
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "";
    }
}
