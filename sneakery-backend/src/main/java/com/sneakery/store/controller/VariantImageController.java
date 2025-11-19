package com.sneakery.store.controller;

import com.sneakery.store.dto.VariantImageDto;
import com.sneakery.store.dto.VariantImageUploadDto;
import com.sneakery.store.service.VariantImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/variant-images")
@RequiredArgsConstructor
public class VariantImageController {

    private final VariantImageService variantImageService;

    @GetMapping("/{variantId}")
    @Operation(summary = "Lấy danh sách ảnh theo variant")
    public ResponseEntity<List<VariantImageDto>> getImages(@PathVariable Long variantId) {
        return ResponseEntity.ok(variantImageService.getImagesByVariant(variantId));
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload ảnh cho variant")
    public ResponseEntity<VariantImageDto> upload(
            @RequestParam Long variantId,
            @RequestParam(required = false) String altText,
            @RequestParam(defaultValue = "0") Integer displayOrder,
            @RequestParam MultipartFile file
    ) {
        VariantImageUploadDto dto = new VariantImageUploadDto();
        dto.setVariantId(variantId);
        dto.setAltText(altText);
        dto.setDisplayOrder(displayOrder);
        dto.setFile(file);

        return ResponseEntity.ok(variantImageService.uploadImage(dto));
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Xóa ảnh theo ID")
    public ResponseEntity<Void> delete(@PathVariable Long imageId) {
        variantImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
