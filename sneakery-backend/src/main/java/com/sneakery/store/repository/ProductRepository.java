package com.sneakery.store.repository;

import com.sneakery.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Lấy tất cả sản phẩm và JOIN FETCH các biến thể liên quan.
     * Điều này giúp giải quyết lỗi LazyInitializationException và tối ưu hiệu năng.
     * "LEFT JOIN" đảm bảo sản phẩm vẫn được lấy ngay cả khi nó chưa có biến thể nào.
     * "DISTINCT" để tránh trả về các sản phẩm bị trùng lặp.
     */


    @Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN FETCH p.brand
    LEFT JOIN FETCH p.categories
    WHERE p.deletedAt IS NULL
    """)
List<Product> findAllWithBrandAndCategories();

@Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN FETCH p.brand
    LEFT JOIN FETCH p.categories
    WHERE p.id IN :ids
    AND p.deletedAt IS NULL
""")
List<Product> findByIdInWithBrandAndCategories(@Param("ids") List<Long> ids);

//✅ Cái đầu dùng cho lấy toàn bộ (không phân trang).
//✅ Cái thứ hai dùng khi bạn có Pageable.


    @Query("SELECT COALESCE(MAX(p.id), 0) FROM Product p")
    Long findMaxId();
    
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.deletedAt IS NULL")
    List<Product> findAllWithVariants();

    // Thay đổi phương thức để nhận Pageable và trả về Page
    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.deletedAt IS NULL",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p WHERE p.deletedAt IS NULL")
    Page<Product> findAllWithVariants(Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH p.categories " +
            "LEFT JOIN FETCH p.variants " +
            "WHERE p.deletedAt IS NULL AND (p.isActive = true OR p.isActive IS NULL)",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p WHERE p.deletedAt IS NULL AND (p.isActive = true OR p.isActive IS NULL)")
    Page<Product> findAllWithDetails(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH p.categories " +
            "LEFT JOIN FETCH p.variants " +
            "WHERE p.id = :productId " +
            "AND p.deletedAt IS NULL")
    Optional<Product> findByIdWithDetails(@Param("productId") Long productId);

    /**
     * Find product by slug (for validation) - excludes soft deleted
     */
    @Query("SELECT p FROM Product p WHERE p.slug = :slug AND p.deletedAt IS NULL")
    Optional<Product> findBySlug(@Param("slug") String slug);

    /**
     * Count active products (optimized with aggregation) - excludes soft deleted
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = true AND p.deletedAt IS NULL")
    Long countActiveProducts();

    /**
     * Count inactive products (optimized with aggregation) - excludes soft deleted
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE (p.isActive = false OR p.isActive IS NULL) AND p.deletedAt IS NULL")
    Long countInactiveProducts();

    /**
     * Count products by brand ID (for validation before deleting brand)
     * Excludes soft deleted products
     */
    Optional<Product> findBySlugAndIsActiveTrueAndDeletedAtIsNull(String slug);

    // Lấy sản phẩm theo brand (trừ sản phẩm hiện tại)
    List<Product> findTop4ByBrandIdAndIdNotAndIsActiveTrueAndDeletedAtIsNull(Long brandId, Long excludeId);

    // Lấy sản phẩm theo category
    @Query("""
    SELECT DISTINCT p FROM Product p
    JOIN p.categories c
    WHERE c.id IN :categoryIds
      AND p.id <> :excludeId
      AND p.isActive = true
      AND p.deletedAt IS NULL
""")
    List<Product> findByCategories(Long excludeId, List<Long> categoryIds);

    @Query("""
SELECT p FROM Product p
WHERE p.isActive = true
AND p.deletedAt IS NULL
AND (:brand IS NULL OR LOWER(p.brand.name) = LOWER(:brand))
AND (
    :category IS NULL OR EXISTS (
        SELECT c FROM p.categories c
        WHERE LOWER(c.name) = LOWER(:category)
    )
)
""")
    Page<Product> searchAdvanced(
            @Param("brand") String brand,
            @Param("category") String category,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId AND p.deletedAt IS NULL")
    Long countByBrandId(@Param("brandId") Integer brandId);

    @Query("""
    SELECT MAX(CAST(SUBSTRING(p.code, LOCATE('-', p.code) + 1) AS int))
    FROM Product p
    WHERE p.brand.id = :brandId
""")
    Integer findLastNumberByBrand(@Param("brandId") Integer brandId);

    @Query("""
SELECT p FROM Product p
WHERE p.isActive = true
  AND p.deletedAt IS NULL
  AND (
        :search IS NULL
        OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(p.slug) LIKE LOWER(CONCAT('%', :search, '%'))
        OR EXISTS (
            SELECT v FROM ProductVariant v
            WHERE v.product = p
              AND LOWER(v.sku) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    )
ORDER BY p.id DESC
""")
    Page<Product> searchProducts(
            @Param("search") String search,
            Pageable pageable);
}