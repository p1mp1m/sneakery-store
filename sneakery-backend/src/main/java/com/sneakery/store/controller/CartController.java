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
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Bao gồm tất cả items trong giỏ hàng: sản phẩm, variant, số lượng, giá</li>
     *   <li>Bao gồm tổng tiền của giỏ hàng</li>
     *   <li>Sử dụng query tối ưu để load tất cả dữ liệu trong 1 lần</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa CartDto với thông tin giỏ hàng (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;CartDto&gt; response = cartController.getMyCart(currentUser);
     * CartDto cart = response.getBody();
     * System.out.println(cart.getItems().size()); // Số lượng items trong giỏ hàng
     * System.out.println(cart.getTotalAmount()); // Tổng tiền giỏ hàng
     * </pre>
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
     * Thêm/Cập nhật sản phẩm vào giỏ hàng
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Lấy hoặc tạo giỏ hàng của user</li>
     *   <li>Kiểm tra variant có tồn tại và còn tồn kho không</li>
     *   <li>Nếu variant đã có trong giỏ hàng: Cập nhật số lượng</li>
     *   <li>Nếu variant chưa có trong giỏ hàng: Thêm mới</li>
     *   <li>Kiểm tra tồn kho đủ cho số lượng mới</li>
     *   <li>Trả về giỏ hàng sau khi cập nhật</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Nếu variant đã có trong giỏ hàng, số lượng sẽ được cập nhật (không cộng dồn)</li>
     *   <li>Số lượng phải > 0 và <= tồn kho của variant</li>
     *   <li>Nếu tồn kho không đủ, sẽ throw ApiException</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @param requestDto DTO chứa thông tin sản phẩm cần thêm:
     *                   - variantId: ID của variant (bắt buộc)
     *                   - quantity: Số lượng (bắt buộc, phải > 0)
     * @return ResponseEntity chứa CartDto với giỏ hàng sau khi cập nhật (HTTP 200 OK)
     * @throws ApiException nếu variant không tồn tại, hết tồn kho, hoặc validation thất bại
     * 
     * @example
     * <pre>
     * AddToCartRequestDto request = new AddToCartRequestDto();
     * request.setVariantId(1L); // Variant: Nike Air Max 90 - Size 40 - Đỏ
     * request.setQuantity(2); // Thêm 2 đôi
     * 
     * ResponseEntity&lt;CartDto&gt; response = cartController.addItemToMyCart(currentUser, request);
     * CartDto cart = response.getBody();
     * </pre>
     */
    @Operation(summary = "Thêm/Cập nhật sản phẩm vào giỏ hàng", description = "Thêm hoặc cập nhật số lượng sản phẩm trong giỏ hàng. Nếu variant đã có trong giỏ hàng, số lượng sẽ được cập nhật.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Thêm/Cập nhật thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
        @ApiResponse(responseCode = "404", description = "Variant không tồn tại"),
        @ApiResponse(responseCode = "409", description = "Hết tồn kho")
    })
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
     * Xóa sản phẩm khỏi giỏ hàng
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Lấy giỏ hàng của user</li>
     *   <li>Tìm item có variantId tương ứng</li>
     *   <li>Xóa item khỏi giỏ hàng</li>
     *   <li>Trả về giỏ hàng sau khi xóa</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Sử dụng variantId để xác định item cần xóa</li>
     *   <li>Nếu variant không có trong giỏ hàng, sẽ không có gì xảy ra (không throw exception)</li>
     *   <li>Sau khi xóa, tổng tiền giỏ hàng sẽ được tính lại tự động</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @param variantId ID của variant cần xóa khỏi giỏ hàng
     * @return ResponseEntity chứa CartDto với giỏ hàng sau khi xóa (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * // Xóa variant có ID = 1 khỏi giỏ hàng
     * ResponseEntity&lt;CartDto&gt; response = cartController.removeItemFromMyCart(currentUser, 1L);
     * CartDto cart = response.getBody();
     * </pre>
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
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Lấy giỏ hàng của user</li>
     *   <li>Xóa tất cả items trong giỏ hàng</li>
     *   <li>Trả về thông báo thành công</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Giỏ hàng sẽ trở thành rỗng (không có items)</li>
     *   <li>Giỏ hàng vẫn tồn tại (không bị xóa), chỉ xóa các items</li>
     *   <li>Hành động này không thể hoàn tác</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa thông báo thành công (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Map&lt;String, String&gt;&gt; response = cartController.clearMyCart(currentUser);
     * String message = response.getBody().get("message"); // "Đã xóa toàn bộ giỏ hàng"
     * </pre>
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