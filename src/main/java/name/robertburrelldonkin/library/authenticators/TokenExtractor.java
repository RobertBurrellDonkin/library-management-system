package name.robertburrelldonkin.library.authenticators;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface TokenExtractor {
    /**
     * TODO:
     *
     * @param request
     * @return
     */
    Optional<String> extractToken(HttpServletRequest request);
}
