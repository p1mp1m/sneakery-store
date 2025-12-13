package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO: WishlistDto
 * Dữ liệu wishlist item cho frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDto {
    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String brandName;
    private String imageUrl;
    private java.math.BigDecimal price; // Giá hiện tại (sale hoặc base)
    private java.math.BigDecimal priceBase;
    private java.math.BigDecimal priceSale;
    private Boolean isActive;
    private LocalDateTime addedAt;
}

