package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdminVariantRequestDto {
    
    // ID dùng để cập nhật (sẽ là null khi tạo mới)
    private Long id; 

    @NotBlank(message = "SKU không được để trống")
    private String sku;

    @NotBlank(message = "Size không được để trống")
    private String size;

    @NotBlank(message = "Màu không được để trống")
    private String color;

    @NotNull(message = "Giá gốc không được để trống")
    @PositiveOrZero(message = "Giá gốc phải lớn hơn hoặc bằng 0")
    private BigDecimal priceBase;

    @PositiveOrZero(message = "Giá sale phải lớn hơn hoặc bằng 0")
    private BigDecimal priceSale;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @PositiveOrZero(message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity;

    private String imageUrl;
}