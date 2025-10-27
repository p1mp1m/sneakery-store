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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service: ProductImageService
 * Quản lý gallery hình ảnh sản phẩm.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    /** Lấy danh sách hình ảnh theo sản phẩm */
    @Transactional(readOnly = true)
    public List<ProductImageDto> getProductImages(Long productId) {
        log.info("Fetching images for product ID: {}", productId);

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        return images.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /** Thêm mới một ảnh */
    @Transactional
    public ProductImageDto addProductImage(Long productId, ProductImageDto dto) {
        log.info("Adding image to product ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));

        // Nếu là ảnh bìa thì bỏ cờ ảnh bìa cũ
        if (Boolean.TRUE.equals(dto.getIsPrimary())) {
            productImageRepository.findByProductIdAndIsPrimaryTrue(productId)
                    .ifPresent(img -> {
                        img.setIsPrimary(false);
                        productImageRepository.save(img);
                    });
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(dto.getImageUrl())
                .altText(dto.getAltText())
                .isPrimary(dto.getIsPrimary())
                .displayOrder(dto.getDisplayOrder())
                .build();

        image = productImageRepository.save(image);
        return convertToDto(image);
    }

    /** Cập nhật thông tin ảnh */
    @Transactional
    public ProductImageDto updateProductImage(Long imageId, ProductImageDto dto) {
        log.info("Updating image ID: {}", imageId);

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hình ảnh không tồn tại"));

        // Nếu đổi sang ảnh bìa
        if (Boolean.TRUE.equals(dto.getIsPrimary()) && !image.getIsPrimary()) {
            productImageRepository.findByProductIdAndIsPrimaryTrue(image.getProduct().getId())
                    .ifPresent(img -> {
                        img.setIsPrimary(false);
                        productImageRepository.save(img);
                    });
        }

        image.setImageUrl(dto.getImageUrl());
        image.setAltText(dto.getAltText());
        image.setIsPrimary(dto.getIsPrimary());
        image.setDisplayOrder(dto.getDisplayOrder());

        image = productImageRepository.save(image);
        return convertToDto(image);
    }

    /** Xóa ảnh đơn lẻ */
    @Transactional
    public void deleteProductImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hình ảnh không tồn tại"));
        productImageRepository.delete(image);
    }

    /** Xóa toàn bộ ảnh của sản phẩm */
    @Transactional
    public void deleteAllProductImages(Long productId) {
        productImageRepository.deleteByProductId(productId);
    }

    /** Đặt ảnh bìa */
    @Transactional
    public ProductImageDto setPrimaryImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hình ảnh không tồn tại"));

        productImageRepository.findByProductIdAndIsPrimaryTrue(image.getProduct().getId())
                .ifPresent(img -> {
                    img.setIsPrimary(false);
                    productImageRepository.save(img);
                });

        image.setIsPrimary(true);
        image = productImageRepository.save(image);
        return convertToDto(image);
    }

    /** Convert Entity → DTO */
    private ProductImageDto convertToDto(ProductImage image) {
        return ProductImageDto.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .altText(image.getAltText())
                .isPrimary(image.getIsPrimary())
                .displayOrder(image.getDisplayOrder())
                .build();
    }
}
