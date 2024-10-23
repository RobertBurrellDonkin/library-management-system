package name.robertburrelldonkin.library.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.Semaphore;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * Limits endpoint access rate by returning 429 TOO_MANY_REQUESTS when the number of
 * concurrent requests intercepted exceeds maxConcurrentRequests.
 */
public class RateLimitingHandlerInterceptor implements HandlerInterceptor {

    private final Semaphore semaphore;

    public RateLimitingHandlerInterceptor(final int maxConcurrentRequests) {
        semaphore = new Semaphore(maxConcurrentRequests);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (semaphore.tryAcquire()) {
            return true;
        }
        response.setStatus(TOO_MANY_REQUESTS.value());
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        semaphore.release();
    }
}
