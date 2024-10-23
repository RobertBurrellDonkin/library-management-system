package name.robertburrelldonkin.library.authenticators;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenPreAuthenticationFilterTest {

    @Mock
    HttpServletRequest request;
    @Mock
    TokenExtractor tokenExtractor;
    @Mock
    TokenAuthenticator tokenAuthenticator;
    TokenPreAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new TokenPreAuthenticationFilter(tokenAuthenticator, tokenExtractor);
    }

    @Nested
    class GetPreAuthenticatedCredentials {
        @Test
        void shouldReturnDummyCredentials() {
            assertThat(filter.getPreAuthenticatedCredentials(request), is(""));
        }
    }

    @Nested
    class GetPreAuthenticatedPrincipal {
        @Test
        void shouldExtractTokenAndReturnSubjectWhenAuthenticated() {
            when(tokenExtractor.extractToken(request)).thenReturn(Optional.of("some-token"));
            when(tokenAuthenticator.authenticate("some-token")).thenReturn(Optional.of(new Subject("some-subject")));

            assertThat(filter.getPreAuthenticatedPrincipal(request), is("some-subject"));
        }

        @Test
        void shouldExtractTokenAndReturnNullWhenNotAuthenticated() {
            when(tokenExtractor.extractToken(request)).thenReturn(Optional.of("some-token"));
            when(tokenAuthenticator.authenticate("some-token")).thenReturn(Optional.empty());

            assertThat(filter.getPreAuthenticatedPrincipal(request), is(nullValue()));
        }


        @Test
        void whenTokenIsNotPresent() {
            when(tokenExtractor.extractToken(request)).thenReturn(Optional.empty());

            assertThat(filter.getPreAuthenticatedPrincipal(request), is(nullValue()));
        }
    }
}