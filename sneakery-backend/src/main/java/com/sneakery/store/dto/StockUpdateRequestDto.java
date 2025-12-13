package com.sneakery.store.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class StockUpdateRequestDto {
    
    @NotNull(message = "Số lượng mới không được để trống")
    @PositiveOrZero(message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer newQuantity;
    
    private String reason; // restock, sale, return, adjustment, damaged
    private String note;
}
