package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO cho guest checkout request
@Data
public class GuestCheckoutRequestDto {

    @NotBlank(message = "Session ID không được để trống")
    private String sessionId;

    // Thông tin người nhận
    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String email; // Optional - để nhận thông tin đơn hàng

    // Địa chỉ giao hàng
    @NotBlank(message = "Địa chỉ không được để trống")
    private String line1;

    private String line2; // Optional

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String city;

    private String ward; // Optional

    private String postalCode; // Optional

    // Phương thức thanh toán
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod; // 'cod' hoặc 'online'

    private String couponCode; // Optional

    private String customerNote; // Optional - ghi chú của khách hàng
}

