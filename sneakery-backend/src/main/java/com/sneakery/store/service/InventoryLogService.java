package com.sneakery.store.service;

import com.sneakery.store.entity.InventoryLog;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.entity.User;
import com.sneakery.store.repository.InventoryLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inventory Log Service
 * Auto logging cho mọi inventory changes
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryLogService {

    private final InventoryLogRepository inventoryLogRepository;

    /**
     * Log inventory change
     */
    @Transactional
    public void logInventoryChange(
        ProductVariant variant, 
        String changeType,
        int quantityBefore, 
        int quantityChange,
        String referenceType,
        Long referenceId,
        String note,
        User changedBy
    ) {
        log.info("📊 Logging inventory change for variant ID: {}", variant.getId());
        
        InventoryLog log = new InventoryLog();
        log.setVariant(variant);
        log.setChangeType(changeType);
        log.setQuantityBefore(quantityBefore);
        log.setQuantityChange(quantityChange);
        log.setQuantityAfter(quantityBefore + quantityChange);
        log.setReferenceType(referenceType);
        log.setReferenceId(referenceId);
        log.setNote(note);
        log.setChangedBy(changedBy);
        
        InventoryLog savedLog = inventoryLogRepository.save(log);
        
        InventoryLogService.log.debug("✅ Inventory log created: {} {} -> {}", 
            changeType, quantityBefore, savedLog.getQuantityAfter());
    }

    /**
     * Log inventory change (overload without user)
     */
    @Transactional
    public void logInventoryChange(
        ProductVariant variant,
        String changeType,
        int quantityBefore,
        int quantityChange,
        String referenceType,
        Long referenceId,
        String note
    ) {
        logInventoryChange(variant, changeType, quantityBefore, quantityChange, 
            referenceType, referenceId, note, null);
    }

    /**
     * Get logs by variant
     */
    @Transactional(readOnly = true)
    public Page<InventoryLog> getLogsByVariant(Long variantId, Pageable pageable) {
        return inventoryLogRepository.findByVariantIdOrderByCreatedAtDesc(variantId, pageable);
    }

    /**
     * Get logs by product
     */
    @Transactional(readOnly = true)
    public Page<InventoryLog> getLogsByProduct(Long productId, Pageable pageable) {
        return inventoryLogRepository.findByVariantProductIdOrderByCreatedAtDesc(productId, pageable);
    }
}

