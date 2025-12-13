package com.sneakery.store.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TEST CONTROLLER - CHỈ ĐỂ DEBUG
 * Chỉ hoạt động trong môi trường development (profile: dev)
 * Tự động disable trong production
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@Profile("dev")
@RequiredArgsConstructor
public class TestController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Autowired(required = false)
    private Cloudinary cloudinary;

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

    /**
     * Test Cloudinary connection và cấu hình
     * GET /api/test/cloudinary
     */
    @GetMapping("/cloudinary")
    public Map<String, Object> testCloudinary() {
        Map<String, Object> result = new HashMap<>();
        
        // Kiểm tra Cloudinary bean
        if (cloudinary == null) {
            result.put("status", "ERROR");
            result.put("message", "Cloudinary bean chưa được khởi tạo");
            result.put("configured", false);
            result.put("suggestion", "Kiểm tra lại cấu hình trong application.properties");
            return result;
        }
        
        result.put("configured", true);
        result.put("status", "CHECKING");
        
        try {
            // Test kết nối bằng cách lấy thông tin account
            ApiResponse accountInfo = cloudinary.api().ping(new HashMap<>());
            
            result.put("status", "SUCCESS");
            result.put("message", "Cloudinary đã được cấu hình và kết nối thành công!");
            result.put("connection", "OK");
            
            Map<String, Object> accountInfoMap = new HashMap<>();
            accountInfoMap.put("status", accountInfo.get("status") != null ? accountInfo.get("status").toString() : "unknown");
            accountInfoMap.put("cloudName", cloudinary.config.cloudName);
            result.put("accountInfo", accountInfoMap);
            
            log.info("✅ Cloudinary test thành công!");
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Không thể kết nối đến Cloudinary");
            result.put("connection", "FAILED");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            
            log.error("❌ Cloudinary test thất bại: {}", e.getMessage(), e);
        }
        
        return result;
    }
}

