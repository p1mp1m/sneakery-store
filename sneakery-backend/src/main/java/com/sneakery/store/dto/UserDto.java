package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO (Data Transfer Object) để hiển thị thông tin User an toàn
 * (không chứa password_hash)
 */
@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Boolean isActive;
    private String role;
}