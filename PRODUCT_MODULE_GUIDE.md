# 📦 Module Quản Lý Sản Phẩm - Hướng Dẫn Sử Dụng

## 🎯 Tổng Quan

Module quản lý sản phẩm đã được **code lại hoàn toàn** với nhiều tính năng từ cơ bản đến nâng cao, bao gồm:

### ✨ Tính Năng Chính

#### 1. **Import/Export Excel** 📊
- **Import**: Nhập hàng loạt sản phẩm từ file Excel với preview trước khi import
- **Export**: Xuất danh sách sản phẩm ra file Excel
- **Template**: Tải file mẫu Excel để điền thông tin đúng format

#### 2. **Bulk Operations** 🔄
- Cập nhật hàng loạt trạng thái (Active/Inactive)
- Đổi thương hiệu cho nhiều sản phẩm cùng lúc
- Thêm/xóa danh mục cho nhiều sản phẩm
- Xóa nhiều sản phẩm cùng lúc

#### 3. **Advanced Filters** 🔍
- Tìm kiếm theo tên/slug
- Lọc theo thương hiệu
- Lọc theo danh mục  
- Lọc theo trạng thái (Active/Inactive)
- Lọc theo khoảng giá (min-max)
- Lọc theo tồn kho (Còn hàng/Sắp hết/Hết hàng)

#### 4. **Advanced Sorting** ⬆️⬇️
- Sắp xếp theo tên A-Z
- Sắp xếp theo giá (thấp → cao)
- Sắp xếp theo tồn kho (thấp → cao)
- Sắp xếp theo ngày tạo

#### 5. **Duplicate Product** 📋
- Nhân bản sản phẩm nhanh chóng
- Tự động thêm hậu tố "(Copy)" vào tên
- Tự động đặt SKU mới cho variants
- Đặt tồn kho về 0 và trạng thái về Inactive để admin kiểm tra

#### 6. **Stock Management** 📦
- Hiển thị trạng thái tồn kho trực quan (badge màu)
- Cảnh báo sản phẩm sắp hết hàng (≤10)
- Cảnh báo sản phẩm hết hàng (0)
- Icons Material để phân biệt nhanh

#### 7. **Product Statistics** 📈
- Tổng số sản phẩm/variants
- Số sản phẩm đang bán/ngừng bán
- Tổng tồn kho
- Số sản phẩm sắp hết/hết hàng
- Giá trung bình/cao nhất/thấp nhất
- Số thương hiệu/danh mục

---

## 🔧 Backend Changes

### DTOs Mới
1. **ProductImportDto** - Dữ liệu import từ Excel
2. **ProductImportResultDto** - Kết quả import
3. **ProductBulkUpdateRequestDto** - Request bulk update
4. **ProductBulkUpdateResultDto** - Kết quả bulk update
5. **ProductAdvancedFilterDto** - Advanced filter params
6. **ProductStatsDto** - Thống kê sản phẩm

### Service Methods Mới
```java
// AdminProductService.java
public ProductImportResultDto importProducts(List<ProductImportDto> importList)
public ProductBulkUpdateResultDto bulkUpdateProducts(ProductBulkUpdateRequestDto request)
public Page<AdminProductListDto> getProductsWithAdvancedFilter(ProductAdvancedFilterDto filter, Pageable pageable)
public AdminProductDetailDto duplicateProduct(Long productId)
public ProductStatsDto getProductStatistics()
```

### API Endpoints Mới
```
POST   /api/admin/products/import          - Import từ Excel
POST   /api/admin/products/bulk-update     - Bulk update
POST   /api/admin/products/{id}/duplicate  - Duplicate product
GET    /api/admin/products/statistics      - Lấy thống kê
GET    /api/admin/products                 - Advanced filter (updated)
```

---

## 🎨 Frontend Changes

### Component Mới
- **Import Excel Modal**: Preview data trước khi import
- **Bulk Update Modal**: Chọn action và giá trị để update hàng loạt
- **Statistics Cards**: Hiển thị thống kê trực quan

### Features Cải Tiến
1. **Filters Panel**: 2 hàng filters với nhiều options
2. **Stock Indicators**: Badge màu + icon cho trạng thái tồn kho
3. **Bulk Selection**: Checkbox để chọn nhiều sản phẩm
4. **Action Buttons**: Thêm nút Duplicate cho mỗi sản phẩm

### Services Mới
```javascript
// adminService.js
async importProducts(productList)
async bulkUpdateProducts(bulkData)
async duplicateProduct(productId)
async getProductStatistics()
```

### Store Actions Mới
```javascript
// stores/admin.js
importProducts(productList)
bulkUpdateProducts(bulkData)
duplicateProduct(productId)
getProductStatistics()
getProductById(id)
```

---

## 📝 Hướng Dẫn Sử Dụng

### 1. Import Sản Phẩm Từ Excel

**Bước 1**: Click nút "Import Excel" trên header

**Bước 2**: Tải file mẫu Excel (nếu chưa có)

**Bước 3**: Điền thông tin sản phẩm theo format:
```
Tên sản phẩm | Slug | Thương hiệu | Mô tả | Danh mục | Trạng thái | SKU | Size | Màu sắc | Giá gốc | Giá sale | Tồn kho | URL ảnh
```

**Lưu ý**:
- Danh mục phân cách bởi dấu phẩy: "Men,Sneakers"
- Trạng thái: TRUE (đang bán) hoặc FALSE (ngừng bán)
- Nếu nhiều dòng có cùng Tên sản phẩm + Thương hiệu → sẽ gộp thành 1 sản phẩm với nhiều variants

**Bước 4**: Upload file và xem preview

**Bước 5**: Click "Import" để thêm sản phẩm vào database

### 2. Bulk Update Sản Phẩm

**Bước 1**: Chọn nhiều sản phẩm bằng checkbox

**Bước 2**: Click "Cập nhật hàng loạt" trên bulk action bar

**Bước 3**: Chọn action:
- **Cập nhật trạng thái**: Đổi tất cả sang Active hoặc Inactive
- **Đổi thương hiệu**: Chuyển tất cả sang thương hiệu khác
- **Thêm danh mục**: Thêm 1 danh mục cho tất cả sản phẩm
- **Xóa danh mục**: Xóa 1 danh mục khỏi tất cả sản phẩm

**Bước 4**: Chọn giá trị tương ứng và click "Cập nhật"

### 3. Duplicate (Nhân Bản) Sản Phẩm

**Cách 1**: Click icon "Copy" (content_copy) trên mỗi dòng sản phẩm

**Kết quả**:
- Tạo sản phẩm mới với tên có hậu tố "(Copy)"
- Slug tự động thêm timestamp để tránh trùng
- Tất cả variants được copy với SKU có hậu tố "-COPY"
- Tồn kho đặt về 0
- Trạng thái đặt về Inactive
- Admin có thể kiểm tra và chỉnh sửa trước khi kích hoạt

### 4. Advanced Filters

**Tìm kiếm**: Tìm theo tên hoặc slug sản phẩm

**Lọc theo thương hiệu**: Chọn thương hiệu từ dropdown

**Lọc theo danh mục**: Chọn danh mục từ dropdown

**Lọc theo giá**: Nhập khoảng giá min-max

**Lọc theo tồn kho**:
- **Tất cả**: Hiển thị tất cả
- **Còn hàng**: Tồn kho > 10
- **Sắp hết**: 1 ≤ Tồn kho ≤ 10  
- **Hết hàng**: Tồn kho = 0

**Sắp xếp**: Chọn tiêu chí và thứ tự (asc/desc)

**Reset**: Click "Xóa bộ lọc" để reset về mặc định

### 5. Export Excel

**Bước 1**: Click nút "Export Excel"

**Bước 2**: File Excel sẽ được tải về với tên `san-pham_YYYY-MM-DD.xlsx`

**Nội dung**: STT, Tên sản phẩm, Slug, Thương hiệu, Số variants, Trạng thái

---

## 🎨 UI/UX Improvements

### Statistics Cards
- Gradient backgrounds
- Màu sắc phân biệt (success, info, warning, danger)
- Icons Material rõ ràng
- Animation fade-in

### Stock Badges
- 🟢 **Còn hàng**: Xanh lá + icon check_circle
- 🟡 **Sắp hết**: Vàng + icon warning
- 🔴 **Hết hàng**: Đỏ + icon remove_shopping_cart

### Bulk Action Bar
- Hiển thị khi có sản phẩm được chọn
- Gradient purple background
- Animations: slide-in

### Filters Panel
- 2 hàng filters
- Grid layout responsive
- Icons cho buttons

### Modal Improvements
- Import modal với preview table
- Bulk update modal với conditional inputs
- Better validation và error handling

---

## 🔐 Security

Tất cả API đều được bảo vệ bởi:
```java
@PreAuthorize("hasRole('ADMIN')")
```

Chỉ user có role **ADMIN** mới có thể:
- Import/Export sản phẩm
- Bulk update
- Duplicate sản phẩm
- Xem thống kê chi tiết

---

## 🐛 Error Handling

### Backend
- Validation đầy đủ cho import data
- Try-catch cho mọi operation
- Trả về error messages cụ thể
- Logging với SLF4J

### Frontend
- ElMessage cho success/error notifications
- Loading states cho async operations
- Form validation trước khi submit
- Console.error để debug

---

## 📊 Performance

### Backend Optimizations
- **Criteria API**: Dynamic query cho advanced filter
- **Batch Processing**: Import/Bulk update xử lý từng item nhưng 1 transaction
- **Caching**: Brand và Category cache trong import
- **Pagination**: Tất cả danh sách đều có phân trang

### Frontend Optimizations  
- **Debounce Search**: 500ms delay để giảm API calls
- **Lazy Load**: Chỉ load data khi cần
- **Computed Properties**: Cache calculated values
- **Vue Reactivity**: Efficient updates

---

## 🧪 Testing Guide

### Backend Testing
```bash
# Test import API
POST http://localhost:8080/api/admin/products/import
Content-Type: application/json
Authorization: Bearer <admin-token>

[
  {
    "productName": "Test Product",
    "brandName": "Nike",
    "categories": "Men,Sneakers",
    "sku": "TEST-001",
    "size": "42",
    "color": "Black",
    "priceBase": 1000000,
    "stockQuantity": 50
  }
]
```

### Frontend Testing
1. Login với account admin
2. Vào trang "Quản lý sản phẩm"
3. Test từng feature:
   - Import Excel
   - Bulk Update
   - Duplicate
   - Advanced Filters
   - Export

---

## 📦 Files Changed

### Backend
```
sneakery-backend/src/main/java/com/sneakery/store/
├── dto/
│   ├── ProductImportDto.java (NEW)
│   ├── ProductImportResultDto.java (NEW)
│   ├── ProductBulkUpdateRequestDto.java (NEW)
│   ├── ProductBulkUpdateResultDto.java (NEW)
│   ├── ProductAdvancedFilterDto.java (NEW)
│   └── ProductStatsDto.java (NEW)
├── service/
│   └── AdminProductService.java (UPDATED - thêm 400+ dòng)
└── controller/
    └── AdminProductController.java (UPDATED - thêm 4 endpoints)
```

### Frontend
```
sneakery-frontend/src/
├── services/
│   └── adminService.js (UPDATED - thêm 4 methods)
├── stores/
│   └── admin.js (UPDATED - thêm 5 actions)
└── views/admin/
    ├── AdminProducts.vue (REWRITTEN - 2500+ dòng)
    └── AdminProducts_BACKUP.vue (OLD VERSION)
```

---

## 🚀 Next Steps

### Tính Năng Có Thể Thêm (Optional)
1. **Product History**: Lịch sử thay đổi sản phẩm
2. **Image Upload**: Upload ảnh thực thay vì URL
3. **Product Analytics**: Thống kê bán chạy, doanh thu theo sản phẩm
4. **Bulk Price Update**: Tăng/giảm giá hàng loạt theo %
5. **Product Templates**: Template để tạo sản phẩm nhanh
6. **Advanced Stock Alert**: Email/notification khi sắp hết hàng
7. **Product Categories Tree**: Hiển thị cây danh mục
8. **Variant Management**: Thêm/sửa/xóa variant inline trong bảng

---

## 💡 Tips & Tricks

1. **Import Excel**: Dùng file mẫu để đảm bảo format đúng
2. **Bulk Update**: Test với 1-2 sản phẩm trước khi update hàng loạt
3. **Duplicate**: Kiểm tra SKU để tránh trùng
4. **Filters**: Combine nhiều filters để tìm chính xác
5. **Stock Alert**: Dùng filter "Sắp hết" để theo dõi tồn kho

---

## 📞 Support

Nếu gặp vấn đề:
1. Check console để xem error messages
2. Check Network tab để xem API response
3. Check backend logs (SLF4J)
4. Contact admin team

---

## 📄 Changelog

### Version 2.0.0 (2025-10-22)
- ✅ Import/Export Excel
- ✅ Bulk Update Operations
- ✅ Advanced Filters (10+ options)
- ✅ Advanced Sorting
- ✅ Duplicate Product
- ✅ Stock Management & Alerts
- ✅ Product Statistics Dashboard
- ✅ UI/UX Improvements
- ✅ Performance Optimizations
- ✅ Complete Documentation

### Version 1.0.0 (Previous)
- Basic CRUD operations
- Simple filters (search, brand, status)
- Basic export

---

**Developed with ❤️ by AI Assistant**

