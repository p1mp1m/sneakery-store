package com.sneakery.store.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO cho bulk update sản phẩm
 * Cho phép cập nhật hàng loạt status, brand, hoặc category
 */
@Data
public class ProductBulkUpdateRequestDto {
    @NotEmpty(message = "Danh sách ID sản phẩm không được để trống")
    private List<Long> productIds;
    
    // Chọn 1 trong các action sau
    private String action; // "UPDATE_STATUS", "UPDATE_BRAND", "ADD_CATEGORY", "REMOVE_CATEGORY"
    
    // Giá trị tương ứng với action
    private Boolean isActive; // Cho UPDATE_STATUS
    private Integer brandId; // Cho UPDATE_BRAND
    private Integer categoryId; // Cho ADD_CATEGORY hoặc REMOVE_CATEGORY
}

