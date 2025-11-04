package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.*;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
// SỬA LỖI: Đảm bảo import DÒNG NÀY
import org.springframework.data.domain.Pageable; 
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// XÓA DÒNG "import java.awt.print.Pageable;" NẾU BẠN THẤY NÓ
// import java.awt.print.Pageable; // <-- XÓA DÒNG NÀY

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    @Transactional(readOnly = true)
    public Page<AdminOrderListDto> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllWithUser(pageable);
        return orderPage.map(this::convertToOrderListDto);
    }

    /**
     * Lấy danh sách đơn hàng với search và filter
     */
    @Transactional(readOnly = true)
    public Page<AdminOrderListDto> getAllOrdersWithFilters(String search, String status, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllWithUserAndFilters(search, status, pageable);
        return orderPage.map(this::convertToOrderListDto);
    }

    // ... (Giữ nguyên các hàm còn lại: getOrderById, updateOrderStatus, và các hàm helper)
    
    @Transactional(readOnly = true)
    public AdminOrderDetailDto getOrderById(Long orderId) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));
        return convertToOrderDetailDto(order);
    }

    @Transactional
    public AdminOrderDetailDto updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        order.setStatus(newStatus);
        
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus);
        history.setChangedAt(LocalDateTime.now());
        
        statusHistoryRepository.save(history); 
        Order savedOrder = orderRepository.save(order); 
        
        return getOrderById(savedOrder.getId());
    }

    private AdminOrderListDto convertToOrderListDto(Order order) {
        return AdminOrderListDto.builder()
                .id(order.getId())
                .customerName(order.getUser() != null ? order.getUser().getFullName() : "Guest")
                .customerEmail(order.getUser() != null ? order.getUser().getEmail() : "N/A")
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
    
    private AdminOrderDetailDto convertToOrderDetailDto(Order order) {
        List<CartItemDto> detailDtos = order.getOrderDetails().stream().map(detail -> {
            var v = detail.getVariant();
            return CartItemDto.builder()
                    .variantId(v.getId())
                    .productName(v.getProduct().getName())
                    .brandName(v.getProduct().getBrand().getName())
                    .size(v.getSize())
                    .color(v.getColor())
                    .imageUrl(v.getImageUrl())
                    .quantity(detail.getQuantity())
                    .unitPrice(detail.getUnitPrice())
                    .totalPrice(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

        Payment p = order.getPayments().stream().findFirst().orElse(null);
        PaymentDto paymentDto = (p == null) ? null : PaymentDto.builder()
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

        return AdminOrderDetailDto.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .customerName(order.getUser() != null ? order.getUser().getFullName() : "Guest")
                .customerEmail(order.getUser() != null ? order.getUser().getEmail() : "N/A")
                .addressShipping(convertToAddressDto(order.getAddressShipping()))
                .addressBilling(convertToAddressDto(order.getAddressBilling()))
                .payment(paymentDto)
                .orderDetails(detailDtos)
                .statusHistories(historyDtos)
                .build();
    }
    
    private AddressDto convertToAddressDto(Address address) {
        if (address == null) return null;
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
        
        // 1. Generate order number: POS-YYYYMMDD-XXXX
        String orderNumber = generatePOSOrderNumber();
        
        // 2. Lấy User nếu có customerId
        User user = null;
        if (requestDto.getCustomerId() != null) {
            user = userRepository.findById(requestDto.getCustomerId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));
        }
        
        // 3. Tạo địa chỉ mặc định cho POS (hoặc null nếu không cần)
        Address posAddress = createPOSDefaultAddress(user);
        
        // 4. Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(orderNumber);
        order.setAddressShipping(posAddress);
        order.setAddressBilling(posAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("delivered"); // POS orders bán tại quầy, trạng thái delivered ngay
        order.setShippingFee(BigDecimal.ZERO); // POS không có phí ship
        order.setSubtotal(BigDecimal.ZERO); // Sẽ tính sau
        
        // 5. Xử lý items và tính subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (POSOrderItemDto itemDto : requestDto.getItems()) {
            ProductVariant variant;
            
            // Lấy variant
            if (itemDto.getVariantId() != null) {
                variant = variantRepository.findByIdWithDetails(itemDto.getVariantId())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 
                                "Không tìm thấy variant với ID: " + itemDto.getVariantId()));
            } else {
                // Nếu không có variantId, lấy variant đầu tiên của product
                variant = variantRepository.findWithFilters(
                        null, null, null, itemDto.getProductId(), null, 
                        org.springframework.data.domain.PageRequest.of(0, 1)
                ).getContent().stream().findFirst()
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, 
                                "Không tìm thấy variant cho product ID: " + itemDto.getProductId()));
            }
            
            // Kiểm tra tồn kho
            if (variant.getStockQuantity() == null || variant.getStockQuantity() < itemDto.getQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 
                        String.format("Sản phẩm %s (Size: %s, Color: %s) không đủ hàng. Tồn kho: %d, Yêu cầu: %d",
                                variant.getProduct().getName(),
                                variant.getSize(),
                                variant.getColor(),
                                variant.getStockQuantity() != null ? variant.getStockQuantity() : 0,
                                itemDto.getQuantity()));
            }
            
            // Giảm tồn kho
            int newStock = variant.getStockQuantity() - itemDto.getQuantity();
            variant.setStockQuantity(newStock);
            variantRepository.save(variant);
            
            // Lấy giá từ database (effective price: priceSale nếu có, nếu không thì priceBase)
            BigDecimal effectivePrice = getEffectivePrice(variant);
            
            // Validate giá: Nếu giá từ frontend khác với giá database, log warning và dùng giá từ database
            if (itemDto.getUnitPrice().compareTo(effectivePrice) != 0) {
                log.warn("⚠️ Price mismatch for variant {}: Frontend sent {}, Database has {}. Using database price.",
                        variant.getId(), itemDto.getUnitPrice(), effectivePrice);
            }
            
            // Tạo OrderDetail - LUÔN dùng giá từ database để đảm bảo tính nhất quán
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(itemDto.getQuantity());
            detail.setUnitPrice(effectivePrice); // Dùng giá từ database, không tin tưởng frontend
            
            // Set các trường denormalized (lưu lại thông tin tại thời điểm mua hàng)
            detail.setProductName(variant.getProduct().getName());
            detail.setVariantSku(variant.getSku() != null ? variant.getSku() : "");
            detail.setSize(variant.getSize() != null ? variant.getSize() : "");
            detail.setColor(variant.getColor() != null ? variant.getColor() : "");
            
            // Tính total_price
            BigDecimal totalPrice = effectivePrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            detail.setTotalPrice(totalPrice);
            
            order.getOrderDetails().add(detail);
            subtotal = subtotal.add(totalPrice);
        }
        
        order.setSubtotal(subtotal);
        
        // 6. Xử lý coupon nếu có
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;
        if (requestDto.getDiscountCode() != null && !requestDto.getDiscountCode().trim().isEmpty()) {
            try {
                CouponDto couponDto = couponService.validateCouponCode(requestDto.getDiscountCode());
                coupon = couponRepository.findById(couponDto.getId()).orElse(null);
                
                if (coupon != null) {
                    // Tính discount amount
                    if ("percent".equalsIgnoreCase(coupon.getDiscountType())) {
                        BigDecimal discount = subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100));
                        if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                            discount = coupon.getMaxDiscountAmount();
                        }
                        discountAmount = discount;
                    } else if ("fixed".equalsIgnoreCase(coupon.getDiscountType())) {
                        discountAmount = coupon.getValue();
                    }
                    
                    // Kiểm tra minOrderAmount
                    if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, 
                                String.format("Đơn hàng tối thiểu %s để áp dụng mã giảm giá", 
                                        formatCurrency(coupon.getMinOrderAmount())));
                    }
                    
                    // Cập nhật usesCount
                    if (coupon.getUsesCount() == null) {
                        coupon.setUsesCount(0);
                    }
                    coupon.setUsesCount(coupon.getUsesCount() + 1);
                    couponRepository.save(coupon);
                    
                    order.setCoupon(coupon);
                }
            } catch (ApiException e) {
                // Re-throw ApiException để frontend xử lý
                throw e;
            } catch (Exception e) {
                // Log các exception khác và tiếp tục mà không áp dụng coupon
                log.warn("Error applying coupon: {}", e.getMessage());
            }
        }
        
        // Nếu có discountAmount từ request, dùng nó (đã validate ở frontend)
        if (requestDto.getDiscountAmount() != null && requestDto.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = requestDto.getDiscountAmount();
        }
        
        order.setDiscountAmount(discountAmount);
        
        // 7. Tính totalAmount
        BigDecimal totalAmount = subtotal.subtract(discountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }
        order.setTotalAmount(totalAmount);
        
        // 8. Tạo Payment với status "completed" (đã thanh toán tại quầy)
        // Map payment method từ frontend sang giá trị hợp lệ trong database
        String paymentMethod = mapPaymentMethod(requestDto.getPaymentMethod());
        
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("completed");
        payment.setPaidAt(LocalDateTime.now());
        order.getPayments().add(payment);
        
        // 9. Tạo OrderStatusHistory
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("delivered"); // POS orders bán tại quầy, trạng thái delivered ngay
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistories().add(history);
        
        // 10. Lưu order
        Order savedOrder = orderRepository.save(order);
        log.info("✅ POS order created: {} - Total: {}", orderNumber, totalAmount);
        
        // 11. Tích điểm loyalty nếu có customerId
        if (user != null) {
            try {
                // Tích điểm từ đơn hàng POS (status = delivered)
                loyaltyService.earnPointsFromOrder(savedOrder);
                log.info("✅ Customer {} earned points from POS order {}", user.getId(), orderNumber);
            } catch (Exception e) {
                log.warn("Error awarding loyalty points: {}", e.getMessage());
                // Không throw exception để không làm fail order creation
            }
        }
        
        // 12. Convert to DTO
        return convertToOrderDto(savedOrder);
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
     * Lưu ý: address_type chỉ cho phép 'home', 'office', 'other' - dùng 'other' cho POS
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
        // Lưu ý: Database yêu cầu user_id NOT NULL, nhưng có thể là user khác nhau
        // Với POS, ta có thể tạo địa chỉ với user đầu tiên hoặc user null (nếu có)
        Address posAddress = new Address();
        posAddress.setUser(user); // Có thể null nếu là khách vãng lai, nhưng database yêu cầu NOT NULL
        // Nếu user null, cần tạo user giả hoặc dùng user nào đó
        if (user == null) {
            // Tìm user đầu tiên để gán cho POS address (vì user_id NOT NULL)
            // Hoặc có thể tạo một user đặc biệt cho POS
            user = userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, 
                            "Không tìm thấy user nào trong hệ thống"));
            posAddress.setUser(user);
        }
        
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
     * Database cho phép: 'cod', 'vnpay', 'momo', 'zalopay', 'bank_transfer', 'credit_card'
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
        List<CartItemDto> detailDtos = order.getOrderDetails().stream().map(detail -> {
            var v = detail.getVariant();
            return CartItemDto.builder()
                    .variantId(v.getId())
                    .productName(v.getProduct().getName())
                    .brandName(v.getProduct().getBrand().getName())
                    .size(v.getSize())
                    .color(v.getColor())
                    .imageUrl(v.getImageUrl())
                    .quantity(detail.getQuantity())
                    .unitPrice(detail.getUnitPrice())
                    .totalPrice(detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

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

        // Lấy coupon code nếu có
        String couponCode = null;
        if (order.getCoupon() != null) {
            couponCode = order.getCoupon().getCode();
        }

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO)
                .couponCode(couponCode)
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .addressShipping(convertToAddressDto(order.getAddressShipping()))
                .payment(paymentDto)
                .orderDetails(detailDtos)
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
     * Helper: Lấy giá hiệu quả từ variant (priceSale nếu có, nếu không thì priceBase)
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
}