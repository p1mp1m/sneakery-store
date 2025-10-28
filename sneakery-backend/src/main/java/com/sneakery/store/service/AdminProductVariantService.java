package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    /**
     * Lấy danh sách biến thể với filter
     */
    @Transactional(readOnly = true)
    public Page<AdminProductVariantDto> getVariantsWithFilter(ProductVariantFilterDto filter, Pageable pageable) {
        Page<ProductVariant> variants = productVariantRepository.findWithFilters(
                filter.getSearch(),
                filter.getColor(),
                filter.getSize(),
                filter.getProductId(),
                filter.getStockStatus(),
                pageable
        );
        return variants.map(this::convertToDto);
    }

    /**
     * Lấy chi tiết biến thể theo ID
     */
    @Transactional(readOnly = true)
    public AdminProductVariantDto getVariantById(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + id));
        return convertToDto(variant);
    }

    /**
     * Tạo biến thể mới
     */
    public AdminProductVariantDto createVariant(AdminProductVariantRequestDto requestDto) {
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + requestDto.getProductId()));

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(requestDto.getSku());
        variant.setSize(requestDto.getSize());
        variant.setColor(requestDto.getColor());
        variant.setPriceBase(requestDto.getPriceBase());
        variant.setPriceSale(requestDto.getPriceSale());
        variant.setCostPrice(requestDto.getCostPrice());
        variant.setStockQuantity(requestDto.getStockQuantity());
        variant.setLowStockThreshold(requestDto.getLowStockThreshold() != null ? requestDto.getLowStockThreshold() : 10);
        variant.setWeightGrams(requestDto.getWeightGrams());
        variant.setImageUrl(requestDto.getImageUrl());
        variant.setIsActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : true);
        variant.setCreatedAt(LocalDateTime.now());
        variant.setUpdatedAt(LocalDateTime.now());

        ProductVariant savedVariant = productVariantRepository.save(variant);
        return convertToDto(savedVariant);
    }

    /**
     * Cập nhật biến thể
     */
    public AdminProductVariantDto updateVariant(Long id, AdminProductVariantRequestDto requestDto) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + id));

        variant.setSku(requestDto.getSku());
        variant.setSize(requestDto.getSize());
        variant.setColor(requestDto.getColor());
        variant.setPriceBase(requestDto.getPriceBase());
        variant.setPriceSale(requestDto.getPriceSale());
        variant.setCostPrice(requestDto.getCostPrice());
        variant.setStockQuantity(requestDto.getStockQuantity());
        variant.setLowStockThreshold(requestDto.getLowStockThreshold());
        variant.setWeightGrams(requestDto.getWeightGrams());
        variant.setImageUrl(requestDto.getImageUrl());
        variant.setIsActive(requestDto.getIsActive());
        variant.setUpdatedAt(LocalDateTime.now());

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return convertToDto(updatedVariant);
    }

    /**
     * Xóa biến thể
     */
    public void deleteVariant(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + id));
        productVariantRepository.delete(variant);
    }

    /**
     * Cập nhật tồn kho
     */
    public AdminProductVariantDto updateStock(Long id, StockUpdateRequestDto requestDto) {
        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + id));

        variant.setStockQuantity(requestDto.getNewQuantity());
        variant.setUpdatedAt(LocalDateTime.now());

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return convertToDto(updatedVariant);
    }

    /**
     * Lấy thống kê biến thể
     */
    @Transactional(readOnly = true)
    public ProductVariantStatsDto getVariantStatistics() {
        List<ProductVariant> allVariants = productVariantRepository.findAll();
        
        long totalVariants = allVariants.size();
        long inStockVariants = allVariants.stream()
                .filter(v -> v.getStockQuantity() > 10)
                .count();
        long lowStockVariants = allVariants.stream()
                .filter(v -> v.getStockQuantity() > 0 && v.getStockQuantity() <= 10)
                .count();
        long outOfStockVariants = allVariants.stream()
                .filter(v -> v.getStockQuantity() == 0)
                .count();
        
        long totalStockValue = allVariants.stream()
                .mapToLong(v -> v.getPriceBase().multiply(BigDecimal.valueOf(v.getStockQuantity())).longValue())
                .sum();
        
        long averageStockPerVariant = totalVariants > 0 ? 
                allVariants.stream().mapToInt(ProductVariant::getStockQuantity).sum() / (int) totalVariants : 0;

        return ProductVariantStatsDto.builder()
                .totalVariants(totalVariants)
                .inStockVariants(inStockVariants)
                .lowStockVariants(lowStockVariants)
                .outOfStockVariants(outOfStockVariants)
                .totalStockValue(totalStockValue)
                .averageStockPerVariant(averageStockPerVariant)
                .lowStockThreshold(10L)
                .build();
    }

    /**
     * Convert entity to DTO
     */
    private AdminProductVariantDto convertToDto(ProductVariant variant) {
        BigDecimal currentPrice = variant.getPriceSale() != null ? variant.getPriceSale() : variant.getPriceBase();
        String stockStatus = getStockStatus(variant.getStockQuantity());
        boolean isLowStock = variant.getStockQuantity() > 0 && variant.getStockQuantity() <= 10;
        boolean isOutOfStock = variant.getStockQuantity() == 0;

        // Safely get product and brand info
        Long productId = variant.getProduct() != null ? variant.getProduct().getId() : null;
        String productName = variant.getProduct() != null ? variant.getProduct().getName() : "Unknown Product";
        String productSlug = variant.getProduct() != null ? variant.getProduct().getSlug() : "";
        String brandName = (variant.getProduct() != null && variant.getProduct().getBrand() != null) 
                ? variant.getProduct().getBrand().getName() : "Unknown Brand";

        return AdminProductVariantDto.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .size(variant.getSize())
                .color(variant.getColor())
                .priceBase(variant.getPriceBase())
                .priceSale(variant.getPriceSale())
                .costPrice(variant.getCostPrice())
                .stockQuantity(variant.getStockQuantity())
                .lowStockThreshold(variant.getLowStockThreshold())
                .weightGrams(variant.getWeightGrams())
                .imageUrl(variant.getImageUrl())
                .isActive(variant.getIsActive())
                .createdAt(variant.getCreatedAt())
                .updatedAt(variant.getUpdatedAt())
                .productId(productId)
                .productName(productName)
                .productSlug(productSlug)
                .brandName(brandName)
                .currentPrice(currentPrice.toString())
                .stockStatus(stockStatus)
                .isLowStock(isLowStock)
                .isOutOfStock(isOutOfStock)
                .build();
    }

    private String getStockStatus(Integer quantity) {
        if (quantity == 0) return "out_of_stock";
        if (quantity <= 10) return "low_stock";
        return "in_stock";
    }
}
