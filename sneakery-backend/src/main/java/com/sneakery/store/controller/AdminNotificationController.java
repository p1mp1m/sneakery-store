package com.sneakery.store.controller;

import com.sneakery.store.dto.NotificationDto;
import com.sneakery.store.entity.Notification;
import com.sneakery.store.exception.NotificationNotFoundException;
import com.sneakery.store.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Admin Notification Controller
 * Quản lý notifications cho admin
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminNotificationController {

    private final NotificationRepository notificationRepository;
    
    /**
     * Map entity sang DTO để tránh lỗi Hibernate proxy
     */
    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .link(notification.getLink())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .userName(notification.getUser() != null ? notification.getUser().getFullName() : null)
                .userEmail(notification.getUser() != null ? notification.getUser().getEmail() : null)
                .build();
    }

    /**
     * Lấy tất cả notifications (phân trang)
     */
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<Page<NotificationDto>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status
    ) {
        log.info("📍 GET /api/admin/notifications?page={}&size={}&search={}&type={}&status={}", 
                page, size, search, type, status);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications;
        
        // Nếu có filter, search, type hoặc status
        if (search != null && !search.isEmpty()) {
            notifications = notificationRepository.findByTitleContainingOrMessageContaining(
                search, search, pageable
            );
        } else if (type != null && !type.isEmpty()) {
            notifications = notificationRepository.findByType(type, pageable);
        } else if (status != null && status.equals("read")) {
            notifications = notificationRepository.findByIsReadTrue(pageable);
        } else if (status != null && status.equals("unread")) {
            notifications = notificationRepository.findByIsReadFalse(pageable);
        } else {
            notifications = notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        // Map sang DTO
        Page<NotificationDto> dtoPage = notifications.map(this::mapToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Lấy thống kê notifications
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("📍 GET /api/admin/notifications/stats");
        
        Map<String, Object> stats = new HashMap<>();
        long totalNotifications = notificationRepository.count();
        long readCount = notificationRepository.countByIsReadTrue();
        long unreadCount = notificationRepository.countByIsReadFalse();
        
        stats.put("totalNotifications", totalNotifications);
        stats.put("readCount", readCount);
        stats.put("unreadCount", unreadCount);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Lấy notification theo ID
     */
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getNotificationById(@PathVariable Long id) {
        log.info("📍 GET /api/admin/notifications/{}", id);
        
        // Sử dụng method với @EntityGraph để eager load User
        Notification notification = notificationRepository.findByIdWithUser(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        
        return ResponseEntity.ok(mapToDto(notification));
    }

    /**
     * Xóa notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        log.info("📍 DELETE /api/admin/notifications/{}", id);
        
        notificationRepository.deleteById(Objects.requireNonNull(id));
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đã xóa thông báo thành công");
        
        return ResponseEntity.ok(response);
    }
}

