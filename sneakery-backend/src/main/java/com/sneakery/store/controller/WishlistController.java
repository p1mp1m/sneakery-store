package com.sneakery.store.controller;

import com.sneakery.store.dto.WishlistDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller: WishlistController
 * API endpoints quản lý danh sách yêu thích
 */
@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "http://localhost:5173")
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Lấy danh sách wishlist của user hiện tại
     * GET /api/wishlist
     */
    @GetMapping
    public ResponseEntity<List<WishlistDto>> getMyWishlist(
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 GET /api/wishlist - User: {}", userPrincipal.getId());
        List<WishlistDto> wishlist = wishlistService.getWishlistByUserId(userPrincipal.getId());
        return ResponseEntity.ok(wishlist);
    }

    /**
     * Thêm sản phẩm vào wishlist
     * POST /api/wishlist/{productId}
     */
    @PostMapping("/{productId}")
    public ResponseEntity<WishlistDto> addToWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 POST /api/wishlist/{} - User: {}", productId, userPrincipal.getId());
        WishlistDto wishlistDto = wishlistService.addToWishlist(userPrincipal.getId(), productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistDto);
    }

    /**
     * Xóa sản phẩm khỏi wishlist
     * DELETE /api/wishlist/{productId}
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, String>> removeFromWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 DELETE /api/wishlist/{} - User: {}", productId, userPrincipal.getId());
        wishlistService.removeFromWishlist(userPrincipal.getId(), productId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa sản phẩm khỏi danh sách yêu thích"));
    }

    /**
     * Kiểm tra sản phẩm có trong wishlist không
     * GET /api/wishlist/check/{productId}
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkInWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal User userPrincipal
    ) {
        boolean inWishlist = wishlistService.isInWishlist(userPrincipal.getId(), productId);
        return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
    }

    /**
     * Đếm số sản phẩm trong wishlist
     * GET /api/wishlist/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countWishlistItems(
            @AuthenticationPrincipal User userPrincipal
    ) {
        long count = wishlistService.countWishlistItems(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Xóa toàn bộ wishlist
     * DELETE /api/wishlist/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearWishlist(
            @AuthenticationPrincipal User userPrincipal
    ) {
        wishlistService.clearWishlist(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ danh sách yêu thích"));
    }
}

