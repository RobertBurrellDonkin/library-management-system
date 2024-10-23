package name.robertburrelldonkin.library.authenticators.jwt;

import jakarta.servlet.http.HttpServletRequest;
import name.robertburrelldonkin.library.authenticators.TokenExtractor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public class BearerTokenExtractor implements TokenExtractor {

    public static final String BEARER = "Bearer ";
    public static final int TOKEN_START_INDEX = BEARER.length();

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
