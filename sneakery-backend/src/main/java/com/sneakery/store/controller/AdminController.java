package com.sneakery.store.controller;

import com.sneakery.store.dto.CreateUserRequestDto;
import com.sneakery.store.dto.UserDto;
import com.sneakery.store.exception.DatabaseOperationException;
import com.sneakery.store.repository.*;
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
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin - Dashboard", description = "API Dashboard và thống kê cho Admin")
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminController {


    private final OrderRepository orderRepository;
    private final AdminUserService adminUserService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Lấy thống kê tổng quan cho Admin Dashboard
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Thực hiện single aggregation query để lấy tất cả thống kê trong 1 lần</li>
     *   <li>Cache kết quả để tối ưu hiệu năng (TTL: 5 phút)</li>
     *   <li>Trả về thống kê tổng quan</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>totalUsers: Tổng số users trong hệ thống</li>
     *   <li>totalProducts: Tổng số sản phẩm (chưa bị xóa)</li>
     *   <li>totalOrders: Tổng số đơn hàng</li>
     *   <li>totalRevenue: Tổng doanh thu (VNĐ)</li>
     * </ul>
     * 
     * <p><b>Về tối ưu hiệu năng:</b>
     * <ul>
     *   <li>Sử dụng single aggregation query thay vì 4 queries riêng lẻ</li>
     *   <li>Caching với TTL 5 phút để giảm database load</li>
     *   <li>Performance improvement: Giảm số lượng queries từ 4 xuống 1 + caching</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Nếu có lỗi xảy ra, sẽ trả về giá trị mặc định (0) cho tất cả các thống kê.
     * 
     * @return ResponseEntity chứa Map với các thống kê:
     *         - totalUsers: Long
     *         - totalProducts: Long
     *         - totalOrders: Long
     *         - totalRevenue: Double (VNĐ)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Map&lt;String, Object&gt;&gt; response = adminController.getDashboardStats();
     * Map&lt;String, Object&gt; stats = response.getBody();
     * Long totalUsers = (Long) stats.get("totalUsers");
     * Double totalRevenue = (Double) stats.get("totalRevenue");
     * </pre>
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
                throw new DatabaseOperationException("No stats data returned from database");
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

    /**
     * GET /api/admin/dashboard/badges
     * Lấy số liệu cho badges trên quick actions (data thật từ database)
     */
    @GetMapping("/dashboard/badges")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardBadges() {
        log.info("📊 GET /api/admin/dashboard/badges");
        
        Map<String, Object> badges = new HashMap<>();
        
        try {
            // Count brands
            long brandsCount = brandRepository.count();
            badges.put("brands", brandsCount > 0 ? String.valueOf(brandsCount) : null);
            
            // Count categories
            long categoriesCount = categoryRepository.count();
            badges.put("categories", categoriesCount > 0 ? String.valueOf(categoriesCount) : null);
            
            // Count reviews (pending approval - chưa được approve)
            long reviewsCount = reviewRepository.count();
            badges.put("reviews", reviewsCount > 0 ? String.valueOf(reviewsCount) : null);
            
            // Count unread notifications
            long unreadNotificationsCount = notificationRepository.countByIsReadFalse();
            badges.put("notifications", unreadNotificationsCount > 0 ? String.valueOf(unreadNotificationsCount) : null);
            
            // Count pending return requests
            long pendingReturnsCount = returnRequestRepository.countByStatus("pending");
            badges.put("returns", pendingReturnsCount > 0 ? String.valueOf(pendingReturnsCount) : null);
            
            // Count pending payments
            long pendingPaymentsCount = paymentRepository.countByStatus("pending");
            badges.put("payments", pendingPaymentsCount > 0 ? String.valueOf(pendingPaymentsCount) : null);
            
            // Loyalty points (tổng điểm đã phát hành - có thể tính từ LoyaltyPoints table nếu có)
            // Tạm thời return null vì có thể không có table này
            badges.put("loyalty", null);
            
            log.debug("Badges fetched: {}", badges);
        } catch (Exception e) {
            log.error("Error fetching dashboard badges: {}", e.getMessage(), e);
            // Return empty badges on error
            badges.put("brands", null);
            badges.put("categories", null);
            badges.put("reviews", null);
            badges.put("notifications", null);
            badges.put("returns", null);
            badges.put("payments", null);
            badges.put("loyalty", null);
        }
        
        return ResponseEntity.ok(badges);
    }

    /**
     * GET /api/admin/payments/stats
     * Lấy thống kê về payments (data thật từ database)
     */
    @GetMapping("/payments/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPaymentStats() {
        log.info("📊 GET /api/admin/payments/stats");
        
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Tính tổng doanh thu từ payments completed
            BigDecimal totalRevenue = paymentRepository.sumAmountByStatus("completed");
            if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
            
            // Đếm số lượng payments theo status
            long completedCount = paymentRepository.countByStatus("completed");
            long failedCount = paymentRepository.countByStatus("failed");
            long pendingCount = paymentRepository.countByStatus("pending");
            long refundedCount = paymentRepository.countByStatus("refunded");
            long totalCount = paymentRepository.count();
            
            // Tính revenue tháng trước để tính trend
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime previousMonthStart = currentMonthStart.minusMonths(1);
            LocalDateTime previousMonthEnd = currentMonthStart.minusSeconds(1);
            
            BigDecimal currentMonthRevenue = paymentRepository.sumAmountByStatusAndDateRange("completed", currentMonthStart, now);
            if (currentMonthRevenue == null) currentMonthRevenue = BigDecimal.ZERO;
            
            BigDecimal previousMonthRevenue = paymentRepository.sumAmountByStatusAndDateRange("completed", previousMonthStart, previousMonthEnd);
            if (previousMonthRevenue == null) previousMonthRevenue = BigDecimal.ZERO;
            
            // Tính trend (%)
            double revenueTrend = 0;
            if (previousMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
                revenueTrend = ((currentMonthRevenue.doubleValue() - previousMonthRevenue.doubleValue()) / previousMonthRevenue.doubleValue()) * 100;
            }
            
            // Tính % thành công
            double successRate = totalCount > 0 ? (completedCount * 100.0 / totalCount) : 0;
            
            stats.put("totalRevenue", totalRevenue.doubleValue());
            stats.put("completedCount", completedCount);
            stats.put("failedCount", failedCount);
            stats.put("pendingCount", pendingCount);
            stats.put("refundedCount", refundedCount);
            stats.put("totalCount", totalCount);
            stats.put("successRate", successRate);
            stats.put("revenueTrend", revenueTrend);
            stats.put("currentMonthRevenue", currentMonthRevenue.doubleValue());
            stats.put("previousMonthRevenue", previousMonthRevenue.doubleValue());
            
            log.debug("Payment stats fetched: revenue={}, completed={}, failed={}, pending={}, trend={}%", 
                    totalRevenue, completedCount, failedCount, pendingCount, revenueTrend);
        } catch (Exception e) {
            log.error("Error fetching payment stats: {}", e.getMessage(), e);
            // Return empty stats on error
            stats.put("totalRevenue", 0.0);
            stats.put("completedCount", 0);
            stats.put("failedCount", 0);
            stats.put("pendingCount", 0);
            stats.put("refundedCount", 0);
            stats.put("totalCount", 0);
            stats.put("successRate", 0.0);
            stats.put("revenueTrend", 0.0);
            stats.put("currentMonthRevenue", 0.0);
            stats.put("previousMonthRevenue", 0.0);
        }
        
        return ResponseEntity.ok(stats);
    }
}
