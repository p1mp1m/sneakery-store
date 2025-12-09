package com.sneakery.store.repository;

import com.sneakery.store.entity.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository: ReturnRequestRepository
 * Quản lý yêu cầu đổi trả hàng
 */
@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {

    /**
     * Lấy tất cả return requests của user
     */
    @Query("SELECT rr FROM ReturnRequest rr " +
           "JOIN FETCH rr.order " +
           "WHERE rr.user.id = :userId " +
           "ORDER BY rr.createdAt DESC")
    List<ReturnRequest> findByUserIdWithOrder(@Param("userId") Long userId);

    /**
     * Lấy return request theo ID với details (cho admin)
     */
//    @Query("SELECT rr FROM ReturnRequest rr " +
//           "JOIN FETCH rr.order o " +
//           "JOIN FETCH rr.user u " +
//           "LEFT JOIN FETCH rr.approvedBy " +
//            "LEFT JOIN FETCH o.orderDetails d " +
//            "LEFT JOIN FETCH d.variant v " +
//            "LEFT JOIN FETCH v.product p " +
//           "WHERE rr.id = :id")
//    Optional<ReturnRequest> findByIdWithDetails(@Param("id") Long id);
    @Query("""
    SELECT rr FROM ReturnRequest rr
    JOIN FETCH rr.order o
    JOIN FETCH rr.user u
    LEFT JOIN FETCH rr.approvedBy
    LEFT JOIN FETCH o.orderDetails d
    LEFT JOIN FETCH d.variant v
    LEFT JOIN FETCH v.product p
    WHERE rr.id = :id
""")
    Optional<ReturnRequest> findByIdWithDetails(@Param("id") Long id);

    /**
     * Lấy tất cả return requests với filter (admin)
     */
    @Query(value = """
    SELECT rr FROM ReturnRequest rr
    JOIN rr.order o
    JOIN rr.user u
    WHERE (:status IS NULL OR :status = '' OR rr.status = :status)
      AND (:search IS NULL OR :search = '' OR 
           CAST(rr.id AS string) LIKE %:search% OR
           CAST(o.id AS string) LIKE %:search% OR
           LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:reason IS NULL OR :reason = '' OR rr.reason LIKE CONCAT(:reason, '%'))
""",
            countQuery = """
    SELECT count(rr) FROM ReturnRequest rr
    JOIN rr.order o
    JOIN rr.user u
    WHERE (:status IS NULL OR :status = '' OR rr.status = :status)
      AND (:search IS NULL OR :search = '' OR 
           CAST(rr.id AS string) LIKE %:search% OR
           CAST(o.id AS string) LIKE %:search% OR
           LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
      AND (:reason IS NULL OR :reason = '' OR rr.reason LIKE CONCAT(:reason, '%'))
""")
    Page<ReturnRequest> findAllWithFilters(
            @Param("status") String status,
            @Param("search") String search,
            @Param("reason") String reason,
            Pageable pageable
    );

    /**
     * Kiểm tra order đã có return request chưa
     */
    boolean existsByOrderId(Long orderId);

    /**
     * Lấy return request theo orderId (cho user)
     */
    @Query("SELECT rr FROM ReturnRequest rr " +
           "JOIN FETCH rr.order o " +
           "JOIN FETCH rr.user u " +
           "LEFT JOIN FETCH rr.approvedBy " +
           "WHERE o.id = :orderId")
    Optional<ReturnRequest> findByOrderIdWithDetails(@Param("orderId") Long orderId);

    /**
     * Đếm số return requests pending (admin dashboard)
     */
    long countByStatus(String status);
}

