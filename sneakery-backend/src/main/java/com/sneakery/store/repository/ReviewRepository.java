package com.sneakery.store.repository;

import com.sneakery.store.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    // Lấy tất cả review của 1 sản phẩm (chỉ lấy các review đã được duyệt)
    // và tải luôn thông tin User để lấy tên
    @Query("SELECT r FROM Review r JOIN FETCH r.user " +
           "WHERE r.product.id = :productId AND r.isApproved = true " +
           "ORDER BY r.createdAt DESC")
    List<Review> findByProductIdAndIsApprovedTrue(Long productId);

    // Kiểm tra xem user đã review sản phẩm này chưa
    boolean existsByProductIdAndUserId(Long productId, Long userId);
}