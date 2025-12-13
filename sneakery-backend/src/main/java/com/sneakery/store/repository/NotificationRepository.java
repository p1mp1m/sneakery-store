package com.sneakery.store.repository;

import com.sneakery.store.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
     * Eager load User để tránh LazyInitializationException
     */
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Đếm unread notifications
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND (n.isRead IS NULL OR n.isRead = FALSE)")
    long countByUserIdAndIsReadFalse(@Param("userId") Long userId);
    
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
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND (n.isRead IS NULL OR n.isRead = FALSE)")
    int markAllAsRead(@Param("userId") Long userId);
    
    /**
     * Tìm notifications theo tiêu đề hoặc message (dành cho admin)
     * Eager load User để tránh LazyInitializationException
     */
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT n FROM Notification n WHERE n.title LIKE CONCAT('%', :title, '%') OR n.message LIKE CONCAT('%', :message, '%') ORDER BY n.createdAt DESC")
    Page<Notification> findByTitleContainingOrMessageContaining(@Param("title") String title, @Param("message") String message, Pageable pageable);
    
    /**
     * Tìm notifications theo type
     * Eager load User để tránh LazyInitializationException
     */
    @EntityGraph(attributePaths = {"user"})
    Page<Notification> findByType(String type, Pageable pageable);
    
    /**
     * Tìm notifications đã đọc
     * Eager load User để tránh LazyInitializationException
     */
    @EntityGraph(attributePaths = {"user"})
    Page<Notification> findByIsReadTrue(Pageable pageable);
    
    /**
     * Tìm notifications chưa đọc
     * Eager load User để tránh LazyInitializationException
     */
    @EntityGraph(attributePaths = {"user"})
    Page<Notification> findByIsReadFalse(Pageable pageable);
    
    /**
     * Lấy tất cả notifications sắp xếp theo created_at DESC
     * Eager load User để tránh LazyInitializationException
     */
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC")
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Đếm số notifications đã đọc
     */
    long countByIsReadTrue();
    
    /**
     * Đếm số notifications chưa đọc
     */
    long countByIsReadFalse();
    
    /**
     * Tìm notification theo ID với eager load User
     * Sử dụng để tránh LazyInitializationException
     */
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT n FROM Notification n WHERE n.id = :id")
    java.util.Optional<Notification> findByIdWithUser(@Param("id") Long id);
}

