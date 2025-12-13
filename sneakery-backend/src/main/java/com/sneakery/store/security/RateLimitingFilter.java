package com.sneakery.store.security;

/*
import com.bucket4j.Bucket;
import com.sneakery.store.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
*/
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Rate Limiting Filter - Bảo vệ API khỏi abuse và DDoS attacks
 * 
 * <p>Filter này kiểm tra rate limit cho mỗi request dựa trên:
 * <ul>
 *   <li>IP address của client</li>
 *   <li>Loại API (public, authenticated, admin)</li>
 * </ul>
 * 
 * <p><b>Rate Limits:</b>
 * <ul>
 *   <li>Public APIs: 100 requests/phút</li>
 *   <li>Authenticated APIs: 200 requests/phút</li>
 *   <li>Admin APIs: 500 requests/phút</li>
 * </ul>
 * 
 * <p><b>Lưu ý:</b>
 * <ul>
 *   <li>Filter này chạy trước authentication filter</li>
 *   <li>Sử dụng in-memory storage (có thể mở rộng với Redis)</li>
 *   <li>Rate limit được reset mỗi phút</li>
 * </ul>
 * 
 * <p><b>Tạm thời disable:</b>
 * Filter này tạm thời được comment out do lỗi Bucket4j dependency.
 * Để enable lại, cần:
 * 1. Uncomment Bucket4j dependency trong pom.xml
 * 2. Chạy `mvn clean install` để tải dependencies
 * 3. Uncomment code trong file này và RateLimitingConfig.java
 * 4. Uncomment filter trong SecurityConfig.java
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Slf4j
// @Component - Tạm thời comment để tránh lỗi compile
@Order(1) // Chạy trước JwtAuthenticationFilter
public class RateLimitingFilter extends OncePerRequestFilter {

    /*
    private final Bucket publicApiBucket;
    private final Bucket authenticatedApiBucket;
    private final Bucket adminApiBucket;

    public RateLimitingFilter(
            @Qualifier("publicApiBucket") Bucket publicApiBucket,
            @Qualifier("authenticatedApiBucket") Bucket authenticatedApiBucket,
            @Qualifier("adminApiBucket") Bucket adminApiBucket
    ) {
        this.publicApiBucket = publicApiBucket;
        this.authenticatedApiBucket = authenticatedApiBucket;
        this.adminApiBucket = adminApiBucket;
    }
    */

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Tạm thời bỏ qua rate limiting - chỉ pass through
        /*
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Bỏ qua rate limiting cho OPTIONS requests (CORS preflight)
        if ("OPTIONS".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Xác định bucket dựa trên path và method
        Bucket bucket = getBucketForPath(path, method);
        
        // Lấy IP address
        String clientIp = getClientIpAddress(request);
        
        // Kiểm tra rate limit
        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, path);
            
            ErrorResponseDto errorDto = ErrorResponseDto.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.TOO_MANY_REQUESTS.value())
                    .error(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
                    .message("Quá nhiều requests. Vui lòng thử lại sau.")
                    .path(path)
                    .build();
            
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    String.format(
                            "{\"timestamp\":\"%s\",\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Quá nhiều requests. Vui lòng thử lại sau.\",\"path\":\"%s\"}",
                            errorDto.getTimestamp(),
                            path
                    )
            );
            return;
        }
        */
        
        filterChain.doFilter(request, response);
    }

    /*
    /**
     * Xác định bucket dựa trên path và method của request
     */
    /*
    private Bucket getBucketForPath(String path, String method) {
        // Admin APIs
        if (path.startsWith("/api/admin/")) {
            return adminApiBucket;
        }
        
        // Public APIs (auth, products GET, guest, swagger)
        if (path.startsWith("/api/auth/") || 
            (path.startsWith("/api/products") && "GET".equals(method)) ||
            path.startsWith("/api/guest/") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui")) {
            return publicApiBucket;
        }
        
        // Authenticated APIs (mặc định)
        return authenticatedApiBucket;
    }

    /**
     * Lấy IP address của client
     * Xử lý các trường hợp proxy/load balancer
     */
    /*
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Lấy IP đầu tiên trong danh sách (client IP thực sự)
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    */
}

