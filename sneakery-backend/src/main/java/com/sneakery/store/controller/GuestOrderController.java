package com.sneakery.store.controller;

import com.sneakery.store.dto.AddToCartRequestDto;
import com.sneakery.store.dto.CartDto;
import com.sneakery.store.dto.GuestCheckoutRequestDto;
import com.sneakery.store.dto.OrderDto;
import com.sneakery.store.service.CartService;
import com.sneakery.store.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class GuestOrderController {

    private final CartService cartService;
    private final OrderService orderService;

    /**
     * Lấy giỏ hàng của guest
     */
    @GetMapping("/cart")
    public ResponseEntity<CartDto> getGuestCart(@RequestParam String sessionId) {
        log.info("📍 GET /api/guest/cart - SessionId: {}", sessionId);
        CartDto cart = cartService.getCartBySessionId(sessionId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Thêm sản phẩm vào guest cart
     */
    @PostMapping("/cart/item")
    public ResponseEntity<CartDto> addItemToGuestCart(
            @RequestParam String sessionId,
            @Valid @RequestBody AddToCartRequestDto requestDto
    ) {
        log.info("📍 POST /api/guest/cart/item - SessionId: {}, VariantId: {}", sessionId, requestDto.getVariantId());
        CartDto cart = cartService.addItemToGuestCart(sessionId, requestDto);
        return ResponseEntity.ok(cart);
    }

    /**
     * Xóa sản phẩm khỏi guest cart
     */
    @DeleteMapping("/cart/item/{variantId}")
    public ResponseEntity<CartDto> removeItemFromGuestCart(
            @RequestParam String sessionId,
            @PathVariable Long variantId
    ) {
        log.info("📍 DELETE /api/guest/cart/item/{} - SessionId: {}", variantId, sessionId);
        CartDto cart = cartService.removeItemFromGuestCart(sessionId, variantId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Guest checkout - Tạo đơn hàng từ guest cart
     */
    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> guestCheckout(
            @Valid @RequestBody GuestCheckoutRequestDto requestDto
    ) {
        log.info("📍 POST /api/guest/checkout - SessionId: {}", requestDto.getSessionId());
        OrderDto order = orderService.createGuestOrderFromCart(requestDto.getSessionId(), requestDto);
        return ResponseEntity.ok(order);
    }
}

