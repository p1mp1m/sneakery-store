package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductVariantDto {
    
    private Long id;
    private String sku;
    private String size;
    private String color;
    private BigDecimal priceBase;
    private BigDecimal priceSale;
    private BigDecimal costPrice;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private Integer weightGrams;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Product info
    private Long productId;
    private String productName;
    private String productSlug;
    private String brandName;
    
    // Computed fields
    private String currentPrice;
    private String stockStatus;
    private Boolean isLowStock;
    private Boolean isOutOfStock;
}
