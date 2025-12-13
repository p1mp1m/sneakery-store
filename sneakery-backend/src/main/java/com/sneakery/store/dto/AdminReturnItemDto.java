package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminReturnItemDto {
    private Long variantId;
    private Long productId;
    private String productName;
    private String productImage;
    private String variant;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Integer goodQuantity;
    private Integer damagedQuantity;
}

