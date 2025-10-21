package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO trả về kết quả bulk update
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBulkUpdateResultDto {
    private Integer totalRequested; // Tổng số sản phẩm yêu cầu cập nhật
    private Integer successCount; // Số sản phẩm cập nhật thành công
    private Integer errorCount; // Số sản phẩm bị lỗi
    
    @Builder.Default
    private List<Long> successIds = new ArrayList<>();
    
    @Builder.Default
    private List<Long> errorIds = new ArrayList<>();
    
    private String message;
}

