package com.sneakery.store.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity: ProductImage
 * --------------------
 * Đại diện cho 1 hình ảnh thuộc về sản phẩm.
 * Một sản phẩm có thể có nhiều hình ảnh (gallery),
 * trong đó có thể có 1 ảnh được đánh dấu là ảnh chính (primary).
 */
@Data
@Entity
@Table(name = "Product_Images")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Ảnh sản phẩm trong hệ thống Sneakery Store")
public class ProductImage {

    /** ID tự tăng của ảnh sản phẩm */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID tự tăng của ảnh sản phẩm")
    private Long id;

    /** Liên kết tới sản phẩm chứa ảnh này (nhiều ảnh thuộc 1 sản phẩm) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "Sản phẩm mà ảnh này thuộc về")
    private Product product;

    /** Đường dẫn ảnh (có thể là local hoặc URL ngoài như Unsplash) */
    @Column(name = "image_url", nullable = false, length = 500)
    @Schema(description = "Đường dẫn ảnh (local hoặc URL ngoài)")
    private String imageUrl;

    /** Văn bản mô tả thay thế cho ảnh (SEO hoặc accessibility) */
    @Column(name = "alt_text", length = 255)
    @Schema(description = "Mô tả ngắn cho ảnh (alt text)")
    private String altText;

    /** Cờ đánh dấu ảnh đại diện (primary) của sản phẩm */
    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    @Schema(description = "Đánh dấu ảnh chính của sản phẩm")
    private Boolean isPrimary = false;

    /** Thứ tự hiển thị trong gallery (1 = đầu tiên) */
    @Builder.Default
    @Column(name = "display_order", nullable = false)
    @Schema(description = "Thứ tự hiển thị trong gallery (1 = đầu tiên)")
    private Integer displayOrder = 1;

    /** Thời điểm tạo bản ghi */
    @Column(name = "created_at", updatable = false)
    @Schema(description = "Thời điểm ảnh được thêm vào hệ thống")
    private LocalDateTime createdAt;

    /** Cloudinary Public ID - dùng để xóa ảnh trên Cloudinary */
    @Column(name = "cloudinary_public_id", length = 255, nullable = true)
    @Schema(description = "Public ID từ Cloudinary (dùng để xóa ảnh)")
    private String cloudinaryPublicId;

    /** Thiết lập mặc định khi thêm mới */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
//        if (isPrimary == null) isPrimary = false;
        if (displayOrder == null) displayOrder = 0;
    }
}
