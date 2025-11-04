package com.sneakery.store.controller;

import com.sneakery.store.repository.OrderRepository;
import com.sneakery.store.repository.PaymentRepository;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Analytics Controller
 * Cung cấp các API analytics cho admin dashboard
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminAnalyticsController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * GET /api/admin/analytics/revenue
     * Lấy dữ liệu doanh thu theo period
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics(
            @RequestParam(defaultValue = "30d") String period
    ) {
        log.info("📊 GET /api/admin/analytics/revenue - period: {}", period);
        
        LocalDateTime startDate = calculateStartDate(period);
        LocalDateTime endDate = LocalDateTime.now();
        
        // Tính tổng doanh thu từ payments completed trong period
        BigDecimal totalRevenue = paymentRepository.sumAmountByStatus("completed");
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        
        // Tạo dữ liệu theo ngày/tuần/tháng (tùy theo period)
        List<Map<String, Object>> dailyData = new ArrayList<>();
        
        // Group by date range
        if (period.equals("7d") || period.equals("30d")) {
            // Group by day
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", current.toLocalDate().toString());
                dayData.put("revenue", getDailyRevenue(current.toLocalDate()));
                dailyData.add(dayData);
                current = current.plusDays(1);
            }
        } else if (period.equals("90d")) {
            // Group by week
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> weekData = new HashMap<>();
                weekData.put("date", current.toLocalDate().toString());
                weekData.put("revenue", getWeeklyRevenue(current));
                dailyData.add(weekData);
                current = current.plusWeeks(1);
            }
        } else {
            // Group by month
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("date", current.toLocalDate().toString());
                monthData.put("revenue", getMonthlyRevenue(current));
                dailyData.add(monthData);
                current = current.plusMonths(1);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue);
        response.put("data", dailyData);
        response.put("period", period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/analytics/orders
     * Lấy dữ liệu đơn hàng theo period
     */
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrderAnalytics(
            @RequestParam(defaultValue = "30d") String period
    ) {
        log.info("📊 GET /api/admin/analytics/orders - period: {}", period);
        
        LocalDateTime startDate = calculateStartDate(period);
        LocalDateTime endDate = LocalDateTime.now();
        
        // Tổng số đơn hàng
        long totalOrders = orderRepository.count();
        
        // Tạo dữ liệu theo thời gian
        List<Map<String, Object>> dailyData = new ArrayList<>();
        
        if (period.equals("7d") || period.equals("30d")) {
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", current.toLocalDate().toString());
                dayData.put("orders", getDailyOrderCount(current.toLocalDate()));
                dailyData.add(dayData);
                current = current.plusDays(1);
            }
        } else if (period.equals("90d")) {
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> weekData = new HashMap<>();
                weekData.put("date", current.toLocalDate().toString());
                weekData.put("orders", getWeeklyOrderCount(current));
                dailyData.add(weekData);
                current = current.plusWeeks(1);
            }
        } else {
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("date", current.toLocalDate().toString());
                monthData.put("orders", getMonthlyOrderCount(current));
                dailyData.add(monthData);
                current = current.plusMonths(1);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalOrders", totalOrders);
        response.put("data", dailyData);
        response.put("period", period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/analytics/products
     * Lấy dữ liệu sản phẩm theo period
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductAnalytics(
            @RequestParam(defaultValue = "30d") String period
    ) {
        log.info("📊 GET /api/admin/analytics/products - period: {}", period);
        
        // Tổng số sản phẩm
        long totalProducts = productRepository.count();
        
        // Top sản phẩm bán chạy (có thể mở rộng sau)
        List<Map<String, Object>> topProducts = new ArrayList<>();
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalProducts", totalProducts);
        response.put("topProducts", topProducts);
        response.put("period", period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/admin/analytics/customers
     * Lấy dữ liệu khách hàng theo period
     */
    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomerAnalytics(
            @RequestParam(defaultValue = "30d") String period
    ) {
        log.info("📊 GET /api/admin/analytics/customers - period: {}", period);
        
        LocalDateTime startDate = calculateStartDate(period);
        LocalDateTime endDate = LocalDateTime.now();
        
        // Tổng số khách hàng
        long totalCustomers = userRepository.count();
        
        // Khách hàng mới trong period
        long newCustomers = userRepository.countByCreatedAtBetween(startDate, endDate);
        
        // Tạo dữ liệu theo thời gian
        List<Map<String, Object>> dailyData = new ArrayList<>();
        
        if (period.equals("7d") || period.equals("30d")) {
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", current.toLocalDate().toString());
                dayData.put("customers", getDailyCustomerCount(current.toLocalDate()));
                dailyData.add(dayData);
                current = current.plusDays(1);
            }
        } else if (period.equals("90d")) {
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> weekData = new HashMap<>();
                weekData.put("date", current.toLocalDate().toString());
                weekData.put("customers", getWeeklyCustomerCount(current));
                dailyData.add(weekData);
                current = current.plusWeeks(1);
            }
        } else {
            LocalDateTime current = startDate;
            while (!current.isAfter(endDate)) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("date", current.toLocalDate().toString());
                monthData.put("customers", getMonthlyCustomerCount(current));
                dailyData.add(monthData);
                current = current.plusMonths(1);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalCustomers", totalCustomers);
        response.put("newCustomers", newCustomers);
        response.put("data", dailyData);
        response.put("period", period);
        
        return ResponseEntity.ok(response);
    }

    // Helper methods
    private LocalDateTime calculateStartDate(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case "7d":
                return now.minusDays(7);
            case "30d":
                return now.minusDays(30);
            case "90d":
                return now.minusDays(90);
            case "1y":
                return now.minusYears(1);
            default:
                return now.minusDays(30);
        }
    }

    private BigDecimal getDailyRevenue(java.time.LocalDate date) {
        // Simplified: return total revenue / number of days
        // In production, should query actual daily revenue from Payments table
        BigDecimal total = paymentRepository.sumAmountByStatus("completed");
        return total != null ? total.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private BigDecimal getWeeklyRevenue(LocalDateTime weekStart) {
        // Simplified implementation
        BigDecimal total = paymentRepository.sumAmountByStatus("completed");
        return total != null ? total.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private BigDecimal getMonthlyRevenue(LocalDateTime monthStart) {
        // Simplified implementation
        BigDecimal total = paymentRepository.sumAmountByStatus("completed");
        return total != null ? total.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private long getDailyOrderCount(java.time.LocalDate date) {
        // Simplified: return total orders / number of days
        return orderRepository.count() / 30;
    }

    private long getWeeklyOrderCount(LocalDateTime weekStart) {
        return orderRepository.count() / 12;
    }

    private long getMonthlyOrderCount(LocalDateTime monthStart) {
        return orderRepository.count() / 12;
    }

    private long getDailyCustomerCount(java.time.LocalDate date) {
        return userRepository.count() / 30;
    }

    private long getWeeklyCustomerCount(LocalDateTime weekStart) {
        return userRepository.count() / 12;
    }

    private long getMonthlyCustomerCount(LocalDateTime monthStart) {
        return userRepository.count() / 12;
    }
}

