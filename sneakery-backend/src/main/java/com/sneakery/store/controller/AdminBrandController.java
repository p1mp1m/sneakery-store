package com.sneakery.store.controller;

import com.sneakery.store.dto.BrandDto;
import com.sneakery.store.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Controller quản lý thương hiệu cho Admin
 * 
 * <p>Controller này cung cấp các API endpoints cho admin để quản lý thương hiệu:
 * <ul>
 *   <li>Tạo, đọc, cập nhật, xóa thương hiệu</li>
 *   <li>Lấy danh sách tất cả thương hiệu</li>
 *   <li>Lấy thông tin chi tiết thương hiệu theo ID</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu role ADMIN</li>
 *   <li>Sử dụng @PreAuthorize("hasRole('ADMIN')") để bảo vệ toàn bộ controller</li>
 * </ul>
 * 
 * <p><b>Về caching:</b>
 * <ul>
 *   <li>Tất cả dữ liệu thương hiệu được cache để tối ưu hiệu năng</li>
 *   <li>Cache tự động bị xóa khi có thương hiệu mới được tạo, cập nhật, hoặc xóa</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Tạo thương hiệu mới
 * BrandDto newBrand = new BrandDto();
 * newBrand.setName("Nike");
 * newBrand.setSlug("nike");
 * ResponseEntity&lt;BrandDto&gt; response = adminBrandController.createBrand(newBrand);
 * 
 * // Lấy tất cả thương hiệu
 * ResponseEntity&lt;List&lt;BrandDto&gt;&gt; brands = adminBrandController.getAllBrands();
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Admin - Brands", description = "API quản lý thương hiệu cho Admin")
@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminBrandController {

    private final BrandService brandService;

    /**
     * Tạo thương hiệu mới
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào (tên, slug)</li>
     *   <li>Gọi service để tạo thương hiệu mới</li>
     *   <li>Xóa cache của thương hiệu</li>
     *   <li>Trả về thương hiệu vừa tạo</li>
     * </ol>
     * 
     * @param brandDto DTO chứa thông tin thương hiệu cần tạo:
     *                 - name: Tên thương hiệu (bắt buộc)
     *                 - slug: Slug của thương hiệu (bắt buộc, phải unique)
     * @return ResponseEntity chứa BrandDto của thương hiệu vừa tạo (HTTP 201 Created)
     * @throws ApiException nếu validation thất bại hoặc slug đã tồn tại
     * 
     * @example
     * <pre>
     * BrandDto newBrand = new BrandDto();
     * newBrand.setName("Nike");
     * newBrand.setSlug("nike");
     * ResponseEntity&lt;BrandDto&gt; response = adminBrandController.createBrand(newBrand);
     * BrandDto created = response.getBody();
     * </pre>
     */
    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@Valid @RequestBody BrandDto brandDto) {
        BrandDto newBrand = brandService.createBrand(brandDto);
        return new ResponseEntity<>(newBrand, HttpStatus.CREATED);
    }

    /**
     * Lấy danh sách tất cả thương hiệu
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy tất cả thương hiệu (có cache)</li>
     *   <li>Trả về danh sách thương hiệu</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b> Dữ liệu được cache để tối ưu hiệu năng. Lần đầu tiên sẽ lấy từ database,
     * các lần sau sẽ lấy từ cache.
     * 
     * @return ResponseEntity chứa danh sách tất cả BrandDto (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;BrandDto&gt;&gt; response = adminBrandController.getAllBrands();
     * List&lt;BrandDto&gt; brands = response.getBody();
     * brands.forEach(brand -&gt; System.out.println(brand.getName()));
     * </pre>
     */
    @GetMapping
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    /**
     * Lấy thông tin chi tiết thương hiệu theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy thương hiệu theo ID (có cache)</li>
     *   <li>Trả về thông tin chi tiết thương hiệu</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b> Dữ liệu được cache để tối ưu hiệu năng. Cache key là ID của thương hiệu.
     * 
     * @param id ID của thương hiệu cần lấy
     * @return ResponseEntity chứa BrandDto với thông tin chi tiết thương hiệu (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy thương hiệu với ID này
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;BrandDto&gt; response = adminBrandController.getBrandById(1);
     * BrandDto brand = response.getBody();
     * System.out.println(brand.getName()); // "Nike"
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(@PathVariable Integer id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    /**
     * Cập nhật thông tin thương hiệu
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào</li>
     *   <li>Gọi service để cập nhật thương hiệu</li>
     *   <li>Xóa cache của thương hiệu</li>
     *   <li>Trả về thương hiệu sau khi cập nhật</li>
     * </ol>
     * 
     * @param id ID của thương hiệu cần cập nhật
     * @param brandDto DTO chứa thông tin mới của thương hiệu (tên, slug)
     * @return ResponseEntity chứa BrandDto của thương hiệu sau khi cập nhật (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy thương hiệu hoặc validation thất bại
     * 
     * @example
     * <pre>
     * BrandDto updateData = new BrandDto();
     * updateData.setName("Nike Updated");
     * updateData.setSlug("nike-updated");
     * ResponseEntity&lt;BrandDto&gt; response = adminBrandController.updateBrand(1, updateData);
     * BrandDto updated = response.getBody();
     * </pre>
     */
    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable Integer id, @Valid @RequestBody BrandDto brandDto) {
        return ResponseEntity.ok(brandService.updateBrand(id, brandDto));
    }

    /**
     * Xóa thương hiệu theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để xóa thương hiệu</li>
     *   <li>Xóa cache của thương hiệu</li>
     *   <li>Trả về thông báo thành công</li>
     * </ol>
     * 
     * <p><b>Cảnh báo:</b> Hành động này không thể hoàn tác. Hãy chắc chắn trước khi xóa.
     * 
     * <p><b>Lưu ý:</b> Nếu thương hiệu đang được sử dụng bởi các sản phẩm, có thể gặp lỗi
     * foreign key constraint. Cần xóa hoặc cập nhật các sản phẩm liên quan trước.
     * 
     * @param id ID của thương hiệu cần xóa
     * @return ResponseEntity chứa thông báo thành công (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy thương hiệu hoặc thương hiệu đang được sử dụng
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;String&gt; response = adminBrandController.deleteBrand(1);
     * String message = response.getBody(); // "Đã xóa thương hiệu thành công"
     * </pre>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Đã xóa thương hiệu thành công");
    }
}