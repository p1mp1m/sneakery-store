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
                .cors(cors -> {}) // Bật CORS (sử dụng cấu hình từ WebMvcConfigurer)

                // Cấu hình quy tắc truy cập
                .authorizeHttpRequests(auth -> auth
                        // 1. Các API Public (Không cần đăng nhập)
                        .requestMatchers("/api/auth/**").permitAll() // API Đăng nhập/Đăng ký
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // Swagger

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
}