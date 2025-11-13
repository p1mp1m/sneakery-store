package com.sneakery.store.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception khi không tìm thấy EmailTemplate
 */
public class EmailTemplateNotFoundException extends ApiException {
    public EmailTemplateNotFoundException(Integer templateId) {
        super(HttpStatus.NOT_FOUND, "Không tìm thấy email template với ID: " + templateId);
    }
    
    public EmailTemplateNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

