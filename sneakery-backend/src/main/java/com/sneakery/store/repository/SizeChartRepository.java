package com.sneakery.store.repository;

import com.sneakery.store.entity.SizeChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository: SizeChartRepository
 * Quản lý bảng size giày
 */
@Repository
public interface SizeChartRepository extends JpaRepository<SizeChart, Integer> {

    /**
     * Lấy size chart theo brand và category
     */
    List<SizeChart> findByBrandIdAndCategory(Integer brandId, String category);

    /**
     * Lấy size chart theo brand
     */
    @Query("SELECT sc FROM SizeChart sc " +
           "JOIN FETCH sc.brand " +
           "WHERE sc.brand.id = :brandId " +
           "ORDER BY sc.category, CAST(sc.size AS int)")
    List<SizeChart> findByBrandIdWithDetails(@Param("brandId") Integer brandId);

    /**
     * Tìm size chart cụ thể
     */
    Optional<SizeChart> findByBrandIdAndCategoryAndSize(Integer brandId, String category, String size);

    /**
     * Lấy tất cả categories cho brand
     */
    @Query("SELECT DISTINCT sc.category FROM SizeChart sc WHERE sc.brand.id = :brandId")
    List<String> findDistinctCategoriesByBrandId(@Param("brandId") Integer brandId);
}

