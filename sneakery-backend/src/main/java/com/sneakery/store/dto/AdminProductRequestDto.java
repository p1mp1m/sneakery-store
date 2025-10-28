package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AdminProductRequestDto {
    @NotNull(message = "ID thương hiệu không được để trống")
    private Integer brandId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    private String slug;

    private String description;
    
    @NotNull(message = "Trạng thái active không được để trống")
    private Boolean isActive;

    // 🆕 Bổ sung 2 trường mới
    @Schema(description = "ID chất liệu của sản phẩm", example = "2")
    private Integer materialId;

    @Schema(description = "ID loại đế giày của sản phẩm", example = "3")
    private Integer shoeSoleId;

    // Danh sách các ID của Category
    @NotEmpty(message = "Sản phẩm phải thuộc ít nhất 1 danh mục")
    private Set<Integer> categoryIds;

    // Danh sách các biến thể
    @Valid // Quan trọng: Báo cho Spring Boot validate các object bên trong List
    @NotEmpty(message = "Sản phẩm phải có ít nhất 1 biến thể")
    private List<AdminVariantRequestDto> variants;
}