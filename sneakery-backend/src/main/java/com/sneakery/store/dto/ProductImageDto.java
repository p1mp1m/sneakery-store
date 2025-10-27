package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO: ProductImageDto
 * Dữ liệu trao đổi giữa backend và frontend cho ảnh sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
    private Long id;
    private String imageUrl;
    private String altText;
    private Boolean isPrimary;
    private Integer displayOrder;
}
