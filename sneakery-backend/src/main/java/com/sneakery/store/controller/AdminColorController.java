package com.sneakery.store.controller;

import com.sneakery.store.dto.ColorDto;
import com.sneakery.store.dto.ColorRequestDto;
import com.sneakery.store.service.ColorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/colors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Color Management", description = "API quản lý màu sắc sản phẩm")
public class AdminColorController {

    private final ColorService colorService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả màu")
    public ResponseEntity<List<ColorDto>> getAllColors() {
        return ResponseEntity.ok(colorService.getAllColors());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết màu theo ID")
    public ResponseEntity<ColorDto> getColorById(@PathVariable Integer id) {
        return ResponseEntity.ok(colorService.getColorById(id));
    }

    @PostMapping
    @Operation(summary = "Tạo màu mới")
    public ResponseEntity<ColorDto> createColor(@Valid @RequestBody ColorRequestDto requestDto) {
        ColorDto createdColor = colorService.createColor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdColor);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật màu")
    public ResponseEntity<ColorDto> updateColor(
            @PathVariable Integer id,
            @Valid @RequestBody ColorRequestDto requestDto
    ) {
        return ResponseEntity.ok(colorService.updateColor(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa màu")
    public ResponseEntity<Void> deleteColor(@PathVariable Integer id) {
        colorService.deleteColor(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Bật/tắt trạng thái hoạt động của màu")
    public ResponseEntity<ColorDto> toggleActive(@PathVariable Integer id) {
        return ResponseEntity.ok(colorService.toggleActive(id));
    }
}
