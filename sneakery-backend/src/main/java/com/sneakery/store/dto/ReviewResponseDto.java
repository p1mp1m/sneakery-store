package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

// DTO trả về khi xem review (che giấu thông tin nhạy cảm của user)
@Data
@Builder
public class ReviewResponseDto {
    private Long id;
    private Integer rating;
    private String body;
    private LocalDateTime createdAt;
    private String authorName; // Chỉ hiển thị tên, không hiển thị email/id
    private boolean isVerifiedPurchase;
}