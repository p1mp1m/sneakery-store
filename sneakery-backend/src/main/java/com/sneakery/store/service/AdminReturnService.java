package com.sneakery.store.service;

import com.sneakery.store.dto.AdminReturnDto;
import com.sneakery.store.dto.AdminReturnListDto;
import com.sneakery.store.entity.*;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.OrderStatusHistoryRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import com.sneakery.store.repository.ReturnRequestRepository;
import com.sneakery.store.repository.UserRepository;
import com.sneakery.store.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ProductVariantRepository variantRepository;

    /**
     * Lấy tất cả return requests với pagination và filter
     */
    @Transactional(readOnly = true)
    public Page<AdminReturnListDto> getAllReturns(
            String search,
            String status,
            String reason,
            Pageable pageable
    ) {
        log.info("🔍 Fetching returns with filters - search: {}, status: {}", search, status);

        Page<ReturnRequest> returns = returnRequestRepository.findAllWithFilters(status, search, reason, pageable);
        return returns.map(this::convertToListDto);
    }

    /**
     * Lấy chi tiết return request
     */
    @Transactional(readOnly = true)
    public AdminReturnDto getReturnById(Long id) {
        log.info("📄 Fetching return detail - ID: {}", id);

        // Use findByIdWithDetails to load all necessary relationships (order, user, approvedBy)
        ReturnRequest returnRequest = returnRequestRepository.findByIdWithDetails(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả"));

        return convertToDto(returnRequest);
    }

    /**
     * Cập nhật trạng thái return request
     */
    @Transactional
    public AdminReturnDto updateReturnStatus(Long id, String status, Long adminId, String adminNote) {
        log.info("✅ Updating return status - ID: {}, status: {}, by admin: {}", id, status, adminId);

        try {
            // Load return request with all relationships
            ReturnRequest returnRequest = returnRequestRepository.findByIdWithDetails(Objects.requireNonNull(id))
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả"));

            User admin = userRepository.findById(Objects.requireNonNull(adminId))
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

            // Update fields
            returnRequest.setStatus(status);
            if (adminNote != null && !adminNote.trim().isEmpty()) {
                returnRequest.setAdminNote(adminNote);
            }

            if ("approved".equals(status) || "rejected".equals(status)) {
                returnRequest.setApprovedBy(admin);
                returnRequest.setApprovedAt(LocalDateTime.now());
            }

            // ====== UPDATE ORDER STATUS ======
            switch (status.toLowerCase()) {
                case "approved":
                    returnRequest.getOrder().setStatus("return_approved");
                    break;

                case "rejected":
                    returnRequest.getOrder().setStatus("return_rejected");
                    break;

                case "completed":
                    returnRequest.getOrder().setStatus("return_completed");
                    // ====== RESTOCK VARIANTS HERE ======
                    Order order = returnRequest.getOrder();

                    if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
                        log.warn("⚠️ Order #{} không có OrderDetails nào – bỏ qua restock", order.getId());
                    } else {
                        log.info("🔄 Restocking inventory for Order #{} – {} items",
                                order.getId(), order.getOrderDetails().size());

                        for (OrderDetail detail : order.getOrderDetails()) {
                            ProductVariant variant = detail.getVariant();

                            if (variant == null) {
                                log.error("⚠️ OrderDetail #{} không có ProductVariant → SKIP restock",
                                        detail.getId());
                                continue;
                            }

                            int qty = detail.getQuantity();
                            int oldStock = variant.getStockQuantity();
                            int newStock = oldStock + qty;

                            variant.setStockQuantity(newStock);
                            variantRepository.save(variant);

                            log.info("🟢 Restocked Variant #{} | {} → {} (+{})",
                                    variant.getId(), oldStock, newStock, qty);
                        }
                    }
                    break;

                default:
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ: " + status);
            }

            returnRequest.getOrder().setUpdatedAt(LocalDateTime.now());

            // ====== SAVE ORDER STATUS HISTORY ======
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(returnRequest.getOrder());
            history.setStatus(returnRequest.getOrder().getStatus());
            history.setChangedAt(LocalDateTime.now());
            history.setNote(adminNote);
            statusHistoryRepository.save(history);

            returnRequest.getOrder().getStatusHistories().add(history);

            // Save entity
            returnRequestRepository.save(returnRequest);
            
            // Reload with all relationships to ensure they are properly loaded after save
            ReturnRequest updated = returnRequestRepository.findByIdWithDetails(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả sau khi cập nhật"));
            
            // Convert to DTO
            return convertToDto(updated);
            
        } catch (ApiException e) {
            log.error("Error updating return status: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating return status - ID: {}, status: {}", id, status, e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
    }

    /**
     * Xử lý hoàn tiền
     */
    @Transactional
    public AdminReturnDto processRefund(Long id, Long adminId) {
        log.info("💰 Processing refund - ID: {}, by admin: {}", id, adminId);

        // Use findByIdWithDetails to load all necessary relationships (order, user, approvedBy)
        ReturnRequest returnRequest = returnRequestRepository.findByIdWithDetails(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả"));

        if (!"approved".equals(returnRequest.getStatus())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Yêu cầu chưa được duyệt");
        }

        User admin = userRepository.findById(Objects.requireNonNull(adminId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        // Update status to completed
        returnRequest.setStatus("completed");
        returnRequest.setApprovedBy(admin);

        // ====== UPDATE ORDER STATUS ======
        returnRequest.getOrder().setStatus("return_completed");
        returnRequest.getOrder().setUpdatedAt(LocalDateTime.now());

        // Save entity (changes will be flushed when transaction commits)
        // Note: We use the entity loaded with findByIdWithDetails, so all relationships are already loaded
        ReturnRequest saved = returnRequestRepository.save(returnRequest);

        // Convert to DTO using the saved entity (relationships are already loaded from findByIdWithDetails)
        return convertToDto(saved);
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
        if (returnRequest == null) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Return request is null");
        }

        List<String> images = parseJsonImages(returnRequest.getImagesJson());

        // Validate required relationships
        if (returnRequest.getOrder() == null) {
            log.error("Return request #{} has null order", returnRequest.getId());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Return request has no associated order");
        }

        if (returnRequest.getUser() == null) {
            log.error("Return request #{} has null user", returnRequest.getId());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Return request has no associated user");
        }

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
        return JsonUtil.parseJsonToStringList(imagesJson);
    }
}

