package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Product_Variants")
@Data
@NamedEntityGraph(
        name = "ProductVariant.withProductAndBrand",
        attributeNodes = {
                @NamedAttributeNode("product"),
                @NamedAttributeNode(value = "product", subgraph = "product.brand")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "product.brand",
                        attributeNodes = @NamedAttributeNode("brand")
                )
        }
)
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    // @JsonIgnore - Tạm thời comment để test
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "size", nullable = false)
    private String size;

    @Column(name = "color", nullable = false)
    private String color;

    // TÊN TRƯỜNG LÀ priceBase
    @Column(name = "price_base", nullable = false)
    private BigDecimal priceBase;

    // TÊN TRƯỜNG LÀ priceSale
    @Column(name = "price_sale")
    private BigDecimal priceSale;

    // V3.1 field - cost price for profit calculation
    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "damaged_quantity", nullable = false)
    private int damagedQuantity = 0;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    // V3.1 field - weight for shipping calculation
    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Quan hệ với VariantImages (Một Variant có nhiều ảnh)
    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VariantImage> images;
}