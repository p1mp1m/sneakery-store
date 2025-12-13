package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO đơn giản cho Category trong danh sách sản phẩm
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCategoryDto {
    private Integer id;
    private String name;
}

