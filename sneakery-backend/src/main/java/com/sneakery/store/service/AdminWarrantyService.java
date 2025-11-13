package com.sneakery.store.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sneakery.store.dto.AdminWarrantyDto;
import com.sneakery.store.dto.AdminWarrantyListDto;
import com.sneakery.store.entity.Warranty;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.WarrantyRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Service: AdminWarrantyService
 * Quản lý warranty requests cho admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminWarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Lấy tất cả warranty requests với pagination và filter
     */
    @Transactional(readOnly = true)
    public Page<AdminWarrantyListDto> getAllWarranties(
            String search,
            String status,
            Pageable pageable
    ) {
        log.info("🔍 Fetching warranties with filters - search: {}, status: {}", search, status);

        Specification<Warranty> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();

            // Search filter
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("order").get("orderNumber")), searchPattern),
                        cb.like(cb.lower(root.get("user").get("fullName")), searchPattern),
                        cb.like(cb.lower(root.get("product").get("name")), searchPattern),
                        cb.like(cb.lower(root.get("issueDescription")), searchPattern)
                ));
            }

            // Status filter
            if (status != null && !status.trim().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }

            return predicates;
        };
        
        Page<Warranty> warranties = warrantyRepository.findAll(spec, Objects.requireNonNull(pageable));
        return warranties.map(this::convertToListDto);
    }

    /**
     * Lấy chi tiết warranty request
     */
    @Transactional(readOnly = true)
    public AdminWarrantyDto getWarrantyById(Long id) {
        log.info("📄 Fetching warranty detail - ID: {}", id);

        Warranty warranty = warrantyRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu bảo hành"));

        return convertToDto(warranty);
    }

    /**
     * Cập nhật trạng thái warranty request
     */
    @Transactional
    public AdminWarrantyDto updateWarrantyStatus(Long id, String status, Long adminId, String adminNote) {
        log.info("✅ Updating warranty status - ID: {}, status: {}, by admin: {}", id, status, adminId);

        Warranty warranty = warrantyRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu bảo hành"));

        User admin = userRepository.findById(Objects.requireNonNull(adminId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        warranty.setStatus(status);
        if (adminNote != null && !adminNote.trim().isEmpty()) {
            warranty.setAdminNote(adminNote);
        }

        if ("processing".equals(status) || "completed".equals(status) || "rejected".equals(status)) {
            warranty.setProcessedBy(admin);
            warranty.setProcessedAt(LocalDateTime.now());
        }

        Warranty updated = warrantyRepository.save(warranty);
        return convertToDto(updated);
    }

    /**
     * Xử lý bảo hành (approve và bắt đầu xử lý)
     */
    @Transactional
    public AdminWarrantyDto processWarranty(Long id, Long adminId, String resolutionNote, String warrantyType) {
        log.info("🔧 Processing warranty - ID: {}, by admin: {}", id, adminId);

        Warranty warranty = warrantyRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu bảo hành"));

        User admin = userRepository.findById(Objects.requireNonNull(adminId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        warranty.setStatus("processing");
        warranty.setProcessedBy(admin);
        warranty.setProcessedAt(LocalDateTime.now());
        
        if (resolutionNote != null && !resolutionNote.trim().isEmpty()) {
            warranty.setResolutionNote(resolutionNote);
        }
        
        if (warrantyType != null && !warrantyType.trim().isEmpty()) {
            warranty.setWarrantyType(warrantyType);
        }

        Warranty updated = warrantyRepository.save(warranty);
        return convertToDto(updated);
    }

    // ===== HELPER METHODS =====

    private AdminWarrantyListDto convertToListDto(Warranty warranty) {
        // Get product image URL from first image if available
        String productImageUrl = null;
        if (warranty.getProduct().getImages() != null && !warranty.getProduct().getImages().isEmpty()) {
            productImageUrl = warranty.getProduct().getImages().get(0).getImageUrl();
        }
        
        // Get variant info from order item if available
        String variant = "";
        Integer warrantyMonths = 12; // Default warranty period
        java.time.LocalDateTime purchaseDate = warranty.getOrder() != null ? warranty.getOrder().getCreatedAt() : null;
        
        return AdminWarrantyListDto.builder()
                .id(warranty.getId())
                .orderId(warranty.getOrder().getId())
                .orderNumber(warranty.getOrder().getOrderNumber())
                .userId(warranty.getUser().getId())
                .userName(warranty.getUser().getFullName())
                .customerName(warranty.getUser().getFullName())
                .customerEmail(warranty.getUser().getEmail())
                .customerPhone(warranty.getUser().getPhoneNumber())
                .productId(warranty.getProduct().getId())
                .productName(warranty.getProduct().getName())
                .productImage(productImageUrl)
                .variant(variant)
                .issue(warranty.getIssueDescription())
                .status(warranty.getStatus())
                .warrantyType(warranty.getWarrantyType())
                .warrantyMonths(warrantyMonths)
                .purchaseDate(purchaseDate)
                .createdAt(warranty.getCreatedAt())
                .build();
    }

    private AdminWarrantyDto convertToDto(Warranty warranty) {
        List<String> images = parseJsonImages(warranty.getImagesJson());
        
        // Get product image URL from first image if available
        String productImageUrl = null;
        if (warranty.getProduct().getImages() != null && !warranty.getProduct().getImages().isEmpty()) {
            productImageUrl = warranty.getProduct().getImages().get(0).getImageUrl();
        }

        return AdminWarrantyDto.builder()
                .id(warranty.getId())
                .orderId(warranty.getOrder().getId())
                .orderNumber(warranty.getOrder().getOrderNumber())
                .userId(warranty.getUser().getId())
                .userName(warranty.getUser().getFullName())
                .userEmail(warranty.getUser().getEmail())
                .userPhone(warranty.getUser().getPhoneNumber())
                .productId(warranty.getProduct().getId())
                .productName(warranty.getProduct().getName())
                .productImageUrl(productImageUrl)
                .issueDescription(warranty.getIssueDescription())
                .status(warranty.getStatus())
                .warrantyType(warranty.getWarrantyType())
                .images(images)
                .adminNote(warranty.getAdminNote())
                .resolutionNote(warranty.getResolutionNote())
                .processedByName(warranty.getProcessedBy() != null ? warranty.getProcessedBy().getFullName() : null)
                .processedAt(warranty.getProcessedAt())
                .createdAt(warranty.getCreatedAt())
                .updatedAt(warranty.getUpdatedAt())
                .build();
    }

    private List<String> parseJsonImages(String imagesJson) {
        if (imagesJson == null || imagesJson.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse images JSON: {}", imagesJson, e);
            return Collections.emptyList();
        }
    }
}

