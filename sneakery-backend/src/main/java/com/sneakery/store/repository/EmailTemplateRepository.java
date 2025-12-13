package com.sneakery.store.repository;

import com.sneakery.store.entity.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    /**
     * Đếm số templates được tạo trong tháng này
     */
    @Query("SELECT COUNT(e) FROM EmailTemplate e WHERE YEAR(e.createdAt) = :year AND MONTH(e.createdAt) = :month")
    long countByCreatedAtYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    /**
     * Đếm số templates theo isActive
     */
    @Query("SELECT COUNT(e) FROM EmailTemplate e WHERE e.isActive = :isActive")
    long countByIsActive(@Param("isActive") Boolean isActive);
}

