package com.sneakery.store.config;

import com.sneakery.store.security.CustomUserDetailsService;
import com.sneakery.store.security.JwtAuthenticationFilter;
// Rate limiting filter - tạm thời comment để tránh lỗi compile với Bucket4j
// import com.sneakery.store.security.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    // Rate limiting filter - tạm thời comment để tránh lỗi compile với Bucket4j
    // private final RateLimitingFilter rateLimitingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF protection disabled for REST APIs with JWT
                // JWT tokens provide CSRF protection inherently
                // For state-changing operations, JWT validation is sufficient
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép OPTIONS requests (CORS preflight) - không cần authentication
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        // 2. Các API Public (Không cần đăng nhập)
                        .requestMatchers("/api/auth/**").permitAll() // API Đăng nhập/Đăng ký/Reset Password
                        // TestController chỉ hoạt động trong dev profile (đã có @Profile("dev"))
                        // Cho phép test endpoints trong dev (sẽ tự động disable trong production nhờ @Profile)
                        .requestMatchers("/api/test/**").permitAll() // Test endpoints (chỉ hoạt động trong dev profile)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui/index.html").permitAll() // Swagger

                        // ✅ Cho phép GET public cho Product
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // ✅ Cho phép GET public cho Flash Sales
                        .requestMatchers(HttpMethod.GET, "/api/flash-sales/**").permitAll()

                        // ✅ Cho phép GET public cho Reviews (approved testimonials)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/approved").permitAll()

                        // ✅ Cho phép Guest APIs (không cần authentication)
                        .requestMatchers("/api/guest/**").permitAll()
                        
                        // ✅ Cho phép truy cập static files (uploads)
                        .requestMatchers("/uploads/**").permitAll()

                        // ❌ Các request khác yêu cầu xác thực
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                // Rate limiting filter - tạm thời comment để tránh lỗi compile với Bucket4j                // 
                // .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://localhost:5173", // ✅ Vite
                "http://localhost:5174",
                "http://localhost:3000"
        ));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}