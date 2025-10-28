package com.sneakery.store.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDto {
    private Long id;
    private String method;
    private String status;
    private BigDecimal amount;
    private LocalDateTime paidAt;
    // Tùy chọn: Thêm link thanh toán (nếu là 'online')
    private String paymentUrl; 
}