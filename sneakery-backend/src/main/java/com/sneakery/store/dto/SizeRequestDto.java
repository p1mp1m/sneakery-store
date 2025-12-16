package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO để tạo/cập nhật Size")
public class SizeRequestDto {

    @NotBlank(message = "Tên size không được để trống")
    @Size(max = 20, message = "Tên size không được quá 20 ký tự")
    @Schema(description = "Tên size", example = "42", required = true)
    private String name;

    @Schema(description = "Thứ tự hiển thị", example = "8")
    private Integer displayOrder;

    @Schema(description = "Trạng thái hoạt động", example = "true")
    private Boolean isActive;
}
