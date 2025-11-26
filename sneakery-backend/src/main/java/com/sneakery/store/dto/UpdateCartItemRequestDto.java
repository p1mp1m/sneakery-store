package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request cập nhật số lượng sản phẩm trong giỏ hàng")
public class UpdateCartItemRequestDto {

    @NotNull(message = "variantId không được để trống")
    @Schema(description = "ID của Product Variant", example = "10")
    private Long variantId;

    @Min(value = 1, message = "Số lượng phải >= 1")
    @Schema(description = "Số lượng mới của sản phẩm", example = "3")
    private int quantity;
}
