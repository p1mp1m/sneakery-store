package com.sneakery.store.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sneakery.store.dto.AdminReturnDto;
import com.sneakery.store.dto.AdminReturnListDto;
import com.sneakery.store.entity.ReturnRequest;
import com.sneakery.store.entity.User;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.ReturnRequestRepository;
import com.sneakery.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Service: AdminReturnService
 * Quản lý return requests cho admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReturnService {

    private final ReturnRequestRepository returnRequestRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Lấy tất cả return requests với pagination và filter
     */
    @Transactional(readOnly = true)
    public Page<AdminReturnListDto> getAllReturns(
            String search,
            String status,
            Pageable pageable
    ) {
        log.info("🔍 Fetching returns with filters - search: {}, status: {}", search, status);

        Page<ReturnRequest> returns = returnRequestRepository.findAllWithFilters(status, search, pageable);

        return returns.map(this::convertToListDto);
    }

    /**
     * Lấy chi tiết return request
     */
    @Transactional(readOnly = true)
    public AdminReturnDto getReturnById(Long id) {
        log.info("📄 Fetching return detail - ID: {}", id);

        ReturnRequest returnRequest = returnRequestRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả"));

        return convertToDto(returnRequest);
    }

    /**
     * Cập nhật trạng thái return request
     */
    @Transactional
    public AdminReturnDto updateReturnStatus(Long id, String status, Long adminId, String adminNote) {
        log.info("✅ Updating return status - ID: {}, status: {}, by admin: {}", id, status, adminId);

        ReturnRequest returnRequest = returnRequestRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        returnRequest.setStatus(status);
        if (adminNote != null && !adminNote.trim().isEmpty()) {
            returnRequest.setAdminNote(adminNote);
        }

        if ("approved".equals(status) || "rejected".equals(status)) {
            returnRequest.setApprovedBy(admin);
            returnRequest.setApprovedAt(LocalDateTime.now());
        }

        ReturnRequest updated = returnRequestRepository.save(returnRequest);
        return convertToDto(updated);
    }

    /**
     * Xử lý hoàn tiền
     */
    @Transactional
    public AdminReturnDto processRefund(Long id, Long adminId) {
        log.info("💰 Processing refund - ID: {}, by admin: {}", id, adminId);

        ReturnRequest returnRequest = returnRequestRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả"));

        if (!"approved".equals(returnRequest.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu chưa được duyệt");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        // Update status to completed
        returnRequest.setStatus("completed");
        returnRequest.setApprovedBy(admin);

        ReturnRequest updated = returnRequestRepository.save(returnRequest);
        return convertToDto(updated);
    }

    // ===== HELPER METHODS =====

    private AdminReturnListDto convertToListDto(ReturnRequest returnRequest) {
        // Get first order detail for product info (assuming return is for first item, or you can aggregate)
        String productName = "";
        String productImage = "";
        Long productId = null;
        String variant = "";
        Integer quantity = 0;
        java.math.BigDecimal unitPrice = java.math.BigDecimal.ZERO;
        
        if (returnRequest.getOrder() != null && 
            returnRequest.getOrder().getOrderDetails() != null && 
            !returnRequest.getOrder().getOrderDetails().isEmpty()) {
            
            var firstDetail = returnRequest.getOrder().getOrderDetails().get(0);
            var productVariant = firstDetail.getVariant();
            var product = productVariant.getProduct();
            
            productId = product.getId();
            productName = product.getName();
            quantity = firstDetail.getQuantity();
            unitPrice = firstDetail.getUnitPrice();
            
            // Get product image
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                productImage = product.getImages().get(0).getImageUrl();
            }
            
            // Get variant info
            variant = "Size " + productVariant.getSize() + " - " + productVariant.getColor();
        }
        
        return AdminReturnListDto.builder()
                .id(returnRequest.getId())
                .orderId(returnRequest.getOrder().getId())
                .orderNumber(returnRequest.getOrder().getOrderNumber())
                .userId(returnRequest.getUser().getId())
                .userName(returnRequest.getUser().getFullName())
                .customerName(returnRequest.getUser().getFullName())
                .customerEmail(returnRequest.getUser().getEmail())
                .customerPhone(returnRequest.getUser().getPhoneNumber())
                .productId(productId)
                .productName(productName)
                .productImage(productImage)
                .variant(variant)
                .quantity(quantity)
                .reason(returnRequest.getReason())
                .note(returnRequest.getAdminNote())
                .refundAmount(returnRequest.getOrder().getTotalAmount())
                .unitPrice(unitPrice)
                .status(returnRequest.getStatus())
                .createdAt(returnRequest.getCreatedAt())
                .build();
    }

    private AdminReturnDto convertToDto(ReturnRequest returnRequest) {
        List<String> images = parseJsonImages(returnRequest.getImagesJson());

        return AdminReturnDto.builder()
                .id(returnRequest.getId())
                .orderId(returnRequest.getOrder().getId())
                .orderNumber(returnRequest.getOrder().getOrderNumber())
                .orderTotal(returnRequest.getOrder().getTotalAmount())
                .userId(returnRequest.getUser().getId())
                .userName(returnRequest.getUser().getFullName())
                .userEmail(returnRequest.getUser().getEmail())
                .userPhone(returnRequest.getUser().getPhoneNumber())
                .reason(returnRequest.getReason())
                .status(returnRequest.getStatus())
                .images(images)
                .adminNote(returnRequest.getAdminNote())
                .approvedByName(returnRequest.getApprovedBy() != null ? returnRequest.getApprovedBy().getFullName() : null)
                .approvedAt(returnRequest.getApprovedAt())
                .createdAt(returnRequest.getCreatedAt())
                .updatedAt(returnRequest.getUpdatedAt())
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

