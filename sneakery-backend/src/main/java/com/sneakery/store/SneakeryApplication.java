package com.sneakery.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 🏪 Sneakery E-commerce Application
 * 
 * Spring Boot main application class
 * CORS configuration được handle bởi SecurityConfig.java
 */
@SpringBootApplication
public class SneakeryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SneakeryApplication.class, args);
        System.out.println("🚀 Sneakery Backend is running!");
        System.out.println("📚 API Documentation: http://localhost:8080/swagger-ui.html");
    }
}