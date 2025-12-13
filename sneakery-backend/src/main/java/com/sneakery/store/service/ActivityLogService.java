package com.sneakery.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sneakery.store.entity.ActivityLog;
import com.sneakery.store.entity.User;
import com.sneakery.store.repository.ActivityLogRepository;
import com.sneakery.store.repository.UserRepository;
import com.sneakery.store.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Service for logging admin activities (audit trail)
 * Logs all CRUD operations and important admin actions
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Log a simple action (backward compatibility)
     * @param type Action type
     * @param message Action message
     */
    public void logAction(String type, String message) {
        log.info("[{}] {}", type, message);
    }

    /**
     * Log CREATE action
     * @param entityType Entity type (e.g., "Product", "Order")
     * @param entityId Entity ID
     * @param newValue New entity value (will be serialized to JSON)
     */
    @Transactional
    public void logCreate(String entityType, Long entityId, Object newValue) {
        logActivity("CREATE", entityType, entityId, null, newValue);
    }

    /**
     * Log UPDATE action
     * @param entityType Entity type
     * @param entityId Entity ID
     * @param oldValue Old entity value
     * @param newValue New entity value
     */
    @Transactional
    public void logUpdate(String entityType, Long entityId, Object oldValue, Object newValue) {
        logActivity("UPDATE", entityType, entityId, oldValue, newValue);
    }

    /**
     * Log DELETE action
     * @param entityType Entity type
     * @param entityId Entity ID
     * @param oldValue Deleted entity value
     */
    @Transactional
    public void logDelete(String entityType, Long entityId, Object oldValue) {
        logActivity("DELETE", entityType, entityId, oldValue, null);
    }

    /**
     * Log custom action
     * @param action Action name (e.g., "APPROVE", "REJECT", "EXPORT")
     * @param entityType Entity type
     * @param entityId Entity ID (optional)
     * @param description Action description
     */
    @Transactional
    public void logCustomAction(String action, String entityType, Long entityId, String description) {
        ActivityLog activityLog = createActivityLog();
        activityLog.setAction(action);
        activityLog.setEntityType(entityType);
        activityLog.setEntityId(entityId);
        activityLog.setNewValue(description);
        
        setUserAndRequestInfo(activityLog);
        
        activityLogRepository.save(activityLog);
        log.debug("✅ Activity logged: {} {} (ID: {})", action, entityType, entityId);
    }

    /**
     * Core method to log activity
     * @param action Action type (CREATE, UPDATE, DELETE, etc.)
     * @param entityType Entity type
     * @param entityId Entity ID
     * @param oldValue Old value (for UPDATE/DELETE)
     * @param newValue New value (for CREATE/UPDATE)
     */
    @Transactional
    public void logActivity(String action, String entityType, Long entityId, Object oldValue, Object newValue) {
        try {
            ActivityLog activityLog = createActivityLog();
            activityLog.setAction(action);
            activityLog.setEntityType(entityType);
            activityLog.setEntityId(entityId);
            
            // Serialize values to JSON
            if (oldValue != null) {
                activityLog.setOldValue(objectMapper.writeValueAsString(oldValue));
            }
            if (newValue != null) {
                activityLog.setNewValue(objectMapper.writeValueAsString(newValue));
            }
            
            setUserAndRequestInfo(activityLog);
            
            activityLogRepository.save(activityLog);
            log.debug("✅ Activity logged: {} {} (ID: {})", action, entityType, entityId);
        } catch (Exception e) {
            // Don't fail the main operation if logging fails
            log.error("❌ Failed to log activity: {} {} (ID: {}): {}", action, entityType, entityId, e.getMessage());
        }
    }

    /**
     * Create new ActivityLog instance
     */
    private ActivityLog createActivityLog() {
        return new ActivityLog();
    }

    /**
     * Set user and request info (IP, User-Agent) from current context
     */
    private void setUserAndRequestInfo(ActivityLog activityLog) {
        // Get current user from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Long userId = userPrincipal.getId();
            if (userId != null) {
                Optional<User> user = userRepository.findById(userId);
                user.ifPresent(activityLog::setUser);
            }
        }

        // Get request info (IP, User-Agent)
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                activityLog.setIpAddress(getClientIpAddress(request));
                activityLog.setUserAgent(request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            log.warn("Could not extract request info: {}", e.getMessage());
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
