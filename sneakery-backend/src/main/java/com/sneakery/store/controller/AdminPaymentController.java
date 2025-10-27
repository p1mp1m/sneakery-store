package com.sneakery.store.controller;

import com.sneakery.store.dto.PaymentDto;
import com.sneakery.store.entity.Payment;
import com.sneakery.store.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminPaymentController {

    private final PaymentRepository paymentRepository;
    
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
            payments = paymentRepository.findAll(pageable);
        }
        
        // Map to DTO
        Page<PaymentDto> dtoPage = payments.map(this::mapToDto);
        
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        log.info("📍 GET /api/admin/payments/{}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment với id: " + id));
        
        return ResponseEntity.ok(mapToDto(payment));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPaymentStats() {
        log.info("📍 GET /api/admin/payments/stats");
        
        Map<String, Object> stats = new HashMap<>();
        
        BigDecimal totalRevenue = paymentRepository.sumAmountByStatus("completed");
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        
        long completedPayments = paymentRepository.countByStatus("completed");
        long pendingPayments = paymentRepository.countByStatus("pending");
        long failedPayments = paymentRepository.countByStatus("failed");
        
        stats.put("totalRevenue", totalRevenue);
        stats.put("completedPayments", completedPayments);
        stats.put("pendingPayments", pendingPayments);
        stats.put("failedPayments", failedPayments);
        stats.put("totalPayments", completedPayments + pendingPayments + failedPayments);
        
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDto> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        log.info("📍 PUT /api/admin/payments/{}/status - status: {}", id, status);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment"));
        
        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        
        return ResponseEntity.ok(mapToDto(updatedPayment));
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentDto> refundPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> refundData
    ) {
        log.info("📍 POST /api/admin/payments/{}/refund", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment"));
        
        payment.setStatus("refunded");
        Payment refundedPayment = paymentRepository.save(payment);
        
        return ResponseEntity.ok(mapToDto(refundedPayment));
    }
}

