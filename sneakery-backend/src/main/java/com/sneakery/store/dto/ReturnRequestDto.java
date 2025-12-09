package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO: ReturnRequestDto
 * Dữ liệu yêu cầu đổi trả
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRequestDto {
    private Long id;
    
    @NotNull(message = "Order ID không được để trống")
    private Long orderId;
    
    private Long userId;
    private String customerName;
    private String customerEmail;
    
    @NotBlank(message = "Lý do đổi trả không được để trống")
    private String reason;
    
    private String status; // pending, approved, rejected, completed
    
    private List<String> images; // Danh sách URL hình ảnh

    private String returnMethod;
    private String bankName;        // Tên ngân hàng
    private String bankAccountNumber; // Số tài khoản ngân hàng
    private String bankAccountHolder; // Chủ tài khoản

    private String adminNote;
    private String approvedByName;
    private LocalDateTime approvedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

