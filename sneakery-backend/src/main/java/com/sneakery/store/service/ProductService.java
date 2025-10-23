package com.sneakery.store.service;

import com.sneakery.store.dto.ProductCardDto;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor; // SỬA ĐỔI: Thêm import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor // SỬA ĐỔI: Dùng Constructor Injection
public class ProductService {

    // SỬA ĐỔI: Thêm 'final'
    private final ProductRepository productRepository;

    // Chỉ giữ lại phương thức có phân trang (page, size)
    public Page<ProductCardDto> getAllProductsForCard(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Gọi phương thức repository đã được tối ưu
        Page<Product> productPage = productRepository.findAllWithDetails(pageable);

        return productPage.map(this::convertToProductCardDto);
    }

    /**
     * Convert Product Entity sang ProductCardDto
     * Map đầy đủ fields cho frontend display
     */
    private ProductCardDto convertToProductCardDto(Product product) {
        // Lấy variant có giá thấp nhất (đại diện cho product)
        Optional<ProductVariant> representativeVariant = Optional.ofNullable(product.getVariants())
                .flatMap(variants -> variants.stream()
                        .min(Comparator.comparing(v -> v.getPriceSale() != null ? v.getPriceSale() : v.getPriceBase())));

        // Lấy ảnh đại diện (ưu tiên từ product images, fallback variant image)
        String imageUrl = Optional.ofNullable(product.getImages())
                .flatMap(images -> images.stream()
                        .filter(img -> img.getIsPrimary())
                        .findFirst()
                        .map(img -> img.getImageUrl()))
                .or(() -> representativeVariant.map(ProductVariant::getImageUrl))
                .orElse("https://placehold.co/400");

        // Tính giá (ưu tiên sale, fallback base)
        BigDecimal priceBase = representativeVariant.map(ProductVariant::getPriceBase).orElse(BigDecimal.ZERO);
        BigDecimal priceSale = representativeVariant.map(ProductVariant::getPriceSale).orElse(null);
        BigDecimal price = priceSale != null ? priceSale : priceBase;

        // Tính tổng stock
        Integer totalStock = Optional.ofNullable(product.getVariants())
                .map(variants -> variants.stream()
                        .mapToInt(ProductVariant::getStockQuantity)
                        .sum())
                .orElse(0);

        return ProductCardDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .brandName(product.getBrand().getName())
                .imageUrl(imageUrl)
                
                // Pricing
                .priceBase(priceBase)
                .priceSale(priceSale)
                .price(price)
                
                // Stats & Badges (từ Product entity)
                .avgRating(product.getAvgRating() != null ? product.getAvgRating().doubleValue() : null)
                .reviewCount(product.getReviewCount())
                .isNew(product.getIsNew())
                .isFeatured(product.getIsFeatured())
                
                // Stock
                .totalStock(totalStock)
                .inStock(totalStock > 0)
                
                .build();
    }
}