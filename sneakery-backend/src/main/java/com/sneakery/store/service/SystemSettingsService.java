package com.sneakery.store.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sneakery.store.entity.SystemSettings;
import com.sneakery.store.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository settingsRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Lấy tất cả settings, nhóm theo type
     */
    @Transactional(readOnly = true)
    public Map<String, Map<String, Object>> getAllSettings() {
        log.info("📋 Getting all system settings");
        
        List<SystemSettings> allSettings = settingsRepository.findAllOrdered();
        
        Map<String, Map<String, Object>> groupedSettings = new HashMap<>();
        
        for (SystemSettings setting : allSettings) {
            String type = setting.getSettingType();
            String key = setting.getSettingKey();
            
            // Remove type prefix from key (e.g., "store.name" -> "name")
            String shortKey = key.contains(".") ? key.substring(key.indexOf(".") + 1) : key;
            
            groupedSettings.computeIfAbsent(type, k -> new HashMap<>());
            
            // Try to parse as JSON, if fails use as string
            try {
                Object value = objectMapper.readValue(setting.getSettingValue(), Object.class);
                groupedSettings.get(type).put(shortKey, value);
            } catch (Exception e) {
                // If not JSON, use as string
                groupedSettings.get(type).put(shortKey, setting.getSettingValue());
            }
        }
        
        return groupedSettings;
    }

    /**
     * Lấy settings theo type
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSettingsByType(String type) {
        log.info("📋 Getting settings for type: {}", type);
        
        List<SystemSettings> settings = settingsRepository.findBySettingType(type);
        Map<String, Object> result = new HashMap<>();
        
        for (SystemSettings setting : settings) {
            String key = setting.getSettingKey();
            String shortKey = key.contains(".") ? key.substring(key.indexOf(".") + 1) : key;
            
            try {
                Object value = objectMapper.readValue(setting.getSettingValue(), Object.class);
                result.put(shortKey, value);
            } catch (Exception e) {
                result.put(shortKey, setting.getSettingValue());
            }
        }
        
        return result;
    }

    /**
     * Lấy một setting theo key
     */
    @Transactional(readOnly = true)
    public String getSetting(String key) {
        return settingsRepository.findBySettingKey(key)
                .map(SystemSettings::getSettingValue)
                .orElse(null);
    }

    /**
     * Lưu hoặc cập nhật settings
     */
    @Transactional
    public void saveSettings(String type, Map<String, Object> settings) {
        log.info("💾 Saving settings for type: {}", type);
        
        for (Map.Entry<String, Object> entry : settings.entrySet()) {
            String key = type + "." + entry.getKey();
            Object value = entry.getValue();
            
            String valueStr;
            try {
                // Convert to JSON string if it's an object
                if (value instanceof Map || value instanceof List) {
                    valueStr = objectMapper.writeValueAsString(value);
                } else {
                    valueStr = value != null ? value.toString() : "";
                }
            } catch (Exception e) {
                valueStr = value != null ? value.toString() : "";
            }
            
            SystemSettings setting = settingsRepository.findBySettingKey(key)
                    .orElse(new SystemSettings());
            
            setting.setSettingKey(key);
            setting.setSettingValue(valueStr);
            setting.setSettingType(type);
            
            settingsRepository.save(setting);
        }
        
        log.info("✅ Settings saved successfully for type: {}", type);
    }

    /**
     * Lưu một setting cụ thể
     */
    @Transactional
    public void saveSetting(String key, String value, String type) {
        SystemSettings setting = settingsRepository.findBySettingKey(key)
                .orElse(new SystemSettings());
        
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        setting.setSettingType(type);
        
        settingsRepository.save(setting);
    }

    /**
     * Xóa setting
     */
    @Transactional
    public void deleteSetting(String key) {
        settingsRepository.findBySettingKey(key)
                .ifPresent(settingsRepository::delete);
    }
}

