package com.sneakery.store.service;

import com.sneakery.store.entity.Order;
import com.sneakery.store.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service: EmailService (Mock)
 * Gửi email thông báo cho khách hàng
 * TODO: Tích hợp SMTP server thật khi deploy production
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    /**
     * Gửi email xác nhận đơn hàng
     */
    public void sendOrderConfirmationEmail(Order order) {
        log.info("📧 [MOCK] Sending order confirmation email to: {}", order.getUser().getEmail());
        log.info("Order ID: {}, Total: {}", order.getId(), order.getTotalAmount());
        
        // TODO: Implement real email sending logic
        // Example: use JavaMailSender or third-party service (SendGrid, AWS SES)
        
        String emailBody = buildOrderConfirmationEmail(order);
        log.debug("Email body: \n{}", emailBody);
    }

    /**
     * Gửi email thông báo đơn hàng đã được giao cho ĐVVC
     */
    public void sendOrderShippedEmail(Order order, String trackingNumber) {
        log.info("📧 [MOCK] Sending order shipped email to: {}", order.getUser().getEmail());
        log.info("Order ID: {}, Tracking: {}", order.getId(), trackingNumber);
        
        String emailBody = buildOrderShippedEmail(order, trackingNumber);
        log.debug("Email body: \n{}", emailBody);
    }

    /**
     * Gửi email thông báo đơn hàng đã giao thành công
     */
    public void sendOrderDeliveredEmail(Order order) {
        log.info("📧 [MOCK] Sending order delivered email to: {}", order.getUser().getEmail());
        
        String emailBody = buildOrderDeliveredEmail(order);
        log.debug("Email body: \n{}", emailBody);
    }

    /**
     * Gửi email thông báo đơn hàng bị hủy
     */
    public void sendOrderCancelledEmail(Order order, String reason) {
        log.info("📧 [MOCK] Sending order cancelled email to: {}", order.getUser().getEmail());
        
        String emailBody = buildOrderCancelledEmail(order, reason);
        log.debug("Email body: \n{}", emailBody);
    }

    /**
     * Gửi email chào mừng user mới
     */
    public void sendWelcomeEmail(User user) {
        log.info("📧 [MOCK] Sending welcome email to: {}", user.getEmail());
        
        String emailBody = buildWelcomeEmail(user);
        log.debug("Email body: \n{}", emailBody);
    }

    // ===== Email Templates =====

    private String buildOrderConfirmationEmail(Order order) {
        return String.format("""
                Xin chào %s,
                
                Cảm ơn bạn đã đặt hàng tại Sneakery Store!
                
                Mã đơn hàng: #%d
                Tổng tiền: %,.0f VND
                Trạng thái: %s
                
                Chúng tôi sẽ xử lý đơn hàng của bạn trong thời gian sớm nhất.
                
                Trân trọng,
                Sneakery Team
                """,
                order.getUser().getFullName(),
                order.getId(),
                order.getTotalAmount(),
                order.getStatus()
        );
    }

    private String buildOrderShippedEmail(Order order, String trackingNumber) {
        return String.format("""
                Xin chào %s,
                
                Đơn hàng #%d của bạn đã được giao cho đơn vị vận chuyển.
                
                Mã vận đơn: %s
                
                Bạn có thể theo dõi đơn hàng qua mã vận đơn này.
                
                Trân trọng,
                Sneakery Team
                """,
                order.getUser().getFullName(),
                order.getId(),
                trackingNumber != null ? trackingNumber : "Chưa cập nhật"
        );
    }

    private String buildOrderDeliveredEmail(Order order) {
        return String.format("""
                Xin chào %s,
                
                Đơn hàng #%d của bạn đã được giao thành công!
                
                Cảm ơn bạn đã mua sắm tại Sneakery Store.
                Hãy để lại đánh giá cho sản phẩm để giúp chúng tôi cải thiện dịch vụ nhé.
                
                Trân trọng,
                Sneakery Team
                """,
                order.getUser().getFullName(),
                order.getId()
        );
    }

    private String buildOrderCancelledEmail(Order order, String reason) {
        return String.format("""
                Xin chào %s,
                
                Đơn hàng #%d của bạn đã bị hủy.
                
                Lý do: %s
                
                Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.
                
                Trân trọng,
                Sneakery Team
                """,
                order.getUser().getFullName(),
                order.getId(),
                reason != null ? reason : "Không có lý do cụ thể"
        );
    }

    private String buildWelcomeEmail(User user) {
        return String.format("""
                Xin chào %s,
                
                Chào mừng bạn đến với Sneakery Store!
                
                Cảm ơn bạn đã đăng ký tài khoản. Hãy khám phá bộ sưu tập giày sneaker 
                chất lượng cao của chúng tôi.
                
                Chúc bạn mua sắm vui vẻ!
                
                Trân trọng,
                Sneakery Team
                """,
                user.getFullName()
        );
    }
}

