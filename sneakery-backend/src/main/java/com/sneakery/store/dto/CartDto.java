package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

// DTO hiển thị toàn bộ giỏ hàng
@Data
@Builder
public class CartDto {
    private Long cartId;
    private List<CartItemDto> items;
    private Integer totalItems; // Tổng số lượng sản phẩm
    private BigDecimal subTotal; // Tổng tiền
}