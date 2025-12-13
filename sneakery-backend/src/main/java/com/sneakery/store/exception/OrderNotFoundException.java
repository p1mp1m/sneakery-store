package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception được throw khi không tìm thấy đơn hàng
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
public class OrderNotFoundException extends ApiException {
    
    public OrderNotFoundException(Long orderId) {
        super(HttpStatus.NOT_FOUND, 
              String.format("Không tìm thấy đơn hàng với ID: %d", orderId),
              "ORDER_NOT_FOUND");
    }
    
    public OrderNotFoundException(String orderNumber) {
        super(HttpStatus.NOT_FOUND, 
              String.format("Không tìm thấy đơn hàng với mã: %s", orderNumber),
              "ORDER_NOT_FOUND");
    }
}

