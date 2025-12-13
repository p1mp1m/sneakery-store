package com.sneakery.store.service;

import com.sneakery.store.dto.BrandDto;
import com.sneakery.store.entity.Brand;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service quản lý thương hiệu (Brand)
 * 
 * <p>Service này cung cấp các chức năng:
 * <ul>
 *   <li>Tạo, đọc, cập nhật, xóa thương hiệu</li>
 *   <li>Cache dữ liệu thương hiệu để tối ưu hiệu năng</li>
 * </ul>
 * 
 * <p><b>Về Caching:</b>
 * <ul>
 *   <li>Tất cả dữ liệu thương hiệu được cache để giảm tải database</li>
 *   <li>Cache tự động bị xóa khi có thương hiệu mới được tạo, cập nhật, hoặc xóa</li>
 *   <li>Thời gian cache: 5 phút (cấu hình trong application.properties)</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy tất cả thương hiệu (có cache)
 * List&lt;BrandDto&gt; brands = brandService.getAllBrands();
 * 
 * // Lấy thương hiệu theo ID (có cache)
 * BrandDto brand = brandService.getBrandById(1);
 * 
 * // Tạo thương hiệu mới (sẽ xóa cache)
 * BrandDto newBrand = brandService.createBrand(brandDto);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    /**
     * Tạo thương hiệu mới
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Chuyển đổi DTO thành Entity</li>
     *   <li>Lưu vào database</li>
     *   <li>Xóa tất cả cache của thương hiệu (để đảm bảo dữ liệu mới nhất)</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b> Sau khi tạo thành công, cache sẽ bị xóa để lần truy vấn tiếp theo
     * sẽ lấy dữ liệu mới nhất từ database.
     * 
     * @param brandDto DTO chứa thông tin thương hiệu cần tạo (tên, slug, logo)
     * @return BrandDto của thương hiệu vừa tạo
     * @throws ApiException nếu validation thất bại hoặc dữ liệu không hợp lệ
     * 
     * @example
     * <pre>
     * BrandDto newBrand = BrandDto.builder()
     *     .name("Nike")
     *     .slug("nike")
     *     .build();
     * BrandDto created = brandService.createBrand(newBrand);
     * </pre>
     */
    @CacheEvict(value = "brands", allEntries = true)
    public BrandDto createBrand(BrandDto brandDto) {
        Brand brand = convertToEntity(brandDto);
        Brand savedBrand = brandRepository.save(Objects.requireNonNull(brand));
        return convertToDto(savedBrand);
    }

    /**
     * Lấy thông tin thương hiệu theo ID
     * 
     * <p>Phương thức này sử dụng cache để tối ưu hiệu năng:
     * <ul>
     *   <li>Lần đầu tiên: Lấy từ database và lưu vào cache</li>
     *   <li>Lần sau: Lấy trực tiếp từ cache (nhanh hơn)</li>
     *   <li>Cache key: ID của thương hiệu (ví dụ: "brands::1")</li>
     * </ul>
     * 
     * @param id ID của thương hiệu cần lấy
     * @return BrandDto chứa thông tin thương hiệu
     * @throws ApiException nếu không tìm thấy thương hiệu với ID này
     * 
     * @example
     * <pre>
     * // Lấy thương hiệu có ID = 1
     * BrandDto brand = brandService.getBrandById(1);
     * System.out.println(brand.getName()); // "Nike"
     * </pre>
     */
    @Cacheable(value = "brands", key = "#id")
    public BrandDto getBrandById(Integer id) {
        Brand brand = brandRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu"));
        return convertToDto(brand);
    }

    /**
     * Lấy danh sách tất cả thương hiệu
     * 
     * <p>Phương thức này sử dụng cache để tối ưu hiệu năng:
     * <ul>
     *   <li>Lần đầu tiên: Lấy từ database và lưu vào cache</li>
     *   <li>Lần sau: Lấy trực tiếp từ cache (nhanh hơn)</li>
     *   <li>Cache key: "all" (ví dụ: "brands::all")</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Nếu có thương hiệu mới được tạo/cập nhật/xóa, cache này sẽ bị xóa
     * và lần truy vấn tiếp theo sẽ lấy dữ liệu mới nhất từ database.
     * 
     * @return Danh sách tất cả BrandDto
     * 
     * @example
     * <pre>
     * // Lấy tất cả thương hiệu
     * List&lt;BrandDto&gt; brands = brandService.getAllBrands();
     * brands.forEach(brand -&gt; System.out.println(brand.getName()));
     * </pre>
     */
    @Cacheable(value = "brands", key = "'all'")
    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin thương hiệu
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Tìm thương hiệu theo ID</li>
     *   <li>Cập nhật các trường: tên, slug, logo</li>
     *   <li>Lưu vào database</li>
     *   <li>Xóa tất cả cache của thương hiệu (để đảm bảo dữ liệu mới nhất)</li>
     * </ol>
     * 
     * <p><b>Lưu ý:</b> Sau khi cập nhật thành công, cache sẽ bị xóa để lần truy vấn tiếp theo
     * sẽ lấy dữ liệu mới nhất từ database.
     * 
     * @param id ID của thương hiệu cần cập nhật
     * @param brandDto DTO chứa thông tin mới của thương hiệu
     * @return BrandDto của thương hiệu sau khi cập nhật
     * @throws ApiException nếu không tìm thấy thương hiệu với ID này
     * 
     * @example
     * <pre>
     * BrandDto updateData = BrandDto.builder()
     *     .name("Nike Updated")
     *     .slug("nike-updated")
     *     .build();
     * BrandDto updated = brandService.updateBrand(1, updateData);
     * </pre>
     */
    @CacheEvict(value = "brands", allEntries = true)
    public BrandDto updateBrand(Integer id, BrandDto brandDto) {
        Brand brand = brandRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu"));
        
        brand.setName(brandDto.getName());
        brand.setSlug(brandDto.getSlug());
        brand.setLogoUrl(brandDto.getLogoUrl()); // Giả sử BrandDto có logoUrl
        
        Brand updatedBrand = brandRepository.save(brand);
        return convertToDto(updatedBrand);
    }

    /**
     * Xóa thương hiệu theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Kiểm tra thương hiệu có tồn tại không</li>
     *   <li>Xóa khỏi database</li>
     *   <li>Xóa tất cả cache của thương hiệu (để đảm bảo dữ liệu mới nhất)</li>
     * </ol>
     * 
     * <p><b>Cảnh báo:</b> Hành động này không thể hoàn tác. Hãy chắc chắn trước khi xóa.
     * 
     * <p><b>Lưu ý:</b> Nếu thương hiệu đang được sử dụng bởi các sản phẩm, có thể gặp lỗi
     * foreign key constraint. Cần xóa hoặc cập nhật các sản phẩm liên quan trước.
     * 
     * @param id ID của thương hiệu cần xóa
     * @throws ApiException nếu không tìm thấy thương hiệu với ID này
     * 
     * @example
     * <pre>
     * // Xóa thương hiệu có ID = 1
     * brandService.deleteBrand(1);
     * </pre>
     */
    @CacheEvict(value = "brands", allEntries = true)
    public void deleteBrand(Integer id) {
        Integer nonNullId = Objects.requireNonNull(id);
        if (!brandRepository.existsById(nonNullId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu");
        }

        brandRepository.deleteById(nonNullId);
    }

    // --- Mapper ---
    private BrandDto convertToDto(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .build();
    }
    
    private Brand convertToEntity(BrandDto dto) {
        Brand brand = new Brand();
        brand.setName(dto.getName());
        brand.setSlug(dto.getSlug());
        // brand.setLogoUrl(dto.getLogoUrl());
        return brand;
    }
}