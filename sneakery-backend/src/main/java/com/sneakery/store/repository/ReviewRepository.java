package com.sneakery.store.repository;

import com.sneakery.store.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Lấy tất cả reviews đã được duyệt với pagination (cho testimonials)
    // JOIN FETCH để load product, brand, và user trong 1 query
    // Lưu ý: Không JOIN FETCH p.images vì nó là List và không tương thích với pagination
    // Sẽ sử dụng mainImageUrl từ Product thay vì images
    @Query(value = "SELECT DISTINCT r FROM Review r " +
           "JOIN FETCH r.user " +
           "JOIN FETCH r.product p " +
           "LEFT JOIN FETCH p.brand " +
           "WHERE r.isApproved = true AND r.deletedAt IS NULL " +
           "ORDER BY r.createdAt DESC",
           countQuery = "SELECT COUNT(DISTINCT r) FROM Review r " +
           "WHERE r.isApproved = true AND r.deletedAt IS NULL")
    Page<Review> findAllApprovedReviews(Pageable pageable);
}