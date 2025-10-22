package com.sneakery.store.repository;

import com.sneakery.store.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository cho Activity Logs (Admin Audit Trail)
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    /**
     * Lấy activity logs theo user
     */
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Lấy activity logs theo entity type và id
     */
    Page<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
        String entityType, 
        Long entityId, 
        Pageable pageable
    );
    
    /**
     * Lấy activity logs theo action type
     */
    Page<ActivityLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);
    
    /**
     * Lấy activity logs trong khoảng thời gian
     */
    Page<ActivityLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
}

