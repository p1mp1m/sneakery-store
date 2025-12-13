package com.sneakery.store.service;

import com.sneakery.store.dto.AdminProductDetailDto;
import com.sneakery.store.dto.AdminVariantRequestDto;
import com.sneakery.store.dto.CategoryDto;
import com.sneakery.store.dto.ProductCardDto;
import com.sneakery.store.entity.Category;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor; // SỬA ĐỔI: Thêm import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
//                            .imageUrl("https://placehold.co/400")
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

        String imageUrl = product.getMainImageUrl();
        if ((imageUrl == null || imageUrl.isBlank())
                && product.getImages() != null
                && !product.getImages().isEmpty()) {

            imageUrl = product.getImages().get(0).getImageUrl();
        }

        if (imageUrl == null) {
            imageUrl = "/placeholder-image.png";
        }
        // Lấy ảnh đại diện (ưu tiên mainImageUrl, sau đó ProductImage primary, cuối cùng variant image)
//        String imageUrl = Optional.ofNullable(product.getMainImageUrl())
//                .filter(url -> !url.isEmpty())
//                .or(() -> Optional.ofNullable(product.getImages())
//                        .filter(images -> !images.isEmpty())
//                        .flatMap(images -> images.stream()
//                                .filter(img -> img != null && img.getIsPrimary() != null && img.getIsPrimary())
//                                .findFirst()
//                                .map(img -> img.getImageUrl() != null ? img.getImageUrl() : ""))
//                        .filter(url -> !url.isEmpty()))
//                .or(() -> representativeVariant
//                        .map(ProductVariant::getImageUrl)
//                        .filter(url -> url != null && !url.isEmpty()))
//                .orElse("https://placehold.co/400");

        // Tính giá (ưu tiên sale, fallback base)
        BigDecimal priceBase = representativeVariant.map(ProductVariant::getPriceBase).orElse(BigDecimal.ZERO);
        BigDecimal priceSale = representativeVariant.map(ProductVariant::getPriceSale).orElse(null);
        BigDecimal price = priceSale != null ? priceSale : priceBase;

        // Tính tổng stock
//        Integer totalStock = Optional.ofNullable(product.getVariants())
//                .filter(variants -> !variants.isEmpty())
//                .map(variants -> variants.stream()
//                        .filter(v -> v != null)
//                        .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
//                        .sum())
//                .orElse(0);
        Integer totalStock = Optional.ofNullable(product.getVariants())
                .filter(variants -> !variants.isEmpty())
                .map(variants -> variants.stream()
                        .filter(v -> v != null)
                        .mapToInt(v -> {
                            int stock = Optional.ofNullable(v.getStockQuantity()).orElse(0);
                            int reserved = Optional.ofNullable(v.getReservedQuantity()).orElse(0);
                            return Math.max(0, stock - reserved);
                        })
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

                // Categories
                .categories(convertCategoriesToDto(product.getCategories()))

                .build();
    }

    /**
     * Lấy thông tin chi tiết sản phẩm theo ID (Public)
     *
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Gọi repository để lấy sản phẩm theo ID</li>
     *   <li>Chỉ lấy các sản phẩm đang active và chưa bị xóa</li>
     *   <li>Chuyển đổi sang AdminProductDetailDto</li>
     *   <li>Trả về thông tin chi tiết đầy đủ của sản phẩm</li>
     * </ol>
     *
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Bao gồm tất cả thông tin sản phẩm: tên, mô tả, brand, categories, variants</li>
     *   <li>Bao gồm tất cả biến thể (variants) với đầy đủ thông tin: SKU, size, màu, giá, số lượng</li>
     *   <li>Sử dụng query tối ưu (findByIdWithDetails) để load tất cả dữ liệu trong 1 lần</li>
     * </ul>
     *
     * <p><b>Về bảo mật:</b>
     * <ul>
     *   <li>Chỉ trả về các sản phẩm đang active (isActive = true)</li>
     *   <li>Chỉ trả về các sản phẩm chưa bị xóa (soft-deleted)</li>
     * </ul>
     *
     * @param productId ID của sản phẩm cần lấy
     * @return AdminProductDetailDto chứa thông tin chi tiết sản phẩm (bao gồm tất cả variants)
     * @throws ApiException nếu không tìm thấy sản phẩm với ID này hoặc sản phẩm không active
     *
     * @example
     * <pre>
     * // Lấy sản phẩm có ID = 1
     * AdminProductDetailDto product = productService.getProductByIdForPublic(1L);
     * System.out.println(product.getName()); // "Nike Air Max 90"
     * System.out.println(product.getVariants().size()); // Số lượng biến thể
     * </pre>
     */
    @Transactional(readOnly = true)
    public AdminProductDetailDto getProductByIdForPublic(Long productId) {
        Product product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        // Kiểm tra sản phẩm có đang active không
        if (product.getIsActive() == null || !product.getIsActive()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không có sẵn");
        }

        return convertToAdminDetailDto(product);
    }

    /**
     * Convert Product Entity sang AdminProductDetailDto
     * Sử dụng lại logic từ AdminProductService
     */
    private AdminProductDetailDto convertToAdminDetailDto(Product product) {
        // Convert Categories
        Set<CategoryDto> categoryDtos = product.getCategories().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getSlug(), c.getParent() != null ? c.getParent().getId() : null))
                .collect(Collectors.toSet());

        // Convert Variants
        List<AdminVariantRequestDto> variantDtos = product.getVariants().stream()
                .filter(v -> v.getDeletedAt() == null) // Chỉ lấy variants chưa bị xóa
                .map(v -> {
                    AdminVariantRequestDto dto = new AdminVariantRequestDto();
                    dto.setId(v.getId());
                    dto.setSku(v.getSku());
                    dto.setSize(v.getSize());
                    dto.setColor(v.getColor());
                    dto.setPriceBase(v.getPriceBase());
                    dto.setPriceSale(v.getPriceSale());
                    dto.setStockQuantity(v.getStockQuantity());
                    dto.setReservedQuantity(
                            v.getReservedQuantity() != null ? v.getReservedQuantity() : 0
                    );
                    dto.setAvailableStock(
                            Math.max(0, v.getStockQuantity() - dto.getReservedQuantity())
                    );
//                dto.setImageUrl(v.getImageUrl());
                    return dto;
                }).collect(Collectors.toList());

        return AdminProductDetailDto.builder()
                .id(product.getId())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .isActive(product.getIsActive())
                .materialId(product.getMaterial() != null ? product.getMaterial().getId() : null)
                .shoeSoleId(product.getShoeSole() != null ? product.getShoeSole().getId() : null)
                .categories(categoryDtos)
                .variants(variantDtos)
//                .mainImageUrl(product.getMainImageUrl())
                .build();
    }

    /**
     * Convert Set<Category> to Set<CategoryDto>
     */
    private Set<CategoryDto> convertCategoriesToDto(Set<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return new HashSet<>();
        }
        return categories.stream()
                .map(c -> new CategoryDto(
                        c.getId(),
                        c.getName(),
                        c.getSlug(),
                        c.getParent() != null ? c.getParent().getId() : null
                ))
                .collect(Collectors.toSet());
    }

    public Page<ProductCardDto> getAllProductsForCard(int page, int size, String search) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách Product từ repo
        Page<Product> products = productRepository.searchProducts(search, pageable);

        // CHUYỂN sang ProductCardDto để FE dùng
        return products.map(this::convertToProductCardDto);
    }

    public AdminProductDetailDto getProductBySlugForPublic(String slug) {
        Product product = productRepository.findBySlugAndIsActiveTrueAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy sản phẩm"
                ));

        return convertToAdminDetailDto(product);
    }

    public List<ProductCardDto> getRelatedProducts(Long productId, Long brandId, List<Long> categoryIds, int limit) {
        int max = limit;

        // 1) Lấy theo BRAND
        List<Product> brandProducts = productRepository
                .findTop4ByBrandIdAndIdNotAndIsActiveTrueAndDeletedAtIsNull(brandId, productId);

        List<ProductCardDto> result = brandProducts.stream()
                .map(this::convertToProductCardDto)
                .collect(Collectors.toList());

        // Nếu đủ 4 thì return luôn
        if (result.size() >= max) {
            return result.subList(0, max);
        }

        // 2) Lấy theo CATEGORY, tránh trùng brand
        List<Product> categoryProducts = productRepository.findByCategories(productId, categoryIds);

        for (Product p : categoryProducts) {
            if (result.size() >= max) break;

            // Loại sản phẩm cùng brand với main brand (để ưu tiên brand trước)
            if (!p.getBrand().getId().equals(brandId)) {
                result.add(convertToProductCardDto(p));
            }
        }

        return result;
    }

    public Page<ProductCardDto> searchProductsAdvanced(
            String brand,
            String category,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.searchAdvanced(
                brand,
                category,
                pageable
        );

        return products.map(this::convertToProductCardDto);
    }
}