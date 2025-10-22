package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO: ProductImageDto
 * Dữ liệu hình ảnh sản phẩm
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Long id;
    
    @NotBlank(message = "URL hình ảnh không được để trống")
    private String imageUrl;
    
    private String altText;
    private Boolean isPrimary;
    private Integer displayOrder;
}

