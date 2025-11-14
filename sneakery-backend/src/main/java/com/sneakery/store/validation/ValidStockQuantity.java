package com.sneakery.store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation để kiểm tra stock quantity
 * Đảm bảo stock_quantity >= 0
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = ValidStockQuantityValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStockQuantity {
    String message() default "Số lượng tồn kho phải lớn hơn hoặc bằng 0";
    
    /**
     * Cho phép giá trị null không
     * Mặc định: true (null sẽ được coi là hợp lệ, có thể dùng @NotNull riêng)
     */
    boolean allowNull() default true;
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

