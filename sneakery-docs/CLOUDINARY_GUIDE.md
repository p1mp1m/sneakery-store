# Hướng dẫn tích hợp Cloudinary cho Sneakery Store

## 📋 Mục lục

1. [Tổng quan về Cloudinary](#tổng-quan-về-cloudinary)
2. [Cài đặt và cấu hình](#cài-đặt-và-cấu-hình)
3. [Kiến trúc và Flow Upload ảnh](#kiến-trúc-và-flow-upload-ảnh)
4. [Backend Implementation](#backend-implementation)
5. [Frontend Implementation](#frontend-implementation)
6. [Bảo mật](#bảo-mật)
7. [Tối ưu hiệu suất](#tối-ưu-hiệu-suất)
8. [Best Practices](#best-practices)
9. [Troubleshooting](#troubleshooting)

---

## 🌟 Tổng quan về Cloudinary

### Cloudinary là gì?

Cloudinary là một dịch vụ quản lý media (ảnh, video) trên cloud với các tính năng:

- ✅ **Lưu trữ trên cloud** - Không cần lưu file trên server backend
- ✅ **CDN tự động** - Tốc độ tải nhanh toàn cầu
- ✅ **Tự động resize/optimize** - Giảm dung lượng, tăng tốc độ
- ✅ **Transformations** - Resize, crop, format conversion (WebP, AVIF)
- ✅ **Lazy loading** - Tối ưu bandwidth
- ✅ **SEO friendly** - URL tối ưu, alt text

### Lợi ích cho Sneakery Store

```
[VueJS Frontend] <--URL ảnh-- [Cloudinary CDN]
    |                              |
    V (gửi file)                   V (serve ảnh)
[Spring Boot Backend] <-- lưu URL --> [SQL Server]
```

- **Giảm tải backend**: Không cần serve file ảnh
- **Tốc độ cao**: CDN global, cache thông minh
- **Tự động optimize**: Resize, format conversion
- **Dễ scale**: Không lo storage

---

## ⚙️ Cài đặt và cấu hình

### 1. Tạo tài khoản Cloudinary

1. Truy cập: https://cloudinary.com/users/register/free
2. Đăng ký tài khoản miễn phí (25GB storage, 25GB bandwidth/tháng)
3. Vào Dashboard → Settings → Upload presets

### 2. Lấy thông tin API

Từ Dashboard, copy các thông tin sau:

```
Cloud Name: djk6lqgu7 (ví dụ)
API Key: 333354252274719 (ví dụ)
API Secret: X9VOs0qbR_D01JHqEBm4wH2WQq0 (ví dụ)
```

⚠️ **Lưu ý**: API Secret phải được bảo mật, không commit lên Git!

### 3. Cấu hình Backend (Spring Boot)

#### 3.1. Thêm dependency (đã có trong `pom.xml`)

```xml
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.39.0</version>
</dependency>
```

#### 3.2. Cấu hình trong `application.properties`

```properties
# Cloudinary Configuration
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME:djk6lqgu7}
cloudinary.api-key=${CLOUDINARY_API_KEY:333354252274719}
cloudinary.api-secret=${CLOUDINARY_API_SECRET:X9VOs0qbR_D01JHqEBm4wH2WQq0}
```

#### 3.3. Sử dụng Environment Variables (Khuyến nghị)

Tạo file `.env` hoặc set environment variables:

```bash
# Windows (PowerShell)
$env:CLOUDINARY_CLOUD_NAME="djk6lqgu7"
$env:CLOUDINARY_API_KEY="333354252274719"
$env:CLOUDINARY_API_SECRET="X9VOs0qbR_D01JHqEBm4wH2WQq0"

# Linux/Mac
export CLOUDINARY_CLOUD_NAME="djk6lqgu7"
export CLOUDINARY_API_KEY="333354252274719"
export CLOUDINARY_API_SECRET="X9VOs0qbR_D01JHqEBm4wH2WQq0"
```

#### 3.4. CloudinaryConfig Bean

File: `sneakery-backend/src/main/java/com/sneakery/store/config/CloudinaryConfig.java`

```java
@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.cloud-name:}")
    private String cloudName;
    
    @Value("${cloudinary.api-key:}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret:}")
    private String apiSecret;
    
    @Bean
    @Nullable
    public Cloudinary cloudinary() {
        if (cloudName == null || cloudName.isBlank() || 
            apiKey == null || apiKey.isBlank() || 
            apiSecret == null || apiSecret.isBlank()) {
            return null; // Fallback to local storage
        }
        
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true // HTTPS only
        ));
    }
}
```

### 4. Kiểm tra kết nối

Chạy backend và kiểm tra log:

```
✅ Cloudinary bean initialized successfully
```

Nếu thấy:
```
⚠️ Cloudinary chưa được cấu hình, sử dụng local storage
```

→ Kiểm tra lại cấu hình trong `application.properties`

---

## 🏗️ Kiến trúc và Flow Upload ảnh

### Flow hoàn chỉnh

```
┌─────────────────┐
│  VueJS Frontend │
│  (AdminProducts)│
└────────┬────────┘
         │ 1. User chọn file ảnh
         │ 2. FormData.append('file', file)
         │ 3. POST /api/admin/products/{id}/images/upload
         ▼
┌─────────────────┐
│ Spring Boot API │
│ FileUploadCtrl  │
└────────┬────────┘
         │ 4. Validate file (size, type)
         │ 5. FileStorageService.storeProductImage()
         ▼
┌─────────────────┐
│   Cloudinary    │
│   (Cloud CDN)   │
└────────┬────────┘
         │ 6. Upload file
         │ 7. Trả về: { secure_url, public_id }
         ▼
┌─────────────────┐
│ Spring Boot API │
│ ProductImageSvc │
└────────┬────────┘
         │ 8. Lưu vào SQL Server:
         │    - image_url (secure_url)
         │    - cloudinary_public_id
         │    - is_primary, display_order
         ▼
┌─────────────────┐
│   SQL Server    │
│ Product_Images  │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  VueJS Frontend │
│  Hiển thị ảnh   │
│  <img :src="...">│
└─────────────────┘
```

### Database Schema

```sql
CREATE TABLE Product_Images (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url NVARCHAR(500) NOT NULL,        -- Cloudinary URL
    cloudinary_public_id NVARCHAR(255),      -- Dùng để xóa ảnh
    alt_text NVARCHAR(255),
    is_primary BIT DEFAULT 0,
    display_order INT DEFAULT 0,
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (product_id) REFERENCES Products(id)
);
```

---

## 🔧 Backend Implementation

### 1. FileStorageService

File: `sneakery-backend/src/main/java/com/sneakery/store/service/FileStorageService.java`

#### Upload ảnh lên Cloudinary

```java
public CloudinaryUploadResult storeProductImage(Long productId, MultipartFile file) {
    // 1. Validate file
    FileValidationUtil.validateImageFile(file);
    
    // 2. Upload lên Cloudinary
    Map<?, ?> res = cloudinary.uploader().upload(
        file.getBytes(),
        ObjectUtils.asMap(
            "folder", "uploads/sanpham/" + productId,
            "use_filename", true,
            "unique_filename", true,
            "resource_type", "image",
            // Transformations
            "transformation", Arrays.asList(
                new Transformation()
                    .width(1200)
                    .height(1200)
                    .crop("limit")
                    .quality("auto")
                    .fetchFormat("auto") // WebP nếu browser support
            )
        )
    );
    
    String url = res.get("secure_url").toString();
    String publicId = res.get("public_id").toString();
    
    return new CloudinaryUploadResult(url, publicId);
}
```

#### Xóa ảnh từ Cloudinary

```java
public void deleteByPublicId(String publicId) {
    if (publicId == null || publicId.isBlank()) return;
    
    try {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        log.info("🗑️ Đã xoá Cloudinary asset: {}", publicId);
    } catch (Exception e) {
        log.warn("⚠️ Không thể xoá Cloudinary asset: {}", e.getMessage());
    }
}
```

### 2. ProductImageService

File: `sneakery-backend/src/main/java/com/sneakery/store/service/ProductImageService.java`

#### Upload và lưu vào DB

```java
@Transactional
public ProductImageDto uploadImageFile(Long productId, MultipartFile file, 
                                       boolean isPrimary, Integer displayOrder) {
    // 1. Upload lên Cloudinary
    FileStorageService.CloudinaryUploadResult result = 
        fileStorageService.storeProductImage(productId, file);
    
    // 2. Lưu vào database
    ProductImage image = ProductImage.builder()
        .product(product)
        .imageUrl(result.url())              // Cloudinary URL
        .cloudinaryPublicId(result.publicId()) // Lưu để xóa sau
        .isPrimary(isPrimary)
        .displayOrder(displayOrder)
        .build();
    
    return convertToDto(productImageRepository.save(image));
}
```

### 3. API Endpoints

#### Upload ảnh

```http
POST /api/admin/products/{productId}/images/upload
Content-Type: multipart/form-data

file: [binary]
isPrimary: true/false
displayOrder: 1
```

**Response:**
```json
{
  "id": 123,
  "productId": 45,
  "imageUrl": "https://res.cloudinary.com/.../uploads/sanpham/45/abc123.jpg",
  "cloudinaryPublicId": "uploads/sanpham/45/abc123",
  "isPrimary": true,
  "displayOrder": 1
}
```

#### Lấy danh sách ảnh

```http
GET /api/admin/products/{productId}/images
```

**Response:**
```json
[
  {
    "id": 123,
    "imageUrl": "https://res.cloudinary.com/.../image.jpg",
    "isPrimary": true,
    "displayOrder": 1
  }
]
```

#### Xóa ảnh

```http
DELETE /api/admin/products/{productId}/images/{imageId}
```

---

## 🎨 Frontend Implementation

### 1. Upload ảnh từ VueJS

File: `sneakery-frontend/src/views/admin/AdminProducts.vue`

```javascript
// Upload ảnh sản phẩm
const uploadImage = async (productId, file) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('isPrimary', 'false')
  formData.append('displayOrder', '1')
  
  try {
    const response = await axios.post(
      `/api/admin/products/${productId}/images/upload`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    )
    
    console.log('✅ Upload thành công:', response.data.imageUrl)
    return response.data
  } catch (error) {
    console.error('❌ Upload thất bại:', error)
    throw error
  }
}
```

### 2. Hiển thị ảnh

#### Sử dụng URL trực tiếp

```vue
<template>
  <img 
    :src="product.imageUrl" 
    :alt="product.name"
    loading="lazy"
  />
</template>
```

#### Sử dụng LazyImage component

```vue
<template>
  <LazyImage
    :src="product.imageUrl"
    :alt="product.name"
    container-class="w-full h-64"
    image-class="object-cover rounded-lg"
  />
</template>

<script setup>
import LazyImage from '@/components/common/LazyImage.vue'
</script>
```

### 3. ProductCard Component

File: `sneakery-frontend/src/assets/components/products/ProductCard.vue`

```vue
<template>
  <div class="product-card">
    <img 
      :src="product.mainImageUrl || product.imageUrl || '/placeholder-image.png'"
      :alt="product.name"
      class="product-image"
      loading="lazy"
    />
  </div>
</template>
```

---

## 🔒 Bảo mật

### 1. API Keys

⚠️ **KHÔNG BAO GIỜ** commit API Secret lên Git!

```properties
# ❌ SAI - Hardcode trong code
cloudinary.api-secret=YOUR_SECRET

# ✅ ĐÚNG - Dùng environment variables
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
```

### 2. Signed URLs (Tùy chọn)

Để bảo vệ ảnh khỏi truy cập trái phép:

```java
// Tạo signed URL với expiration
String signedUrl = cloudinary.url()
    .publicId(publicId)
    .expiresAt(System.currentTimeMillis() / 1000 + 3600) // 1 giờ
    .signUrl()
    .generate();
```

### 3. Upload Presets

Tạo Upload Preset trong Cloudinary Dashboard:

1. Settings → Upload → Upload presets
2. Tạo preset mới với:
   - **Signing mode**: Unsigned (cho public) hoặc Signed (cho private)
   - **Folder**: `uploads/sanpham`
   - **Allowed formats**: jpg, png, webp
   - **Max file size**: 5MB

### 4. CORS Configuration

Cloudinary tự động xử lý CORS, không cần cấu hình thêm.

---

## ⚡ Tối ưu hiệu suất

### 1. Image Transformations

#### Resize tự động

```java
// Backend: Upload với transformation
Map<?, ?> res = cloudinary.uploader().upload(
    file.getBytes(),
    ObjectUtils.asMap(
        "transformation", Arrays.asList(
            new Transformation()
                .width(1200)      // Max width
                .height(1200)      // Max height
                .crop("limit")     // Giữ tỷ lệ, không crop
                .quality("auto")   // Tự động optimize quality
                .fetchFormat("auto") // WebP/AVIF nếu browser support
        )
    )
);
```

#### Frontend: Transform URL

```javascript
// cloudinaryHelper.js
export function getOptimizedImageUrl(originalUrl, width, height) {
  if (!originalUrl || !originalUrl.includes('cloudinary.com')) {
    return originalUrl // Không phải Cloudinary URL
  }
  
  // Thêm transformation vào URL
  const parts = originalUrl.split('/upload/')
  if (parts.length !== 2) return originalUrl
  
  const transformations = `w_${width},h_${height},c_limit,q_auto,f_auto`
  return `${parts[0]}/upload/${transformations}/${parts[1]}`
}

// Sử dụng
const thumbnailUrl = getOptimizedImageUrl(product.imageUrl, 300, 300)
const mediumUrl = getOptimizedImageUrl(product.imageUrl, 800, 800)
```

### 2. Lazy Loading

#### Sử dụng LazyImage component

```vue
<template>
  <LazyImage
    :src="product.imageUrl"
    :alt="product.name"
    root-margin="100px"
    threshold="0.1"
  />
</template>
```

#### Native lazy loading

```vue
<img 
  :src="product.imageUrl"
  loading="lazy"
  decoding="async"
/>
```

### 3. CDN và Caching

Cloudinary tự động:
- ✅ CDN global (Fastly)
- ✅ Cache headers (Cache-Control)
- ✅ Browser caching
- ✅ Edge caching

### 4. Format Optimization

Cloudinary tự động chọn format tốt nhất:

```
Original: JPG (500KB)
↓
Browser hỗ trợ WebP → WebP (200KB) ✅
Browser không hỗ trợ → JPG (300KB) ✅
```

Sử dụng `f_auto` trong transformation.

### 5. Responsive Images

```vue
<template>
  <img 
    :srcset="`
      ${getOptimizedUrl(imageUrl, 300)} 300w,
      ${getOptimizedUrl(imageUrl, 600)} 600w,
      ${getOptimizedUrl(imageUrl, 1200)} 1200w
    `"
    sizes="(max-width: 600px) 300px, (max-width: 1200px) 600px, 1200px"
    :src="imageUrl"
    alt="Product"
  />
</template>
```

---

## 📚 Best Practices

### 1. File Naming

✅ **ĐÚNG:**
```
uploads/sanpham/45/product-name-abc123.jpg
```

❌ **SAI:**
```
uploads/sanpham/45/IMG_20240101_123456.jpg
```

### 2. Folder Structure

```
uploads/
  ├── sanpham/
  │   ├── 1/          # Product ID 1
  │   │   ├── main.jpg
  │   │   └── gallery-1.jpg
  │   └── 2/
  │       └── ...
  └── users/
      └── avatars/
```

### 3. Image Sizes

| Use Case | Width | Height | Quality |
|----------|-------|--------|---------|
| Thumbnail | 300 | 300 | auto |
| Product Card | 400 | 400 | auto |
| Product Detail | 800 | 800 | auto |
| Full Size | 1200 | 1200 | auto |

### 4. Error Handling

```javascript
// Frontend
try {
  const result = await uploadImage(productId, file)
  toast.success('Upload thành công!')
} catch (error) {
  if (error.response?.status === 413) {
    toast.error('File quá lớn (tối đa 5MB)')
  } else if (error.response?.status === 400) {
    toast.error('Định dạng file không hợp lệ')
  } else {
    toast.error('Upload thất bại. Vui lòng thử lại.')
  }
}
```

### 5. Validation

```java
// Backend: FileValidationUtil
public static void validateImageFile(MultipartFile file) {
    // Size: max 5MB
    if (file.getSize() > 5 * 1024 * 1024) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "File quá lớn");
    }
    
    // Type: chỉ JPG, PNG, WEBP
    String contentType = file.getContentType();
    if (!Arrays.asList("image/jpeg", "image/png", "image/webp")
            .contains(contentType)) {
        throw new ApiException(HttpStatus.BAD_REQUEST, "Định dạng không hợp lệ");
    }
}
```

### 6. Cleanup khi xóa sản phẩm

```java
@Transactional
public void deleteProduct(Long productId) {
    // 1. Xóa ảnh trên Cloudinary
    List<ProductImage> images = productImageRepository
        .findByProductIdOrderByDisplayOrderAsc(productId);
    
    for (ProductImage image : images) {
        if (image.getCloudinaryPublicId() != null) {
            fileStorageService.deleteByPublicId(image.getCloudinaryPublicId());
        }
    }
    
    // 2. Xóa record trong DB (CASCADE sẽ xóa Product_Images)
    productRepository.deleteById(productId);
}
```

---

## 🐛 Troubleshooting

### Lỗi: "Cloudinary chưa được cấu hình"

**Nguyên nhân:**
- Thiếu cấu hình trong `application.properties`
- Environment variables chưa được set

**Giải pháp:**
1. Kiểm tra `application.properties`
2. Set environment variables
3. Restart backend

### Lỗi: "Upload failed: Invalid API credentials"

**Nguyên nhân:**
- API Key/Secret sai
- Cloud Name sai

**Giải pháp:**
1. Kiểm tra lại credentials trong Cloudinary Dashboard
2. Update `application.properties`
3. Restart backend

### Lỗi: "File quá lớn"

**Nguyên nhân:**
- File > 5MB (giới hạn trong code)
- Cloudinary free plan: max 10MB

**Giải pháp:**
- Compress ảnh trước khi upload
- Tăng limit trong `ProductConstants.MAX_IMAGE_FILE_SIZE`

### Ảnh không hiển thị

**Nguyên nhân:**
- URL sai
- CORS issue
- Cloudinary asset bị xóa

**Giải pháp:**
1. Kiểm tra URL trong database
2. Test URL trực tiếp trong browser
3. Kiểm tra Cloudinary Dashboard → Media Library

### Performance chậm

**Nguyên nhân:**
- Ảnh quá lớn, chưa optimize
- Chưa dùng transformations
- Chưa lazy loading

**Giải pháp:**
1. Thêm transformations khi upload
2. Sử dụng `f_auto` (auto format)
3. Implement lazy loading
4. Sử dụng CDN (Cloudinary tự động)

---

## 📖 Tài liệu tham khảo

- [Cloudinary Documentation](https://cloudinary.com/documentation)
- [Cloudinary Java SDK](https://cloudinary.com/documentation/java_integration)
- [Image Transformations](https://cloudinary.com/documentation/image_transformations)
- [Upload API](https://cloudinary.com/documentation/upload_images)

---

## ✅ Checklist triển khai

- [ ] Tạo tài khoản Cloudinary
- [ ] Cấu hình API credentials
- [ ] Test upload ảnh
- [ ] Kiểm tra URL trong database
- [ ] Test hiển thị ảnh trên frontend
- [ ] Implement lazy loading
- [ ] Thêm transformations
- [ ] Test xóa ảnh
- [ ] Monitor usage trong Cloudinary Dashboard

---

**Tác giả**: Sneakery Store Team  
**Cập nhật**: 2024  
**Version**: 1.0

