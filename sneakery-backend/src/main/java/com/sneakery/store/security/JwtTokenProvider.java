package com.sneakery.store.security;

import com.sneakery.store.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function; // SỬA ĐỔI: Thêm import

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String JWT_SECRET;

    @Value("${app.jwt.expiration-ms}")
    private long JWT_EXPIRATION;

    /**
     * Tạo token từ đối tượng Authentication của Spring Security
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, Object> extraClaims = new HashMap<>();
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            extraClaims.put("role", user.getRole()); // Đây là nơi 'role' được thêm vào
            extraClaims.put("fullName", user.getFullName());
            extraClaims.put("userId", user.getId());
        }

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =================================================================
    // CÁC PHƯƠNG THỨC MỚI ĐỂ ĐỌC DỮ LIỆU TỪ TOKEN
    // =================================================================

    /**
     * SỬA LỖI: Phương thức `getRole` mà bạn cần
     * Trích xuất 'role' từ token.
     */
    public String getRole(String token) {
        // Lấy claim tên là "role" và ép kiểu về String
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Lấy email (username) từ token
     */
    public String getUsernameFromToken(String token) {
        // Subject chính là username (email)
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Xác thực token
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Kiểm tra token hết hạn chưa
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // --- CÁC PHƯƠNG THỨC HELPER ---

    /**
     * Helper đa năng để trích xuất một claim cụ thể
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Helper giải mã token và lấy tất cả thông tin (claims)
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Lấy Key bí mật từ chuỗi Base64
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}