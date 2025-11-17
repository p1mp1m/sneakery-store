package com.sneakery.store.dto;

import lombok.Data;

@Data
public class ActivityLogCreateRequest {
    private String action;
    private String entityType;
    private Long entityId;
    private String oldValue;
    private String newValue;
}
