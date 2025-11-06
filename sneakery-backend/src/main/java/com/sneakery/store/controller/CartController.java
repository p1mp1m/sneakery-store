package com.sneakery.store.controller;

import com.sneakery.store.dto.AddToCartRequestDto;
import com.sneakery.store.dto.CartDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class CartController {

    private final CartService cartService;

    /**
     * Lấy giỏ hàng của user đang đăng nhập
     */
    @GetMapping
    public ResponseEntity<CartDto> getMyCart(
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 GET /api/cart - User: {}", userPrincipal.getId());
        CartDto cart = cartService.getCartByUserId(userPrincipal.getId());
        return ResponseEntity.ok(cart);
    }

    /**
     * Thêm/Cập nhật sản phẩm vào giỏ
     */
    @PostMapping("/item")
    public ResponseEntity<CartDto> addItemToMyCart(
            @AuthenticationPrincipal User userPrincipal,
            @Valid @RequestBody AddToCartRequestDto requestDto
    ) {
        log.info("📍 POST /api/cart/item - User: {}, VariantId: {}", userPrincipal.getId(), requestDto.getVariantId());
        CartDto cart = cartService.addItemToCart(userPrincipal.getId(), requestDto);
        return ResponseEntity.ok(cart);
    }

    /**
     * Xóa 1 sản phẩm khỏi giỏ
     * (Chúng ta dùng variantId để xóa)
     */
    @DeleteMapping("/item/{variantId}")
    public ResponseEntity<CartDto> removeItemFromMyCart(
            @AuthenticationPrincipal User userPrincipal,
            @PathVariable Long variantId
    ) {
        log.info("📍 DELETE /api/cart/item/{} - User: {}", variantId, userPrincipal.getId());
        CartDto cart = cartService.removeItemFromCart(userPrincipal.getId(), variantId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Xóa toàn bộ giỏ hàng (clear all items)
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearMyCart(
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 DELETE /api/cart - User: {}", userPrincipal.getId());
        cartService.clearCart(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ giỏ hàng"));
    }
}