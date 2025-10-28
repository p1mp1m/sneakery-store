package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * DTO đơn giản cho danh sách sản phẩm (Admin)
 * Chỉ chứa thông tin cần thiết để hiển thị trong table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductListDto {
    private Long id;

    // 🆕 Thêm mã sản phẩm
    private String code;

    private String name;
    private String slug;
    private Integer brandId;
    private String brandName; // Tên thương hiệu để hiển thị
    private Boolean isActive;
    // 🆕 Bổ sung hai trường này
    @Schema(description = "ID chất liệu của sản phẩm", example = "2")
    private Integer materialId;

    @Schema(description = "ID loại đế giày của sản phẩm", example = "3")
    private Integer shoeSoleId;
    private Integer variantCount; // Số lượng variants

    // 🆕 Danh sách danh mục
    private List<SimpleCategoryDto> categories;
}
