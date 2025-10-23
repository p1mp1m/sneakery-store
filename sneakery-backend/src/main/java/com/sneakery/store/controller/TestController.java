package com.sneakery.store.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TEST CONTROLLER - CHỈ ĐỂ DEBUG
 * XÓA SAU KHI PRODUCTION!
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:5173")
public class TestController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Test endpoint để verify BCrypt password
     * GET /api/test/verify?password=admin123
     */
    @GetMapping("/verify")
    public Map<String, Object> verifyPassword(@RequestParam String password) {
        // Hash từ database
        String dbHash = "$2a$10$eImiTXuWVxfM37uY4JANjOL3PJ/dQMpVKbzsHsvbQKHZ.8UJ2hIqu";
        
        // Test matches
        boolean matches = passwordEncoder.matches(password, dbHash);
        
        // Generate new hash
        String newHash = passwordEncoder.encode(password);
        
        Map<String, Object> result = new HashMap<>();
        result.put("inputPassword", password);
        result.put("dbHash", dbHash);
        result.put("matches", matches);
        result.put("newGeneratedHash", newHash);
        
        return result;
    }

    /**
     * Generate BCrypt hash cho một password
     * GET /api/test/hash?password=admin123
     */
    @GetMapping("/hash")
    public Map<String, Object> generateHash(@RequestParam String password) {
        String hash = passwordEncoder.encode(password);
        
        Map<String, Object> result = new HashMap<>();
        result.put("password", password);
        result.put("bcryptHash", hash);
        result.put("strength", 10);
        
        return result;
    }
}

