package com.sneakery.store.service;

import com.sneakery.store.dto.FlashSaleDto;
import com.sneakery.store.entity.FlashSale;
import com.sneakery.store.entity.Product;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.FlashSaleRepository;
import com.sneakery.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Flash Sale Service
 * Quản lý flash sales - giảm giá có thời hạn
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlashSaleService {

    private final FlashSaleRepository flashSaleRepository;
    private final ProductRepository productRepository;

    /**
     * Lấy tất cả flash sales (cho admin panel)
     */
    @Transactional(readOnly = true)
    public List<FlashSaleDto> getAllFlashSales() {
        log.info("Fetching all flash sales");
        List<FlashSale> flashSales = flashSaleRepository.findAll();
        
        return flashSales.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả flash sales đang active
     */
    @Transactional(readOnly = true)
    public List<FlashSaleDto> getActiveFlashSales() {
        log.info("Fetching active flash sales");
        LocalDateTime now = LocalDateTime.now();
        
        List<FlashSale> flashSales = flashSaleRepository.findActiveFlashSales(now);
        
        return flashSales.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy flash sale by product ID
     */
    @Transactional(readOnly = true)
    public Optional<FlashSaleDto> getFlashSaleByProductId(Long productId) {
        log.info("Fetching flash sale for product ID: {}", productId);
        LocalDateTime now = LocalDateTime.now();
        
        return flashSaleRepository
                .findActiveFlashSaleByProductId(productId, now)
                .map(this::convertToDto);
    }

    /**
     * Tạo flash sale mới (Admin)
     */
    @Transactional
    public FlashSaleDto createFlashSale(FlashSaleDto dto) {
        log.info("Creating flash sale for product ID: {}", dto.getProductId());
        
        // Validate product exists
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));
        
        // Validate dates
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        
        // Check if product already has an active flash sale
        LocalDateTime now = LocalDateTime.now();
        boolean hasActiveFlashSale = flashSaleRepository
                .findActiveFlashSaleByProductId(dto.getProductId(), now)
                .isPresent();
        
        if (hasActiveFlashSale) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Sản phẩm đã có flash sale đang active");
        }
        
        FlashSale flashSale = new FlashSale();
        flashSale.setProduct(product);
        flashSale.setDiscountPercent(dto.getDiscountPercent());
        flashSale.setStartTime(dto.getStartTime());
        flashSale.setEndTime(dto.getEndTime());
        flashSale.setQuantityLimit(dto.getQuantityLimit());
        flashSale.setSoldCount(0);
        flashSale.setIsActive(true);
        
        flashSale = flashSaleRepository.save(flashSale);
        
        log.info("Created flash sale ID: {}", flashSale.getId());
        return convertToDto(flashSale);
    }

    /**
     * Update flash sale (Admin)
     */
    @Transactional
    public FlashSaleDto updateFlashSale(Integer id, FlashSaleDto dto) {
        log.info("Updating flash sale ID: {}", id);
        
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Flash sale không tồn tại"));
        
        flashSale.setDiscountPercent(dto.getDiscountPercent());
        flashSale.setStartTime(dto.getStartTime());
        flashSale.setEndTime(dto.getEndTime());
        flashSale.setQuantityLimit(dto.getQuantityLimit());
        flashSale.setIsActive(dto.getIsActive());
        
        flashSale = flashSaleRepository.save(flashSale);
        
        log.info("Updated flash sale ID: {}", id);
        return convertToDto(flashSale);
    }

    /**
     * Delete flash sale (Admin)
     */
    @Transactional
    public void deleteFlashSale(Integer id) {
        log.info("Deleting flash sale ID: {}", id);
        
        FlashSale flashSale = flashSaleRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Flash sale không tồn tại"));
        
        flashSaleRepository.delete(flashSale);
        log.info("Deleted flash sale ID: {}", id);
    }

    /**
     * Increment sold count khi có order
     */
    @Transactional
    public void incrementSoldCount(Long productId) {
        LocalDateTime now = LocalDateTime.now();
        
        flashSaleRepository
                .findActiveFlashSaleByProductId(productId, now)
                .ifPresent(flashSale -> {
                    flashSale.setSoldCount(flashSale.getSoldCount() + 1);
                    
                    // Auto deactivate if sold out
                    if (flashSale.getQuantityLimit() != null && 
                        flashSale.getSoldCount() >= flashSale.getQuantityLimit()) {
                        flashSale.setIsActive(false);
                        log.info("Flash sale {} sold out, deactivated", flashSale.getId());
                    }
                    
                    flashSaleRepository.save(flashSale);
                });
    }

    /**
     * Scheduled task: Auto deactivate expired flash sales
     * Chạy mỗi giờ
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void deactivateExpiredFlashSales() {
        log.info("🔄 Running scheduled task: Deactivate expired flash sales");
        
        LocalDateTime now = LocalDateTime.now();
        
        List<FlashSale> expiredSales = flashSaleRepository
                .findByIsActiveTrueAndEndTimeLessThan(now);
        
        if (!expiredSales.isEmpty()) {
            expiredSales.forEach(sale -> sale.setIsActive(false));
            flashSaleRepository.saveAll(expiredSales);
            
            log.info("✅ Deactivated {} expired flash sales", expiredSales.size());
        }
    }

    /**
     * Convert Entity to DTO
     */
    private FlashSaleDto convertToDto(FlashSale flashSale) {
        return FlashSaleDto.builder()
                .id(flashSale.getId())
                .productId(flashSale.getProduct().getId())
                .productName(flashSale.getProduct().getName())
                .productSlug(flashSale.getProduct().getSlug())
                .discountPercent(flashSale.getDiscountPercent())
                .startTime(flashSale.getStartTime())
                .endTime(flashSale.getEndTime())
                .quantityLimit(flashSale.getQuantityLimit())
                .soldCount(flashSale.getSoldCount())
                .isActive(flashSale.getIsActive())
                .build();
    }
}

