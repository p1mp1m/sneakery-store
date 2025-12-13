package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity: ReturnRequest - Yêu cầu đổi trả
 * Lưu thông tin yêu cầu đổi/trả hàng của khách hàng
 */
@Data
@Entity
@Table(name = "Return_Requests")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    // Quan hệ với User (người yêu cầu)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "reason", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    @Column(name = "status", length = 20)
    private String status; // pending, approved, rejected, completed

    @Column(name = "images_json", columnDefinition = "NVARCHAR(MAX)")
    private String imagesJson; // JSON array of image URLs: ["url1", "url2"]

    // ==== Refund Bank Info ====

    @Column(name = "return_method")
    private String returnMethod; // Luôn là "refund"

    @Column(name = "bank_name")
    private String bankName; // Tên ngân hàng

    @Column(name = "bank_account_number")
    private String bankAccountNumber; // STK

    @Column(name = "bank_account_holder")
    private String bankAccountHolder; // Chủ tài khoản

    @Column(name = "admin_note", columnDefinition = "NVARCHAR(MAX)")
    private String adminNote;

    @Lob
    @Column(name = "item_conditions_json", columnDefinition = "NVARCHAR(MAX)")
    private String itemConditionsJson;

    // getter/setter
    public String getItemConditionsJson() {
        return itemConditionsJson;
    }

    public void setItemConditionsJson(String itemConditionsJson) {
        this.itemConditionsJson = itemConditionsJson;
    }

    @Column(name = "assets_refunded")
    private Boolean assetsRefunded = false;

    // Admin user duyệt request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "pending";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

