package name.robertburrelldonkin.library.authenticators;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenExtractor {
    String extractToken(HttpServletRequest request);
}
