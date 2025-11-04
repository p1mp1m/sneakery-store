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

        try {
            // Gọi phương thức repository đã được tối ưu
            Page<Product> productPage = productRepository.findAllWithDetails(pageable);

            return productPage.map(product -> {
                try {
                    return convertToProductCardDto(product);
                } catch (Exception e) {
                    // Log error nhưng không throw để không block toàn bộ page
                    System.err.println("Error converting product to DTO - ID: " + 
                            (product != null ? product.getId() : "null") + 
                            ", Error: " + e.getMessage());
                    e.printStackTrace();
                    // Return a minimal DTO để không crash
                    return ProductCardDto.builder()
                            .id(product != null ? product.getId() : 0L)
                            .name(product != null && product.getName() != null ? product.getName() : "Unknown Product")
                            .slug(product != null && product.getSlug() != null ? product.getSlug() : "")
                            .brandName("Unknown")
                            .imageUrl("https://placehold.co/400")
                            .priceBase(BigDecimal.ZERO)
                            .price(BigDecimal.ZERO)
                            .totalStock(0)
                            .inStock(false)
                            .build();
                }
            });
        } catch (Exception e) {
            System.err.println("Error in getAllProductsForCard: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Convert Product Entity sang ProductCardDto
     * Map đầy đủ fields cho frontend display
     */
    private ProductCardDto convertToProductCardDto(Product product) {
        // Lấy variant có giá thấp nhất (đại diện cho product)
        Optional<ProductVariant> representativeVariant = Optional.ofNullable(product.getVariants())
                .filter(variants -> !variants.isEmpty())
                .flatMap(variants -> variants.stream()
                        .filter(v -> v != null)
                        .min(Comparator.comparing(v -> {
                            BigDecimal price = v.getPriceSale() != null ? v.getPriceSale() : v.getPriceBase();
                            return price != null ? price : BigDecimal.ZERO;
                        })));

        // Lấy ảnh đại diện (ưu tiên từ product images, fallback variant image)
        String imageUrl = Optional.ofNullable(product.getImages())
                .filter(images -> !images.isEmpty())
                .flatMap(images -> images.stream()
                        .filter(img -> img != null && img.getIsPrimary() != null && img.getIsPrimary())
                        .findFirst()
                        .map(img -> img.getImageUrl() != null ? img.getImageUrl() : ""))
                .filter(url -> !url.isEmpty())
                .or(() -> representativeVariant
                        .map(ProductVariant::getImageUrl)
                        .filter(url -> url != null && !url.isEmpty()))
                .orElse("https://placehold.co/400");

        // Tính giá (ưu tiên sale, fallback base)
        BigDecimal priceBase = representativeVariant.map(ProductVariant::getPriceBase).orElse(BigDecimal.ZERO);
        BigDecimal priceSale = representativeVariant.map(ProductVariant::getPriceSale).orElse(null);
        BigDecimal price = priceSale != null ? priceSale : priceBase;

        // Tính tổng stock
        Integer totalStock = Optional.ofNullable(product.getVariants())
                .filter(variants -> !variants.isEmpty())
                .map(variants -> variants.stream()
                        .filter(v -> v != null)
                        .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                        .sum())
                .orElse(0);

        return ProductCardDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .brandName(product.getBrand() != null ? product.getBrand().getName() : "Unknown")
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