package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminOrderListDto {
    private Long id;
    private String orderNumber;
    private String orderChannel;
    private String customerName; // Tên user
    private String customerEmail; // Email user
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private ReturnRequestSummaryDto returnRequest;
}