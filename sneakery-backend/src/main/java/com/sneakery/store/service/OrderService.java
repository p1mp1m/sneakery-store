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

        // 5. Tạo đơn hàng (Order)
        Order order = new Order();
        order.setUser(user);
        order.setAddressShipping(shippingAddress);
        order.setAddressBilling(billingAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("Pending"); // Trạng thái ban đầu

        // 6. Tính tổng tiền VÀ chuyển CartItem -> OrderDetail
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            
            // 6.1. Kiểm tra tồn kho (quan trọng)
            if (variant.getStockQuantity() < cartItem.getQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Sản phẩm " + variant.getProduct().getName() + " không đủ hàng");
            }
            
            // 6.2. Giảm tồn kho
            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            variantRepository.save(variant);

            // 6.3. Tạo OrderDetail (chốt giá)
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(cartItem.getQuantity());
            BigDecimal effectivePrice = getEffectivePrice(variant);
            detail.setUnitPrice(effectivePrice);
            
            order.getOrderDetails().add(detail);
            totalAmount = totalAmount.add(effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        
        // ... (logic trừ tiền vào totalAmount)

        order.setTotalAmount(totalAmount);

        // 8. Tạo Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(requestDto.getPaymentMethod());
        payment.setStatus("pending"); // Chờ thanh toán
        order.getPayments().add(payment);

        // 9. Tạo Lịch sử Status
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus("Pending");
        history.setChangedAt(LocalDateTime.now());
        order.getStatusHistories().add(history);

        Order savedOrder = orderRepository.save(order);
        cartRepository.delete(cart);

        String paymentUrl = null;
        if ("online".equalsIgnoreCase(requestDto.getPaymentMethod())) {
            paymentUrl = paymentGatewayService.createVNPayPaymentUrl(savedOrder.getId(), totalAmount, "Thanh toan don hang " + savedOrder.getId());
        }
        
        try {
            emailService.sendOrderConfirmation(savedOrder);
        } catch (Exception e) {
            // Log error but don't fail the order creation
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
            paymentUrl = "https://sandbox.vnpayment.vn/pay.html?token=example_token_" + order.getId(); // Ví dụ
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
}