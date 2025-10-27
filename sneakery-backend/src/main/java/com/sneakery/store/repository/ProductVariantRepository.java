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
            "WHERE v.id = :variantId")
    Optional<ProductVariant> findByIdWithDetails(Long variantId);

    @EntityGraph("ProductVariant.withProductAndBrand")
    @NonNull
    Page<ProductVariant> findAll(@NonNull Pageable pageable);

    @EntityGraph("ProductVariant.withProductAndBrand")
    @Query("SELECT v FROM ProductVariant v " +
            "LEFT JOIN v.product p " +
            "WHERE (:search IS NULL OR " +
            "   LOWER(v.sku) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(v.color) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(v.size) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "   LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:color IS NULL OR LOWER(v.color) = LOWER(:color)) " +
            "AND (:size IS NULL OR LOWER(v.size) = LOWER(:size)) " +
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
}