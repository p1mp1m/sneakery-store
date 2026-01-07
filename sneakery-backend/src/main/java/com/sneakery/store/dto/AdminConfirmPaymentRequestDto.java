package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AdminConfirmPaymentRequestDto", description = "Request admin xác nhận thanh toán")
public class AdminConfirmPaymentRequestDto {

    @NotBlank
    @Schema(description = "Trạng thái thanh toán muốn set", example = "completed", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentStatus;

    @Schema(description = "Ghi chú admin (optional)", example = "Đã nhận chuyển khoản từ khách ngày 2026-01-07")
    private String adminNote;
}
