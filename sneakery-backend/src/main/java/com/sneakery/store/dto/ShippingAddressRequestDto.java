package com.sneakery.store.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO: Request tính phí vận chuyển
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin địa chỉ để tính phí vận chuyển")
public class ShippingAddressRequestDto {

    @Schema(description = "Địa chỉ dòng 1", example = "236 Hoàng Quốc Việt")
    private String line1;

    @Schema(description = "Địa chỉ bổ sung", example = "Chung cư DEF")
    private String line2;

    @Schema(description = "Phường/Xã", example = "Cổ Nhuế 1")
    private String ward;

    @Schema(description = "Quận/Huyện", example = "Bắc Từ Liêm")
    private String district;

    @Schema(description = "Tỉnh/Thành phố", example = "Hà Nội")
    private String city;

    @Schema(description = "Mã bưu chính", example = "100000")
    private String postalCode;
}
