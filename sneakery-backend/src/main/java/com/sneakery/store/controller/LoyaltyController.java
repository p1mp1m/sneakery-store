package com.sneakery.store.controller;

import com.sneakery.store.entity.LoyaltyPoint;
import com.sneakery.store.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loyalty Points Controller
 * User endpoints cho loyalty points
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loyalty")
@PreAuthorize("isAuthenticated()")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    /**
     * Lấy loyalty points balance
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(Authentication authentication) {
        log.info("📍 GET /api/loyalty/balance");
        
        Long userId = Long.parseLong(authentication.getName());
        int balance = loyaltyService.getUserPointsBalance(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("balance", balance);
        response.put("valueVnd", balance * 1000); // 1 point = 1,000 VND
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy loyalty points transaction history
     */
    @GetMapping("/history")
    public ResponseEntity<List<LoyaltyPoint>> getHistory(Authentication authentication) {
        log.info("📍 GET /api/loyalty/history");
        
        Long userId = Long.parseLong(authentication.getName());
        List<LoyaltyPoint> history = loyaltyService.getUserPointsHistory(userId);
        
        return ResponseEntity.ok(history);
    }
}

