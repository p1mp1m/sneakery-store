package com.sneakery.store.controller;

import com.sneakery.store.dto.*;
import com.sneakery.store.service.AdminProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ tất cả API
@CrossOrigin(origins = "http://localhost:5173")
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * Tạo sản phẩm mới
     */
    @PostMapping
    public ResponseEntity<AdminProductDetailDto> createProduct(
            @Valid @RequestBody AdminProductRequestDto requestDto
    ) {
        AdminProductDetailDto newProduct = adminProductService.createProduct(requestDto);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    /**
     * Lấy danh sách sản phẩm (phân trang) với ADVANCED FILTER
     */
    @GetMapping
    public ResponseEntity<Page<AdminProductListDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) String stockLevel,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Build advanced filter DTO
        ProductAdvancedFilterDto filter = ProductAdvancedFilterDto.builder()
                .search(search)
                .brandId(brandId)
                .status(status)
                .categoryId(categoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .stockLevel(stockLevel)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        Page<AdminProductListDto> productPage = adminProductService.getProductsWithAdvancedFilter(filter, pageable);
        return ResponseEntity.ok(productPage);
    }

    /**
     * Lấy chi tiết 1 sản phẩm (cho trang Edit)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDetailDto> getProductById(@PathVariable Long id) {
        AdminProductDetailDto product = adminProductService.getProductByIdForAdmin(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Cập nhật 1 sản phẩm
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdminProductDetailDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductRequestDto requestDto
    ) {
        AdminProductDetailDto updatedProduct = adminProductService.updateProduct(id, requestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Xóa 1 sản phẩm
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.ok("Đã xóa sản phẩm thành công");
    }

    // =================================================================
    // CÁC API NÂNG CAO MỚI
    // =================================================================

    /**
     * Import sản phẩm từ Excel
     * Frontend sẽ parse Excel thành List<ProductImportDto> và gửi lên
     */
    @PostMapping("/import")
    public ResponseEntity<ProductImportResultDto> importProducts(
            @Valid @RequestBody List<ProductImportDto> importList
    ) {
        ProductImportResultDto result = adminProductService.importProducts(importList);
        return ResponseEntity.ok(result);
    }

    /**
     * Bulk update sản phẩm
     */
    @PostMapping("/bulk-update")
    public ResponseEntity<ProductBulkUpdateResultDto> bulkUpdateProducts(
            @Valid @RequestBody ProductBulkUpdateRequestDto request
    ) {
        ProductBulkUpdateResultDto result = adminProductService.bulkUpdateProducts(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Duplicate (nhân bản) sản phẩm
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<AdminProductDetailDto> duplicateProduct(@PathVariable Long id) {
        AdminProductDetailDto duplicated = adminProductService.duplicateProduct(id);
        return new ResponseEntity<>(duplicated, HttpStatus.CREATED);
    }

    /**
     * Lấy thống kê sản phẩm
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProductStatsDto> getProductStatistics() {
        ProductStatsDto stats = adminProductService.getProductStatistics();
        return ResponseEntity.ok(stats);
    }
}