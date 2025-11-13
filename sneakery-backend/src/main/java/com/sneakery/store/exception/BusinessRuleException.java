package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception cho các lỗi business rule
 */
public class BusinessRuleException extends ApiException {
    public BusinessRuleException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
    
    public BusinessRuleException(String message, HttpStatus status) {
        super(status, message);
    }
}

