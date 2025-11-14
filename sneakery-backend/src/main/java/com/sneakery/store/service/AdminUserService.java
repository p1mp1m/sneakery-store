package com.sneakery.store.service;

import com.sneakery.store.dto.CreateUserRequestDto;
import com.sneakery.store.dto.UserDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Service quản lý users cho Admin
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy danh sách users với phân trang (chỉ lấy users chưa bị xóa)
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        // Sử dụng findAllWithFilters với null để lấy tất cả users chưa bị xóa
        Page<User> userPage = userRepository.findAllWithFilters(null, null, null, pageable);
        return userPage.map(this::convertToDto);
    }

    /**
     * Lấy danh sách users với search và filter
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsersWithFilters(String search, String role, Boolean isActive, Pageable pageable) {
        Page<User> userPage = userRepository.findAllWithFilters(search, role, isActive, pageable);
        return userPage.map(this::convertToDto);
    }

    /**
     * Lấy thông tin user theo ID
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        return convertToDto(user);
    }

    /**
     * Cập nhật trạng thái active của user
     */
    @Transactional
    public UserDto updateUserStatus(Long userId, Boolean isActive) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        
        user.setIsActive(isActive);
        User updatedUser = userRepository.save(user);
        
        return convertToDto(updatedUser);
    }

    /**
     * Cập nhật role của user
     */
    @Transactional
    public UserDto updateUserRole(Long userId, String role) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        
        // Validate role
        if (!role.equals("USER") && !role.equals("ADMIN") && !role.equals("MODERATOR")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Role không hợp lệ");
        }
        
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        
        return convertToDto(updatedUser);
    }

    /**
     * Tạo user mới (Admin only)
     */
    @Transactional
    public UserDto createUser(CreateUserRequestDto requestDto) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
        }
        
        // Validate role
        String role = requestDto.getRole() != null ? requestDto.getRole() : "USER";
        if (!role.equals("USER") && !role.equals("ADMIN") && !role.equals("MODERATOR")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Role không hợp lệ");
        }
        
        // Tạo user mới
        User user = User.builder()
                .email(requestDto.getEmail())
                .passwordHash(passwordEncoder.encode(requestDto.getPassword()))
                .fullName(requestDto.getFullName())
                .phoneNumber(requestDto.getPhoneNumber())
                .address(requestDto.getAddress()) // Thêm địa chỉ
                .role(role)
                .isActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : true)
                .createdAt(LocalDateTime.now())
                .build();
        
        User savedUser = userRepository.save(Objects.requireNonNull(user));
        return convertToDto(savedUser);
    }

    /**
     * Xóa user (soft delete hoặc hard delete)
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        
        // Soft delete: set deletedAt thay vì xóa thật
        user.setDeletedAt(LocalDateTime.now());
        user.setIsActive(false);
        userRepository.save(user);
        
        // Nếu muốn hard delete, uncomment dòng sau:
        // userRepository.delete(user);
    }

    /**
     * Convert User entity sang UserDto
     */
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .build();
    }
}

