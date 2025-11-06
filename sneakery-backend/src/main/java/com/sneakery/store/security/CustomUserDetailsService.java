package com.sneakery.store.security;

import com.sneakery.store.entity.User;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Lớp này cho Spring Security biết cách tải thông tin User từ CSDL
 * sử dụng email làm username.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tìm user bằng email (case-insensitive để tránh lỗi)
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    log.warn("Không tìm thấy người dùng với email: {}", email);
                    return new UsernameNotFoundException("Email hoặc mật khẩu không đúng");
                });

        // Kiểm tra user có bị inactive không
        if (Boolean.FALSE.equals(user.getIsActive())) {
            log.warn("Người dùng với email {} đã bị vô hiệu hóa", email);
            throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
        }

        log.debug("Đã tải thông tin user: {} (ID: {})", email, user.getId());
        // Trả về chính đối tượng User (vì User đã implement UserDetails)
        return user;
    }
}