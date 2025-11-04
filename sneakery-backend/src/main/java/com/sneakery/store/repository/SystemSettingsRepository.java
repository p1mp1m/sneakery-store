package com.sneakery.store.repository;

import com.sneakery.store.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
    
    /**
     * Tìm setting theo key
     */
    Optional<SystemSettings> findBySettingKey(String settingKey);
    
    /**
     * Tìm tất cả settings theo type
     */
    List<SystemSettings> findBySettingType(String settingType);
    
    /**
     * Kiểm tra xem setting key đã tồn tại chưa
     */
    boolean existsBySettingKey(String settingKey);
    
    /**
     * Lấy tất cả settings, sắp xếp theo type và key
     */
    @Query("SELECT s FROM SystemSettings s ORDER BY s.settingType, s.settingKey")
    List<SystemSettings> findAllOrdered();
}

