package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đơn giản cho danh sách sản phẩm (Admin)
 * Chỉ chứa thông tin cần thiết để hiển thị trong table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductListDto {
    private Long id;
    private String name;
    private String slug;
    private Integer brandId;
    private String brandName; // Tên thương hiệu để hiển thị
    private Boolean isActive;
    private Integer variantCount; // Số lượng variants
}

