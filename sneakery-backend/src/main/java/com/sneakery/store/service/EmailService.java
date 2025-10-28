package com.sneakery.store.service;

import com.sneakery.store.entity.EmailTemplate;
import com.sneakery.store.entity.Order;
import com.sneakery.store.repository.EmailTemplateRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class EmailService {

    private final EmailTemplateRepository emailTemplateRepository;
    private final JavaMailSender mailSender;

    public EmailService(EmailTemplateRepository emailTemplateRepository, JavaMailSender mailSender) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.mailSender = mailSender;
    }
    
    @Value("${spring.mail.enabled:false}")
    private boolean emailEnabled;

    @Value("${spring.mail.from:noreply@sneakery.com}")
    private String fromEmail;

    /**
     * Gửi email xác nhận đơn hàng
     */
    public void sendOrderConfirmation(Order order) {
        log.info("📧 [MOCK EMAIL] Sending order confirmation email");
        log.info("   To: {}", order.getUser().getEmail());
        log.info("   Subject: Xác nhận đơn hàng #{}", order.getOrderNumber());
        log.info("   Order ID: {}", order.getId());
        log.info("   Total: {} VND", order.getTotalAmount());
               
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

    public void sendOrderCancelled(Order order, String reason) {
        log.info("❌ [MOCK EMAIL] Sending order cancellation email");
        log.info("   To: {}", order.getUser().getEmail());
        log.info("   Order: {}", order.getOrderNumber());
        log.info("   Reason: {}", reason);
        
        sendEmail(
            order.getUser().getEmail(),
            "order_cancelled",
            Map.of(
                "customer_name", order.getUser().getFullName(),
                "order_id", order.getOrderNumber(),
                "reason", reason != null ? reason : "N/A"
            )
        );
    }

    public void sendPasswordReset(String email, String resetToken) {
        log.info("🔐 [MOCK EMAIL] Sending password reset email");
        log.info("   To: {}", email);
        log.info("   Reset Token: {}", resetToken);
        
        sendEmail(
            email,
            "password_reset",
            Map.of(
                "reset_token", resetToken,
                "reset_link", "http://localhost:5173/reset-password?token=" + resetToken
            )
        );
    }

    public void sendEmailVerification(String email, String verificationToken) {
        log.info("📧 [MOCK EMAIL] Sending email verification");
        log.info("   To: {}", email);
        log.info("   Verification Token: {}", verificationToken);
        
        sendEmail(
            email,
            "email_verification",
            Map.of(
                "verification_token", verificationToken,
                "verification_link", "http://localhost:5173/verify-email?token=" + verificationToken
            )
        );
    }

    private void sendEmail(String to, String templateName, Map<String, String> variables) {
        try {
            EmailTemplate template = emailTemplateRepository
                .findByTemplateNameAndIsActiveTrue(templateName)
                .orElse(null);
            
            if (template == null) {
                log.warn("⚠️ Email template '{}' not found", templateName);
                return;
            }
            
            String body = template.getBody();
            String subject = template.getSubject();
            
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                body = body.replace(placeholder, entry.getValue());
                subject = subject.replace(placeholder, entry.getValue());
            }
            
            if (emailEnabled && mailSender != null) {
                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    
                    helper.setFrom(fromEmail);
                    helper.setTo(to);
                    helper.setSubject(subject);
                    helper.setText(body, true);
                    
                    mailSender.send(message);
                    log.info("✅ Email sent to: {}", to);
                } catch (Exception ex) {
                    log.error("❌ Error sending email: {}", ex.getMessage());
                    logEmailMock(to, subject, body);
                }
            } else {
                logEmailMock(to, subject, body);
            }
            
        } catch (Exception e) {
            log.error("❌ Failed to send email: {}", e.getMessage(), e);
        }
    }
    
    private void logEmailMock(String to, String subject, String body) {
        log.info("📧 [MOCK EMAIL] Sending to: {}", to);
        log.info("   Subject: {}", subject);
        log.info("   Body: {}", body.length() > 100 ? body.substring(0, 100) + "..." : body);
    }
}
