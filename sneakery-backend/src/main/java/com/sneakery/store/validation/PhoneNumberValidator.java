package com.sneakery.store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for Vietnamese phone numbers
 * Supports formats:
 * - 0xxxxxxxxx (10 digits starting with 0)
 * - +84xxxxxxxxx (11 digits with country code)
 * - 84xxxxxxxxx (11 digits without +)
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final String PHONE_PATTERN = "^(0|\\+84|84)[0-9]{9,10}$";

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return true; // Let @NotBlank handle null/empty validation
        }

        // Remove spaces and dashes
        String cleaned = phoneNumber.replaceAll("[\\s-]", "");

        // Check if matches Vietnamese phone number pattern
        return cleaned.matches(PHONE_PATTERN);
    }
}

