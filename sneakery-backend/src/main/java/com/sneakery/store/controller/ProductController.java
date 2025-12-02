package com.sneakery.store.controller;

import com.sneakery.store.dto.AdminProductDetailDto;
import com.sneakery.store.dto.BrandDto;
import com.sneakery.store.dto.CategoryDto;
import com.sneakery.store.dto.CategoryGroupDto;
import com.sneakery.store.dto.ProductCardDto;
import com.sneakery.store.service.BrandService;
import com.sneakery.store.service.CategoryService;
import com.sneakery.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // QUAN TRỌNG
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller xử lý sản phẩm cho User (Public)
 * 
 * <p>Controller này cung cấp các API endpoints công khai cho user để:
 * <ul>
 *   <li>Lấy danh sách sản phẩm với phân trang</li>
 *   <li>Xem thông tin sản phẩm (dạng card - tóm tắt)</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều công khai (không cần đăng nhập)</li>
 *   <li>Chỉ hiển thị các sản phẩm đang active (isActive = true)</li>
 *   <li>Chỉ hiển thị các sản phẩm chưa bị xóa (soft-deleted)</li>
 * </ul>
 * 
 * <p><b>Về dữ liệu trả về:</b>
 * <ul>
 *   <li>Mỗi sản phẩm bao gồm: ID, tên, slug, brand, hình ảnh, giá thấp nhất, giá cao nhất, tổng tồn kho</li>
 *   <li>Không bao gồm chi tiết variants (chỉ dùng cho danh sách)</li>
 *   <li>Sử dụng query tối ưu để load tất cả dữ liệu trong 1 lần</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy trang đầu tiên, mỗi trang 8 sản phẩm
 * ResponseEntity&lt;Page&lt;ProductCardDto&gt;&gt; response = productController.getAllProducts(0, 8);
 * Page&lt;ProductCardDto&gt; products = response.getBody();
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Products", description = "API xem sản phẩm (Public)")
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private BrandService brandService;

    /**
     * Lấy danh sách sản phẩm với phân trang (Public)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy danh sách sản phẩm với phân trang</li>
     *   <li>Chỉ lấy các sản phẩm đang active và chưa bị xóa</li>
     *   <li>Trả về danh sách sản phẩm dạng card (tóm tắt)</li>
     * </ol>
     * 
     * <p><b>Về phân trang:</b>
     * <ul>
     *   <li>Mặc định: page = 0, size = 8</li>
     *   <li>Trả về Page chứa: danh sách sản phẩm, tổng số trang, tổng số phần tử</li>
     * </ul>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi sản phẩm bao gồm: ID, tên, slug, brand, hình ảnh chính</li>
     *   <li>Giá thấp nhất và giá cao nhất trong tất cả variants</li>
     *   <li>Tổng tồn kho của tất cả variants</li>
     *   <li>Không bao gồm chi tiết variants (chỉ dùng cho danh sách)</li>
     * </ul>
     * 
     * @param page Số trang (bắt đầu từ 0, mặc định: 0)
     * @param size Số items mỗi trang (mặc định: 8)
     * @return ResponseEntity chứa Page&lt;ProductCardDto&gt; với danh sách sản phẩm (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * // Lấy trang đầu tiên, mỗi trang 8 sản phẩm
     * ResponseEntity&lt;Page&lt;ProductCardDto&gt;&gt; response = productController.getAllProducts(0, 8);
     * Page&lt;ProductCardDto&gt; products = response.getBody();
     * 
     * System.out.println("Tổng số sản phẩm: " + products.getTotalElements());
     * System.out.println("Tổng số trang: " + products.getTotalPages());
     * products.getContent().forEach(p -&gt; System.out.println(p.getName()));
     * </pre>
     */
    @Operation(summary = "Lấy danh sách sản phẩm", description = "Lấy danh sách sản phẩm với phân trang. Chỉ hiển thị các sản phẩm đang active và chưa bị xóa.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping
    public ResponseEntity<Page<ProductCardDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {

        Page<ProductCardDto> products = productService.getAllProductsForCard(page, size, search);
        return ResponseEntity.ok(products);
    }

    /**
     * Lấy thông tin chi tiết sản phẩm theo ID (Public)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy sản phẩm theo ID</li>
     *   <li>Chỉ lấy các sản phẩm đang active và chưa bị xóa</li>
     *   <li>Trả về thông tin chi tiết đầy đủ của sản phẩm</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Thông tin cơ bản: tên, slug, mô tả</li>
     *   <li>Thông tin liên quan: Brand, Categories, Material, ShoeSole</li>
     *   <li>Danh sách variants với đầy đủ thông tin: SKU, size, màu, giá, số lượng tồn kho</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Endpoint này công khai, không cần đăng nhập</li>
     *   <li>Chỉ trả về các sản phẩm đang active (isActive = true)</li>
     *   <li>Chỉ trả về các sản phẩm chưa bị xóa (soft-deleted)</li>
     * </ul>
     * 
     * @param id ID của sản phẩm cần lấy
     * @return ResponseEntity chứa AdminProductDetailDto với thông tin chi tiết sản phẩm (HTTP 200 OK)
     * @throws ApiException với status 404 nếu không tìm thấy sản phẩm hoặc sản phẩm không active
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;AdminProductDetailDto&gt; response = productController.getProductById(1L);
     * AdminProductDetailDto product = response.getBody();
     * System.out.println(product.getName()); // "Nike Air Max 90"
     * System.out.println(product.getVariants().size()); // Số lượng biến thể
     * </pre>
     */
    @Operation(summary = "Lấy thông tin chi tiết sản phẩm", description = "Lấy thông tin chi tiết sản phẩm theo ID. Chỉ hiển thị các sản phẩm đang active và chưa bị xóa.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDetailDto> getProductById(@PathVariable Long id) {
        AdminProductDetailDto product = productService.getProductByIdForPublic(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Lấy danh sách tất cả danh mục con (Public)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy tất cả danh mục</li>
     *   <li>Chỉ lấy các danh mục con (có parentId)</li>
     *   <li>Trả về danh sách danh mục con</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Chỉ trả về các danh mục con (parentId != null)</li>
     *   <li>Mỗi danh mục bao gồm: ID, tên, slug, parentId</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Endpoint này công khai, không cần đăng nhập</li>
     *   <li>Dữ liệu được cache để tối ưu hiệu năng</li>
     * </ul>
     * 
     * @return ResponseEntity chứa danh sách CategoryDto của các danh mục con (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;CategoryDto&gt;&gt; response = productController.getCategories();
     * List&lt;CategoryDto&gt; categories = response.getBody();
     * categories.forEach(cat -&gt; System.out.println(cat.getName()));
     * </pre>
     */
    @Operation(summary = "Lấy danh sách danh mục con", description = "Lấy danh sách tất cả danh mục con (có parentId). Endpoint công khai, không cần đăng nhập.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<CategoryDto> allCategories = categoryService.getAllCategories();
        // Chỉ lấy các danh mục con (có parentId)
        List<CategoryDto> childCategories = allCategories.stream()
                .filter(cat -> cat.getParentId() != null)
                .collect(Collectors.toList());
        return ResponseEntity.ok(childCategories);
    }

    /**
     * Lấy danh sách categories theo nhóm (parent categories với children)
     * 
     * <p>Phương thức này trả về categories được nhóm theo parent:
     * <ul>
     *   <li>Mỗi parent category bao gồm danh sách children</li>
     *   <li>Chỉ trả về các parent categories có children</li>
     *   <li>productCount sẽ được tính ở frontend dựa trên số sản phẩm của children</li>
     * </ul>
     * 
     * @return ResponseEntity chứa danh sách CategoryGroupDto (HTTP 200 OK)
     */
    @Operation(summary = "Lấy danh sách categories theo nhóm", description = "Lấy danh sách parent categories với children của chúng. Endpoint công khai, không cần đăng nhập.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping("/categories/groups")
    public ResponseEntity<List<CategoryGroupDto>> getCategoryGroups() {
        List<CategoryGroupDto> categoryGroups = categoryService.getCategoryGroups();
        return ResponseEntity.ok(categoryGroups);
    }

    /**
     * Lấy danh sách tất cả thương hiệu (Public)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy tất cả thương hiệu</li>
     *   <li>Trả về danh sách thương hiệu</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi thương hiệu bao gồm: ID, tên, slug, logo (nếu có)</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Endpoint này công khai, không cần đăng nhập</li>
     *   <li>Dữ liệu được cache để tối ưu hiệu năng</li>
     * </ul>
     * 
     * @return ResponseEntity chứa danh sách BrandDto (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;BrandDto&gt;&gt; response = productController.getBrands();
     * List&lt;BrandDto&gt; brands = response.getBody();
     * brands.forEach(brand -&gt; System.out.println(brand.getName()));
     * </pre>
     */
    @Operation(summary = "Lấy danh sách thương hiệu", description = "Lấy danh sách tất cả thương hiệu. Endpoint công khai, không cần đăng nhập.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    @GetMapping("/brands")
    public ResponseEntity<List<BrandDto>> getBrands() {
        List<BrandDto> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    /**
     * Lấy thông tin sản phẩm theo slug (Public)
     */
    @Operation(summary = "Lấy sản phẩm theo slug", description = "Lấy thông tin chi tiết sản phẩm theo slug. Công khai, không cần đăng nhập.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<AdminProductDetailDto> getProductBySlug(@PathVariable String slug) {
        AdminProductDetailDto product = productService.getProductBySlugForPublic(slug);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}/related")
    public List<ProductCardDto> getRelated(
            @PathVariable Long id,
            @RequestParam Long brandId,
            @RequestParam List<Long> categoryIds) {

        return productService.getRelatedProducts(id, brandId, categoryIds, 4);
    }

    @GetMapping("/search")
    public Page<ProductCardDto> searchProducts(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return productService.searchProductsAdvanced(brand, category, page, size);
    }
}