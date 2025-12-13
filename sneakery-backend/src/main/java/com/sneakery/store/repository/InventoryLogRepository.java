package com.sneakery.store.repository;

import com.sneakery.store.entity.InventoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository cho Inventory Logs (Audit)
 */
@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    
    /**
     * Lấy inventory logs theo variant (pagination)
     */
    Page<InventoryLog> findByVariantIdOrderByCreatedAtDesc(Long variantId, Pageable pageable);
    
    /**
     * Lấy inventory logs theo product (through variant)
     */
    Page<InventoryLog> findByVariantProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    /**
     * Lấy logs theo change type
     */
    List<InventoryLog> findByChangeTypeAndCreatedAtBetween(
        String changeType, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    /**
     * Lấy logs theo reference (order, return, etc.)
     */
    List<InventoryLog> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}

