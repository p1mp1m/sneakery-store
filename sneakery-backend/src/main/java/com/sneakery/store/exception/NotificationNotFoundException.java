package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception khi không tìm thấy Notification
 */
public class NotificationNotFoundException extends ApiException {
    public NotificationNotFoundException(Long notificationId) {
        super(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo với ID: " + notificationId);
    }
    
    public NotificationNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

