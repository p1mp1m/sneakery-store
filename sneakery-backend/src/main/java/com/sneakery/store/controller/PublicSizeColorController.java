package com.sneakery.store.controller;

import com.sneakery.store.dto.ColorDto;
import com.sneakery.store.dto.SizeDto;
import com.sneakery.store.service.ColorService;
import com.sneakery.store.service.SizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Public - Size & Color", description = "API công khai cho size và màu sắc")
public class PublicSizeColorController {

    private final SizeService sizeService;
    private final ColorService colorService;

    @GetMapping("/sizes")
    @Operation(summary = "Lấy danh sách size đang hoạt động")
    public ResponseEntity<List<SizeDto>> getActiveSizes() {
        return ResponseEntity.ok(sizeService.getActiveSizes());
    }

    @GetMapping("/colors")
    @Operation(summary = "Lấy danh sách màu đang hoạt động")
    public ResponseEntity<List<ColorDto>> getActiveColors() {
        return ResponseEntity.ok(colorService.getActiveColors());
    }
}
