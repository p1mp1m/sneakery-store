package com.sneakery.store.controller;

import com.sneakery.store.dto.AdminWarrantyDto;
import com.sneakery.store.dto.AdminWarrantyListDto;
import com.sneakery.store.service.AdminWarrantyService;
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
 * Controller: AdminWarrantyController
 * Admin endpoints cho quản lý warranty requests
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/warranties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminWarrantyController {

    private final AdminWarrantyService adminWarrantyService;

    /**
     * GET /api/admin/warranties
     * Lấy tất cả warranty requests với pagination và filter
     */
    @GetMapping
    public ResponseEntity<Page<AdminWarrantyListDto>> getAllWarranties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        log.info("📍 GET /api/admin/warranties - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminWarrantyListDto> warranties = adminWarrantyService.getAllWarranties(search, status, pageable);

        return ResponseEntity.ok(warranties);
    }

    /**
     * GET /api/admin/warranties/{id}
     * Lấy chi tiết warranty request
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminWarrantyDto> getWarrantyById(@PathVariable Long id) {
        log.info("📍 GET /api/admin/warranties/{}", id);

        AdminWarrantyDto warranty = adminWarrantyService.getWarrantyById(id);
        return ResponseEntity.ok(warranty);
    }

    /**
     * PUT /api/admin/warranties/{id}/status
     * Cập nhật trạng thái warranty request
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AdminWarrantyDto> updateWarrantyStatus(
            @PathVariable Long id,
            @RequestBody UpdateWarrantyStatusRequest request,
            Authentication authentication
    ) {
        log.info("📍 PUT /api/admin/warranties/{}/status - status: {}", id, request.getStatus());

        Long adminId = Long.parseLong(authentication.getName());
        AdminWarrantyDto updated = adminWarrantyService.updateWarrantyStatus(
                id, request.getStatus(), adminId, request.getAdminNote()
        );

        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/admin/warranties/{id}/process
     * Xử lý bảo hành
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<AdminWarrantyDto> processWarranty(
            @PathVariable Long id,
            @RequestBody ProcessWarrantyRequest request,
            Authentication authentication
    ) {
        log.info("📍 POST /api/admin/warranties/{}/process", id);

        Long adminId = Long.parseLong(authentication.getName());
        AdminWarrantyDto updated = adminWarrantyService.processWarranty(
                id, adminId, request.getResolutionNote(), request.getWarrantyType()
        );

        return ResponseEntity.ok(updated);
    }

    // Inner classes for request bodies
    @lombok.Data
    public static class UpdateWarrantyStatusRequest {
        private String status;
        private String adminNote;
    }

    @lombok.Data
    public static class ProcessWarrantyRequest {
        private String resolutionNote;
        private String warrantyType; // repair or replace
    }
}

