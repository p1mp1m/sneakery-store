package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminOrderDetailDto {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    
    // Thông tin khách hàng
    private Long userId;
    private String customerName;
    private String customerEmail;
    
    // Địa chỉ
    private AddressDto addressShipping;
    private AddressDto addressBilling;
    
    // Chi tiết
    private PaymentDto payment;
    private List<CartItemDto> orderDetails;
    private List<OrderStatusHistoryDto> statusHistories;
}