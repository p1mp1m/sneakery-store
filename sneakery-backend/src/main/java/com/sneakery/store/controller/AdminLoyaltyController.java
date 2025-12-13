package com.sneakery.store.controller;

import com.sneakery.store.dto.LoyaltyDto;
import com.sneakery.store.entity.LoyaltyPoint;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.UserNotFoundException;
import com.sneakery.store.repository.LoyaltyPointRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Admin Loyalty Controller
 * Quản lý loyalty points cho admin
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/loyalty")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminLoyaltyController {

    private final LoyaltyPointRepository loyaltyPointRepository;
    private final UserRepository userRepository;
    private final com.sneakery.store.service.LoyaltyService loyaltyService;
    
    /**
     * Map LoyaltyPoint entity to LoyaltyDto to avoid Hibernate proxy issues
     */
    private LoyaltyDto mapToDto(LoyaltyPoint loyaltyPoint) {
        return LoyaltyDto.builder()
                .id(loyaltyPoint.getId())
                .userId(loyaltyPoint.getUser() != null ? loyaltyPoint.getUser().getId() : null)
                .userName(loyaltyPoint.getUser() != null ? loyaltyPoint.getUser().getFullName() : null)
                .userEmail(loyaltyPoint.getUser() != null ? loyaltyPoint.getUser().getEmail() : null)
                .points(loyaltyPoint.getPoints())
                .transactionType(loyaltyPoint.getTransactionType())
                .description(loyaltyPoint.getDescription())
                .expiresAt(loyaltyPoint.getExpiresAt())
                .createdAt(loyaltyPoint.getCreatedAt())
                .build();
    }

    /**
     * GET /api/admin/loyalty/users
     * Lấy danh sách users với loyalty points
     */
    @GetMapping("/users")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<LoyaltyDto>> getLoyaltyUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("⭐ Fetching loyalty users - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<LoyaltyPoint> points = loyaltyPointRepository.findAllWithUser(pageable);
        
        // Map to DTO - User đã được load cùng lúc nhờ JOIN FETCH
        Page<LoyaltyDto> dtoPage = points.map(this::mapToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * GET /api/admin/loyalty/stats
     * Lấy thống kê loyalty points
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getLoyaltyStats() {
        log.info("⭐ Fetching loyalty stats");
        
        long totalUsers = userRepository.count();
        long totalPointsIssued = loyaltyPointRepository.count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalPointsIssued", totalPointsIssued);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/admin/loyalty/users/{userId}/balance
     * Lấy loyalty points balance của user
     */
    @GetMapping("/users/{userId}/balance")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getUserBalance(@PathVariable Long userId) {
        log.info("⭐ Fetching loyalty balance for user {}", userId);
        
        int balance = loyaltyService.getUserPointsBalance(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("balance", balance);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/admin/loyalty/users/{userId}/adjust
     * Điều chỉnh loyalty points cho user
     */
    @PostMapping("/users/{userId}/adjust")
    @Transactional
    public ResponseEntity<LoyaltyDto> adjustUserPoints(
        @PathVariable Long userId,
        @RequestBody Map<String, Object> request
    ) {
        Integer points = Integer.parseInt(request.get("points").toString());
        String reason = request.getOrDefault("reason", "Admin adjustment").toString();
        
        log.info("⭐ Adjusting loyalty points for user {}: {} points", userId, points);
        
        User user = userRepository.findById(Objects.requireNonNull(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
        loyaltyPoint.setUser(user);
        loyaltyPoint.setPoints(points);
        loyaltyPoint.setTransactionType("adjustment");
        loyaltyPoint.setDescription(reason);
        
        LoyaltyPoint saved = loyaltyPointRepository.save(loyaltyPoint);
        
        return ResponseEntity.ok(mapToDto(saved));
    }
}

