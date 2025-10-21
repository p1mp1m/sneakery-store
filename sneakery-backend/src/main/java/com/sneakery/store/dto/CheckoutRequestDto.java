package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO chứa thông tin user gửi lên khi checkout
@Data
public class CheckoutRequestDto {

    @NotNull(message = "ID địa chỉ giao hàng không được để trống")
    private Long addressShippingId;
    
    // Tùy chọn: Nếu không có, dùng chung địa chỉ giao hàng
    private Long addressBillingId; 

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod; // Phải là 'cod' hoặc 'online'
    
    private String couponCode; // Tùy chọn
}