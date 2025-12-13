package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO: SizeChartDto
 * Dữ liệu bảng size giày
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SizeChartDto {
    private Integer id;
    
    @NotNull(message = "Brand ID không được để trống")
    private Integer brandId;
    
    private String brandName; // Cho response
    
    @NotBlank(message = "Category không được để trống")
    private String category;
    
    @NotBlank(message = "Size không được để trống")
    private String size;
    
    private String sizeUs;
    private String sizeUk;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
}

