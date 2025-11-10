package com.sneakery.store.service;

import com.sneakery.store.dto.CategoryDto;
import com.sneakery.store.dto.CategoryGroupDto;
import com.sneakery.store.entity.Category;
import com.sneakery.store.exception.ApiException;
import com.sneakery.store.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service quản lý danh mục (Category)
 * 
 * <p>Service này cung cấp các chức năng:
 * <ul>
 *   <li>Tạo, đọc, cập nhật, xóa danh mục</li>
 *   <li>Quản lý cấu trúc phân cấp danh mục (danh mục cha - danh mục con)</li>
 *   <li>Cache dữ liệu danh mục để tối ưu hiệu năng</li>
 * </ul>
 * 
 * <p><b>Về Caching:</b>
 * <ul>
 *   <li>Tất cả dữ liệu danh mục được cache để giảm tải database</li>
 *   <li>Cache tự động bị xóa khi có danh mục mới được tạo, cập nhật, hoặc xóa</li>
 *   <li>Thời gian cache: 5 phút (cấu hình trong application.properties)</li>
 * </ul>
 * 
 * <p><b>Về Cấu trúc phân cấp:</b>
 * <ul>
 *   <li>Danh mục có thể có danh mục cha (parent category)</li>
 *   <li>Ví dụ: "Giày thể thao" là cha của "Giày chạy bộ", "Giày bóng đá"</li>
 *   <li>Khi xóa danh mục cha, các danh mục con có thể bị xóa theo (nếu cascade delete được bật)</li>
 * </ul>
 * 
 * <p><b>Ví dụ sử dụng:</b>
 * <pre>
 * // Lấy tất cả danh mục (có cache)
 * List&lt;CategoryDto&gt; categories = categoryService.getAllCategories();
 * 
 * // Lấy danh mục theo ID (có cache)
 * CategoryDto category = categoryService.getCategoryById(1);
 * 
 * // Tạo danh mục mới với danh mục cha
 * CategoryDto newCategory = CategoryDto.builder()
 *     .name("Giày chạy bộ")
 *     .slug("giay-chay-bo")
 *     .parentId(1) // ID của danh mục cha "Giày thể thao"
 *     .build();
 * CategoryDto created = categoryService.createCategory(newCategory);
 * </pre>
 * 
 * @author Sneakery Store Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Tạo danh mục mới
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Chuyển đổi DTO thành Entity</li>
     *   <li>Thiết lập danh mục cha (nếu có)</li>
     *   <li>Lưu vào database</li>
     *   <li>Xóa tất cả cache của danh mục (để đảm bảo dữ liệu mới nhất)</li>
     * </ol>
     * 
     * <p><b>Về danh mục cha:</b>
     * <ul>
     *   <li>Nếu categoryDto.getParentId() != null: Tìm danh mục cha và gán vào</li>
     *   <li>Nếu categoryDto.getParentId() == null: Danh mục này là danh mục gốc (không có cha)</li>
     * </ul>
     * 
     * @param categoryDto DTO chứa thông tin danh mục cần tạo (tên, slug, parentId)
     * @return CategoryDto của danh mục vừa tạo
     * @throws ApiException nếu validation thất bại hoặc không tìm thấy danh mục cha
     * 
     * @example
     * <pre>
     * // Tạo danh mục gốc (không có cha)
     * CategoryDto rootCategory = new CategoryDto(null, "Giày thể thao", "giay-the-thao", null);
     * CategoryDto created = categoryService.createCategory(rootCategory);
     * 
     * // Tạo danh mục con (có cha)
     * CategoryDto childCategory = new CategoryDto(null, "Giày chạy bộ", "giay-chay-bo", 1);
     * CategoryDto createdChild = categoryService.createCategory(childCategory);
     * </pre>
     */
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = convertToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(Objects.requireNonNull(category));
        return convertToDto(savedCategory);
    }

    /**
     * Lấy thông tin danh mục theo ID
     * 
     * <p>Phương thức này sử dụng cache để tối ưu hiệu năng:
     * <ul>
     *   <li>Lần đầu tiên: Lấy từ database và lưu vào cache</li>
     *   <li>Lần sau: Lấy trực tiếp từ cache (nhanh hơn)</li>
     *   <li>Cache key: ID của danh mục (ví dụ: "categories::1")</li>
     * </ul>
     * 
     * @param id ID của danh mục cần lấy
     * @return CategoryDto chứa thông tin danh mục (bao gồm parentId nếu có)
     * @throws ApiException nếu không tìm thấy danh mục với ID này
     * 
     * @example
     * <pre>
     * // Lấy danh mục có ID = 1
     * CategoryDto category = categoryService.getCategoryById(1);
     * System.out.println(category.getName()); // "Giày thể thao"
     * System.out.println(category.getParentId()); // null (nếu là danh mục gốc)
     * </pre>
     */
    @Cacheable(value = "categories", key = "#id")
    public CategoryDto getCategoryById(Integer id) {
        Category category = categoryRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục"));
        return convertToDto(category);
    }

    /**
     * Lấy danh sách tất cả danh mục
     * 
     * <p>Phương thức này sử dụng cache để tối ưu hiệu năng:
     * <ul>
     *   <li>Lần đầu tiên: Lấy từ database và lưu vào cache</li>
     *   <li>Lần sau: Lấy trực tiếp từ cache (nhanh hơn)</li>
     *   <li>Cache key: "all" (ví dụ: "categories::all")</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Nếu có danh mục mới được tạo/cập nhật/xóa, cache này sẽ bị xóa
     * và lần truy vấn tiếp theo sẽ lấy dữ liệu mới nhất từ database.
     * 
     * @return Danh sách tất cả CategoryDto (bao gồm cả danh mục gốc và danh mục con)
     * 
     * @example
     * <pre>
     * // Lấy tất cả danh mục
     * List&lt;CategoryDto&gt; categories = categoryService.getAllCategories();
     * categories.forEach(cat -&gt; {
     *     System.out.println(cat.getName());
     *     if (cat.getParentId() != null) {
     *         System.out.println("  - Danh mục con của: " + cat.getParentId());
     *     }
     * });
     * </pre>
     */
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách categories theo nhóm (parent categories với children)
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Lấy tất cả categories</li>
     *   <li>Nhóm child categories theo parent</li>
     *   <li>Trả về danh sách parent categories với children của chúng</li>
     * </ol>
     * 
     * <p><b>Về cấu trúc trả về:</b>
     * <ul>
     *   <li>Chỉ trả về các parent categories có children</li>
     *   <li>Mỗi parent category bao gồm danh sách children</li>
     *   <li>productCount là tổng số sản phẩm của tất cả children (sẽ được tính ở frontend)</li>
     * </ul>
     * 
     * @return Danh sách CategoryGroupDto (parent categories với children)
     */
    @Cacheable(value = "categories", key = "'groups'")
    public List<CategoryGroupDto> getCategoryGroups() {
        List<CategoryDto> allCategories = getAllCategories();
        
        // Lấy tất cả parent categories (không có parentId)
        List<CategoryDto> parentCategories = allCategories.stream()
                .filter(cat -> cat.getParentId() == null)
                .collect(Collectors.toList());
        
        // Nhóm child categories theo parentId
        Map<Integer, List<CategoryDto>> childrenByParent = allCategories.stream()
                .filter(cat -> cat.getParentId() != null)
                .collect(Collectors.groupingBy(CategoryDto::getParentId));
        
        // Tạo CategoryGroupDto cho mỗi parent có children
        return parentCategories.stream()
                .filter(parent -> childrenByParent.containsKey(parent.getId()))
                .map(parent -> CategoryGroupDto.builder()
                        .id(parent.getId())
                        .name(parent.getName())
                        .slug(parent.getSlug())
                        .productCount(0) // Sẽ được tính ở frontend
                        .children(childrenByParent.get(parent.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin danh mục
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Tìm danh mục theo ID</li>
     *   <li>Cập nhật các trường: tên, slug, danh mục cha</li>
     *   <li>Lưu vào database</li>
     *   <li>Xóa tất cả cache của danh mục (để đảm bảo dữ liệu mới nhất)</li>
     * </ol>
     * 
     * <p><b>Về danh mục cha:</b>
     * <ul>
     *   <li>Nếu categoryDto.getParentId() != null: Tìm danh mục cha và gán vào</li>
     *   <li>Nếu categoryDto.getParentId() == null: Xóa danh mục cha (trở thành danh mục gốc)</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Sau khi cập nhật thành công, cache sẽ bị xóa để lần truy vấn tiếp theo
     * sẽ lấy dữ liệu mới nhất từ database.
     * 
     * @param id ID của danh mục cần cập nhật
     * @param categoryDto DTO chứa thông tin mới của danh mục
     * @return CategoryDto của danh mục sau khi cập nhật
     * @throws ApiException nếu không tìm thấy danh mục hoặc danh mục cha không tồn tại
     * 
     * @example
     * <pre>
     * // Cập nhật tên danh mục
     * CategoryDto updateData = new CategoryDto(1, "Giày thể thao mới", "giay-the-thao-moi", null);
     * CategoryDto updated = categoryService.updateCategory(1, updateData);
     * 
     * // Chuyển danh mục con thành danh mục gốc (xóa parent)
     * CategoryDto updateData2 = new CategoryDto(2, "Giày chạy bộ", "giay-chay-bo", null);
     * CategoryDto updated2 = categoryService.updateCategory(2, updateData2);
     * </pre>
     */
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục"));
        
        // Cập nhật các trường
        category.setName(categoryDto.getName());
        category.setSlug(categoryDto.getSlug());

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(Objects.requireNonNull(categoryDto.getParentId()))
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục cha"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        
        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    /**
     * Xóa danh mục theo ID
     * 
     * <p>Phương thức này sẽ:
     * <ol>
     *   <li>Kiểm tra danh mục có tồn tại không</li>
     *   <li>Xóa khỏi database</li>
     *   <li>Xóa tất cả cache của danh mục (để đảm bảo dữ liệu mới nhất)</li>
     * </ol>
     * 
     * <p><b>Cảnh báo:</b> Hành động này không thể hoàn tác. Hãy chắc chắn trước khi xóa.
     * 
     * <p><b>Về cascade delete:</b>
     * <ul>
     *   <li>Nếu cascade delete được bật: Khi xóa danh mục cha, tất cả danh mục con sẽ bị xóa theo</li>
     *   <li>Nếu cascade delete tắt: Cần xóa hoặc cập nhật các danh mục con trước</li>
     * </ul>
     * 
     * <p><b>Lưu ý:</b> Nếu danh mục đang được sử dụng bởi các sản phẩm, có thể gặp lỗi
     * foreign key constraint. Cần xóa hoặc cập nhật các sản phẩm liên quan trước.
     * 
     * @param id ID của danh mục cần xóa
     * @throws ApiException nếu không tìm thấy danh mục với ID này
     * 
     * @example
     * <pre>
     * // Xóa danh mục có ID = 1
     * // Lưu ý: Nếu có danh mục con, cần xóa hoặc cập nhật chúng trước
     * categoryService.deleteCategory(1);
     * </pre>
     */
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Integer id) {
        Integer nonNullId = Objects.requireNonNull(id);
        if (!categoryRepository.existsById(nonNullId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục");
        }
        categoryRepository.deleteById(nonNullId);
    }

    // --- Mapper ---
    private CategoryDto convertToDto(Category category) {
        return new CategoryDto(
            category.getId(),
            category.getName(),
            category.getSlug(),
            category.getParent() != null ? category.getParent().getId() : null
        );
    }
    
    private Category convertToEntity(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(Objects.requireNonNull(dto.getParentId()))
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ID danh mục cha không hợp lệ"));
            category.setParent(parent);
        }
        return category;
    }
}