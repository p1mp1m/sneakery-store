package com.sneakery.store.controller;

import com.sneakery.store.entity.User;
import com.sneakery.store.repository.OrderRepository;
import com.sneakery.store.repository.PaymentRepository;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
     * API: Lấy danh sách tất cả users
     * Chỉ admin mới có quyền truy cập
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
