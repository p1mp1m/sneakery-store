package com.sneakery.store.controller;

import com.sneakery.store.dto.AdminReviewDto;
import com.sneakery.store.dto.AdminReviewListDto;
import com.sneakery.store.service.AdminReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller: AdminReviewController
 * Admin endpoints cho quản lý reviews
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminReviewController {

    private final AdminReviewService adminReviewService;

    /**
     * GET /api/admin/reviews
     * Lấy tất cả reviews với pagination và filter
     */
    @GetMapping
    public ResponseEntity<Page<AdminReviewListDto>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isApproved,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating
    ) {
        log.info("📍 GET /api/admin/reviews - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminReviewListDto> reviews = adminReviewService.getAllReviews(
                search, isApproved, minRating, maxRating, pageable
        );

        return ResponseEntity.ok(reviews);
    }

    /**
     * GET /api/admin/reviews/{id}
     * Lấy chi tiết review
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminReviewDto> getReviewById(@PathVariable Long id) {
        log.info("📍 GET /api/admin/reviews/{}", id);

        AdminReviewDto review = adminReviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    /**
     * PUT /api/admin/reviews/{id}/status
     * Cập nhật trạng thái duyệt review
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AdminReviewDto> updateReviewStatus(
            @PathVariable Long id,
            @RequestParam Boolean isApproved,
            Authentication authentication
    ) {
        log.info("📍 PUT /api/admin/reviews/{}/status - approved: {}", id, isApproved);

        Long adminId = Long.parseLong(authentication.getName());
        AdminReviewDto updated = adminReviewService.updateReviewStatus(id, isApproved, adminId);

        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/admin/reviews/{id}
     * Xóa review (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.info("📍 DELETE /api/admin/reviews/{}", id);

        adminReviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/admin/reviews/{id}/reply
     * Admin reply to review
     */
    @PostMapping("/{id}/reply")
    public ResponseEntity<AdminReviewDto> replyToReview(
            @PathVariable Long id,
            @RequestBody ReplyRequest request,
            Authentication authentication
    ) {
        log.info("📍 POST /api/admin/reviews/{}/reply", id);

        Long adminId = Long.parseLong(authentication.getName());
        AdminReviewDto updated = adminReviewService.replyToReview(id, request.getReplyText(), adminId);

        return ResponseEntity.ok(updated);
    }

    // Inner class for reply request
    @lombok.Data
    public static class ReplyRequest {
        private String replyText;
    }
}

