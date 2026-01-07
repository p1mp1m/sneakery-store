package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AdminConfirmPaymentResponseDto", description = "Response sau khi admin xác nhận thanh toán")
public class AdminConfirmPaymentResponseDto {

    @Schema(example = "123")
    private Long orderId;

    @Schema(example = "completed")
    private String paymentStatus;

    @Schema(example = "2026-01-07T21:05:12")
    private LocalDateTime paidAt;

    @Schema(example = "bank_transfer")
    private String paymentMethod;
}
