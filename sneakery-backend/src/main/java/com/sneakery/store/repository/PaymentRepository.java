package com.sneakery.store.repository;

import com.sneakery.store.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") String status);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(String status);

    /**
     * Tìm payments theo status với eager load Order
     */
    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT p FROM Payment p WHERE p.status = :status")
    Page<Payment> findByStatus(@Param("status") String status, Pageable pageable);

    /**
     * Tìm payments theo payment method với eager load Order
     */
    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod")
    Page<Payment> findByPaymentMethod(@Param("paymentMethod") String paymentMethod, Pageable pageable);

    /**
     * Tìm payments theo status và payment method với eager load Order
     */
    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.paymentMethod = :paymentMethod")
    Page<Payment> findByStatusAndPaymentMethod(@Param("status") String status, @Param("paymentMethod") String paymentMethod, Pageable pageable);
    
    /**
     * Tìm payment theo ID với eager load Order
     */
    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    java.util.Optional<Payment> findByIdWithOrder(@Param("id") Long id);
    
    /**
     * Lấy tất cả payments với eager load Order
     * Lưu ý: Không có ORDER BY trong query vì Pageable đã có Sort
     */
    @EntityGraph(attributePaths = {"order"})
    @Query("SELECT p FROM Payment p")
    Page<Payment> findAllWithOrder(Pageable pageable);

    /**
     * Tính tổng doanh thu từ payments completed trong khoảng thời gian
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByStatusAndDateRange(
            @Param("status") String status,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate
    );
}