package com.sneakery.store.controller;

import com.sneakery.store.dto.AddToCartRequestDto;
import com.sneakery.store.dto.CartDto;
import com.sneakery.store.dto.UpdateCartItemRequestDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.CartService;
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

import java.util.Map;

/**
 * Controller xử lý giỏ hàng cho User
 *
 * <p>Controller này cung cấp các API endpoints cho user để quản lý giỏ hàng:
 * <ul>
 *   <li>Lấy giỏ hàng của user hiện tại</li>
 *   <li>Thêm/Cập nhật sản phẩm vào giỏ hàng</li>
 *   <li>Xóa sản phẩm khỏi giỏ hàng</li>
 *   <li>Xóa toàn bộ giỏ hàng</li>
 * </ul>
 *
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu đăng nhập (isAuthenticated())</li>
 *   <li>User chỉ có thể quản lý giỏ hàng của chính mình</li>
 *   <li>User được lấy từ JWT token (AuthenticationPrincipal)</li>
 * </ul>
 *
 * <p><b>Về giỏ hàng:</b>
 * <ul>
 *   <li>Mỗi user có 1 giỏ hàng duy nhất</li>
 *   <li>Giỏ hàng được tự động tạo khi user thêm sản phẩm đầu tiên</li>
 *   <li>Giỏ hàng sẽ bị xóa sau khi checkout thành công</li>
 *   <li>Mỗi item trong giỏ hàng tương ứng với 1 variant (size, màu sắc)</li>
 * </ul>
 *
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy giỏ hàng
 * ResponseEntity&lt;CartDto&gt; response = cartController.getMyCart(currentUser);
 *
 * // Thêm sản phẩm vào giỏ hàng
 * AddToCartRequestDto request = new AddToCartRequestDto();
 * request.setVariantId(1L);
 * request.setQuantity(2);
 * ResponseEntity&lt;CartDto&gt; response2 = cartController.addItemToMyCart(currentUser, request);
 * </pre>
 *
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Cart", description = "API quản lý giỏ hàng cho User")
@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class CartController {

    private final CartService cartService;

    /**
     * Lấy giỏ hàng của user hiện tại
     *
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để lấy giỏ hàng của user</li>
     *   <li>Nếu chưa có giỏ hàng, sẽ tự động tạo giỏ hàng mới (rỗng)</li>
     *   <li>Trả về giỏ hàng với đầy đủ thông tin items</li>
     * </ol>
     *
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa CartDto với thông tin giỏ hàng (HTTP 200 OK)
     */
    @Operation(summary = "Lấy giỏ hàng", description = "Lấy giỏ hàng của user hiện tại. Nếu chưa có giỏ hàng, sẽ tự động tạo giỏ hàng mới (rỗng).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy giỏ hàng thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    @GetMapping
    public ResponseEntity<CartDto> getMyCart(
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 GET /api/cart - User: {}", userPrincipal.getId());
        CartDto cart = cartService.getCartByUserId(userPrincipal.getId());
        return ResponseEntity.ok(cart);
    }

    /**
     * Thêm/Cập nhật sản phẩm vào giỏ hàng (CỘNG DỒN)
     *
     * <p>Dùng cho nút: <b>Thêm vào giỏ hàng</b></p>
     */
    @Operation(summary = "Thêm/Cập nhật sản phẩm vào giỏ hàng (cộng dồn)")
    @PostMapping("/item")
    public ResponseEntity<CartDto> addItemToMyCart(
            @AuthenticationPrincipal User userPrincipal,
            @Valid @RequestBody AddToCartRequestDto requestDto
    ) {
        log.info(
                "📍 POST /api/cart/item - User: {}, VariantId: {}, Quantity:+{}",
                userPrincipal.getId(),
                requestDto.getVariantId(),
                requestDto.getQuantity()
        );

        CartDto cart = cartService.addItemToCart(userPrincipal.getId(), requestDto);
        return ResponseEntity.ok(cart);
    }

    /**
     * CẬP NHẬT SỐ LƯỢNG SẢN PHẨM TRONG GIỎ (KHÔNG CỘNG DỒN)
     *
     * <p>
     * API này dùng cho nút <b>+ / -</b> trong giỏ hàng.
     * Số lượng được SET trực tiếp, KHÔNG cộng thêm.
     * </p>
     *
     * @param userPrincipal User hiện tại (từ JWT)
     * @param requestDto    DTO chứa variantId và số lượng mới
     * @return CartDto giỏ hàng sau khi cập nhật
     */
    @Operation(
            summary = "Cập nhật số lượng sản phẩm trong giỏ hàng (+ / -)",
            description = "SET số lượng mới cho sản phẩm trong giỏ hàng. KHÔNG cộng dồn."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy sản phẩm"),
            @ApiResponse(responseCode = "409", description = "Không đủ tồn kho")
    })
    @PutMapping("/item")
    public ResponseEntity<CartDto> updateCartItemQuantity(
            @AuthenticationPrincipal User userPrincipal,
            @Valid @RequestBody UpdateCartItemRequestDto requestDto
    ) {
        log.info(
                "📍 PUT /api/cart/item - User: {}, VariantId: {}, NewQuantity:{}",
                userPrincipal.getId(),
                requestDto.getVariantId(),
                requestDto.getQuantity()
        );

        CartDto cart = cartService.updateItemQuantity(userPrincipal.getId(), requestDto);

        return ResponseEntity.ok(cart);
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
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
     * Xóa toàn bộ giỏ hàng
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
