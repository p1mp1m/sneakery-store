package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho Newsletter Subscription
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSubscriptionDto {
    private Long id;
    private String email;
    private Boolean isActive;
    private LocalDateTime subscribedAt;
}




