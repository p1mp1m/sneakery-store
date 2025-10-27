package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer points;
    private String transactionType;
    private String description;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
