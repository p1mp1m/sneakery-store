package com.sneakery.store.repository;

import com.sneakery.store.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository: ProductImageRepository
 * Quản lý hình ảnh sản phẩm
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * Lấy tất cả hình ảnh của sản phẩm
     * Sắp xếp theo display_order
     */
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    /**
     * Tìm hình ảnh chính của sản phẩm
     */
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);

    /**
     * Xóa tất cả hình ảnh của sản phẩm
     */
    void deleteByProductId(Long productId);

    /**
     * Đếm số lượng hình ảnh của sản phẩm
     */
    long countByProductId(Long productId);
}

