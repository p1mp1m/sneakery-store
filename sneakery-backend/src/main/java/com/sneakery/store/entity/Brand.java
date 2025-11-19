package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity đại diện cho thương hiệu (Brand).
 *
 * <p>Quản lý đầy đủ các thông tin: logo, website, mô tả, trạng thái,
 * ngày tạo, cập nhật, xóa mềm.</p>
 */
@Entity
@Table(name = "Brands")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    // === V3.1 Fields ===
    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft delete

    /**
     * Quan hệ 1-n với Product.
     * Tránh vòng lặp JSON (Product -> Brand -> Product...).
     */
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Product> products;

    // =====================
    //   Lifecycle Hooks
    // =====================

    /**
     * Tự động set giá trị mặc định khi tạo mới.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    /**
     * Tự động update timestamp khi cập nhật.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
