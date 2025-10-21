package com.sneakery.store.controller;

import com.sneakery.store.dto.AddToCartRequestDto;
import com.sneakery.store.dto.CartDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Cho phép VueJS gọi
public class CartController {

    private final CartService cartService;

    /**
     * Lấy giỏ hàng của user đang đăng nhập
     */
    @GetMapping
    public ResponseEntity<CartDto> getMyCart(
            @AuthenticationPrincipal User userPrincipal // Tự động lấy user từ JWT
    ) {
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
        CartDto cart = cartService.removeItemFromCart(userPrincipal.getId(), variantId);
        return ResponseEntity.ok(cart);
    }
}