package com.sneakery.store.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductImagePublicDto {
    private Long productId;
    private List<String> images;
}
