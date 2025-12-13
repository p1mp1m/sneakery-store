package com.sneakery.store.controller;

import com.sneakery.store.dto.PaymentDto;
import com.sneakery.store.entity.Order;
import com.sneakery.store.entity.OrderStatusHistory;
import com.sneakery.store.entity.Payment;
import com.sneakery.store.exception.PaymentNotFoundException;
import com.sneakery.store.repository.OrderRepository;
import com.sneakery.store.repository.OrderStatusHistoryRepository;
import com.sneakery.store.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminPaymentController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    
    /**
     * Map Payment entity to PaymentDto to avoid Hibernate proxy issues
     */
    private PaymentDto mapToDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .orderNumber(payment.getOrder() != null ? payment.getOrder().getOrderNumber() : null)
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .gatewayResponse(payment.getGatewayResponse())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .build();
    }

    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<Page<PaymentDto>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod
    ) {
        log.info("📍 GET /api/admin/payments - page: {}, size: {}, status: {}, method: {}", page, size, status, paymentMethod);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<Payment> payments;
        
        if (status != null && !status.isEmpty()) {
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                payments = paymentRepository.findByStatusAndPaymentMethod(status, paymentMethod, pageable);
            } else {
                payments = paymentRepository.findByStatus(status, pageable);
            }
        } else if (paymentMethod != null && !paymentMethod.isEmpty()) {
            payments = paymentRepository.findByPaymentMethod(paymentMethod, pageable);
        } else {
            // Sử dụng method với @EntityGraph để eager load Order
            payments = paymentRepository.findAllWithOrder(pageable);
        }
        
        // Map to DTO (Order đã được eager load nên không còn lỗi LazyInitializationException)
        Page<PaymentDto> dtoPage = payments.map(this::mapToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        log.info("📍 GET /api/admin/payments/{}", id);
        
        // Sử dụng method với @EntityGraph để eager load Order
        Payment payment = paymentRepository.findByIdWithOrder(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        
        return ResponseEntity.ok(mapToDto(payment));
    }

    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<PaymentDto> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        log.info("📍 PUT /api/admin/payments/{}/status - status: {}", id, status);
        
        // Sử dụng method với @EntityGraph để eager load Order
        Payment payment = paymentRepository.findByIdWithOrder(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        
        payment.setStatus(status);
        
        // Đồng bộ: Cập nhật paid_at khi payment completed
        if ("completed".equals(status) && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        // Đồng bộ: Cập nhật order status khi payment status thay đổi
        try {
            Order order = updatedPayment.getOrder();
            if (order != null) {
                // Logic: Nếu payment completed → order status = confirmed
                // Nếu payment failed → order status = cancelled (nếu chưa có payment nào completed)
                if ("completed".equals(status) && !"delivered".equals(order.getStatus()) && !"shipped".equals(order.getStatus())) {
                    order.setStatus("confirmed");
                    
                    // Tạo status history
                    OrderStatusHistory history = new OrderStatusHistory();
                    history.setOrder(order);
                    history.setStatus("confirmed");
                    history.setChangedAt(LocalDateTime.now());
                    statusHistoryRepository.save(history);
                    
                    orderRepository.save(order);
                    log.info("✅ Order {} status updated to 'confirmed' after payment completed", order.getId());
                } else if ("failed".equals(status)) {
                    // Kiểm tra xem có payment nào completed không
                    boolean hasCompletedPayment = order.getPayments().stream()
                        .anyMatch(p -> "completed".equals(p.getStatus()) && !p.getId().equals(payment.getId()));
                    
                    if (!hasCompletedPayment && !"cancelled".equals(order.getStatus())) {
                        order.setStatus("cancelled");
                        
                        // Tạo status history
                        OrderStatusHistory history = new OrderStatusHistory();
                        history.setOrder(order);
                        history.setStatus("cancelled");
                        history.setChangedAt(LocalDateTime.now());
                        statusHistoryRepository.save(history);
                        
                        orderRepository.save(order);
                        log.info("✅ Order {} status updated to 'cancelled' after payment failed", order.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ Error syncing order status with payment status: {}", e.getMessage());
            // Không fail payment update nếu order sync thất bại
        }
        
        return ResponseEntity.ok(mapToDto(updatedPayment));
    }

    @PostMapping("/{id}/refund")
    @Transactional
    public ResponseEntity<PaymentDto> refundPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> refundData
    ) {
        log.info("📍 POST /api/admin/payments/{}/refund", id);
        
        // Sử dụng method với @EntityGraph để eager load Order
        Payment payment = paymentRepository.findByIdWithOrder(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        
        payment.setStatus("refunded");
        Payment refundedPayment = paymentRepository.save(payment);
        
        return ResponseEntity.ok(mapToDto(refundedPayment));
    }
}

