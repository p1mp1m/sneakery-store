package com.sneakery.store.controller;

import com.sneakery.store.dto.*;
import com.sneakery.store.service.AdminProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/product-variants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminProductVariantController {

    private final AdminProductVariantService adminProductVariantService;

    /**
     * Lấy danh sách biến thể sản phẩm (phân trang) với filter
     */
    @GetMapping
    public ResponseEntity<Page<AdminProductVariantDto>> getAllVariants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String variantSize,
            @RequestParam(required = false) String stockStatus,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        Pageable pageable;
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc")) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(page, size, Sort.by(direction, mapSortField(sortBy)));
        } else {
            pageable = PageRequest.of(page, size);
        }
        
        ProductVariantFilterDto filter = ProductVariantFilterDto.builder()
                .search(search)
                .color(color)
                .size(variantSize)
                .stockStatus(stockStatus)
                .productId(productId)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        Page<AdminProductVariantDto> variantPage = adminProductVariantService.getVariantsWithFilter(filter, pageable);
        return ResponseEntity.ok(variantPage);
    }
    
    private String mapSortField(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "sku": return "sku";
            case "size": return "size";
            case "color": return "color";
            case "price": return "priceBase";
            case "stock": return "stockQuantity";
            case "created_at": return "createdAt";
            default: return "id";
        }
    }

    /**
     * Lấy chi tiết 1 biến thể
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminProductVariantDto> getVariantById(@PathVariable Long id) {
        AdminProductVariantDto variant = adminProductVariantService.getVariantById(id);
        return ResponseEntity.ok(variant);
    }

    /**
     * Tạo biến thể mới
     */
    @PostMapping
    public ResponseEntity<AdminProductVariantDto> createVariant(
            @Valid @RequestBody AdminProductVariantRequestDto requestDto
    ) {
        AdminProductVariantDto newVariant = adminProductVariantService.createVariant(requestDto);
        return new ResponseEntity<>(newVariant, HttpStatus.CREATED);
    }

    /**
     * Cập nhật biến thể
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdminProductVariantDto> updateVariant(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductVariantRequestDto requestDto
    ) {
        AdminProductVariantDto updatedVariant = adminProductVariantService.updateVariant(id, requestDto);
        return ResponseEntity.ok(updatedVariant);
    }

    /**
     * Xóa biến thể
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVariant(@PathVariable Long id) {
        adminProductVariantService.deleteVariant(id);
        return ResponseEntity.ok("Đã xóa biến thể thành công");
    }

    /**
     * Cập nhật tồn kho
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<AdminProductVariantDto> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequestDto requestDto
    ) {
        AdminProductVariantDto updatedVariant = adminProductVariantService.updateStock(id, requestDto);
        return ResponseEntity.ok(updatedVariant);
    }

    /**
     * Lấy thống kê biến thể
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProductVariantStatsDto> getVariantStatistics() {
        ProductVariantStatsDto stats = adminProductVariantService.getVariantStatistics();
        return ResponseEntity.ok(stats);
    }
}
