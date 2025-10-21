package com.sneakery.store.controller;

import com.sneakery.store.dto.AdminOrderDetailDto;
import com.sneakery.store.dto.AdminOrderListDto;
import com.sneakery.store.dto.OrderStatusUpdateRequestDto;
import com.sneakery.store.service.AdminOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
// SỬA LỖI: Đảm bảo import DÒNG NÀY
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// XÓA DÒNG "import java.awt.print.Pageable;" NẾU BẠN THẤY NÓ
// import java.awt.print.Pageable; // <-- XÓA DÒNG NÀY

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public ResponseEntity<Page<AdminOrderListDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Nếu có search hoặc status filter, sử dụng method với filters
        if ((search != null && !search.trim().isEmpty()) || 
            (status != null && !status.trim().isEmpty())) {
            Page<AdminOrderListDto> orderPage = adminOrderService.getAllOrdersWithFilters(search, status, pageable);
            return ResponseEntity.ok(orderPage);
        }
        
        // Nếu không có filter, sử dụng method mặc định
        Page<AdminOrderListDto> orderPage = adminOrderService.getAllOrders(pageable);
        return ResponseEntity.ok(orderPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminOrderDetailDto> getOrderById(@PathVariable Long id) {
        AdminOrderDetailDto orderDetail = adminOrderService.getOrderById(id);
        return ResponseEntity.ok(orderDetail);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AdminOrderDetailDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequestDto requestDto
    ) {
        AdminOrderDetailDto updatedOrder = adminOrderService.updateOrderStatus(id, requestDto.getStatus());
        return ResponseEntity.ok(updatedOrder);
    }
}