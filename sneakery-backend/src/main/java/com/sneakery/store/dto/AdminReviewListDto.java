package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO: AdminReviewListDto
 * DTO rút gọn cho danh sách reviews (pagination)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReviewListDto {
    private Long id;
    
    // Product info
    private Long productId;
    private String productName;
    private String productImage;
    
    // User info
    private Long userId;
    private String userName;
    
    // Review info
    private Integer rating;
    private String title;
    private String reviewText;
    private List<String> images;
    
    // Status
    private Boolean isApproved;
    private Boolean isVerifiedPurchase;
    
    // Engagement
    private Integer helpfulCount;
    private Integer unhelpfulCount;
    
    // Admin reply
    private String adminReply;
    private LocalDateTime repliedAt;
    
    private LocalDateTime createdAt;
}

