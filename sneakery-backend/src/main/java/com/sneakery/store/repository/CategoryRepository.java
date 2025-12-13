package com.sneakery.store.repository;

import com.sneakery.store.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    /**
     * Count total categories (optimized aggregation query)
     */
    @Query("SELECT COUNT(c) FROM Category c")
    Long countTotalCategories();
}