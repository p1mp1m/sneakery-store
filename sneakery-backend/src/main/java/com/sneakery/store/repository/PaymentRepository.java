package com.sneakery.store.repository;

import com.sneakery.store.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Tính tổng doanh thu dựa trên các thanh toán đã 'completed'.
     * (Hàm này chúng ta đã dùng cho AdminController)
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(String status);
}