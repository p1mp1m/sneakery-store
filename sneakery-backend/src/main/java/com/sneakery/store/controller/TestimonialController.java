package com.sneakery.store.controller;

import com.sneakery.store.dto.TestimonialDto;
import com.sneakery.store.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller xử lý testimonials (Reviews đã được duyệt)
 * 
 * <p>Controller này cung cấp API endpoint công khai để lấy danh sách reviews đã được duyệt
 * để hiển thị trên homepage và reviews page.
 * 
 * <p><b>Về bảo mật:</b>
 * <ul>
 *   <li>Endpoint này công khai, không cần đăng nhập</li>
 *   <li>Chỉ trả về các reviews đã được duyệt (isApproved = true)</li>
 * </ul>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Tag(name = "Testimonials", description = "API đánh giá đã được duyệt")
@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class TestimonialController {

    private final ReviewService reviewService;

    /**
     * Lấy danh sách tất cả reviews đã được duyệt với pagination (Public - cho testimonials)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi service để lấy tất cả reviews đã được duyệt với pagination</li>
     *   <li>Trả về Page chứa danh sách reviews với đầy đủ thông tin product và brand</li>
     * </ol>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Chỉ trả về các reviews đã được duyệt (isApproved = true)</li>
     *   <li>Bao gồm thông tin product: productId, productName, productImage, brandName</li>
     *   <li>Bao gồm thông tin user: userName</li>
     *   <li>Bao gồm thông tin review: rating, comment, isVerifiedPurchase, createdAt</li>
     *   <li>Được sắp xếp theo ngày tạo (mới nhất trước)</li>
     *   <li>Hỗ trợ pagination với page và size</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Endpoint này công khai, không cần đăng nhập.
     * 
     * @param page Số trang (bắt đầu từ 0, mặc định: 0)
     * @param size Số lượng reviews mỗi trang (mặc định: 6)
     * @return ResponseEntity chứa Page&lt;TestimonialDto&gt; (HTTP 200 OK)
     * 
     * @example
     * <pre>
     * // Lấy trang đầu tiên, mỗi trang 6 reviews
     * ResponseEntity&lt;Page&lt;TestimonialDto&gt;&gt; response = testimonialController.getApprovedReviews(0, 6);
     * Page&lt;TestimonialDto&gt; testimonials = response.getBody();
     * </pre>
     */
    @GetMapping("/approved")
    public ResponseEntity<Page<TestimonialDto>> getApprovedReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        log.info("📍 GET /api/reviews/approved - page: {}, size: {}", page, size);
        Page<TestimonialDto> testimonials = reviewService.getAllApprovedReviews(page, size);
        return ResponseEntity.ok(testimonials);
    }
}

