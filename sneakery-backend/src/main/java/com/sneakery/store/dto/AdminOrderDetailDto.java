package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminOrderDetailDto {
    private Long id;
    private String orderNumber;
    private String status;

    // 💰 Thông tin giá
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private String couponCode;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private Integer pointsUsed;
    @Schema(description = "Điểm thưởng hiện tại còn lại của khách hàng")
    private Integer customerPointBalance;
    private BigDecimal pointsDiscount;
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    // 👤 Customer
    private Long userId;
    private String customerName;
    private String customerEmail;

    // 🏠 Address
    private AddressDto addressShipping;
    private AddressDto addressBilling;

    // 💳 Payment
    private PaymentDto payment;

    // 📦 Items
    private List<CartItemDto> orderDetails;

    // 🕒 Status history
    private List<OrderStatusHistoryDto> statusHistories;

    // 🔁 Return Request
    private ReturnRequestDto returnRequest;
}
