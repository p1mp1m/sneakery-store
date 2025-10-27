package com.sneakery.store.repository;

import com.sneakery.store.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
    BigDecimal sumAmountByStatus(String status);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(String status);

    @Query("SELECT p FROM Payment p WHERE p.status = :status")
    Page<Payment> findByStatus(String status, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod")
    Page<Payment> findByPaymentMethod(String paymentMethod, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.paymentMethod = :paymentMethod")
    Page<Payment> findByStatusAndPaymentMethod(String status, String paymentMethod, Pageable pageable);
}