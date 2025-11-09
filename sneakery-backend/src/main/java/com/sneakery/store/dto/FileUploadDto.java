package com.sneakery.store.dto;

import com.sneakery.store.validation.ValidFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for file upload validation
 */
@Data
public class FileUploadDto {
    
    @ValidFile(maxSize = 5242880, allowedTypes = {"image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp"})
    private MultipartFile file;
    
    private Boolean isPrimary;
    
    private Integer displayOrder;
}

