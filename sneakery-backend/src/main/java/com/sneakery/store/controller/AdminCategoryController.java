package com.sneakery.store.controller;

import com.sneakery.store.dto.CategoryDto;
import com.sneakery.store.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Controller quản lý danh mục cho Admin
 * 
 * <p>Controller này cung cấp các API endpoints cho admin để quản lý danh mục:
 * <ul>
 *   <li>Tạo, đọc, cập nhật, xóa danh mục</li>
 *   <li>Lấy danh sách tất cả danh mục</li>
 *   <li>Lấy thông tin chi tiết danh mục theo ID</li>
 *   <li>Quản lý cấu trúc phân cấp danh mục (danh mục cha - danh mục con)</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu role ADMIN</li>
 *   <li>Sử dụng @PreAuthorize("hasRole('ADMIN')") để bảo vệ toàn bộ controller</li>
 * </ul>
 * 
 * <p><b>Về cấu trúc phân cấp:</b>
 * <ul>
 *   <li>Danh mục có thể có danh mục cha (parent category)</li>
 *   <li>Ví dụ: "Giày thể thao" là cha của "Giày chạy bộ", "Giày bóng đá"</li>
 *   <li>Khi xóa danh mục cha, các danh mục con có thể bị xóa theo (nếu cascade delete được bật)</li>
 * </ul>
 * 
 * <p><b>Về caching:</b>
 * <ul>
 *   <li>Tất cả dữ liệu danh mục được cache để tối ưu hiệu năng</li>
 *   <li>Cache tự động bị xóa khi có danh mục mới được tạo, cập nhật, hoặc xóa</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Tạo danh mục mới
 * CategoryDto newCategory = new CategoryDto(null, "Giày thể thao", "giay-the-thao", null);
 * ResponseEntity&lt;CategoryDto&gt; response = adminCategoryController.createCategory(newCategory);
 * 
 * // Lấy tất cả danh mục
 * ResponseEntity&lt;List&lt;CategoryDto&gt;&gt; categories = adminCategoryController.getAllCategories();
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Admin - Categories", description = "API quản lý danh mục cho Admin")
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * Tạo danh mục mới
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào (tên, slug, parentId)</li>
     *   <li>Gọi service để tạo danh mục mới</li>
     *   <li>Thiết lập danh mục cha nếu có</li>
     *   <li>Xóa cache của danh mục</li>
     *   <li>Trả về danh mục vừa tạo</li>
     * </ol>
     * 
     * <p><b>Về danh mục cha:</b>
     * <ul>
     *   <li>Nếu parentId != null: Tạo danh mục con (có cha)</li>
     *   <li>Nếu parentId == null: Tạo danh mục gốc (không có cha)</li>
     * </ul>
     * 
     * @param categoryDto DTO chứa thông tin danh mục cần tạo:
     *                    - name: Tên danh mục (bắt buộc)
     *                    - slug: Slug của danh mục (bắt buộc, phải unique)
     *                    - parentId: ID của danh mục cha (tùy chọn)
     * @return ResponseEntity chứa CategoryDto của danh mục vừa tạo (HTTP 201 Created)
     * @throws ApiException nếu validation thất bại hoặc không tìm thấy danh mục cha
     * 
     * @example
     * <pre>
     * // Tạo danh mục gốc
     * CategoryDto rootCategory = new CategoryDto(null, "Giày thể thao", "giay-the-thao", null);
     * ResponseEntity&lt;CategoryDto&gt; response = adminCategoryController.createCategory(rootCategory);
     * 
     * // Tạo danh mục con
     * CategoryDto childCategory = new CategoryDto(null, "Giày chạy bộ", "giay-chay-bo", 1);
     * ResponseEntity&lt;CategoryDto&gt; response2 = adminCategoryController.createCategory(childCategory);
     * </pre>
     */
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto newCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    /**
     * Lấy danh sách tất cả danh mục
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy tất cả danh mục (có cache)</li>
     *   <li>Trả về danh sách danh mục (bao gồm cả danh mục gốc và danh mục con)</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b> Dữ liệu được cache để tối ưu hiệu năng. Lần đầu tiên sẽ lấy từ database,
     * các lần sau sẽ lấy từ cache.
     * 
     * @return ResponseEntity chứa danh sách tất cả CategoryDto (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;CategoryDto&gt;&gt; response = adminCategoryController.getAllCategories();
     * List&lt;CategoryDto&gt; categories = response.getBody();
     * categories.forEach(cat -&gt; {
     *     System.out.println(cat.getName());
     *     if (cat.getParentId() != null) {
     *         System.out.println("  - Danh mục con của: " + cat.getParentId());
     *     }
     * });
     * </pre>
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * Lấy thông tin chi tiết danh mục theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy danh mục theo ID (có cache)</li>
     *   <li>Trả về thông tin chi tiết danh mục (bao gồm parentId nếu có)</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b> Dữ liệu được cache để tối ưu hiệu năng. Cache key là ID của danh mục.
     * 
     * @param id ID của danh mục cần lấy
     * @return ResponseEntity chứa CategoryDto với thông tin chi tiết danh mục (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy danh mục với ID này
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;CategoryDto&gt; response = adminCategoryController.getCategoryById(1);
     * CategoryDto category = response.getBody();
     * System.out.println(category.getName()); // "Giày thể thao"
     * System.out.println(category.getParentId()); // null (nếu là danh mục gốc)
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * Cập nhật thông tin danh mục
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào</li>
     *   <li>Gọi service để cập nhật danh mục</li>
     *   <li>Cập nhật danh mục cha nếu có thay đổi</li>
     *   <li>Xóa cache của danh mục</li>
     *   <li>Trả về danh mục sau khi cập nhật</li>
     * </ol>
     * 
     * <p><b>Về danh mục cha:</b>
     * <ul>
     *   <li>Nếu parentId != null: Cập nhật danh mục cha</li>
     *   <li>Nếu parentId == null: Xóa danh mục cha (trở thành danh mục gốc)</li>
     * </ul>
     * 
     * @param id ID của danh mục cần cập nhật
     * @param categoryDto DTO chứa thông tin mới của danh mục (tên, slug, parentId)
     * @return ResponseEntity chứa CategoryDto của danh mục sau khi cập nhật (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy danh mục hoặc danh mục cha không tồn tại
     * 
     * @example
     * <pre>
     * CategoryDto updateData = new CategoryDto(1, "Giày thể thao mới", "giay-the-thao-moi", null);
     * ResponseEntity&lt;CategoryDto&gt; response = adminCategoryController.updateCategory(1, updateData);
     * CategoryDto updated = response.getBody();
     * </pre>
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }

    /**
     * Xóa danh mục theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để xóa danh mục</li>
     *   <li>Xóa cache của danh mục</li>
     *   <li>Trả về thông báo thành công</li>
     * </ol>
     * 
     * <p><b>Cảnh báo:</b> Hành động này không thể hoàn tác. Hãy chắc chắn trước khi xóa.
     * 
     * <p><b>Về cascade delete:</b>
     * <ul>
     *   <li>Nếu cascade delete được bật: Khi xóa danh mục cha, tất cả danh mục con sẽ bị xóa theo</li>
     *   <li>Nếu cascade delete tắt: Cần xóa hoặc cập nhật các danh mục con trước</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Nếu danh mục đang được sử dụng bởi các sản phẩm, có thể gặp lỗi
     * foreign key constraint. Cần xóa hoặc cập nhật các sản phẩm liên quan trước.
     * 
     * @param id ID của danh mục cần xóa
     * @return ResponseEntity chứa thông báo thành công (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy danh mục hoặc danh mục đang được sử dụng
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;String&gt; response = adminCategoryController.deleteCategory(1);
     * String message = response.getBody(); // "Đã xóa danh mục thành công"
     * </pre>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Đã xóa danh mục thành công");
    }
}