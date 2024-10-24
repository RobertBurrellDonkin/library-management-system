package name.robertburrelldonkin.library.authenticators;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface TokenExtractor {

    /**
     * Extracts tokens from a HTTP request.
     *
     * @param request not null
     * @return tokens extracted from the request, not null
     */
    List<String> extractToken(HttpServletRequest request);
}
