package com.sneakery.store.dto;

import lombok.Data;
import java.util.List;

/**
 * Request khi admin xác nhận tình trạng hàng trả về
 */
@Data
public class ConfirmReturnConditionRequest {
    private Long returnRequestId;
    private List<ReturnItemConditionDto> items;
    private String adminNote;
}
