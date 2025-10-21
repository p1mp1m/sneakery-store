package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho thống kê sản phẩm
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatsDto {
    private Long totalProducts; // Tổng số sản phẩm
    private Long totalVariants; // Tổng số biến thể
    private Long activeProducts; // Số sản phẩm đang bán
    private Long inactiveProducts; // Số sản phẩm ngừng bán
    
    private Long totalStock; // Tổng tồn kho
    private Long lowStockCount; // Số variant sắp hết hàng
    private Long outOfStockCount; // Số variant hết hàng
    
    private BigDecimal avgPrice; // Giá trung bình
    private BigDecimal maxPrice; // Giá cao nhất
    private BigDecimal minPrice; // Giá thấp nhất
    
    private Integer totalBrands; // Số thương hiệu
    private Integer totalCategories; // Số danh mục
}

