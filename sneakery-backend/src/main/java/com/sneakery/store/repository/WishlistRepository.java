package com.sneakery.store.repository;

import com.sneakery.store.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository: WishlistRepository
 * Quản lý danh sách yêu thích của user
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    /**
     * Lấy tất cả wishlist items của user
     * Tối ưu: JOIN FETCH product, brand để tránh N+1 query
     */
    @Query("SELECT w FROM Wishlist w " +
           "JOIN FETCH w.product p " +
           "JOIN FETCH p.brand " +
           "WHERE w.user.id = :userId " +
           "ORDER BY w.createdAt DESC")
    List<Wishlist> findByUserIdWithDetails(@Param("userId") Long userId);

    /**
     * Kiểm tra sản phẩm đã có trong wishlist chưa
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * Tìm wishlist item theo user và product
     */
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * Xóa wishlist item theo user và product
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);

    /**
     * Đếm số lượng wishlist items của user
     */
    long countByUserId(Long userId);
}

