package com.sneakery.store.service;

import com.sneakery.store.entity.EmailTemplate;
import com.sneakery.store.entity.Order;
import com.sneakery.store.entity.User;
import com.sneakery.store.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateRepository emailTemplateRepository;
    private final EmailTemplateRenderer renderer;

    @Value("${app.web.reset-base-url}")
    private String resetBaseUrl;

    @Value("${app.reset.token-expire-minutes:30}")
    private int expireMinutes;

    public void sendOrderConfirmation(Order order) {
        log.info("📧 [MOCK EMAIL] Sending order confirmation email");
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
    }

    public void sendPasswordReset(String email, String resetToken) {
        log.info("🔐 [MOCK EMAIL] Sending password reset email");
        log.info("   To: {}", email);
        log.info("   Reset Token: {}", resetToken);
    }

    public void sendEmailVerification(String email, String verificationToken) {
        log.info("📧 [MOCK EMAIL] Sending email verification");
        log.info("   To: {}", email);
        log.info("   Verification Token: {}", verificationToken);
    }

    private void sendEmail(String to, String templateName, Map<String, String> variables) {
        try {
            EmailTemplate template = emailTemplateRepository
                    .findByTemplateNameAndIsActiveTrue(templateName)
                    .orElse(null);

            if (template != null) {
                String body = template.getBody();
                String subject = template.getSubject();

                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    String placeholder = "{{" + entry.getKey() + "}}";
                    body = body.replace(placeholder, entry.getValue());
                    subject = subject.replace(placeholder, entry.getValue());
                }

                log.debug("📧 Email prepared: Subject: {}", subject);
                log.debug("Body length: {} chars", body.length());
            } else {
                log.warn("⚠️ Email template '{}' not found", templateName);
            }

        } catch (Exception e) {
            log.error("❌ Failed to send email: {}", e.getMessage(), e);
        }
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
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(msg);
            log.info("✅ Reset password email sent to {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("❌ Error sending email: {}", e.getMessage(), e);
        }
    }
}