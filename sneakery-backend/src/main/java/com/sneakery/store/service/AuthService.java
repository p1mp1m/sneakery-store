package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.PasswordResetToken;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.PasswordResetTokenRepository;
import com.sneakery.store.repository.UserRepository;
import com.sneakery.store.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // ✅ Thêm 3 dependency cần thiết cho forgot/reset
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;

    /**
     * Xử lý logic Đăng ký
     */
    public AuthResponseDto register(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email đã tồn tại!");
        }

        User user = User.builder()
                .fullName(registerDto.getFullName())
                .email(registerDto.getEmail())
                .passwordHash(passwordEncoder.encode(registerDto.getPassword()))
                .phoneNumber(registerDto.getPhoneNumber())
                .role("USER")
                .isActive(true)
                .build();

        userRepository.save(user);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(registerDto.getEmail());
        loginDto.setPassword(registerDto.getPassword());
        return login(loginDto);
    }

    /**
     * Xử lý logic Đăng nhập
     */
    public AuthResponseDto login(LoginDto loginDto) {
        final String email = loginDto.getEmail();
        final String raw = loginDto.getPassword();

        // Debug (tạm bật khi dev)
        System.out.println("🧩 Login attempt -> email=" + email);
        userRepository.findByEmail(email).ifPresentOrElse(u -> {
            boolean matches = passwordEncoder.matches(raw, u.getPasswordHash());
            System.out.println("🧩 BCrypt matches? " + matches);
        }, () -> System.out.println("🧩 User not found with email=" + email));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, raw)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        User user = (User) authentication.getPrincipal();
        return AuthResponseDto.builder()
                .accessToken(token)
                .role(user.getRole())
                .fullName(user.getFullName())
                .userId(user.getId())
                .build();
    }

    // -------------------- FORGOT PASSWORD --------------------
    public void forgotPassword(ForgotPasswordRequestDto request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản với email này!");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        emailService.sendResetPasswordEmail(user.getEmail(), resetLink);
    }

    // -------------------- RESET PASSWORD --------------------
    public void resetPassword(ResetPasswordRequestDto request) {
        PasswordResetToken tokenEntity = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Token không hợp lệ hoặc đã hết hạn!"));

        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Token đã hết hạn!");
        }

        User user = tokenEntity.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword())); // ✅ dùng đúng field passwordHash
        userRepository.save(user);
        tokenRepository.delete(tokenEntity);
    }
}