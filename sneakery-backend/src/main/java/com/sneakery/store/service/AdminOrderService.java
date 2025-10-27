package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.Address;
import com.sneakery.store.entity.Order;
import com.sneakery.store.entity.OrderStatusHistory;
import com.sneakery.store.entity.Payment;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.OrderRepository;
import com.sneakery.store.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;

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
}