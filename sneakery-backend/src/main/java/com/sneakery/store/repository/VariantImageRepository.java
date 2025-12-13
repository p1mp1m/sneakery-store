package com.sneakery.store.repository;

import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.entity.VariantImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantImageRepository extends JpaRepository<VariantImage, Long> {

    List<VariantImage> findByVariantOrderByDisplayOrderAsc(ProductVariant variant);

    void deleteByVariant(ProductVariant variant);

}
