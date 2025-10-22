package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nhiều Review cho 1 Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

    // Nhiều Review từ 1 User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    // Liên kết với Order để verify purchase
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1 đến 5

    @Column(name = "title")
    private String title;

    @Column(name = "body", columnDefinition = "NVARCHAR(MAX)")
    private String body; // Nội dung review

    // V3.1 - Multiple images support
    @Column(name = "image_url_1")
    private String imageUrl1;

    @Column(name = "image_url_2")
    private String imageUrl2;

    @Column(name = "image_url_3")
    private String imageUrl3;

    @Column(name = "images_json", columnDefinition = "NVARCHAR(MAX)")
    private String imagesJson; // Alternative: JSON array

    // Moderation
    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase;

    // V3.1 - Engagement tracking
    @Column(name = "helpful_count")
    private Integer helpfulCount;

    @Column(name = "unhelpful_count")
    private Integer unhelpfulCount;

    // V3.1 - Admin reply
    @Column(name = "reply_text", columnDefinition = "NVARCHAR(MAX)")
    private String replyText;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replied_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User repliedBy;

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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}