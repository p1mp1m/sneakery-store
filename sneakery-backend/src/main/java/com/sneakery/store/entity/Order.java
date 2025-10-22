package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với User (Nhiều Order thuộc về 1 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Tùy chọn: Quan hệ với Coupon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    // Quan hệ với Address (Địa chỉ giao hàng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_shipping_id")
    private Address addressShipping;

    // Quan hệ với Address (Địa chỉ thanh toán)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_billing_id")
    private Address addressBilling;

    // V3.1 - Order Number (QUAN TRỌNG!)
    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber; // ORD-20250122-0001

    // Pricing
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    // V3.1 - Tax amount
    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    // V3.1 - Loyalty points
    @Column(name = "points_earned")
    private Integer pointsEarned;

    @Column(name = "points_used")
    private Integer pointsUsed;

    @Column(name = "status")
    private String status; // pending, confirmed, processing, packed, shipped, delivered, cancelled, refunded

    // V3.1 - Shipping details
    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "estimated_delivery_at")
    private LocalDateTime estimatedDeliveryAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    // Notes
    @Column(name = "customer_note", columnDefinition = "NVARCHAR(MAX)")
    private String customerNote;

    @Column(name = "admin_note", columnDefinition = "NVARCHAR(MAX)")
    private String adminNote;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Quan hệ với OrderDetails (Một Order có nhiều Detail)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // Quan hệ với Payment (Một Order có thể có nhiều Payment - dù thường là 1)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    // Quan hệ với OrderStatusHistory
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderStatusHistory> statusHistories = new ArrayList<>();
}