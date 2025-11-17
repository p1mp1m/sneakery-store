package com.sneakery.store.controller;

import com.sneakery.store.dto.CheckoutRequestDto;
import com.sneakery.store.dto.CouponDto;
import com.sneakery.store.dto.CreateReturnRequestDto;
import com.sneakery.store.dto.OrderDto;
import com.sneakery.store.dto.OrderSummaryDto;
import com.sneakery.store.dto.ReturnRequestDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.CouponService;
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

@Tag(name = "Orders", description = "API quản lý đơn hàng cho User")
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class OrderController {

    private final OrderService orderService;
    private final CouponService couponService;

    // ==========================
    //  CHECKOUT
    // ==========================
    @Operation(summary = "Tạo đơn hàng từ giỏ hàng (Checkout)", description = "Checkout giỏ hàng của user hiện tại.")
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

    // ==========================
    //  GET MY ORDERS
    // ==========================
    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getMyOrders(
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 GET /api/orders - User: {}", userPrincipal.getId());
        List<OrderSummaryDto> orders = orderService.getMyOrders(userPrincipal.getId());
        return ResponseEntity.ok(orders);
    }

    // ==========================
    //  VALIDATE COUPON
    // ==========================
    @Operation(summary = "Validate coupon code", description = "Validate mã giảm giá cho user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon hợp lệ"),
            @ApiResponse(responseCode = "400", description = "Coupon không hợp lệ hoặc đã hết hạn"),
            @ApiResponse(responseCode = "404", description = "Coupon không tồn tại")
    })
    @GetMapping("/coupons/validate/{code}")
    public ResponseEntity<CouponDto> validateCoupon(@PathVariable String code) {
        log.info("📍 GET /api/orders/coupons/validate/{}", code);
        CouponDto coupon = couponService.validateCouponCode(code);
        return ResponseEntity.ok(coupon);
    }

    // ==========================
    //  GET ACTIVE COUPONS
    // ==========================
    @Operation(summary = "Get active coupons", description = "Lấy danh sách coupon đang hoạt động.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Danh sách thành công")
    })
    @GetMapping("/coupons/active")
    public ResponseEntity<List<CouponDto>> getActiveCoupons() {
        log.info("📍 GET /api/orders/coupons/active");
        List<CouponDto> coupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(coupons);
    }

    // ==========================
    //  CREATE RETURN REQUEST
    // ==========================
    @Operation(summary = "Tạo yêu cầu đổi trả", description = "Tạo return request khi đơn hàng đã hoàn thành.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo yêu cầu thành công"),
            @ApiResponse(responseCode = "400", description = "Đơn hàng chưa hoàn thành hoặc đã có return request"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng")
    })
    @PostMapping("/{orderId}/return")
    public ResponseEntity<ReturnRequestDto> createReturnRequest(
            @AuthenticationPrincipal User userPrincipal,
            @PathVariable Long orderId,
            @Valid @RequestBody CreateReturnRequestDto requestDto
    ) {
        log.info("📍 POST /api/orders/{}/return - User: {}", orderId, userPrincipal.getId());
        ReturnRequestDto rr = orderService.createReturnRequest(orderId, userPrincipal.getId(), requestDto);
        return ResponseEntity.ok(rr);
    }

    // ==========================
    //  CANCEL ORDER
    // ==========================
    @Operation(summary = "Hủy đơn hàng", description = "Chỉ hủy khi đơn hàng đang ở trạng thái 'pending'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hủy đơn thành công"),
            @ApiResponse(responseCode = "400", description = "Đơn hàng không thể hủy"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn hàng")
    })
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(
            @AuthenticationPrincipal User userPrincipal,
            @PathVariable Long orderId
    ) {
        log.info("📍 PUT /api/orders/{}/cancel - User: {}", orderId, userPrincipal.getId());
        OrderDto order = orderService.cancelOrder(orderId, userPrincipal.getId());
        return ResponseEntity.ok(order);
    }

    // ==========================
    //  GET ORDER DETAIL
    // ==========================
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
