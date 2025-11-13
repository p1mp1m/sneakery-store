package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception khi không tìm thấy InventoryLog
 */
public class InventoryLogNotFoundException extends ApiException {
    public InventoryLogNotFoundException(Long logId) {
        super(HttpStatus.NOT_FOUND, "Không tìm thấy inventory log với ID: " + logId);
    }
    
    public InventoryLogNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

