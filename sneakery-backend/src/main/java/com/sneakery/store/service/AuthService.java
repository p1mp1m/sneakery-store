package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.PasswordResetToken;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.PasswordResetTokenRepository;
import com.sneakery.store.repository.UserRepository;
import com.sneakery.store.security.JwtTokenProvider;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;

    @Value("${app.reset.token-expire-minutes:30}")
    private int expireMinutes;

    /**
     * Đăng ký
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

        userRepository.save(Objects.requireNonNull(user));

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(registerDto.getEmail());
        loginDto.setPassword(registerDto.getPassword());
        return login(loginDto);
    }

    /**
     * Đăng nhập
     */
    public AuthResponseDto login(LoginDto loginDto) {
        final String email = loginDto.getEmail();
        final String raw = loginDto.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, raw));

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
    @Transactional
    public void forgotPassword(String email) {
        var userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty())
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email không tồn tại trong hệ thống.");

        var user = userOpt.get();
        
        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Tài khoản này đã bị vô hiệu hoá.");
        }

        // Xoá token cũ (nếu có)
        tokenRepository.deleteByUser(user);

        // Tạo token mới
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(expireMinutes));
        tokenRepository.save(token);

        // Gửi email
        emailService.sendResetPasswordEmail(user, token.getToken());
    }

    // -------------------- RESET PASSWORD --------------------
    @Transactional
    public void resetPassword(String token, String newPassword) {
        var prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));

        if (prt.getUsedAt() != null)
            throw new IllegalStateException("Token đã được sử dụng");

        if (LocalDateTime.now().isAfter(prt.getExpiryDate()))
            throw new IllegalStateException("Token đã hết hạn");

        var user = prt.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(Objects.requireNonNull(user));

        // Đánh dấu đã dùng
        prt.setUsedAt(LocalDateTime.now());
        tokenRepository.save(prt);
    }

    // -------------------- GET PROFILE --------------------
    @Transactional(readOnly = true)
    public UserDto getProfile(Long userId) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }

    // -------------------- UPDATE PROFILE --------------------
    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileDto updateProfileDto) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        if (updateProfileDto.getFullName() != null && !updateProfileDto.getFullName().trim().isEmpty()) {
            user.setFullName(updateProfileDto.getFullName().trim());
        }

        if (updateProfileDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateProfileDto.getPhoneNumber().trim());
        }

        user = userRepository.save(Objects.requireNonNull(user));

        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .build();
    }

    // -------------------- CHANGE PASSWORD --------------------
    @Transactional
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));

        // Xác thực mật khẩu hiện tại
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không đúng");
        }

        // Cập nhật mật khẩu mới
        user.setPasswordHash(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(Objects.requireNonNull(user));
    }
}