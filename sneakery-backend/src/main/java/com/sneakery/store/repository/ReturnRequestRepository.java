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
    @Query("SELECT rr FROM ReturnRequest rr " +
           "JOIN FETCH rr.order o " +
           "JOIN FETCH rr.user u " +
           "LEFT JOIN FETCH rr.approvedBy " +
           "WHERE rr.id = :id")
    Optional<ReturnRequest> findByIdWithDetails(@Param("id") Long id);

    /**
     * Lấy tất cả return requests với filter (admin)
     */
    @Query(value = "SELECT DISTINCT rr FROM ReturnRequest rr " +
           "JOIN FETCH rr.order o " +
           "JOIN FETCH rr.user u " +
           "WHERE (:status IS NULL OR :status = '' OR rr.status = :status) " +
           "AND (:search IS NULL OR :search = '' OR " +
           "CAST(rr.id AS string) LIKE %:search% OR " +
           "CAST(o.id AS string) LIKE %:search% OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))",
           countQuery = "SELECT count(DISTINCT rr) FROM ReturnRequest rr " +
           "JOIN rr.order o " +
           "JOIN rr.user u " +
           "WHERE (:status IS NULL OR :status = '' OR rr.status = :status) " +
           "AND (:search IS NULL OR :search = '' OR " +
           "CAST(rr.id AS string) LIKE %:search% OR " +
           "CAST(o.id AS string) LIKE %:search% OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ReturnRequest> findAllWithFilters(
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable
    );

    /**
     * Kiểm tra order đã có return request chưa
     */
    boolean existsByOrderId(Long orderId);

    /**
     * Đếm số return requests pending (admin dashboard)
     */
    long countByStatus(String status);
}

