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

    private ProductCardDto convertToProductCardDto(Product product) {
        Optional<ProductVariant> representativeVariant = Optional.ofNullable(product.getVariants())
                .flatMap(variants -> variants.stream()
                        // SỬA LỖI: v.getPriceSale() và v.getPriceBase()
                        .min(Comparator.comparing(v -> v.getPriceSale() != null ? v.getPriceSale() : v.getPriceBase())));

        String imageUrl = representativeVariant.map(ProductVariant::getImageUrl).orElse("https://placehold.co/400");

        // SỬA LỖI: v.getPriceSale() và v.getPriceBase()
        BigDecimal price = representativeVariant.map(v -> v.getPriceSale() != null ? v.getPriceSale() : v.getPriceBase()).orElse(BigDecimal.ZERO);

        return ProductCardDto.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .brandName(product.getBrand().getName())
                .imageUrl(imageUrl)
                .price(price)
                .build();
    }
}