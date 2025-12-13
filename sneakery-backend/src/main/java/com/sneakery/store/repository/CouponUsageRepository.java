package com.sneakery.store.repository;

import com.sneakery.store.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

  // Đếm số lần user đã dùng coupon này
  long countByCoupon_IdAndUserId(Integer couponId, Long userId);

  // Chống ghi trùng khi cùng 1 order retry
  boolean existsByOrderId(Long orderId);
}