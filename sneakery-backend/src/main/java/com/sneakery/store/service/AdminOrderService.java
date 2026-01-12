package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.*;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final LoyaltyService loyaltyService;
    private final AddressRepository addressRepository;
    private static final BigDecimal VAT_RATE = BigDecimal.valueOf(0.1); // 10%
    private final ReturnRequestRepository returnRequestRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;
    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    public Page<AdminOrderListDto> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllWithUser(pageable);
        return orderPage.map(this::convertToOrderListDto);
    }

    /**
     * Lấy danh sách đơn hàng với search và filter
     */
    @Transactional(readOnly = true)
    public Page<AdminOrderListDto> getAllOrdersWithFilters(
            String search,
            String status,
            String channel,
            LocalDateTime startDate,
            LocalDateTime endDateExclusive,
            Pageable pageable
    ) {
        String cleanedSearch = preprocessSearch(search);

        // IMPORTANT: status filter phải đưa về backend format
        String normalizedStatus = normalizeOrderStatus(status);

        // ✅ Detect search numeric => search by id EXACT
        boolean searchIsNumeric = isNumeric(cleanedSearch);
        Long searchId = searchIsNumeric ? Long.valueOf(cleanedSearch) : null;

        Page<Order> orderPage = orderRepository.findAllWithUserAndFilters(
                cleanedSearch,
                searchIsNumeric,
                searchId,
                normalizedStatus,
                channel,
                startDate,
                endDateExclusive,
                pageable
        );

        return orderPage.map(this::convertToOrderListDto);
    }

    private boolean isNumeric(String s) {
        return s != null && s.matches("\\d+");
    }

    private String preprocessSearch(String search) {
        if (search == null) return null;
        String s = search.trim();
        if (s.isEmpty()) return null;

        // user hay gõ "#51"
        if (s.startsWith("#")) {
            s = s.substring(1).trim();
        }
        return s.isEmpty() ? null : s;
    }

    // ... (Giữ nguyên các hàm còn lại: getOrderById, updateOrderStatus, và các hàm
    // helper)

    @Transactional(readOnly = true)
    public AdminOrderDetailDto getOrderById(Long orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Load payments và statusHistories riêng để tránh MultipleBagFetchException
        // Trigger lazy loading trong cùng transaction
        order.getPayments().size(); // Trigger lazy load
        order.getStatusHistories().size(); // Trigger lazy load

        return convertToOrderDetailDto(order);
    }

    private String detectOrderChannel(Order order) {
        // Rule A: Ưu tiên orderNumber
        String orderNumber = order.getOrderNumber();
        if (orderNumber != null) {
            if (orderNumber.startsWith("POS-")) return "POS";
            if (orderNumber.startsWith("ORD-")) return "ONLINE";
        }

        // Rule B: Fallback theo địa chỉ cửa hàng (dựa theo chính dữ liệu POS bạn đang tạo)
        Address ship = order.getAddressShipping();
        if (ship != null) {
            String line1 = ship.getLine1() != null ? ship.getLine1().toLowerCase() : "";
            String line2 = ship.getLine2() != null ? ship.getLine2().toLowerCase() : "";

            if (line1.contains("cửa hàng sneakery")
                    || line2.contains("bán tại quầy")
                    || line2.contains("pos")
                    || line2.contains("trịnh văn bô")) {
                return "POS";
            }
        }

        // Default
        return "ONLINE";
    }

    @Transactional
    public AdminOrderDetailDto updateOrderStatus(Long orderId, String newStatus) {
        // Load order với đầy đủ relationships để tránh lỗi khi convert DTO
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Trigger lazy loading cho payments và statusHistories trong cùng transaction
        order.getPayments().size(); // Trigger lazy load
        order.getStatusHistories().size(); // Trigger lazy load

        // Map status từ frontend format sang backend format
        String normalizedStatus = normalizeOrderStatus(newStatus);
        log.info("🔄 Updating order #{} status: {} -> {}", orderId, order.getStatus(), normalizedStatus);

        String oldStatus = order.getStatus();
        order.setStatus(normalizedStatus);

        // Kiểm tra xem có phải POS order không (orderNumber bắt đầu bằng "POS-")
        boolean isPOSOrder = order.getOrderNumber() != null && order.getOrderNumber().startsWith("POS-");

        // ⭐ Redeem Loyalty Points khi Admin xác nhận (Confirmed)
        if (!isPOSOrder
                && "confirmed".equalsIgnoreCase(normalizedStatus)
                && !"confirmed".equalsIgnoreCase(oldStatus)) {

            Integer pointsUsed = order.getPointsUsed() != null ? order.getPointsUsed() : 0;
            if (pointsUsed > 0 && order.getUser() != null) {
                try {
                    log.info("🎯 Redeeming {} points for online order #{}", pointsUsed, orderId);
                    loyaltyService.redeemPointsInNewTx(order.getUser().getId(), pointsUsed, order);
                } catch (Exception e) {
                    log.error("❌ Redeem points failed for order #{}: {}", orderId, e.getMessage(), e);
                }
            }
        }
        // ====== LOYALTY POINT REFUND FOR CANCELLED ORDER ======
        Integer pointsUsed = Optional.ofNullable(order.getPointsUsed()).orElse(0);
        User customer = order.getUser();

// Chỉ hoàn điểm nếu: không phải POS + có dùng điểm + bị hủy trước khi giao
        if (!isPOSOrder
                && "cancelled".equalsIgnoreCase(normalizedStatus)
                && !"cancelled".equalsIgnoreCase(oldStatus)
                && !"delivered".equalsIgnoreCase(oldStatus)
                && pointsUsed > 0
                && customer != null) {

            LoyaltyPoint refund = new LoyaltyPoint();
            refund.setUser(customer);
            refund.setPoints(pointsUsed);
            refund.setTransactionType("earn");
            refund.setDescription("Hoàn điểm do đơn hàng bị hủy: " + order.getOrderNumber());
            refund.setExpiresAt(LocalDateTime.now().plusYears(1));

            loyaltyPointRepository.save(refund);

            log.info("🟢 Refunded {} points for cancelled order #{} to user {}",
                    pointsUsed, orderId, customer.getId());
        }

        // ====== RELEASE RESERVED STOCK WHEN CANCELLED ======
        if (!isPOSOrder
                && "cancelled".equalsIgnoreCase(normalizedStatus)
                && !"cancelled".equalsIgnoreCase(oldStatus)
                && !"delivered".equalsIgnoreCase(oldStatus)) {

            log.info("🔓 Releasing reserved stock for cancelled order #{}", orderId);

            for (OrderDetail detail : order.getOrderDetails()) {
                ProductVariant variant = detail.getVariant();
                if (variant != null) {
                    int releaseQty = detail.getQuantity();
                    variant.setReservedQuantity(
                            Math.max(0, variant.getReservedQuantity() - releaseQty)
                    );
                    variantRepository.save(variant);

                    Long productId = variant.getProduct().getId();
                    cacheManager.getCache("products").evict(productId);

                    log.info("🟢 Released {} reserved units for variant {} -> reserved now {}",
                            releaseQty, variant.getId(), variant.getReservedQuantity());
                }
            }
        }

        // Đối với online/offline orders: trừ kho khi status = "completed" (delivered)
        // POS orders đã được trừ kho khi tạo, không cần trừ lại
        if (!isPOSOrder && "delivered".equalsIgnoreCase(normalizedStatus) && !"delivered".equalsIgnoreCase(oldStatus)) {
            log.info("📦 Deducting stock for online/offline order #{} (status changed to Completed)", orderId);

            // Load orderDetails để trừ kho
            for (OrderDetail detail : order.getOrderDetails()) {
                ProductVariant variant = detail.getVariant();
                // Nếu variant null (lazy loading chưa trigger), cần load lại
                // Nhưng thường thì orderDetails đã được load với variant rồi từ
                // findByIdWithDetails
                if (variant == null) {
                    log.warn("⚠️ Variant is null for order detail ID: {}. Order details may not be loaded properly.",
                            detail.getId());
                    continue;
                }

                // Kiểm tra tồn kho trước khi trừ
                int currentStock = variant.getStockQuantity();
                int quantityToDeduct = detail.getQuantity();

                if (currentStock < quantityToDeduct) {
                    log.error("❌ Insufficient stock for variant {}: Current={}, Required={}",
                            variant.getId(), currentStock, quantityToDeduct);
                    throw new ApiException(HttpStatus.BAD_REQUEST,
                            String.format("Sản phẩm %s không đủ hàng để hoàn thành đơn hàng. Tồn kho: %d, Yêu cầu: %d",
                                    detail.getProductName(), currentStock, quantityToDeduct));
                }

                // Trừ kho
//                variant.setStockQuantity(currentStock - quantityToDeduct);
                // 🔥 Deduct actual stock
                variant.setStockQuantity(currentStock - quantityToDeduct);

                // 🔓 Release reserved stock
                variant.setReservedQuantity(
                        variant.getReservedQuantity() - quantityToDeduct
                );

                variantRepository.save(variant);
                log.info("✅ Deducted {} units from variant {} (new stock: {})",
                        quantityToDeduct, variant.getId(), variant.getStockQuantity());

                if (order.getUser() != null) {
                    try {
                        loyaltyService.earnPointsFromOrder(order);
                        log.info("🎁 Earned loyalty points for delivered order #{}", orderId);
                    } catch (Exception e) {
                        log.warn("⚠️ Earn points failed for order #{}: {}", orderId, e.getMessage(), e);
                    }
                }
            }
        }

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(normalizedStatus);
        history.setChangedAt(LocalDateTime.now());

        // Save history và thêm vào list của order để đảm bảo nó có trong DTO
        statusHistoryRepository.save(history);
        order.getStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        log.info("✅ Order #{} status updated successfully to: {}", orderId, normalizedStatus);

        // Convert trực tiếp order đã save thay vì query lại
        // Đảm bảo relationships vẫn được giữ trong cùng transaction
        return convertToOrderDetailDto(savedOrder);
    }

    /**
     * Map order status từ frontend format (PascalCase) sang backend format
     * (lowercase)
     * Frontend: Pending, Processing, Shipped, Completed, Cancelled
     * Backend: pending, processing, shipped, delivered, cancelled
     */
    private String normalizeOrderStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return status;
        }

        String normalized = status.trim();

        // Map từ PascalCase sang lowercase với các mapping đặc biệt
        switch (normalized) {
            case "Pending":
                return "pending";
            case "Processing":
                return "processing";
            case "Shipped":
                return "shipped";
            case "Completed":
                return "delivered"; // Frontend dùng "Completed" nhưng backend dùng "delivered"
            case "Cancelled":
                return "cancelled";
            case "Confirmed":
                return "confirmed";
            case "Packed":
                return "packed";
            case "Refunded":
                return "refunded";
            default:
                // Nếu không match, chuyển về lowercase và log warning
                String lowercased = normalized.toLowerCase();
                if (!lowercased.matches("pending|confirmed|processing|packed|shipped|delivered|cancelled|refunded")) {
                    log.warn("⚠️ Unknown order status: {}. Using as-is: {}", normalized, lowercased);
                }
                return lowercased;
        }
    }

    private AdminOrderListDto convertToOrderListDto(Order order) {
        ReturnRequestSummaryDto returnRequestSummary = null;
        Optional<ReturnRequest> opt = returnRequestRepository.findByOrderIdWithDetails(order.getId());
        if (opt.isPresent()) {
            ReturnRequest rr = opt.get();
            returnRequestSummary = ReturnRequestSummaryDto.builder()
                    .id(rr.getId())
                    .status(rr.getStatus())
                    .createdAt(rr.getCreatedAt())
                    .reason(rr.getReason() != null && rr.getReason().length() > 50
                            ? rr.getReason().substring(0, 50) + "..."
                            : rr.getReason())
                    // ⬇️ Thêm returnMethod để biết yêu cầu là Refund
                    .returnMethod(rr.getReturnMethod())
                    .build();
        }

        String orderChannel = detectOrderChannel(order);

        return AdminOrderListDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderChannel(orderChannel)
                .customerName(order.getUser() != null ? order.getUser().getFullName() : "Guest")
                .customerEmail(order.getUser() != null ? order.getUser().getEmail() : "N/A")
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .returnRequest(returnRequestSummary)
                .build();
    }

    private AdminOrderDetailDto convertToOrderDetailDto(Order order) {
        // === Load Return Request (nếu có) ===
        ReturnRequestDto returnRequestDto = null;

        try {
            ReturnRequest returnRequest = returnRequestRepository.findByOrderIdWithDetails(order.getId()).orElse(null);

            if (returnRequest != null) {
                returnRequestDto = ReturnRequestDto.builder()
                        .id(returnRequest.getId())
                        .orderId(order.getId())

                        // User yêu cầu đổi trả
                        .userId(returnRequest.getUser() != null ? returnRequest.getUser().getId() : null)
                        .customerName(returnRequest.getUser() != null ? returnRequest.getUser().getFullName() : null)
                        .customerEmail(returnRequest.getUser() != null ? returnRequest.getUser().getEmail() : null)

                        // Nội dung
                        .reason(returnRequest.getReason())
                        .status(returnRequest.getStatus())

                        // Images JSON → List<String>
                        .images(decodeImagesJson(returnRequest.getImagesJson()))

                        // 🚀 Thông tin hoàn tiền (bổ sung)
                        .returnMethod(returnRequest.getReturnMethod())
                        .bankName(returnRequest.getBankName())
                        .bankAccountNumber(returnRequest.getBankAccountNumber())
                        .bankAccountHolder(returnRequest.getBankAccountHolder())

                        .adminNote(returnRequest.getAdminNote())

                        // Admin duyệt
                        .approvedByName(
                                (returnRequest.getApprovedBy() != null)
                                        ? returnRequest.getApprovedBy().getFullName()
                                        : null
                        )
                        .approvedAt(returnRequest.getApprovedAt())

                        // Timestamps
                        .createdAt(returnRequest.getCreatedAt())
                        .updatedAt(returnRequest.getUpdatedAt())
                        .build();
            }
        } catch (Exception e) {
            log.warn("⚠️ Cannot load ReturnRequest for order {}: {}", order.getId(), e.getMessage());
        }

        List<CartItemDto> detailDtos = order.getOrderDetails().stream().map(detail -> {
            var v = detail.getVariant();

            String sku = "";
            // Ưu tiên SKU tại thời điểm đặt hàng (denormalized)
            if (detail.getVariantSku() != null && !detail.getVariantSku().isEmpty()) {
                sku = detail.getVariantSku();
            }
            // Nếu không có trong detail → fallback SKU của variant hiện tại
            else if (detail.getVariant() != null && detail.getVariant().getSku() != null) {
                sku = detail.getVariant().getSku();
            }

            if (v == null) {
                // Fallback nếu variant null (không nên xảy ra nhưng phòng tránh)
                return CartItemDto.builder()
                        .variantId(null)
                        .productName(detail.getProductName() != null ? detail.getProductName() : "N/A")
                        .sku(sku)
                        .brandName("N/A")
                        .size(detail.getSize() != null ? detail.getSize() : "")
                        .color(detail.getColor() != null ? detail.getColor() : "")
//                        .imageUrl("")
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .totalPrice(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                        .build();
            }

            // Lấy product và brand với null safety
            String productName = "N/A";
            String brandName = "N/A";
            String imageUrl = "";

            if (v.getProduct() != null) {
                productName = v.getProduct().getName() != null ? v.getProduct().getName() : "N/A";
                if (v.getProduct().getBrand() != null) {
                    brandName = v.getProduct().getBrand().getName() != null ? v.getProduct().getBrand().getName()
                            : "N/A";
                }
            } else {
                // Fallback: dùng denormalized data từ OrderDetail
                productName = detail.getProductName() != null ? detail.getProductName() : "N/A";
            }

            if (v.getImageUrl() != null) {
                imageUrl = v.getImageUrl();
            }

            return CartItemDto.builder()
                    .variantId(v.getId())
                    .sku(sku)
                    .productName(productName)
                    .brandName(brandName)
                    .size(v.getSize() != null ? v.getSize() : detail.getSize() != null ? detail.getSize() : "")
                    .color(v.getColor() != null ? v.getColor() : detail.getColor() != null ? detail.getColor() : "")
//                    .imageUrl(imageUrl)
                    .quantity(detail.getQuantity())
                    .unitPrice(detail.getUnitPrice())
                    .totalPrice(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

        Payment p = order.getPayments().stream().findFirst().orElse(null);
        PaymentDto paymentDto = (p == null) ? null
                : PaymentDto.builder()
                .id(p.getId())
                .paymentMethod(p.getPaymentMethod())
                .status(p.getStatus())
                .amount(p.getAmount())
                .paidAt(p.getPaidAt())
                .build();

        List<OrderStatusHistoryDto> historyDtos = order.getStatusHistories().stream()
                .map(h -> OrderStatusHistoryDto.builder()
                        .id(h.getId())
                        .status(h.getStatus())
                        .changedAt(h.getChangedAt())
                        .build())
                .collect(Collectors.toList());

        Integer customerPointBalance = 0;
        if (order.getUser() != null) {
            customerPointBalance = loyaltyService.getUserPointsBalance(order.getUser().getId());
        }

        return AdminOrderDetailDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())

                // 💰 Tiền
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .shippingFee(order.getShippingFee())
                .taxAmount(order.getTaxAmount())
                .pointsUsed(order.getPointsUsed())
                .pointsDiscount(order.getPointsUsed() != null
                        ? BigDecimal.valueOf(order.getPointsUsed() * 1000L)
                        : BigDecimal.ZERO)
                .totalAmount(order.getTotalAmount())

                // ⏱️ thời gian
                .createdAt(order.getCreatedAt())

                // 👤 User
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .customerName(order.getUser() != null ? order.getUser().getFullName() : "Guest")
                .customerEmail(order.getUser() != null ? order.getUser().getEmail() : "N/A")
                .customerPointBalance(customerPointBalance)

                // 🏠 Address
                .addressShipping(convertToAddressDto(order.getAddressShipping()))
                .addressBilling(convertToAddressDto(order.getAddressBilling()))

                // 💳 Payment
                .payment(paymentDto)

                // 📦 Items
                .orderDetails(detailDtos)

                // 🕒 Status
                .statusHistories(historyDtos)

                // 🔁 Return request
                .returnRequest(returnRequestDto)
                .build();

    }

    private List<String> decodeImagesJson(String imagesJson) {
        if (imagesJson == null || imagesJson.isBlank()) return List.of();
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(imagesJson, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("❌ Failed to decode images JSON: {}", e.getMessage());
            return List.of();
        }
    }

    private AddressDto convertToAddressDto(Address address) {
        if (address == null)
            return null;
        return AddressDto.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .district(address.getDistrict())
                .ward(address.getWard())
                .postalCode(address.getPostalCode())
                .build();
    }

    /**
     * Tạo đơn hàng từ POS (Point of Sale)
     * Cho phép customerId = null (khách vãng lai)
     */
    @Transactional
    public OrderDto createPOSOrder(POSOrderRequestDto requestDto) {
        log.info("📦 Creating POS order with {} items", requestDto.getItems().size());

        // 1. Generate order number
        String orderNumber = generatePOSOrderNumber();

        // 2. Get customer (optional)
        User user = null;
        if (requestDto.getCustomerId() != null) {
            user = userRepository.findById(requestDto.getCustomerId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));
        }

        // 3. Create POS default address
        Address posAddress = new Address();

        posAddress.setRecipientName(
                requestDto.getCustomerName() != null ?
                        requestDto.getCustomerName() :
                        (user != null ? user.getFullName() : "Khách vãng lai")
        );

//        posAddress.setEmail(
//                requestDto.getCustomerEmail() != null ?
//                        requestDto.getCustomerEmail() :
//                        (user != null ? user.getEmail() : null)
//        );

        String phone =
                (requestDto.getCustomerPhone() != null && !requestDto.getCustomerPhone().isBlank())
                        ? requestDto.getCustomerPhone()
                        : (user != null && user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank())
                        ? user.getPhoneNumber()
                        : "0900000000"; // 📌 phone mặc định cho POS guest

        posAddress.setPhone(phone);

        posAddress.setLine1("Cửa hàng Sneakery");
        posAddress.setLine2("Bán tại quầy POS - 13 Trịnh Văn Bô");
        posAddress.setCity("Hà Nội");
        posAddress.setDistrict("Quận Nam Từ Liêm");
        posAddress.setWard("Phường Xuân Phương");
        posAddress.setPostalCode("100000");

        // ⚠️ Address bắt buộc phải có user (DB constraint)
        if (user == null) {
            user = userRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ApiException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Không tìm thấy user hệ thống để gán cho POS address"
                    ));
        }

        posAddress.setUser(user);

        Address savedAddress = addressRepository.save(posAddress);

        // 4. Initialize order
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(orderNumber);
        order.setAddressShipping(savedAddress);
        order.setAddressBilling(savedAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("delivered");
        order.setShippingFee(BigDecimal.ZERO);
        order.setSubtotal(BigDecimal.ZERO);

        BigDecimal subtotal = BigDecimal.ZERO;

        // 5. Handle items
        for (POSOrderItemDto itemDto : requestDto.getItems()) {

            ProductVariant variant;
            if (itemDto.getVariantId() != null) {
                variant = variantRepository.findByIdWithDetails(itemDto.getVariantId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                                "Không tìm thấy variant ID: " + itemDto.getVariantId()));
            } else {
                variant = variantRepository.findWithFilters(
                                null, null, null, itemDto.getProductId(), null,
                                org.springframework.data.domain.PageRequest.of(0, 1))
                        .getContent().stream().findFirst()
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                                "Không tìm thấy variant cho product ID: " + itemDto.getProductId()));
            }

            // Stock check
            if (variant.getStockQuantity() == null || variant.getStockQuantity() < itemDto.getQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        String.format("Sản phẩm %s (Size %s, Color %s) không đủ kho",
                                variant.getProduct().getName(),
                                variant.getSize(),
                                variant.getColor()));
            }

            // Deduct stock
            int newStock = variant.getStockQuantity() - itemDto.getQuantity();
            variant.setStockQuantity(newStock);
            variantRepository.save(variant);

            Long productId = variant.getProduct().getId();
            cacheManager.getCache("products").evict(productId);

            // Get price
            BigDecimal price = getEffectivePrice(variant);

            // Create order detail
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(itemDto.getQuantity());
            detail.setUnitPrice(price);
            detail.setProductName(variant.getProduct().getName());
            detail.setVariantSku(variant.getSku());
            detail.setSize(variant.getSize());
            detail.setColor(variant.getColor());

            BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            detail.setTotalPrice(totalPrice);

            order.getOrderDetails().add(detail);

            subtotal = subtotal.add(totalPrice);
        }

        order.setSubtotal(subtotal);

        // =============================
        // 6. Handle COUPON (Like Client)
        // =============================
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;

        if (requestDto.getDiscountCode() != null && !requestDto.getDiscountCode().isBlank()) {
            CouponDto couponDto = couponService.validateCouponCode(requestDto.getDiscountCode());
            coupon = couponRepository.findById(couponDto.getId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Coupon không tồn tại"));

            if ("percent".equalsIgnoreCase(coupon.getDiscountType())) {
                discountAmount = subtotal.multiply(coupon.getValue())
                        .divide(BigDecimal.valueOf(100));
                if (coupon.getMaxDiscountAmount() != null &&
                        discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                    discountAmount = coupon.getMaxDiscountAmount();
                }
            } else {
                discountAmount = coupon.getValue();
            }

            if (coupon.getMinOrderAmount() != null &&
                    subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Đơn hàng chưa đạt giá trị tối thiểu để dùng mã giảm giá");
            }

            order.setCoupon(coupon);

            // Update uses
            coupon.setUsesCount((coupon.getUsesCount() == null ? 0 : coupon.getUsesCount()) + 1);
            couponRepository.save(coupon);
        }

        order.setDiscountAmount(discountAmount);

        // =============================
        // 7. Handle LOYALTY POINTS
        // =============================
        int pointsUsed = requestDto.getPointsUsed() != null ? requestDto.getPointsUsed() : 0;
        BigDecimal pointsDiscount = BigDecimal.ZERO;

        if (user != null && pointsUsed > 0) {
            // Apply redeem
            pointsDiscount = loyaltyService.redeemPoints(user.getId(), pointsUsed, order);
            order.setPointsUsed(pointsUsed);
        }

        // =============================
        // 8. TAX CALCULATION (Like Client)
        // =============================

        BigDecimal taxableAmount = subtotal
                .subtract(discountAmount)
                .subtract(pointsDiscount);

        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) taxableAmount = BigDecimal.ZERO;

        BigDecimal taxAmount = BigDecimal.ZERO;
        order.setTaxAmount(taxAmount);

        BigDecimal totalAmount = taxableAmount;
        order.setTotalAmount(totalAmount);

        // =============================
        // 9. Payment
        // =============================
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(mapPaymentMethod(requestDto.getPaymentMethod()));
        payment.setStatus("completed");
        payment.setPaidAt(LocalDateTime.now());

        order.getPayments().add(payment);

        // =============================
        // 10. Status history
        // =============================
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("delivered");
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistories().add(history);

        // =============================
        // 11. Save order
        // =============================
        Order savedOrder = orderRepository.save(order);

        // =============================
        // 12. Earn points afterwards
        // =============================
        if (user != null) {
            loyaltyService.earnPointsFromOrder(savedOrder);
        }

        // =============================
        // 13. Return DTO
        // =============================
        // 13. Build DTO (và inject thêm thông tin POS từ request)
        OrderDto dto = convertToOrderDto(savedOrder);
        // Ưu tiên thông tin do POS nhập, fallback sang user nếu có
        dto.setPosCustomerName(
                requestDto.getCustomerName() != null && !requestDto.getCustomerName().isBlank()
                        ? requestDto.getCustomerName()
                        : (user != null ? user.getFullName() : "Khách vãng lai")
        );

        dto.setPosCustomerEmail(
                requestDto.getCustomerEmail() != null && !requestDto.getCustomerEmail().isBlank()
                        ? requestDto.getCustomerEmail()
                        : (user != null ? user.getEmail() : null)
        );

        dto.setPosCustomerPhone(
                requestDto.getCustomerPhone() != null && !requestDto.getCustomerPhone().isBlank()
                        ? requestDto.getCustomerPhone()
                        : (user != null ? user.getPhoneNumber() : null)
        );

        return dto;
    }

    /**
     * Generate POS order number: POS-YYYYMMDD-XXXX
     * Tối ưu: Sử dụng native query để tìm max sequence thay vì load tất cả orders
     */
    private String generatePOSOrderNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "POS-" + datePrefix + "-%";

        // Tối ưu: Query trực tiếp max sequence từ database
        Integer nextSequence = orderRepository.getNextOrderSequence(prefix);
        if (nextSequence == null) {
            nextSequence = 1;
        }

        return "POS-" + datePrefix + "-" + String.format("%04d", nextSequence);
    }

    /**
     * Tạo địa chỉ mặc định cho POS (địa chỉ cửa hàng)
     * Vì POS order là bán tại cửa hàng, không cần địa chỉ giao hàng thực sự
     * Nhưng database yêu cầu address_shipping_id không được NULL
     * Tối ưu: Tái sử dụng địa chỉ POS chung thay vì tạo mới mỗi lần
     * Lưu ý: address_type chỉ cho phép 'home', 'office', 'other' - dùng 'other' cho
     * POS
     */
    private Address createPOSDefaultAddress(User user) {
        // Tìm địa chỉ POS đã tồn tại (dùng chung cho tất cả POS orders)
        // Địa chỉ POS có line1 cố định "Cửa hàng Sneakery" và address_type = "other"
        String posLine1 = "Cửa hàng Sneakery";
        String posAddressType = "other";
        Optional<Address> existingPosAddress = addressRepository.findByLine1AndAddressType(posLine1, posAddressType);

        if (existingPosAddress.isPresent()) {
            // Tái sử dụng địa chỉ POS đã có
            return existingPosAddress.get();
        }

        // Tạo địa chỉ POS mới nếu chưa có
        // Lưu ý: Address vẫn cần user_id (không thể NULL), nhưng Order có thể NULL
        // user_id
        // Với POS address, ta cần gán cho một user (có thể dùng user đầu tiên hoặc user
        // đặc biệt)
        Address posAddress = new Address();
        // Nếu user null (khách vãng lai), tìm user đầu tiên để gán cho POS address
        // Vì Address table vẫn yêu cầu user_id NOT NULL
        if (user == null) {
            // Tìm user đầu tiên để gán cho POS address
            // Hoặc có thể tạo một user đặc biệt cho POS trong tương lai
            user = userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Không tìm thấy user nào trong hệ thống"));
        }
        posAddress.setUser(user);

        posAddress.setRecipientName(user.getFullName() != null ? user.getFullName() : "Khách hàng");
        posAddress.setPhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "0900000000");
        posAddress.setLine1(posLine1);
        posAddress.setLine2("Bán tại quầy");
        posAddress.setCity("Hà Nội");
        posAddress.setDistrict("Quận Hoàn Kiếm");
        posAddress.setWard("Phường Tràng Tiền");
        posAddress.setPostalCode("100000");
        posAddress.setAddressType("other"); // Dùng 'other' vì CHECK constraint chỉ cho phép 'home', 'office', 'other'
        posAddress.setIsDefault(false);
        posAddress.setCreatedAt(LocalDateTime.now());
        posAddress.setUpdatedAt(LocalDateTime.now());

        // Lưu địa chỉ vào database
        return addressRepository.save(posAddress);
    }

    /**
     * Map payment method từ frontend sang giá trị hợp lệ trong database
     * Database cho phép: 'cod', 'vnpay', 'momo', 'zalopay', 'bank_transfer',
     * 'credit_card'
     * Frontend có thể gửi: 'cash', 'card', 'bank', 'online'
     */
    private String mapPaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            return "cod"; // Mặc định là COD (Cash on Delivery)
        }

        String method = paymentMethod.toLowerCase().trim();

        // Map các giá trị từ frontend sang database
        switch (method) {
            case "cash":
                return "cod"; // Cash on Delivery
            case "card":
                return "credit_card";
            case "bank":
                return "bank_transfer";
            case "online":
                return "vnpay"; // Mặc định online là VNPay
            case "cod":
            case "vnpay":
            case "momo":
            case "zalopay":
            case "bank_transfer":
            case "credit_card":
                // Giá trị đã hợp lệ, trả về trực tiếp
                return method;
            default:
                log.warn("⚠️ Unknown payment method: {}. Defaulting to 'cod'", paymentMethod);
                return "cod"; // Mặc định là COD nếu không nhận diện được
        }
    }

    /**
     * Convert Order to OrderDto (cho POS)
     */
    private OrderDto convertToOrderDto(Order order) {

        // ================================
        // 1) Convert OrderDetails -> CartItemDto
        // ================================
        List<CartItemDto> detailDtos = order.getOrderDetails().stream()
                .map(detail -> {

                    ProductVariant v = detail.getVariant();

                    String sku = (detail.getVariantSku() != null && !detail.getVariantSku().isEmpty())
                            ? detail.getVariantSku()
                            : (v != null ? v.getSku() : "");

                    return CartItemDto.builder()
                            .variantId(v != null ? v.getId() : null)
                            .productName(detail.getProductName())                 // dùng denormalized name
                            .sku(sku)
                            .brandName(v != null && v.getProduct() != null && v.getProduct().getBrand() != null
                                    ? v.getProduct().getBrand().getName()
                                    : "N/A")
                            .size(detail.getSize())
                            .color(detail.getColor())
                            .quantity(detail.getQuantity())
                            .unitPrice(detail.getUnitPrice())
                            .totalPrice(detail.getUnitPrice()
                                    .multiply(BigDecimal.valueOf(detail.getQuantity())))
                            .build();
                })
                .collect(Collectors.toList());


        // ================================
        // 2) Convert Payment
        // ================================
        Payment p = order.getPayments().stream().findFirst().orElse(null);

        PaymentDto paymentDto = (p == null) ? null : PaymentDto.builder()
                .id(p.getId())
                .paymentMethod(p.getPaymentMethod())
                .status(p.getStatus())
                .amount(p.getAmount())
                .paidAt(p.getPaidAt())
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .build();


        // ================================
        // 3) Coupon code
        // ================================
        String couponCode = (order.getCoupon() != null) ? order.getCoupon().getCode() : null;


        // ================================
        // 4) Loyalty points: pointsUsed & pointsDiscount (không có trong DB)
        // ================================
        Integer pointsUsed = order.getPointsUsed() != null ? order.getPointsUsed() : 0;

        BigDecimal pointsDiscount = BigDecimal.valueOf(pointsUsed)
                .multiply(BigDecimal.valueOf(1000)); // 1 point = 1000 VND


        // ================================
        // 5) Taxable Amount (subtotal - discount - pointsDiscount)
        // ================================
        BigDecimal subtotal = order.getSubtotal() != null ? order.getSubtotal() : BigDecimal.ZERO;
        BigDecimal discountAmount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;

        BigDecimal taxableAmount = subtotal
                .subtract(discountAmount)
                .subtract(pointsDiscount);

        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }


        // ================================
        // 6) Tax amount
        // ================================
        BigDecimal taxAmount = order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO;


        // ================================
        // 7) Build OrderDto
        // ================================
        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .couponCode(couponCode)

                // ⭐ Các field bổ sung cho Client A
                .pointsUsed(pointsUsed)
                .pointsDiscount(pointsDiscount)
                .taxableAmount(taxableAmount)
                .taxAmount(taxAmount)

                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .addressShipping(convertToAddressDto(order.getAddressShipping()))
                .payment(paymentDto)
                .orderDetails(detailDtos)
                .statusHistories(order.getStatusHistories().stream()
                        .map(h -> OrderStatusHistoryDto.builder()
                                .id(h.getId())
                                .status(h.getStatus())
                                .changedAt(h.getChangedAt())
                                .build())
                        .collect(Collectors.toList()))
                .returnRequest(null) // nếu có return request bạn tự fill vào
                .build();
    }

    /**
     * Lấy danh sách POS orders với pagination
     */
    @Transactional(readOnly = true)
    public Page<OrderDto> getPOSOrders(Pageable pageable) {
        log.info("📋 Fetching POS orders - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        // Lấy POS orders (orderNumber bắt đầu bằng "POS-")
        Page<Order> orderPage = orderRepository.findPOSOrders(null, null, pageable);

        return orderPage.map(this::convertToOrderDto);
    }

    /**
     * Helper: Lấy giá hiệu quả từ variant (priceSale nếu có, nếu không thì
     * priceBase)
     */
    private BigDecimal getEffectivePrice(ProductVariant variant) {
        if (variant.getPriceSale() != null && variant.getPriceSale().compareTo(BigDecimal.ZERO) > 0) {
            return variant.getPriceSale();
        }
        return variant.getPriceBase();
    }

    /**
     * Helper: Format currency
     */
    private String formatCurrency(BigDecimal amount) {
        return new java.text.DecimalFormat("#,###").format(amount) + " ₫";
    }

    @Transactional
    public AdminConfirmPaymentResponseDto confirmPaymentByAdmin(Long orderId, AdminConfirmPaymentRequestDto request) {

        if (request == null || request.getPaymentStatus() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Thiếu paymentStatus");
        }

        String target = request.getPaymentStatus().trim().toLowerCase();
        if (!"completed".equals(target)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ xác nhận thanh toán với trạng thái 'completed'");
        }

        // 1) Load order cơ bản (managed)
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        // 2) Load payments (fetch) để đảm bảo có payments trong transaction
        Order orderWithPayments = orderRepository.findByIdWithPayments(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng (payments)"));

        if (orderWithPayments.getPayments() == null || orderWithPayments.getPayments().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin thanh toán của đơn hàng");
        }

        Payment payment = orderWithPayments.getPayments().get(0);

        // Optional guard: chặn đơn cancelled/refunded (tuỳ bạn)
        String st = order.getStatus() != null ? order.getStatus().toLowerCase() : "";
        if ("cancelled".equals(st) || "refunded".equals(st)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Không thể xác nhận thanh toán cho đơn đã hủy/hoàn tiền. Trạng thái hiện tại: " + order.getStatus());
        }

        // 3) Update payment
        String currentPayStatus = payment.getStatus() != null ? payment.getStatus().toLowerCase() : "";
        if (!"completed".equals(currentPayStatus)) {
            payment.setStatus("completed");
            if (payment.getPaidAt() == null) {
                payment.setPaidAt(LocalDateTime.now());
            }
        }

        // 4) Ghi audit vào StatusHistory.note nhưng KHÔNG đổi order.status
        // Load histories fetch để tránh lazy
        Order orderWithHistories = orderRepository.findByIdWithStatusHistories(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng (histories)"));

        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);                    // giữ order managed
        history.setStatus(order.getStatus());       // giữ status hiện tại
        history.setChangedAt(LocalDateTime.now());

        String note = "Admin xác nhận đã thanh toán";
        if (request.getAdminNote() != null && !request.getAdminNote().trim().isEmpty()) {
            note += " - " + request.getAdminNote().trim();
        }
        history.setNote(note);

        statusHistoryRepository.save(history);

        if (orderWithHistories.getStatusHistories() != null) {
            orderWithHistories.getStatusHistories().add(history);
        }

        // 5) Save (nếu Payment mapping cascade thì save order đủ; nếu không cascade, vẫn OK vì payment managed)
        orderRepository.save(order);

        return AdminConfirmPaymentResponseDto.builder()
                .orderId(order.getId())
                .paymentStatus(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }

}