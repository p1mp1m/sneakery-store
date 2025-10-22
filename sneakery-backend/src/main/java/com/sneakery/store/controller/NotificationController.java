package com.sneakery.store.controller;

import com.sneakery.store.entity.Notification;
import com.sneakery.store.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Notification Controller
 * User notifications management
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Lấy notifications của user (pagination)
     */
    @GetMapping
    public ResponseEntity<Page<Notification>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        log.info("📍 GET /api/notifications");
        
        Long userId = Long.parseLong(authentication.getName());
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Đếm unread notifications
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        log.info("📍 GET /api/notifications/unread-count");
        
        Long userId = Long.parseLong(authentication.getName());
        long unreadCount = notificationService.getUnreadCount(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", unreadCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("📍 PUT /api/notifications/{}/read", id);
        
        Long userId = Long.parseLong(authentication.getName());
        notificationService.markAsRead(id, userId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all notifications as read
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        log.info("📍 PUT /api/notifications/read-all");
        
        Long userId = Long.parseLong(authentication.getName());
        notificationService.markAllAsRead(userId);
        
        return ResponseEntity.ok().build();
    }
}

