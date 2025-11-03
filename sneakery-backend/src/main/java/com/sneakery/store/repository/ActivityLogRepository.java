package com.sneakery.store.repository;

import com.sneakery.store.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repository cho Activity Logs (Admin Audit Trail)
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    /**
     * Lấy activity logs trong khoảng thời gian
     */
    Page<ActivityLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );

    /**
     * Lấy tất cả activity logs với User được load cùng lúc thông qua EntityGraph
     */
    @EntityGraph(value = "ActivityLog.withUser", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    @NonNull
    Page<ActivityLog> findAll(@NonNull Pageable pageable);

    /**
     * Lấy activity logs theo user với User được load cùng lúc
     */
    @EntityGraph(value = "ActivityLog.withUser", type = EntityGraph.EntityGraphType.FETCH)
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Lấy activity logs theo action với User được load cùng lúc
     */
    @EntityGraph(value = "ActivityLog.withUser", type = EntityGraph.EntityGraphType.FETCH)
    Page<ActivityLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

    /**
     * Lấy activity logs theo entity type với User được load cùng lúc
     */
    @EntityGraph(value = "ActivityLog.withUser", type = EntityGraph.EntityGraphType.FETCH)
    Page<ActivityLog> findByEntityTypeOrderByCreatedAtDesc(String entityType, Pageable pageable);

    /**
     * Lấy activity logs theo entity type và id với User được load cùng lúc
     */
    @EntityGraph(value = "ActivityLog.withUser", type = EntityGraph.EntityGraphType.FETCH)
    Page<ActivityLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
        String entityType,
        Long entityId,
        Pageable pageable
    );

    /**
     * Lấy một activity log theo ID với User được load cùng lúc
     */
    @EntityGraph(value = "ActivityLog.withUser", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    @NonNull
    java.util.Optional<ActivityLog> findById(@NonNull Long id);
}

