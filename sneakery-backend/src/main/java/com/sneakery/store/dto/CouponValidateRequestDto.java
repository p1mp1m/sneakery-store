package com.sneakery.store.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponValidateRequestDto {
  private String code;
  private Long subTotal; // subtotal cart (VNĐ)
}