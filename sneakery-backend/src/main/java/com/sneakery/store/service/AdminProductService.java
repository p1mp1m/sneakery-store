package com.sneakery.store.service;

import com.sneakery.store.dto.CategoryDto;
import com.sneakery.store.dto.AdminProductDetailDto;
import com.sneakery.store.dto.AdminProductRequestDto;
import com.sneakery.store.dto.AdminVariantRequestDto;
import com.sneakery.store.entity.Brand;
import com.sneakery.store.entity.Category;
import com.sneakery.store.entity.Product;
import com.sneakery.store.entity.ProductVariant;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.BrandRepository;
import com.sneakery.store.repository.CategoryRepository;
import com.sneakery.store.repository.ProductRepository;
import com.sneakery.store.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;

    /**
     * API 1: Tạo sản phẩm mới
     */
    @Transactional
    public AdminProductDetailDto createProduct(AdminProductRequestDto requestDto) {
        // 1. Lấy Brand
        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại"));

        // 2. Lấy Categories
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại: " + id)))
                .collect(Collectors.toSet());

        // 3. Tạo Product
        Product product = new Product();
        product.setName(requestDto.getName());
        product.setSlug(requestDto.getSlug());
        product.setDescription(requestDto.getDescription());
        product.setIsActive(requestDto.getIsActive());
        product.setBrand(brand);
        product.setCategories(categories);

        // 4. Tạo Variants
        List<ProductVariant> variants = requestDto.getVariants().stream()
                .map(dto -> convertVariantDtoToEntity(dto, product))
                .collect(Collectors.toList());
        
        product.setVariants(variants); // Gắn variants vào product

        // 5. Lưu (sẽ cascade-save cả variants)
        Product savedProduct = productRepository.save(product);
        return convertToAdminDetailDto(savedProduct);
    }

    /**
     * API 2: Cập nhật sản phẩm
     */
    @Transactional
    public AdminProductDetailDto updateProduct(Long productId, AdminProductRequestDto requestDto) {
        // 1. Tìm Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        // 2. Cập nhật Brand
        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Thương hiệu không tồn tại"));
        
        // 3. Cập nhật Categories
        Set<Category> categories = requestDto.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Danh mục không tồn tại: " + id)))
                .collect(Collectors.toSet());

        // 4. Cập nhật thông tin Product
        product.setName(requestDto.getName());
        product.setSlug(requestDto.getSlug());
        product.setDescription(requestDto.getDescription());
        product.setIsActive(requestDto.getIsActive());
        product.setBrand(brand);
        product.setCategories(categories);

        // 5. Xử lý Cập nhật/Thêm/Xóa Variants
        updateProductVariants(product, requestDto.getVariants());

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
    @Transactional(readOnly = true)
    public Page<Product> getAllProductsForAdmin(Pageable pageable) {
        // Trả về Entity, Controller tự xử lý
        return productRepository.findAll(pageable);
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

    // Helper xử lý logic Cập nhật/Thêm/Xóa Variants
    private void updateProductVariants(Product product, List<AdminVariantRequestDto> variantDtos) {
        // Lấy ID từ DTOs
        Set<Long> dtoVariantIds = variantDtos.stream()
                .map(AdminVariantRequestDto::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // Lấy Variants hiện tại từ CSDL
        Map<Long, ProductVariant> existingVariantsMap = product.getVariants().stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        // 1. Xóa Variants không còn trong DTO
        existingVariantsMap.keySet().stream()
                .filter(id -> !dtoVariantIds.contains(id))
                .forEach(id -> {
                    ProductVariant variantToRemove = existingVariantsMap.get(id);
                    product.getVariants().remove(variantToRemove); // Xóa khỏi list
                    variantRepository.delete(variantToRemove); // Xóa khỏi CSDL
                });
        
        // 2. Cập nhật / Thêm mới
        for (AdminVariantRequestDto dto : variantDtos) {
            if (dto.getId() != null && existingVariantsMap.containsKey(dto.getId())) {
                // Cập nhật
                ProductVariant variantToUpdate = existingVariantsMap.get(dto.getId());
                updateVariantEntityFromDto(variantToUpdate, dto);
            } else {
                // Thêm mới
                product.getVariants().add(convertVariantDtoToEntity(dto, product));
            }
        }
    }

    // Mapper DTO -> Entity (Tạo mới Variant)
    private ProductVariant convertVariantDtoToEntity(AdminVariantRequestDto dto, Product product) {
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        return updateVariantEntityFromDto(variant, dto);
    }

    // Mapper DTO -> Entity (Cập nhật Variant)
    private ProductVariant updateVariantEntityFromDto(ProductVariant variant, AdminVariantRequestDto dto) {
        variant.setSku(dto.getSku());
        variant.setSize(dto.getSize());
        variant.setColor(dto.getColor());
        variant.setPriceBase(dto.getPriceBase());
        variant.setPriceSale(dto.getPriceSale());
        variant.setStockQuantity(dto.getStockQuantity());
        variant.setImageUrl(dto.getImageUrl());
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
                .categories(categoryDtos)
                .variants(variantDtos)
                .build();
    }
}