package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO cho Category Group (Parent category với children)
 * Dùng để hiển thị categories theo nhóm trên frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryGroupDto {
    private Integer id;
    private String name;
    private String slug;
    private Integer productCount; // Tổng số sản phẩm của tất cả children
    private List<CategoryDto> children; // Danh sách child categories
}

