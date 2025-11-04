package com.sneakery.store.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Cache Configuration
 * Enable caching for dashboard stats and other frequently accessed data
 * Spring Boot will auto-configure Caffeine cache manager
 */
@Configuration
@EnableCaching
public class CacheConfig {
    // Spring Boot 3.x will auto-configure Caffeine cache manager
    // Cache configuration can be done via application.properties:
    // spring.cache.caffeine.spec=maximumSize=100,expireAfterWrite=5m
    // spring.cache.cache-names=dashboardStats,productStats
}
