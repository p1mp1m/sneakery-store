package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO cho Product Card hiển thị trong grid/list
 * Chứa đầy đủ thông tin cần thiết để render product card ở frontend
 */
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
    
    // Pricing
    private BigDecimal priceBase;    // Giá gốc
    private BigDecimal priceSale;    // Giá sale (nếu có)
    private BigDecimal price;        // Giá hiển thị (ưu tiên sale, fallback base)
    
    // Stats & Badges
    private Double avgRating;        // Rating trung bình (0-5)
    private Integer reviewCount;     // Số lượng reviews
    private Boolean isNew;           // Badge "NEW"
    private Boolean isFeatured;      // Badge "FEATURED"
    
    // Stock status
    private Integer totalStock;      // Tổng số lượng tồn kho (tất cả variants)
    private Boolean inStock;         // Còn hàng hay không
    
    // Categories
    private Set<CategoryDto> categories; // Danh sách categories của sản phẩm
}