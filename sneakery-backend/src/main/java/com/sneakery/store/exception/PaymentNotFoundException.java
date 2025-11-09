package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception khi không tìm thấy Payment
 */
public class PaymentNotFoundException extends ApiException {
    public PaymentNotFoundException(Long paymentId) {
        super(HttpStatus.NOT_FOUND, "Không tìm thấy payment với ID: " + paymentId);
    }
    
    public PaymentNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

