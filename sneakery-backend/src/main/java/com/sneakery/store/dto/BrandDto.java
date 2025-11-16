package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO đại diện cho thương hiệu sản phẩm.
 *
 * <p>Được sử dụng cho cả API lấy danh sách và tạo / cập nhật thương hiệu.
 * Bao gồm đầy đủ các trường để phản ánh dữ liệu trên giao diện Admin Brand.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BrandDto", description = "Thông tin thương hiệu sản phẩm")
public class BrandDto {

    @Schema(description = "ID thương hiệu", example = "1")
    private Integer id;

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Schema(description = "Tên thương hiệu", example = "Nike", required = true)
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Schema(description = "Slug URL", example = "nike", required = true)
    private String slug;

    @Schema(description = "Đường dẫn logo", example = "https://cdn.sneakery.com/brands/nike.png")
    private String logoUrl;

    @Schema(description = "Website chính thức của thương hiệu", example = "https://www.nike.com")
    private String websiteUrl;

    @Schema(description = "Mô tả thương hiệu", example = "Thương hiệu thể thao nổi tiếng thế giới.")
    private String description;

    @Schema(description = "Trạng thái hoạt động của thương hiệu", example = "true")
    private Boolean isActive;

    @Schema(description = "Ngày tạo bản ghi", example = "2025-11-15T08:25:30")
    private LocalDateTime createdAt;
}
