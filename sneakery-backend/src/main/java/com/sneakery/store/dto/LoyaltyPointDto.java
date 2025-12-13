package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO: LoyaltyPointDto
 * Dữ liệu điểm tích lũy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPointDto {
    private Long id;
    private Integer points; // Âm nếu redeem
    private String transactionType; // earn, redeem, expire
    private Long earnedFromOrderId;
    private Long redeemedInOrderId;
    private String description;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private Boolean isExpired;
}

