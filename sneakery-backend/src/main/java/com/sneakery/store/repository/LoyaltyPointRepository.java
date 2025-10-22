package com.sneakery.store.repository;

import com.sneakery.store.entity.LoyaltyPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository: LoyaltyPointRepository
 * Quản lý điểm tích lũy
 */
@Repository
public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {

    /**
     * Lấy lịch sử điểm của user
     */
    List<LoyaltyPoint> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Tính tổng điểm hiện tại của user (chưa hết hạn)
     */
    @Query("SELECT COALESCE(SUM(lp.points), 0) FROM LoyaltyPoint lp " +
           "WHERE lp.user.id = :userId " +
           "AND (lp.expiresAt IS NULL OR lp.expiresAt > :now)")
    int calculateCurrentPoints(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Lấy các điểm sắp hết hạn
     */
    @Query("SELECT lp FROM LoyaltyPoint lp " +
           "WHERE lp.user.id = :userId " +
           "AND lp.points > 0 " +
           "AND lp.expiresAt IS NOT NULL " +
           "AND lp.expiresAt > :now " +
           "AND lp.expiresAt <= :expiringBefore " +
           "ORDER BY lp.expiresAt ASC")
    List<LoyaltyPoint> findExpiringPoints(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            @Param("expiringBefore") LocalDateTime expiringBefore
    );

    /**
     * Lấy điểm đã hết hạn nhưng chưa xử lý
     */
    @Query("SELECT lp FROM LoyaltyPoint lp " +
           "WHERE lp.points > 0 " +
           "AND lp.expiresAt IS NOT NULL " +
           "AND lp.expiresAt <= :now " +
           "AND lp.transactionType = 'earn'")
    List<LoyaltyPoint> findExpiredPoints(@Param("now") LocalDateTime now);
}

