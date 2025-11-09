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

/**
 * Service xử lý sản phẩm cho User (Public)
 * 
 * <p>Service này cung cấp các chức năng xem sản phẩm cho user:
 * <ul>
 *   <li>Lấy danh sách sản phẩm với phân trang</li>
 *   <li>Xem thông tin sản phẩm (dạng card - tóm tắt)</li>
 * </ul>
 * 
 * <p><b>Về dữ liệu trả về:</b>
 * <ul>
 *   <li>Chỉ hiển thị các sản phẩm đang active (isActive = true)</li>
 *   <li>Chỉ hiển thị các sản phẩm chưa bị xóa (soft-deleted)</li>
 *   <li>Mỗi sản phẩm bao gồm: ID, tên, slug, brand, hình ảnh, giá thấp nhất, giá cao nhất, tổng tồn kho</li>
 *   <li>Không bao gồm chi tiết variants (chỉ dùng cho danh sách)</li>
 * </ul>
 * 
 * <p><b>Về tối ưu hiệu năng:</b>
 * <ul>
 *   <li>Sử dụng query tối ưu (findAllWithDetails) để load tất cả dữ liệu trong 1 lần</li>
 *   <li>Bao gồm thông tin Brand và Categories trong 1 query</li>
 *   <li>Giảm số lượng queries từ N+1 xuống 1</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy trang đầu tiên, mỗi trang 8 sản phẩm
 * Page&lt;ProductCardDto&gt; products = productService.getAllProductsForCard(0, 8);
 * 
 * System.out.println("Tổng số sản phẩm: " + products.getTotalElements());
 * products.getContent().forEach(p -&gt; System.out.println(p.getName()));
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Lấy danh sách sản phẩm với phân trang (dạng card - tóm tắt)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi repository để lấy danh sách sản phẩm với phân trang</li>
     *   <li>Chỉ lấy các sản phẩm đang active và chưa bị xóa</li>
     *   <li>Chuyển đổi từng sản phẩm sang ProductCardDto</li>
     *   <li>Tính giá thấp nhất và giá cao nhất từ tất cả variants</li>
     *   <li>Tính tổng tồn kho từ tất cả variants</li>
     *   <li>Trả về Page chứa danh sách sản phẩm</li>
     * </ol>
     * 
     * <p><b>Về phân trang:</b>
     * <ul>
     *   <li>Sử dụng Spring Data Pageable để phân trang</li>
     *   <li>Mặc định: page = 0, size = 8</li>
     *   <li>Trả về Page chứa: danh sách sản phẩm, tổng số trang, tổng số phần tử</li>
     * </ul>
     * 
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi sản phẩm bao gồm: ID, tên, slug, brand, hình ảnh chính</li>
     *   <li>Giá thấp nhất và giá cao nhất trong tất cả variants</li>
     *   <li>Tổng tồn kho của tất cả variants</li>
     *   <li>Không bao gồm chi tiết variants (chỉ dùng cho danh sách)</li>
     * </ul>
     * 
     * <p><b>Về xử lý lỗi:</b>
     * <ul>
     *   <li>Nếu có lỗi khi chuyển đổi 1 sản phẩm, sẽ log lỗi nhưng không throw exception</li>
     *   <li>Sẽ trả về DTO tối thiểu để không crash toàn bộ page</li>
     * </ul>
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Số items mỗi trang
     * @return Page chứa danh sách ProductCardDto
     * 
     * @example
     * <pre>
     * // Lấy trang đầu tiên, mỗi trang 8 sản phẩm
     * Page&lt;ProductCardDto&gt; products = productService.getAllProductsForCard(0, 8);
     * 
     * System.out.println("Tổng số sản phẩm: " + products.getTotalElements());
     * System.out.println("Tổng số trang: " + products.getTotalPages());
     * products.getContent().forEach(p -&gt; {
     *     System.out.println(p.getName() + " - Giá: " + p.getPrice() + " - " + p.getPriceBase());
     * });
     * </pre>
     */
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