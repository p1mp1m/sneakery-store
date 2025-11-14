package com.sneakery.store.validation;

import com.sneakery.store.repository.ProductRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Validator implementation cho @UniqueSlug
 * Kiểm tra slug unique trong database
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Component
public class UniqueSlugValidator implements ConstraintValidator<UniqueSlug, String> {

    @Autowired
    private ProductRepository productRepository;

    private String excludeId;
    private String entityType;

    @Override
    public void initialize(UniqueSlug constraintAnnotation) {
        this.excludeId = constraintAnnotation.excludeId();
        this.entityType = constraintAnnotation.entityType();
    }

    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        if (slug == null || slug.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        // Normalize slug
        String normalizedSlug = slug.trim().toLowerCase();

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

        // Check uniqueness based on entity type
        boolean exists = false;

        switch (entityType.toLowerCase()) {
            case "product":
                if (excludeIdLong != null) {
                    // Update case: check if slug exists for other products
                    exists = productRepository.findBySlug(normalizedSlug)
                            .map(p -> !p.getId().equals(excludeIdLong))
                            .orElse(false);
                } else {
                    // Create case: check if slug exists
                    exists = productRepository.findBySlug(normalizedSlug).isPresent();
                }
                break;
            default:
                // Default to product (can be extended for brand, category, etc. in the future)
                if (excludeIdLong != null) {
                    exists = productRepository.findBySlug(normalizedSlug)
                            .map(p -> !p.getId().equals(excludeIdLong))
                            .orElse(false);
                } else {
                    exists = productRepository.findBySlug(normalizedSlug).isPresent();
                }
                break;
        }

        if (exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Slug '" + slug + "' đã tồn tại. Vui lòng chọn slug khác"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}

