package com.sneakery.store.controller;

import com.sneakery.store.entity.EmailTemplate;
import com.sneakery.store.repository.EmailTemplateRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin Email Template Controller
 * Quản lý email templates cho hệ thống
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/email-templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminEmailTemplateController {

    private final EmailTemplateRepository emailTemplateRepository;

    /**
     * GET /api/admin/email-templates
     * Lấy danh sách email templates
     */
    @GetMapping
    public ResponseEntity<Page<EmailTemplate>> getAllTemplates(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Boolean isActive
    ) {
        log.info("📧 Fetching email templates - page: {}, size: {}, isActive: {}", page, size, isActive);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<EmailTemplate> templates;
        
        if (isActive != null) {
            templates = emailTemplateRepository.findByIsActive(isActive, pageable);
        } else {
            templates = emailTemplateRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(templates);
    }

    /**
     * GET /api/admin/email-templates/{id}
     * Lấy chi tiết một template
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmailTemplate> getTemplateById(@PathVariable Integer id) {
        log.info("📧 Fetching email template ID: {}", id);
        
        EmailTemplate template = emailTemplateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Email template not found with id: " + id));
        
        return ResponseEntity.ok(template);
    }

    /**
     * POST /api/admin/email-templates
     * Tạo mới email template
     */
    @PostMapping
    public ResponseEntity<EmailTemplate> createTemplate(@Valid @RequestBody EmailTemplate template) {
        log.info("📧 Creating email template: {}", template.getTemplateName());
        
        EmailTemplate savedTemplate = emailTemplateRepository.save(template);
        return new ResponseEntity<>(savedTemplate, HttpStatus.CREATED);
    }

    /**
     * PUT /api/admin/email-templates/{id}
     * Cập nhật email template
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmailTemplate> updateTemplate(
        @PathVariable Integer id,
        @Valid @RequestBody EmailTemplate template
    ) {
        log.info("📧 Updating email template ID: {}", id);
        
        if (!emailTemplateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        template.setId(id);
        EmailTemplate updatedTemplate = emailTemplateRepository.save(template);
        return ResponseEntity.ok(updatedTemplate);
    }

    /**
     * DELETE /api/admin/email-templates/{id}
     * Xóa email template
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTemplate(@PathVariable Integer id) {
        log.info("🗑️ Deleting email template ID: {}", id);
        
        if (!emailTemplateRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        emailTemplateRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa email template thành công");
    }

    /**
     * PUT /api/admin/email-templates/{id}/toggle
     * Toggle active status của template
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<EmailTemplate> toggleTemplateStatus(@PathVariable Integer id) {
        log.info("📧 Toggling email template status ID: {}", id);
        
        EmailTemplate template = emailTemplateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Email template not found with id: " + id));
        
        template.setIsActive(!Boolean.TRUE.equals(template.getIsActive()));
        EmailTemplate updatedTemplate = emailTemplateRepository.save(template);
        
        return ResponseEntity.ok(updatedTemplate);
    }

    /**
     * POST /api/admin/email-templates/{id}/send-test
     * Gửi test email với template
     */
    @PostMapping("/{id}/send-test")
    public ResponseEntity<String> sendTestEmail(
        @PathVariable Integer id,
        @RequestBody Map<String, String> testData
    ) {
        log.info("📧 Sending test email with template ID: {}", id);
        
        EmailTemplate template = emailTemplateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Email template not found with id: " + id));
        
        String recipientEmail = testData.getOrDefault("email", "admin@test.com");
        log.info("   To: {}", recipientEmail);
        log.info("   Subject: {}", template.getSubject());
        log.info("   [MOCK EMAIL SENT]");
        
        return ResponseEntity.ok("Test email sent successfully (mock) to: " + recipientEmail);
    }
}

