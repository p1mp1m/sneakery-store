package com.sneakery.store.controller;

import com.sneakery.store.entity.LoyaltyPoint;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý điểm thưởng (Loyalty Points) cho User
 * 
 * <p>Controller này cung cấp các API endpoints cho user để:
 * <ul>
 *   <li>Xem số điểm thưởng hiện có (balance)</li>
 *   <li>Xem lịch sử giao dịch điểm thưởng</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Tất cả endpoints đều yêu cầu đăng nhập (isAuthenticated())</li>
 *   <li>User chỉ có thể xem điểm thưởng của chính mình</li>
 *   <li>User được lấy từ JWT token (AuthenticationPrincipal)</li>
 * </ul>
 * 
 * <p><b>Về điểm thưởng:</b>
 * <ul>
 *   <li>User tích điểm khi mua hàng (tự động tính)</li>
 *   <li>1 điểm = 1,000 VNĐ (có thể đổi thành tiền giảm giá)</li>
 *   <li>Điểm thưởng có thể được sử dụng khi checkout (áp dụng coupon điểm)</li>
 *   <li>Lịch sử giao dịch bao gồm: tích điểm, sử dụng điểm, đổi điểm</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy số điểm thưởng hiện có
 * ResponseEntity&lt;Map&lt;String, Object&gt;&gt; response = loyaltyController.getBalance(currentUser);
 * 
 * // Lấy lịch sử giao dịch điểm thưởng
 * ResponseEntity&lt;List&lt;LoyaltyPoint&gt;&gt; response2 = loyaltyController.getHistory(currentUser);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Loyalty Points", description = "API quản lý điểm thưởng cho User")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loyalty")
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    /**
     * Lấy số điểm thưởng hiện có (balance)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để lấy số điểm thưởng hiện có</li>
     *   <li>Tính giá trị tương đương bằng VNĐ (1 điểm = 1,000 VNĐ)</li>
     *   <li>Trả về balance và giá trị VNĐ</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>balance: Số điểm thưởng hiện có (int)</li>
     *   <li>valueVnd: Giá trị tương đương bằng VNĐ (balance * 1,000)</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> 1 điểm = 1,000 VNĐ. User có thể sử dụng điểm để giảm giá khi checkout.
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa Map với balance và valueVnd (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;Map&lt;String, Object&gt;&gt; response = loyaltyController.getBalance(currentUser);
     * Map&lt;String, Object&gt; data = response.getBody();
     * int balance = (int) data.get("balance");
     * int valueVnd = (int) data.get("valueVnd");
     * System.out.println("Bạn có " + balance + " điểm (" + valueVnd + " VNĐ)");
     * </pre>
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@AuthenticationPrincipal User userPrincipal) {
        log.info("📍 GET /api/loyalty/balance - User: {}", userPrincipal.getId());
        
        Long userId = userPrincipal.getId();
        int balance = loyaltyService.getUserPointsBalance(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("balance", balance);
        response.put("valueVnd", balance * 1000); // 1 point = 1,000 VND
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy lịch sử giao dịch điểm thưởng
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Gọi service để lấy lịch sử giao dịch điểm thưởng</li>
     *   <li>Trả về danh sách các giao dịch</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi giao dịch bao gồm: loại giao dịch (tích điểm/sử dụng điểm), số điểm, ngày giao dịch, mô tả</li>
     *   <li>Danh sách được sắp xếp theo ngày giao dịch (mới nhất trước)</li>
     *   <li>Bao gồm cả giao dịch tích điểm (khi mua hàng) và sử dụng điểm (khi checkout)</li>
     * </ul>
     * 
     * <p><b>Về loại giao dịch:</b>
     * <ul>
     *   <li>EARNED: Tích điểm (khi mua hàng)</li>
     *   <li>USED: Sử dụng điểm (khi checkout với coupon điểm)</li>
     *   <li>EXPIRED: Điểm hết hạn (nếu có)</li>
     * </ul>
     * 
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @return ResponseEntity chứa danh sách LoyaltyPoint (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;LoyaltyPoint&gt;&gt; response = loyaltyController.getHistory(currentUser);
     * List&lt;LoyaltyPoint&gt; history = response.getBody();
     * history.forEach(point -&gt; {
     *     System.out.println(point.getType() + ": " + point.getPoints() + " điểm");
     * });
     * </pre>
     */
    @GetMapping("/history")
    public ResponseEntity<List<LoyaltyPoint>> getHistory(@AuthenticationPrincipal User userPrincipal) {
        log.info("📍 GET /api/loyalty/history - User: {}", userPrincipal.getId());
        
        Long userId = userPrincipal.getId();
        List<LoyaltyPoint> history = loyaltyService.getUserPointsHistory(userId);
        
        return ResponseEntity.ok(history);
    }
}

