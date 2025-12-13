package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity: Warranty - Yêu cầu bảo hành
 * Lưu thông tin yêu cầu bảo hành sản phẩm
 */
@Data
@Entity
@Table(name = "Warranties")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    // Quan hệ với User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    // Quan hệ với Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    // Quan hệ với ProductVariant (nullable - có thể bảo hành cho toàn bộ sản phẩm)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductVariant variant;

    @Column(name = "issue_description", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String issueDescription;

    @Column(name = "status", length = 20)
    private String status; // pending, approved, rejected, processing, completed

    @Column(name = "warranty_type", length = 50)
    private String warrantyType; // repair, replace

    @Column(name = "warranty_months")
    private Integer warrantyMonths; // Số tháng bảo hành (default 12)

    @Column(name = "images_json", columnDefinition = "NVARCHAR(MAX)")
    private String imagesJson; // JSON array of image URLs

    @Column(name = "admin_note", columnDefinition = "NVARCHAR(MAX)")
    private String adminNote;

    @Column(name = "resolution_note", columnDefinition = "NVARCHAR(MAX)")
    private String resolutionNote;

    // Admin user xử lý warranty
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (submittedAt == null) {
            submittedAt = now;
        }
        if (status == null) {
            status = "pending";
        }
        if (warrantyMonths == null) {
            warrantyMonths = 12; // Default 12 months
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

