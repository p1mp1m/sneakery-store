package com.sneakery.store.repository;

import com.sneakery.store.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("SELECT v FROM ProductVariant v " +
            "LEFT JOIN FETCH v.product p " +
            "LEFT JOIN FETCH p.brand " +
            "WHERE v.id = :variantId " +
            "AND v.deletedAt IS NULL " +
            "AND p.deletedAt IS NULL")
    Optional<ProductVariant> findByIdWithDetails(Long variantId);

    @EntityGraph("ProductVariant.withProductAndBrand")
    @NonNull
    Page<ProductVariant> findAll(@NonNull Pageable pageable);

    @EntityGraph("ProductVariant.withProductAndBrand")
    @Query("SELECT v FROM ProductVariant v " +
            "LEFT JOIN v.product p " +
            "WHERE v.deletedAt IS NULL " +
            "AND p.deletedAt IS NULL " +
            "AND (:search IS NULL OR :search = '' OR " +
            "   LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:color IS NULL OR :color = '' OR LOWER(v.color) = LOWER(:color)) " +
            "AND (:size IS NULL OR :size = '' OR " +
            "   LOWER(v.size) = LOWER(:size) OR " +
            "   LOWER(v.size) LIKE LOWER(CONCAT(:size, ', %')) OR " +
            "   LOWER(v.size) LIKE LOWER(CONCAT('%, ', :size, ', %')) OR " +
            "   LOWER(v.size) LIKE LOWER(CONCAT('%, ', :size)) OR " +
            "   LOWER(v.size) LIKE LOWER(CONCAT(:size, ',')) OR " +
            "   LOWER(v.size) LIKE LOWER(CONCAT(',', :size, ',')) OR " +
            "   LOWER(v.size) LIKE LOWER(CONCAT(',', :size))) " +
            "AND (:productId IS NULL OR v.product.id = :productId) " +
            "AND (:stockStatus IS NULL OR :stockStatus = '' OR " +
            "   (:stockStatus = 'out_of_stock' AND v.stockQuantity = 0) OR " +
            "   (:stockStatus = 'low_stock' AND v.stockQuantity > 0 AND v.stockQuantity <= 10) OR " +
            "   (:stockStatus = 'in_stock' AND v.stockQuantity > 10))")
    Page<ProductVariant> findWithFilters(
            @Param("search") String search,
            @Param("color") String color,
            @Param("size") String size,
            @Param("productId") Long productId,
            @Param("stockStatus") String stockStatus,
            Pageable pageable
    );
    @Query("SELECT v FROM ProductVariant v WHERE v.sku = :sku AND v.deletedAt IS NULL")
    Optional<ProductVariant> findBySku(@Param("sku") String sku);

    // ✅ Có thể thêm nếu chưa có
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM ProductVariant v WHERE v.sku = :sku AND v.deletedAt IS NULL")
    boolean existsBySku(@Param("sku") String sku);

    /**
     * Sum total stock quantity (optimized aggregation query) - excludes soft deleted
     */
    @Query("SELECT COALESCE(SUM(v.stockQuantity), 0) FROM ProductVariant v WHERE v.stockQuantity IS NOT NULL AND v.deletedAt IS NULL")
    Long sumTotalStockQuantity();

    /**
     * Count variants with low stock (quantity > 0 and <= threshold) - excludes soft deleted
     */
    @Query("SELECT COUNT(v) FROM ProductVariant v WHERE v.stockQuantity > 0 AND v.stockQuantity <= :threshold AND v.deletedAt IS NULL")
    Long countLowStockVariants(@Param("threshold") Integer threshold);

    /**
     * Count variants with out of stock (quantity = 0) - excludes soft deleted
     */
    @Query("SELECT COUNT(v) FROM ProductVariant v WHERE (v.stockQuantity = 0 OR v.stockQuantity IS NULL) AND v.deletedAt IS NULL")
    Long countOutOfStockVariants();

    /**
     * Calculate average price (using priceSale if available, else priceBase) - excludes soft deleted
     */
    @Query("SELECT COALESCE(AVG(CASE WHEN v.priceSale IS NOT NULL AND v.priceSale > 0 THEN v.priceSale ELSE v.priceBase END), 0) " +
           "FROM ProductVariant v WHERE v.priceBase IS NOT NULL AND v.deletedAt IS NULL")
    java.math.BigDecimal calculateAveragePrice();

    /**
     * Get maximum price (using priceSale if available, else priceBase) - excludes soft deleted
     */
    @Query("SELECT COALESCE(MAX(CASE WHEN v.priceSale IS NOT NULL AND v.priceSale > 0 THEN v.priceSale ELSE v.priceBase END), 0) " +
           "FROM ProductVariant v WHERE v.priceBase IS NOT NULL AND v.deletedAt IS NULL")
    java.math.BigDecimal getMaxPrice();

    /**
     * Get minimum price (using priceSale if available, else priceBase) - excludes soft deleted
     */
    @Query("SELECT COALESCE(MIN(CASE WHEN v.priceSale IS NOT NULL AND v.priceSale > 0 THEN v.priceSale ELSE v.priceBase END), 0) " +
           "FROM ProductVariant v WHERE v.priceBase IS NOT NULL AND v.deletedAt IS NULL")
    java.math.BigDecimal getMinPrice();

    boolean existsBySkuAndProductIdNot(String sku, Long productId);

}