package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor; // SỬA ĐỔI: Thêm import
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor; // SỬA ĐỔI: Thêm import

import java.util.List;

@Data
@Builder
@NoArgsConstructor // Cần cho Jackson/JPA
@AllArgsConstructor // SỬA ĐỔI: Thêm annotation này
public class CategoryDto {
    private Integer id;

    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    private String slug;

    // ID của danh mục cha (nếu có)
    private Integer parentId;
    
    // Danh sách danh mục con (chỉ có khi lấy category groups)
    private List<CategoryDto> children;
    
    // Constructor cho backward compatibility (không có children)
    public CategoryDto(Integer id, String name, String slug, Integer parentId) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
        this.children = null;
    }
}