package name.robertburrelldonkin.library.authenticators.jwt;

import jakarta.servlet.http.HttpServletRequest;
import name.robertburrelldonkin.library.authenticators.TokenExtractor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Extracts Bearer tokens from AUTHORIZATION headers in a HTTP Request.
 */
public class BearerTokenExtractor implements TokenExtractor {

    public static final String BEARER = "Bearer ";
    public static final int TOKEN_START_INDEX = BEARER.length();

    /**
     * Extracts bearer tokens from AUTHORIZATION headers in the given HTTP request.
     *
     * @param request not null
     * @return any bearer tokens in AUTHORIZATION headers.
     * When only one AUTHORIZATION header is present and contains a bearer token - or when there is only AUTHORIZATION header
     * with a bearer token,
     * this list will contain only that token.
     * When there are multiple AUTHORIZATION headers with bearer tokens, all tokens will be returned in order.
     */
    @Override
    public List<String> extractToken(HttpServletRequest request) {
        final var results = new ArrayList<String>();
        final var headers = request.getHeaders(AUTHORIZATION);
        if (nonNull(headers)) {
            while (headers.hasMoreElements()) {
                final var header = headers.nextElement().strip();
                if (header.startsWith(BEARER)) {
                    final var token = header.substring(TOKEN_START_INDEX).strip();
                    results.add(token);
                }
            }
        }
        return results;
    }
}
