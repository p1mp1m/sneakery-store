package com.sneakery.store.service;

import com.sneakery.store.dto.AdminReturnDto;
import com.sneakery.store.dto.AdminReturnItemDto;
import com.sneakery.store.dto.AdminReturnListDto;
import com.sneakery.store.dto.ConfirmReturnConditionRequest;
import com.sneakery.store.dto.ReturnItemConditionDto;
import com.sneakery.store.entity.*;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.OrderStatusHistoryRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import com.sneakery.store.repository.ReturnRequestRepository;
import com.sneakery.store.repository.UserRepository;
import com.sneakery.store.repository.LoyaltyPointRepository;
import com.sneakery.store.repository.CouponRepository;
import com.sneakery.store.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final LoyaltyPointRepository loyaltyPointRepository;
    private final CouponRepository couponRepository;

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
//                    Order order = returnRequest.getOrder();
//
//                    if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
//                        log.warn("⚠️ Order #{} không có OrderDetails nào – bỏ qua restock", order.getId());
//                    } else {
//                        log.info("🔄 Restocking inventory for Order #{} – {} items",
//                                order.getId(), order.getOrderDetails().size());
//
//                        for (OrderDetail detail : order.getOrderDetails()) {
//                            ProductVariant variant = detail.getVariant();
//
//                            if (variant == null) {
//                                log.error("⚠️ OrderDetail #{} không có ProductVariant → SKIP restock",
//                                        detail.getId());
//                                continue;
//                            }
//
//                            int qty = detail.getQuantity();
//                            int oldStock = variant.getStockQuantity();
//                            int newStock = oldStock + qty;
//
//                            variant.setStockQuantity(newStock);
//                            variantRepository.save(variant);
//
//                            log.info("🟢 Restocked Variant #{} | {} → {} (+{})",
//                                    variant.getId(), oldStock, newStock, qty);
//                        }
//                    }
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

        // 🧠 Đọc conditions đã lưu (nếu có)
        Map<Long, ReturnItemConditionDto> conditionMap = parseItemConditions(returnRequest);

        List<AdminReturnItemDto> items = returnRequest.getOrder()
                .getOrderDetails()
                .stream()
                .map(detail -> {
                    var variant = detail.getVariant();
                    var product = variant.getProduct();

                    String image = (product.getImages() != null && !product.getImages().isEmpty())
                            ? product.getImages().get(0).getImageUrl()
                            : "";

                    ReturnItemConditionDto condition = conditionMap.get(variant.getId());

                    int goodQty = condition != null
                            ? Optional.ofNullable(condition.getGoodQuantity()).orElse(0)
                            : detail.getQuantity(); // nếu chưa lưu → coi như toàn bộ là hàng tốt

                    int damagedQty = condition != null
                            ? Optional.ofNullable(condition.getDamagedQuantity()).orElse(0)
                            : 0;

                    return AdminReturnItemDto.builder()
                            .variantId(variant.getId())
                            .productId(product.getId())
                            .productName(product.getName())
                            .productImage(image)
                            .variant("Size " + variant.getSize() + " - " + variant.getColor())
                            .quantity(detail.getQuantity())
                            .unitPrice(detail.getUnitPrice())
                            .goodQuantity(goodQty)
                            .damagedQuantity(damagedQty)
                            .build();
                })
                .toList();

        return AdminReturnListDto.builder()
                .id(returnRequest.getId())
                .orderId(returnRequest.getOrder().getId())
                .orderNumber(returnRequest.getOrder().getOrderNumber())
                .userId(returnRequest.getUser().getId())
                .userName(returnRequest.getUser().getFullName())
                .customerName(returnRequest.getUser().getFullName())
                .customerEmail(returnRequest.getUser().getEmail())
                .customerPhone(returnRequest.getUser().getPhoneNumber())
                .reason(returnRequest.getReason())
                .note(returnRequest.getAdminNote())
                .refundAmount(returnRequest.getOrder().getTotalAmount())
                .returnMethod(returnRequest.getReturnMethod())
                .bankName(returnRequest.getBankName())
                .bankAccountNumber(returnRequest.getBankAccountNumber())
                .bankAccountHolder(returnRequest.getBankAccountHolder())
                .status(returnRequest.getStatus())
                .createdAt(returnRequest.getCreatedAt())
                .items(items)
                .build();
    }

    private AdminReturnDto convertToDto(ReturnRequest returnRequest) {

        List<String> images = parseJsonImages(returnRequest.getImagesJson());
        Map<Long, ReturnItemConditionDto> conditionMap = parseItemConditions(returnRequest);

        List<AdminReturnItemDto> items = returnRequest.getOrder()
                .getOrderDetails()
                .stream()
                .map(detail -> {
                    var variant = detail.getVariant();
                    var product = variant.getProduct();

                    ReturnItemConditionDto condition = conditionMap.get(variant.getId());

                    int goodQty = condition != null
                            ? Optional.ofNullable(condition.getGoodQuantity()).orElse(0)
                            : detail.getQuantity();

                    int damagedQty = condition != null
                            ? Optional.ofNullable(condition.getDamagedQuantity()).orElse(0)
                            : 0;

                    return AdminReturnItemDto.builder()
                            .variantId(variant.getId())
                            .productId(product.getId())
                            .productName(product.getName())
                            .productImage(product.getImages() != null && !product.getImages().isEmpty()
                                    ? product.getImages().get(0).getImageUrl()
                                    : "")
                            .variant("Size " + variant.getSize() + " - " + variant.getColor())
                            .quantity(detail.getQuantity())
                            .unitPrice(detail.getUnitPrice())
                            .goodQuantity(goodQty)
                            .damagedQuantity(damagedQty)
                            .build();
                })
                .toList();

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
                .returnMethod(returnRequest.getReturnMethod())
                .bankName(returnRequest.getBankName())
                .bankAccountNumber(returnRequest.getBankAccountNumber())
                .bankAccountHolder(returnRequest.getBankAccountHolder())
                .status(returnRequest.getStatus())
                .images(images)
                .items(items)
                .adminNote(returnRequest.getAdminNote())
                .approvedByName(returnRequest.getApprovedBy() != null
                        ? returnRequest.getApprovedBy().getFullName()
                        : null)
                .approvedAt(returnRequest.getApprovedAt())
                .createdAt(returnRequest.getCreatedAt())
                .updatedAt(returnRequest.getUpdatedAt())
                .build();
    }

    @Transactional
    public AdminReturnDto confirmReturnItemConditions(ConfirmReturnConditionRequest request, Long adminId) {
        ReturnRequest returnRequest = returnRequestRepository.findByIdWithDetails(request.getReturnRequestId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy admin"));

        Order order = returnRequest.getOrder();

        // 🔹 Map để dễ tra cứu điều kiện theo variantId (nếu cần dùng)
        Map<Long, ReturnItemConditionDto> conditionMap = request.getItems().stream()
                .collect(Collectors.toMap(ReturnItemConditionDto::getVariantId, Function.identity()));

        // ====== NGĂN CHẠY LẶP TÀI SẢN ======
        if (Boolean.TRUE.equals(returnRequest.getAssetsRefunded())) {
            log.warn("⚠️ Assets (Stock + Loyalty) đã hoàn trước đó cho ReturnRequest #{}", returnRequest.getId());
        } else {

            // ====== RESTOCK ITEMS ======
            for (ReturnItemConditionDto item : request.getItems()) {
                OrderDetail detail = order.getOrderDetails().stream()
                        .filter(d -> d.getVariant().getId().equals(item.getVariantId()))
                        .findFirst()
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                                "Không tìm thấy variant #" + item.getVariantId() + " trong đơn"));

                ProductVariant variant = detail.getVariant();

                int purchasedQty = detail.getQuantity();
                int totalReturnQty = item.getGoodQuantity() + item.getDamagedQuantity();

                if (totalReturnQty > purchasedQty) {
                    throw new ApiException(HttpStatus.BAD_REQUEST,
                            "Tổng hàng hoàn (" + totalReturnQty + ") vượt quá số lượng đã mua: " + purchasedQty);
                }

                // Hàng tốt
                if (item.getGoodQuantity() > 0) {
                    variant.setStockQuantity(variant.getStockQuantity() + item.getGoodQuantity());
                }

                // Hàng lỗi
                if (item.getDamagedQuantity() > 0) {
                    int currentDamaged = Optional.ofNullable(variant.getDamagedQuantity()).orElse(0);
                    variant.setDamagedQuantity(currentDamaged + item.getDamagedQuantity());
                }

                variantRepository.save(variant);
            }

            // ====== SAVE CONDITIONS JSON ======
            // 🔸 LƯU CONDITIONS VÀO RETURN_REQUEST DƯỚI DẠNG JSON
            try {
                String conditionsJson = JsonUtil.toJson(request.getItems());
                returnRequest.setItemConditionsJson(conditionsJson);
                log.info("📝 Saved itemConditionsJson for ReturnRequest #{}: {} items",
                        returnRequest.getId(), request.getItems().size());
            } catch (Exception e) {
                log.error("❌ Lỗi serialize item conditions cho ReturnRequest #{}",
                        returnRequest.getId(), e);
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Không thể lưu điều kiện hàng hoàn vào yêu cầu đổi trả");
            }

            // ====== LOYALTY POINTS HANDLING ======
            int pointsUsed = Optional.ofNullable(order.getPointsUsed()).orElse(0);
            int pointsEarned = Optional.ofNullable(order.getPointsEarned()).orElse(0);
            User customer = returnRequest.getUser();

            // Hoàn lại điểm đã dùng
            if (pointsUsed > 0) {
                LoyaltyPoint refund = new LoyaltyPoint();
                refund.setUser(customer);
                refund.setPoints(pointsUsed);
                refund.setTransactionType("earn");
                refund.setDescription("Hoàn điểm trả hàng đơn " + order.getOrderNumber());
                refund.setExpiresAt(LocalDateTime.now().plusYears(1));
                loyaltyPointRepository.save(refund);
                log.info("🟢 Refunded {} points to user {}", pointsUsed, customer.getId());
            }

            // Thu hồi điểm earn
            if (pointsEarned > 0) {
                LoyaltyPoint revoke = new LoyaltyPoint();
                revoke.setUser(customer);
                revoke.setPoints(-pointsEarned);
                revoke.setTransactionType("redeem");
                revoke.setDescription("Thu hồi điểm đã earn đơn " + order.getOrderNumber());
                revoke.setExpiresAt(null);
                loyaltyPointRepository.save(revoke);
                log.info("🔻 Deducted {} earned points from user {}", pointsEarned, customer.getId());
            }

            // ====== COUPON HANDLING ======
            Coupon coupon = order.getCoupon();
            if (coupon != null) {
                Integer currentUses = Optional.ofNullable(coupon.getUsesCount()).orElse(0);
                if (currentUses > 0) {
                    coupon.setUsesCount(currentUses - 1); // hoàn lại số lượt sử dụng
                    log.info("🎟️ Coupon restored use count: {} -> {} (Coupon #{})",
                            currentUses, coupon.getUsesCount(), coupon.getId());
                }
                couponRepository.save(coupon);
            }

            // Đánh dấu đã xử lý tài sản tránh chạy lặp
            returnRequest.setAssetsRefunded(true);
        }

        // Duyệt qua từng item được xác nhận
//        for (ReturnItemConditionDto item : request.getItems()) {
//
//            OrderDetail detail = order.getOrderDetails().stream()
//                    .filter(d -> d.getVariant().getId().equals(item.getVariantId()))
//                    .findFirst()
//                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
//                            "Không tìm thấy variant #" + item.getVariantId() + " trong đơn"));
//
//            int purchasedQty = detail.getQuantity();
//            int totalReturnQty = item.getGoodQuantity() + item.getDamagedQuantity();
//
//            if (totalReturnQty > purchasedQty) {
//                throw new ApiException(HttpStatus.BAD_REQUEST,
//                        "Tổng hàng hoàn (" + totalReturnQty + ") vượt quá số lượng đã mua: " + purchasedQty);
//            }
//
//            ProductVariant variant = detail.getVariant();
//
//            // Cộng lại kho cho hàng tốt
//            if (item.getGoodQuantity() > 0) {
//                variant.setStockQuantity(variant.getStockQuantity() + item.getGoodQuantity());
//            }
//
//            // Cộng số lượng hàng lỗi (damaged) vào cột damagedQuantity của variant
//            if (item.getDamagedQuantity() > 0) {
//                Integer currentDamaged = variant.getDamagedQuantity();
//                int safeDamaged = (currentDamaged != null ? currentDamaged : 0);
//                variant.setDamagedQuantity(safeDamaged + item.getDamagedQuantity());
//            }
//
//            variantRepository.save(variant);
//        }
//
//        // 🔸 LƯU CONDITIONS VÀO RETURN_REQUEST DƯỚI DẠNG JSON
//        try {
//            String conditionsJson = JsonUtil.toJson(request.getItems());
//            returnRequest.setItemConditionsJson(conditionsJson);
//            log.info("📝 Saved itemConditionsJson for ReturnRequest #{}: {} items",
//                    returnRequest.getId(), request.getItems().size());
//        } catch (Exception e) {
//            log.error("❌ Lỗi serialize item conditions cho ReturnRequest #{}",
//                    returnRequest.getId(), e);
//            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
//                    "Không thể lưu điều kiện hàng hoàn vào yêu cầu đổi trả");
//        }

        // Cập nhật trạng thái ReturnRequest → completed
        returnRequest.setStatus("completed");
        returnRequest.setApprovedBy(admin);
        returnRequest.setApprovedAt(LocalDateTime.now());
        returnRequest.setAdminNote(request.getAdminNote());
        order.setStatus("return_completed");

        // Lưu lịch sử trạng thái
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("return_completed");
        history.setChangedAt(LocalDateTime.now());
        history.setNote(request.getAdminNote());
        statusHistoryRepository.save(history);

        order.getStatusHistories().add(history);

        ReturnRequest saved = returnRequestRepository.save(returnRequest);

        // 🔹 convertToDto sẽ tự đọc itemConditionsJson để set good/damaged
        return convertToDto(saved);
    }

    private LoyaltyPoint createLoyaltyEntry(User user, int points, String type, String desc) {
        LoyaltyPoint lp = new LoyaltyPoint();
        lp.setUser(user);
        lp.setPoints(points);
        lp.setTransactionType(type);
        lp.setDescription(desc);
        lp.setExpiresAt("earn".equals(type) ? LocalDateTime.now().plusYears(1) : null);
        return lp;
    }

    private List<String> parseJsonImages(String imagesJson) {
        return JsonUtil.parseJsonToStringList(imagesJson);
    }

    private Map<Long, ReturnItemConditionDto> parseItemConditions(ReturnRequest request) {
        String json = request.getItemConditionsJson();
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            List<ReturnItemConditionDto> list =
                    JsonUtil.parseJsonToList(json, ReturnItemConditionDto.class);

            return list.stream()
                    .filter(Objects::nonNull)
                    .filter(c -> c.getVariantId() != null)
                    .collect(Collectors.toMap(
                            ReturnItemConditionDto::getVariantId,
                            Function.identity(),
                            // nếu trùng variantId thì lấy cái mới hơn
                            (c1, c2) -> c2
                    ));
        } catch (Exception e) {
            log.error("❌ Lỗi parse itemConditionsJson cho ReturnRequest #{}",
                    request.getId(), e);
            return Collections.emptyMap();
        }
    }
}

