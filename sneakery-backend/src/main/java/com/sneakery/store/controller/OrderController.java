package com.sneakery.store.controller;

import com.sneakery.store.dto.CheckoutRequestDto;
import com.sneakery.store.dto.OrderDto;
import com.sneakery.store.dto.OrderSummaryDto;
import com.sneakery.store.entity.User;
import com.sneakery.store.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(
            @AuthenticationPrincipal User userPrincipal,
            @Valid @RequestBody CheckoutRequestDto requestDto
    ) {
        log.info("📍 POST /api/orders/checkout - User: {}", userPrincipal.getId());
        OrderDto order = orderService.createOrderFromCart(userPrincipal.getId(), requestDto);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryDto>> getMyOrders(
            @AuthenticationPrincipal User userPrincipal
    ) {
        log.info("📍 GET /api/orders - User: {}", userPrincipal.getId());
        List<OrderSummaryDto> orders = orderService.getMyOrders(userPrincipal.getId());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getMyOrderById(
            @AuthenticationPrincipal User userPrincipal,
            @PathVariable Long orderId
    ) {
        log.info("📍 GET /api/orders/{} - User: {}", orderId, userPrincipal.getId());
        OrderDto order = orderService.getMyOrderById(orderId, userPrincipal.getId());
        return ResponseEntity.ok(order);
    }
}

