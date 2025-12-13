package com.sneakery.store.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO: FlashSaleDto
 * Dữ liệu Flash Sale
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleDto {
    private Integer id;
    
    @NotNull(message = "Product ID không được để trống")
    private Long productId;
    
    // Thông tin sản phẩm (cho response)
    private String productName;
    private String productSlug;
    private String brandName;
    private String imageUrl;
    private BigDecimal originalPrice; // Giá gốc từ product variants
    
    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Phần trăm giảm giá phải >= 1")
    @Max(value = 100, message = "Phần trăm giảm giá phải <= 100")
    private BigDecimal discountPercent;
    
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startTime;
    
    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endTime;
    
    private Integer quantityLimit;
    private Integer soldCount;
    private Boolean isActive;
    
    // Calculated fields
    private Boolean isCurrentlyActive; // Đang diễn ra
    private Boolean hasAvailableSlots; // Còn slot
    private Long secondsRemaining; // Số giây còn lại
}

