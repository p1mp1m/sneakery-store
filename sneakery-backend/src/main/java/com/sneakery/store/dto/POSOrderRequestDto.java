package com.sneakery.store.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO cho POS Order Request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class POSOrderRequestDto {
    
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<POSOrderItemDto> items;
    
    private Long customerId; // Optional, null = khách vãng lai
    
    private String discountCode; // Optional, mã giảm giá
    
    private BigDecimal discountAmount; // Optional, số tiền giảm giá đã tính
    
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod; // cash, card, bank, online
    
    @NotNull(message = "Tổng tiền không được để trống")
    private BigDecimal totalAmount;
    
    private String notes; // Optional, ghi chú cho đơn hàng
}

