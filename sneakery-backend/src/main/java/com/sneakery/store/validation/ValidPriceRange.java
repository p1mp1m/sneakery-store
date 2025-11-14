package com.sneakery.store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation để kiểm tra price range
 * Đảm bảo price_sale <= price_base và cả hai đều > 0
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = ValidPriceRangeValidator.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPriceRange {
    String message() default "Giá bán phải nhỏ hơn hoặc bằng giá gốc và cả hai đều phải lớn hơn 0";
    
    /**
     * Tên field chứa price_base
     */
    String basePriceField() default "priceBase";
    
    /**
     * Tên field chứa price_sale
     */
    String salePriceField() default "priceSale";
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

