package com.sneakery.store.controller;

import com.sneakery.store.dto.ProductImageDto;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.ProductImage;
import com.sneakery.store.repository.ProductImageRepository;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Controller: FileUploadController
 * --------------------------------
 * API upload ảnh sản phẩm, lưu file thật vào uploads/sanpham/{productId}/
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/uploads")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

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
        log.info("📤 Uploading image for product {}", productId);

        try {
            // 1️⃣ Lưu file vật lý
            String imageUrl = fileStorageService.storeProductImage(productId, file);

            // 2️⃣ Lấy product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + productId));

            // 3️⃣ Nếu là ảnh đầu tiên -> tự động set isPrimary = true
            long existingCount = productImageRepository.countByProductId(productId);
            boolean finalPrimary = (isPrimary != null && isPrimary) || existingCount == 0;

            // 4️⃣ Nếu set ảnh chính -> bỏ cờ primary ảnh cũ
            if (finalPrimary) {
                productImageRepository.findByProductIdAndIsPrimaryTrue(productId)
                        .ifPresent(img -> {
                            img.setIsPrimary(false);
                            productImageRepository.save(img);
                        });
            }

            // 5️⃣ Tính displayOrder (bắt đầu từ 1)
            int order = (displayOrder != null && displayOrder > 0)
                    ? displayOrder
                    : (int) existingCount + 1;

            // 6️⃣ Tạo entity và lưu DB
            ProductImage newImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrl)
                    .altText(product.getName())
                    .isPrimary(finalPrimary)
                    .displayOrder(order)
                    .createdAt(LocalDateTime.now())
                    .build();

            ProductImage saved = productImageRepository.save(newImage);

            log.info("✅ Upload thành công cho product {} → {}", productId, imageUrl);

            // 7️⃣ Trả DTO về FE
            ProductImageDto dto = ProductImageDto.builder()
                    .id(saved.getId())
                    .imageUrl(saved.getImageUrl())
                    .altText(saved.getAltText())
                    .isPrimary(saved.getIsPrimary())
                    .displayOrder(saved.getDisplayOrder())
                    .build();

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            log.error("❌ Lỗi upload ảnh cho product {}: {}", productId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
