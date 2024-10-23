package name.robertburrelldonkin.library.authenticators.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BearerTokenExtractorTest {

    @Mock
    HttpServletRequest request;

    BearerTokenExtractor bearerTokenExtractor = new BearerTokenExtractor();


    @Nested
    class ExtractToken {
        @Test
        void whenHeaders() {
            assertThat(bearerTokenExtractor.extractToken(request), hasSize(0));
        }

        @Test
        void whenNoAuthorizationHeaderIsPresent() {
            when(request.getHeaders("Authorization")).thenReturn(null);
            assertThat(bearerTokenExtractor.extractToken(request), hasSize(0));
        }

        @Test
        void whenAuthorizationHeaderIsNotBearerToken() {
            when(request.getHeaders("Authorization")).thenReturn(enumerate(List.of("not-bearer-token")));
            assertThat(bearerTokenExtractor.extractToken(request), hasSize(0));
        }

        @Test
        void whenAuthorizationHeaderIsInvalidBearer() {
            when(request.getHeaders("Authorization")).thenReturn(enumerate(List.of("Bearer")));
            assertThat(bearerTokenExtractor.extractToken(request), hasSize(0));
        }

        @Test
        void whenAuthorizationHeaderIsEmptyBearerToken() {
            when(request.getHeaders("Authorization")).thenReturn(enumerate(List.of(" Bearer   ")));
            assertThat(bearerTokenExtractor.extractToken(request), hasSize(0));
        }

        @Test
        void whenAuthorizationHeaderIsBearerMissingToken() {
            when(request.getHeaders("Authorization")).thenReturn(enumerate(List.of("Bearer ")));
            assertThat(bearerTokenExtractor.extractToken(request), hasSize(0));
        }

        @Test
        void whenAuthorizationHeaderIsBearerToken() {
            when(request.getHeaders("Authorization")).thenReturn(enumerate(List.of(" Bearer SomeToken")));
            assertThat(bearerTokenExtractor.extractToken(request), contains("SomeToken"));
        }

        @Test
        void whenAuthorizationHeaderIsBearerTokenWithWhitespace() {
            when(request.getHeaders("Authorization")).thenReturn(enumerate(List.of(" Bearer          SomeToken    ")));
            assertThat(bearerTokenExtractor.extractToken(request), contains("SomeToken"));
        }

        @Test
        void whenThereAreOtherAuthorizationHeaders() {
            when(request.getHeaders("Authorization")).thenReturn(enumerate(List.of(" Bearer          SomeToken    ", "Basic SomeBasicAuth")));
            assertThat(bearerTokenExtractor.extractToken(request), contains("SomeToken"));
        }


        @Test
        void whenThereAreMultipleAuthorizationHeadersWithBearerTokens() {
            when(request.getHeaders("Authorization")).thenReturn(enumerate(List.of("Bearer          SomeToken    ", "Bearer          AnotherToken    ")));
            assertThat(bearerTokenExtractor.extractToken(request), contains("SomeToken", "AnotherToken"));
        }
    }

    private static Enumeration<String> enumerate(List<String> strings) {
        return new Vector<>(strings).elements();
    }
}