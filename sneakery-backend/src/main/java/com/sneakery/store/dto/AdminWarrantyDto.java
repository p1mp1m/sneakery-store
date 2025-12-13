package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO: AdminWarrantyDto
 * DTO đầy đủ cho admin quản lý warranties
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminWarrantyDto {
    private Long id;
    
    private Long orderId;
    private String orderNumber;
    
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    
    private Long productId;
    private String productName;
    private String productImageUrl;
    
    private String issueDescription;
    private String status; // pending, approved, rejected, processing, completed
    private String warrantyType; // repair, replace
    
    private List<String> images;
    
    private String adminNote;
    private String resolutionNote;
    
    private String processedByName;
    private LocalDateTime processedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

