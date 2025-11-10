package com.sneakery.store.controller;

import com.sneakery.store.dto.ReviewRequestDto;
import com.sneakery.store.dto.ReviewResponseDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Controller xử lý đánh giá sản phẩm (Reviews)
 * 
 * <p>Controller này cung cấp các API endpoints cho user để:
 * <ul>
 *   <li>Xem danh sách đánh giá của sản phẩm (public)</li>
 *   <li>Tạo đánh giá mới cho sản phẩm (yêu cầu đăng nhập)</li>
 * </ul>
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Xem đánh giá: Công khai (không cần đăng nhập)</li>
 *   <li>Tạo đánh giá: Yêu cầu đăng nhập (isAuthenticated())</li>
 *   <li>User chỉ có thể tạo đánh giá cho sản phẩm đã mua</li>
 * </ul>
 * 
 * <p><b>Về đánh giá:</b>
 * <ul>
 *   <li>Mỗi đánh giá bao gồm: điểm số (1-5 sao), nội dung, hình ảnh (tùy chọn)</li>
 *   <li>User chỉ có thể đánh giá sản phẩm đã mua</li>
 *   <li>Mỗi user chỉ có thể đánh giá 1 lần cho mỗi sản phẩm</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy danh sách đánh giá của sản phẩm
 * ResponseEntity&lt;List&lt;ReviewResponseDto&gt;&gt; response = reviewController.getProductReviews(1L);
 * 
 * // Tạo đánh giá mới
 * ReviewRequestDto reviewRequest = new ReviewRequestDto();
 * reviewRequest.setRating(5);
 * reviewRequest.setComment("Sản phẩm rất tốt!");
 * ResponseEntity&lt;ReviewResponseDto&gt; response2 = reviewController.createProductReview(1L, currentUser, reviewRequest);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Reviews", description = "API đánh giá sản phẩm")
@Slf4j
@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Lấy danh sách đánh giá của sản phẩm (Public)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy tất cả đánh giá của sản phẩm</li>
     *   <li>Trả về danh sách đánh giá với đầy đủ thông tin</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi đánh giá bao gồm: điểm số, nội dung, tên user, ngày đánh giá, hình ảnh (nếu có)</li>
     *   <li>Đánh giá được sắp xếp theo ngày tạo (mới nhất trước)</li>
     *   <li>Bao gồm cả đánh giá đã được phê duyệt và chưa phê duyệt</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Endpoint này công khai, không cần đăng nhập.
     * 
     * @param productId ID của sản phẩm cần lấy đánh giá
     * @return ResponseEntity chứa danh sách ReviewResponseDto (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * ResponseEntity&lt;List&lt;ReviewResponseDto&gt;&gt; response = reviewController.getProductReviews(1L);
     * List&lt;ReviewResponseDto&gt; reviews = response.getBody();
     * reviews.forEach(review -&gt; {
     *     System.out.println(review.getRating() + " sao: " + review.getComment());
     * });
     * </pre>
     */
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getProductReviews(
            @PathVariable Long productId
    ) {
        log.info("📍 GET /api/products/{}/reviews", productId);
        List<ReviewResponseDto> reviews = reviewService.getReviewsForProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Tạo đánh giá mới cho sản phẩm (Yêu cầu đăng nhập)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy user từ JWT token</li>
     *   <li>Validate dữ liệu đầu vào (điểm số, nội dung)</li>
     *   <li>Kiểm tra user đã mua sản phẩm này chưa</li>
     *   <li>Kiểm tra user đã đánh giá sản phẩm này chưa</li>
     *   <li>Tạo đánh giá mới</li>
     *   <li>Trả về đánh giá vừa tạo</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Yêu cầu đăng nhập (isAuthenticated())</li>
     *   <li>User chỉ có thể đánh giá sản phẩm đã mua</li>
     *   <li>Mỗi user chỉ có thể đánh giá 1 lần cho mỗi sản phẩm</li>
     *   <li>Điểm số phải từ 1 đến 5</li>
     *   <li>Nội dung đánh giá là tùy chọn (có thể chỉ đánh giá điểm số)</li>
     * </ul>
     * 
     * @param productId ID của sản phẩm cần đánh giá
     * @param userPrincipal User hiện tại (tự động lấy từ JWT token)
     * @param requestDto DTO chứa thông tin đánh giá:
     *                   - rating: Điểm số (bắt buộc, từ 1 đến 5)
     *                   - comment: Nội dung đánh giá (tùy chọn)
     *                   - images: Danh sách hình ảnh (tùy chọn)
     * @return ResponseEntity chứa ReviewResponseDto của đánh giá vừa tạo (HTTP 201 Created)
     * @throws ApiException nếu user chưa mua sản phẩm, đã đánh giá rồi, hoặc validation thất bại
     * 
     * @example
     * <pre>
     * ReviewRequestDto reviewRequest = new ReviewRequestDto();
     * reviewRequest.setRating(5);
     * reviewRequest.setComment("Sản phẩm rất tốt, chất lượng cao!");
     * 
     * ResponseEntity&lt;ReviewResponseDto&gt; response = reviewController.createProductReview(1L, currentUser, reviewRequest);
     * ReviewResponseDto newReview = response.getBody();
     * </pre>
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> createProductReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal User userPrincipal,
            @Valid @RequestBody ReviewRequestDto requestDto
    ) {
        log.info("📍 POST /api/products/{}/reviews - User: {}", productId, userPrincipal.getId());
        ReviewResponseDto newReview = reviewService.createReview(productId, userPrincipal.getId(), requestDto);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

}