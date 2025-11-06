package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.*;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.*;
import com.sneakery.store.util.CodeGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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



    /**
 * API 1: Tạo sản phẩm mới
 */
    @Transactional
    public AdminProductDetailDto createProduct(AdminProductRequestDto requestDto) {
        // ✅ VALIDATION: Business rules
        productValidationUtil.validateSlugUniqueness(requestDto.getSlug(), null);
        productValidationUtil.validateProductNameUniqueness(requestDto.getName(), requestDto.getBrandId(), null);
        productValidationUtil.validateSkuUniqueness(requestDto.getVariants(), null);
        productValidationUtil.validateVariantPrices(requestDto.getVariants());
        
        // 1️⃣ Lấy Brand
        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại"));

        // 2️⃣ Lấy Categories
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại: " + id)))
                .collect(Collectors.toSet());

        // 3️⃣ Lấy Material (nếu có)
        Material material = null;
        if (requestDto.getMaterialId() != null) {
            material = materialRepository.findById(requestDto.getMaterialId())
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Chất liệu không tồn tại"));
        }

        // 4️⃣ Lấy Shoe Sole (nếu có)
        ShoeSole shoeSole = null;
        if (requestDto.getShoeSoleId() != null) {
            shoeSole = shoeSoleRepository.findById(requestDto.getShoeSoleId())
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
        Long lastId = productRepository.findMaxId();
        String newCode = codeGenerator.generateProductCode(lastId);
        product.setCode(newCode);

        // 7️⃣ Map Variants
        List<ProductVariant> variants = requestDto.getVariants().stream()
                .map(dto -> convertVariantDtoToEntity(dto, product))
                .collect(Collectors.toList());
        product.setVariants(variants);

        // 8️⃣ Lưu sản phẩm (cascade variants)
        Product savedProduct = productRepository.save(product);

        // 9️⃣ Trả về DTO chi tiết
        return convertToAdminDetailDto(savedProduct);
    }


    /**
     * API 2: Cập nhật sản phẩm
     */
    @Transactional
    public AdminProductDetailDto updateProduct(Long productId, AdminProductRequestDto requestDto) {
        // 1️⃣ Tìm Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        // ✅ VALIDATION: Business rules
        productValidationUtil.validateSlugUniqueness(requestDto.getSlug(), productId);
        productValidationUtil.validateProductNameUniqueness(requestDto.getName(), requestDto.getBrandId(), productId);
        productValidationUtil.validateSkuUniqueness(requestDto.getVariants(), productId);
        productValidationUtil.validateVariantPrices(requestDto.getVariants());

        // 2️⃣ Lấy Brand
        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại"));

        // 3️⃣ Lấy Categories
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại: " + id)))
                .collect(Collectors.toSet());

        // 4️⃣ Lấy Material (nếu có)
        Material material = null;
        if (requestDto.getMaterialId() != null) {
            material = materialRepository.findById(requestDto.getMaterialId())
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Chất liệu không tồn tại"));
        }

        // 5️⃣ Lấy Shoe Sole (nếu có)
        ShoeSole shoeSole = null;
        if (requestDto.getShoeSoleId() != null) {
            shoeSole = shoeSoleRepository.findById(requestDto.getShoeSoleId())
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

        // 7️⃣ Cập nhật variants
        updateProductVariants(product, requestDto.getVariants());

        // 8️⃣ Lưu lại
        Product updatedProduct = productRepository.save(product);
        return convertToAdminDetailDto(updatedProduct);
    }


    /**
     * API 3: Lấy 1 sản phẩm (cho trang Edit)
     */
    @Transactional(readOnly = true)
    public AdminProductDetailDto getProductByIdForAdmin(Long productId) {
        Product product = productRepository.findByIdWithDetails(productId) // Dùng query tối ưu
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));
        return convertToAdminDetailDto(product);
    }

    /**
     * API 4: Lấy danh sách (phân trang)
     */
    /**
 * API 4: Lấy danh sách sản phẩm (Admin, có Brand + Categories)
 */
@Transactional(readOnly = true)
public Page<AdminProductListDto> getAllProductsForAdmin(Pageable pageable) {
    // 1️⃣ Bước 1: Lấy Page cơ bản (chỉ ID)
    Page<Product> page = productRepository.findAll(pageable);

    if (page.isEmpty()) {
        return Page.empty(pageable);
    }

    // 2️⃣ Bước 2: Lấy danh sách ID trong trang hiện tại
    List<Long> ids = page.getContent().stream()
            .map(Product::getId)
            .toList();

    // 3️⃣ Bước 3: Fetch join Brand + Categories cho đúng các ID đó
    List<Product> fullProducts = productRepository.findByIdInWithBrandAndCategories(ids);

    // 4️⃣ Bước 4: Convert sang DTO
    List<AdminProductListDto> dtoList = fullProducts.stream()
            .map(this::convertToListDto)
            .toList();

    // 5️⃣ Bước 5: Tạo PageImpl để giữ nguyên thông tin phân trang
    return new PageImpl<>(dtoList, pageable, page.getTotalElements());
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
    return AdminProductListDto.builder()
            .id(product.getId())
            .code(product.getCode()) // 🆕 Thêm dòng này để hiển thị mã sản phẩm
            .name(product.getName())
            .slug(product.getSlug())
            .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
            .brandName(product.getBrand() != null ? product.getBrand().getName() : "N/A")
            .isActive(product.getIsActive())
            .variantCount(product.getVariants() != null ? product.getVariants().size() : 0)
            .categories(categoryDtos)
            .materialId(product.getMaterial() != null ? product.getMaterial().getId() : null)
            .shoeSoleId(product.getShoeSole() != null ? product.getShoeSole().getId() : null)
            .build();
}


    /**
     * API 5: Xóa sản phẩm
     */
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm");
        }
        productRepository.deleteById(productId); // Sẽ cascade-delete variants
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
            variantRepository.delete(v);
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
                existing.setImageUrl(dto.getImageUrl());
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
                newVariant.setImageUrl(dto.getImageUrl());
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
        variant.setImageUrl(dto.getImageUrl());

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
                dto.setImageUrl(v.getImageUrl());
                return dto;
            }).collect(Collectors.toList());

        return AdminProductDetailDto.builder()
                .id(product.getId())
                .brandId(product.getBrand().getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .isActive(product.getIsActive())
                .materialId(product.getMaterial() != null ? product.getMaterial().getId() : null)
                .shoeSoleId(product.getShoeSole() != null ? product.getShoeSole().getId() : null)
                .categories(categoryDtos)
                .variants(variantDtos)
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
                variant.setImageUrl(dto.getImageUrl());

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
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm ID: " + productId));

                switch (request.getAction()) {
                    case "UPDATE_STATUS":
                        product.setIsActive(request.getIsActive());
                        break;
                        
                    case "UPDATE_BRAND":
                        Brand brand = brandRepository.findById(request.getBrandId())
                                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại"));
                        product.setBrand(brand);
                        break;
                        
                    case "ADD_CATEGORY":
                        Category categoryToAdd = categoryRepository.findById(request.getCategoryId())
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

        return new PageImpl<>(dtos, pageable, total);
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
                    newVariant.setImageUrl(v.getImageUrl());
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

        // Search
        if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
            String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(product.get("name")), searchPattern),
                    cb.like(cb.lower(product.get("slug")), searchPattern)
            ));
        }

        // Brand
        if (filter.getBrandId() != null) {
            predicates.add(cb.equal(brand.get("id"), filter.getBrandId()));
        }

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