package com.sneakery.store.controller;

import com.sneakery.store.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin Settings Controller
 * Quản lý cài đặt hệ thống
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminSettingsController {

    private final SystemSettingsService settingsService;

    /**
     * GET /api/admin/settings
     * Lấy tất cả settings
     */
    @GetMapping
    public ResponseEntity<Map<String, Map<String, Object>>> getAllSettings() {
        log.info("📋 GET /api/admin/settings");
        
        Map<String, Map<String, Object>> settings = settingsService.getAllSettings();
        return ResponseEntity.ok(settings);
    }

    /**
     * GET /api/admin/settings/{type}
     * Lấy settings theo type
     */
    @GetMapping("/{type}")
    public ResponseEntity<Map<String, Object>> getSettingsByType(@PathVariable String type) {
        log.info("📋 GET /api/admin/settings/{}", type);
        
        Map<String, Object> settings = settingsService.getSettingsByType(type);
        return ResponseEntity.ok(settings);
    }

    /**
     * PUT /api/admin/settings/{type}
     * Lưu settings theo type
     */
    @PutMapping("/{type}")
    public ResponseEntity<Map<String, String>> saveSettings(
            @PathVariable String type,
            @RequestBody Map<String, Object> settings
    ) {
        log.info("💾 PUT /api/admin/settings/{}", type);
        
        settingsService.saveSettings(type, settings);
        
        return ResponseEntity.ok(Map.of("message", "Đã lưu cài đặt thành công", "type", type));
    }

    /**
     * PUT /api/admin/settings
     * Lưu tất cả settings (nhóm theo type)
     */
    @PutMapping
    public ResponseEntity<Map<String, String>> saveAllSettings(@RequestBody Map<String, Map<String, Object>> allSettings) {
        log.info("💾 PUT /api/admin/settings (all)");
        
        for (Map.Entry<String, Map<String, Object>> entry : allSettings.entrySet()) {
            settingsService.saveSettings(entry.getKey(), entry.getValue());
        }
        
        return ResponseEntity.ok(Map.of("message", "Đã lưu tất cả cài đặt thành công"));
    }
}

