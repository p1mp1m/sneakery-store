package com.sneakery.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho item trong POS order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class POSOrderItemDto {
    
    @NotNull(message = "Product ID không được để trống")
    private Long productId;
    
    private Long variantId; // Optional, có thể null nếu product không có variant
    
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
    
//    @NotNull(message = "Giá đơn vị không được để trống")
//    @Min(value = 0, message = "Giá đơn vị phải >= 0")
//    private BigDecimal unitPrice;
}

