package com.sneakery.store.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho Coupon (Mã giảm giá)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    private Integer id;

    @NotBlank(message = "Mã coupon không được để trống")
    private String code; // VD: SUMMER2025

    private String description; // Mô tả ngắn

    @NotBlank(message = "Loại giảm giá không được để trống")
    private String discountType; // 'percent' hoặc 'fixed'

    @NotNull(message = "Giá trị giảm không được để trống")
    @Positive(message = "Giá trị giảm phải > 0")
    private BigDecimal value; // 10 (%) hoặc 50000 (VND)

    private BigDecimal maxDiscountAmount; // Số tiền giảm tối đa (cho % discount)
    
    private BigDecimal minOrderAmount; // Đơn hàng tối thiểu để áp dụng

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startAt;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endAt;

    private Integer maxUses; // Tổng số lượt dùng (null = không giới hạn)

    private Integer usesCount; // Số lượt đã dùng

    private Integer maxUsesPerUser; // Số lần tối đa mỗi user dùng (default: 1)

    private Boolean isActive; // Trạng thái kích hoạt
}

