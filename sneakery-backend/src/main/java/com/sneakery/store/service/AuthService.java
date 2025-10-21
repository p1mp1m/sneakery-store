package com.sneakery.store.service;

import com.sneakery.store.dto.AuthResponseDto;
import com.sneakery.store.dto.LoginDto;
import com.sneakery.store.dto.RegisterDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Xử lý logic Đăng ký
     */
    public AuthResponseDto register(RegisterDto registerDto) {
        // 1. Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email đã tồn tại!");
        }

        // 2. Tạo đối tượng User
        User user = User.builder()
                .fullName(registerDto.getFullName())
                .email(registerDto.getEmail())
                .passwordHash(passwordEncoder.encode(registerDto.getPassword()))
                .phoneNumber(registerDto.getPhoneNumber())
                .role("USER")
                .isActive(true)
                .build();

        // 3. Lưu vào CSDL
        userRepository.save(user);

        // 4. Tự động đăng nhập user sau khi đăng ký thành công

        // SỬA LỖI: Thay vì dùng new LoginDto(email, pass),
        // chúng ta dùng hàm khởi tạo rỗng và các hàm setter
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(registerDto.getEmail());
        loginDto.setPassword(registerDto.getPassword());

        return login(loginDto); // Gọi hàm login() ở dưới
    }

    /**
     * Xử lý logic Đăng nhập
     */
    public AuthResponseDto login(LoginDto loginDto) {
        // 1. Xác thực (email + password)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        // 2. Nếu thành công, lưu thông tin xác thực vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Tạo JWT token
        String token = jwtTokenProvider.generateToken(authentication);

        // 4. Lấy thông tin User (đã được xác thực)
        User user = (User) authentication.getPrincipal();

        // 5. Trả về DTO chứa token và thông tin user cho VueJS
        return AuthResponseDto.builder()
                .accessToken(token)
                .role(user.getRole())
                .fullName(user.getFullName())
                .userId(user.getId())
                .build();
    }
}