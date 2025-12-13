package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Order_Status_Histories")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "note", columnDefinition = "NVARCHAR(MAX)")
    private String note; // Ghi chú về thay đổi trạng thái

    // Người thay đổi trạng thái (có thể là admin hoặc system)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User changedBy;

    // Thời điểm thay đổi (mapped từ created_at trong database)
    @Column(name = "created_at")
    private LocalDateTime changedAt;
}