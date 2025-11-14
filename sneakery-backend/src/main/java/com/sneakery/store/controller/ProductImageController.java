package com.sneakery.store.controller;

import com.sneakery.store.dto.ProductImageDto;
import com.sneakery.store.service.ProductImageService;
import com.sneakery.store.util.FileValidationUtil;
import jakarta.validation.Valid;
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
                                                           @Valid @RequestBody ProductImageDto dto) {
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
        // Validate file before processing
        FileValidationUtil.validateImageFile(file);
        
        ProductImageDto dto = productImageService.uploadImageFile(productId, file, isPrimary, displayOrder);
        return ResponseEntity.ok(dto);
    }

    // ==========================================================
    // [DELETE] XOÁ ẢNH THEO URL (FE gửi { "imageUrl": "..." })
    // ==========================================================
    @DeleteMapping
    public ResponseEntity<Void> deleteByUrl(@PathVariable Long productId,
                                            @Valid @RequestBody ProductImageDto dto) {
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

/**
 * Controller: ProductImageSyncController
 * --------------------------------------
 * Controller riêng cho các endpoint sync ảnh (không cần productId)
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
class ProductImageSyncController {

    private final ProductImageService productImageService;

    // ==========================================================
    // [POST] SYNC ẢNH CHO TẤT CẢ SẢN PHẨM
    // ==========================================================
    /**
     * Đồng bộ Product.mainImageUrl cho tất cả sản phẩm
     * Endpoint này sẽ:
     * 1. Set isPrimary=true cho ảnh đầu tiên nếu sản phẩm chưa có ảnh primary
     * 2. Update Product.mainImageUrl từ ProductImage có isPrimary=true
     * 
     * @return Số lượng sản phẩm đã được sync
     */
    @PostMapping("/sync-images")
    public ResponseEntity<Object> syncAllProductsImages() {
        log.info("🔄 Admin trigger sync ảnh cho tất cả sản phẩm");
        try {
            int syncedCount = productImageService.syncAllProductsMainImageUrl();
            return ResponseEntity.ok(java.util.Map.of(
                    "success", true,
                    "message", "Đã đồng bộ ảnh cho " + syncedCount + " sản phẩm",
                    "syncedCount", syncedCount
            ));
        } catch (Exception e) {
            log.error("❌ Lỗi khi sync ảnh cho tất cả sản phẩm: {}", e.getMessage(), e);
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of(
                            "success", false,
                            "message", "Lỗi khi đồng bộ ảnh: " + e.getMessage()
                    ));
        }
    }
}