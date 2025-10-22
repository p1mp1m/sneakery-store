package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {
    // ID cần cho response và update
    private Long id;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Địa chỉ (dòng 1) không được để trống")
    private String line1;

    private String line2;

    @NotBlank(message = "Thành phố/Tỉnh không được để trống")
    private String city;

    private String district;
    private String ward; // Phường/Xã
    private String postalCode;
    
    // Không cần 'userId' vì chúng ta sẽ lấy từ JWT
}