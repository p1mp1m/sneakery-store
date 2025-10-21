package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.Set; // SỬA ĐỔI: Thêm import
import java.util.HashSet; // SỬA ĐỔI: Thêm import

@Entity
@Table(name = "Products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    private String name;
    private String slug;
    private String description;

    // Cột is_active từ CSDL
    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductVariant> variants;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews;

    // SỬA ĐỔI: Thêm quan hệ ManyToMany với Category
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "Product_Categories", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "product_id"), // Cột trỏ về Product
            inverseJoinColumns = @JoinColumn(name = "category_id") // Cột trỏ về Category
    )
    private Set<Category> categories = new HashSet<>();
}