package com.sneakery.store.repository;

import com.sneakery.store.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // --- Các hàm cho User ---
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails " +
            "WHERE o.user.id = :userId " +
            "ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.variant v " +
            "LEFT JOIN FETCH v.product p " +
            "LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH o.addressShipping " +
            "LEFT JOIN FETCH o.payments " +
            "WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserIdWithDetails(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );

    // --- Các hàm cho Admin ---
    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.user",
            countQuery = "SELECT count(o) FROM Order o")
    Page<Order> findAllWithUser(Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.user " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.variant v " +
            "LEFT JOIN FETCH v.product p " +
            "LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH o.addressShipping " +
            "LEFT JOIN FETCH o.addressBilling " +
            "LEFT JOIN FETCH o.payments " +
            "LEFT JOIN FETCH o.statusHistories " +
            "WHERE o.id = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

    /**
     * SỬA LỖI: Thêm hàm kiểm tra user đã mua sản phẩm chưa
     * (Hàm này đang bị thiếu trong file của bạn)
     */
    @Query("SELECT COUNT(o) FROM Order o " +
            "JOIN o.orderDetails od " +
            "JOIN od.variant v " +
            "WHERE o.user.id = :userId " +
            "AND v.product.id = :productId " +
            "AND o.status = 'Completed'") // Chỉ tính đơn đã hoàn thành
    long countCompletedOrdersByUserIdAndProductId(
            @Param("userId") Long userId,
            @Param("productId") Long productId
    );
}