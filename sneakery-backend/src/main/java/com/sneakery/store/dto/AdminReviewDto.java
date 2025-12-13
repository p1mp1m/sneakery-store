package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO: AdminReviewDto
 * DTO đầy đủ cho admin quản lý reviews
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReviewDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    
    private Long userId;
    private String userName;
    private String userEmail;
    
    private Long orderId;
    
    private Integer rating;
    private String title;
    private String body;
    
    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;
    
    private Boolean isApproved;
    private Boolean isVerifiedPurchase;
    
    private Integer helpfulCount;
    private Integer unhelpfulCount;
    
    private String replyText;
    private LocalDateTime repliedAt;
    private String repliedByName;
    
    private String approvedByName;
    private LocalDateTime approvedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}

