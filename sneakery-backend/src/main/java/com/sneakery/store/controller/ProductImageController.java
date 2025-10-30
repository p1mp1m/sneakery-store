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
 * ----------------------------------
 * Cung cấp API quản lý ảnh sản phẩm (gallery) cho admin.
 * Tương thích hoàn toàn với FE (UploadGallery.vue + VariantModal.vue)
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    // ==========================================================
    // [GET] LẤY DANH SÁCH ẢNH CỦA SẢN PHẨM
    // ==========================================================
    @GetMapping
    public ResponseEntity<List<ProductImageDto>> getProductImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getProductImages(productId));
    }

    // ==========================================================
    // [POST] THÊM ẢNH TỪ URL NGOÀI (ví dụ Unsplash)
    // ==========================================================
    @PostMapping(consumes = "application/json")
    public ResponseEntity<ProductImageDto> addProductImage(@PathVariable Long productId,
                                                           @RequestBody ProductImageDto dto) {
        return ResponseEntity.ok(productImageService.addProductImage(productId, dto));
    }

    // ==========================================================
    // [POST] UPLOAD FILE LOCAL (multipart/form-data)
    // ==========================================================
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ProductImageDto> uploadProductImage(@PathVariable Long productId,
                                                              @RequestParam("file") MultipartFile file,
                                                              @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
                                                              @RequestParam(value = "displayOrder", required = false) Integer displayOrder) {
        ProductImageDto dto = productImageService.uploadImageFile(productId, file, isPrimary, displayOrder);
        return ResponseEntity.ok(dto);
    }

    // ==========================================================
    // [DELETE] XOÁ ẢNH THEO URL (FE gửi { "imageUrl": "..." })
    // ==========================================================
    @DeleteMapping
    public ResponseEntity<Void> deleteByUrl(@PathVariable Long productId,
                                            @RequestBody ProductImageDto dto) {
        productImageService.deleteByUrl(productId, dto.getImageUrl());
        return ResponseEntity.noContent().build();
    }

    // ==========================================================
    // [DELETE] XOÁ ẢNH THEO ID (admin internal)
    // ==========================================================
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long productId,
                                                   @PathVariable Long imageId) {
        productImageService.deleteProductImage(imageId);
        return ResponseEntity.noContent().build();
    }

    // ==========================================================
    // [POST] SET ẢNH PRIMARY
    // ==========================================================
    @PostMapping("/{imageId}/primary")
    public ResponseEntity<ProductImageDto> setPrimaryImage(@PathVariable Long productId,
                                                           @PathVariable Long imageId) {
        return ResponseEntity.ok(productImageService.setPrimaryImage(imageId));
    }
}
