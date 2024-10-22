package name.robertburrelldonkin.library.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RateLimitingHandlerInterceptorTest {

    RateLimitingHandlerInterceptor interceptor;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new RateLimitingHandlerInterceptor(2);
    }

    @Test
    public void shouldReturnTooManyRequestsWhenRateExceeded() throws Exception {
        assertThat(interceptor.preHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), new Object()), is(true));
        assertThat(interceptor.preHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), new Object()), is(true));

        assertThat(interceptor.preHandle(request, response, new Object()), is(false));
        verify(response).setStatus(429);
    }

    @Test
    public void shouldReleaseOnAfterCompletion() throws Exception {
        assertThat(interceptor.preHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), new Object()), is(true));
        assertThat(interceptor.preHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), new Object()), is(true));
        interceptor.afterCompletion(mock(HttpServletRequest.class), mock(HttpServletResponse.class), new Object(), null);

        assertThat(interceptor.preHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), new Object()), is(true));
    }
}