package com.sneakery.store.controller;

import com.sneakery.store.dto.ProductImagePublicDto;
import com.sneakery.store.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products/images")
@RequiredArgsConstructor
public class AdminProductImageController {

    private final ProductImageService productImageService;

    /**
     * Admin: lấy toàn bộ ảnh gallery theo product (giống y API public)
     */
    @GetMapping
    public ResponseEntity<List<ProductImagePublicDto>> getAllImagesAdmin() {
        return ResponseEntity.ok(productImageService.getAllImagesPublic());
    }
}
