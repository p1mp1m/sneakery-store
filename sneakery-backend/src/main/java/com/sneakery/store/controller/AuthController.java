package com.sneakery.store.controller;

import com.sneakery.store.dto.AuthResponseDto; // SỬA LỖI: Import DTO mới
import com.sneakery.store.dto.LoginDto;
import com.sneakery.store.dto.RegisterDto;
import com.sneakery.store.dto.ForgotPasswordRequestDto;
import com.sneakery.store.dto.ResetPasswordRequestDto;
import com.sneakery.store.dto.UpdateProfileDto;
import com.sneakery.store.dto.ChangePasswordDto;
import com.sneakery.store.dto.UserDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; // SỬA LỖI: Dùng Constructor Injection
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // SỬA LỖI: Dùng Constructor Injection
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AuthController {

    private final AuthService authService; // SỬA LỖI: Dùng 'final'

    /**
     * Sửa lại: Trả về AuthResponseDto
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterDto registerDto) {
        AuthResponseDto response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Sửa lại: Trả về AuthResponseDto
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Nếu email tồn tại, hệ thống sẽ gửi link đặt lại mật khẩu.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }

    /**
     * Lấy thông tin profile của user hiện tại
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal User currentUser) {
        UserDto userDto = authService.getProfile(currentUser.getId());
        return ResponseEntity.ok(userDto);
    }

    /**
     * Cập nhật thông tin profile của user hiện tại
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateProfile(
            @Valid @RequestBody UpdateProfileDto updateProfileDto,
            @AuthenticationPrincipal User currentUser
    ) {
        UserDto updatedUser = authService.updateProfile(currentUser.getId(), updateProfileDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Đổi mật khẩu của user hiện tại
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordDto changePasswordDto,
            @AuthenticationPrincipal User currentUser
    ) {
        authService.changePassword(currentUser.getId(), changePasswordDto);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
}