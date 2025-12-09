package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.*;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.*;
import com.sneakery.store.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cloudinary.utils.StringUtils.isBlank;

/**
 * Service xử lý đơn hàng cho User
 * 
 * <p>Service này cung cấp các chức năng quản lý đơn hàng cho user:
 * <ul>
 *   <li>Tạo đơn hàng từ giỏ hàng (checkout)</li>
 *   <li>Lấy danh sách đơn hàng của user</li>
 *   <li>Lấy thông tin chi tiết đơn hàng theo ID</li>
 * </ul>
 * 
 * <p><b>Về checkout:</b>
 * <ul>
 *   <li>Checkout sẽ tạo đơn hàng từ giỏ hàng hiện tại</li>
 *   <li>Sau khi checkout thành công, giỏ hàng sẽ được xóa</li>
 *   <li>Hệ thống sẽ tự động áp dụng coupon (nếu có) và tính điểm loyalty</li>
 *   <li>Gửi email xác nhận đơn hàng cho khách hàng</li>
 * </ul>
 * 
 * <p><b>Về thanh toán:</b>
 * <ul>
 *   <li>Hỗ trợ nhiều phương thức thanh toán: COD, Bank Transfer, Credit Card</li>
 *   <li>Nếu thanh toán online, sẽ tích hợp với Payment Gateway</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Tạo đơn hàng từ giỏ hàng
 * CheckoutRequestDto checkoutRequest = new CheckoutRequestDto();
 * checkoutRequest.setAddressId(1L);
 * checkoutRequest.setPaymentMethod("COD");
 * OrderDto order = orderService.createOrderFromCart(userId, checkoutRequest);
 * 
 * // Lấy danh sách đơn hàng
 * List&lt;OrderSummaryDto&gt; orders = orderService.getMyOrders(userId);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PaymentGatewayService paymentGatewayService;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    private final LoyaltyService loyaltyService;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final ShippingService shippingService;

    /**
     * Xử lý Checkout - Tạo đơn hàng từ giỏ hàng
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy giỏ hàng của user (với đầy đủ thông tin items)</li>
     *   <li>Validate giỏ hàng (không được trống)</li>
     *   <li>Lấy địa chỉ giao hàng</li>
     *   <li>Kiểm tra tồn kho của tất cả sản phẩm trong giỏ hàng</li>
     *   <li>Tính toán tổng tiền (bao gồm coupon nếu có)</li>
     *   <li>Tạo đơn hàng mới với các order items</li>
     *   <li>Xóa giỏ hàng sau khi tạo đơn hàng thành công</li>
     *   <li>Áp dụng coupon và tính điểm loyalty (nếu có)</li>
     *   <li>Gửi email xác nhận đơn hàng</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Giỏ hàng phải có ít nhất 1 sản phẩm</li>
     *   <li>Tất cả sản phẩm trong giỏ hàng phải còn tồn kho đủ</li>
     *   <li>Địa chỉ giao hàng phải tồn tại và thuộc về user</li>
     *   <li>Sau khi checkout thành công, giỏ hàng sẽ được xóa</li>
     *   <li>Nếu có coupon, sẽ tự động áp dụng và tính lại tổng tiền</li>
     *   <li>Nếu thanh toán online, sẽ tích hợp với Payment Gateway</li>
     * </ul>
     * 
     * @param userId ID của user đang checkout
     * @param requestDto DTO chứa thông tin checkout:
     *                   - addressId: ID địa chỉ giao hàng (bắt buộc)
     *                   - paymentMethod: Phương thức thanh toán (bắt buộc)
     *                   - couponCode: Mã coupon (tùy chọn)
     *                   - note: Ghi chú đơn hàng (tùy chọn)
     * @return OrderDto của đơn hàng vừa tạo
     * @throws ApiException nếu giỏ hàng trống, địa chỉ không tồn tại, hết tồn kho, hoặc validation thất bại
     * 
     * @example
     * <pre>
     * CheckoutRequestDto checkoutRequest = new CheckoutRequestDto();
     * checkoutRequest.setAddressId(1L);
     * checkoutRequest.setPaymentMethod("COD");
     * checkoutRequest.setCouponCode("SALE10"); // Tùy chọn
     * 
     * OrderDto order = orderService.createOrderFromCart(userId, checkoutRequest);
     * System.out.println(order.getOrderCode()); // Mã đơn hàng
     * System.out.println(order.getTotalAmount()); // Tổng tiền
     * </pre>
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
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User không tồn tại"));

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
            // Đối với online/offline orders: chỉ kiểm tra, KHÔNG trừ kho ngay
            // Kho sẽ được trừ khi order status = "Completed"
            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Sản phẩm " + variant.getProduct().getName() + " không đủ hàng");
            }
            
            // 7.2. KHÔNG trừ kho ở đây cho online/offline orders
            // Kho sẽ được trừ khi order status được cập nhật thành "Completed" trong AdminOrderService.updateOrderStatus
            // Note: POS orders sẽ trừ kho ngay khi tạo (xem AdminOrderService.createPOSOrder)

            // 7.3. Tạo OrderDetail (chốt giá)
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(cartItem.getQuantity());
            BigDecimal effectivePrice = getEffectivePrice(variant);
            detail.setUnitPrice(effectivePrice);
            
            // Set các trường denormalized (lưu lại thông tin tại thời điểm mua hàng)
            detail.setProductName(variant.getProduct().getName());
            detail.setVariantSku(variant.getSku() != null ? variant.getSku() : "");
            detail.setSize(variant.getSize() != null ? variant.getSize() : "");
            detail.setColor(variant.getColor() != null ? variant.getColor() : "");
            
            // Tính total_price
            BigDecimal totalPrice = effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            detail.setTotalPrice(totalPrice);
            
            order.getOrderDetails().add(detail);
            totalAmount = totalAmount.add(totalPrice);
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
                coupon = couponRepository.findById(Objects.requireNonNull(couponDto.getId())).orElse(null);
                
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

        // 9. amountAfterCoupon
        BigDecimal amountAfterCoupon = subtotal.subtract(discountAmount);

// 10. loyalty points BEFORE VAT (FE logic)
// --> Declare pointsUsed FIRST
        Integer pointsUsed = Optional.ofNullable(requestDto.getPointsUsed())
                .filter(p -> p > 0)
                .orElse(0);

        BigDecimal pointsDiscount = BigDecimal.ZERO;

        if (pointsUsed > 0) {
            int currentBalance = loyaltyService.getUserPointsBalance(userId);

            if (pointsUsed > currentBalance) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Không đủ điểm thưởng");
            }

            pointsDiscount = BigDecimal.valueOf(pointsUsed * 1000L);

            // Không cho vượt quá amountAfterCoupon
            if (pointsDiscount.compareTo(amountAfterCoupon) > 0) {
                pointsDiscount = amountAfterCoupon;
                pointsUsed = pointsDiscount
                        .divide(BigDecimal.valueOf(1000), 0, RoundingMode.DOWN)
                        .intValue();
            }

            order.setPointsUsed(pointsUsed);
        }

// 11. amountAfterDiscounts BEFORE VAT
        BigDecimal amountAfterDiscounts = amountAfterCoupon.subtract(pointsDiscount);

// 12. VAT 10%
        BigDecimal taxAmount = amountAfterDiscounts
                .multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);
        order.setTaxAmount(taxAmount);

// 13. Shipping fee
//        BigDecimal shippingFee = calculateShippingFee(shippingAddress);
//        order.setShippingFee(shippingFee);

// 14. Final Total
        BigDecimal finalTotal = amountAfterDiscounts
                .add(taxAmount)
                .add(shippingFee)
                .max(BigDecimal.ZERO);

        order.setTotalAmount(finalTotal);


        // 14. Set customer note nếu có
        if (requestDto.getCustomerNote() != null && !requestDto.getCustomerNote().trim().isEmpty()) {
            order.setCustomerNote(requestDto.getCustomerNote());
        }

        // 15. Tạo Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(finalTotal);
        payment.setPaymentMethod(requestDto.getPaymentMethod());
        payment.setStatus("pending"); // Chờ thanh toán
        order.getPayments().add(payment);

        // 16. Tạo Lịch sử Status
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("Pending");
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistories().add(history);

//        Order savedOrder = orderRepository.save(order);
        Order savedOrder = orderRepository.saveAndFlush(order);

        // 17. Redeem loyalty points sau khi order được lưu (có ID)
//        if (pointsUsed > 0) {
//            try {
//                loyaltyService.redeemPointsInNewTx(userId, pointsUsed, savedOrder);
//                log.info("✅ Redeemed {} points for order {}", pointsUsed, savedOrder.getId());
//            } catch (Exception e) {
//                log.error("Failed to redeem points for order {}: {}", savedOrder.getId(), e.getMessage(), e);
//                // Không throw error vì order đã được tạo, chỉ log
//            }
//        }
//
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
     * API 2: Xử lý Guest Checkout (Tạo đơn hàng từ guest cart)
     */
    @Transactional
    public OrderDto createGuestOrderFromCart(String sessionId, GuestCheckoutRequestDto requestDto) {
        
        // 1. Lấy guest cart
        Cart cart = cartRepository.findBySessionIdWithDetails(sessionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"));
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Giỏ hàng trống!");
        }

        // 2. Lấy hoặc tạo guest user (system account cho guest orders)
        User guestUser = getOrCreateGuestUser();

        // 3. Tạo Address từ thông tin guest
        Address shippingAddress = new Address();
        shippingAddress.setUser(guestUser);
        shippingAddress.setRecipientName(requestDto.getRecipientName());
        shippingAddress.setPhone(requestDto.getPhone());
        shippingAddress.setLine1(requestDto.getLine1());
        shippingAddress.setLine2(requestDto.getLine2());
        shippingAddress.setDistrict(requestDto.getDistrict());
        shippingAddress.setCity(requestDto.getCity());
        shippingAddress.setWard(requestDto.getWard());
        shippingAddress.setPostalCode(requestDto.getPostalCode());
        shippingAddress.setCreatedAt(LocalDateTime.now());
        shippingAddress = addressRepository.save(shippingAddress);

        // 4. Dùng địa chỉ giao hàng cho billing
        Address billingAddress = shippingAddress;

        // 5. Generate order number
        String orderNumber = generateOrderNumber();
        
        // 6. Tạo đơn hàng (Order)
        Order order = new Order();
        order.setUser(guestUser);
        order.setOrderNumber(orderNumber);
        order.setAddressShipping(shippingAddress);
        order.setAddressBilling(billingAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("Pending");

        // 7. Tính tổng tiền VÀ chuyển CartItem -> OrderDetail
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            
            // 7.1. Kiểm tra tồn kho (quan trọng)
            // Đối với guest/online/offline orders: chỉ kiểm tra, KHÔNG trừ kho ngay
            // Kho sẽ được trừ khi order status = "Completed"
            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 
                    "Sản phẩm " + variant.getProduct().getName() + " không đủ hàng");
            }
            
            // 7.2. KHÔNG trừ kho ở đây cho guest/online/offline orders
            // Kho sẽ được trừ khi order status được cập nhật thành "Completed" trong AdminOrderService.updateOrderStatus
            // Note: POS orders sẽ trừ kho ngay khi tạo (xem AdminOrderService.createPOSOrder)

            // 7.3. Tạo OrderDetail (chốt giá)
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(cartItem.getQuantity());
            BigDecimal effectivePrice = getEffectivePrice(variant);
            detail.setUnitPrice(effectivePrice);
            
            // Set các trường denormalized (lưu lại thông tin tại thời điểm mua hàng)
            detail.setProductName(variant.getProduct().getName());
            detail.setVariantSku(variant.getSku() != null ? variant.getSku() : "");
            detail.setSize(variant.getSize() != null ? variant.getSize() : "");
            detail.setColor(variant.getColor() != null ? variant.getColor() : "");
            
            // Tính total_price
            BigDecimal totalPrice = effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            detail.setTotalPrice(totalPrice);
            
            order.getOrderDetails().add(detail);
            totalAmount = totalAmount.add(totalPrice);
        }
        
        // Set subtotal
        BigDecimal subtotal = totalAmount;
        order.setSubtotal(subtotal);

        // -------------------------------------------------------------
// 🔥 NEW LOGIC – START (Guest FE-aligned calculation)
// -------------------------------------------------------------

// 8. Áp dụng coupon
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;

        if (requestDto.getCouponCode() != null && !requestDto.getCouponCode().trim().isEmpty()) {
            try {
                CouponDto couponDto = couponService.validateCouponCode(requestDto.getCouponCode());
                coupon = couponRepository.findById(couponDto.getId()).orElse(null);

                if (coupon != null) {
                    if ("percent".equalsIgnoreCase(coupon.getDiscountType())) {
                        BigDecimal discount = subtotal.multiply(coupon.getValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                        if (coupon.getMaxDiscountAmount() != null &&
                                discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                            discount = coupon.getMaxDiscountAmount();
                        }
                        discountAmount = discount;
                    } else if ("fixed".equalsIgnoreCase(coupon.getDiscountType())) {
                        discountAmount = coupon.getValue().min(subtotal);
                    }

                    if (coupon.getMinOrderAmount() != null &&
                            subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
                        throw new ApiException(HttpStatus.BAD_REQUEST,
                                "Đơn hàng tối thiểu " + formatCurrency(coupon.getMinOrderAmount())
                                        + " để áp dụng mã giảm giá");
                    }

                    coupon.setUsesCount(Optional.ofNullable(coupon.getUsesCount()).orElse(0) + 1);
                    couponRepository.save(coupon);

                    order.setCoupon(coupon);
                }
            } catch (Exception e) {
                log.warn("Coupon error: {}", e.getMessage());
            }
        }

        order.setDiscountAmount(discountAmount);

// 9. amountAfterCoupon
        BigDecimal amountAfterCoupon = subtotal.subtract(discountAmount);

// 10. Loyalty disabled for guest
        Integer pointsUsed = 0;
        BigDecimal pointsDiscount = BigDecimal.ZERO;
        order.setPointsUsed(0);

// 11. amountAfterDiscountsBeforeVAT
        BigDecimal amountAfterDiscounts = amountAfterCoupon;

// 12. TAX 10%
        BigDecimal taxAmount = amountAfterDiscounts.multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);
        order.setTaxAmount(taxAmount);

// 13. Shipping fee
        BigDecimal shippingFee = calculateShippingFee(shippingAddress);
        order.setShippingFee(shippingFee);

// 14. Final total
        BigDecimal finalTotal = amountAfterDiscounts.add(taxAmount).add(shippingFee);
        order.setTotalAmount(finalTotal.max(BigDecimal.ZERO));

        // 13. Tạo Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(finalTotal);
        payment.setPaymentMethod(requestDto.getPaymentMethod());
        payment.setStatus("pending");
        order.getPayments().add(payment);

        // 14. Tạo Lịch sử Status
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("Pending");
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        
        // 15. Xóa guest cart
        cartRepository.delete(cart);

        String paymentUrl = null;
        if ("online".equalsIgnoreCase(requestDto.getPaymentMethod())) {
            paymentUrl = paymentGatewayService.createVNPayPaymentUrl(savedOrder.getId(), finalTotal, 
                    "Thanh toan don hang " + savedOrder.getOrderNumber());
        }
        
        // 16. Gửi email xác nhận (nếu có email)
        if (requestDto.getEmail() != null && !requestDto.getEmail().trim().isEmpty()) {
            try {
                // Gửi email cho guest (có thể cần custom email service method)
                emailService.sendOrderConfirmation(savedOrder);
            } catch (Exception e) {
                log.error("Failed to send order confirmation email for guest order {}: {}", 
                        savedOrder.getOrderNumber(), e.getMessage(), e);
            }
        }
        
        return convertToOrderDto(savedOrder, paymentUrl);
    }

    /**
     * Helper: Lấy hoặc tạo guest user (system account cho guest orders)
     */
    private User getOrCreateGuestUser() {
        // Tìm user với email "guest@system.sneakery" hoặc tạo mới
        return userRepository.findByEmail("guest@system.sneakery").orElseGet(() -> {
            User guestUser = new User();
            guestUser.setEmail("guest@system.sneakery");
            guestUser.setPasswordHash("$2a$10$GUEST_USER_SYSTEM_ACCOUNT"); // Dummy password, không thể đăng nhập
            guestUser.setFullName("Khách vãng lai");
            guestUser.setRole("USER");
            guestUser.setIsActive(true);
            return userRepository.save(guestUser);
        });
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

    /**
     * Hủy đơn hàng (chỉ cho phép khi đơn hàng đang ở trạng thái "pending")
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Kiểm tra đơn hàng có thuộc về user hiện tại không</li>
     *   <li>Kiểm tra đơn hàng có đang ở trạng thái "pending" không</li>
     *   <li>Nếu có, cập nhật trạng thái đơn hàng thành "cancelled"</li>
     *   <li>Tạo OrderStatusHistory để ghi lại lịch sử</li>
     *   <li>Hoàn trả tồn kho cho các sản phẩm trong đơn hàng</li>
     *   <li>Trả về OrderDto sau khi hủy</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Chỉ cho phép hủy khi đơn hàng đang ở trạng thái "pending" (chờ xác nhận)</li>
     *   <li>Nếu đơn hàng đã được xác nhận hoặc đang xử lý, không cho phép hủy</li>
     *   <li>Sẽ hoàn trả tồn kho cho các sản phẩm trong đơn hàng</li>
     * </ul>
     * 
     * @param orderId ID của đơn hàng cần hủy
     * @param userId ID của user hiện tại
     * @return OrderDto của đơn hàng sau khi hủy
     * @throws ApiException nếu không tìm thấy đơn hàng, đơn hàng không thuộc về user, hoặc đơn hàng không thể hủy
     */
    @Transactional
    public OrderDto cancelOrder(Long orderId, Long userId) {
        // Load order với đầy đủ relationships
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        // Kiểm tra trạng thái đơn hàng - chỉ cho phép hủy khi status là "pending"
        String currentStatus = order.getStatus() != null ? order.getStatus().toLowerCase() : "";
        if (!"pending".equals(currentStatus)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                "Chỉ có thể hủy đơn hàng khi đơn hàng đang ở trạng thái 'Chờ xác nhận'. Trạng thái hiện tại: " + order.getStatus());
        }

        log.info("🔄 Cancelling order #{} for user {}", orderId, userId);

        // Hoàn trả tồn kho cho các sản phẩm trong đơn hàng
        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                ProductVariant variant = detail.getVariant();
                if (variant != null) {
                    int currentStock = variant.getStockQuantity();
                    int quantityToRestore = detail.getQuantity();
                    variant.setStockQuantity(currentStock + quantityToRestore);
                    variantRepository.save(variant);
                    log.info("✅ Restored stock for variant #{}: {} -> {}", 
                        variant.getId(), currentStock, currentStock + quantityToRestore);
                }
            }
        }

        // Cập nhật trạng thái đơn hàng thành "cancelled"
        order.setStatus("cancelled");

        // Tạo OrderStatusHistory để ghi lại lịch sử
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("cancelled");
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);
        order.getStatusHistories().add(history);

        // Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);
        log.info("✅ Order #{} cancelled successfully", orderId);

        // Convert và trả về OrderDto
        return convertToOrderDto(savedOrder, null);
    }

    /**
     * User xác nhận đã nhận hàng (Đơn hàng -> DELIVERED, Payment -> COMPLETED)
     *
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Kiểm tra đơn hàng có thuộc về user không</li>
     *   <li>Kiểm tra trạng thái hiện tại phải là "shipped"</li>
     *   <li>Cập nhật:
     *     <ul>
     *       <li>Order.status = "delivered"</li>
     *       <li>Payment.status = "completed"</li>
     *       <li>Payment.paidAt = now()</li>
     *     </ul>
     *   </li>
     *   <li>Trừ tồn kho thực tế</li>
     *   <li>Ghi OrderStatusHistory</li>
     * </ol>
     *
     * @param orderId ID đơn hàng
     * @param userId  ID user
     */
    @Transactional
    public void confirmOrderReceived(Long orderId, Long userId) {

        log.info("✅ User {} confirming received order #{}", userId, orderId);

        // 1. Lấy order + check quyền sở hữu
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        String currentStatus = order.getStatus() != null ? order.getStatus().toLowerCase() : "";

        // 2. Chỉ cho xác nhận khi đang shipped
        if (!"shipped".equals(currentStatus)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Chỉ có thể xác nhận khi đơn hàng đang giao. Trạng thái hiện tại: " + order.getStatus());
        }

        // 3. Update PAYMENT first
        if (order.getPayments() == null || order.getPayments().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Không tìm thấy thông tin thanh toán của đơn hàng");
        }

        Payment payment = order.getPayments().get(0);

        if (!"completed".equalsIgnoreCase(payment.getStatus())) {
            payment.setStatus("completed");
            payment.setPaidAt(LocalDateTime.now());
            log.info("💰 Payment for order #{} has been COMPLETED", orderId);
        }

        // 4. Trừ tồn kho thực tế (theo đúng rule bạn ghi chú)
        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                ProductVariant variant = detail.getVariant();

                if (variant != null) {
                    int currentStock = variant.getStockQuantity();
                    int quantityToReduce = detail.getQuantity();

                    if (currentStock < quantityToReduce) {
                        throw new ApiException(HttpStatus.BAD_REQUEST,
                                "Không đủ tồn kho cho sản phẩm: " + variant.getProduct().getName());
                    }

                    variant.setStockQuantity(currentStock - quantityToReduce);
                    variantRepository.save(variant);

                    log.info("📦 Reduced stock for variant #{}: {} -> {}",
                            variant.getId(), currentStock, (currentStock - quantityToReduce));
                }
            }
        }

        // 5. Cập nhật trạng thái order
        order.setStatus("delivered");
        orderRepository.save(order);

        // 6. Ghi lịch sử trạng thái
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("delivered");
        history.setChangedAt(LocalDateTime.now());

        statusHistoryRepository.save(history);
        order.getStatusHistories().add(history);

        log.info("✅ Order #{} is now DELIVERED and PAYMENT COMPLETED", orderId);

        // 7. Cộng điểm loyalty cho đơn hàng đã hoàn thành
        try {
            loyaltyService.earnPointsFromOrder(order);
            log.info("🎁 Loyalty points have been added for order #{}", orderId);
        } catch (Exception e) {
            log.warn("⚠️ Cannot earn loyalty points for order #{}: {}", orderId, e.getMessage());
        }
    }


    /**
     * Tạo yêu cầu đổi trả cho đơn hàng
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Kiểm tra đơn hàng có thuộc về user không</li>
     *   <li>Kiểm tra đơn hàng đã hoàn thành (delivered) chưa</li>
     *   <li>Kiểm tra đơn hàng đã có return request chưa</li>
     *   <li>Tạo ReturnRequest mới với status = "pending"</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Chỉ cho phép tạo return request khi đơn hàng đã hoàn thành (delivered)</li>
     *   <li>Mỗi đơn hàng chỉ có thể có 1 return request</li>
     *   <li>Return request sẽ có status = "pending" khi tạo</li>
     * </ul>
     * 
     * @param orderId ID của đơn hàng
     * @param userId ID của user hiện tại
     * @param requestDto DTO chứa thông tin return request (reason, note, images)
     * @return ReturnRequestDto của return request vừa tạo
     * @throws ApiException nếu không tìm thấy đơn hàng, đơn hàng không thuộc về user, đơn hàng chưa hoàn thành, hoặc đơn hàng đã có return request
     */
    @Transactional
    public ReturnRequestDto createReturnRequest(Long orderId, Long userId, CreateReturnRequestDto requestDto) {
        log.info("📦 Creating return request for order #{} by user {}", orderId, userId);
        
        // 1. Kiểm tra đơn hàng có thuộc về user không
        Order order = orderRepository.findByIdAndUserIdWithDetails(orderId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));
        
        // 2. Kiểm tra đơn hàng đã hoàn thành (delivered) chưa
        String currentStatus = order.getStatus() != null ? order.getStatus().toLowerCase() : "";
        if (!"delivered".equals(currentStatus)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                "Chỉ có thể tạo yêu cầu đổi trả khi đơn hàng đã hoàn thành. Trạng thái hiện tại: " + order.getStatus());
        }
        
        // 3. Kiểm tra đơn hàng đã có return request chưa
        if (returnRequestRepository.existsByOrderId(orderId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                "Đơn hàng này đã có yêu cầu đổi trả. Vui lòng kiểm tra lại.");
        }

        // 4. Lấy user
        @SuppressWarnings("null")
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy user"));
        
        // 5. Tạo ReturnRequest
        // Combine reason and note if note exists
        String reason = requestDto.getReason();
        if (requestDto.getNote() != null && !requestDto.getNote().trim().isEmpty()) {
            reason = reason + "\n\nGhi chú: " + requestDto.getNote();
        }

        if (isBlank(requestDto.getBankName()) ||
                isBlank(requestDto.getBankAccountNumber()) ||
                isBlank(requestDto.getBankAccountHolder())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Vui lòng cung cấp đầy đủ thông tin tài khoản để hoàn tiền");
        }
        
        ReturnRequest returnRequest = ReturnRequest.builder()
                .order(order)
                .user(user)
                .reason(reason)
                .status("pending") // Mặc định là pending
                .imagesJson(requestDto.getImages() != null && !requestDto.getImages().isEmpty() 
                    ? JsonUtil.stringListToJson(requestDto.getImages()) 
                    : null) // Convert images list to JSON, or null if empty
                .returnMethod("refund")
                .bankName(requestDto.getBankName())
                .bankAccountNumber(requestDto.getBankAccountNumber())
                .bankAccountHolder(requestDto.getBankAccountHolder())
                .adminNote(null) // Admin note sẽ được set sau khi admin duyệt
                .build();
        // 🔥 6. CẬP NHẬT ORDER STATUS
        order.setStatus("return_pending");
        orderRepository.save(order);

        // 🔥 7. GHI LỊCH SỬ TRẠNG THÁI
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("return_pending");
        history.setChangedAt(LocalDateTime.now());
        statusHistoryRepository.save(history);
        order.getStatusHistories().add(history);
        // 6. Lưu ReturnRequest
        @SuppressWarnings("null")
        ReturnRequest savedReturnRequest = returnRequestRepository.save(returnRequest);
        log.info("✅ Return request #{} created successfully for order #{}", savedReturnRequest.getId(), orderId);
        
        // 7. Convert và trả về ReturnRequestDto
        return convertToReturnRequestDto(savedReturnRequest);
    }
    
    /**
     * Convert ReturnRequest entity to ReturnRequestDto
     */
    private ReturnRequestDto convertToReturnRequestDto(ReturnRequest returnRequest) {
        List<String> images = JsonUtil.parseJsonToStringList(returnRequest.getImagesJson());
        
        return ReturnRequestDto.builder()
                .id(returnRequest.getId())
                .orderId(returnRequest.getOrder().getId())
                .userId(returnRequest.getUser().getId())
                .customerName(returnRequest.getUser().getFullName())
                .customerEmail(returnRequest.getUser().getEmail())
                .reason(returnRequest.getReason())
                .status(returnRequest.getStatus())
                .images(images)
                .returnMethod(returnRequest.getReturnMethod())
                .bankName(returnRequest.getBankName())
                .bankAccountNumber(returnRequest.getBankAccountNumber())
                .bankAccountHolder(returnRequest.getBankAccountHolder())
                .adminNote(returnRequest.getAdminNote())
                .approvedByName(returnRequest.getApprovedBy() != null ? returnRequest.getApprovedBy().getFullName() : null)
                .approvedAt(returnRequest.getApprovedAt())
                .createdAt(returnRequest.getCreatedAt())
                .updatedAt(returnRequest.getUpdatedAt())
                .build();
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

        // Fetch return request nếu có
        ReturnRequestSummaryDto returnRequestSummary = null;
        Optional<ReturnRequest> returnRequestOpt = returnRequestRepository.findByOrderIdWithDetails(order.getId());
        if (returnRequestOpt.isPresent()) {
            ReturnRequest returnRequest = returnRequestOpt.get();
            // Truncate reason nếu quá dài (chỉ hiển thị 50 ký tự đầu)
            String reason = returnRequest.getReason();
            if (reason != null && reason.length() > 50) {
                reason = reason.substring(0, 50) + "...";
            }
            returnRequestSummary = ReturnRequestSummaryDto.builder()
                    .id(returnRequest.getId())
                    .status(returnRequest.getStatus())
                    .createdAt(returnRequest.getCreatedAt())
                    .reason(reason)
                    .build();
        }

        return OrderSummaryDto.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .totalItems(totalItems)
                .returnRequest(returnRequestSummary)
                .build();
    }
    
    /**
     * Helper: Chuyển Order Entity -> OrderDto (Chi tiết)
     */
    private OrderDto convertToOrderDto(Order order, String paymentUrl) {
        // Chuyển OrderDetail -> CartItemDto (dùng tạm DTO này)
        List<CartItemDto> detailDtos = order.getOrderDetails().stream().map(detail -> {
            ProductVariant v = detail.getVariant();
            
            // Lấy imageUrl từ variant, nếu null hoặc rỗng thì lấy ảnh primary từ Product_Images
            String imageUrl = v.getImageUrl();
            if ((imageUrl == null || imageUrl.isBlank()) && v.getProduct() != null) {
                Long productId = v.getProduct().getId();
                Optional<ProductImage> coverImage = productImageRepository.findByProductIdAndIsPrimaryTrue(productId);
                if (coverImage.isPresent()) {
                    imageUrl = coverImage.get().getImageUrl();
                }
            }
            
            return CartItemDto.builder()
                    .variantId(v.getId())
                    .sku(detail.getVariantSku())
                    .productName(v.getProduct().getName())
                    .brandName(v.getProduct().getBrand().getName())
                    .size(v.getSize())
                    .color(v.getColor())
//                    .imageUrl(imageUrl)
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

        // Lấy coupon code nếu có
        String couponCode = null;
        if (order.getCoupon() != null) {
            couponCode = order.getCoupon().getCode();
        }

        // Convert status histories (nếu có)
        List<OrderStatusHistoryDto> statusHistories = new ArrayList<>();
        if (order.getStatusHistories() != null && !order.getStatusHistories().isEmpty()) {
            statusHistories = order.getStatusHistories().stream()
                    .map(h -> OrderStatusHistoryDto.builder()
                            .id(h.getId())
                            .status(h.getStatus())
                            .changedAt(h.getChangedAt())
                            .build())
                    .collect(Collectors.toList());
        }

        // Fetch return request nếu có
        ReturnRequestDto returnRequestDto = null;
        Optional<ReturnRequest> returnRequestOpt = returnRequestRepository.findByOrderIdWithDetails(order.getId());
        if (returnRequestOpt.isPresent()) {
            returnRequestDto = convertToReturnRequestDto(returnRequestOpt.get());
        }

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO)
                .couponCode(couponCode)
                .shippingFee(order.getShippingFee())      // ⬅️ NEW
                .taxAmount(order.getTaxAmount())          // ⬅️ NEW
                .pointsUsed(order.getPointsUsed())        // ⬅️ NEW
                .pointsDiscount(
                        order.getPointsUsed() != null
                                ? BigDecimal.valueOf(order.getPointsUsed() * 1000L)
                                : BigDecimal.ZERO
                )                                         // ⬅️ NEW
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .addressShipping(convertToAddressDto(order.getAddressShipping()))
                .payment(paymentDto)
                .orderDetails(detailDtos)
                .statusHistories(statusHistories)
                .returnRequest(returnRequestDto)
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

        // Ghép thành ShippingAddressRequestDto
        ShippingAddressRequestDto dto = ShippingAddressRequestDto.builder()
                .line1(address.getLine1())
                .line2(address.getLine2())
                .ward(address.getWard())
                .district(address.getDistrict())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .build();

        // Gọi BE tính phí ship chuẩn theo KM
        double fee = shippingService.calculateShippingFee(dto);

        return BigDecimal.valueOf(fee);
    }
//    private BigDecimal calculateShippingFee(Address address) {
//        if (address == null || address.getCity() == null) {
//            // Default shipping fee nếu không có địa chỉ
//            return BigDecimal.valueOf(50000);
//        }
//
//        String city = address.getCity().toLowerCase().trim();
//
//        // Danh sách thành phố lớn (nội thành - phí ship thấp hơn)
//        String[] majorCities = {
//            "hà nội", "hanoi", "ha noi",
//            "tp. hồ chí minh", "tp hcm", "hồ chí minh", "ho chi minh", "hochiminh",
//            "đà nẵng", "da nang", "danang",
//            "cần thơ", "can tho", "cantho",
//            "hải phòng", "hai phong", "haiphong"
//        };
//
//        // Kiểm tra xem có phải thành phố lớn không
//        for (String majorCity : majorCities) {
//            if (city.contains(majorCity) || majorCity.contains(city)) {
//                return BigDecimal.valueOf(30000); // Phí ship nội thành
//            }
//        }
//
//        // Các tỉnh/thành phố khác
//        return BigDecimal.valueOf(50000); // Phí ship ngoại thành
//    }
}