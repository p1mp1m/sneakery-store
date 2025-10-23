package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO: AdminWarrantyListDto
 * DTO rút gọn cho danh sách warranties (pagination)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminWarrantyListDto {
    private Long id;
    
    // Order info
    private Long orderId;
    private String orderNumber;
    
    // Customer info
    private Long userId;
    private String userName;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    // Product info
    private Long productId;
    private String productName;
    private String productImage;
    private String variant;
    
    // Warranty info
    private String issue;
    private String status;
    private String warrantyType;
    private Integer warrantyMonths;
    private LocalDateTime purchaseDate;
    
    private LocalDateTime createdAt;
}

