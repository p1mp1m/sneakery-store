package com.sneakery.store.config;

import com.sneakery.store.security.CustomUserDetailsService;
import com.sneakery.store.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Bật @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // "Người gác cổng" JWT
    private final CustomUserDetailsService userDetailsService; // Dịch vụ tải user

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF vì dùng JWT
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Bật CORS với cấu hình chi tiết

                // Cấu hình quy tắc truy cập
                .authorizeHttpRequests(auth -> auth
                        // 1. Các API Public (Không cần đăng nhập)
                        .requestMatchers("/api/auth/**").permitAll() // API Đăng nhập/Đăng ký
                        .requestMatchers("/api/test/**").permitAll() // TEST API - CHỈ DÙNG CHO DEBUG, XÓA KHI PRODUCTION!
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html").permitAll() // Swagger

                        // 2. API Product & Review (Chỉ cho phép GET public)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // 3. Tất cả API còn lại
                        // (POST/PUT /api/products, /api/cart, /api/orders, /api/addresses...)
                        .anyRequest().authenticated() // Đều phải đăng nhập
                )

                // Cấu hình Session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Không dùng Session
                )

                // Cung cấp AuthenticationProvider (dùng CSDL)
                .authenticationProvider(authenticationProvider())

                // Thêm Filter JWT vào chuỗi
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean để mã hoá mật khẩu
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean cung cấp cơ chế xác thực (dùng CSDL)
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Cung cấp cách lấy user
        authProvider.setPasswordEncoder(passwordEncoder()); // Cung cấp cách so sánh pass
        return authProvider;
    }

    /**
     * Bean quản lý việc xác thực (cần cho API /login)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Cấu hình CORS cho phép frontend truy cập
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Cho phép các origin (frontend URLs)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",  // Vite dev server mặc định
            "http://localhost:5174",  // Vite dev server port thay thế
            "http://localhost:3000"   // React/Next.js dev server
        ));
        
        // Cho phép các HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Cho phép các headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept",
            "X-Requested-With"
        ));
        
        // Cho phép gửi credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Thời gian cache preflight request (giây)
        configuration.setMaxAge(3600L);
        
        // Expose headers cho client đọc được
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // Áp dụng cấu hình cho tất cả các endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}