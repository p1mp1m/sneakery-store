package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdminProductVariantRequestDto {
    
    private Long id;
    
    @NotNull(message = "Product ID không được để trống")
    private Long productId;
    
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
    
    @PositiveOrZero(message = "Giá cost phải lớn hơn hoặc bằng 0")
    private BigDecimal costPrice;
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @PositiveOrZero(message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity;
    
    @PositiveOrZero(message = "Ngưỡng tồn kho thấp phải lớn hơn hoặc bằng 0")
    private Integer lowStockThreshold;
    
    @PositiveOrZero(message = "Trọng lượng phải lớn hơn hoặc bằng 0")
    private Integer weightGrams;
    
//    private String imageUrl;
    private Boolean isActive;
}
