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
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Admin - Products", description = "API quản lý sản phẩm cho Admin")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ tất cả API
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * Tạo sản phẩm mới
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào (tên, slug, brand, categories, variants)</li>
     *   <li>Validate các business rules (slug unique, tên sản phẩm unique, SKU unique, giá hợp lệ)</li>
     *   <li>Gọi service để tạo sản phẩm mới với các biến thể</li>
     *   <li>Xóa cache của sản phẩm</li>
     *   <li>Trả về sản phẩm vừa tạo</li>
     * </ol>
     * 
     * <p><b>Về variants:</b>
     * <ul>
     *   <li>Mỗi sản phẩm phải có ít nhất 1 biến thể (variant)</li>
     *   <li>Mỗi variant phải có: SKU (unique), size, màu sắc, giá, số lượng tồn kho</li>
     *   <li>SKU phải unique trong toàn bộ hệ thống</li>
     * </ul>
     * 
     * @param requestDto DTO chứa thông tin sản phẩm cần tạo:
     *                   - name: Tên sản phẩm (bắt buộc)
     *                   - slug: Slug của sản phẩm (bắt buộc, phải unique)
     *                   - brandId: ID thương hiệu (bắt buộc)
     *                   - categoryIds: Danh sách ID danh mục (bắt buộc, ít nhất 1)
     *                   - variants: Danh sách biến thể (bắt buộc, ít nhất 1)
     * @return ResponseEntity chứa AdminProductDetailDto của sản phẩm vừa tạo (HTTP 201 Created)
     * @throws ApiException nếu validation thất bại hoặc dữ liệu không hợp lệ
     * 
     * @example
     * <pre>
     * AdminProductRequestDto request = new AdminProductRequestDto();
     * request.setName("Nike Air Max 90");
     * request.setSlug("nike-air-max-90");
     * request.setBrandId(1);
     * request.setCategoryIds(Arrays.asList(1, 2));
     * 
     * // Thêm biến thể
     * ProductVariantRequestDto variant = new ProductVariantRequestDto();
     * variant.setSku("NIKE-AM90-40-RED");
     * variant.setSize(40);
     * variant.setColor("Đỏ");
     * variant.setPrice(new BigDecimal("2500000"));
     * variant.setStockQuantity(100);
     * request.setVariants(Arrays.asList(variant));
     * 
     * ResponseEntity&lt;AdminProductDetailDto&gt; response = adminProductController.createProduct(request);
     * </pre>
     */
    @PostMapping
    public ResponseEntity<AdminProductDetailDto> createProduct(
            @Valid @RequestBody AdminProductRequestDto requestDto
    ) {
        AdminProductDetailDto newProduct = adminProductService.createProduct(requestDto);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    /**
     * Lấy danh sách sản phẩm với phân trang và lọc nâng cao
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Xây dựng filter DTO từ các tham số query</li>
     *   <li>Gọi service để lấy danh sách sản phẩm với filter và phân trang</li>
     *   <li>Trả về danh sách sản phẩm đã được lọc và phân trang</li>
     * </ol>
     * 
     * <p><b>Hỗ trợ các filter options:</b>
     * <ul>
     *   <li>Search: Tìm kiếm theo tên hoặc slug (không phân biệt hoa thường)</li>
     *   <li>Brand: Lọc theo thương hiệu (brandId)</li>
     *   <li>Category: Lọc theo danh mục (categoryId)</li>
     *   <li>Status: Lọc theo trạng thái ("active" hoặc "inactive")</li>
     *   <li>Price range: Lọc theo khoảng giá (minPrice - maxPrice, đơn vị: VNĐ)</li>
     *   <li>Stock level: Lọc theo mức tồn kho ("in_stock", "low_stock", "out_of_stock")</li>
     *   <li>Sorting: Sắp xếp theo cột (sortBy: "name", "price", "stock") và hướng (sortDirection: "asc", "desc")</li>
     * </ul>
     * 
     * <p><b>Về phân trang:</b>
     * <ul>
     *   <li>Mặc định: page = 0, size = 10</li>
     *   <li>Trả về Page chứa: danh sách sản phẩm, tổng số trang, tổng số phần tử</li>
     * </ul>
     * 
     * @param page Số trang (bắt đầu từ 0, mặc định: 0)
     * @param size Số items mỗi trang (mặc định: 10)
     * @param search Từ khóa tìm kiếm (tên hoặc slug, tùy chọn)
     * @param brandId ID thương hiệu để lọc (tùy chọn)
     * @param status Trạng thái: "active" hoặc "inactive" (tùy chọn)
     * @param categoryId ID danh mục để lọc (tùy chọn)
     * @param minPrice Giá tối thiểu (VNĐ, tùy chọn)
     * @param maxPrice Giá tối đa (VNĐ, tùy chọn)
     * @param stockLevel Mức tồn kho: "in_stock", "low_stock", "out_of_stock" (tùy chọn)
     * @param sortBy Cột để sort: "name", "price", "stock" (tùy chọn)
     * @param sortDirection Hướng sort: "asc" hoặc "desc" (tùy chọn)
     * @return ResponseEntity chứa Page&lt;AdminProductListDto&gt; với danh sách sản phẩm đã lọc (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * // Lấy trang đầu tiên, mỗi trang 20 sản phẩm, lọc theo brand Nike
     * ResponseEntity&lt;Page&lt;AdminProductListDto&gt;&gt; response = adminProductController.getAllProducts(
     *     0, 20, null, 1, null, null, null, null, null, null, null
     * );
     * Page&lt;AdminProductListDto&gt; products = response.getBody();
     * 
     * // Tìm kiếm sản phẩm có tên chứa "Nike"
     * ResponseEntity&lt;Page&lt;AdminProductListDto&gt;&gt; response2 = adminProductController.getAllProducts(
     *     0, 10, "Nike", null, null, null, null, null, null, null, null
     * );
     * </pre>
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
     * Lấy thông tin chi tiết sản phẩm theo ID (dùng cho trang Edit)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy sản phẩm theo ID (có cache)</li>
     *   <li>Trả về thông tin chi tiết đầy đủ của sản phẩm</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Thông tin cơ bản: tên, slug, mô tả, trạng thái</li>
     *   <li>Thông tin liên quan: Brand, Categories, Material, ShoeSole</li>
     *   <li>Danh sách variants với đầy đủ thông tin: SKU, size, màu, giá, số lượng tồn kho</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Dữ liệu được cache để tối ưu hiệu năng. Cache key là ID của sản phẩm.
     * 
     * @param id ID của sản phẩm cần lấy
     * @return ResponseEntity chứa AdminProductDetailDto với thông tin chi tiết sản phẩm (HTTP 200 OK)
     * @throws ApiException với status 404 nếu không tìm thấy sản phẩm
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;AdminProductDetailDto&gt; response = adminProductController.getProductById(1L);
     * AdminProductDetailDto product = response.getBody();
     * System.out.println(product.getName()); // "Nike Air Max 90"
     * System.out.println(product.getVariants().size()); // Số lượng biến thể
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDetailDto> getProductById(@PathVariable Long id) {
        AdminProductDetailDto product = adminProductService.getProductByIdForAdmin(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Cập nhật thông tin sản phẩm
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Tìm sản phẩm theo ID</li>
     *   <li>Validate dữ liệu đầu vào và các business rules</li>
     *   <li>Gọi service để cập nhật sản phẩm</li>
     *   <li>Cập nhật variants (thêm mới, cập nhật, hoặc xóa)</li>
     *   <li>Xóa cache của sản phẩm này</li>
     *   <li>Trả về sản phẩm sau khi cập nhật</li>
     * </ol>
     * 
     * <p><b>Có thể cập nhật:</b>
     * <ul>
     *   <li>Thông tin cơ bản: tên, slug, mô tả, trạng thái</li>
     *   <li>Thông tin liên quan: Brand, Categories, Material, ShoeSole</li>
     *   <li>Variants: thêm mới (không có ID), cập nhật (có ID), xóa (không có trong danh sách mới)</li>
     * </ul>
     * 
     * <p><b>Về validation:</b>
     * <ul>
     *   <li>Slug phải unique (trừ chính sản phẩm này)</li>
     *   <li>SKU phải unique trong toàn bộ variants</li>
     *   <li>Giá sale phải <= giá gốc (priceSale <= priceBase)</li>
     *   <li>Tên sản phẩm phải unique trong cùng brand (trừ chính sản phẩm này)</li>
     * </ul>
     * 
     * @param id ID của sản phẩm cần cập nhật
     * @param requestDto DTO chứa thông tin mới của sản phẩm (tương tự như createProduct)
     * @return ResponseEntity chứa AdminProductDetailDto của sản phẩm sau khi cập nhật (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy sản phẩm hoặc validation thất bại
     * 
     * @example
     * <pre>
     * AdminProductRequestDto updateData = new AdminProductRequestDto();
     * updateData.setName("Nike Air Max 90 Updated");
     * updateData.setPrice(new BigDecimal("2600000"));
     * // ... các thông tin khác
     * 
     * ResponseEntity&lt;AdminProductDetailDto&gt; response = adminProductController.updateProduct(1L, updateData);
     * AdminProductDetailDto updated = response.getBody();
     * </pre>
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
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Tìm sản phẩm theo ID</li>
     *   <li>Gọi service để xóa sản phẩm (soft delete - đánh dấu đã xóa, không xóa thật)</li>
     *   <li>Xóa cache của sản phẩm</li>
     *   <li>Trả về thông báo thành công</li>
     * </ol>
     * 
     * <p><b>Cảnh báo:</b> Hành động này sẽ xóa sản phẩm và tất cả variants liên quan.
     * Nếu sản phẩm đã có đơn hàng, cần cân nhắc kỹ.
     * 
     * <p><b>Về soft delete:</b>
     * <ul>
     *   <li>Sản phẩm không bị xóa thật khỏi database</li>
     *   <li>Chỉ đánh dấu là đã xóa (deleted_at != null)</li>
     *   <li>Có thể khôi phục lại nếu cần</li>
     *   <li>Sản phẩm đã xóa sẽ không hiển thị trong danh sách (trừ khi filter riêng)</li>
     * </ul>
     * 
     * @param id ID của sản phẩm cần xóa
     * @return ResponseEntity chứa thông báo thành công (HTTP 200 OK)
     * @throws ApiException với status 404 nếu không tìm thấy sản phẩm
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;String&gt; response = adminProductController.deleteProduct(1L);
     * String message = response.getBody(); // "Đã xóa sản phẩm thành công"
     * </pre>
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