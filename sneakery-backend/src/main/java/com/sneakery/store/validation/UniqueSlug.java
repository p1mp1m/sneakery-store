package com.sneakery.store.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation để kiểm tra slug unique
 * Hỗ trợ excludeId để dùng cho update (cho phép slug trùng với chính nó)
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Documented
@Constraint(validatedBy = UniqueSlugValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueSlug {
    String message() default "Slug đã tồn tại. Vui lòng chọn slug khác";
    
    /**
     * ID của entity hiện tại (dùng cho update - cho phép slug trùng với chính nó)
     * Nếu null hoặc 0, sẽ kiểm tra unique cho create
     */
    String excludeId() default "";
    
    /**
     * Entity type để xác định repository nào cần dùng
     * Có thể là: "product", "brand", "category", "material", "shoeSole"
     */
    String entityType() default "product";
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

