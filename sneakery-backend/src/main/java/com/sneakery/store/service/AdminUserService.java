package com.sneakery.store.service;

import com.sneakery.store.dto.UserDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service quản lý users cho Admin
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    /**
     * Lấy danh sách users với phân trang
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        return convertToDto(user);
    }

    /**
     * Cập nhật trạng thái active của user
     */
    @Transactional
    public UserDto updateUserStatus(Long userId, Boolean isActive) {
        User user = userRepository.findById(userId)
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
        User user = userRepository.findById(userId)
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

