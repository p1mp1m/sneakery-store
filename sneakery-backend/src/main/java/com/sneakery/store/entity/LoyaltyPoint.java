package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity: LoyaltyPoint - Điểm tích lũy
 * Lưu lịch sử tích điểm và sử dụng điểm của user
 */
@Data
@Entity
@Table(name = "Loyalty_Points")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
    name = "LoyaltyPoint.withUser",
    attributeNodes = @NamedAttributeNode("user")
)
public class LoyaltyPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "points", nullable = false)
    private Integer points; // Số điểm (âm nếu redeem)

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType; // earn, redeem, expire

    // Quan hệ với Order khi earn điểm (từ đơn hàng) - optional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "earned_from_order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order earnedFromOrder;

    // Quan hệ với Order khi redeem điểm (dùng điểm) - optional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redeemed_in_order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order redeemedInOrder;

    @Column(name = "description")
    private String description;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra điểm có hết hạn không
     */
    public boolean isExpired() {
        if (expiresAt == null) return false;
        return LocalDateTime.now().isAfter(expiresAt);
    }
}

