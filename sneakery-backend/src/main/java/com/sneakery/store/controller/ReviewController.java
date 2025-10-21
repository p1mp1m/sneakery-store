package com.sneakery.store.controller;

import com.sneakery.store.dto.ReviewRequestDto;
import com.sneakery.store.dto.ReviewResponseDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// Lồng vào /api/products/
@RequestMapping("/api/products/{productId}/reviews") 
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * API Public: Lấy tất cả review của 1 sản phẩm
     * GET /api/products/123/reviews
     */
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getProductReviews(
            @PathVariable Long productId
    ) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsForProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * API Private: Tạo review mới (Yêu cầu đăng nhập)
     * POST /api/products/123/reviews
     */
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createProductReview(
            @PathVariable Long productId,
            @AuthenticationPrincipal User userPrincipal, // Lấy user đã đăng nhập
            @Valid @RequestBody ReviewRequestDto requestDto
    ) {
        ReviewResponseDto newReview = reviewService.createReview(productId, userPrincipal.getId(), requestDto);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }
}