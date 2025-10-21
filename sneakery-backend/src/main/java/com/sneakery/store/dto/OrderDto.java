package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// DTO trả về thông tin chi tiết đơn hàng (sau khi checkout, hoặc xem lại)
@Data
@Builder
public class OrderDto {
    private Long id;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private AddressDto addressShipping;
    private PaymentDto payment; // Lấy thanh toán đầu tiên
    private List<CartItemDto> orderDetails; // Dùng lại CartItemDto cho tiện
}