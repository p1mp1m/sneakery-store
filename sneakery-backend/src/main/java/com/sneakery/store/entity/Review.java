package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nhiều Review cho 1 Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    // Nhiều Review từ 1 User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "rating")
    private Integer rating; // 1 đến 5

    @Column(name = "body")
    private String body; // Nội dung review

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}