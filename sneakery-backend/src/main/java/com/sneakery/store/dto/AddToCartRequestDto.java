package com.sneakery.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO cho body của request Thêm/Cập nhật giỏ hàng
@Data
public class AddToCartRequestDto {
    @NotNull(message = "variantId không được để trống")
    private Long variantId;

    @NotNull(message = "quantity không được để trống")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private Integer quantity;
}