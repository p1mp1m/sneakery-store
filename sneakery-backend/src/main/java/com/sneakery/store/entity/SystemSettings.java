package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * System Settings Entity
 * Lưu trữ cài đặt hệ thống trong database
 */
@Data
@Entity
@Table(name = "System_Settings")
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey; // store.name, general.currency, email.smtpHost, etc.

    @Column(name = "setting_value", columnDefinition = "NVARCHAR(MAX)")
    private String settingValue; // JSON string hoặc plain text

    @Column(name = "setting_type", length = 50)
    private String settingType; // store, general, email, payment, theme

    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

