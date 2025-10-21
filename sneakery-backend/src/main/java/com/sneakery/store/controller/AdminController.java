package com.sneakery.store.controller;

import com.sneakery.store.dto.UserDto;
import com.sneakery.store.repository.OrderRepository;
import com.sneakery.store.repository.PaymentRepository;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.UserRepository;
import com.sneakery.store.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final AdminUserService adminUserService;

    /**
     * API Dashboard: Lấy thống kê tổng quan cho Admin
     * Bao gồm: Tổng users, products, orders, và doanh thu
     */
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Lấy dữ liệu thực từ database
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        
        // Tính tổng doanh thu từ các payment đã hoàn thành
        BigDecimal totalRevenue = paymentRepository.sumAmountByStatus("completed");
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        stats.put("totalUsers", totalUsers);
        stats.put("totalProducts", totalProducts);
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);

        return ResponseEntity.ok(stats);
    }

    /**
     * API: Lấy danh sách users với phân trang và filter
     * Chỉ admin mới có quyền truy cập
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        
        // Nếu có search hoặc filters, sử dụng method với filters
        if ((search != null && !search.trim().isEmpty()) || 
            (role != null && !role.trim().isEmpty()) ||
            isActive != null) {
            Page<UserDto> userPage = adminUserService.getAllUsersWithFilters(search, role, isActive, pageable);
            return ResponseEntity.ok(userPage);
        }
        
        // Nếu không có filter, sử dụng method mặc định
        Page<UserDto> userPage = adminUserService.getAllUsers(pageable);
        return ResponseEntity.ok(userPage);
    }

    /**
     * API: Lấy thông tin user theo ID
     */
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = adminUserService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * API: Cập nhật trạng thái user
     */
    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request
    ) {
        Boolean isActive = request.get("isActive");
        UserDto updatedUser = adminUserService.updateUserStatus(id, isActive);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * API: Cập nhật role của user
     */
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String role = request.get("role");
        UserDto updatedUser = adminUserService.updateUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }
}
