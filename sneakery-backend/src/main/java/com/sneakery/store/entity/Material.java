package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity đại diện cho Chất liệu (Material)
 * Dùng để quản lý danh sách các chất liệu sản xuất giày, ví dụ: Da, Vải, Lưới, Nhựa,...
 */
@Entity
@Table(name = "Materials")
@Data
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Khóa chính

    @Column(name = "name", nullable = false, unique = true)
    private String name; // Tên chất liệu, ví dụ: "Da", "Vải Canvas"

    @Column(name = "slug", nullable = false, unique = true)
    private String slug; // Slug SEO-friendly, ví dụ: "da", "vai-canvas"

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description; // Mô tả chi tiết chất liệu

    @Column(name = "is_active")
    private Boolean isActive; // Trạng thái hoạt động

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Ngày tạo

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // Ngày cập nhật

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Ngày xóa (mềm)

    /**
     * Quan hệ 1-nhiều với Product.
     * Một chất liệu có thể được dùng trong nhiều sản phẩm.
     */
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Product> products;
}
