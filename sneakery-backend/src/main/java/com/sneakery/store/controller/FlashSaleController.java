package com.sneakery.store.controller;

import com.sneakery.store.dto.FlashSaleDto;
import com.sneakery.store.service.FlashSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Controller xử lý Flash Sale (Khuyến mãi giờ vàng)
 * 
 * <p>Controller này cung cấp các API endpoints cho:
 * <ul>
 *   <li>User: Xem các flash sale đang active (public)</li>
 *   <li>Admin: Quản lý flash sale (tạo, cập nhật, xóa)</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Xem flash sale: Công khai (không cần đăng nhập)</li>
 *   <li>Quản lý flash sale: Yêu cầu role ADMIN</li>
 * </ul>
 * 
 * <p><b>Về Flash Sale:</b>
 * <ul>
 *   <li>Flash Sale là chương trình khuyến mãi giới hạn thời gian</li>
 *   <li>Mỗi flash sale có: thời gian bắt đầu, thời gian kết thúc, phần trăm giảm giá</li>
 *   <li>Flash Sale chỉ áp dụng cho các sản phẩm được chỉ định</li>
 *   <li>Chỉ hiển thị các flash sale đang active (trong khoảng thời gian)</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy tất cả flash sale đang active (public)
 * ResponseEntity&lt;List&lt;FlashSaleDto&gt;&gt; response = flashSaleController.getActiveFlashSales();
 * 
 * // Lấy flash sale của sản phẩm (public)
 * ResponseEntity&lt;FlashSaleDto&gt; response2 = flashSaleController.getFlashSaleByProductId(1L);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Flash Sales", description = "API quản lý Flash Sale (Khuyến mãi giờ vàng)")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    /**
     * Lấy tất cả flash sale (cho Admin)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy tất cả flash sale (bao gồm cả đã kết thúc)</li>
     *   <li>Trả về danh sách flash sale</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Yêu cầu role ADMIN</li>
     *   <li>Bao gồm cả flash sale đã kết thúc (để admin quản lý)</li>
     *   <li>Bao gồm cả flash sale chưa bắt đầu</li>
     * </ul>
     * 
     * @return ResponseEntity chứa danh sách FlashSaleDto (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;FlashSaleDto&gt;&gt; response = flashSaleController.getAllFlashSales();
     * List&lt;FlashSaleDto&gt; flashSales = response.getBody();
     * flashSales.forEach(fs -&gt; System.out.println(fs.getDiscountPercent() + "%"));
     * </pre>
     */
    @GetMapping("/admin/flash-sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FlashSaleDto>> getAllFlashSales() {
        log.info("📍 GET /api/admin/flash-sales");
        List<FlashSaleDto> flashSales = flashSaleService.getAllFlashSales();
        return ResponseEntity.ok(flashSales);
    }

    /**
     * Lấy tất cả flash sale đang active (Public)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy tất cả flash sale đang active</li>
     *   <li>Chỉ lấy các flash sale trong khoảng thời gian hiện tại</li>
     *   <li>Trả về danh sách flash sale</li>
     * </ol>
     * 
     * <p><b>Về flash sale active:</b>
     * <ul>
     *   <li>Flash sale được coi là active khi: startTime <= hiện tại <= endTime</li>
     *   <li>Chỉ hiển thị các flash sale đang diễn ra</li>
     *   <li>Không hiển thị flash sale đã kết thúc hoặc chưa bắt đầu</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Endpoint này công khai, không cần đăng nhập.
     * 
     * @return ResponseEntity chứa danh sách FlashSaleDto đang active (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;FlashSaleDto&gt;&gt; response = flashSaleController.getActiveFlashSales();
     * List&lt;FlashSaleDto&gt; flashSales = response.getBody();
     * flashSales.forEach(fs -&gt; System.out.println(fs.getDiscountPercent() + "%"));
     * </pre>
     */
    @GetMapping("/flash-sales/active")
    public ResponseEntity<List<FlashSaleDto>> getActiveFlashSales() {
        log.info("📍 GET /api/flash-sales/active");
        List<FlashSaleDto> flashSales = flashSaleService.getActiveFlashSales();
        return ResponseEntity.ok(flashSales);
    }

    /**
     * Lấy flash sale của sản phẩm (Public)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy flash sale của sản phẩm</li>
     *   <li>Chỉ lấy flash sale đang active</li>
     *   <li>Trả về flash sale nếu có, hoặc 404 nếu không có</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Endpoint này công khai, không cần đăng nhập</li>
     *   <li>Chỉ trả về flash sale đang active</li>
     *   <li>Nếu sản phẩm không có flash sale hoặc flash sale đã kết thúc, trả về 404</li>
     * </ul>
     * 
     * @param productId ID của sản phẩm cần lấy flash sale
     * @return ResponseEntity chứa FlashSaleDto nếu có (HTTP 200 OK), hoặc 404 nếu không có
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;FlashSaleDto&gt; response = flashSaleController.getFlashSaleByProductId(1L);
     * if (response.getStatusCode() == HttpStatus.OK) {
     *     FlashSaleDto flashSale = response.getBody();
     *     System.out.println("Giảm giá: " + flashSale.getDiscountPercent() + "%");
     * }
     * </pre>
     */
    @GetMapping("/flash-sales/product/{productId}")
    public ResponseEntity<FlashSaleDto> getFlashSaleByProductId(@PathVariable Long productId) {
        log.info("📍 GET /api/flash-sales/product/{}", productId);
        
        return flashSaleService.getFlashSaleByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Tạo flash sale mới (Admin)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào (thời gian, phần trăm giảm giá, sản phẩm)</li>
     *   <li>Gọi service để tạo flash sale mới</li>
     *   <li>Trả về flash sale vừa tạo</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Yêu cầu role ADMIN</li>
     *   <li>Thời gian kết thúc phải sau thời gian bắt đầu</li>
     *   <li>Phần trăm giảm giá phải > 0 và <= 100</li>
     *   <li>Sản phẩm phải tồn tại và đang active</li>
     * </ul>
     * 
     * @param dto DTO chứa thông tin flash sale cần tạo:
     *             - productId: ID sản phẩm (bắt buộc)
     *             - discountPercent: Phần trăm giảm giá (bắt buộc, 0-100)
     *             - startTime: Thời gian bắt đầu (bắt buộc)
     *             - endTime: Thời gian kết thúc (bắt buộc)
     * @return ResponseEntity chứa FlashSaleDto của flash sale vừa tạo (HTTP 200 OK)
     * @throws ApiException nếu validation thất bại hoặc sản phẩm không tồn tại
     * 
     * @example
     * <pre>
     * FlashSaleDto newFlashSale = new FlashSaleDto();
     * newFlashSale.setProductId(1L);
     * newFlashSale.setDiscountPercent(20);
     * newFlashSale.setStartTime(LocalDateTime.now());
     * newFlashSale.setEndTime(LocalDateTime.now().plusHours(24));
     * 
     * ResponseEntity&lt;FlashSaleDto&gt; response = flashSaleController.createFlashSale(newFlashSale);
     * FlashSaleDto created = response.getBody();
     * </pre>
     */
    @PostMapping("/admin/flash-sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlashSaleDto> createFlashSale(@Valid @RequestBody FlashSaleDto dto) {
        log.info("📍 POST /api/admin/flash-sales");
        FlashSaleDto created = flashSaleService.createFlashSale(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Cập nhật flash sale (Admin)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Tìm flash sale theo ID</li>
     *   <li>Validate dữ liệu đầu vào</li>
     *   <li>Gọi service để cập nhật flash sale</li>
     *   <li>Trả về flash sale sau khi cập nhật</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Yêu cầu role ADMIN</li>
     *   <li>Thời gian kết thúc phải sau thời gian bắt đầu</li>
     *   <li>Phần trăm giảm giá phải > 0 và <= 100</li>
     *   <li>Có thể cập nhật flash sale đã kết thúc (để sửa lỗi hoặc thống kê)</li>
     * </ul>
     * 
     * @param id ID của flash sale cần cập nhật
     * @param dto DTO chứa thông tin mới của flash sale
     * @return ResponseEntity chứa FlashSaleDto của flash sale sau khi cập nhật (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy flash sale hoặc validation thất bại
     * 
     * @example
     * <pre>
     * FlashSaleDto updateData = new FlashSaleDto();
     * updateData.setDiscountPercent(30); // Tăng giảm giá lên 30%
     * 
     * ResponseEntity&lt;FlashSaleDto&gt; response = flashSaleController.updateFlashSale(1, updateData);
     * FlashSaleDto updated = response.getBody();
     * </pre>
     */
    @PutMapping("/admin/flash-sales/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlashSaleDto> updateFlashSale(
            @PathVariable Integer id,
            @Valid @RequestBody FlashSaleDto dto
    ) {
        log.info("📍 PUT /api/admin/flash-sales/{}", id);
        FlashSaleDto updated = flashSaleService.updateFlashSale(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa flash sale (Admin)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Tìm flash sale theo ID</li>
     *   <li>Gọi service để xóa flash sale</li>
     *   <li>Trả về HTTP 204 No Content</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Yêu cầu role ADMIN</li>
     *   <li>Hành động này không thể hoàn tác</li>
     *   <li>Có thể xóa flash sale đang active (sẽ ngừng áp dụng ngay lập tức)</li>
     * </ul>
     * 
     * @param id ID của flash sale cần xóa
     * @return ResponseEntity với HTTP 204 No Content
     * @throws ApiException nếu không tìm thấy flash sale
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Void&gt; response = flashSaleController.deleteFlashSale(1);
     * // HTTP 204 No Content
     * </pre>
     */
    @DeleteMapping("/admin/flash-sales/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlashSale(@PathVariable Integer id) {
        log.info("📍 DELETE /api/admin/flash-sales/{}", id);
        flashSaleService.deleteFlashSale(id);
        return ResponseEntity.noContent().build();
    }
}

