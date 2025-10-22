package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Brands")
@Data
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // CSDL của bạn dùng 'int'

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    // V3.1 fields
    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Định nghĩa quan hệ ngược lại với Product.
     * Một Brand có nhiều Product.
     * * - mappedBy = "brand": Chỉ ra rằng quan hệ này được quản lý bởi
     * trường 'brand' bên trong class Product.
     * * - fetch = FetchType.LAZY: Tốt cho hiệu năng. Sẽ không tải danh sách
     * sản phẩm trừ khi ta gọi hàm getProducts().
     *
     * - @JsonIgnore: Cần thiết để tránh lỗi vòng lặp vô hạn (infinite loop)
     * khi serialize JSON (Product -> Brand -> List<Product> -> Brand...).
     */
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
    @JsonIgnore // Quan trọng: Tránh lỗi vòng lặp JSON
    @ToString.Exclude // Tránh lỗi vòng lặp khi gọi .toString()
    @EqualsAndHashCode.Exclude // Tránh lỗi vòng lặp khi gọi .equals()
    private List<Product> products;
}