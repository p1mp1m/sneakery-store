package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "DTO upload ảnh cho variant")
public class VariantImageUploadDto {

    private Long variantId;

    private String altText;

    private Integer displayOrder;

    private MultipartFile file; // Upload ảnh
}
