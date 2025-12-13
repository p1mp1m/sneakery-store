package com.sneakery.store.controller;

import com.sneakery.store.dto.ProductImagePublicDto;
import com.sneakery.store.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * PublicProductImageController
 * ----------------------------
 * API PUBLIC trả về toàn bộ ảnh gallery theo product.
 * FE dùng để load tất cả ảnh và đổi ảnh khi hover.
 */
@RestController
@RequestMapping("/api/products/images")
@RequiredArgsConstructor
public class PublicProductImageController {

    private final ProductImageService productImageService;

    @GetMapping
    public ResponseEntity<List<ProductImagePublicDto>> getAllImages() {
        return ResponseEntity.ok(productImageService.getAllImagesPublic());
    }
}
