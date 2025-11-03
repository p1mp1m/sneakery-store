package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 * Activity Log Entity
 * Comprehensive audit trail cho tất cả admin actions
 */
@Data
@Entity
@Table(name = "Activity_Logs")
@NamedEntityGraph(
    name = "ActivityLog.withUser",
    attributeNodes = @NamedAttributeNode("user")
)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "action", nullable = false)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.

    @Column(name = "entity_type", nullable = false)
    private String entityType; // Product, Order, User, etc.

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "old_value", columnDefinition = "NVARCHAR(MAX)")
    private String oldValue; // JSON representation

    @Column(name = "new_value", columnDefinition = "NVARCHAR(MAX)")
    private String newValue; // JSON representation

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

