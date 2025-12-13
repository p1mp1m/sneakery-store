package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "Coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // CSDL của bạn dùng 'int'

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_type")
    private String discountType; // 'percent' hoặc 'fixed'

    @Column(name = "discount_value", nullable = false)
    private BigDecimal value;

    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "uses_count")
    private Integer usesCount;

    @Column(name = "max_uses_per_user")
    private Integer maxUsesPerUser;

    @Column(name = "is_active")
    private Boolean isActive;

    // V3.1 - Applicability fields
    @Column(name = "applicable_to")
    private String applicableTo; // all, brand, category, product

    @Column(name = "applicable_id")
    private Integer applicableId; // brand_id, category_id, or product_id

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ ngược lại: Một Coupon có thể được dùng cho nhiều Order
    @OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY)
    @JsonIgnore // Tránh vòng lặp JSON
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Order> orders;
}