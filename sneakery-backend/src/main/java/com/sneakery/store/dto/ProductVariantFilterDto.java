package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantFilterDto {
    
    private String search;
    private String color;
    private String size;
    private String stockStatus; // in_stock, low_stock, out_of_stock
    private Long productId;
    private String sortBy; // sku, size, color, price, stock, created_at
    private String sortDirection; // asc, desc
}
