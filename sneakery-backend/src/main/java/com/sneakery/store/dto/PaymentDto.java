package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private String transactionId;
    private String gatewayResponse;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}