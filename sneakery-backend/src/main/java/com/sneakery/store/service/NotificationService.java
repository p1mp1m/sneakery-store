package com.sneakery.store.service;

import com.sneakery.store.entity.Notification;
import com.sneakery.store.entity.Order;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.NotificationRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notification Service
 * Quản lý real-time notifications cho users
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Lấy notifications của user (phân trang)
     */
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        log.info("Fetching notifications for user ID: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Đếm unread notifications
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.info("Marking notification {} as read for user {}", notificationId, userId);
        
        int updated = notificationRepository.markAsRead(notificationId, userId);
        
        if (updated == 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Notification không tồn tại hoặc không thuộc về user này");
        }
    }

    /**
     * Mark all notifications as read
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user {}", userId);
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * Tạo notification cho user
     */
    @Transactional
    public Notification createNotification(Long userId, String type, String title, String message, String link) {
        log.info("Creating notification for user {}: {}", userId, title);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setLink(link);
        
        notification = notificationRepository.save(notification);
        
        log.info("Created notification ID: {}", notification.getId());
        return notification;
    }

    /**
     * Notify order status change
     */
    @Transactional
    public void notifyOrderStatusChange(Order order) {
        String title = getOrderStatusTitle(order.getStatus());
        String message = getOrderStatusMessage(order);
        String link = "/user/orders/" + order.getId();
        
        createNotification(
            order.getUser().getId(),
            "order_status",
            title,
            message,
            link
        );
    }

    /**
     * Notify product restock
     */
    @Transactional
    public void notifyProductRestock(Long userId, String productName, String productSlug) {
        createNotification(
            userId,
            "product_restock",
            "Sản phẩm đã có hàng trở lại",
            String.format("'%s' đã có hàng. Đặt ngay!", productName),
            "/products/" + productSlug
        );
    }

    /**
     * Notify promotion
     */
    @Transactional
    public void notifyPromotion(Long userId, String title, String message) {
        createNotification(
            userId,
            "promotion",
            title,
            message,
            "/promotions"
        );
    }

    /**
     * Notify review reply
     */
    @Transactional
    public void notifyReviewReply(Long userId, Long productId) {
        createNotification(
            userId,
            "review_reply",
            "Shop đã trả lời đánh giá của bạn",
            "Xem phản hồi từ shop về đánh giá sản phẩm của bạn",
            "/products/" + productId
        );
    }

    /**
     * Get order status title cho notification
     */
    private String getOrderStatusTitle(String status) {
        return switch (status) {
            case "confirmed" -> "Đơn hàng đã được xác nhận";
            case "processing" -> "Đơn hàng đang được xử lý";
            case "packed" -> "Đơn hàng đã được đóng gói";
            case "shipped" -> "Đơn hàng đang giao đến bạn";
            case "delivered" -> "Đơn hàng đã giao thành công";
            case "cancelled" -> "Đơn hàng đã bị hủy";
            case "refunded" -> "Đơn hàng đã được hoàn tiền";
            default -> "Cập nhật trạng thái đơn hàng";
        };
    }

    /**
     * Get order status message cho notification
     */
    private String getOrderStatusMessage(Order order) {
        return switch (order.getStatus()) {
            case "confirmed" -> String.format("Đơn hàng %s đã được xác nhận và sẽ sớm được xử lý.", order.getOrderNumber());
            case "shipped" -> String.format("Đơn hàng %s đang trên đường giao đến bạn. Mã vận đơn: %s", 
                order.getOrderNumber(), order.getTrackingNumber());
            case "delivered" -> String.format("Đơn hàng %s đã được giao thành công. Cảm ơn bạn đã mua hàng!", 
                order.getOrderNumber());
            case "cancelled" -> String.format("Đơn hàng %s đã bị hủy.", order.getOrderNumber());
            default -> String.format("Đơn hàng %s đã được cập nhật.", order.getOrderNumber());
        };
    }
}

