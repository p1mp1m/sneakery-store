package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor; // SỬA ĐỔI: Thêm import
import lombok.Data;
import lombok.NoArgsConstructor; // SỬA ĐỔI: Thêm import

@Data
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
}