package com.sneakery.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
  name = "coupon_usages",
  indexes = {
    @Index(name = "idx_coupon_usage_coupon_user", columnList = "coupon_id,user_id"),
    @Index(name = "idx_coupon_usage_order", columnList = "order_id")
  }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUsage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "coupon_id", nullable = false)
  private Coupon coupon; // coupon được sử dụng

  @Column(name = "user_id", nullable = false)
  private Long userId; // user dùng coupon (auth user)

  @Column(name = "order_id", nullable = false, unique = true)
  private Long orderId; // mỗi order chỉ record 1 lần dùng coupon

  @Column(name = "used_at", nullable = false)
  private LocalDateTime usedAt; // thời điểm dùng
}
