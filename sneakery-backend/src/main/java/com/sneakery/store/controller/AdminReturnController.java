package com.sneakery.store.controller;

import com.sneakery.store.dto.AdminReturnDto;
import com.sneakery.store.dto.AdminReturnListDto;
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
@CrossOrigin(origins = "http://localhost:5173")
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
            @RequestParam(required = false) String status
    ) {
        log.info("📍 GET /api/admin/returns - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminReturnListDto> returns = adminReturnService.getAllReturns(search, status, pageable);

        return ResponseEntity.ok(returns);
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
            Authentication authentication
    ) {
        log.info("📍 PUT /api/admin/returns/{}/status - status: {}", id, request.getStatus());

        Long adminId = Long.parseLong(authentication.getName());
        AdminReturnDto updated = adminReturnService.updateReturnStatus(
                id, request.getStatus(), adminId, request.getAdminNote()
        );

        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/admin/returns/{id}/refund
     * Xử lý hoàn tiền
     */
    @PostMapping("/{id}/refund")
    public ResponseEntity<AdminReturnDto> processRefund(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("📍 POST /api/admin/returns/{}/refund", id);

        Long adminId = Long.parseLong(authentication.getName());
        AdminReturnDto updated = adminReturnService.processRefund(id, adminId);

        return ResponseEntity.ok(updated);
    }

    // Inner class for request body
    @lombok.Data
    public static class UpdateReturnStatusRequest {
        private String status;
        private String adminNote;
    }
}

