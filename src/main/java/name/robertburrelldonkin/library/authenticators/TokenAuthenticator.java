package name.robertburrelldonkin.library.authenticators;

import java.util.Optional;

public interface TokenAuthenticator {
    /**
     * Authenticates a token and extracts the subject.
     *
     * @param token not null
     * @return the subject if authentic, empty otherwise
     */
    Optional<Subject> authenticate(String token);
}
