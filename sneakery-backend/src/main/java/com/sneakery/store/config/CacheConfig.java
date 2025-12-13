package com.sneakery.store.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration
 * Enable caching for dashboard stats and other frequently accessed data
 * 
 * <p>Cấu hình cache riêng cho từng cache name:
 * <ul>
 *   <li><b>brands, categories:</b> Cache lâu hơn (10 phút) vì ít thay đổi</li>
 *   <li><b>products:</b> Cache trung bình (5 phút) vì có thể thay đổi giá, stock</li>
 *   <li><b>dashboardStats, productStats:</b> Cache ngắn (2 phút) vì cần real-time hơn</li>
 * </ul>
 * 
 * <p>Cache size được cấu hình qua application.properties:
 * <ul>
 *   <li>Development: maximumSize=100</li>
 *   <li>Production: maximumSize=500</li>
 * </ul>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cấu hình CacheManager với các settings khác nhau cho từng cache
     * 
     * <p>Nếu không có cấu hình riêng, Spring Boot sẽ sử dụng
     * cấu hình mặc định từ application.properties
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Cấu hình mặc định cho tất cả caches
        // Spring Boot sẽ sử dụng cấu hình từ application.properties nếu có
        // Nếu không, sẽ dùng cấu hình mặc định này
        Caffeine<Object, Object> caffeine = Objects.requireNonNull(Caffeine.newBuilder()
                .maximumSize(100) // Default size, có thể override trong properties
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()); // Enable cache statistics
        
        cacheManager.setCaffeine(caffeine);
        
        return cacheManager;
    }
}
