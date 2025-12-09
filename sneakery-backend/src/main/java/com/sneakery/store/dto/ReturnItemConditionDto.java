package com.sneakery.store.dto;

import lombok.Data;

/**
 * Thông tin mỗi biến thể trong yêu cầu trả hàng khi admin xác nhận
 */
@Data
public class ReturnItemConditionDto {

    private Long variantId;       // Biến thể nào
    private int goodQuantity;     // Còn tốt → + vào stock
    private int damagedQuantity;  // Hỏng → + vào damagedQuantity
}

