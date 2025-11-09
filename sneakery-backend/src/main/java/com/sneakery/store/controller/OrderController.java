package com.sneakery.store.controller;

import com.sneakery.store.dto.CheckoutRequestDto;
import com.sneakery.store.dto.OrderDto;
import com.sneakery.store.dto.OrderSummaryDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Controller xử lý đơn hàng cho User
 * 
 * <p>Controller này cung cấp các API endpoints cho user để:
 * <ul>
 *   <li>Tạo đơn hàng từ giỏ hàng (checkout)</li>
 *   <li>Lấy danh sách đơn hàng của chính mình</li>
 *   <li>Lấy thông tin chi tiết đơn hàng theo ID</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu đăng nhập (isAuthenticated())</li>
 *   <li>User chỉ có thể xem và quản lý đơn hàng của chính mình</li>
 *   <li>User được lấy từ JWT token (AuthenticationPrincipal)</li>
 * </ul>
 * 
 * <p><b>Về checkout:</b>
 * <ul>
 *   <li>Checkout sẽ tạo đơn hàng từ giỏ hàng hiện tại</li>
 *   <li>Sau khi checkout thành công, giỏ hàng sẽ được xóa</li>
 *   <li>Hệ thống sẽ tự động áp dụng coupon (nếu có) và tính điểm loyalty</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Checkout từ giỏ hàng
 * CheckoutRequestDto checkoutRequest = new CheckoutRequestDto();
 * checkoutRequest.setAddressId(1L);
 * checkoutRequest.setPaymentMethod("COD");
 * ResponseEntity&lt;OrderDto&gt; response = orderController.checkout(currentUser, checkoutRequest);
 * 
 * // Lấy danh sách đơn hàng của mình
 * ResponseEntity&lt;List&lt;OrderSummaryDto&gt;&gt; orders = orderController.getMyOrders(currentUser);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Orders", description = "API quản lý đơn hàng cho User")
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class OrderController {

    private final OrderService orderService;

    /**
     * Tạo đơn hàng từ giỏ hàng (Checkout)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy giỏ hàng của user hiện tại</li>
     *   <li>Validate giỏ hàng (không được trống)</li>
     *   <li>Lấy địa chỉ giao hàng</li>
     *   <li>Tính toán tổng tiền (bao gồm coupon nếu có)</li>
     *   <li>Tạo đơn hàng mới</li>
     *   <li>Xóa giỏ hàng sau khi tạo đơn hàng thành công</li>
     *   <li>Áp dụng coupon và tính điểm loyalty (nếu có)</li>
     *   <li>Gửi email xác nhận đơn hàng</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Giỏ hàng phải có ít nhất 1 sản phẩm</li>
     *   <li>Địa chỉ giao hàng phải tồn tại và thuộc về user hiện tại</li>
     *   <li>Sau khi checkout thành công, giỏ hàng sẽ được xóa</li>
     *   <li>Nếu có coupon, sẽ tự động áp dụng và tính lại tổng tiền</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @param requestDto DTO chứa thông tin checkout:
     *                   - addressId: ID địa chỉ giao hàng (bắt buộc)
     *                   - paymentMethod: Phương thức thanh toán (bắt buộc: "COD", "BANK_TRANSFER", "CREDIT_CARD")
     *                   - couponCode: Mã coupon (tùy chọn)
     *                   - note: Ghi chú đơn hàng (tùy chọn)
     * @return ResponseEntity chứa OrderDto của đơn hàng vừa tạo (HTTP 200 OK)
     * @throws ApiException nếu giỏ hàng trống, địa chỉ không tồn tại, hoặc validation thất bại
     * 
     * @example
     * <pre>
     * CheckoutRequestDto checkoutRequest = new CheckoutRequestDto();
     * checkoutRequest.setAddressId(1L);
     * checkoutRequest.setPaymentMethod("COD");
     * checkoutRequest.setCouponCode("SALE10"); // Tùy chọn
     * 
     * ResponseEntity&lt;OrderDto&gt; response = orderController.checkout(currentUser, checkoutRequest);
     * OrderDto order = response.getBody();
     * System.out.println(order.getTotalAmount()); // Tổng tiền đơn hàng
     * </pre>
     */
    @Operation(summary = "Tạo đơn hàng từ giỏ hàng (Checkout)", description = "Tạo đơn hàng từ giỏ hàng hiện tại. Sau khi checkout thành công, giỏ hàng sẽ được xóa.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Checkout thành công"),
        @ApiResponse(responseCode = "400", description = "Giỏ hàng trống hoặc dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Địa chỉ không tồn tại"),
        @ApiResponse(responseCode = "409", description = "Hết tồn kho")
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(
            @AuthenticationPrincipal User userPrincipal,
            @Valid @RequestBody CheckoutRequestDto requestDto
    ) {
        log.info("📍 POST /api/orders/checkout - User: {}", userPrincipal.getId());
        OrderDto order = orderService.createOrderFromCart(userPrincipal.getId(), requestDto);
        return ResponseEntity.ok(order);
    }

    /**
     * Lấy danh sách đơn hàng của user hiện tại
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để lấy tất cả đơn hàng của user</li>
     *   <li>Trả về danh sách đơn hàng (dạng summary - không có chi tiết items)</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi đơn hàng bao gồm: ID, mã đơn hàng, tổng tiền, trạng thái, ngày tạo</li>
     *   <li>Không bao gồm chi tiết items (chỉ dùng cho danh sách)</li>
     *   <li>Đơn hàng được sắp xếp theo ngày tạo (mới nhất trước)</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> User chỉ có thể xem đơn hàng của chính mình.
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa danh sách OrderSummaryDto (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;OrderSummaryDto&gt;&gt; response = orderController.getMyOrders(currentUser);
     * List&lt;OrderSummaryDto&gt; orders = response.getBody();
     * orders.forEach(order -&gt; System.out.println(order.getOrderCode()));
     * </pre>
     */
    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getMyOrders(
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 GET /api/orders - User: {}", userPrincipal.getId());
        List<OrderSummaryDto> orders = orderService.getMyOrders(userPrincipal.getId());
        return ResponseEntity.ok(orders);
    }

    /**
     * Lấy thông tin chi tiết đơn hàng theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để lấy đơn hàng theo ID</li>
     *   <li>Kiểm tra đơn hàng có thuộc về user hiện tại không</li>
     *   <li>Trả về thông tin chi tiết đơn hàng</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Bao gồm tất cả thông tin đơn hàng: mã đơn hàng, tổng tiền, trạng thái, địa chỉ giao hàng</li>
     *   <li>Bao gồm danh sách items với đầy đủ thông tin: sản phẩm, variant, số lượng, giá</li>
     *   <li>Bao gồm thông tin thanh toán và coupon (nếu có)</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> User chỉ có thể xem đơn hàng của chính mình.
     * Nếu cố gắng xem đơn hàng của user khác, sẽ throw ApiException.
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @param orderId ID của đơn hàng cần lấy
     * @return ResponseEntity chứa OrderDto với thông tin chi tiết đơn hàng (HTTP 200 OK)
     * @throws ApiException nếu không tìm thấy đơn hàng hoặc đơn hàng không thuộc về user hiện tại
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;OrderDto&gt; response = orderController.getMyOrderById(currentUser, 1L);
     * OrderDto order = response.getBody();
     * System.out.println(order.getOrderCode()); // Mã đơn hàng
     * System.out.println(order.getItems().size()); // Số lượng items
     * </pre>
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getMyOrderById(
            @AuthenticationPrincipal User userPrincipal,
            @PathVariable Long orderId
    ) {
        log.info("📍 GET /api/orders/{} - User: {}", orderId, userPrincipal.getId());
        OrderDto order = orderService.getMyOrderById(orderId, userPrincipal.getId());
        return ResponseEntity.ok(order);
    }
}

