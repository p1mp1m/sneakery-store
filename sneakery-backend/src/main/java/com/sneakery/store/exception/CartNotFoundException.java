package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception được throw khi không tìm thấy giỏ hàng
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
public class CartNotFoundException extends ApiException {
    
    public CartNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, 
              String.format("Không tìm thấy giỏ hàng cho user ID: %d", userId),
              "CART_NOT_FOUND");
    }
    
    public CartNotFoundException() {
        super(HttpStatus.NOT_FOUND, 
              "Không tìm thấy giỏ hàng",
              "CART_NOT_FOUND");
    }
}

