package com.sneakery.store.service;

import com.sneakery.store.dto.ProductImageDto;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.ProductImage;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.ProductImageRepository;
import com.sneakery.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service: ProductImageService
 * ----------------------------
 * Quản lý nghiệp vụ hình ảnh sản phẩm (gallery)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    /**
     * Chuẩn hoá URL ảnh về dạng tương đối để khớp DB.
     * FE thường gửi full URL (http://localhost:5173/uploads/...),
     * trong khi DB lưu /uploads/...
     */
    private String normalizeImageUrl(String url) {
        if (url == null) return null;
        int idx = url.indexOf("/uploads/");
        if (idx != -1) {
            return url.substring(idx); // chỉ lấy phần "/uploads/..."
        }
        return url;
    }

    // ==========================================================
    // [1] LẤY DANH SÁCH ẢNH CỦA SẢN PHẨM
    // ==========================================================
    @Transactional(readOnly = true)
    public List<ProductImageDto> getProductImages(Long productId) {
        log.info("📸 Fetching images for product ID: {}", productId);
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        return images.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // ==========================================================
    // [2] THÊM ẢNH TỪ URL NGOÀI (ví dụ Unsplash)
    // ==========================================================
    @Transactional
    public ProductImageDto addProductImage(Long productId, ProductImageDto dto) {
        log.info("🌐 Adding image URL to product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));

        // Nếu set primary => clear ảnh primary cũ
        if (Boolean.TRUE.equals(dto.getIsPrimary())) {
            productImageRepository.clearPrimaryForProduct(productId);
        }

        // Tính display_order (bắt đầu từ 1)
        Integer displayOrder = dto.getDisplayOrder();
        if (displayOrder == null || displayOrder <= 0) {
            long count = productImageRepository.countByProductId(productId);
            displayOrder = (int) count + 1;
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(dto.getImageUrl())
                .altText(dto.getAltText())
                .isPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false)
                .displayOrder(displayOrder)
                .build();

        productImageRepository.save(image);
        log.info("✅ Added image from URL for product {}: {}", productId, dto.getImageUrl());
        return convertToDto(image);
    }

    // ==========================================================
    // [3] UPLOAD FILE ẢNH TỪ LOCAL (multipart/form-data)
    // ==========================================================
    @Transactional
    public ProductImageDto uploadImageFile(Long productId, MultipartFile file, boolean isPrimary, Integer displayOrder) {
        log.info("🖼️ Uploading local file for product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));

        // Lưu file vật lý
        String imageUrl = fileStorageService.storeProductImage(productId, file);

        // Nếu chưa có ảnh primary => tự động set ảnh đầu tiên
        boolean hasPrimary = productImageRepository.existsByProductIdAndIsPrimaryTrue(productId);
        boolean finalPrimary = isPrimary || !hasPrimary;

        // Nếu set primary => clear cũ
        if (finalPrimary) {
            productImageRepository.clearPrimaryForProduct(productId);
        }

        // Tính displayOrder (bắt đầu từ 1)
        long count = productImageRepository.countByProductId(productId);
        int finalOrder = (displayOrder != null && displayOrder > 0)
                ? displayOrder
                : (int) count + 1;

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isPrimary(finalPrimary)
                .displayOrder(finalOrder)
                .build();

        productImageRepository.save(image);
        log.info("✅ Uploaded image {} for product {}", imageUrl, productId);
        return convertToDto(image);
    }

    // ==========================================================
    // [4] XÓA ẢNH THEO URL (FE gửi { imageUrl })
    // ==========================================================
    @Transactional
    public void deleteByUrl(Long productId, String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Thiếu đường dẫn ảnh cần xoá");
        }

        log.info("🗑️ Request xoá ảnh sản phẩm {} với URL: {}", productId, imageUrl);

        // Chuẩn hoá URL để khớp DB (nếu FE gửi full absolute path)
        String normalizedUrl = normalizeImageUrl(imageUrl);

        // Tìm ảnh trong DB
        ProductImage image = productImageRepository
                .findByProductIdAndImageUrl(productId, normalizedUrl)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy ảnh trong DB với URL: " + normalizedUrl));

        // Xoá file vật lý (nếu tồn tại)
        try {
            fileStorageService.deleteFileByUrl(image.getImageUrl());
        } catch (Exception e) {
            log.warn("⚠️ Không thể xoá file vật lý cho ảnh {}: {}", normalizedUrl, e.getMessage());
        }

        // Xoá record DB
        productImageRepository.delete(image);
        reorderDisplayOrder(productId);

        log.info("✅ Đã xoá ảnh [{}] của sản phẩm {}", normalizedUrl, productId);
    }


    // ==========================================================
    // [5] XÓA ẢNH THEO ID (ít dùng, giữ cho admin tool)
    // ==========================================================
    @Transactional
    public void deleteProductImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hình ảnh không tồn tại"));
        fileStorageService.deleteFileByUrl(image.getImageUrl());
        Long productId = image.getProduct().getId();
        productImageRepository.delete(image);
        reorderDisplayOrder(productId);
    }

    // ==========================================================
    // [6] ĐẶT ẢNH PRIMARY (qua imageId)
    // ==========================================================
    @Transactional
    public ProductImageDto setPrimaryImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hình ảnh không tồn tại"));
        productImageRepository.clearPrimaryForProduct(image.getProduct().getId());
        image.setIsPrimary(true);
        productImageRepository.save(image);
        log.info("⭐ Set image {} as primary", imageId);
        return convertToDto(image);
    }

    // ==========================================================
    // [7] REORDER DISPLAY ORDER (bắt đầu từ 1)
    // ==========================================================
    private void reorderDisplayOrder(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        int order = 1;
        for (ProductImage img : images) {
            if (img.getDisplayOrder() == null || img.getDisplayOrder() != order) {
                img.setDisplayOrder(order);
                productImageRepository.save(img);
            }
            order++;
        }
        log.info("♻️ Reordered display order for product {}: total {} images", productId, images.size());
    }

    // ==========================================================
    // [8] MAPPING ENTITY ↔ DTO
    // ==========================================================
    private ProductImageDto convertToDto(ProductImage image) {
        return ProductImageDto.builder()
                .id(image.getId())
                .productId(image.getProduct() != null ? image.getProduct().getId() : null)
                .imageUrl(image.getImageUrl())
                .altText(image.getAltText())
                .isPrimary(image.getIsPrimary())
                .displayOrder(image.getDisplayOrder())
                .build();
    }
}
