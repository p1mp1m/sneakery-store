package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO tóm tắt, gọn nhẹ cho trang "Lịch sử đơn hàng"
 */
@Data
@Builder
public class OrderSummaryDto {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private int totalItems; // Tổng số lượng sản phẩm trong đơn
    private ReturnRequestSummaryDto returnRequest; // Thông tin return request (nếu có)
}