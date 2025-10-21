package com.sneakery.store.service;

import com.sneakery.store.dto.ReviewRequestDto;
import com.sneakery.store.dto.ReviewResponseDto;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.Review;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.OrderRepository;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.ReviewRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository; // Để kiểm tra "đã mua"

    /**
     * API 1: Lấy tất cả review (đã duyệt) của 1 sản phẩm
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsForProduct(Long productId) {
        // Kiểm tra sản phẩm có tồn tại không
        if (!productRepository.existsById(productId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }
        
        List<Review> reviews = reviewRepository.findByProductIdAndIsApprovedTrue(productId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * API 2: User tạo review mới
     */
    @Transactional
    public ReviewResponseDto createReview(Long productId, Long userId, ReviewRequestDto requestDto) {
        
        // 1. Kiểm tra sản phẩm và user
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy user"));

        // 2. Kiểm tra xem user đã review sản phẩm này chưa
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bạn đã đánh giá sản phẩm này rồi");
        }

        // 3. Kiểm tra xem user đã mua sản phẩm này chưa
        boolean hasPurchased = orderRepository.countCompletedOrdersByUserIdAndProductId(userId, productId) > 0;
        
        // 4. Tạo review
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(requestDto.getRating());
        review.setBody(requestDto.getBody());
        review.setCreatedAt(LocalDateTime.now());
        review.setIsVerifiedPurchase(hasPurchased); // Gắn cờ "Đã mua"
        review.setIsApproved(false); // Mặc định: Chờ duyệt

        Review savedReview = reviewRepository.save(review);
        
        return convertToDto(savedReview);
    }

    // =================================================================
    // HÀM HELPER
    // =================================================================

    private ReviewResponseDto convertToDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .rating(review.getRating())
                .body(review.getBody())
                .createdAt(review.getCreatedAt())
                .authorName(review.getUser().getFullName()) // Lấy tên user
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .build();
    }
}