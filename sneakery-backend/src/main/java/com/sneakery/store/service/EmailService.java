package com.sneakery.store.service;

import com.sneakery.store.entity.EmailTemplate;
import com.sneakery.store.entity.Order;
import com.sneakery.store.entity.User;
import com.sneakery.store.repository.EmailTemplateRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateRenderer renderer;

    @Value("${spring.mail.enabled:false}")
    private boolean emailEnabled;

    @Value("${spring.mail.from:noreply@sneakery.com}")
    private String fromEmail;

    @Value("${app.web.reset-base-url:http://localhost:5173/reset-password}")
    private String resetBaseUrl;

    @Value("${app.reset.token-expire-minutes:30}")
    private int expireMinutes;

    public EmailService(EmailTemplateRepository emailTemplateRepository, 
                       JavaMailSender mailSender,
                       EmailTemplateRenderer renderer) {
        this.emailTemplateRepository = emailTemplateRepository;
        this.mailSender = mailSender;
        this.renderer = renderer;
    }

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

    public void sendOrderShipped(Order order, String trackingNumber) {
        log.info("📦 [MOCK EMAIL] Sending shipping notification email");
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

    public void sendOrderDelivered(Order order) {
        log.info("✅ [MOCK EMAIL] Sending delivery confirmation email");
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
            
            // Support both {{var}} and {var} placeholder formats
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder1 = "{{" + entry.getKey() + "}}";
                String placeholder2 = "{" + entry.getKey() + "}";
                body = body.replace(placeholder1, entry.getValue()).replace(placeholder2, entry.getValue());
                subject = subject.replace(placeholder1, entry.getValue()).replace(placeholder2, entry.getValue());
            }
            
            log.debug("📧 Email prepared: Subject: {}", subject);
            log.debug("Body length: {} chars", body.length());
            
            if (emailEnabled && mailSender != null) {
                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    
                    helper.setFrom(Objects.requireNonNull(fromEmail));
                    helper.setTo(Objects.requireNonNull(to));
                    helper.setSubject(Objects.requireNonNull(subject));
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

    public void sendResetPasswordEmail(User user, String token) {
        var tpl = emailTemplateRepository.findByTemplateNameAndIsActiveTrue("password_reset")
                .orElseThrow(() -> new IllegalStateException("Missing email template: password_reset"));

        String resetLink = resetBaseUrl + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);

        Map<String, Object> vars = Map.of(
                "full_name", user.getFullName() == null ? user.getEmail() : user.getFullName(),
                "reset_link", resetLink,
                "app_name", "Sneakery Store",
                "support_email", "support@sneakery.com",
                "expire_minutes", expireMinutes,
                "logo_url", "https://i.postimg.cc/V6bHkXtR/logo.png"
        );

        String subject = renderer.render(tpl.getSubject(), vars);
        String html = renderer.render(tpl.getBody(), vars);

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setTo(Objects.requireNonNull(user.getEmail()));
            helper.setSubject(Objects.requireNonNull(subject));
            helper.setText(Objects.requireNonNull(html), true);
            mailSender.send(msg);
            log.info("✅ Reset password email sent to {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("❌ Error sending email: {}", e.getMessage(), e);
        }
    }
}
