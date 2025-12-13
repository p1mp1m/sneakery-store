package com.sneakery.store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation cho @ValidStockQuantity
 * Kiểm tra stock_quantity >= 0
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
public class ValidStockQuantityValidator implements ConstraintValidator<ValidStockQuantity, Integer> {

    private boolean allowNull;

    @Override
    public void initialize(ValidStockQuantity constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer stockQuantity, ConstraintValidatorContext context) {
        if (stockQuantity == null) {
            return allowNull; // Let @NotNull handle null validation if allowNull = false
        }

        if (stockQuantity < 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Số lượng tồn kho phải lớn hơn hoặc bằng 0"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}

