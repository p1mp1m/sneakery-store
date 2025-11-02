package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO: ProductImageDto
 * --------------------
 * Đại diện dữ liệu hình ảnh sản phẩm gửi/nhận giữa FE ↔ BE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO truyền dữ liệu ảnh sản phẩm giữa FE ↔ BE")
public class ProductImageDto {

    /** ID của ảnh (null khi tạo mới) */
    @Schema(description = "ID của ảnh sản phẩm (nếu có)")
    private Long id;

    /** ID sản phẩm mà ảnh thuộc về */
    @Schema(description = "ID sản phẩm mà ảnh này thuộc về")
    private Long productId;

    /** Đường dẫn ảnh (bắt buộc) */
    @NotBlank(message = "URL hình ảnh không được để trống")
    @Schema(description = "Đường dẫn ảnh (bắt buộc, local hoặc URL ngoài)")
    private String imageUrl;

    /** Mô tả ngắn cho ảnh (alt text) */
    @Schema(description = "Mô tả ngắn cho ảnh (alt text)")
    private String altText;

    /** Ảnh chính (true = ảnh đại diện của sản phẩm) */
    @Schema(description = "Cờ đánh dấu ảnh chính của sản phẩm")
    private Boolean isPrimary;

    /** Thứ tự hiển thị trong gallery (1 = đầu tiên) */
    @Builder.Default
    @Schema(description = "Thứ tự hiển thị trong gallery (1 = đầu tiên)")
    private Integer displayOrder = 1;

    private String cloudinaryPublicId;
}
