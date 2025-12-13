package com.sneakery.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ảnh của product variant")
public class VariantImageDto {

    private Long id;

    private Long variantId;

    private String imageUrl;

    private String altText;

    private Integer displayOrder;

    private String cloudinaryPublicId;
}
