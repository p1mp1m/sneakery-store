// file: com/sneakery/store/controller/FileUploadController.java
package com.sneakery.store.controller;

import com.sneakery.store.dto.ProductImageDto;
import com.sneakery.store.service.ProductImageService;
import com.sneakery.store.util.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/admin/uploads")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final ProductImageService productImageService;

    /**
     * Upload ảnh sản phẩm (multipart/form-data)
     */
    @PostMapping("/product/{productId}")
    public ResponseEntity<ProductImageDto> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false) Boolean isPrimary,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder
    ) {
        // Validate file before processing
        FileValidationUtil.validateImageFile(file);
        
        try {
            ProductImageDto dto = productImageService.uploadImageFile(
                    productId,
                    file,
                    Boolean.TRUE.equals(isPrimary),
                    displayOrder
            );
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("❌ Lỗi upload ảnh cho product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
