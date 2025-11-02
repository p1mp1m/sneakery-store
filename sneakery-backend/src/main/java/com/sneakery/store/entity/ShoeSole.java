package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity đại diện cho Loại đế giày (Shoe Sole)
 * Dùng để quản lý các loại đế giày, ví dụ: Cao su, EVA, TPU,...
 */
@Entity
@Table(name = "Shoe_Soles")
@Data
public class ShoeSole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Khóa chính

    @Column(name = "name", nullable = false, unique = true)
    private String name; // Tên loại đế, ví dụ: "Cao su", "EVA", "TPU"

    @Column(name = "slug", nullable = false, unique = true)
    private String slug; // Slug SEO-friendly, ví dụ: "cao-su", "eva"

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description; // Mô tả chi tiết loại đế

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
     * Một loại đế có thể được dùng trong nhiều sản phẩm.
     */
    @OneToMany(mappedBy = "shoeSole", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Product> products;
}
