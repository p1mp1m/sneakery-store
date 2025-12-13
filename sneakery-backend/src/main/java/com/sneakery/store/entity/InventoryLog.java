package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 * Inventory Log Entity
 * Audit trail cho inventory changes - tracking mọi thay đổi stock
 */
@Data
@Entity
@Table(name = "Inventory_Logs")
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductVariant variant;

    @Column(name = "change_type", nullable = false)
    private String changeType; // restock, sale, return, adjustment, damaged

    @Column(name = "quantity_before", nullable = false)
    private Integer quantityBefore;

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange; // Positive or negative

    @Column(name = "quantity_after", nullable = false)
    private Integer quantityAfter;

    @Column(name = "reference_type")
    private String referenceType; // order, manual, return, etc.

    @Column(name = "reference_id")
    private Long referenceId; // order_id if applicable

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User changedBy; // Admin who made the change

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

