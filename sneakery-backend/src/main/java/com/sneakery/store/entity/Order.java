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

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "status")
    private String status; // Pending, Processing, Paid, Shipped, ...

    @Column(name = "created_at")
    private LocalDateTime createdAt;

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