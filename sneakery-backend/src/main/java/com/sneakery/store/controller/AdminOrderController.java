package com.sneakery.store.controller;

import com.sneakery.store.dto.AdminOrderDetailDto;
import com.sneakery.store.dto.AdminOrderListDto;
import com.sneakery.store.dto.OrderStatusUpdateRequestDto;
import com.sneakery.store.service.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller quản lý đơn hàng cho Admin
 * 
 * <p>Controller này cung cấp các API endpoints cho admin để quản lý đơn hàng:
 * <ul>
 *   <li>Lấy danh sách đơn hàng với phân trang và lọc</li>
 *   <li>Lấy thông tin chi tiết đơn hàng theo ID</li>
 *   <li>Cập nhật trạng thái đơn hàng</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu role ADMIN hoặc MODERATOR</li>
 *   <li>Sử dụng @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')") để bảo vệ toàn bộ controller</li>
 * </ul>
 * 
 * <p><b>Về lọc và phân trang:</b>
 * <ul>
 *   <li>Hỗ trợ tìm kiếm theo mã đơn hàng hoặc tên khách hàng</li>
 *   <li>Hỗ trợ lọc theo trạng thái đơn hàng</li>
 *   <li>Mặc định sắp xếp theo ngày tạo (mới nhất trước)</li>
 * </ul>
 * 
 * <p><b>Về trạng thái đơn hàng:</b>
 * <ul>
 *   <li>PENDING: Chờ xử lý</li>
 *   <li>CONFIRMED: Đã xác nhận</li>
 *   <li>PROCESSING: Đang xử lý</li>
 *   <li>SHIPPED: Đã giao hàng</li>
 *   <li>DELIVERED: Đã nhận hàng</li>
 *   <li>CANCELLED: Đã hủy</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy danh sách đơn hàng với phân trang
 * ResponseEntity&lt;Page&lt;AdminOrderListDto&gt;&gt; response = adminOrderController.getAllOrders(0, 10, null, null);
 * 
 * // Lấy đơn hàng theo ID
 * ResponseEntity&lt;AdminOrderDetailDto&gt; order = adminOrderController.getOrderById(1L);
 * 
 * // Cập nhật trạng thái đơn hàng
 * OrderStatusUpdateRequestDto updateRequest = new OrderStatusUpdateRequestDto();
 * updateRequest.setStatus("SHIPPED");
 * ResponseEntity&lt;AdminOrderDetailDto&gt; updated = adminOrderController.updateOrderStatus(1L, updateRequest);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Admin - Orders", description = "API quản lý đơn hàng cho Admin")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    /**
     * Lấy danh sách đơn hàng với phân trang và lọc
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Xây dựng Pageable từ các tham số phân trang</li>
     *   <li>Nếu có search hoặc status filter, sử dụng method với filters</li>
     *   <li>Nếu không có filter, sử dụng method mặc định</li>
     *   <li>Trả về danh sách đơn hàng đã được lọc và phân trang</li>
     * </ol>
     * 
     * <p><b>Về phân trang:</b>
     * <ul>
     *   <li>Mặc định: page = 0, size = 10</li>
     *   <li>Mặc định sắp xếp theo ngày tạo (mới nhất trước)</li>
     *   <li>Trả về Page chứa: danh sách đơn hàng, tổng số trang, tổng số phần tử</li>
     * </ul>
     * 
     * <p><b>Về lọc:</b>
     * <ul>
     *   <li>Search: Tìm kiếm theo mã đơn hàng hoặc tên khách hàng (tùy chọn)</li>
     *   <li>Status: Lọc theo trạng thái đơn hàng (tùy chọn: "PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED")</li>
     * </ul>
     * 
     * @param page Số trang (bắt đầu từ 0, mặc định: 0)
     * @param size Số items mỗi trang (mặc định: 10)
     * @param search Từ khóa tìm kiếm (mã đơn hàng hoặc tên khách hàng, tùy chọn)
     * @param status Trạng thái đơn hàng để lọc (tùy chọn)
     * @return ResponseEntity chứa Page&lt;AdminOrderListDto&gt; với danh sách đơn hàng đã lọc (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * // Lấy trang đầu tiên, mỗi trang 20 đơn hàng
     * ResponseEntity&lt;Page&lt;AdminOrderListDto&gt;&gt; response = adminOrderController.getAllOrders(0, 20, null, null);
     * 
     * // Tìm kiếm đơn hàng có mã chứa "ORD"
     * ResponseEntity&lt;Page&lt;AdminOrderListDto&gt;&gt; response2 = adminOrderController.getAllOrders(0, 10, "ORD", null);
     * 
     * // Lọc đơn hàng theo trạng thái
     * ResponseEntity&lt;Page&lt;AdminOrderListDto&gt;&gt; response3 = adminOrderController.getAllOrders(0, 10, null, "PENDING");
     * </pre>
     */
    @GetMapping
    public ResponseEntity<Page<AdminOrderListDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Nếu có search hoặc status filter, sử dụng method với filters
        if ((search != null && !search.trim().isEmpty()) || 
            (status != null && !status.trim().isEmpty())) {
            Page<AdminOrderListDto> orderPage = adminOrderService.getAllOrdersWithFilters(search, status, pageable);
            return ResponseEntity.ok(orderPage);
        }
        
        // Nếu không có filter, sử dụng method mặc định
        Page<AdminOrderListDto> orderPage = adminOrderService.getAllOrders(pageable);
        return ResponseEntity.ok(orderPage);
    }

    /**
     * Lấy thông tin chi tiết đơn hàng theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy đơn hàng theo ID</li>
     *   <li>Trả về thông tin chi tiết đầy đủ của đơn hàng</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Thông tin cơ bản: mã đơn hàng, tổng tiền, trạng thái, ngày tạo</li>
     *   <li>Thông tin khách hàng: tên, email, số điện thoại</li>
     *   <li>Địa chỉ giao hàng: đầy đủ thông tin địa chỉ</li>
     *   <li>Danh sách items: sản phẩm, variant, số lượng, giá</li>
     *   <li>Thông tin thanh toán và coupon (nếu có)</li>
     * </ul>
     * 
     * @param id ID của đơn hàng cần lấy
     * @return ResponseEntity chứa AdminOrderDetailDto với thông tin chi tiết đơn hàng (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy đơn hàng với ID này
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;AdminOrderDetailDto&gt; response = adminOrderController.getOrderById(1L);
     * AdminOrderDetailDto order = response.getBody();
     * System.out.println(order.getOrderCode()); // Mã đơn hàng
     * System.out.println(order.getItems().size()); // Số lượng items
     * </pre>
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderDetailDto> getOrderById(@PathVariable Long id) {
        AdminOrderDetailDto orderDetail = adminOrderService.getOrderById(id);
        return ResponseEntity.ok(orderDetail);
    }

    /**
     * Cập nhật trạng thái đơn hàng
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Tìm đơn hàng theo ID</li>
     *   <li>Validate trạng thái mới (phải hợp lệ)</li>
     *   <li>Cập nhật trạng thái đơn hàng</li>
     *   <li>Gửi email thông báo cho khách hàng (nếu cần)</li>
     *   <li>Trả về đơn hàng sau khi cập nhật</li>
     * </ol>
     * 
     * <p><b>Về trạng thái đơn hàng:</b>
     * <ul>
     *   <li>PENDING: Chờ xử lý (trạng thái ban đầu)</li>
     *   <li>CONFIRMED: Đã xác nhận</li>
     *   <li>PROCESSING: Đang xử lý</li>
     *   <li>SHIPPED: Đã giao hàng</li>
     *   <li>DELIVERED: Đã nhận hàng</li>
     *   <li>CANCELLED: Đã hủy</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Trạng thái phải hợp lệ (một trong các giá trị trên)</li>
     *   <li>Một số trạng thái có thể yêu cầu quyền cao hơn (ví dụ: CANCELLED)</li>
     *   <li>Khi cập nhật trạng thái, hệ thống có thể tự động gửi email thông báo cho khách hàng</li>
     * </ul>
     * 
     * @param id ID của đơn hàng cần cập nhật
     * @param requestDto DTO chứa trạng thái mới:
     *                   - status: Trạng thái mới (bắt buộc, phải hợp lệ)
     * @return ResponseEntity chứa AdminOrderDetailDto của đơn hàng sau khi cập nhật (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy đơn hàng hoặc trạng thái không hợp lệ
     * 
     * @example
     * <pre>
     * OrderStatusUpdateRequestDto updateRequest = new OrderStatusUpdateRequestDto();
     * updateRequest.setStatus("SHIPPED");
     * 
     * ResponseEntity&lt;AdminOrderDetailDto&gt; response = adminOrderController.updateOrderStatus(1L, updateRequest);
     * AdminOrderDetailDto updated = response.getBody();
     * System.out.println(updated.getStatus()); // "SHIPPED"
     * </pre>
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AdminOrderDetailDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequestDto requestDto
    ) {
        AdminOrderDetailDto updatedOrder = adminOrderService.updateOrderStatus(id, requestDto.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }
}