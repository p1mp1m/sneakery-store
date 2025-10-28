package com.sneakery.store.repository;

import com.sneakery.store.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository cho Notifications
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Lấy notifications của user (pagination)
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Đếm unread notifications
     */
    long countByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Mark notification as read
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id AND n.user.id = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * Mark all notifications as read for user
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") Long userId);
    
    /**
     * Tìm notifications theo tiêu đề hoặc message (dành cho admin)
     */
    Page<Notification> findByTitleContainingOrMessageContaining(String title, String message, Pageable pageable);
    
    /**
     * Tìm notifications theo type
     */
    Page<Notification> findByType(String type, Pageable pageable);
    
    /**
     * Tìm notifications đã đọc
     */
    Page<Notification> findByIsReadTrue(Pageable pageable);
    
    /**
     * Tìm notifications chưa đọc
     */
    Page<Notification> findByIsReadFalse(Pageable pageable);
    
    /**
     * Lấy tất cả notifications sắp xếp theo created_at DESC
     */
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Đếm số notifications đã đọc
     */
    long countByIsReadTrue();
    
    /**
     * Đếm số notifications chưa đọc
     */
    long countByIsReadFalse();
}

