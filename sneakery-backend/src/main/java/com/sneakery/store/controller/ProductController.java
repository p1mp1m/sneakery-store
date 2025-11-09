package com.sneakery.store.controller;

import com.sneakery.store.dto.ProductCardDto;
import com.sneakery.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // QUAN TRỌNG
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
            @RequestParam(defaultValue = "8") int size) { // Mặc định hiển thị 8 sản phẩm/trang
        Page<ProductCardDto> products = productService.getAllProductsForCard(page, size);
        return ResponseEntity.ok(products);
    }
}