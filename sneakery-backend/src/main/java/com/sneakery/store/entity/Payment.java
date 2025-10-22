package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nhiều Payment thuộc về 1 Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // cod, vnpay, momo, zalopay, bank_transfer, credit_card

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status")
    private String status; // pending, processing, completed, failed, refunded

    // Payment gateway details
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // V3.1 - refunded_at
    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}