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

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.variant v " +
            "LEFT JOIN FETCH v.product p " +
            "LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH o.addressShipping " +
            "WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserIdWithDetails(
            @Param("orderId") Long orderId,
            @Param("userId") Long userId
    );

    // --- Các hàm cho Admin ---
    @Query(value = "SELECT o FROM Order o LEFT JOIN FETCH o.user",
            countQuery = "SELECT count(o) FROM Order o")
    Page<Order> findAllWithUser(Pageable pageable);

    /**
     * Tìm kiếm và lọc đơn hàng cho Admin với search và status
     * Search: tìm theo ID, tên khách hàng, email khách hàng
     * Status: lọc theo trạng thái đơn hàng
     */
    @Query(value = "SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.user u " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "CAST(o.id AS string) LIKE %:search% OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status IS NULL OR :status = '' OR o.status = :status)",
            countQuery = "SELECT count(DISTINCT o) FROM Order o " +
            "LEFT JOIN o.user u " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "CAST(o.id AS string) LIKE %:search% OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status IS NULL OR :status = '' OR o.status = :status)")
    Page<Order> findAllWithUserAndFilters(
            @Param("search") String search,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.user " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.variant v " +
            "LEFT JOIN FETCH v.product p " +
            "LEFT JOIN FETCH p.brand " +
            "LEFT JOIN FETCH o.addressShipping " +
            "LEFT JOIN FETCH o.addressBilling " +
            "WHERE o.id = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);
    
    /**
     * Load payments và statusHistories riêng để tránh MultipleBagFetchException
     * Sử dụng trong service để load các collections sau khi đã có order
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.payments " +
            "WHERE o.id = :orderId")
    Optional<Order> findByIdWithPayments(@Param("orderId") Long orderId);
    
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.statusHistories " +
            "WHERE o.id = :orderId")
    Optional<Order> findByIdWithStatusHistories(@Param("orderId") Long orderId);

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

    /**
     * Lấy POS orders (orders có orderNumber bắt đầu bằng "POS-")
     * Với pagination và filter theo date range
     * Chỉ fetch orderDetails để tránh MultipleBagFetchException
     */
    @Query(value = "SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.variant v " +
            "LEFT JOIN FETCH v.product p " +
            "LEFT JOIN FETCH p.brand " +
            "WHERE o.orderNumber LIKE 'POS-%' " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate) " +
            "ORDER BY o.createdAt DESC",
            countQuery = "SELECT count(DISTINCT o) FROM Order o " +
            "WHERE o.orderNumber LIKE 'POS-%' " +
            "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR o.createdAt <= :endDate)")
    Page<Order> findPOSOrders(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable
    );
    
    /**
     * Tối ưu: Tìm sequence number tiếp theo cho order number trong ngày
     * Chỉ query max sequence thay vì load tất cả orders
     */
    @Query(value = "SELECT ISNULL(MAX(CAST(RIGHT(order_number, 4) AS INT)), 0) + 1 " +
            "FROM Orders " +
            "WHERE order_number LIKE :prefix",
            nativeQuery = true)
    Integer getNextOrderSequence(@Param("prefix") String prefix);

    /**
     * OPTIMIZED: Lấy tất cả dashboard stats trong một query duy nhất
     * Sử dụng aggregation queries với subqueries để tính toán trực tiếp trên database
     * Giảm số lượng queries từ 4 xuống 1, cải thiện performance đáng kể
     * 
     * Note: Native query returns List<Object[]>, so we'll get the first row in the controller
     */
    @Query(value = "SELECT " +
            "   (SELECT COUNT(*) FROM Users WHERE deleted_at IS NULL) AS total_users, " +
            "   (SELECT COUNT(*) FROM Products WHERE deleted_at IS NULL) AS total_products, " +
            "   (SELECT COUNT(*) FROM Orders) AS total_orders, " +
            "   (SELECT ISNULL(SUM(CAST(amount AS FLOAT)), 0) FROM Payments WHERE status = 'completed') AS total_revenue",
            nativeQuery = true)
    List<Object[]> getDashboardStatsRaw();

    /**
     * Đếm số lượng đơn hàng theo status
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    /**
     * Lấy doanh thu theo ngày trong khoảng thời gian
     */
    @Query(value = "SELECT CAST(p.created_at AS DATE) AS date, " +
            "ISNULL(SUM(CAST(p.amount AS FLOAT)), 0) AS revenue " +
            "FROM Payments p " +
            "WHERE p.status = 'completed' " +
            "AND CAST(p.created_at AS DATE) >= :startDate " +
            "AND CAST(p.created_at AS DATE) <= :endDate " +
            "GROUP BY CAST(p.created_at AS DATE) " +
            "ORDER BY date ASC",
            nativeQuery = true)
    List<Object[]> getRevenueByDateRange(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );

    /**
     * Lấy số lượng đơn hàng theo ngày trong khoảng thời gian
     */
    @Query(value = "SELECT CAST(created_at AS DATE) AS date, COUNT(*) AS order_count " +
            "FROM Orders " +
            "WHERE CAST(created_at AS DATE) >= :startDate " +
            "AND CAST(created_at AS DATE) <= :endDate " +
            "GROUP BY CAST(created_at AS DATE) " +
            "ORDER BY date ASC",
            nativeQuery = true)
    List<Object[]> getOrderCountByDateRange(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );

    /**
     * Lấy các đơn hàng gần đây để hiển thị trong dashboard
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.user " +
            "ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);

    /**
     * Đếm số lượng orders trong khoảng thời gian
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    long countByCreatedAtBetween(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );
}