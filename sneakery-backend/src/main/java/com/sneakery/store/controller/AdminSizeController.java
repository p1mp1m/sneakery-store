package com.sneakery.store.controller;

import com.sneakery.store.dto.SizeDto;
import com.sneakery.store.dto.SizeRequestDto;
import com.sneakery.store.service.SizeService;
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
@RequestMapping("/api/admin/sizes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Size Management", description = "API quản lý kích thước sản phẩm")
public class AdminSizeController {

    private final SizeService sizeService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả size")
    public ResponseEntity<List<SizeDto>> getAllSizes() {
        return ResponseEntity.ok(sizeService.getAllSizes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết size theo ID")
    public ResponseEntity<SizeDto> getSizeById(@PathVariable Integer id) {
        return ResponseEntity.ok(sizeService.getSizeById(id));
    }

    @PostMapping
    @Operation(summary = "Tạo size mới")
    public ResponseEntity<SizeDto> createSize(@Valid @RequestBody SizeRequestDto requestDto) {
        SizeDto createdSize = sizeService.createSize(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSize);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật size")
    public ResponseEntity<SizeDto> updateSize(
            @PathVariable Integer id,
            @Valid @RequestBody SizeRequestDto requestDto
    ) {
        return ResponseEntity.ok(sizeService.updateSize(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa size")
    public ResponseEntity<Void> deleteSize(@PathVariable Integer id) {
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Bật/tắt trạng thái hoạt động của size")
    public ResponseEntity<SizeDto> toggleActive(@PathVariable Integer id) {
        return ResponseEntity.ok(sizeService.toggleActive(id));
    }
}
