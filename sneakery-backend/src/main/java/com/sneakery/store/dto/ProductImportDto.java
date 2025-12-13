package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * DTO cho import sản phẩm từ Excel
 * Mỗi dòng trong Excel sẽ map thành 1 ProductImportDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImportDto {
    // Thông tin sản phẩm
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;
    
    private String productSlug; // Tự động tạo nếu để trống
    
    @NotBlank(message = "Tên thương hiệu không được để trống")
    private String brandName; // Tên thương hiệu (sẽ tìm hoặc tạo mới)
    
    private String description;
    
    @NotBlank(message = "Danh mục không được để trống")
    private String categories; // Phân cách bởi dấu phẩy: "Men,Sneakers"
    
    private Boolean isActive; // Mặc định true
    
    // Thông tin variant
    @NotBlank(message = "SKU không được để trống")
    private String sku;
    
    @NotBlank(message = "Size không được để trống")
    private String size;
    
    @NotBlank(message = "Màu sắc không được để trống")
    private String color;
    
    @NotNull(message = "Giá gốc không được để trống")
    @PositiveOrZero(message = "Giá gốc phải >= 0")
    private BigDecimal priceBase;
    
    @PositiveOrZero(message = "Giá sale phải >= 0")
    private BigDecimal priceSale;
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @PositiveOrZero(message = "Số lượng phải >= 0")
    private Integer stockQuantity;
    
    private String imageUrl;
    
    // Metadata cho validation
    private Integer rowNumber; // Số dòng trong Excel (để báo lỗi)
    private String errorMessage; // Lỗi validation nếu có
}

