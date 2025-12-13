package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Testimonials (Reviews đã được approved)
 * Dùng để hiển thị trên homepage và reviews page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialDto {
    private Long id;
    
    // Product info
    private Long productId;
    private String productName;
    private String productImage;
    private String brandName;
    
    // User info
    private String userName;
    
    // Review info
    private Integer rating;
    private String comment; // body
    
    // Status
    private Boolean isVerifiedPurchase;
    
    private LocalDateTime createdAt;
}

