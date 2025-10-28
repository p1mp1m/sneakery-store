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
    private String name;
    private String slug;
    private String description;
    private Boolean isActive;
    // 🆕 Bổ sung hai trường này
    @Schema(description = "ID chất liệu của sản phẩm", example = "2")
    private Integer materialId;

    @Schema(description = "ID loại đế giày của sản phẩm", example = "3")
    private Integer shoeSoleId;
    private Set<CategoryDto> categories;
    private List<AdminVariantRequestDto> variants; // Dùng lại DTO request cho tiện
}