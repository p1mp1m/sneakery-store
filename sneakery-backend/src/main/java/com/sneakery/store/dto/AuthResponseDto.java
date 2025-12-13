package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) chứa thông tin trả về sau khi
 * đăng nhập hoặc đăng ký thành công.
 * Đây là file mà AuthController sẽ trả về.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private String accessToken;

    @Builder.Default // Giá trị mặc định khi build
    private String tokenType = "Bearer";

    // Thông tin thêm cho VueJS sử dụng
    private String role;
    private String fullName;
    private Long userId;
}