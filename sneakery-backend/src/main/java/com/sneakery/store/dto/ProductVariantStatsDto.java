package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantStatsDto {
    
    private Long totalVariants;
    private Long inStockVariants;
    private Long lowStockVariants;
    private Long outOfStockVariants;
    private Long totalStockValue;
    private Long averageStockPerVariant;
    private Long lowStockThreshold;
}
