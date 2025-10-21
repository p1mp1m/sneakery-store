package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCardDto {
    private Long id;
    private String name;
    private String slug;
    private String brandName;
    private String imageUrl; // Ảnh đại diện của sản phẩm
    private BigDecimal price;    // Giá (ưu tiên giá sale nếu có)
}