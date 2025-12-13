package com.sneakery.store.validation;

import com.sneakery.store.repository.ProductVariantRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validator implementation cho @ValidSKU
 * Kiểm tra SKU format và unique trong database
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Component
public class ValidSKUValidator implements ConstraintValidator<ValidSKU, String> {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    private String excludeId;
    private boolean checkUnique;
    
    // SKU pattern: Chữ hoa, số, dấu gạch ngang, tối đa 100 ký tự
    private static final String SKU_PATTERN = "^[A-Z0-9-]{1,100}$";

    @Override
    public void initialize(ValidSKU constraintAnnotation) {
        this.excludeId = constraintAnnotation.excludeId();
        this.checkUnique = constraintAnnotation.checkUnique();
    }

    @Override
    public boolean isValid(String sku, ConstraintValidatorContext context) {
        if (sku == null || sku.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        String normalizedSku = sku.trim().toUpperCase();

        // Check format
        if (!normalizedSku.matches(SKU_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "SKU không hợp lệ. SKU chỉ được chứa chữ hoa, số và dấu gạch ngang, tối đa 100 ký tự"
            ).addConstraintViolation();
            return false;
        }

        // Check unique if enabled
        if (checkUnique) {
            // Parse excludeId to Long (final for lambda usage)
            Long parsedId = null;
            if (excludeId != null && !excludeId.isEmpty() && !excludeId.equals("0")) {
                try {
                    parsedId = Long.parseLong(excludeId);
                } catch (NumberFormatException e) {
                    // Invalid excludeId, treat as create
                    parsedId = null;
                }
            }
            final Long excludeIdLong = parsedId;

            boolean exists = false;
            if (excludeIdLong != null) {
                // Update case: check if SKU exists for other variants
                exists = productVariantRepository.findBySku(normalizedSku)
                        .map(v -> !v.getId().equals(excludeIdLong))
                        .orElse(false);
            } else {
                // Create case: check if SKU exists
                exists = productVariantRepository.existsBySku(normalizedSku);
            }

            if (exists) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "SKU '" + normalizedSku + "' đã tồn tại. Vui lòng chọn SKU khác"
                ).addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}

