package com.sneakery.store.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter này chạy MỘT LẦN cho MỖI request.
 * Đây là "Người gác cổng" kiểm tra JWT token.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService; // Đây là CustomUserDetailsService

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Lấy token từ request (từ header "Authorization")
        String token = getJwtFromRequest(request);

        // 2. Xác thực token
        if (StringUtils.hasText(token)) {
            try {
                // 2.1. Lấy email từ token
                String email = jwtTokenProvider.getUsernameFromToken(token);

                // 2.2. Kiểm tra xem user đã được xác thực trong SecurityContext chưa
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    // 2.3. Tải thông tin User (UserDetails) từ CSDL
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                    // 2.4. Nếu token hợp lệ (khớp user và chưa hết hạn)
                    if (jwtTokenProvider.validateToken(token, userDetails)) {
                        
                        // 2.5. Tạo đối tượng xác thực
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // Không cần password
                                userDetails.getAuthorities()
                        );
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 2.6. QUAN TRỌNG: Lưu vào SecurityContext
                        // Báo cho Spring Security biết "User này đã được xác thực"
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception ex) {
                // Nếu token có vấn đề (hết hạn, sai chữ ký,...)
                // Chúng ta không làm gì cả, request sẽ tự động bị từ chối ở bước sau
                logger.warn("Could not set user authentication in security context", ex);
            }
        }

        // 3. Chuyển request cho filter tiếp theo
        filterChain.doFilter(request, response);
    }

    /**
     * Helper để lấy token từ Header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        // Kiểm tra xem header có tồn tại và bắt đầu bằng "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Bỏ 7 ký tự "Bearer "
        }
        return null;
    }
}