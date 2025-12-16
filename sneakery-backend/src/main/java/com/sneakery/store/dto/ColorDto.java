package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO cho Color")
public class ColorDto {

    @Schema(description = "ID của màu", example = "1")
    private Integer id;

    @Schema(description = "Tên màu", example = "Đen")
    private String name;

    @Schema(description = "Mã màu hex", example = "#000000")
    private String hexCode;

    @Schema(description = "Thứ tự hiển thị", example = "1")
    private Integer displayOrder;

    @Schema(description = "Trạng thái hoạt động", example = "true")
    private Boolean isActive;

    @Schema(description = "Ngày tạo")
    private LocalDateTime createdAt;

    @Schema(description = "Ngày cập nhật")
    private LocalDateTime updatedAt;
}
