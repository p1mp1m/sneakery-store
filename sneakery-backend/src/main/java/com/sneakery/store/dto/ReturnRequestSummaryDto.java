package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO tóm tắt return request để hiển thị trong order list
 */
@Data
@Builder
public class ReturnRequestSummaryDto {
    private Long id;
    private String status; // pending, approved, processing, packed, refunded, rejected
    private LocalDateTime createdAt;
    private String reason; // Lý do hoàn trả (có thể truncate để hiển thị ngắn)
    private String returnMethod;
}

