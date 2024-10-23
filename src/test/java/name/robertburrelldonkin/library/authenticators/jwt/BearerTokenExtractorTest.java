package name.robertburrelldonkin.library.authenticators.jwt;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
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
            assertThat(bearerTokenExtractor.extractToken(request), is(Optional.empty()));
        }

        @Test
        void whenNoAuthorizationHeaderIsPresent() {
            when(request.getHeader("Authorization")).thenReturn(null);
            assertThat(bearerTokenExtractor.extractToken(request), is(Optional.empty()));
        }

        @Test
        void whenAuthorizationHeaderIsNotBearerToken() {
            when(request.getHeader("Authorization")).thenReturn("not-bearer-token");
            assertThat(bearerTokenExtractor.extractToken(request), is(Optional.empty()));
        }

        @Test
        void whenAuthorizationHeaderIsInvalidBearer() {
            when(request.getHeader("Authorization")).thenReturn("Bearer");
            assertThat(bearerTokenExtractor.extractToken(request), is(Optional.empty()));
        }

        @Test
        void whenAuthorizationHeaderIsEmptyBearerToken() {
            when(request.getHeader("Authorization")).thenReturn(" Bearer   ");
            assertThat(bearerTokenExtractor.extractToken(request), is(Optional.empty()));
        }

        @Test
        void whenAuthorizationHeaderIsBearerToken() {
            when(request.getHeader("Authorization")).thenReturn(" Bearer SomeToken");
            assertThat(bearerTokenExtractor.extractToken(request), is(Optional.of("SomeToken")));
        }

        @Test
        void whenAuthorizationHeaderIsBearerTokenWithWhitespace() {
            when(request.getHeader("Authorization")).thenReturn(" Bearer          SomeToken    ");
            assertThat(bearerTokenExtractor.extractToken(request), is(Optional.of("SomeToken")));
        }
    }
}