package com.sneakery.store.controller;

import com.sneakery.store.dto.FlashSaleDto;
import com.sneakery.store.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Flash Sale Controller
 * Public endpoints cho users + Admin endpoints
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    /**
     * ADMIN: Lấy tất cả flash sales (cho admin panel)
     */
    @GetMapping("/admin/flash-sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FlashSaleDto>> getAllFlashSales() {
        log.info("📍 GET /api/admin/flash-sales");
        List<FlashSaleDto> flashSales = flashSaleService.getAllFlashSales();
        return ResponseEntity.ok(flashSales);
    }

    /**
     * PUBLIC: Lấy tất cả flash sales đang active
     */
    @GetMapping("/flash-sales/active")
    public ResponseEntity<List<FlashSaleDto>> getActiveFlashSales() {
        log.info("📍 GET /api/flash-sales/active");
        List<FlashSaleDto> flashSales = flashSaleService.getActiveFlashSales();
        return ResponseEntity.ok(flashSales);
    }

    /**
     * PUBLIC: Lấy flash sale của product
     */
    @GetMapping("/flash-sales/product/{productId}")
    public ResponseEntity<FlashSaleDto> getFlashSaleByProductId(@PathVariable Long productId) {
        log.info("📍 GET /api/flash-sales/product/{}", productId);
        
        return flashSaleService.getFlashSaleByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * ADMIN: Tạo flash sale mới
     */
    @PostMapping("/admin/flash-sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlashSaleDto> createFlashSale(@RequestBody FlashSaleDto dto) {
        log.info("📍 POST /api/admin/flash-sales");
        FlashSaleDto created = flashSaleService.createFlashSale(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * ADMIN: Update flash sale
     */
    @PutMapping("/admin/flash-sales/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlashSaleDto> updateFlashSale(
            @PathVariable Integer id,
            @RequestBody FlashSaleDto dto
    ) {
        log.info("📍 PUT /api/admin/flash-sales/{}", id);
        FlashSaleDto updated = flashSaleService.updateFlashSale(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * ADMIN: Delete flash sale
     */
    @DeleteMapping("/admin/flash-sales/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlashSale(@PathVariable Integer id) {
        log.info("📍 DELETE /api/admin/flash-sales/{}", id);
        flashSaleService.deleteFlashSale(id);
        return ResponseEntity.noContent().build();
    }
}

