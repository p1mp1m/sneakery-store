package com.sneakery.store.repository;

import com.sneakery.store.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository: ProductImageRepository
 * ----------------------------------
 * Xử lý truy vấn liên quan đến hình ảnh sản phẩm (gallery).
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * Lấy danh sách ảnh theo Product ID (sắp xếp theo displayOrder tăng dần)
     */
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    /**
     * Tìm ảnh chính (primary) của một sản phẩm
     */
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);

    /**
     * Tìm ảnh theo Product ID và đường dẫn ảnh
     */
    Optional<ProductImage> findByProductIdAndImageUrl(Long productId, String imageUrl);

    /**
     * Kiểm tra xem sản phẩm đã có ảnh primary chưa
     */
    boolean existsByProductIdAndIsPrimaryTrue(Long productId);

    /**
     * Đếm số ảnh của một sản phẩm
     */
    long countByProductId(Long productId);

    /**
     * Xóa toàn bộ ảnh của sản phẩm
     */
    void deleteByProductId(Long productId);

    /**
     * Gỡ bỏ cờ primary khỏi toàn bộ ảnh thuộc sản phẩm (khi set ảnh mới làm primary)
     */
    @Modifying
    @Query("UPDATE ProductImage i SET i.isPrimary = false WHERE i.product.id = :productId")
    void clearPrimaryForProduct(@Param("productId") Long productId);
}
