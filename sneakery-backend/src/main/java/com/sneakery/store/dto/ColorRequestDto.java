package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO để tạo/cập nhật Color")
public class ColorRequestDto {

    @NotBlank(message = "Tên màu không được để trống")
    @Size(max = 50, message = "Tên màu không được quá 50 ký tự")
    @Schema(description = "Tên màu", example = "Đen", required = true)
    private String name;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Mã màu hex không hợp lệ (ví dụ: #000000)")
    @Schema(description = "Mã màu hex", example = "#000000")
    private String hexCode;

    @Schema(description = "Thứ tự hiển thị", example = "1")
    private Integer displayOrder;

    @Schema(description = "Trạng thái hoạt động", example = "true")
    private Boolean isActive;
}
