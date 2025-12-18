package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class AdminProductDetailDto {
    private Long id;
    private Integer brandId;
    private String brandName;
    private String name;
    private String slug;
    private String description;
    private Boolean isActive;
    @Schema(description = "Tổng tồn kho của tất cả biến thể")
    private Integer totalStock;

    // 🆕 Bổ sung hai trường này
    @Schema(description = "ID chất liệu của sản phẩm", example = "2")
    private Integer materialId;

    @Schema(description = "ID loại đế giày của sản phẩm", example = "3")
    private Integer shoeSoleId;

    @Schema(description = "Giá từ (VNĐ)", example = "1000000")
    private Integer priceFrom;

    @Schema(description = "Giá đến (VNĐ)", example = "5000000")
    private Integer priceTo;

    private Set<CategoryDto> categories;
    private List<AdminVariantRequestDto> variants; // Dùng lại DTO request cho tiện
}