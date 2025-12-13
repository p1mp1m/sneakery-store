package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception khi không tìm thấy User
 */
public class UserNotFoundException extends ApiException {
    public UserNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID: " + userId);
    }
    
    public UserNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

