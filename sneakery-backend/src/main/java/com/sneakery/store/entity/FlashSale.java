package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity: FlashSale - Chương trình Flash Sale
 * Giảm giá sản phẩm theo thời gian có giới hạn
 */
@Data
@Entity
@Table(name = "Flash_Sales")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quan hệ với Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent; // Phần trăm giảm giá (0-100)

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "quantity_limit")
    private Integer quantityLimit; // Giới hạn số lượng (null = không giới hạn)

    @Column(name = "sold_count")
    private Integer soldCount;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (soldCount == null) {
            soldCount = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
    }

    /**
     * Kiểm tra Flash Sale có đang active không
     */
    public boolean isCurrentlyActive() {
        if (!isActive) return false;
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

    /**
     * Kiểm tra còn slot không (nếu có giới hạn)
     */
    public boolean hasAvailableSlots() {
        if (quantityLimit == null) return true; // Không giới hạn
        return soldCount < quantityLimit;
    }
}

