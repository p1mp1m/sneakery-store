package com.sneakery.store.controller;

import com.sneakery.store.dto.CreateUserRequestDto;
import com.sneakery.store.dto.UserDto;
import com.sneakery.store.repository.OrderRepository;
import com.sneakery.store.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {


    private final OrderRepository orderRepository;
    private final AdminUserService adminUserService;

    /**
     * API Dashboard: Lấy thống kê tổng quan cho Admin
     * Bao gồm: Tổng users, products, orders, và doanh thu
     * 
     * OPTIMIZED: 
     * - Sử dụng single aggregation query thay vì 4 queries riêng lẻ
     * - Caching với TTL 5 minutes để giảm database load
     * Performance improvement: Giảm số lượng queries từ 4 xuống 1 + caching
     */
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "dashboardStats", key = "'admin-dashboard-stats'")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("📊 GET /api/admin/dashboard/stats (OPTIMIZED + CACHED)");
        Map<String, Object> stats = new HashMap<>();

        try {
            // OPTIMIZED: Lấy tất cả stats trong một query duy nhất
            log.debug("Fetching dashboard stats with optimized single query...");
            List<Object[]> results = orderRepository.getDashboardStatsRaw();
            
            if (results == null || results.isEmpty()) {
                throw new RuntimeException("No stats data returned from database");
            }
            
            // Native query returns List<Object[]>, get first row: [total_users, total_products, total_orders, total_revenue]
            Object[] result = results.get(0);
            Long totalUsers = result[0] != null ? ((Number) result[0]).longValue() : 0L;
            Long totalProducts = result[1] != null ? ((Number) result[1]).longValue() : 0L;
            Long totalOrders = result[2] != null ? ((Number) result[2]).longValue() : 0L;
            Double totalRevenue = result[3] != null ? ((Number) result[3]).doubleValue() : 0.0;
            
            stats.put("totalUsers", totalUsers);
            stats.put("totalProducts", totalProducts);
            stats.put("totalOrders", totalOrders);
            stats.put("totalRevenue", totalRevenue);
            
            log.debug("Stats fetched: users={}, products={}, orders={}, revenue={}", 
                    totalUsers, totalProducts, totalOrders, totalRevenue);
        } catch (Exception e) {
            log.error("Error fetching dashboard stats: {}", e.getMessage(), e);
            // Fallback values in case of error
            stats.put("totalUsers", 0);
            stats.put("totalProducts", 0);
            stats.put("totalOrders", 0);
            stats.put("totalRevenue", 0.0);
        }

        log.info("✅ Dashboard stats response: {}", stats);
        return ResponseEntity.ok(stats);
    }

    /**
     * API: Clear dashboard stats cache (admin only)
     * Useful when data changes and cache needs to be refreshed immediately
     */
    @PostMapping("/dashboard/stats/clear-cache")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "dashboardStats", allEntries = true)
    public ResponseEntity<Map<String, String>> clearDashboardCache() {
        log.info("🗑️ POST /api/admin/dashboard/stats/clear-cache");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Dashboard cache cleared successfully");
        response.put("status", "success");
        return ResponseEntity.ok(response);
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

    /**
     * API: Tạo user mới (Admin only)
     */
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody CreateUserRequestDto requestDto
    ) {
        UserDto newUser = adminUserService.createUser(requestDto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /**
     * API: Xóa user (Admin only)
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xóa người dùng thành công");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}
