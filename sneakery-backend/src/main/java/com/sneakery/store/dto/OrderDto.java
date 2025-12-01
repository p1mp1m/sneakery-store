package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String orderNumber; // Mã đơn hàng
    private String status;
    private BigDecimal subtotal; // Tạm tính (trước khi giảm giá)
    private BigDecimal discountAmount; // Số tiền giảm giá
    private String couponCode; // Mã giảm giá đã áp dụng
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private Integer pointsUsed;
    private BigDecimal pointsDiscount;
    private BigDecimal taxableAmount;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private AddressDto addressShipping;
    private PaymentDto payment; // Lấy thanh toán đầu tiên
    private List<CartItemDto> orderDetails; // Dùng lại CartItemDto cho tiện
    private List<OrderStatusHistoryDto> statusHistories; // Lịch sử thay đổi trạng thái
    private ReturnRequestDto returnRequest; // Thông tin return request (nếu có)
    @Schema(description = "Tên khách POS (chỉ dùng cho đơn POS, không persist DB)")
    private String posCustomerName;

    @Schema(description = "Email khách POS (chỉ dùng cho đơn POS, không persist DB)")
    private String posCustomerEmail;

    @Schema(description = "Số điện thoại khách POS (chỉ dùng cho đơn POS, không persist DB)")
    private String posCustomerPhone;
}