package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO: AdminReturnDto
 * DTO đầy đủ cho admin quản lý returns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReturnDto {
    private Long id;
    
    private Long orderId;
    private String orderNumber;
    private BigDecimal orderTotal;
    
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    
    private String reason;
    private String returnMethod;
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;

    private String status; // pending, approved, rejected, completed
    
    private List<String> images;
    
    private String adminNote;
    private String approvedByName;
    private LocalDateTime approvedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AdminReturnItemDto> items;
}

