package com.sneakery.store.config;

/*
import com.bucket4j.Bandwidth;
import com.bucket4j.Bucket;
import com.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import java.time.Duration;
*/
import org.springframework.context.annotation.Configuration;

/**
 * Configuration cho Rate Limiting
 * 
 * <p>Cấu hình rate limiting để bảo vệ API khỏi abuse và DDoS attacks.
 * Sử dụng Bucket4j với in-memory storage.
 * 
 * <p><b>Rate Limits:</b>
 * <ul>
 *   <li>Public APIs (auth, products): 100 requests/phút</li>
 *   <li>Authenticated APIs: 200 requests/phút</li>
 *   <li>Admin APIs: 500 requests/phút</li>
 * </ul>
 * 
 * <p><b>Lưu ý:</b>
 * <ul>
 *   <li>Rate limiting được áp dụng per IP address</li>
 *   <li>Có thể mở rộng để sử dụng Redis cho distributed rate limiting</li>
 *   <li>Rate limits có thể được cấu hình qua application.properties</li>
 * </ul>
 * 
 * <p><b>Tạm thời disable:</b>
 * File này tạm thời được comment out do lỗi Bucket4j dependency.
 * Để enable lại, cần:
 * 1. Uncomment Bucket4j dependency trong pom.xml
 * 2. Chạy `mvn clean install` để tải dependencies
 * 3. Uncomment code trong file này
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Configuration
public class RateLimitingConfig {

    /*
    /**
     * Bucket cho Public APIs (auth, products)
     * 100 requests/phút
     */
    /*
    @Bean(name = "publicApiBucket")
    public Bucket publicApiBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
                .build();
    }

    /**
     * Bucket cho Authenticated APIs
     * 200 requests/phút
     */
    /*
    @Bean(name = "authenticatedApiBucket")
    public Bucket authenticatedApiBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(200, Refill.intervally(200, Duration.ofMinutes(1))))
                .build();
    }

    /**
     * Bucket cho Admin APIs
     * 500 requests/phút
     */
    /*
    @Bean(name = "adminApiBucket")
    public Bucket adminApiBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(500, Refill.intervally(500, Duration.ofMinutes(1))))
                .build();
    }
    */
}

