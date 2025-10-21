package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

// DTO hiển thị 1 món hàng trong giỏ
@Data
@Builder
public class CartItemDto {
    private Long cartItemId;
    private Long variantId;
    private String productName;
    private String brandName;
    private String size;
    private String color;
    private String imageUrl;
    private int quantity;
    private BigDecimal unitPrice; // Giá của 1 sản phẩm (đã sale nếu có)
    private BigDecimal totalPrice; // quantity * unitPrice
}