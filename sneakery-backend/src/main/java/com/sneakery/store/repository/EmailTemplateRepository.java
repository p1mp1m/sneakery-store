package com.sneakery.store.repository;

import com.sneakery.store.entity.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho Email Templates
 */
@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Integer> {
    
    /**
     * Tìm template theo tên
     */
    Optional<EmailTemplate> findByTemplateNameAndIsActiveTrue(String templateName);
    
    /**
     * Tìm template theo isActive với pagination
     */
    Page<EmailTemplate> findByIsActive(Boolean isActive, Pageable pageable);
}

