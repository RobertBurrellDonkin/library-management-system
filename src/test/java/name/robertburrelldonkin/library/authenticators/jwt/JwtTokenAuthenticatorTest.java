package name.robertburrelldonkin.library.authenticators.jwt;

import io.jsonwebtoken.Jwts;
import name.robertburrelldonkin.library.authenticators.Subject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class JwtTokenAuthenticatorTest {

    @Nested
    class PublicKey {
        final KeyPair keyPair = Jwts.SIG.ES384.keyPair().build();
        final JwtTokenAuthenticator authenticator = new JwtTokenAuthenticator(keyPair.getPublic());

        @Test
        void whenCompactTokenContainsAuthenticatedSubject() {
            String token = Jwts
                    .builder()
                    .subject("some-subject")
                    .signWith(keyPair.getPrivate())
                    .compact();
            assertThat(authenticator.authenticate(token), is(Optional.of(new Subject("some-subject"))));
        }

        @Test
        void whenCompactTokenDoesNotContainSubject() {
            String token = Jwts
                    .builder()
                    .signWith(keyPair.getPrivate())
                    .compact();
            assertThat(authenticator.authenticate(token), is(Optional.empty()));
        }

        @Test
        void whenCompactTokenIsSignedByDifferentKey() {
            String token = Jwts
                    .builder()
                    .signWith(Jwts.SIG.HS512.key().build())
                    .compact();
            assertThat(authenticator.authenticate(token), is(Optional.empty()));
        }

        @Test
        void whenTokenIsNotJwt() {
            String token = "NOT A JWT TOKEN";
            assertThat(authenticator.authenticate(token), is(Optional.empty()));
        }

        @Test
        void whenTokenIsWhitespace() {
            String token = "  ";
            assertThat(authenticator.authenticate(token), is(Optional.empty()));
        }

        @Test
        void whenNoToken() {
            assertThat(authenticator.authenticate(null), is(Optional.empty()));
        }
    }

    @Nested
    class SymmetricKey {
        final SecretKey secretKey = Jwts.SIG.HS256.key().build();
        final JwtTokenAuthenticator authenticator = new JwtTokenAuthenticator(secretKey);

        @Test
        void whenCompactTokenContainsAuthenticatedSubject() {
            String token = Jwts
                    .builder()
                    .subject("some-subject")
                    .signWith(secretKey)
                    .compact();
            assertThat(authenticator.authenticate(token), is(Optional.of(new Subject("some-subject"))));
        }

        @Test
        void whenCompactTokenDoesNotContainSubject() {
            String token = Jwts
                    .builder()
                    .signWith(secretKey)
                    .compact();
            assertThat(authenticator.authenticate(token), is(Optional.empty()));
        }

        @Test
        void whenCompactTokenIsSignedByDifferentKey() {
            String token = Jwts
                    .builder()
                    .signWith(Jwts.SIG.HS512.key().build())
                    .compact();
            assertThat(authenticator.authenticate(token), is(Optional.empty()));
        }

        @Test
        void whenTokenIsNotJwt() {
            String token = "NOT A JWT TOKEN";
            assertThat(authenticator.authenticate(token), is(Optional.empty()));
        }

        @Test
        void whenTokenIsWhitespace() {
            String token = "  ";
            assertThat(authenticator.authenticate(token), is(Optional.empty()));
        }

        @Test
        void whenNoToken() {
            assertThat(authenticator.authenticate(null), is(Optional.empty()));
        }
    }
}