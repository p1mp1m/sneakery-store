package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.*;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PaymentGatewayService paymentGatewayService;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    // Note: LoyaltyService sẽ được dùng khi tích điểm sau khi đơn hàng hoàn thành (delivered)
    // private final LoyaltyService loyaltyService;

    /**
     * API 1: Xử lý Checkout (Tạo đơn hàng)
     */
    @Transactional
    public OrderDto createOrderFromCart(Long userId, CheckoutRequestDto requestDto) {
        
        // 1. Lấy giỏ hàng (đã tối ưu)
        Cart cart = cartRepository.findByUserIdWithDetails(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"));
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Giỏ hàng trống!");
        }

        // 2. Lấy User
        User user = userRepository.findById(userId).get();

        // 3. Lấy địa chỉ giao hàng (và kiểm tra)
        Address shippingAddress = addressRepository.findByIdAndUserId(requestDto.getAddressShippingId(), userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Địa chỉ giao hàng không hợp lệ"));
        
        // 4. Lấy địa chỉ thanh toán
        Address billingAddress = requestDto.getAddressBillingId() != null ?
                addressRepository.findByIdAndUserId(requestDto.getAddressBillingId(), userId)
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Địa chỉ thanh toán không hợp lệ"))
                : shippingAddress;

        // 5. Generate order number
        String orderNumber = generateOrderNumber();
        
        // 6. Tạo đơn hàng (Order)
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(orderNumber);
        order.setAddressShipping(shippingAddress);
        order.setAddressBilling(billingAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("Pending"); // Trạng thái ban đầu

        // 7. Tính tổng tiền VÀ chuyển CartItem -> OrderDetail
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            
            // 7.1. Kiểm tra tồn kho (quan trọng)
            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Sản phẩm " + variant.getProduct().getName() + " không đủ hàng");
            }
            
            // 7.2. Giảm tồn kho
            int quantityBefore = variant.getStockQuantity();
            int quantityChange = -cartItem.getQuantity();
            variant.setStockQuantity(quantityBefore + quantityChange);
            variantRepository.save(variant);
            
            // Note: Inventory log sẽ được tạo tự động bởi trigger trg_ProductVariants_InventoryLog
            // khi stock_quantity thay đổi. Chúng ta chỉ cần đảm bảo stock được cập nhật đúng.

            // 7.3. Tạo OrderDetail (chốt giá)
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(cartItem.getQuantity());
            BigDecimal effectivePrice = getEffectivePrice(variant);
            detail.setUnitPrice(effectivePrice);
            
            order.getOrderDetails().add(detail);
            totalAmount = totalAmount.add(effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        
        // Set subtotal
        BigDecimal subtotal = totalAmount;
        order.setSubtotal(subtotal);
        
        // 8. Xử lý coupon nếu có
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;
        if (requestDto.getCouponCode() != null && !requestDto.getCouponCode().trim().isEmpty()) {
            try {
                CouponDto couponDto = couponService.validateCouponCode(requestDto.getCouponCode());
                coupon = couponRepository.findById(couponDto.getId()).orElse(null);
                
                if (coupon != null) {
                    // Tính discount amount
                    if ("percent".equalsIgnoreCase(coupon.getDiscountType())) {
                        BigDecimal discount = subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                        if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                            discount = coupon.getMaxDiscountAmount();
                        }
                        discountAmount = discount;
                    } else if ("fixed".equalsIgnoreCase(coupon.getDiscountType())) {
                        discountAmount = coupon.getValue();
                        // Đảm bảo không giảm nhiều hơn subtotal
                        if (discountAmount.compareTo(subtotal) > 0) {
                            discountAmount = subtotal;
                        }
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
                throw e;
            } catch (Exception e) {
                log.warn("Error applying coupon: {}", e.getMessage());
                // Nếu có lỗi với coupon, tiếp tục mà không áp dụng coupon
            }
        }
        
        order.setDiscountAmount(discountAmount);
        
        // 9. Tính shipping fee dựa trên địa chỉ giao hàng
        BigDecimal shippingFee = calculateShippingFee(shippingAddress);
        order.setShippingFee(shippingFee);
        
        // 10. Tính tax amount (VAT 10% trên subtotal sau discount)
        BigDecimal amountAfterDiscount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = amountAfterDiscount.multiply(BigDecimal.valueOf(0.10))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        order.setTaxAmount(taxAmount);
        
        // 11. Tính total amount
        BigDecimal finalTotal = amountAfterDiscount.add(shippingFee).add(taxAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }
        order.setTotalAmount(finalTotal);

        // 12. Tạo Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(finalTotal);
        payment.setPaymentMethod(requestDto.getPaymentMethod());
        payment.setStatus("pending"); // Chờ thanh toán
        order.getPayments().add(payment);

        // 13. Tạo Lịch sử Status
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("Pending");
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        
        // Note: Inventory logs được tạo tự động bởi database trigger khi stock_quantity thay đổi
        // Trigger sẽ tự động log mọi thay đổi inventory
        
        cartRepository.delete(cart);

        String paymentUrl = null;
        if ("online".equalsIgnoreCase(requestDto.getPaymentMethod())) {
            paymentUrl = paymentGatewayService.createVNPayPaymentUrl(savedOrder.getId(), finalTotal, "Thanh toan don hang " + savedOrder.getOrderNumber());
        }
        
        try {
            emailService.sendOrderConfirmation(savedOrder);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email for order {}: {}", 
                    savedOrder.getOrderNumber(), e.getMessage(), e);
        }
        
        return convertToOrderDto(savedOrder, paymentUrl);
    }

    /**
     * API Lấy danh sách (tóm tắt) đơn hàng
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryDto> getMyOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return orders.stream().distinct()
                .map(this::convertToOrderSummaryDto)
                .collect(Collectors.toList());
    }

    /**
     * API Lấy chi tiết 1 đơn hàng
     */
    @Transactional(readOnly = true)
    public OrderDto getMyOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        String paymentUrl = null;
        Payment payment = order.getPayments().stream().findFirst().orElse(null);
        if (payment != null && "pending".equals(payment.getStatus()) && "online".equals(payment.getPaymentMethod())) {
            // Generate payment URL với orderNumber (nếu có) hoặc orderId
            if (order.getOrderNumber() != null) {
                paymentUrl = paymentGatewayService.createVNPayPaymentUrl(
                    order.getId(), 
                    payment.getAmount(), 
                    "Thanh toan don hang " + order.getOrderNumber()
                );
            } else {
                // Fallback nếu không có orderNumber
                paymentUrl = paymentGatewayService.createVNPayPaymentUrl(
                    order.getId(), 
                    payment.getAmount(), 
                    "Thanh toan don hang " + order.getId()
                );
            }
        }

        return convertToOrderDto(order, paymentUrl);
    }

    // =================================================================
    // HÀM HELPER (MAPPER)
    // =================================================================

    /**
     * Helper: Chuyển sang DTO tóm tắt
     */
    private OrderSummaryDto convertToOrderSummaryDto(Order order) {
        int totalItems = 0;
        if (order.getOrderDetails() != null) {
            totalItems = order.getOrderDetails().stream()
                    .mapToInt(OrderDetail::getQuantity)
                    .sum();
        }

        return OrderSummaryDto.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .totalItems(totalItems)
                .build();
    }
    
    /**
     * Helper: Chuyển Order Entity -> OrderDto (Chi tiết)
     */
    private OrderDto convertToOrderDto(Order order, String paymentUrl) {
        // Chuyển OrderDetail -> CartItemDto (dùng tạm DTO này)
        List<CartItemDto> detailDtos = order.getOrderDetails().stream().map(detail -> {
            ProductVariant v = detail.getVariant();
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

        // Lấy Payment DTO
        Payment firstPayment = order.getPayments().get(0);
        PaymentDto.PaymentDtoBuilder paymentDtoBuilder = PaymentDto.builder()
                .id(firstPayment.getId())
                .paymentMethod(firstPayment.getPaymentMethod())
                .status(firstPayment.getStatus())
                .amount(firstPayment.getAmount())
                .orderId(order.getId());
        
        if (paymentUrl != null) {
            paymentDtoBuilder.transactionId(paymentUrl);
        }
        
        PaymentDto paymentDto = paymentDtoBuilder.build();

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .addressShipping(convertToAddressDto(order.getAddressShipping()))
                .payment(paymentDto)
                .orderDetails(detailDtos)
                .build();
    }
    
    // Dùng lại hàm convertToDto của AddressService (nếu có)
    private AddressDto convertToAddressDto(Address address) {
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
    
    // Helper: Lấy giá cuối cùng (sale hoặc gốc)
    private BigDecimal getEffectivePrice(ProductVariant variant) {
        return (variant.getPriceSale() != null && variant.getPriceSale().compareTo(BigDecimal.ZERO) > 0)
                ? variant.getPriceSale()
                : variant.getPriceBase();
    }
    
    /**
     * Generate order number: ORD-YYYYMMDD-XXXX
     * Format: ORD-20250122-0001
     * Tối ưu: Sử dụng native query để tìm max sequence thay vì load tất cả orders
     */
    private String generateOrderNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "ORD-" + datePrefix + "-%";
        
        // Tối ưu: Query trực tiếp max sequence từ database
        Integer nextSequence = orderRepository.getNextOrderSequence(prefix);
        if (nextSequence == null) {
            nextSequence = 1;
        }
        
        return "ORD-" + datePrefix + "-" + String.format("%04d", nextSequence);
    }
    
    /**
     * Helper: Format currency
     */
    private String formatCurrency(BigDecimal amount) {
        return new java.text.DecimalFormat("#,###").format(amount) + " ₫";
    }
    
    /**
     * Tính shipping fee dựa trên địa chỉ giao hàng
     * Logic:
     * - Thành phố lớn (Hà Nội, TP.HCM, Đà Nẵng, Cần Thơ): 30,000 VND
     * - Tỉnh/thành phố khác: 50,000 VND
     * - Vùng xa (nếu có): 80,000 VND
     */
    private BigDecimal calculateShippingFee(Address address) {
        if (address == null || address.getCity() == null) {
            // Default shipping fee nếu không có địa chỉ
            return BigDecimal.valueOf(50000);
        }
        
        String city = address.getCity().toLowerCase().trim();
        
        // Danh sách thành phố lớn (nội thành - phí ship thấp hơn)
        String[] majorCities = {
            "hà nội", "hanoi", "ha noi",
            "tp. hồ chí minh", "tp hcm", "hồ chí minh", "ho chi minh", "hochiminh",
            "đà nẵng", "da nang", "danang",
            "cần thơ", "can tho", "cantho",
            "hải phòng", "hai phong", "haiphong"
        };
        
        // Kiểm tra xem có phải thành phố lớn không
        for (String majorCity : majorCities) {
            if (city.contains(majorCity) || majorCity.contains(city)) {
                return BigDecimal.valueOf(30000); // Phí ship nội thành
            }
        }
        
        // Các tỉnh/thành phố khác
        return BigDecimal.valueOf(50000); // Phí ship ngoại thành
    }
}