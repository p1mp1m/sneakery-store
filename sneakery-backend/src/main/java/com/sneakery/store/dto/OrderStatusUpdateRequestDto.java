package com.sneakery.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusUpdateRequestDto {
    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // Ví dụ: "Processing", "Shipped", "Completed", "Cancelled"
}