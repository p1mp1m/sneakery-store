package com.sneakery.store.service;

import com.sneakery.store.entity.EmailTemplate;
import com.sneakery.store.entity.Order;
import com.sneakery.store.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Email Service (Mock Implementation)
 * Trong production, tích hợp với SendGrid, AWS SES, hoặc SMTP server
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailTemplateRepository emailTemplateRepository;

    /**
     * Gửi email xác nhận đơn hàng
     */
    public void sendOrderConfirmation(Order order) {
        log.info("📧 [MOCK EMAIL] Sending order confirmation email");
        log.info("   To: {}", order.getUser().getEmail());
        log.info("   Subject: Xác nhận đơn hàng #{}", order.getOrderNumber());
        log.info("   Order ID: {}", order.getId());
        log.info("   Total: {} VND", order.getTotalAmount());
        
        // TODO: In production, send actual email using template
        sendEmail(
            order.getUser().getEmail(),
            "order_confirmation",
            Map.of(
                "customer_name", order.getUser().getFullName(),
                "order_id", order.getOrderNumber(),
                "total", order.getTotalAmount().toString()
            )
        );
    }

    /**
     * Gửi email thông báo đơn hàng đã được giao cho shipper
     */
    public void sendOrderShipped(Order order, String trackingNumber) {
        log.info("📦 [MOCK EMAIL] Sending shipping notification email");
        log.info("   To: {}", order.getUser().getEmail());
        log.info("   Order: {}", order.getOrderNumber());
        log.info("   Tracking: {}", trackingNumber);
        
        sendEmail(
            order.getUser().getEmail(),
            "order_shipped",
            Map.of(
                "customer_name", order.getUser().getFullName(),
                "order_id", order.getOrderNumber(),
                "tracking_number", trackingNumber
            )
        );
    }

    /**
     * Gửi email thông báo đơn hàng đã giao thành công
     */
    public void sendOrderDelivered(Order order) {
        log.info("✅ [MOCK EMAIL] Sending delivery confirmation email");
        log.info("   To: {}", order.getUser().getEmail());
        log.info("   Order: {}", order.getOrderNumber());
        
        sendEmail(
            order.getUser().getEmail(),
            "order_delivered",
            Map.of(
                "customer_name", order.getUser().getFullName(),
                "order_id", order.getOrderNumber()
            )
        );
    }

    /**
     * Gửi email thông báo đơn hàng bị hủy
     */
    public void sendOrderCancelled(Order order, String reason) {
        log.info("❌ [MOCK EMAIL] Sending order cancellation email");
        log.info("   To: {}", order.getUser().getEmail());
        log.info("   Order: {}", order.getOrderNumber());
        log.info("   Reason: {}", reason);
    }

    /**
     * Gửi email reset password
     */
    public void sendPasswordReset(String email, String resetToken) {
        log.info("🔐 [MOCK EMAIL] Sending password reset email");
        log.info("   To: {}", email);
        log.info("   Reset Token: {}", resetToken);
    }

    /**
     * Gửi email xác thực email
     */
    public void sendEmailVerification(String email, String verificationToken) {
        log.info("📧 [MOCK EMAIL] Sending email verification");
        log.info("   To: {}", email);
        log.info("   Verification Token: {}", verificationToken);
    }

    /**
     * Core send email method (sử dụng template)
     */
    private void sendEmail(String to, String templateName, Map<String, String> variables) {
        try {
            EmailTemplate template = emailTemplateRepository
                .findByTemplateNameAndIsActiveTrue(templateName)
                .orElse(null);
            
            if (template != null) {
                String body = template.getBody();
                String subject = template.getSubject();
                
                // Replace variables in template
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    String placeholder = "{" + entry.getKey() + "}";
                    body = body.replace(placeholder, entry.getValue());
                    subject = subject.replace(placeholder, entry.getValue());
                }
                
                log.debug("📧 Email prepared:");
                log.debug("   Subject: {}", subject);
                log.debug("   Body length: {} chars", body.length());
            } else {
                log.warn("⚠️ Email template '{}' not found", templateName);
            }
            
            // TODO: Actual email sending logic here
            // e.g., JavaMailSender, SendGrid API, AWS SES
            
        } catch (Exception e) {
            log.error("❌ Failed to send email: {}", e.getMessage(), e);
        }
    }
}
