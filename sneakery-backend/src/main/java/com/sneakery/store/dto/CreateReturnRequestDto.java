package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO: CreateReturnRequestDto
 * Dữ liệu request tạo yêu cầu đổi trả
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReturnRequestDto {
    
    // Note: orderId is passed as path variable, not in request body
    
    @NotBlank(message = "Lý do đổi trả không được để trống")
    private String reason;
    
    private String note; // Ghi chú của user (không bắt buộc)
    
    private List<String> images; // Danh sách URL hình ảnh (không bắt buộc)

    // 🔥 Thông tin ngân hàng — required cho hoàn tiền
    @NotBlank(message = "Tên ngân hàng không được để trống")
    private String bankName;

    @NotBlank(message = "Số tài khoản không được để trống")
    private String bankAccountNumber;

    @NotBlank(message = "Tên chủ tài khoản không được để trống")
    private String bankAccountHolder;
}

