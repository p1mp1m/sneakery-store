package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.*;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.*;
import com.sneakery.store.util.CodeGenerator;
import com.sneakery.store.util.PriceRangeConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service quản lý sản phẩm cho Admin
 *
 * <p>Service này cung cấp các chức năng quản lý sản phẩm cho admin:
 * <ul>
 *   <li>Tạo, đọc, cập nhật, xóa sản phẩm</li>
 *   <li>Tìm kiếm và lọc sản phẩm nâng cao (theo brand, category, giá, v.v.)</li>
 *   <li>Quản lý biến thể sản phẩm (variants) - size, màu sắc, giá</li>
 *   <li>Cache dữ liệu sản phẩm để tối ưu hiệu năng</li>
 * </ul>
 *
 * <p><b>Về Caching:</b>
 * <ul>
 *   <li>Dữ liệu sản phẩm được cache để giảm tải database</li>
 *   <li>Cache tự động bị xóa khi có sản phẩm mới được tạo, cập nhật, hoặc xóa</li>
 *   <li>Cache key: ID của sản phẩm (ví dụ: "products::1")</li>
 * </ul>
 *
 * <p><b>Về Sản phẩm và Biến thể:</b>
 * <ul>
 *   <li>Một sản phẩm có thể có nhiều biến thể (variants)</li>
 *   <li>Ví dụ: Sản phẩm "Nike Air Max" có các biến thể: Size 40 - Đỏ, Size 41 - Đỏ, Size 40 - Xanh</li>
 *   <li>Mỗi biến thể có: SKU, size, màu sắc, giá, số lượng tồn kho</li>
 * </ul>
 *
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Tạo sản phẩm mới với các biến thể
 * AdminProductRequestDto request = new AdminProductRequestDto();
 * request.setName("Nike Air Max");
 * request.setBrandId(1);
 * request.setCategoryIds(Arrays.asList(1, 2));
 * // ... thêm các biến thể
 * AdminProductDetailDto created = adminProductService.createProduct(request);
 *
 * // Lấy sản phẩm theo ID (có cache)
 * AdminProductDetailDto product = adminProductService.getProductByIdForAdmin(1L);
 * </pre>
 *
 * @author Sneakery Store Team
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;
    private final EntityManager entityManager;
    private final CodeGenerator codeGenerator;
    private final MaterialRepository materialRepository;
    private final ShoeSoleRepository shoeSoleRepository;
    private final com.sneakery.store.util.ProductValidationUtil productValidationUtil;
    private final ProductImageRepository productImageRepository;
    private final ActivityLogService activityLogService;



    /**
     * Tạo sản phẩm mới
     *
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Validate các business rules (slug unique, tên sản phẩm unique, SKU unique, giá hợp lệ)</li>
     *   <li>Lấy thương hiệu (Brand) từ database</li>
     *   <li>Lấy các danh mục (Categories) từ database</li>
     *   <li>Lấy chất liệu (Material) và loại đế giày (ShoeSole) nếu có</li>
     *   <li>Tạo sản phẩm mới với mã sản phẩm tự động</li>
     *   <li>Tạo các biến thể (variants) của sản phẩm</li>
     *   <li>Lưu vào database (cascade variants)</li>
     *   <li>Xóa tất cả cache của sản phẩm</li>
     * </ol>
     *
     * <p><b>Lưu ý:</b>
     * <ul>
     *   <li>Mã sản phẩm (code) sẽ được tự động sinh dựa trên ID cuối cùng</li>
     *   <li>Nếu validation thất bại, sẽ throw ApiException với thông báo lỗi cụ thể</li>
     *   <li>Sau khi tạo thành công, cache sẽ bị xóa để đảm bảo dữ liệu mới nhất</li>
     * </ul>
     *
     * @param requestDto DTO chứa thông tin sản phẩm cần tạo:
     *                   - Tên, slug, mô tả
     *                   - Brand ID, Category IDs
     *                   - Material ID, ShoeSole ID (tùy chọn)
     *                   - Danh sách variants (SKU, size, màu, giá, số lượng)
     * @return AdminProductDetailDto của sản phẩm vừa tạo (bao gồm tất cả variants)
     * @throws ApiException nếu validation thất bại hoặc không tìm thấy Brand/Category
     *
     * @example
     * <pre>
     * AdminProductRequestDto request = new AdminProductRequestDto();
     * request.setName("Nike Air Max 90");
     * request.setSlug("nike-air-max-90");
     * request.setBrandId(1); // Nike
     * request.setCategoryIds(Arrays.asList(1, 2)); // Giày thể thao, Giày chạy bộ
     *
     * // Thêm biến thể
     * ProductVariantRequestDto variant1 = new ProductVariantRequestDto();
     * variant1.setSku("NIKE-AM90-40-RED");
     * variant1.setSize(40);
     * variant1.setColor("Đỏ");
     * variant1.setPrice(new BigDecimal("2500000"));
     * variant1.setStockQuantity(100);
     * request.setVariants(Arrays.asList(variant1));
     *
     * AdminProductDetailDto created = adminProductService.createProduct(request);
     * </pre>
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public AdminProductDetailDto createProduct(AdminProductRequestDto requestDto) {
        // ✅ VALIDATION: Business rules
        productValidationUtil.validateSlugUniqueness(requestDto.getSlug(), null);
        productValidationUtil.validateProductNameUniqueness(requestDto.getName(), requestDto.getBrandId(), null);
        productValidationUtil.validateSkuUniqueness(requestDto.getVariants(), null);
        productValidationUtil.validateVariantPrices(requestDto.getVariants());

        // 1️⃣ Lấy Brand
        Brand brand = brandRepository.findById(Objects.requireNonNull(requestDto.getBrandId()))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại"));

        // 2️⃣ Lấy Categories
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(Objects.requireNonNull(id))
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại: " + id)))
                .collect(Collectors.toSet());

        // 3️⃣ Lấy Material (nếu có)
        Material material = null;
        if (requestDto.getMaterialId() != null) {
            material = materialRepository.findById(Objects.requireNonNull(requestDto.getMaterialId()))
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Chất liệu không tồn tại"));
        }

        // 4️⃣ Lấy Shoe Sole (nếu có)
        ShoeSole shoeSole = null;
        if (requestDto.getShoeSoleId() != null) {
            shoeSole = shoeSoleRepository.findById(Objects.requireNonNull(requestDto.getShoeSoleId()))
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Loại đế giày không tồn tại"));
        }

        // 5️⃣ Khởi tạo Product
        Product product = new Product();
        product.setName(requestDto.getName());
        product.setSlug(requestDto.getSlug());
        product.setDescription(requestDto.getDescription());
        product.setIsActive(requestDto.getIsActive());
        product.setBrand(brand);
        product.setCategories(categories);
        product.setMaterial(material);
        product.setShoeSole(shoeSole);

        // 6️⃣ Sinh mã sản phẩm tự động
//        Long lastId = productRepository.findMaxId();
//        String newCode = codeGenerator.generateProductCode(lastId);
//        product.setCode(newCode);
        // 6️⃣ Sinh mã sản phẩm tự động theo BRAND
        Integer lastNumber = productRepository.findLastNumberByBrand(brand.getId());
        String newCode = codeGenerator.generateProductCodeByBrand(brand.getName(), lastNumber);
        product.setCode(newCode);


        // 7️⃣ Map Variants
        List<ProductVariant> variants = requestDto.getVariants().stream()
                .map(dto -> convertVariantDtoToEntity(dto, product))
                .collect(Collectors.toList());
        product.setVariants(variants);

        // 8️⃣ Lưu sản phẩm (cascade variants)
        Product savedProduct = productRepository.save(product);

        // 9️⃣ Log activity (audit trail)
        try {
            AdminProductDetailDto savedDto = convertToAdminDetailDto(savedProduct);
            activityLogService.logCreate("Product", savedProduct.getId(), savedDto);
        } catch (Exception e) {
            log.warn("Failed to log product creation: {}", e.getMessage());
        }

        // 🔟 Trả về DTO chi tiết
        return convertToAdminDetailDto(savedProduct);
    }


    /**
     * Cập nhật thông tin sản phẩm
     *
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Tìm sản phẩm theo ID</li>
     *   <li>Validate các business rules (slug unique, tên sản phẩm unique, SKU unique, giá hợp lệ)</li>
     *   <li>Lấy thương hiệu (Brand) và các danh mục (Categories) mới từ database</li>
     *   <li>Cập nhật thông tin sản phẩm (tên, slug, mô tả, brand, categories, v.v.)</li>
     *   <li>Cập nhật các biến thể (variants) - thêm mới, cập nhật, hoặc xóa</li>
     *   <li>Lưu vào database</li>
     *   <li>Xóa cache của sản phẩm này</li>
     * </ol>
     *
     * <p><b>Về cập nhật variants:</b>
     * <ul>
     *   <li>Nếu variant có ID: Cập nhật variant hiện có</li>
     *   <li>Nếu variant không có ID: Tạo variant mới</li>
     *   <li>Nếu variant hiện có không có trong danh sách mới: Xóa variant đó</li>
     * </ul>
     *
     * <p><b>Lưu ý:</b> Sau khi cập nhật thành công, cache của sản phẩm này sẽ bị xóa
     * để đảm bảo dữ liệu mới nhất.
     *
     * @param productId ID của sản phẩm cần cập nhật
     * @param requestDto DTO chứa thông tin mới của sản phẩm (tương tự như createProduct)
     * @return AdminProductDetailDto của sản phẩm sau khi cập nhật
     * @throws ApiException nếu không tìm thấy sản phẩm hoặc validation thất bại
     *
     * @example
     * <pre>
     * AdminProductRequestDto updateData = new AdminProductRequestDto();
     * updateData.setName("Nike Air Max 90 Updated");
     * updateData.setPrice(new BigDecimal("2600000")); // Cập nhật giá
     * // ... các thông tin khác
     *
     * AdminProductDetailDto updated = adminProductService.updateProduct(1L, updateData);
     * </pre>
     */
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public AdminProductDetailDto updateProduct(Long productId, AdminProductRequestDto requestDto) {
        // 1️⃣ Tìm Product
        Product product = productRepository.findById(Objects.requireNonNull(productId))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        // ✅ VALIDATION: Business rules
        productValidationUtil.validateSlugUniqueness(requestDto.getSlug(), productId);
        productValidationUtil.validateProductNameUniqueness(requestDto.getName(), requestDto.getBrandId(), productId);
        productValidationUtil.validateSkuUniqueness(requestDto.getVariants(), productId);
        productValidationUtil.validateVariantPrices(requestDto.getVariants());

        // 2️⃣ Lấy Brand
        Brand brand = brandRepository.findById(Objects.requireNonNull(requestDto.getBrandId()))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại"));

        // 3️⃣ Lấy Categories
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(Objects.requireNonNull(id))
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại: " + id)))
                .collect(Collectors.toSet());

        // 4️⃣ Lấy Material (nếu có)
        Material material = null;
        if (requestDto.getMaterialId() != null) {
            material = materialRepository.findById(Objects.requireNonNull(requestDto.getMaterialId()))
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Chất liệu không tồn tại"));
        }

        // 5️⃣ Lấy Shoe Sole (nếu có)
        ShoeSole shoeSole = null;
        if (requestDto.getShoeSoleId() != null) {
            shoeSole = shoeSoleRepository.findById(Objects.requireNonNull(requestDto.getShoeSoleId()))
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Loại đế giày không tồn tại"));
        }

        // 6️⃣ Cập nhật thông tin
        product.setName(requestDto.getName());
        product.setSlug(requestDto.getSlug());
        product.setDescription(requestDto.getDescription());
        product.setIsActive(requestDto.getIsActive());
        product.setBrand(brand);
        product.setCategories(categories);
        product.setMaterial(material);
        product.setShoeSole(shoeSole);
//        product.setMainImageUrl(requestDto.getMainImageUrl());

        // 7️⃣ Cập nhật variants
        updateProductVariants(product, requestDto.getVariants());

        // 8️⃣ Lưu lại
        Product updatedProduct = productRepository.save(product);

        // 9️⃣ Log activity (audit trail)
        try {
            AdminProductDetailDto oldDto = convertToAdminDetailDto(product);
            AdminProductDetailDto newDto = convertToAdminDetailDto(updatedProduct);
            activityLogService.logUpdate("Product", productId, oldDto, newDto);
        } catch (Exception e) {
            log.warn("Failed to log product update: {}", e.getMessage());
        }

        return convertToAdminDetailDto(updatedProduct);
    }


    /**
     * Lấy thông tin chi tiết sản phẩm theo ID (cho trang Edit)
     *
     * <p>Phương thức này sử dụng cache để tối ưu hiệu năng:
     * <ul>
     *   <li>Lần đầu tiên: Lấy từ database và lưu vào cache</li>
     *   <li>Lần sau: Lấy trực tiếp từ cache (nhanh hơn)</li>
     *   <li>Cache key: ID của sản phẩm (ví dụ: "products::1")</li>
     * </ul>
     *
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Bao gồm tất cả thông tin sản phẩm: tên, mô tả, brand, categories, variants</li>
     *   <li>Bao gồm tất cả biến thể (variants) với đầy đủ thông tin: SKU, size, màu, giá, số lượng</li>
     *   <li>Sử dụng query tối ưu (findByIdWithDetails) để load tất cả dữ liệu trong 1 lần</li>
     * </ul>
     *
     * @param productId ID của sản phẩm cần lấy
     * @return AdminProductDetailDto chứa thông tin chi tiết sản phẩm (bao gồm tất cả variants)
     * @throws ApiException nếu không tìm thấy sản phẩm với ID này
     *
     * @example
     * <pre>
     * // Lấy sản phẩm có ID = 1
     * AdminProductDetailDto product = adminProductService.getProductByIdForAdmin(1L);
     * System.out.println(product.getName()); // "Nike Air Max 90"
     * System.out.println(product.getVariants().size()); // Số lượng biến thể
     * </pre>
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#productId")
    public AdminProductDetailDto getProductByIdForAdmin(Long productId) {
        Product product = productRepository.findByIdWithDetails(productId) // Dùng query tối ưu
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));
        return convertToAdminDetailDto(product);
    }

    /**
     * Lấy danh sách sản phẩm với phân trang (cho Admin)
     *
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy danh sách sản phẩm từ database với phân trang</li>
     *   <li>Bao gồm thông tin Brand và Categories (sử dụng query tối ưu)</li>
     *   <li>Loại trừ các sản phẩm đã bị xóa mềm (soft-deleted)</li>
     *   <li>Chuyển đổi sang DTO để trả về</li>
     * </ol>
     *
     * <p><b>Về phân trang:</b>
     * <ul>
     *   <li>Sử dụng Spring Data Pageable để phân trang</li>
     *   <li>Mặc định: page = 0, size = 20 (có thể tùy chỉnh)</li>
     *   <li>Trả về Page chứa: danh sách sản phẩm, tổng số trang, tổng số phần tử</li>
     * </ul>
     *
     * <p><b>Về dữ liệu trả về:</b>
     * <ul>
     *   <li>Mỗi sản phẩm bao gồm: ID, tên, slug, brand, categories, giá thấp nhất, giá cao nhất</li>
     *   <li>Không bao gồm chi tiết variants (chỉ dùng cho danh sách)</li>
     * </ul>
     *
     * @param pageable Thông tin phân trang (page, size, sort)
     * @return Page chứa danh sách AdminProductListDto
     *
     * @example
     * <pre>
     * // Lấy trang đầu tiên, mỗi trang 20 sản phẩm
     * Pageable pageable = PageRequest.of(0, 20);
     * Page&lt;AdminProductListDto&gt; products = adminProductService.getAllProductsForAdmin(pageable);
     *
     * System.out.println("Tổng số sản phẩm: " + products.getTotalElements());
     * System.out.println("Tổng số trang: " + products.getTotalPages());
     * products.getContent().forEach(p -&gt; System.out.println(p.getName()));
     * </pre>
     */
    @Transactional(readOnly = true)
    public Page<AdminProductListDto> getAllProductsForAdmin(Pageable pageable) {
        // Use the optimized query that already excludes soft-deleted products
        Page<Product> page = productRepository.findAllWithDetails(Objects.requireNonNull(pageable));

        if (page.isEmpty()) {
            return Page.empty(Objects.requireNonNull(pageable));
        }

        // Convert to DTO
        List<AdminProductListDto> dtoList = page.getContent().stream()
                .map(this::convertToListDto)
                .toList();

        return new PageImpl<>(Objects.requireNonNull(dtoList), Objects.requireNonNull(pageable), page.getTotalElements());
    }


    /**
     * Helper method: Convert Product Entity sang AdminProductListDto
     */
    private AdminProductListDto convertToListDto(Product product) {
        // Lấy danh sách category (nếu có)
        List<SimpleCategoryDto> categoryDtos = product.getCategories() != null
                ? product.getCategories().stream()
                .map(cat -> new SimpleCategoryDto(cat.getId(), cat.getName()))
                .toList()
                : List.of();
        // ✅ Tính tổng tồn kho (sum của tất cả stockQuantity)
        int totalStock = 0;
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            totalStock = product.getVariants().stream()
                    .mapToInt(v -> Optional.ofNullable(v.getStockQuantity()).orElse(0))
                    .sum();
        }

        // 🆕 Tính giá min/max từ các biến thể (variants)
        // Logic: Lấy giá thực tế (priceSale nếu có, không thì priceBase) từ tất cả variants
        Integer priceFrom = null;
        Integer priceTo = null;
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            java.util.List<Integer> prices = product.getVariants().stream()
                    .map(variant -> {
                        // Ưu tiên dùng priceSale nếu có, không thì dùng priceBase
                        if (variant.getPriceSale() != null && variant.getPriceSale().compareTo(java.math.BigDecimal.ZERO) > 0) {
                            return variant.getPriceSale().intValue();
                        } else if (variant.getPriceBase() != null) {
                            return variant.getPriceBase().intValue();
                        }
                        return null;
                    })
                    .filter(price -> price != null && price > 0)
                    .collect(java.util.stream.Collectors.toList());

            if (!prices.isEmpty()) {
                priceFrom = prices.stream().mapToInt(Integer::intValue).min().orElse(0);
                priceTo = prices.stream().mapToInt(Integer::intValue).max().orElse(0);
            }
        }

        return AdminProductListDto.builder()
                .id(product.getId())
                .code(product.getCode()) // 🆕 Thêm dòng này để hiển thị mã sản phẩm
                .name(product.getName())
                .slug(product.getSlug())
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : "N/A")
                .isActive(product.getIsActive())
                .variantCount(product.getVariants() != null ? product.getVariants().size() : 0)
                .totalStock(totalStock)
                .categories(categoryDtos)
                .materialId(product.getMaterial() != null ? product.getMaterial().getId() : null)
                .shoeSoleId(product.getShoeSole() != null ? product.getShoeSole().getId() : null)
                .priceFrom(priceFrom)
                .priceTo(priceTo)
//            .mainImageUrl(product.getMainImageUrl())
                .build();
    }


    /**
     * API 5: Xóa sản phẩm (hard delete)
     *
     * <p>Thực hiện hard delete bằng cách xóa tất cả các bản ghi liên quan trước:
     * <ul>
     *   <li>1. Xóa InventoryLogs (theo variant)</li>
     *   <li>2. Xóa CartItems (theo variant) - sử dụng native query</li>
     *   <li>3. Xóa OrderDetails (theo variant) - lưu ý: có thể ảnh hưởng đến lịch sử đơn hàng</li>
     *   <li>4. Xóa ProductImages (theo product)</li>
     *   <li>5. Xóa Reviews (theo product)</li>
     *   <li>6. Xóa Wishlists (theo product)</li>
     *   <li>7. Xóa Warranties (theo product)</li>
     *   <li>8. Xóa FlashSales (theo product)</li>
     *   <li>9. Xóa ProductVariants (cascade delete hoặc manual)</li>
     *   <li>10. Xóa Product (hard delete)</li>
     * </ul>
     *
     * <p><b>Cảnh báo:</b> Hành động này sẽ xóa vĩnh viễn tất cả dữ liệu liên quan.
     */
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void deleteProduct(Long productId) {
        // Load product với variants để tránh LazyInitializationException
        Product product = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        // Save product data for audit log before deletion
        AdminProductDetailDto deletedProductDto = null;
        try {
            deletedProductDto = convertToAdminDetailDto(product);
        } catch (Exception e) {
            log.warn("Failed to convert product to DTO for audit log: {}", e.getMessage());
        }

        log.info("Bắt đầu xóa product ID: {} và tất cả dữ liệu liên quan", productId);

        // 1. Lấy danh sách variant IDs của product
        List<Long> variantIds = product.getVariants() != null
                ? product.getVariants().stream().map(ProductVariant::getId).collect(Collectors.toList())
                : Collections.emptyList();

        log.info("Tìm thấy {} variants cần xóa", variantIds.size());

        // 2. Xóa InventoryLogs (theo variant) - sử dụng native query
        if (!variantIds.isEmpty()) {
            for (Long variantId : variantIds) {
                int deletedLogs = entityManager.createNativeQuery(
                                "DELETE FROM Inventory_Logs WHERE variant_id = :variantId")
                        .setParameter("variantId", variantId)
                        .executeUpdate();
                log.info("Đã xóa {} inventory logs cho variant ID: {}", deletedLogs, variantId);
            }
        }

        // 3. Xóa CartItems (theo variant) - sử dụng native query
        if (!variantIds.isEmpty()) {
            for (Long variantId : variantIds) {
                int deletedCartItems = entityManager.createNativeQuery(
                                "DELETE FROM Cart_Items WHERE variant_id = :variantId")
                        .setParameter("variantId", variantId)
                        .executeUpdate();
                log.info("Đã xóa {} cart items cho variant ID: {}", deletedCartItems, variantId);
            }
        }

        // 4. Xóa OrderDetails (theo variant) - CẢNH BÁO: Có thể ảnh hưởng đến lịch sử đơn hàng
        // Tuy nhiên, user yêu cầu xóa hết, nên sẽ xóa
        if (!variantIds.isEmpty()) {
            for (Long variantId : variantIds) {
                int deletedOrderDetails = entityManager.createNativeQuery(
                                "DELETE FROM Order_Details WHERE variant_id = :variantId")
                        .setParameter("variantId", variantId)
                        .executeUpdate();
                log.info("Đã xóa {} order details cho variant ID: {}", deletedOrderDetails, variantId);
            }
        }

        // 5. Xóa ProductImages (theo product)
        productImageRepository.deleteByProductId(productId);
        log.info("Đã xóa tất cả product images cho product ID: {}", productId);

        // 6. Xóa Reviews (theo product) - sử dụng native query
        int deletedReviews = entityManager.createNativeQuery(
                        "DELETE FROM Reviews WHERE product_id = :productId")
                .setParameter("productId", productId)
                .executeUpdate();
        log.info("Đã xóa {} reviews cho product ID: {}", deletedReviews, productId);

        // 7. Xóa Wishlists (theo product) - sử dụng native query
        int deletedWishlists = entityManager.createNativeQuery(
                        "DELETE FROM Wishlists WHERE product_id = :productId")
                .setParameter("productId", productId)
                .executeUpdate();
        log.info("Đã xóa {} wishlist items cho product ID: {}", deletedWishlists, productId);

        // 8. Xóa Warranties (theo product) - sử dụng native query
        int deletedWarranties = entityManager.createNativeQuery(
                        "DELETE FROM Warranties WHERE product_id = :productId")
                .setParameter("productId", productId)
                .executeUpdate();
        log.info("Đã xóa {} warranties cho product ID: {}", deletedWarranties, productId);

        // 9. Xóa FlashSales (theo product) - sử dụng native query
        int deletedFlashSales = entityManager.createNativeQuery(
                        "DELETE FROM Flash_Sales WHERE product_id = :productId")
                .setParameter("productId", productId)
                .executeUpdate();
        log.info("Đã xóa {} flash sales cho product ID: {}", deletedFlashSales, productId);

        // 10. Xóa Product_Categories join table (many-to-many relationship)
        int deletedProductCategories = entityManager.createNativeQuery(
                        "DELETE FROM Product_Categories WHERE product_id = :productId")
                .setParameter("productId", productId)
                .executeUpdate();
        log.info("Đã xóa {} product-category relationships cho product ID: {}", deletedProductCategories, productId);

        // 11. Xóa ProductVariants (manual delete để đảm bảo)
        if (!variantIds.isEmpty()) {
            variantRepository.deleteAllById(variantIds);
            log.info("Đã xóa {} variants cho product ID: {}", variantIds.size(), productId);
        }

        // 12. Xóa Product (hard delete)
        productRepository.delete(product);

        // 13. Log activity (audit trail)
        try {
            if (deletedProductDto != null) {
                activityLogService.logDelete("Product", productId, deletedProductDto);
            }
        } catch (Exception e) {
            log.warn("Failed to log product deletion: {}", e.getMessage());
        }

        log.info("✅ Đã xóa thành công product ID: {}", productId);
    }


    // =================================================================
    // HÀM HELPER
    // =================================================================

    /**
     * Helper xử lý cập nhật / thêm / xóa biến thể cho sản phẩm (chuẩn enterprise)
     */
    private void updateProductVariants(Product product, List<AdminVariantRequestDto> variantDtos) {
        if (variantDtos == null) return;

        // ✅ Map các variant hiện có từ DB theo ID
        Map<Long, ProductVariant> existingMap = product.getVariants().stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        // ✅ Lưu lại ID biến thể từ DTO
        Set<Long> dtoIds = variantDtos.stream()
                .map(AdminVariantRequestDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 1️⃣ Xóa những biến thể không còn tồn tại trong DTO
        List<ProductVariant> toRemove = product.getVariants().stream()
                .filter(v -> v.getId() != null && !dtoIds.contains(v.getId()))
                .collect(Collectors.toList());
        for (ProductVariant v : toRemove) {
            product.getVariants().remove(v);
            variantRepository.delete(Objects.requireNonNull(v));
        }

        // 2️⃣ Cập nhật hoặc thêm mới các biến thể
        for (AdminVariantRequestDto dto : variantDtos) {
            if (dto.getId() != null && existingMap.containsKey(dto.getId())) {
                // 🔁 Cập nhật biến thể cũ
                ProductVariant existing = existingMap.get(dto.getId());
                existing.setSku(dto.getSku());
                existing.setSize(dto.getSize());
                existing.setColor(dto.getColor());
                existing.setPriceBase(dto.getPriceBase());
                existing.setPriceSale(dto.getPriceSale());
                existing.setStockQuantity(dto.getStockQuantity());
//                existing.setImageUrl(dto.getImageUrl());
            } else {
                // 🆕 Thêm mới (chỉ tạo nếu SKU chưa tồn tại)
                if (variantRepository.existsBySku(dto.getSku())) {
                    log.warn("⚠️ Bỏ qua SKU trùng: {}", dto.getSku());
                    continue;
                }
                ProductVariant newVariant = new ProductVariant();
                newVariant.setProduct(product);
                newVariant.setSku(dto.getSku());
                newVariant.setSize(dto.getSize());
                newVariant.setColor(dto.getColor());
                newVariant.setPriceBase(dto.getPriceBase());
                newVariant.setPriceSale(dto.getPriceSale());
                newVariant.setStockQuantity(dto.getStockQuantity());
//                newVariant.setImageUrl(dto.getImageUrl());
                product.getVariants().add(newVariant);
            }
        }
    }


    /**
     * Helper: Chuyển DTO → Entity (Tạo mới Variant)
     */
    private ProductVariant convertVariantDtoToEntity(AdminVariantRequestDto dto, Product product) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        return updateVariantEntityFromDto(variant, dto);
    }

    /**
     * Helper: Cập nhật dữ liệu từ DTO vào Entity (Dùng chung cho cả update & create)
     */
    private ProductVariant updateVariantEntityFromDto(ProductVariant variant, AdminVariantRequestDto dto) {
        if (dto == null) return variant;

        // 🧩 Đảm bảo không ghi đè ID cũ
        if (dto.getId() != null) {
            variant.setId(dto.getId());
        }

        // ⚙️ Cập nhật toàn bộ field có thể thay đổi
        variant.setSku(dto.getSku() != null ? dto.getSku().trim() : variant.getSku());
        variant.setSize(dto.getSize());
        variant.setColor(dto.getColor());
        variant.setPriceBase(dto.getPriceBase());
        variant.setPriceSale(dto.getPriceSale());
        variant.setStockQuantity(dto.getStockQuantity());
//        variant.setImageUrl(dto.getImageUrl());

        // 🟢 Giữ trạng thái isActive nếu có
        if (variant.getIsActive() == null) {
            variant.setIsActive(true);
        }

        if (variant.getCreatedAt() == null) {
            variant.setCreatedAt(LocalDateTime.now());
        }
        // 🕒 Tự động cập nhật thời gian
        variant.setUpdatedAt(LocalDateTime.now());

        return variant;
    }


    // Mapper Entity -> DTO (Chi tiết)
    private AdminProductDetailDto convertToAdminDetailDto(Product product) {
        // Convert Categories
        Set<CategoryDto> categoryDtos = product.getCategories().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName(), c.getSlug(), c.getParent() != null ? c.getParent().getId() : null))
                .collect(Collectors.toSet());

        // Convert Variants
        List<AdminVariantRequestDto> variantDtos = product.getVariants().stream()
                .map(v -> {
                    AdminVariantRequestDto dto = new AdminVariantRequestDto();
                    dto.setId(v.getId());
                    dto.setSku(v.getSku());
                    dto.setSize(v.getSize());
                    dto.setColor(v.getColor());
                    dto.setPriceBase(v.getPriceBase());
                    dto.setPriceSale(v.getPriceSale());
                    dto.setStockQuantity(v.getStockQuantity());
//                dto.setImageUrl(v.getImageUrl());
                    return dto;
                }).collect(Collectors.toList());

        // ✅ Tính tổng tồn kho
        int totalStock = 0;
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            totalStock = product.getVariants().stream()
                    .mapToInt(v -> Optional.ofNullable(v.getStockQuantity()).orElse(0))
                    .sum();
        }

        // 🆕 Convert priceRange JSON thành priceFrom/priceTo
        PriceRangeConverter.PriceRange priceRange = PriceRangeConverter.fromJsonString(product.getPriceRange());
        Integer priceFrom = priceRange != null ? priceRange.getFromAsInteger() : null;
        Integer priceTo = priceRange != null ? priceRange.getToAsInteger() : null;

        // Debug log
        if (product.getPriceRange() != null) {
            log.debug("Product ID {} - priceRange JSON: {}, parsed - from: {}, to: {}",
                    product.getId(), product.getPriceRange(), priceFrom, priceTo);
        }

        return AdminProductDetailDto.builder()
                .id(product.getId())
                .brandId(product.getBrand().getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .isActive(product.getIsActive())
                .totalStock(totalStock)
                .materialId(product.getMaterial() != null ? product.getMaterial().getId() : null)
                .shoeSoleId(product.getShoeSole() != null ? product.getShoeSole().getId() : null)
                .priceFrom(priceFrom)
                .priceTo(priceTo)
                .categories(categoryDtos)
                .variants(variantDtos)
//                .mainImageUrl(product.getMainImageUrl()) // ✅ Trả về ảnh bìa chính
                .build();
    }

    // =================================================================
    // CÁC TÍNH NĂNG NÂNG CAO MỚI
    // =================================================================

    /**
     * API 6: Import sản phẩm từ danh sách Excel
     * Mỗi item trong list là 1 dòng từ Excel
     */
    @Transactional
    public ProductImportResultDto importProducts(List<ProductImportDto> importList) {
        log.info("Bắt đầu import {} sản phẩm", importList.size());

        ProductImportResultDto result = ProductImportResultDto.builder()
                .totalRows(importList.size())
                .successCount(0)
                .errorCount(0)
                .build();

        // Map để cache brands và categories đã tìm kiếm
        Map<String, Brand> brandCache = new HashMap<>();
        Map<String, Category> categoryCache = new HashMap<>();

        // Map để nhóm variants theo product (cùng tên + brand = cùng product)
        Map<String, Product> productMap = new HashMap<>();

        for (ProductImportDto dto : importList) {
            try {
                // 1. Tìm hoặc tạo Brand
                Brand brand = brandCache.computeIfAbsent(dto.getBrandName().trim(),
                        brandName -> findOrCreateBrand(brandName));

                // 2. Tìm hoặc tạo Categories
                Set<Category> categories = parseAndFindCategories(dto.getCategories(), categoryCache);

                // 3. Tạo key để nhóm product
                String productKey = dto.getProductName().trim() + "_" + brand.getId();

                // 4. Lấy hoặc tạo Product
                Product product = productMap.computeIfAbsent(productKey, key -> {
                    Product p = new Product();
                    p.setName(dto.getProductName().trim());
                    p.setSlug(generateSlugFromName(dto.getProductSlug(), dto.getProductName()));
                    p.setDescription(dto.getDescription());
                    p.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
                    p.setBrand(brand);
                    p.setCategories(categories);
                    p.setVariants(new ArrayList<>());
                    return p;
                });

                // 5. Tạo Variant và thêm vào Product
                ProductVariant variant = new ProductVariant();
                variant.setProduct(product);
                variant.setSku(dto.getSku().trim());
                variant.setSize(dto.getSize().trim());
                variant.setColor(dto.getColor().trim());
                variant.setPriceBase(dto.getPriceBase());
                variant.setPriceSale(dto.getPriceSale());
                variant.setStockQuantity(dto.getStockQuantity());
//                variant.setImageUrl(dto.getImageUrl());

                product.getVariants().add(variant);

                result.getSuccessItems().add(dto);
                result.setSuccessCount(result.getSuccessCount() + 1);

            } catch (Exception e) {
                log.error("Lỗi import dòng {}: {}", dto.getRowNumber(), e.getMessage());
                dto.setErrorMessage(e.getMessage());
                result.getErrorItems().add(dto);
                result.setErrorCount(result.getErrorCount() + 1);
            }
        }

        // 6. Lưu tất cả products (sẽ cascade-save variants)
        productMap.values().forEach(productRepository::save);

        result.setMessage(String.format("Import hoàn tất: %d thành công, %d lỗi",
                result.getSuccessCount(), result.getErrorCount()));

        log.info("Kết thúc import: {}", result.getMessage());
        return result;
    }

    /**
     * API 7: Bulk Update sản phẩm
     */
    @Transactional
    public ProductBulkUpdateResultDto bulkUpdateProducts(ProductBulkUpdateRequestDto request) {
        log.info("Bulk update {} sản phẩm với action: {}", request.getProductIds().size(), request.getAction());

        ProductBulkUpdateResultDto result = ProductBulkUpdateResultDto.builder()
                .totalRequested(request.getProductIds().size())
                .successCount(0)
                .errorCount(0)
                .build();

        for (Long productId : request.getProductIds()) {
            try {
                Product product = productRepository.findById(Objects.requireNonNull(productId))
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm ID: " + productId));

                switch (request.getAction()) {
                    case "UPDATE_STATUS":
                        product.setIsActive(request.getIsActive());
                        break;

                    case "UPDATE_BRAND":
                        Brand brand = brandRepository.findById(Objects.requireNonNull(request.getBrandId()))
                                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại"));
                        product.setBrand(brand);
                        break;

                    case "ADD_CATEGORY":
                        Category categoryToAdd = categoryRepository.findById(Objects.requireNonNull(request.getCategoryId()))
                                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại"));
                        product.getCategories().add(categoryToAdd);
                        break;

                    case "REMOVE_CATEGORY":
                        product.getCategories().removeIf(cat -> cat.getId().equals(request.getCategoryId()));
                        break;

                    default:
                        throw new ApiException(HttpStatus.BAD_REQUEST, "Action không hợp lệ: " + request.getAction());
                }

                productRepository.save(product);
                result.getSuccessIds().add(productId);
                result.setSuccessCount(result.getSuccessCount() + 1);

            } catch (Exception e) {
                log.error("Lỗi bulk update product {}: {}", productId, e.getMessage());
                result.getErrorIds().add(productId);
                result.setErrorCount(result.getErrorCount() + 1);
            }
        }

        result.setMessage(String.format("Bulk update hoàn tất: %d thành công, %d lỗi",
                result.getSuccessCount(), result.getErrorCount()));

        return result;
    }

    /**
     * API 8: Advanced Filter với Criteria API
     */
    @Transactional(readOnly = true)
    public Page<AdminProductListDto> getProductsWithAdvancedFilter(ProductAdvancedFilterDto filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Query cho data
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        // Join với các bảng liên quan
        Join<Product, Brand> brand = product.join("brand", JoinType.LEFT);
        Join<Product, ProductVariant> variant = product.join("variants", JoinType.LEFT);

        List<Predicate> predicates = buildFilterPredicates(cb, product, brand, variant, filter);

        query.select(product).distinct(true).where(predicates.toArray(new Predicate[0]));

        // Thêm sorting
        if (filter.getSortBy() != null) {
            query.orderBy(buildSortOrder(cb, product, variant, filter));
        }

        // Execute query
        List<Product> products = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        Join<Product, Brand> countBrand = countRoot.join("brand", JoinType.LEFT);
        Join<Product, ProductVariant> countVariant = countRoot.join("variants", JoinType.LEFT);

        List<Predicate> countPredicates = buildFilterPredicates(cb, countRoot, countBrand, countVariant, filter);
        countQuery.select(cb.countDistinct(countRoot)).where(countPredicates.toArray(new Predicate[0]));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        // Convert to DTO
        List<AdminProductListDto> dtos = products.stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());

        return new PageImpl<>(Objects.requireNonNull(dtos), pageable, total);
    }

    /**
     * API 9: Duplicate (nhân bản) sản phẩm
     */
    @Transactional
    public AdminProductDetailDto duplicateProduct(Long productId) {
        // 1. Tìm product gốc
        Product original = productRepository.findByIdWithDetails(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        // 2. Tạo product mới (copy)
        Product duplicate = new Product();
        duplicate.setName(original.getName() + com.sneakery.store.constants.ProductConstants.DUPLICATE_NAME_SUFFIX);
        duplicate.setSlug(original.getSlug() + "-copy-" + System.currentTimeMillis());
        duplicate.setDescription(original.getDescription());
        duplicate.setIsActive(false); // Mặc định tắt để admin kiểm tra trước
        duplicate.setBrand(original.getBrand());
        duplicate.setCategories(new HashSet<>(original.getCategories()));

        // 3. Copy variants
        List<ProductVariant> duplicateVariants = original.getVariants().stream()
                .map(v -> {
                    ProductVariant newVariant = new ProductVariant();
                    newVariant.setProduct(duplicate);
                    newVariant.setSku(v.getSku() + com.sneakery.store.constants.ProductConstants.DUPLICATE_SKU_SUFFIX);
                    newVariant.setSize(v.getSize());
                    newVariant.setColor(v.getColor());
                    newVariant.setPriceBase(v.getPriceBase());
                    newVariant.setPriceSale(v.getPriceSale());
                    newVariant.setStockQuantity(0); // Đặt về 0 để admin cập nhật
//                    newVariant.setImageUrl(v.getImageUrl());
                    return newVariant;
                })
                .collect(Collectors.toList());

        duplicate.setVariants(duplicateVariants);

        // 4. Lưu
        Product saved = productRepository.save(duplicate);
        return convertToAdminDetailDto(saved);
    }

    /**
     * API 10: Lấy thống kê sản phẩm (OPTIMIZED - sử dụng aggregation queries)
     *
     * Performance improvement: Thay vì load tất cả data vào memory,
     * sử dụng aggregation queries để tính toán trực tiếp trên database.
     */
    @Transactional(readOnly = true)
    public ProductStatsDto getProductStatistics() {
        // Count queries - không load data
        Long totalProducts = productRepository.count();
        Long totalVariants = variantRepository.count();

        // Đếm active/inactive với aggregation queries
        Long activeProducts = productRepository.countActiveProducts();
        Long inactiveProducts = productRepository.countInactiveProducts();

        // Tồn kho - sử dụng aggregation queries
        Long totalStock = variantRepository.sumTotalStockQuantity();

        // Low stock threshold - sử dụng constant
        Long lowStockCount = variantRepository.countLowStockVariants(
                com.sneakery.store.constants.ProductConstants.LOW_STOCK_THRESHOLD);

        Long outOfStockCount = variantRepository.countOutOfStockVariants();

        // Giá - sử dụng aggregation queries
        BigDecimal avgPrice = variantRepository.calculateAveragePrice();
        BigDecimal maxPrice = variantRepository.getMaxPrice();
        BigDecimal minPrice = variantRepository.getMinPrice();

        // Count brands và categories với aggregation queries
        Long totalBrands = brandRepository.countTotalBrands();
        Long totalCategories = categoryRepository.countTotalCategories();

        return ProductStatsDto.builder()
                .totalProducts(totalProducts)
                .totalVariants(totalVariants)
                .activeProducts(activeProducts != null ? activeProducts : 0L)
                .inactiveProducts(inactiveProducts != null ? inactiveProducts : 0L)
                .totalStock(totalStock != null ? totalStock : 0L)
                .lowStockCount(lowStockCount != null ? lowStockCount : 0L)
                .outOfStockCount(outOfStockCount != null ? outOfStockCount : 0L)
                .avgPrice(avgPrice != null ? avgPrice : BigDecimal.ZERO)
                .maxPrice(maxPrice != null ? maxPrice : BigDecimal.ZERO)
                .minPrice(minPrice != null ? minPrice : BigDecimal.ZERO)
                .totalBrands(totalBrands != null ? totalBrands.intValue() : 0)
                .totalCategories(totalCategories != null ? totalCategories.intValue() : 0)
                .build();
    }

    // =================================================================
    // HELPER METHODS CHO ADVANCED FEATURES
    // =================================================================

    /**
     * Tìm hoặc tạo Brand từ tên
     */
    private Brand findOrCreateBrand(String brandName) {
        return brandRepository.findAll().stream()
                .filter(b -> b.getName().equalsIgnoreCase(brandName))
                .findFirst()
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setName(brandName);
                    newBrand.setSlug(generateSlugFromName(null, brandName));
                    return brandRepository.save(newBrand);
                });
    }

    /**
     * Parse string categories và tìm hoặc tạo mới
     */
    private Set<Category> parseAndFindCategories(String categoriesStr, Map<String, Category> cache) {
        String[] catNames = categoriesStr.split(",");
        Set<Category> categories = new HashSet<>();

        for (String catName : catNames) {
            String trimmed = catName.trim();
            Category category = cache.computeIfAbsent(trimmed, name -> {
                return categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElseGet(() -> {
                            Category newCat = new Category();
                            newCat.setName(name);
                            newCat.setSlug(generateSlugFromName(null, name));
                            return categoryRepository.save(newCat);
                        });
            });
            categories.add(category);
        }

        return categories;
    }

    /**
     * Tạo slug từ tên
     */
    private String generateSlugFromName(String slug, String name) {
        if (slug != null && !slug.trim().isEmpty()) {
            return slug.trim().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-");
        }

        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    /**
     * Build predicates cho advanced filter
     */
    private List<Predicate> buildFilterPredicates(CriteriaBuilder cb, Root<Product> product,
                                                  Join<Product, Brand> brand,
                                                  Join<Product, ProductVariant> variant,
                                                  ProductAdvancedFilterDto filter) {
        List<Predicate> predicates = new ArrayList<>();

        // Search - chỉ tìm trong tên sản phẩm
        if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
            String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(product.get("name")), searchPattern));
        }

        // Brand
        if (filter.getBrandId() != null) {
            predicates.add(cb.equal(brand.get("id"), filter.getBrandId()));
        }

        // Exclude soft deleted products
        predicates.add(cb.isNull(product.get("deletedAt")));

        // Status
        if (filter.getStatus() != null && !filter.getStatus().equals("all")) {
            predicates.add(cb.equal(product.get("isActive"), filter.getStatus().equals("active")));
        }

        // Category
        if (filter.getCategoryId() != null) {
            Join<Product, Category> categories = product.join("categories", JoinType.INNER);
            predicates.add(cb.equal(categories.get("id"), filter.getCategoryId()));
        }

        // Price range
        if (filter.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(variant.get("priceBase"), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(variant.get("priceBase"), filter.getMaxPrice()));
        }

        // Stock level
        if (filter.getStockLevel() != null && !filter.getStockLevel().equals("all")) {
            int threshold = filter.getLowStockThreshold() != null
                    ? filter.getLowStockThreshold()
                    : com.sneakery.store.constants.ProductConstants.LOW_STOCK_THRESHOLD;

            switch (filter.getStockLevel()) {
                case "in_stock":
                    predicates.add(cb.greaterThan(variant.get("stockQuantity"), threshold));
                    break;
                case "low_stock":
                    predicates.add(cb.and(
                            cb.greaterThan(variant.get("stockQuantity"), 0),
                            cb.lessThanOrEqualTo(variant.get("stockQuantity"), threshold)
                    ));
                    break;
                case "out_of_stock":
                    predicates.add(cb.equal(variant.get("stockQuantity"), 0));
                    break;
            }
        }

        return predicates;
    }

    /**
     * Build sort order
     */
    private jakarta.persistence.criteria.Order buildSortOrder(CriteriaBuilder cb, Root<Product> product,
                                                              Join<Product, ProductVariant> variant,
                                                              ProductAdvancedFilterDto filter) {
        boolean isAsc = "asc".equalsIgnoreCase(filter.getSortDirection());

        switch (filter.getSortBy()) {
            case "price":
                return isAsc ? cb.asc(variant.get("priceBase")) : cb.desc(variant.get("priceBase"));
            case "stock":
                return isAsc ? cb.asc(variant.get("stockQuantity")) : cb.desc(variant.get("stockQuantity"));
            case "name":
                return isAsc ? cb.asc(product.get("name")) : cb.desc(product.get("name"));
            default:
                return cb.desc(product.get("id"));
        }
    }
}