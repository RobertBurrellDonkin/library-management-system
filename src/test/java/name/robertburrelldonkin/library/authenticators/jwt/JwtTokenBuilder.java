package name.robertburrelldonkin.library.authenticators.jwt;

import io.jsonwebtoken.Jwts;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

public class JwtTokenBuilder {

    public static final byte[] DECODED_PRIVATE_KEY = Base64.getDecoder().decode(
            "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");

    public static void main(String[] args) {
        System.out.println(createAValidToken());
    }

    public static String createAValidToken() {
        final var privateKey = getPrivateKey();

        return Jwts
                .builder()
                .subject("admin")
                .expiration(Date.from(Instant.now().plus(10, DAYS)))
                .signWith(privateKey)
                .compact();
    }

    public static String createAnExpiredToken() {
        final var privateKey = getPrivateKey();

        return Jwts
                .builder()
                .subject("admin")
                .expiration(Date.from(Instant.now().minus(1, DAYS)))
                .signWith(privateKey)
                .compact();
    }

    private static PrivateKey getPrivateKey() {
        final PrivateKey privateKey;
        try {
            privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(DECODED_PRIVATE_KEY));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return privateKey;
    }
}
