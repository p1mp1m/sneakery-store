package com.sneakery.store.util;

import com.sneakery.store.exception.BusinessRuleException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for file upload validation
 */
public class FileValidationUtil {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp"
    );

    /**
     * Validate image file upload
     * 
     * @param file The file to validate
     * @throws BusinessRuleException if file is invalid
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("File không được để trống");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessRuleException(
                    String.format("File không được vượt quá %d MB", MAX_FILE_SIZE / 1024 / 1024)
            );
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessRuleException(
                    String.format("File phải là một trong các định dạng: %s", 
                            String.join(", ", ALLOWED_IMAGE_TYPES))
            );
        }
    }
}

