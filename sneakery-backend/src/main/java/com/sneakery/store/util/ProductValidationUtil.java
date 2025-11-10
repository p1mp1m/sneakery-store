package com.sneakery.store.util;

import com.sneakery.store.dto.AdminVariantRequestDto;
import com.sneakery.store.entity.Product;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Utility class cho validation business rules của Product
 * Tách validation logic ra khỏi service để code dễ maintain hơn
 */
@Component
@RequiredArgsConstructor
public class ProductValidationUtil {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;

    /**
     * Validate SKU uniqueness trong danh sách variants
     * 
     * @param variants Danh sách variants cần validate
     * @param productId ID sản phẩm (null nếu là tạo mới)
     * @throws ApiException nếu có SKU trùng
     */
    public void validateSkuUniqueness(List<AdminVariantRequestDto> variants, Long productId) {
        Set<String> skuSet = new HashSet<>();
        
        for (AdminVariantRequestDto variant : variants) {
            String sku = variant.getSku();
            
            if (sku == null || sku.trim().isEmpty()) {
                continue; // Skip null/empty, sẽ được validate bởi @NotBlank
            }
            
            // Check duplicate trong list hiện tại
            if (!skuSet.add(sku.trim().toUpperCase())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 
                    "SKU '" + sku + "' bị trùng lặp trong danh sách variants");
            }
            
            // Check duplicate với variants đã tồn tại trong DB
            // Nếu là update variant (có ID), skip variant đó
            boolean isExistingVariant = variant.getId() != null;
            boolean existsInDb = variantRepository.existsBySku(sku.trim());
            
            if (existsInDb && !isExistingVariant) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 
                    "SKU '" + sku + "' đã tồn tại trong hệ thống");
            }
        }
    }

    /**
     * Validate slug uniqueness
     * 
     * @param slug Slug cần validate
     * @param productId ID sản phẩm (null nếu là tạo mới, để skip check chính nó khi update)
     * @throws ApiException nếu slug đã tồn tại
     */
    public void validateSlugUniqueness(String slug, Long productId) {
        if (slug == null || slug.trim().isEmpty()) {
            return; // Will be validated by @NotBlank
        }
        
        Optional<Product> existingProduct = productRepository.findBySlug(slug.trim());
        
        if (existingProduct.isPresent()) {
            // Nếu là update (có productId), chỉ throw error nếu slug thuộc về sản phẩm khác
            if (productId == null || !existingProduct.get().getId().equals(productId)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 
                    "Slug '" + slug + "' đã tồn tại. Vui lòng chọn slug khác.");
            }
        }
    }

    /**
     * Validate variant price logic
     * priceSale phải <= priceBase nếu có
     * 
     * @param variants Danh sách variants
     * @throws ApiException nếu priceSale > priceBase
     */
    public void validateVariantPrices(List<AdminVariantRequestDto> variants) {
        for (AdminVariantRequestDto variant : variants) {
            if (variant.getPriceSale() != null && 
                variant.getPriceBase() != null &&
                variant.getPriceSale().compareTo(variant.getPriceBase()) > 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, 
                    "Giá sale (" + variant.getPriceSale() + ") không được lớn hơn giá gốc (" + 
                    variant.getPriceBase() + ") cho SKU: " + variant.getSku());
            }
        }
    }

    /**
     * Validate product name không trùng với sản phẩm khác (cùng brand)
     * 
     * @param name Tên sản phẩm
     * @param brandId ID thương hiệu
     * @param productId ID sản phẩm (null nếu là tạo mới)
     * @throws ApiException nếu tên đã tồn tại
     */
    public void validateProductNameUniqueness(String name, Integer brandId, Long productId) {
        if (name == null || name.trim().isEmpty() || brandId == null) {
            return;
        }
        
        boolean exists = productRepository.findAll().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name.trim()) &&
                              p.getBrand().getId().equals(brandId) &&
                              (productId == null || !p.getId().equals(productId)));
        
        if (exists) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                "Tên sản phẩm '" + name + "' đã tồn tại cho thương hiệu này");
        }
    }

    /**
     * Validate price range (giá từ và giá đến)
     * - Giá từ và giá đến phải >= 0 nếu có
     * - Giá từ phải <= giá đến nếu cả 2 đều có
     * 
     * @param priceFrom Giá từ (có thể null)
     * @param priceTo Giá đến (có thể null)
     * @throws ApiException nếu validation fail
     */
    public void validatePriceRange(Integer priceFrom, Integer priceTo) {
        // Validate giá từ
        if (priceFrom != null && priceFrom < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                "Giá từ không được âm");
        }

        // Validate giá đến
        if (priceTo != null && priceTo < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                "Giá đến không được âm");
        }

        // Validate giá từ <= giá đến (nếu cả 2 đều có)
        if (priceFrom != null && priceTo != null && priceFrom > priceTo) {
            throw new ApiException(HttpStatus.BAD_REQUEST, 
                "Giá từ (" + priceFrom + ") phải nhỏ hơn hoặc bằng giá đến (" + priceTo + ")");
        }
    }
}

