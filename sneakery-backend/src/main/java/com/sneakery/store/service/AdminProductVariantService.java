package com.sneakery.store.service;

import com.sneakery.store.dto.*;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.entity.ProductImage; // ✅ Added: import ProductImage
import com.sneakery.store.exception.ProductNotFoundException;
import com.sneakery.store.exception.ProductVariantNotFoundException;
import com.sneakery.store.exception.BusinessRuleException;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import com.sneakery.store.repository.ProductImageRepository; // ✅ Added: import ProductImageRepository
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository; // ✅ Added
    private final ActivityLogService activityLogService;

    private final EntityManager entityManager;

    /**
     * Lấy danh sách biến thể với filter
     */
    @Transactional(readOnly = true)
    public Page<AdminProductVariantDto> getVariantsWithFilter(ProductVariantFilterDto filter, Pageable pageable) {
        // Nếu sort theo color hoặc size, cần custom sort logic
        // Lấy tất cả data trước (không sort trong query)
        Pageable customPageable = pageable;
        if (filter.getSortBy() != null && (filter.getSortBy().equalsIgnoreCase("color") || filter.getSortBy().equalsIgnoreCase("size"))) {
            // Lấy tất cả data không sort
            customPageable = PageRequest.of(0, Integer.MAX_VALUE);
        }
        
        Page<ProductVariant> variants = productVariantRepository.findWithFilters(
                filter.getSearch(),
                filter.getColor(),
                filter.getSize(),
                filter.getProductId(),
                filter.getStockStatus(),
                customPageable
        );
        
        // Nếu sort theo color hoặc size, sort thủ công rồi paginate
        if (filter.getSortBy() != null && (filter.getSortBy().equalsIgnoreCase("color") || filter.getSortBy().equalsIgnoreCase("size"))) {
            List<ProductVariant> variantList = new ArrayList<>(variants.getContent());
            boolean isAsc = filter.getSortDirection() == null || filter.getSortDirection().equalsIgnoreCase("asc");
            
            variantList.sort((v1, v2) -> {
                if (filter.getSortBy().equalsIgnoreCase("color")) {
                    // Sort màu case-insensitive
                    String color1 = v1.getColor() != null ? v1.getColor().toLowerCase() : "";
                    String color2 = v2.getColor() != null ? v2.getColor().toLowerCase() : "";
                    int compare = color1.compareTo(color2);
                    return isAsc ? compare : -compare;
                } else if (filter.getSortBy().equalsIgnoreCase("size")) {
                    // Sort size theo số
                    try {
                        int size1 = Integer.parseInt(v1.getSize() != null ? v1.getSize() : "0");
                        int size2 = Integer.parseInt(v2.getSize() != null ? v2.getSize() : "0");
                        int compare = Integer.compare(size1, size2);
                        return isAsc ? compare : -compare;
                    } catch (NumberFormatException e) {
                        // Nếu không parse được, sort theo string
                        String size1 = v1.getSize() != null ? v1.getSize() : "";
                        String size2 = v2.getSize() != null ? v2.getSize() : "";
                        int compare = size1.compareTo(size2);
                        return isAsc ? compare : -compare;
                    }
                }
                return 0;
            });
            
            // Paginate sau khi sort
            int page = pageable.getPageNumber();
            int size = pageable.getPageSize();
            int start = page * size;
            int end = Math.min(start + size, variantList.size());
            List<ProductVariant> pagedList = start < variantList.size() ? variantList.subList(start, end) : new ArrayList<>();
            
            // Tạo Page mới với data đã sort và paginate
            return new org.springframework.data.domain.PageImpl<>(
                    Objects.requireNonNull(pagedList.stream().map(this::convertToDto).collect(java.util.stream.Collectors.toList())),
                    pageable,
                    variantList.size()
            );
        }
        
        return variants.map(this::convertToDto);
    }

    /**
     * Lấy chi tiết biến thể theo ID
     */
    @Transactional(readOnly = true)
    public AdminProductVariantDto getVariantById(Long id) {
        ProductVariant variant = productVariantRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ProductVariantNotFoundException(id));
        return convertToDto(variant);
    }

    /**
     * Tạo biến thể mới
     */
    public AdminProductVariantDto createVariant(AdminProductVariantRequestDto requestDto) {
        Product product = productRepository.findById(Objects.requireNonNull(requestDto.getProductId()))
                .orElseThrow(() -> new ProductNotFoundException(requestDto.getProductId()));

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(requestDto.getSku());
        variant.setSize(requestDto.getSize());
        variant.setColor(requestDto.getColor());
        variant.setPriceBase(requestDto.getPriceBase());
        variant.setPriceSale(requestDto.getPriceSale());
        variant.setCostPrice(requestDto.getCostPrice());
        variant.setStockQuantity(requestDto.getStockQuantity());
        variant.setLowStockThreshold(requestDto.getLowStockThreshold() != null 
            ? requestDto.getLowStockThreshold() 
            : com.sneakery.store.constants.ProductConstants.LOW_STOCK_THRESHOLD);
        variant.setWeightGrams(requestDto.getWeightGrams());
//        variant.setImageUrl(requestDto.getImageUrl());
        variant.setIsActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : true);
        variant.setCreatedAt(LocalDateTime.now());
        variant.setUpdatedAt(LocalDateTime.now());

        ProductVariant savedVariant = productVariantRepository.save(variant);
        return convertToDto(savedVariant);
    }

    /**
     * ✅ Tạo nhiều biến thể cùng lúc — tự động gộp nếu SKU trùng
     */
    public List<AdminProductVariantDto> createVariantsBatch(List<AdminProductVariantRequestDto> requestList) {
        if (requestList == null || requestList.isEmpty()) {
            throw new BusinessRuleException("Danh sách biến thể rỗng");
        }

        List<AdminProductVariantDto> resultList = new ArrayList<>();

        for (AdminProductVariantRequestDto requestDto : requestList) {
            Product product = productRepository.findById(Objects.requireNonNull(requestDto.getProductId()))
                    .orElseThrow(() -> new ProductNotFoundException(requestDto.getProductId()));

            // ✅ Kiểm tra SKU đã tồn tại chưa
            Optional<ProductVariant> existingOpt = productVariantRepository.findBySku(requestDto.getSku());

            if (existingOpt.isPresent()) {
                // 👉 Nếu đã tồn tại → Cộng dồn số lượng
                ProductVariant existing = existingOpt.get();
                // 🟢 Khai báo tại đây
                int oldStock = existing.getStockQuantity();
                int addedStock = requestDto.getStockQuantity();
                int newStock = oldStock + addedStock;
                existing.setStockQuantity(newStock);
                existing.setUpdatedAt(LocalDateTime.now());

                productVariantRepository.save(existing);

                // ✅ Ghi log cộng dồn
                String logMsg = String.format(
                        "Biến thể SKU [%s] của sản phẩm [%s] đã được cộng dồn tồn kho: %d → %d (+%d)",
                        existing.getSku(),
                        product.getName(),
                        oldStock,
                        newStock,
                        addedStock
                );
                activityLogService.logAction("UPDATE_STOCK", logMsg);

                resultList.add(convertToDto(existing));
            } else {
                // 👉 Nếu chưa tồn tại → Tạo mới
                ProductVariant variant = new ProductVariant();
                variant.setProduct(product);
                variant.setSku(requestDto.getSku());
                variant.setSize(requestDto.getSize());
                variant.setColor(requestDto.getColor());
                variant.setPriceBase(requestDto.getPriceBase());
                variant.setPriceSale(requestDto.getPriceSale());
                variant.setCostPrice(requestDto.getCostPrice());
                variant.setStockQuantity(requestDto.getStockQuantity());
                variant.setLowStockThreshold(requestDto.getLowStockThreshold() != null 
            ? requestDto.getLowStockThreshold() 
            : com.sneakery.store.constants.ProductConstants.LOW_STOCK_THRESHOLD);
                variant.setWeightGrams(requestDto.getWeightGrams());
//                variant.setImageUrl(requestDto.getImageUrl());
                variant.setIsActive(requestDto.getIsActive() != null ? requestDto.getIsActive() : true);
                variant.setCreatedAt(LocalDateTime.now());
                variant.setUpdatedAt(LocalDateTime.now());

                ProductVariant saved = productVariantRepository.save(variant);

                // ✅ Ghi log tạo mới
                String logMsg = String.format(
                        "Đã tạo mới biến thể SKU [%s] cho sản phẩm [%s] (màu: %s, size: %s, tồn: %d)",
                        saved.getSku(),
                        product.getName(),
                        saved.getColor(),
                        saved.getSize(),
                        saved.getStockQuantity()
                );
                activityLogService.logAction("CREATE_VARIANT", logMsg);

                resultList.add(convertToDto(saved));
            }
        }

        return resultList;
    }


    /**
     * Cập nhật biến thể
     */
    public AdminProductVariantDto updateVariant(Long id, AdminProductVariantRequestDto requestDto) {
        ProductVariant variant = productVariantRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ProductVariantNotFoundException(id));

        variant.setSku(requestDto.getSku());
        variant.setSize(requestDto.getSize());
        variant.setColor(requestDto.getColor());
        variant.setPriceBase(requestDto.getPriceBase());
        variant.setPriceSale(requestDto.getPriceSale());
        variant.setCostPrice(requestDto.getCostPrice());
        variant.setStockQuantity(requestDto.getStockQuantity());
        variant.setLowStockThreshold(requestDto.getLowStockThreshold());
        variant.setWeightGrams(requestDto.getWeightGrams());
//        variant.setImageUrl(requestDto.getImageUrl());
        variant.setIsActive(requestDto.getIsActive());
        variant.setUpdatedAt(LocalDateTime.now());

        ProductVariant updatedVariant = productVariantRepository.save(variant);
        return convertToDto(updatedVariant);
    }

    /**
     * Xóa biến thể (hard delete)
     * 
     * <p>Thực hiện hard delete bằng cách xóa tất cả các bản ghi liên quan trước:
     * <ul>
     *   <li>1. Xóa Warranties (theo variant_id)</li>
     *   <li>2. Xóa InventoryLogs (theo variant_id)</li>
     *   <li>3. Xóa CartItems (theo variant_id)</li>
     *   <li>4. Xóa OrderDetails (theo variant_id) - lưu ý: có thể ảnh hưởng đến lịch sử đơn hàng</li>
     *   <li>5. Xóa ProductVariant (hard delete)</li>
     * </ul>
     * 
     * <p><b>Cảnh báo:</b> Hành động này sẽ xóa vĩnh viễn tất cả dữ liệu liên quan.
     */
    public void deleteVariant(Long id) {
        ProductVariant variant = productVariantRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ProductVariantNotFoundException(id));
        
        log.info("Bắt đầu xóa variant ID: {} (SKU: {}) và tất cả dữ liệu liên quan", id, variant.getSku());
        
        // 1. Xóa Warranties (theo variant_id) - sử dụng native query
        int deletedWarranties = entityManager.createNativeQuery(
                "DELETE FROM Warranties WHERE variant_id = :variantId")
                .setParameter("variantId", id)
                .executeUpdate();
        log.info("Đã xóa {} warranties cho variant ID: {}", deletedWarranties, id);
        
        // 2. Xóa InventoryLogs (theo variant_id) - sử dụng native query
        int deletedLogs = entityManager.createNativeQuery(
                "DELETE FROM Inventory_Logs WHERE variant_id = :variantId")
                .setParameter("variantId", id)
                .executeUpdate();
        log.info("Đã xóa {} inventory logs cho variant ID: {}", deletedLogs, id);
        
        // 3. Xóa CartItems (theo variant_id) - sử dụng native query
        int deletedCartItems = entityManager.createNativeQuery(
                "DELETE FROM Cart_Items WHERE variant_id = :variantId")
                .setParameter("variantId", id)
                .executeUpdate();
        log.info("Đã xóa {} cart items cho variant ID: {}", deletedCartItems, id);
        
        // 4. Xóa OrderDetails (theo variant_id) - CẢNH BÁO: Có thể ảnh hưởng đến lịch sử đơn hàng
        // Tuy nhiên, user yêu cầu xóa hết, nên sẽ xóa
        int deletedOrderDetails = entityManager.createNativeQuery(
                "DELETE FROM Order_Details WHERE variant_id = :variantId")
                .setParameter("variantId", id)
                .executeUpdate();
        log.info("Đã xóa {} order details cho variant ID: {}", deletedOrderDetails, id);
        
        // 5. Xóa ProductVariant (hard delete)
        productVariantRepository.delete(variant);
        log.info("Đã xóa variant ID: {} (SKU: {}) thành công", id, variant.getSku());
    }

    /**
     * Cập nhật tồn kho
     */
    public AdminProductVariantDto updateStock(Long id, StockUpdateRequestDto requestDto) {
        ProductVariant variant = productVariantRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ProductVariantNotFoundException(id));

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
        int lowStockThreshold = com.sneakery.store.constants.ProductConstants.LOW_STOCK_THRESHOLD;
        long inStockVariants = allVariants.stream()
                .filter(v -> v.getStockQuantity() > lowStockThreshold)
                .count();
        long lowStockVariants = allVariants.stream()
                .filter(v -> v.getStockQuantity() > 0 && v.getStockQuantity() <= lowStockThreshold)
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
        int threshold = variant.getLowStockThreshold() != null 
            ? variant.getLowStockThreshold() 
            : com.sneakery.store.constants.ProductConstants.LOW_STOCK_THRESHOLD;
        boolean isLowStock = variant.getStockQuantity() > 0 && variant.getStockQuantity() <= threshold;
        boolean isOutOfStock = variant.getStockQuantity() == 0;

        // Safely get product and brand info
        Long productId = variant.getProduct() != null ? variant.getProduct().getId() : null;
        String productName = variant.getProduct() != null ? variant.getProduct().getName() : "Unknown Product";
        String productSlug = variant.getProduct() != null ? variant.getProduct().getSlug() : "";
        String brandName = (variant.getProduct() != null && variant.getProduct().getBrand() != null) 
                ? variant.getProduct().getBrand().getName() : "Unknown Brand";

//        String imageUrl = variant.getImageUrl();

        // ✅ Added: nếu imageUrl null → lấy ảnh bìa từ bảng Product_Images
//        if ((imageUrl == null || imageUrl.isBlank()) && productId != null) {
//            Optional<ProductImage> coverImage = productImageRepository.findByProductIdAndIsPrimaryTrue(productId);
//            if (coverImage.isPresent()) {
//                imageUrl = coverImage.get().getImageUrl();
//            }
//        }

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
//                .imageUrl(imageUrl)
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
        if (quantity <= com.sneakery.store.constants.ProductConstants.LOW_STOCK_THRESHOLD) return "low_stock";
        return "in_stock";
    }
}
