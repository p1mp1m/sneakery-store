package com.sneakery.store.controller;

import com.sneakery.store.dto.AdminProductDetailDto;
import com.sneakery.store.dto.AdminProductListDto;
import com.sneakery.store.dto.AdminProductRequestDto;
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
     * Lấy danh sách sản phẩm (phân trang) với filter
     */
    @GetMapping
    public ResponseEntity<Page<AdminProductListDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminProductListDto> productPage = adminProductService.getAllProductsForAdmin(pageable);
        // Note: Filter logic sẽ được implement sau nếu cần
        // Hiện tại chỉ trả về tất cả products để tránh lỗi 500
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
}