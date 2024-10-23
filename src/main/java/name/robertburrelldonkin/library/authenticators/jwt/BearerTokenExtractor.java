package name.robertburrelldonkin.library.authenticators.jwt;

import jakarta.servlet.http.HttpServletRequest;
import name.robertburrelldonkin.library.authenticators.TokenExtractor;

import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class BearerTokenExtractor implements TokenExtractor {

    public static final String BEARER = "Bearer ";
    public static final int TOKEN_START_INDEX = BEARER.length();

    @Override
    public Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .filter(s -> !s.isEmpty())
                .map(String::strip)
                .filter(s -> s.startsWith(BEARER))
                .map(s -> s.substring(TOKEN_START_INDEX).strip());
    }
}
