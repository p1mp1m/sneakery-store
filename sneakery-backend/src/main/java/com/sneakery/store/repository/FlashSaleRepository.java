package com.sneakery.store.repository;

import com.sneakery.store.entity.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository: FlashSaleRepository
 * Quản lý chương trình Flash Sale
 */
@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Integer> {

    /**
     * Lấy tất cả flash sales đang active
     */
    @Query("SELECT fs FROM FlashSale fs " +
           "JOIN FETCH fs.product p " +
           "JOIN FETCH p.brand " +
           "WHERE (fs.isActive IS NULL OR fs.isActive = true) " +
           "AND (p.isActive IS NULL OR p.isActive = true) " +
           "AND p.deletedAt IS NULL " +
           "AND fs.startTime <= :now " +
           "AND fs.endTime > :now " +
           "ORDER BY fs.endTime ASC")
    List<FlashSale> findActiveFlashSales(@Param("now") LocalDateTime now);

    /**
     * Tìm flash sale đang active cho sản phẩm cụ thể
     */
    @Query("SELECT fs FROM FlashSale fs " +
           "WHERE fs.product.id = :productId " +
           "AND fs.isActive = true " +
           "AND fs.startTime <= :now " +
           "AND fs.endTime > :now")
    Optional<FlashSale> findActiveFlashSaleByProductId(
            @Param("productId") Long productId,
            @Param("now") LocalDateTime now
    );

    /**
     * Lấy tất cả flash sales (admin)
     */
    @Query("SELECT DISTINCT fs FROM FlashSale fs " +
           "JOIN FETCH fs.product p " +
           "JOIN FETCH p.brand " +
           "LEFT JOIN FETCH p.variants " +
           "ORDER BY fs.startTime DESC")
    List<FlashSale> findAllWithProduct();

    /**
     * Lấy flash sales sắp diễn ra (upcoming)
     */
    @Query("SELECT fs FROM FlashSale fs " +
           "JOIN FETCH fs.product p " +
           "JOIN FETCH p.brand " +
           "WHERE fs.isActive = true " +
           "AND fs.startTime > :now " +
           "ORDER BY fs.startTime ASC")
    List<FlashSale> findUpcomingFlashSales(@Param("now") LocalDateTime now);
    
    /**
     * Tìm flash sales đã hết hạn nhưng vẫn active (để auto deactivate)
     */
    List<FlashSale> findByIsActiveTrueAndEndTimeLessThan(LocalDateTime now);
}

