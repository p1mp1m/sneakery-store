package com.sneakery.store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation để kiểm tra SKU format và unique
 * SKU format: Chữ hoa, số, dấu gạch ngang, tối đa 100 ký tự
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = ValidSKUValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSKU {
    String message() default "SKU không hợp lệ. SKU chỉ được chứa chữ hoa, số và dấu gạch ngang, tối đa 100 ký tự";
    
    /**
     * ID của variant hiện tại (dùng cho update - cho phép SKU trùng với chính nó)
     * Nếu null hoặc 0, sẽ kiểm tra unique cho create
     */
    String excludeId() default "";
    
    /**
     * Có kiểm tra unique trong database không
     * Mặc định: true
     */
    boolean checkUnique() default true;
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

