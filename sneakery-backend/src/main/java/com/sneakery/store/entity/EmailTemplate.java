package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Email Template Entity
 * Quản lý templates cho email notifications
 */
@Data
@Entity
@Table(name = "Email_Templates")
public class EmailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "template_name", nullable = false, unique = true)
    private String templateName; // order_confirmation, order_shipped, etc.

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String body; // HTML template with placeholders

    @Column(name = "variables")
    private String variables; // Comma-separated: {name},{order_id},{total}

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

