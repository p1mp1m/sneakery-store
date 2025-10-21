package com.sneakery.store.repository;

import com.sneakery.store.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}