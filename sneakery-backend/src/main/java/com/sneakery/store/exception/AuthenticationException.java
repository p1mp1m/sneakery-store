package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception cho các lỗi authentication
 */
public class AuthenticationException extends ApiException {
    public AuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}

