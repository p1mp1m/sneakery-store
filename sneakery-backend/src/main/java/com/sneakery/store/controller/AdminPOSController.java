package com.sneakery.store.controller;

import com.sneakery.store.dto.OrderDto;
import com.sneakery.store.dto.POSOrderRequestDto;
import com.sneakery.store.service.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/pos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminPOSController {

    private final AdminOrderService adminOrderService;

    /**
     * Tạo đơn hàng từ POS (Point of Sale)
     * Endpoint: POST /api/admin/pos/orders
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> createPOSOrder(
            @Valid @RequestBody POSOrderRequestDto requestDto
    ) {
        log.info("📍 POST /api/admin/pos/orders - Items: {}", requestDto.getItems().size());
        
        OrderDto order = adminOrderService.createPOSOrder(requestDto);
        return ResponseEntity.ok(order);
    }

    /**
     * Lấy danh sách POS orders với pagination
     * Endpoint: GET /api/admin/pos/orders
     */
    @GetMapping("/orders")
    public ResponseEntity<org.springframework.data.domain.Page<OrderDto>> getPOSOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("📋 GET /api/admin/pos/orders - page: {}, size: {}", page, size);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<OrderDto> orders = adminOrderService.getPOSOrders(pageable);
        
        return ResponseEntity.ok(orders);
    }
}

