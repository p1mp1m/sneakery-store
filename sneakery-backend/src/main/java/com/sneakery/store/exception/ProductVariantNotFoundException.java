package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception khi không tìm thấy ProductVariant
 */
public class ProductVariantNotFoundException extends ApiException {
    public ProductVariantNotFoundException(Long variantId) {
        super(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể với ID: " + variantId);
    }
    
    public ProductVariantNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

