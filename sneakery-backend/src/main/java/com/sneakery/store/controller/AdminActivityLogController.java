package com.sneakery.store.controller;

import com.sneakery.store.dto.ActivityLogDto;
import com.sneakery.store.dto.ActivityLogCreateRequest;
import com.sneakery.store.entity.ActivityLog;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.ActivityLogRepository;
import com.sneakery.store.repository.UserRepository;
import com.sneakery.store.security.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;


/**
 * Admin Activity Log Controller
 * Quản lý nhật ký hoạt động cho admin audit trail
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/activity-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminActivityLogController {

    private final ActivityLogRepository activityLogRepository;
    
    /**
     * Map ActivityLog entity to ActivityLogDto to avoid Hibernate proxy issues
     */
    private ActivityLogDto mapToDto(ActivityLog log) {
        return ActivityLogDto.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .userName(log.getUser() != null ? log.getUser().getFullName() : null)
                .userEmail(log.getUser() != null ? log.getUser().getEmail() : null)
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .createdAt(log.getCreatedAt())
                .build();
    }

    /**
     * GET /api/admin/activity-logs
     * Lấy danh sách activity logs với pagination và filters
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ActivityLogDto>> getAllLogs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) String action,
        @RequestParam(required = false) String entityType
    ) {
        log.info("📋 Fetching activity logs - page: {}, size: {}, userId: {}, action: {}, entityType: {}", 
            page, size, userId, action, entityType);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityLog> logs;
        
        if (userId != null) {
            logs = activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        } else if (action != null && !action.trim().isEmpty()) {
            logs = activityLogRepository.findByActionOrderByCreatedAtDesc(action, pageable);
        } else if (entityType != null && !entityType.trim().isEmpty()) {
            logs = activityLogRepository.findByEntityTypeOrderByCreatedAtDesc(entityType, pageable);
        } else {
            logs = activityLogRepository.findAll(pageable);
        }
        
        // Map to DTO
        Page<ActivityLogDto> dtoPage = logs.map(this::mapToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * GET /api/admin/activity-logs/{id}
     * Lấy chi tiết một activity log
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ActivityLogDto> getLogById(@PathVariable Long id) {
        log.info("📋 Fetching activity log ID: {}", id);
        
        ActivityLog log = activityLogRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Activity log not found with id: " + id));
        
        return ResponseEntity.ok(mapToDto(log));
    }

    /**
     * GET /api/admin/activity-logs/user/{userId}
     * Lấy logs của một user cụ thể
     */
    @GetMapping("/user/{userId}")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ActivityLogDto>> getLogsByUser(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("📋 Fetching activity logs for user ID: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityLog> logs = activityLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        // Map to DTO
        Page<ActivityLogDto> dtoPage = logs.map(this::mapToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * GET /api/admin/activity-logs/entity/{entityType}/{entityId}
     * Lấy logs của một entity cụ thể
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<ActivityLogDto>> getLogsByEntity(
        @PathVariable String entityType,
        @PathVariable Long entityId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        log.info("📋 Fetching activity logs for entity: {} - {}", entityType, entityId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityLog> logs = activityLogRepository
            .findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId, pageable);
        
        // Map to DTO
        Page<ActivityLogDto> dtoPage = logs.map(this::mapToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * DELETE /api/admin/activity-logs/{id}
     * Xóa một activity log (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(@PathVariable Long id) {
        log.info("🗑️ Deleting activity log ID: {}", id);
        
        Long nonNullId = Objects.requireNonNull(id);
        if (!activityLogRepository.existsById(nonNullId)) {
            return ResponseEntity.notFound().build();
        }
        
        activityLogRepository.deleteById(nonNullId);
        return ResponseEntity.ok("Đã xóa nhật ký hoạt động thành công");
    }

    private final UserRepository userRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<ActivityLogDto> createLog(@RequestBody ActivityLogCreateRequest req) {

        ActivityLog log = new ActivityLog();
        log.setAction(req.getAction());
        log.setEntityType(req.getEntityType());
        log.setEntityId(req.getEntityId());
        log.setOldValue(req.getOldValue());
        log.setNewValue(req.getNewValue());

        // Lấy user + IP + user-agent
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            Optional<User> user = userRepository.findById(principal.getId());
            user.ifPresent(log::setUser);
        }

        HttpServletRequest http =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        log.setIpAddress(http.getRemoteAddr());
        log.setUserAgent(http.getHeader("User-Agent"));

        ActivityLog saved = activityLogRepository.save(log);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(saved));
    }

}

