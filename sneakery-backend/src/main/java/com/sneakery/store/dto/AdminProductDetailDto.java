package com.sneakery.store.dto;

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
    private Set<CategoryDto> categories;
    private List<AdminVariantRequestDto> variants; // Dùng lại DTO request cho tiện
}