package com.sneakery.store.controller;

import com.sneakery.store.dto.VariantImageDto;
import com.sneakery.store.service.VariantImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PUBLIC API - Ảnh Variant cho FE (Product Detail, Cart, QuickView, POS...)
 *
 * Endpoint:
 *   GET /api/variant-images/{variantId}
 *
 * Không yêu cầu đăng nhập, không có upload/delete.
 */
@RestController
@RequestMapping("/api/variant-images")
@RequiredArgsConstructor
public class PublicVariantImageController {

    private final VariantImageService variantImageService;

    @GetMapping("/{variantId}")
    @Operation(summary = "Lấy danh sách ảnh của Variant (PUBLIC)")
    public ResponseEntity<List<VariantImageDto>> getVariantImages(
            @PathVariable Long variantId
    ) {
        return ResponseEntity.ok(variantImageService.getImagesByVariant(variantId));
    }
}
