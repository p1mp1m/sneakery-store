package com.sneakery.store.repository;

import com.sneakery.store.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    
    /**
     * Count total brands (optimized aggregation query)
     */
    @Query("SELECT COUNT(b) FROM Brand b")
    Long countTotalBrands();
}