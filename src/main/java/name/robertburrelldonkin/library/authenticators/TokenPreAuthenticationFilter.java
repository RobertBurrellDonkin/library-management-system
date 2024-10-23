package name.robertburrelldonkin.library.authenticators;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * PreAuthenticated scenario TODO
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
     *
     * @param request not null
     * @return the subject of an authenticated token, or null otherwise
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        final var principal = tokenExtractor
                .extractToken(request)
                .flatMap(tokenAuthenticator::authenticate)
                .map(Subject::name)
                .orElse(null);
        logger.info("Principal is: {}", principal);
        return principal;
    }

    /**
     * Always returns a dummy value.
     * Credentials are not expected to be supplied in the token.
     * In cases where credentials are not supplied, a dummy value should be returned.
     *
     * @param request not null
     * @return empty string ""
     */
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "";
    }
}
