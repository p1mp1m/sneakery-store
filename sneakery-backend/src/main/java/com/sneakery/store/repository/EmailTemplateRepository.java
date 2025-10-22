package com.sneakery.store.repository;

import com.sneakery.store.entity.EmailTemplate;
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
}

