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

/**
 * REST Controller cho quản lý sản phẩm (Admin)
 * 
 * <p>Controller này cung cấp các API endpoints cho admin để:
 * <ul>
 *   <li>CRUD operations cho sản phẩm</li>
 *   <li>Advanced filtering và search</li>
 *   <li>Import/Export Excel</li>
 *   <li>Bulk operations</li>
 *   <li>Statistics và analytics</li>
 * </ul>
 * 
 * <p>Tất cả endpoints đều yêu cầu role ADMIN.
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ tất cả API
@CrossOrigin(origins = "http://localhost:5173")
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * Tạo sản phẩm mới
     * 
     * @param requestDto DTO chứa thông tin sản phẩm cần tạo
     *                    (bao gồm brand, categories, variants, etc.)
     * @return ResponseEntity chứa AdminProductDetailDto của sản phẩm vừa tạo
     * @throws ApiException nếu validation fails hoặc data không hợp lệ
     * @see AdminProductRequestDto
     * @see AdminProductDetailDto
     */
    @PostMapping
    public ResponseEntity<AdminProductDetailDto> createProduct(
            @Valid @RequestBody AdminProductRequestDto requestDto
    ) {
        AdminProductDetailDto newProduct = adminProductService.createProduct(requestDto);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    /**
     * Lấy danh sách sản phẩm với phân trang và advanced filtering
     * 
     * <p>Hỗ trợ các filter options:
     * <ul>
     *   <li>Search: Tìm kiếm theo tên hoặc slug</li>
     *   <li>Brand: Lọc theo thương hiệu</li>
     *   <li>Category: Lọc theo danh mục</li>
     *   <li>Status: active/inactive</li>
     *   <li>Price range: minPrice - maxPrice</li>
     *   <li>Stock level: in_stock, low_stock, out_of_stock</li>
     *   <li>Sorting: sortBy và sortDirection</li>
     * </ul>
     * 
     * @param page Số trang (bắt đầu từ 0, default: 0)
     * @param size Số items mỗi trang (default: 10)
     * @param search Từ khóa tìm kiếm (tên hoặc slug)
     * @param brandId ID thương hiệu để lọc
     * @param status Trạng thái: "active" hoặc "inactive"
     * @param categoryId ID danh mục để lọc
     * @param minPrice Giá tối thiểu (VNĐ)
     * @param maxPrice Giá tối đa (VNĐ)
     * @param stockLevel Mức tồn kho: "in_stock", "low_stock", "out_of_stock"
     * @param sortBy Cột để sort: "name", "price", "stock"
     * @param sortDirection Hướng sort: "asc" hoặc "desc"
     * @return ResponseEntity chứa Page<AdminProductListDto>
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
     * Lấy chi tiết 1 sản phẩm (dùng cho trang Edit)
     * 
     * <p>Trả về đầy đủ thông tin sản phẩm bao gồm:
     * <ul>
     *   <li>Thông tin cơ bản (name, slug, description, etc.)</li>
     *   <li>Brand, Categories, Material, ShoeSole</li>
     *   <li>Danh sách variants với đầy đủ thông tin</li>
     * </ul>
     * 
     * @param id ID của sản phẩm cần lấy
     * @return ResponseEntity chứa AdminProductDetailDto
     * @throws ApiException với status 404 nếu không tìm thấy sản phẩm
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDetailDto> getProductById(@PathVariable Long id) {
        AdminProductDetailDto product = adminProductService.getProductByIdForAdmin(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Cập nhật thông tin sản phẩm
     * 
     * <p>Có thể cập nhật:
     * <ul>
     *   <li>Thông tin cơ bản (name, slug, description)</li>
     *   <li>Brand, Categories, Material, ShoeSole</li>
     *   <li>Variants (thêm, sửa, xóa)</li>
     *   <li>Trạng thái active/inactive</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Validation sẽ kiểm tra:
     * <ul>
     *   <li>Slug uniqueness</li>
     *   <li>SKU uniqueness trong variants</li>
     *   <li>Price logic (priceSale <= priceBase)</li>
     * </ul>
     * 
     * @param id ID của sản phẩm cần cập nhật
     * @param requestDto DTO chứa thông tin mới
     * @return ResponseEntity chứa AdminProductDetailDto đã cập nhật
     * @throws ApiException nếu không tìm thấy sản phẩm hoặc validation fails
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
     * Xóa sản phẩm (soft delete)
     * 
     * <p><b>Cảnh báo:</b> Hành động này sẽ xóa sản phẩm và tất cả variants liên quan.
     * Nếu sản phẩm đã có đơn hàng, cần cân nhắc kỹ.
     * 
     * @param id ID của sản phẩm cần xóa
     * @return ResponseEntity với message xác nhận
     * @throws ApiException với status 404 nếu không tìm thấy sản phẩm
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.ok("Đã xóa sản phẩm thành công");
    }

    // =================================================================
    // CÁC API NÂNG CAO
    // =================================================================

    /**
     * Import sản phẩm từ Excel (batch import)
     * 
     * <p>Frontend sẽ parse Excel file thành List<ProductImportDto> và gửi lên.
     * System sẽ tự động:
     * <ul>
     *   <li>Tìm hoặc tạo Brand nếu chưa có</li>
     *   <li>Tìm hoặc tạo Categories nếu chưa có</li>
     *   <li>Nhóm variants cùng product lại</li>
     *   <li>Trả về kết quả với success/error count</li>
     * </ul>
     * 
     * @param importList Danh sách ProductImportDto đã parse từ Excel
     * @return ResponseEntity chứa ProductImportResultDto với:
     *         <ul>
     *           <li>totalRows: Tổng số dòng</li>
     *           <li>successCount: Số dòng import thành công</li>
     *           <li>errorCount: Số dòng lỗi</li>
     *           <li>errorItems: Danh sách các dòng lỗi</li>
     *         </ul>
     * @see ProductImportDto
     * @see ProductImportResultDto
     */
    @PostMapping("/import")
    public ResponseEntity<ProductImportResultDto> importProducts(
            @Valid @RequestBody List<ProductImportDto> importList
    ) {
        ProductImportResultDto result = adminProductService.importProducts(importList);
        return ResponseEntity.ok(result);
    }

    /**
     * Bulk update nhiều sản phẩm cùng lúc
     * 
     * <p>Hỗ trợ các actions:
     * <ul>
     *   <li>UPDATE_STATUS: Cập nhật trạng thái active/inactive</li>
     *   <li>UPDATE_BRAND: Đổi thương hiệu</li>
     *   <li>ADD_CATEGORY: Thêm danh mục</li>
     *   <li>REMOVE_CATEGORY: Xóa danh mục</li>
     * </ul>
     * 
     * @param request DTO chứa danh sách product IDs và action cần thực hiện
     * @return ResponseEntity chứa ProductBulkUpdateResultDto với:
     *         <ul>
     *           <li>totalRequested: Tổng số sản phẩm yêu cầu</li>
     *           <li>successCount: Số sản phẩm cập nhật thành công</li>
     *           <li>errorCount: Số sản phẩm lỗi</li>
     *           <li>successIds: Danh sách IDs thành công</li>
     *           <li>errorIds: Danh sách IDs lỗi</li>
     *         </ul>
     * @see ProductBulkUpdateRequestDto
     * @see ProductBulkUpdateResultDto
     */
    @PostMapping("/bulk-update")
    public ResponseEntity<ProductBulkUpdateResultDto> bulkUpdateProducts(
            @Valid @RequestBody ProductBulkUpdateRequestDto request
    ) {
        ProductBulkUpdateResultDto result = adminProductService.bulkUpdateProducts(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Nhân bản (duplicate) sản phẩm
     * 
     * <p>Tạo một bản copy của sản phẩm với:
     * <ul>
     *   <li>Tên mới: "{originalName} (Copy)"</li>
     *   <li>Slug mới: "{originalSlug}-copy-{timestamp}"</li>
     *   <li>SKU variants mới: "{originalSKU}-COPY"</li>
     *   <li>Stock quantity = 0 (cần cập nhật sau)</li>
     *   <li>Trạng thái: inactive (cần kích hoạt sau)</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Sản phẩm duplicate sẽ không copy images và reviews.
     * 
     * @param id ID của sản phẩm cần nhân bản
     * @return ResponseEntity chứa AdminProductDetailDto của sản phẩm mới
     * @throws ApiException với status 404 nếu không tìm thấy sản phẩm
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<AdminProductDetailDto> duplicateProduct(@PathVariable Long id) {
        AdminProductDetailDto duplicated = adminProductService.duplicateProduct(id);
        return new ResponseEntity<>(duplicated, HttpStatus.CREATED);
    }

    /**
     * Lấy thống kê tổng quan về sản phẩm
     * 
     * <p>Trả về các thống kê:
     * <ul>
     *   <li>Tổng số sản phẩm, variants</li>
     *   <li>Số sản phẩm active/inactive</li>
     *   <li>Tổng tồn kho, low stock count, out of stock count</li>
     *   <li>Giá trung bình, min, max</li>
     *   <li>Tổng số brands, categories</li>
     * </ul>
     * 
     * <p><b>Performance:</b> Sử dụng aggregation queries để tối ưu,
     * không load toàn bộ data vào memory.
     * 
     * @return ResponseEntity chứa ProductStatsDto với đầy đủ thống kê
     * @see ProductStatsDto
     */
    @GetMapping("/statistics")
    public ResponseEntity<ProductStatsDto> getProductStatistics() {
        ProductStatsDto stats = adminProductService.getProductStatistics();
        return ResponseEntity.ok(stats);
    }
}