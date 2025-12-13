package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO trả về kết quả import
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImportResultDto {
    private Integer totalRows; // Tổng số dòng đọc được
    private Integer successCount; // Số dòng import thành công
    private Integer errorCount; // Số dòng bị lỗi
    
    @Builder.Default
    private List<ProductImportDto> successItems = new ArrayList<>(); // Danh sách thành công
    
    @Builder.Default
    private List<ProductImportDto> errorItems = new ArrayList<>(); // Danh sách lỗi (kèm errorMessage)
    
    private String message; // Thông báo tổng quan
}

