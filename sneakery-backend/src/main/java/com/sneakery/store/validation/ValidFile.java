package com.sneakery.store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for file uploads
 * Validates file size and content type
 */
@Documented
@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {
    String message() default "File không hợp lệ";
    
    long maxSize() default 5242880; // 5MB default
    
    String[] allowedTypes() default {"image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp"};
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

