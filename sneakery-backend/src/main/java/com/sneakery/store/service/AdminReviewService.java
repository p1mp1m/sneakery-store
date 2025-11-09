package com.sneakery.store.service;

import com.sneakery.store.dto.AdminReviewDto;
import com.sneakery.store.dto.AdminReviewListDto;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.Review;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.exception.DatabaseOperationException;
import com.sneakery.store.repository.ReviewRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Service: AdminReviewService
 * Quản lý reviews cho admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Lấy tất cả reviews với pagination và filter
     */
    @Transactional(readOnly = true)
    public Page<AdminReviewListDto> getAllReviews(
            String search,
            Boolean isApproved,
            Integer minRating,
            Integer maxRating,
            Pageable pageable
    ) {
        log.info("🔍 Fetching reviews with filters - search: {}, approved: {}, rating: {}-{}",
                search, isApproved, minRating, maxRating);

        Specification<Review> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();

            // Search filter (product name or user name)
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("product").get("name")), searchPattern),
                        cb.like(cb.lower(root.get("user").get("fullName")), searchPattern),
                        cb.like(cb.lower(root.get("body")), searchPattern)
                ));
            }

            // Approved filter
            if (isApproved != null) {
                predicates = cb.and(predicates, cb.equal(root.get("isApproved"), isApproved));
            }

            // Rating filter
            if (minRating != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("rating"), minRating));
            }
            if (maxRating != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("rating"), maxRating));
            }

            // Exclude deleted
            predicates = cb.and(predicates, cb.isNull(root.get("deletedAt")));

            return predicates;
        };
        
        Page<Review> reviews = reviewRepository.findAll(spec, Objects.requireNonNull(pageable));

        return reviews.map(this::convertToListDto);
    }

    /**
     * Lấy chi tiết review
     */
    @Transactional(readOnly = true)
    public AdminReviewDto getReviewById(Long id) {
        log.info("📄 Fetching review detail - ID: {}", id);

        Review review = reviewRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy review"));

        return convertToDto(review);
    }

    /**
     * Cập nhật trạng thái duyệt review
     */
    @Transactional
    public AdminReviewDto updateReviewStatus(Long id, Boolean isApproved, Long adminId) {
        log.info("✅ Updating review status - ID: {}, approved: {}, by admin: {}", id, isApproved, adminId);

        Review review = reviewRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy review"));

        User admin = userRepository.findById(Objects.requireNonNull(adminId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        review.setIsApproved(isApproved);
        review.setApprovedBy(admin);
        review.setApprovedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review updated = reviewRepository.save(review);
        
        // Đồng bộ: Cập nhật product rating và review_count khi review được approve/reject
        try {
            updateProductRating(review.getProduct().getId());
            log.info("✅ Updated product rating for product ID: {}", review.getProduct().getId());
        } catch (Exception e) {
            log.error("❌ Failed to update product rating for product ID {}: {}", 
                review.getProduct().getId(), e.getMessage());
            // Không fail operation nếu update rating thất bại
        }
        
        return convertToDto(updated);
    }

    /**
     * Xóa review (soft delete)
     */
    @Transactional
    public void deleteReview(Long id) {
        log.info("🗑️ Deleting review - ID: {}", id);

        Review review = reviewRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy review"));

        Product product = review.getProduct();
        review.setDeletedAt(LocalDateTime.now());
        reviewRepository.save(review);
        
        // Đồng bộ: Cập nhật product rating và review_count khi review bị xóa
        try {
            updateProductRating(product.getId());
            log.info("✅ Updated product rating after review deletion for product ID: {}", product.getId());
        } catch (Exception e) {
            log.error("❌ Failed to update product rating after deletion for product ID {}: {}", 
                product.getId(), e.getMessage());
            // Không fail operation nếu update rating thất bại
        }
    }

    /**
     * Admin reply to review
     */
    @Transactional
    public AdminReviewDto replyToReview(Long id, String replyText, Long adminId) {
        log.info("💬 Replying to review - ID: {}, by admin: {}", id, adminId);

        Review review = reviewRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy review"));

        User admin = userRepository.findById(Objects.requireNonNull(adminId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        review.setReplyText(replyText);
        review.setRepliedBy(admin);
        review.setRepliedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review updated = reviewRepository.save(review);
        return convertToDto(updated);
    }

    // ===== HELPER METHODS =====

    private AdminReviewListDto convertToListDto(Review review) {
        // Get product image URL from first image if available
        String productImageUrl = null;
        if (review.getProduct().getImages() != null && !review.getProduct().getImages().isEmpty()) {
            productImageUrl = review.getProduct().getImages().get(0).getImageUrl();
        }
        
        // Collect review images into a list
        java.util.List<String> reviewImages = new java.util.ArrayList<>();
        if (review.getImageUrl1() != null && !review.getImageUrl1().isEmpty()) {
            reviewImages.add(review.getImageUrl1());
        }
        if (review.getImageUrl2() != null && !review.getImageUrl2().isEmpty()) {
            reviewImages.add(review.getImageUrl2());
        }
        if (review.getImageUrl3() != null && !review.getImageUrl3().isEmpty()) {
            reviewImages.add(review.getImageUrl3());
        }
        
        return AdminReviewListDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .productImage(productImageUrl)
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .rating(review.getRating())
                .title(review.getTitle())
                .reviewText(review.getBody())
                .images(reviewImages)
                .isApproved(review.getIsApproved())
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .helpfulCount(review.getHelpfulCount() != null ? review.getHelpfulCount() : 0)
                .unhelpfulCount(review.getUnhelpfulCount() != null ? review.getUnhelpfulCount() : 0)
                .adminReply(review.getReplyText())
                .repliedAt(review.getRepliedAt())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private AdminReviewDto convertToDto(Review review) {
        // Get product image URL from first image if available
        String productImageUrl = null;
        if (review.getProduct().getImages() != null && !review.getProduct().getImages().isEmpty()) {
            productImageUrl = review.getProduct().getImages().get(0).getImageUrl();
        }
        
        return AdminReviewDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .productImageUrl(productImageUrl)
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .userEmail(review.getUser().getEmail())
                .orderId(review.getOrder() != null ? review.getOrder().getId() : null)
                .rating(review.getRating())
                .title(review.getTitle())
                .body(review.getBody())
                .imageUrl1(review.getImageUrl1())
                .imageUrl2(review.getImageUrl2())
                .imageUrl3(review.getImageUrl3())
                .isApproved(review.getIsApproved())
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .helpfulCount(review.getHelpfulCount() != null ? review.getHelpfulCount() : 0)
                .unhelpfulCount(review.getUnhelpfulCount() != null ? review.getUnhelpfulCount() : 0)
                .replyText(review.getReplyText())
                .repliedAt(review.getRepliedAt())
                .repliedByName(review.getRepliedBy() != null ? review.getRepliedBy().getFullName() : null)
                .approvedByName(review.getApprovedBy() != null ? review.getApprovedBy().getFullName() : null)
                .approvedAt(review.getApprovedAt())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .deletedAt(review.getDeletedAt())
                .build();
    }
    
    /**
     * Đồng bộ: Cập nhật product rating và review_count bằng stored procedure
     */
    private void updateProductRating(Long productId) {
        try {
            jdbcTemplate.update("EXEC sp_UpdateProductRating ?", productId);
            log.debug("✅ Called sp_UpdateProductRating for product ID: {}", productId);
        } catch (Exception e) {
            log.error("❌ Error calling sp_UpdateProductRating for product ID {}: {}", productId, e.getMessage());
            throw new DatabaseOperationException("Failed to update product rating", e);
        }
    }
}

