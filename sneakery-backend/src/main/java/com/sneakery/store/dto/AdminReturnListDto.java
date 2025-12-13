package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO: AdminReturnListDto
 * DTO rút gọn cho danh sách returns (pagination)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReturnListDto {
    private Long id;
    
    // Order info
    private Long orderId;
    private String orderNumber;
    
    // Customer info
    private Long userId;
    private String userName;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    // Product info
    private Long productId;
    private String productName;
    private String productImage;
    private String variant;
    private Integer quantity;

    @Schema(description = "Phương thức hoàn tiền")
    private String returnMethod;

    @Schema(description = "Tên ngân hàng")
    private String bankName;

    @Schema(description = "Số tài khoản ngân hàng")
    private String bankAccountNumber;

    @Schema(description = "Tên chủ tài khoản")
    private String bankAccountHolder;

    // Return info
    private String reason;
    private String note;
    private BigDecimal refundAmount;
    private BigDecimal unitPrice;
    private String status;
    
    private LocalDateTime createdAt;
    private List<AdminReturnItemDto> items;
}

