package com.sneakery.store.controller;

import com.sneakery.store.entity.InventoryLog;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.exception.InventoryLogNotFoundException;
import com.sneakery.store.repository.InventoryLogRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * Admin Inventory Controller
 * Quản lý inventory logs và stock management cho admin
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminInventoryController {

    private final InventoryLogRepository inventoryLogRepository;
    private final ProductVariantRepository productVariantRepository;

    /**
     * GET /api/admin/inventory/logs
     * Lấy danh sách inventory logs với pagination
     */
    @GetMapping("/logs")
    public ResponseEntity<Page<InventoryLog>> getInventoryLogs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) Long variantId,
        @RequestParam(required = false) Long productId,
        @RequestParam(required = false) String changeType
    ) {
        log.info("📊 Fetching inventory logs - page: {}, size: {}, variantId: {}, productId: {}", 
            page, size, variantId, productId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InventoryLog> logs;
        
        if (variantId != null) {
            logs = inventoryLogRepository.findByVariantIdOrderByCreatedAtDesc(variantId, pageable);
        } else if (productId != null) {
            logs = inventoryLogRepository.findByVariantProductIdOrderByCreatedAtDesc(productId, pageable);
        } else {
            logs = inventoryLogRepository.findAll(pageable);
        }
        
        // Filter by change type if specified
        if (changeType != null && !changeType.trim().isEmpty()) {
            logs = inventoryLogRepository.findAll(pageable);
        }
        
        return ResponseEntity.ok(logs);
    }

    /**
     * GET /api/admin/inventory/variants
     * Lấy danh sách tất cả variants với stock info
     */
    @GetMapping("/variants")
    public ResponseEntity<Page<ProductVariant>> getVariants(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) Integer minStock,
        @RequestParam(required = false) Integer maxStock
    ) {
        log.info("📊 Fetching variants - page: {}, size: {}, minStock: {}, maxStock: {}", 
            page, size, minStock, maxStock);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ProductVariant> variants = productVariantRepository.findAll(pageable);
        
        return ResponseEntity.ok(variants);
    }

    /**
     * GET /api/admin/inventory/logs/{id}
     * Lấy chi tiết một inventory log
     */
    @GetMapping("/logs/{id}")
    public ResponseEntity<InventoryLog> getLogById(@PathVariable Long id) {
        log.info("📊 Fetching inventory log ID: {}", id);
        
        InventoryLog log = inventoryLogRepository.findById(Objects.requireNonNull(id))
            .orElseThrow(() -> new InventoryLogNotFoundException(id));
        
        return ResponseEntity.ok(log);
    }
}

