package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho advanced filter
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdvancedFilterDto {
    // Basic filters (đã có)
    private String search;
    private Integer brandId;
    private String status; // "active", "inactive", "all"
    
    // Advanced filters (mới)
    private Integer categoryId; // Lọc theo danh mục
    private BigDecimal minPrice; // Giá tối thiểu
    private BigDecimal maxPrice; // Giá tối đa
    private String stockLevel; // "in_stock", "low_stock", "out_of_stock", "all"
    private Integer lowStockThreshold; // Ngưỡng tồn kho thấp (mặc định 10)
    
    // Sorting
    private String sortBy; // "name", "price", "stock", "created_at"
    private String sortDirection; // "asc", "desc"
}

