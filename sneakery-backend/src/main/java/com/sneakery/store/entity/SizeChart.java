package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity: SizeChart - Bảng size giày
 * Lưu thông tin chuyển đổi size và kích thước thực tế
 */
@Data
@Entity
@Table(name = "Size_Charts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeChart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quan hệ với Brand (Mỗi brand có bảng size riêng)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(name = "category", nullable = false, length = 50)
    private String category; // Running, Basketball, Lifestyle, etc.

    @Column(name = "size", nullable = false, length = 10)
    private String size; // EU size: 38, 39, 40...

    @Column(name = "size_us", length = 10)
    private String sizeUs; // US size conversion

    @Column(name = "size_uk", length = 10)
    private String sizeUk; // UK size conversion

    @Column(name = "length_cm", precision = 5, scale = 2)
    private BigDecimal lengthCm; // Độ dài bàn chân (cm)

    @Column(name = "width_cm", precision = 5, scale = 2)
    private BigDecimal widthCm; // Độ rộng bàn chân (cm)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

