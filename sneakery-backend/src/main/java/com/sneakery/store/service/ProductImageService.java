package com.sneakery.store.service;

import com.sneakery.store.dto.ProductImageDto;
import com.sneakery.store.dto.ProductImagePublicDto;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.ProductImage;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.ProductImageRepository;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.util.CloudinaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private final CloudinaryUtil cloudinaryUtil;

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
        Product product = productRepository.findById(Objects.requireNonNull(productId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));

        // Nếu set primary => clear ảnh primary cũ
//        if (Boolean.TRUE.equals(dto.getIsPrimary())) {
//            productImageRepository.clearPrimaryForProduct(productId);
//        }

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
//                .isPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false)
                .displayOrder(displayOrder)
                .build();

        ProductImage saved = productImageRepository.save(Objects.requireNonNull(image));
        
        // 🔄 Tự động sync Product.mainImageUrl nếu là ảnh primary
//        if (Boolean.TRUE.equals(dto.getIsPrimary())) {
//            syncProductMainImageUrl(productId, dto.getImageUrl());
//        }
        
        log.info("✅ Added image from URL for product {}: {}", productId, dto.getImageUrl());
        return convertToDto(saved);
    }

    // ==========================================================
    // [3] UPLOAD FILE ẢNH TỪ LOCAL (multipart/form-data)
    // ==========================================================
    // trong ProductImageService
    @Transactional
    public ProductImageDto uploadImageFile(Long productId, MultipartFile file, boolean isPrimary, Integer displayOrder) {
        log.info("🖼️ Uploading local file for product ID: {}", productId);
        Product product = productRepository.findById(Objects.requireNonNull(productId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));

        // Upload lên Cloudinary → nhận url + publicId
        FileStorageService.CloudinaryUploadResult up = fileStorageService.storeProductImage(productId, file);
        String imageUrl = up.url();
        String publicId = up.publicId();

        // Nếu chưa có ảnh primary => tự động set ảnh đầu tiên
        boolean hasPrimary = productImageRepository.existsByProductIdAndIsPrimaryTrue(productId);
        boolean finalPrimary = isPrimary || !hasPrimary;

        // Nếu set primary => clear cũ
        if (finalPrimary) {
            productImageRepository.clearPrimaryForProduct(productId);
        }

        // Tính displayOrder
        long count = productImageRepository.countByProductId(productId);
        int finalOrder = (displayOrder != null && displayOrder > 0) ? displayOrder : (int) count + 1;

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .cloudinaryPublicId(publicId)        // <<=== LƯU PUBLIC ID
                .altText(product.getName())
                .isPrimary(finalPrimary)
                .displayOrder(finalOrder)
                .build();

        ProductImage saved = productImageRepository.save(Objects.requireNonNull(image));
        
        // 🔄 Tự động sync Product.mainImageUrl nếu là ảnh primary
        if (finalPrimary) {
            syncProductMainImageUrl(productId, imageUrl);
        }
        
        log.info("✅ Uploaded image {} for product {}", imageUrl, productId);
        return convertToDto(saved);
    }


    // ==========================================================
    // [4] XÓA ẢNH THEO URL (FE gửi { imageUrl }) ưu tiên xoá bằng publicId
    // ==========================================================
    @Transactional
    public void deleteByUrl(Long productId, String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Thiếu đường dẫn ảnh cần xoá");
        }

        log.info("🗑️ Request xoá ảnh sản phẩm {} với URL: {}", productId, imageUrl);

        // Nếu DB lưu URL Cloudinary -> không cần normalize "/uploads/"
        // Nhưng ta vẫn tìm theo imageUrl đúng như lưu DB
        ProductImage image = productImageRepository
                .findByProductIdAndImageUrl(productId, imageUrl)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy ảnh trong DB với URL: " + imageUrl));

        // ƯU TIÊN xoá theo public_id
        try {
            if (image.getCloudinaryPublicId() != null && !image.getCloudinaryPublicId().isBlank()) {
                fileStorageService.deleteByPublicId(image.getCloudinaryPublicId());
            } else {
                // fallback nếu dữ liệu cũ chưa có publicId
                fileStorageService.deleteByUrlBestEffort(image.getImageUrl());
            }
        } catch (Exception e) {
            log.warn("⚠️ Không thể xoá file Cloudinary cho ảnh {}: {}", imageUrl, e.getMessage());
        }

        // Xoá record DB + reorder
        Long pid = image.getProduct().getId();
        boolean wasPrimary = Boolean.TRUE.equals(image.getIsPrimary());
        productImageRepository.delete(image);
        reorderDisplayOrder(pid);
        
        // 🔄 Nếu xóa ảnh primary, tự động sync ảnh primary mới (nếu có)
        if (wasPrimary) {
            syncProductMainImageUrlFromPrimary(pid);
        }

        log.info("✅ Đã xoá ảnh [{}] của sản phẩm {}", imageUrl, productId);
    }


    // ==========================================================
    // [5] XÓA ẢNH THEO ID (ít dùng, giữ cho admin tool)
    // ==========================================================
    @Transactional
    public void deleteProductImage(Long imageId) {
        ProductImage image = productImageRepository.findById(Objects.requireNonNull(imageId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hình ảnh không tồn tại"));

        try {
            // ƯU TIÊN XOÁ TRÊN CLOUDINARY BẰNG PUBLIC ID
            if (image.getCloudinaryPublicId() != null && !image.getCloudinaryPublicId().isBlank()) {
                fileStorageService.deleteByPublicId(image.getCloudinaryPublicId());
            } else {
                // fallback nếu ảnh cũ chưa có public_id
                fileStorageService.deleteByUrlBestEffort(image.getImageUrl());
            }
        } catch (Exception e) {
            log.warn("⚠️ Không thể xoá file Cloudinary cho imageId {}: {}", imageId, e.getMessage());
        }

        // Xoá record trong DB
        Long productId = image.getProduct().getId();
        boolean wasPrimary = Boolean.TRUE.equals(image.getIsPrimary());
        productImageRepository.delete(image);

        // Reorder lại thứ tự hiển thị
        reorderDisplayOrder(productId);
        
        // 🔄 Nếu xóa ảnh primary, tự động sync ảnh primary mới (nếu có)
        if (wasPrimary) {
            syncProductMainImageUrlFromPrimary(productId);
        }

        log.info("✅ Đã xoá ảnh ID={} của sản phẩm {}", imageId, productId);
    }

    // ==========================================================
    // [6] ĐẶT ẢNH PRIMARY (qua imageId)
    // ==========================================================
    @Transactional
    public ProductImageDto setPrimaryImage(Long imageId) {
        ProductImage image = productImageRepository.findById(Objects.requireNonNull(imageId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hình ảnh không tồn tại"));
        Long productId = image.getProduct().getId();
        productImageRepository.clearPrimaryForProduct(productId);
        image.setIsPrimary(true);
        ProductImage saved = productImageRepository.save(Objects.requireNonNull(image));
        
        // 🔄 Tự động sync Product.mainImageUrl
        syncProductMainImageUrl(productId, saved.getImageUrl());
        
        log.info("⭐ Set image {} as primary", imageId);
        return convertToDto(saved);
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
    // [8] SYNC PRODUCT.MAIN_IMAGE_URL
    // ==========================================================
    /**
     * Đồng bộ Product.mainImageUrl từ ProductImage có isPrimary=true
     * 
     * @param productId ID sản phẩm
     * @param imageUrl URL ảnh primary (nếu đã biết)
     */
    private void syncProductMainImageUrl(Long productId, String imageUrl) {
        try {
            Product product = productRepository.findById(Objects.requireNonNull(productId))
                    .orElse(null);
            if (product != null) {
                product.setMainImageUrl(imageUrl);
                productRepository.save(product);
                log.info("🔄 Synced Product.mainImageUrl for product {}: {}", productId, imageUrl);
            }
        } catch (Exception e) {
            log.warn("⚠️ Không thể sync Product.mainImageUrl cho product {}: {}", productId, e.getMessage());
        }
    }

    /**
     * Đồng bộ Product.mainImageUrl từ ảnh primary hiện có (nếu có)
     * Dùng khi xóa ảnh primary, cần tìm ảnh primary mới
     */
    private void syncProductMainImageUrlFromPrimary(Long productId) {
        try {
            Optional<ProductImage> primaryImage = productImageRepository.findByProductIdAndIsPrimaryTrue(productId);
            if (primaryImage.isPresent()) {
                syncProductMainImageUrl(productId, primaryImage.get().getImageUrl());
            } else {
                // Không còn ảnh primary, set mainImageUrl = null
                Product product = productRepository.findById(Objects.requireNonNull(productId)).orElse(null);
                if (product != null) {
                    product.setMainImageUrl(null);
                    productRepository.save(product);
                    log.info("🔄 Cleared Product.mainImageUrl for product {} (no primary image)", productId);
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ Không thể sync Product.mainImageUrl từ primary cho product {}: {}", productId, e.getMessage());
        }
    }

    // ==========================================================
    // [9] SYNC ALL PRODUCTS MAIN IMAGE URL
    // ==========================================================
    /**
     * Đồng bộ Product.mainImageUrl cho tất cả sản phẩm
     * Method này sẽ:
     * 1. Set isPrimary=true cho ảnh đầu tiên nếu sản phẩm chưa có ảnh primary
     * 2. Update Product.mainImageUrl từ ProductImage có isPrimary=true
     * 
     * @return Số lượng sản phẩm đã được sync
     */
    @Transactional
    public int syncAllProductsMainImageUrl() {
        log.info("🔄 Bắt đầu sync mainImageUrl cho tất cả sản phẩm...");
        int syncedCount = 0;
        int setPrimaryCount = 0;
        
        // Lấy tất cả sản phẩm (chưa bị xóa)
        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.getDeletedAt() == null)
                .collect(Collectors.toList());
        
        log.info("📦 Tìm thấy {} sản phẩm cần sync", products.size());
        
        for (Product product : products) {
            try {
                Long productId = product.getId();
                
                // Kiểm tra xem có ảnh primary chưa
                Optional<ProductImage> primaryImageOpt = productImageRepository.findByProductIdAndIsPrimaryTrue(productId);
                
                if (primaryImageOpt.isEmpty()) {
                    // Chưa có ảnh primary, set ảnh đầu tiên làm primary
                    List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
                    if (!images.isEmpty()) {
                        ProductImage firstImage = images.get(0);
                        firstImage.setIsPrimary(true);
                        productImageRepository.save(firstImage);
                        setPrimaryCount++;
                        log.debug("✅ Set primary cho ảnh đầu tiên của product {}", productId);
                        
                        // Sync mainImageUrl
                        product.setMainImageUrl(firstImage.getImageUrl());
                        productRepository.save(product);
                        syncedCount++;
                    }
                } else {
                    // Đã có ảnh primary, sync mainImageUrl
                    ProductImage primaryImage = primaryImageOpt.get();
                    String primaryUrl = primaryImage.getImageUrl();
                    
                    // Chỉ update nếu khác nhau hoặc null
                    if (product.getMainImageUrl() == null || 
                        !product.getMainImageUrl().equals(primaryUrl)) {
                        product.setMainImageUrl(primaryUrl);
                        productRepository.save(product);
                        syncedCount++;
                    }
                }
            } catch (Exception e) {
                log.warn("⚠️ Lỗi khi sync product {}: {}", product.getId(), e.getMessage());
            }
        }
        
        log.info("✅ Hoàn thành sync: {} sản phẩm đã được sync, {} ảnh được set primary", 
                syncedCount, setPrimaryCount);
        return syncedCount;
    }

    // ==========================================================
    // [10] MAPPING ENTITY ↔ DTO
    // ==========================================================
    private ProductImageDto convertToDto(ProductImage image) {
        String imageUrl = image.getImageUrl();
        
        // Generate optimized URLs nếu là Cloudinary URL
        String thumbnailUrl = null;
        String mediumUrl = null;
        String largeUrl = null;
        
        if (cloudinaryUtil != null && cloudinaryUtil.isCloudinaryUrl(imageUrl)) {
            thumbnailUrl = cloudinaryUtil.generateThumbnailUrl(imageUrl);
            mediumUrl = cloudinaryUtil.generateMediumUrl(imageUrl);
            largeUrl = cloudinaryUtil.generateLargeUrl(imageUrl);
        }
        
        return ProductImageDto.builder()
                .id(image.getId())
                .productId(image.getProduct() != null ? image.getProduct().getId() : null)
                .imageUrl(imageUrl)
                .cloudinaryPublicId(image.getCloudinaryPublicId())
                .altText(image.getAltText())
//                .isPrimary(image.getIsPrimary())
                .displayOrder(image.getDisplayOrder())
                .thumbnailUrl(thumbnailUrl)
                .mediumUrl(mediumUrl)
                .largeUrl(largeUrl)
                .build();
    }

    public List<ProductImagePublicDto> getAllImagesPublic() {
        Map<Long, List<String>> grouped = productImageRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getId(),
                        Collectors.mapping(
                                ProductImage::getImageUrl,
                                Collectors.toList()
                        )
                ));

        return grouped.entrySet().stream()
                .map(e -> {
                    ProductImagePublicDto dto = new ProductImagePublicDto();
                    dto.setProductId(e.getKey());
                    dto.setImages(e.getValue());
                    return dto;
                })
                .toList();
    }

}
