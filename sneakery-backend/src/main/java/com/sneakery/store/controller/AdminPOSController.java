package com.sneakery.store.controller;

import com.sneakery.store.dto.OrderDto;
import com.sneakery.store.dto.POSOrderRequestDto;
import com.sneakery.store.service.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller xử lý POS (Point of Sale) cho Admin
 * 
 * <p>Controller này cung cấp các API endpoints cho admin để quản lý đơn hàng từ POS:
 * <ul>
 *   <li>Tạo đơn hàng từ POS (bán hàng tại cửa hàng)</li>
 *   <li>Lấy danh sách đơn hàng POS với phân trang</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu role ADMIN</li>
 *   <li>Sử dụng @PreAuthorize("hasRole('ADMIN')") để bảo vệ toàn bộ controller</li>
 * </ul>
 * 
 * <p><b>Về POS (Point of Sale):</b>
 * <ul>
 *   <li>POS là hệ thống bán hàng tại cửa hàng vật lý</li>
 *   <li>Đơn hàng POS khác với đơn hàng online: không cần địa chỉ giao hàng, thanh toán trực tiếp</li>
 *   <li>Đơn hàng POS có thể được tạo nhanh chóng cho khách hàng tại cửa hàng</li>
 *   <li>Đơn hàng POS thường có trạng thái "DELIVERED" ngay sau khi tạo (đã giao hàng tại cửa hàng)</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Tạo đơn hàng POS
 * POSOrderRequestDto posOrder = new POSOrderRequestDto();
 * posOrder.setCustomerName("Nguyễn Văn A");
 * posOrder.setItems(Arrays.asList(item1, item2));
 * ResponseEntity&lt;OrderDto&gt; response = adminPOSController.createPOSOrder(posOrder);
 * 
 * // Lấy danh sách đơn hàng POS
 * ResponseEntity&lt;Page&lt;OrderDto&gt;&gt; response2 = adminPOSController.getPOSOrders(0, 20);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Admin - POS", description = "API quản lý POS (Point of Sale) cho Admin")
@Slf4j
@RestController
@RequestMapping("/api/admin/pos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminPOSController {

    private final AdminOrderService adminOrderService;

    /**
     * Tạo đơn hàng từ POS (Point of Sale)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate dữ liệu đầu vào (tên khách hàng, danh sách items)</li>
     *   <li>Kiểm tra tồn kho của tất cả sản phẩm</li>
     *   <li>Tính toán tổng tiền</li>
     *   <li>Tạo đơn hàng POS (không cần địa chỉ giao hàng)</li>
     *   <li>Đặt trạng thái đơn hàng là "DELIVERED" (đã giao hàng tại cửa hàng)</li>
     *   <li>Trả về đơn hàng vừa tạo</li>
     * </ol>
     * 
     * <p><b>Về đơn hàng POS:</b>
     * <ul>
     *   <li>Đơn hàng POS không cần địa chỉ giao hàng (giao hàng tại cửa hàng)</li>
     *   <li>Đơn hàng POS thường có trạng thái "DELIVERED" ngay sau khi tạo</li>
     *   <li>Đơn hàng POS có thể có hoặc không có thông tin khách hàng (có thể là khách vãng lai)</li>
     *   <li>Thanh toán thường là tiền mặt (COD) tại cửa hàng</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Tất cả sản phẩm trong danh sách phải còn tồn kho đủ</li>
     *   <li>Tên khách hàng là tùy chọn (có thể là khách vãng lai)</li>
     *   <li>Sau khi tạo đơn hàng, tồn kho sẽ được cập nhật tự động</li>
     * </ul>
     * 
     * @param requestDto DTO chứa thông tin đơn hàng POS:
     *                   - customerName: Tên khách hàng (tùy chọn)
     *                   - items: Danh sách sản phẩm (bắt buộc, ít nhất 1)
     *                   - paymentMethod: Phương thức thanh toán (mặc định: "COD")
     * @return ResponseEntity chứa OrderDto của đơn hàng vừa tạo (HTTP 200 OK)
     * @throws ApiException nếu hết tồn kho hoặc validation thất bại
     * 
     * @example
     * <pre>
     * POSOrderRequestDto posOrder = new POSOrderRequestDto();
     * posOrder.setCustomerName("Nguyễn Văn A");
     * posOrder.setPaymentMethod("COD");
     * 
     * // Thêm items
     * POSOrderItemDto item1 = new POSOrderItemDto();
     * item1.setVariantId(1L);
     * item1.setQuantity(2);
     * posOrder.setItems(Arrays.asList(item1));
     * 
     * ResponseEntity&lt;OrderDto&gt; response = adminPOSController.createPOSOrder(posOrder);
     * OrderDto order = response.getBody();
     * </pre>
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createPOSOrder(
            @Valid @RequestBody POSOrderRequestDto requestDto
    ) {
        log.info("📍 POST /api/admin/pos/orders - Items: {}", requestDto.getItems().size());
        
        OrderDto order = adminOrderService.createPOSOrder(requestDto);
        return ResponseEntity.ok(order);
    }

    /**
     * Lấy danh sách đơn hàng POS với phân trang
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy danh sách đơn hàng POS với phân trang</li>
     *   <li>Chỉ lấy các đơn hàng POS (không bao gồm đơn hàng online)</li>
     *   <li>Trả về danh sách đơn hàng đã được phân trang</li>
     * </ol>
     * 
     * <p><b>Về phân trang:</b>
     * <ul>
     *   <li>Mặc định: page = 0, size = 20</li>
     *   <li>Trả về Page chứa: danh sách đơn hàng, tổng số trang, tổng số phần tử</li>
     *   <li>Đơn hàng được sắp xếp theo ngày tạo (mới nhất trước)</li>
     * </ul>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi đơn hàng bao gồm: ID, mã đơn hàng, tên khách hàng, tổng tiền, trạng thái, ngày tạo</li>
     *   <li>Chỉ bao gồm các đơn hàng POS (không bao gồm đơn hàng online)</li>
     * </ul>
     * 
     * @param page Số trang (bắt đầu từ 0, mặc định: 0)
     * @param size Số items mỗi trang (mặc định: 20)
     * @return ResponseEntity chứa Page&lt;OrderDto&gt; với danh sách đơn hàng POS (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * // Lấy trang đầu tiên, mỗi trang 20 đơn hàng POS
     * ResponseEntity&lt;Page&lt;OrderDto&gt;&gt; response = adminPOSController.getPOSOrders(0, 20);
     * Page&lt;OrderDto&gt; orders = response.getBody();
     * 
     * System.out.println("Tổng số đơn hàng POS: " + orders.getTotalElements());
     * orders.getContent().forEach(order -&gt; System.out.println(order.getOrderCode()));
     * </pre>
     */
    @GetMapping("/orders")
    public ResponseEntity<org.springframework.data.domain.Page<OrderDto>> getPOSOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("📋 GET /api/admin/pos/orders - page: {}, size: {}", page, size);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<OrderDto> orders = adminOrderService.getPOSOrders(pageable);
        
        return ResponseEntity.ok(orders);
    }
}

