package com.sneakery.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private LocalDateTime timestamp;
    private int status;
    private String error; // Ví dụ: "Not Found", "Bad Request"
    private String message;
    private String path;
    
    // Dùng cho lỗi Validation (xác thực)
    private Map<String, List<String>> validationErrors;
}