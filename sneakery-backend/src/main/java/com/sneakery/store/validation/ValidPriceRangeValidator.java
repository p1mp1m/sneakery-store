package com.sneakery.store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * Validator implementation cho @ValidPriceRange
 * Kiểm tra price_sale <= price_base và cả hai đều > 0
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
public class ValidPriceRangeValidator implements ConstraintValidator<ValidPriceRange, Object> {

    private String basePriceField;
    private String salePriceField;

    @Override
    public void initialize(ValidPriceRange constraintAnnotation) {
        this.basePriceField = constraintAnnotation.basePriceField();
        this.salePriceField = constraintAnnotation.salePriceField();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true; // Let @NotNull handle null validation
        }

        try {
            BigDecimal basePrice = getFieldValue(obj, basePriceField, BigDecimal.class);
            BigDecimal salePrice = getFieldValue(obj, salePriceField, BigDecimal.class);

            // Base price must be > 0
            if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Giá gốc phải lớn hơn 0"
                ).addPropertyNode(basePriceField).addConstraintViolation();
                return false;
            }

            // If sale price is provided, it must be > 0 and <= base price
            if (salePrice != null) {
                if (salePrice.compareTo(BigDecimal.ZERO) <= 0) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Giá bán phải lớn hơn 0"
                    ).addPropertyNode(salePriceField).addConstraintViolation();
                    return false;
                }

                if (salePrice.compareTo(basePrice) > 0) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Giá bán không được lớn hơn giá gốc"
                    ).addPropertyNode(salePriceField).addConstraintViolation();
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            // If we can't access fields, skip validation (let other validators handle it)
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object obj, String fieldName, Class<T> fieldType) throws Exception {
        Class<?> clazz = obj.getClass();
        
        // Try to find field in current class and superclasses
        Field field = null;
        Class<?> currentClass = clazz;
        while (currentClass != null && field == null) {
            try {
                field = currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }

        if (field == null) {
            return null;
        }

        field.setAccessible(true);
        Object value = field.get(obj);
        
        if (value == null) {
            return null;
        }

        if (fieldType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        return null;
    }
}

