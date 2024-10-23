package name.robertburrelldonkin.library.authenticators;

import java.util.Optional;

public interface TokenAuthenticator {
    Optional<Subject> authenticate(String token);
}
