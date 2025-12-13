package com.sneakery.store.controller;

import com.sneakery.store.dto.AdminReturnDto;
import com.sneakery.store.dto.AdminReturnListDto;
import com.sneakery.store.dto.ConfirmReturnConditionRequest;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.AdminReturnService;
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
 * Controller: AdminReturnController
 * Admin endpoints cho quản lý return requests
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/returns")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" })
public class AdminReturnController {

    private final AdminReturnService adminReturnService;

    /**
     * GET /api/admin/returns
     * Lấy tất cả return requests với pagination và filter
     */
    @GetMapping
    public ResponseEntity<Page<AdminReturnListDto>> getAllReturns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String status) {
        log.info("📍 GET /api/admin/returns - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminReturnListDto> result = adminReturnService.getAllReturns(search, status, reason, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/admin/returns/{id}
     * Lấy chi tiết return request
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminReturnDto> getReturnById(@PathVariable Long id) {
        log.info("📍 GET /api/admin/returns/{}", id);

        AdminReturnDto returnDto = adminReturnService.getReturnById(id);
        return ResponseEntity.ok(returnDto);
    }

    /**
     * PUT /api/admin/returns/{id}/status
     * Cập nhật trạng thái return request
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AdminReturnDto> updateReturnStatus(
            @PathVariable Long id,
            @RequestBody UpdateReturnStatusRequest request,
            Authentication authentication) {
        log.info("📍 PUT /api/admin/returns/{}/status - status: {}", id, request.getStatus());

        // Get admin ID from authentication principal (User entity)
        User admin = (User) authentication.getPrincipal();
        Long adminId = admin.getId();
        AdminReturnDto updated = adminReturnService.updateReturnStatus(
                id, request.getStatus(), adminId, request.getAdminNote());

        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/admin/returns/{id}/refund
     * Xử lý hoàn tiền
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<AdminReturnDto> processRefund(
            @PathVariable Long id,
            Authentication authentication) {
        log.info("📍 POST /api/admin/returns/{}/refund", id);

        // Get admin ID from authentication principal (User entity)
        User admin = (User) authentication.getPrincipal();
        Long adminId = admin.getId();
        AdminReturnDto updated = adminReturnService.processRefund(id, adminId);

        return ResponseEntity.ok(updated);
    }

    /**
     * Helper method to get admin ID from Authentication
     */
    private Long getAdminIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }
        throw new IllegalStateException("Cannot get admin ID from authentication. Principal is not User entity.");
    }

    // Inner class for request body
    @lombok.Data
    public static class UpdateReturnStatusRequest {
        private String status;
        private String adminNote;
    }

    /**
     * PUT /api/admin/returns/{id}/confirm-conditions
     * Xác nhận số lượng sản phẩm tốt / hỏng & cập nhật tồn kho
     */
    @PutMapping("/{id}/confirm-conditions")
    public ResponseEntity<AdminReturnDto> confirmReturnConditions(
            @PathVariable Long id,
            @RequestBody ConfirmReturnConditionRequest request,
            Authentication authentication
    ) {
        log.info("📍 PUT /api/admin/returns/{}/confirm-conditions", id);

        // Lấy admin ID từ Authentication principal
        User admin = (User) authentication.getPrincipal();
        Long adminId = admin.getId();

        request.setReturnRequestId(id); // đảm bảo BE xử lý đúng ID

        AdminReturnDto updated = adminReturnService.confirmReturnItemConditions(request, adminId);
        return ResponseEntity.ok(updated);
    }
}
