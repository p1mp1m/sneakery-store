package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception khi không tìm thấy Product
 */
public class ProductNotFoundException extends ApiException {
    public ProductNotFoundException(Long productId) {
        super(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + productId);
    }
    
    public ProductNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

