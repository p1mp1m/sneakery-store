package com.sneakery.store.repository;

import com.sneakery.store.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // Tối ưu: Tìm Variant VÀ tải luôn Product + Brand
    @Query("SELECT v FROM ProductVariant v " +
            "LEFT JOIN FETCH v.product p " +
            "LEFT JOIN FETCH p.brand " +
            "WHERE v.id = :variantId")
    Optional<ProductVariant> findByIdWithDetails(Long variantId);

    // Load tất cả variants với Product và Brand - sử dụng EntityGraph
    @EntityGraph("ProductVariant.withProductAndBrand")
    @NonNull
    Page<ProductVariant> findAll(@NonNull Pageable pageable);
}