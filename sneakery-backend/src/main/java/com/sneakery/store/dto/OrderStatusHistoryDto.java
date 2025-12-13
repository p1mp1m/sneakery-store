package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderStatusHistoryDto {
    private Long id;
    private String status;
    private LocalDateTime changedAt;
}