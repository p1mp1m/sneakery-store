package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception cho các lỗi database operation
 */
public class DatabaseOperationException extends ApiException {
    public DatabaseOperationException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
    
    public DatabaseOperationException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
        initCause(cause);
    }
}

