package com.sneakery.store.controller;

import com.sneakery.store.dto.ProductImageDto;
import com.sneakery.store.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller: ProductImageController
 * Quản lý API cho upload, danh sách và xóa ảnh sản phẩm.
 */
@Slf4j
@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    /** Lấy danh sách ảnh của sản phẩm */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageDto>> getProductImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getProductImages(productId));
    }

    /** Upload ảnh mới (form-data: file, isPrimary) */
    @PostMapping("/product/{productId}")
    public ResponseEntity<ProductImageDto> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false, defaultValue = "false") boolean isPrimary
    ) {
        try {
            // Giả định service upload lưu file vào thư mục uploads/ (đã có cấu hình static)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = "uploads/products/" + productId + "/" + fileName;
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("uploads/products/" + productId));
            file.transferTo(java.nio.file.Paths.get(filePath));

            ProductImageDto dto = ProductImageDto.builder()
                    .imageUrl("/" + filePath.replace("\\", "/"))
                    .altText(file.getOriginalFilename())
                    .isPrimary(isPrimary)
                    .displayOrder(0)
                    .build();

            return ResponseEntity.ok(productImageService.addProductImage(productId, dto));
        } catch (Exception e) {
            log.error("Upload image failed: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể upload ảnh");
        }
    }

    /** Xóa ảnh */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteProductImage(imageId);
        return ResponseEntity.ok("Đã xóa ảnh thành công");
    }
}
