package name.robertburrelldonkin.library.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.Semaphore;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

public class RateLimitingHandlerInterceptor implements HandlerInterceptor {

    private final Semaphore semaphore;

    public RateLimitingHandlerInterceptor(final int maxConcurrentRequests) {
        semaphore = new Semaphore(maxConcurrentRequests);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (semaphore.tryAcquire()) {
            return true;
        }
        response.setStatus(TOO_MANY_REQUESTS.value());
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        semaphore.release();
    }
}
