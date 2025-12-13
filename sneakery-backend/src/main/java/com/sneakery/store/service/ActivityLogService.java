package com.sneakery.store.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ActivityLogService {

    public void logAction(String type, String message) {
        // Sau này có thể mở rộng lưu vào DB
        log.info("[{}] {}", type, message);
    }
}
