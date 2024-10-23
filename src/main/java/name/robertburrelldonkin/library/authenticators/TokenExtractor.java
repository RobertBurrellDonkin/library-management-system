package name.robertburrelldonkin.library.authenticators;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface TokenExtractor {

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
    List<String> extractToken(HttpServletRequest request);
}
