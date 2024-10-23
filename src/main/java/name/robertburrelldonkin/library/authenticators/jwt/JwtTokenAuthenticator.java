package name.robertburrelldonkin.library.authenticators.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import name.robertburrelldonkin.library.authenticators.Subject;
import name.robertburrelldonkin.library.authenticators.TokenAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Optional;

/**
 * Authenticates a signed JWT.
 */
public class JwtTokenAuthenticator implements TokenAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(JwtTokenAuthenticator.class);

    /**
     * Thread safe
     */
    private final JwtParser jwtParser;

    public JwtTokenAuthenticator(PublicKey publicKey) {
        this(Jwts.parser().verifyWith(publicKey).build());
    }

    public JwtTokenAuthenticator(SecretKey secretKey) {
        this(Jwts.parser().verifyWith(secretKey).build());
    }

    public JwtTokenAuthenticator(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    public Optional<Subject> authenticate(String token) {
        try {
            final var claims = signedClaimsIn(token);
            final var subject = new Subject(claims.getSubject());
            logger.info("Authenticated subject: {}", subject);
            return Optional.of(subject);
        } catch (JwtException | IllegalArgumentException e) {
            logger.info("Invalid JWT token: {}", token, e);
        }
        return Optional.empty();
    }

    private Claims signedClaimsIn(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}
