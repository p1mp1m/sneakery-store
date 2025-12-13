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
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

/**
 * Controller xử lý danh sách yêu thích (Wishlist) cho User
 * 
 * <p>Controller này cung cấp các API endpoints cho user để quản lý danh sách yêu thích:
 * <ul>
 *   <li>Lấy danh sách sản phẩm yêu thích</li>
 *   <li>Thêm sản phẩm vào danh sách yêu thích</li>
 *   <li>Xóa sản phẩm khỏi danh sách yêu thích</li>
 *   <li>Kiểm tra sản phẩm có trong danh sách yêu thích không</li>
 *   <li>Đếm số sản phẩm trong danh sách yêu thích</li>
 *   <li>Xóa toàn bộ danh sách yêu thích</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu đăng nhập (isAuthenticated())</li>
 *   <li>User chỉ có thể quản lý danh sách yêu thích của chính mình</li>
 *   <li>User được lấy từ JWT token (AuthenticationPrincipal)</li>
 * </ul>
 * 
 * <p><b>Về danh sách yêu thích:</b>
 * <ul>
 *   <li>Mỗi user có 1 danh sách yêu thích duy nhất</li>
 *   <li>Danh sách yêu thích được tự động tạo khi user thêm sản phẩm đầu tiên</li>
 *   <li>Mỗi sản phẩm chỉ có thể có trong danh sách yêu thích 1 lần</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy danh sách yêu thích
 * ResponseEntity&lt;List&lt;WishlistDto&gt;&gt; response = wishlistController.getMyWishlist(currentUser);
 * 
 * // Thêm sản phẩm vào danh sách yêu thích
 * ResponseEntity&lt;WishlistDto&gt; response2 = wishlistController.addToWishlist(1L, currentUser);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Wishlist", description = "API quản lý danh sách yêu thích cho User")
@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Lấy danh sách sản phẩm yêu thích của user hiện tại
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để lấy tất cả sản phẩm trong danh sách yêu thích</li>
     *   <li>Trả về danh sách sản phẩm yêu thích</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi item bao gồm: ID sản phẩm, tên sản phẩm, hình ảnh, giá, ngày thêm vào</li>
     *   <li>Danh sách được sắp xếp theo ngày thêm vào (mới nhất trước)</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa danh sách WishlistDto (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;WishlistDto&gt;&gt; response = wishlistController.getMyWishlist(currentUser);
     * List&lt;WishlistDto&gt; wishlist = response.getBody();
     * wishlist.forEach(item -&gt; System.out.println(item.getProductName()));
     * </pre>
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
     * Thêm sản phẩm vào danh sách yêu thích
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Kiểm tra sản phẩm có tồn tại không</li>
     *   <li>Kiểm tra sản phẩm đã có trong danh sách yêu thích chưa</li>
     *   <li>Nếu chưa có: Thêm sản phẩm vào danh sách yêu thích</li>
     *   <li>Nếu đã có: Trả về item hiện có (không tạo duplicate)</li>
     *   <li>Trả về item vừa thêm</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Mỗi sản phẩm chỉ có thể có trong danh sách yêu thích 1 lần</li>
     *   <li>Nếu sản phẩm đã có trong danh sách, sẽ không tạo duplicate</li>
     *   <li>Sản phẩm phải tồn tại và đang active</li>
     * </ul>
     * 
     * @param productId ID của sản phẩm cần thêm vào danh sách yêu thích
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa WishlistDto của item vừa thêm (HTTP 201 Created)
     * @throws ApiException nếu sản phẩm không tồn tại hoặc không active
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;WishlistDto&gt; response = wishlistController.addToWishlist(1L, currentUser);
     * WishlistDto item = response.getBody();
     * </pre>
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
     * Xóa sản phẩm khỏi danh sách yêu thích
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để xóa sản phẩm khỏi danh sách yêu thích</li>
     *   <li>Trả về thông báo thành công</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Nếu sản phẩm không có trong danh sách yêu thích, sẽ không có gì xảy ra (không throw exception)</li>
     *   <li>Hành động này không thể hoàn tác</li>
     * </ul>
     * 
     * @param productId ID của sản phẩm cần xóa khỏi danh sách yêu thích
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa thông báo thành công (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Map&lt;String, String&gt;&gt; response = wishlistController.removeFromWishlist(1L, currentUser);
     * String message = response.getBody().get("message"); // "Đã xóa sản phẩm khỏi danh sách yêu thích"
     * </pre>
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
     * Kiểm tra sản phẩm có trong danh sách yêu thích không
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để kiểm tra sản phẩm có trong danh sách yêu thích không</li>
     *   <li>Trả về kết quả (true/false)</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>inWishlist: true nếu sản phẩm có trong danh sách yêu thích, false nếu không</li>
     * </ul>
     * 
     * @param productId ID của sản phẩm cần kiểm tra
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa Map với key "inWishlist" và giá trị boolean (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Map&lt;String, Boolean&gt;&gt; response = wishlistController.checkInWishlist(1L, currentUser);
     * boolean inWishlist = response.getBody().get("inWishlist");
     * System.out.println("Sản phẩm có trong danh sách yêu thích: " + inWishlist);
     * </pre>
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
     * Đếm số sản phẩm trong danh sách yêu thích
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để đếm số sản phẩm trong danh sách yêu thích</li>
     *   <li>Trả về số lượng</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>count: Số lượng sản phẩm trong danh sách yêu thích</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa Map với key "count" và giá trị Long (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Map&lt;String, Long&gt;&gt; response = wishlistController.countWishlistItems(currentUser);
     * long count = response.getBody().get("count");
     * System.out.println("Số sản phẩm trong danh sách yêu thích: " + count);
     * </pre>
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countWishlistItems(
            @AuthenticationPrincipal User userPrincipal
    ) {
        long count = wishlistService.countWishlistItems(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Xóa toàn bộ danh sách yêu thích
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để xóa tất cả sản phẩm khỏi danh sách yêu thích</li>
     *   <li>Trả về thông báo thành công</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Danh sách yêu thích sẽ trở thành rỗng (không có sản phẩm)</li>
     *   <li>Danh sách yêu thích vẫn tồn tại (không bị xóa), chỉ xóa các items</li>
     *   <li>Hành động này không thể hoàn tác</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa thông báo thành công (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Map&lt;String, String&gt;&gt; response = wishlistController.clearWishlist(currentUser);
     * String message = response.getBody().get("message"); // "Đã xóa toàn bộ danh sách yêu thích"
     * </pre>
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearWishlist(
            @AuthenticationPrincipal User userPrincipal
    ) {
        wishlistService.clearWishlist(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ danh sách yêu thích"));
    }
}

