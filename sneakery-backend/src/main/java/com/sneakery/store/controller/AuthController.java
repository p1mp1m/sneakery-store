package com.sneakery.store.controller;

import com.sneakery.store.dto.AuthResponseDto; // SỬA LỖI: Import DTO mới
import com.sneakery.store.dto.LoginDto;
import com.sneakery.store.dto.RegisterDto;
import com.sneakery.store.dto.ForgotPasswordRequestDto;
import com.sneakery.store.dto.ResetPasswordRequestDto;
import com.sneakery.store.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; // SỬA LỖI: Dùng Constructor Injection
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // SỬA LỖI: Dùng Constructor Injection
@CrossOrigin(origins = "http://localhost:5173")
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
}