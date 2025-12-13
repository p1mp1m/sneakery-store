# Cloudinary Code Examples

Tài liệu này chứa các ví dụ code thực tế về cách sử dụng Cloudinary trong dự án Sneakery Store.

## 📋 Mục lục

1. [Backend Examples](#backend-examples)
2. [Frontend Examples](#frontend-examples)
3. [Common Use Cases](#common-use-cases)
4. [Error Handling](#error-handling)

---

## 🔧 Backend Examples

### 1. Upload ảnh sản phẩm

```java
// File: ProductImageController.java
@PostMapping(value = "/upload", consumes = "multipart/form-data")
public ResponseEntity<ProductImageDto> uploadProductImage(
        @PathVariable Long productId,
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
        @RequestParam(value = "displayOrder", required = false) Integer displayOrder) {
    
    // Validate file
    FileValidationUtil.validateImageFile(file);
    
    // Upload và lưu vào DB
    ProductImageDto dto = productImageService.uploadImageFile(
        productId, 
        file, 
        isPrimary, 
        displayOrder
    );
    
    return ResponseEntity.ok(dto);
}
```

**Request:**
```http
POST /api/admin/products/123/images/upload
Content-Type: multipart/form-data

file: [binary image data]
isPrimary: true
displayOrder: 1
```

**Response:**
```json
{
  "id": 456,
  "productId": 123,
  "imageUrl": "https://res.cloudinary.com/djk6lqgu7/image/upload/v1234567890/uploads/sanpham/123/abc123.jpg",
  "cloudinaryPublicId": "uploads/sanpham/123/abc123",
  "thumbnailUrl": "https://res.cloudinary.com/djk6lqgu7/image/upload/w_300,h_300,c_limit,q_auto,f_auto/uploads/sanpham/123/abc123.jpg",
  "mediumUrl": "https://res.cloudinary.com/djk6lqgu7/image/upload/w_800,h_800,c_limit,q_auto,f_auto/uploads/sanpham/123/abc123.jpg",
  "largeUrl": "https://res.cloudinary.com/djk6lqgu7/image/upload/w_1200,h_1200,c_limit,q_auto,f_auto/uploads/sanpham/123/abc123.jpg",
  "isPrimary": true,
  "displayOrder": 1
}
```

### 2. Lấy danh sách ảnh sản phẩm

```java
// File: ProductImageController.java
@GetMapping
public ResponseEntity<List<ProductImageDto>> getProductImages(@PathVariable Long productId) {
    List<ProductImageDto> images = productImageService.getProductImages(productId);
    return ResponseEntity.ok(images);
}
```

**Request:**
```http
GET /api/admin/products/123/images
```

**Response:**
```json
[
  {
    "id": 456,
    "productId": 123,
    "imageUrl": "https://res.cloudinary.com/.../image.jpg",
    "thumbnailUrl": "https://res.cloudinary.com/.../w_300,h_300/.../image.jpg",
    "mediumUrl": "https://res.cloudinary.com/.../w_800,h_800/.../image.jpg",
    "largeUrl": "https://res.cloudinary.com/.../w_1200,h_1200/.../image.jpg",
    "isPrimary": true,
    "displayOrder": 1
  }
]
```

### 3. Xóa ảnh sản phẩm

```java
// File: ProductImageController.java
@DeleteMapping("/{imageId}")
public ResponseEntity<Void> deleteProductImage(
        @PathVariable Long productId,
        @PathVariable Long imageId) {
    productImageService.deleteProductImage(imageId);
    return ResponseEntity.noContent().build();
}
```

**Request:**
```http
DELETE /api/admin/products/123/images/456
```

### 4. Sử dụng CloudinaryUtil để tạo optimized URLs

```java
// File: ProductService.java
@Autowired
private CloudinaryUtil cloudinaryUtil;

public ProductDetailDto getProductDetail(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"));
    
    String imageUrl = product.getImageUrl();
    
    // Generate optimized URLs
    String thumbnailUrl = cloudinaryUtil.generateThumbnailUrl(imageUrl);
    String mediumUrl = cloudinaryUtil.generateMediumUrl(imageUrl);
    String largeUrl = cloudinaryUtil.generateLargeUrl(imageUrl);
    
    // Custom transformation
    String customUrl = cloudinaryUtil.generateOptimizedUrl(
        imageUrl,
        500,    // width
        500,    // height
        "fill", // crop
        "90",   // quality
        "webp"  // format
    );
    
    return ProductDetailDto.builder()
        .id(product.getId())
        .name(product.getName())
        .imageUrl(imageUrl)
        .thumbnailUrl(thumbnailUrl)
        .mediumUrl(mediumUrl)
        .largeUrl(largeUrl)
        .build();
}
```

### 5. Xử lý lỗi upload

```java
// File: FileStorageService.java
public CloudinaryUploadResult storeProductImage(Long productId, MultipartFile file) {
    try {
        // Validate
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File rỗng");
        }
        
        // Upload
        Map<?, ?> res = cloudinary.uploader().upload(
            file.getBytes(),
            ObjectUtils.asMap(
                "folder", "uploads/sanpham/" + productId,
                "transformation", Arrays.asList(
                    new Transformation()
                        .width(1200)
                        .height(1200)
                        .crop("limit")
                        .quality("auto")
                        .fetchFormat("auto")
                )
            )
        );
        
        String url = res.get("secure_url").toString();
        String publicId = res.get("public_id").toString();
        
        return new CloudinaryUploadResult(url, publicId);
        
    } catch (IOException e) {
        log.error("❌ Upload lỗi: {}", e.getMessage(), e);
        throw new ApiException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Không thể upload file: " + e.getMessage()
        );
    }
}
```

---

## 🎨 Frontend Examples

### 1. Upload ảnh từ VueJS

```vue
<template>
  <div>
    <input 
      type="file" 
      @change="handleFileSelect"
      accept="image/jpeg,image/png,image/webp"
    />
    <button @click="uploadImage" :disabled="!selectedFile || uploading">
      {{ uploading ? 'Đang upload...' : 'Upload ảnh' }}
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { toastService } from '@/utils/toastService'

const props = defineProps({
  productId: {
    type: Number,
    required: true
  }
})

const selectedFile = ref(null)
const uploading = ref(false)

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      toastService.error('Lỗi', 'File quá lớn (tối đa 5MB)')
      return
    }
    
    // Validate file type
    const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
    if (!allowedTypes.includes(file.type)) {
      toastService.error('Lỗi', 'Định dạng file không hợp lệ')
      return
    }
    
    selectedFile.value = file
  }
}

const uploadImage = async () => {
  if (!selectedFile.value) return
  
  uploading.value = true
  
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('isPrimary', 'false')
    formData.append('displayOrder', '1')
    
    const response = await axios.post(
      `/api/admin/products/${props.productId}/images/upload`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    )
    
    toastService.success('Thành công', 'Upload ảnh thành công!')
    emit('uploaded', response.data)
    selectedFile.value = null
    
  } catch (error) {
    console.error('Upload error:', error)
    
    if (error.response?.status === 413) {
      toastService.error('Lỗi', 'File quá lớn (tối đa 5MB)')
    } else if (error.response?.status === 400) {
      toastService.error('Lỗi', error.response.data?.message || 'Định dạng file không hợp lệ')
    } else {
      toastService.error('Lỗi', 'Upload thất bại. Vui lòng thử lại.')
    }
  } finally {
    uploading.value = false
  }
}

const emit = defineEmits(['uploaded'])
</script>
```

### 2. Hiển thị ảnh với LazyImage component

```vue
<template>
  <div class="product-gallery">
    <!-- Primary image -->
    <LazyImage
      :src="primaryImage?.imageUrl || '/placeholder-image.png'"
      :alt="product.name"
      size="large"
      container-class="w-full h-96 rounded-lg overflow-hidden"
      image-class="object-cover"
    />
    
    <!-- Thumbnail gallery -->
    <div class="flex gap-2 mt-4">
      <LazyImage
        v-for="image in productImages"
        :key="image.id"
        :src="image.thumbnailUrl || image.imageUrl"
        :alt="product.name"
        size="thumbnail"
        container-class="w-20 h-20 rounded cursor-pointer border-2"
        :class="{ 'border-purple-600': image.isPrimary }"
        @click="selectImage(image)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import LazyImage from '@/components/common/LazyImage.vue'
import axios from 'axios'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const productImages = ref([])
const selectedImage = ref(null)

const primaryImage = computed(() => {
  return productImages.value.find(img => img.isPrimary) || productImages.value[0]
})

const loadProductImages = async () => {
  try {
    const response = await axios.get(`/api/admin/products/${props.product.id}/images`)
    productImages.value = response.data
  } catch (error) {
    console.error('Error loading images:', error)
  }
}

const selectImage = (image) => {
  selectedImage.value = image
}

onMounted(() => {
  loadProductImages()
})
</script>
```

### 3. Sử dụng cloudinaryHelper để optimize URLs

```vue
<template>
  <div>
    <!-- Thumbnail -->
    <img :src="thumbnailUrl" alt="Thumbnail" />
    
    <!-- Medium size -->
    <img :src="mediumUrl" alt="Medium" />
    
    <!-- Large size -->
    <img :src="largeUrl" alt="Large" />
    
    <!-- Custom size -->
    <img :src="customUrl" alt="Custom" />
    
    <!-- Responsive image -->
    <img 
      :src="imageUrl"
      :srcset="srcset"
      :sizes="sizes"
      alt="Responsive"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  getThumbnailUrl,
  getMediumUrl,
  getLargeUrl,
  getOptimizedImageUrl,
  generateSrcset,
  generateSizes
} from '@/utils/cloudinaryHelper'

const props = defineProps({
  imageUrl: {
    type: String,
    required: true
  }
})

const thumbnailUrl = computed(() => getThumbnailUrl(props.imageUrl))
const mediumUrl = computed(() => getMediumUrl(props.imageUrl))
const largeUrl = computed(() => getLargeUrl(props.imageUrl))

const customUrl = computed(() => 
  getOptimizedImageUrl(props.imageUrl, {
    width: 500,
    height: 500,
    crop: 'fill',
    quality: '90',
    format: 'webp'
  })
)

const srcset = computed(() => generateSrcset(props.imageUrl, [300, 600, 1200]))
const sizes = computed(() => generateSizes())
</script>
```

### 4. ProductCard với optimized images

```vue
<template>
  <div class="product-card">
    <LazyImage
      :src="product.mainImageUrl || product.imageUrl"
      :alt="product.name"
      size="medium"
      container-class="w-full aspect-square rounded-lg overflow-hidden"
      image-class="object-cover hover:scale-105 transition-transform duration-300"
    />
    <h3>{{ product.name }}</h3>
    <p>{{ formatCurrency(product.price) }}</p>
  </div>
</template>

<script setup>
import LazyImage from '@/components/common/LazyImage.vue'
import { formatCurrency } from '@/utils/formatters'

defineProps({
  product: {
    type: Object,
    required: true
  }
})
</script>
```

### 5. Image gallery với lazy loading

```vue
<template>
  <div class="image-gallery">
    <div 
      v-for="image in images" 
      :key="image.id"
      class="gallery-item"
    >
      <LazyImage
        :src="image.imageUrl"
        :alt="image.altText || product.name"
        size="medium"
        root-margin="100px"
        container-class="w-full h-64 rounded-lg overflow-hidden"
        image-class="object-cover"
        @load="onImageLoad(image)"
        @error="onImageError(image)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import LazyImage from '@/components/common/LazyImage.vue'

const props = defineProps({
  images: {
    type: Array,
    required: true
  },
  product: {
    type: Object,
    required: true
  }
})

const loadedImages = ref(new Set())

const onImageLoad = (image) => {
  loadedImages.value.add(image.id)
  console.log('Image loaded:', image.id)
}

const onImageError = (image) => {
  console.error('Image load error:', image.id)
}
</script>
```

---

## 💡 Common Use Cases

### 1. Upload multiple images

```javascript
// Frontend
const uploadMultipleImages = async (productId, files) => {
  const uploadPromises = files.map((file, index) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('isPrimary', index === 0 ? 'true' : 'false')
    formData.append('displayOrder', String(index + 1))
    
    return axios.post(
      `/api/admin/products/${productId}/images/upload`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    )
  })
  
  try {
    const results = await Promise.all(uploadPromises)
    return results.map(r => r.data)
  } catch (error) {
    console.error('Upload error:', error)
    throw error
  }
}
```

### 2. Replace primary image

```javascript
// Frontend
const setPrimaryImage = async (productId, imageId) => {
  try {
    await axios.put(
      `/api/admin/products/${productId}/images/${imageId}`,
      { isPrimary: true }
    )
    toastService.success('Thành công', 'Đã đặt làm ảnh chính')
  } catch (error) {
    toastService.error('Lỗi', 'Không thể đặt ảnh chính')
  }
}
```

### 3. Delete image with confirmation

```javascript
// Frontend
const deleteImage = async (productId, imageId) => {
  const confirmed = await confirmDialogService.confirm(
    'Xác nhận',
    'Bạn có chắc chắn muốn xóa ảnh này?'
  )
  
  if (!confirmed) return
  
  try {
    await axios.delete(`/api/admin/products/${productId}/images/${imageId}`)
    toastService.success('Thành công', 'Đã xóa ảnh')
    emit('deleted', imageId)
  } catch (error) {
    toastService.error('Lỗi', 'Không thể xóa ảnh')
  }
}
```

### 4. Preview image before upload

```vue
<template>
  <div>
    <input type="file" @change="handleFileSelect" accept="image/*" />
    
    <div v-if="previewUrl" class="preview">
      <img :src="previewUrl" alt="Preview" class="w-64 h-64 object-cover" />
      <button @click="clearPreview">Xóa</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const previewUrl = ref(null)

const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    // Tạo preview URL từ file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const clearPreview = () => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = null
  }
}
</script>
```

---

## ⚠️ Error Handling

### Backend Error Handling

```java
@ExceptionHandler(MaxUploadSizeExceededException.class)
public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
        MaxUploadSizeExceededException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .message("File quá lớn. Tối đa 5MB")
        .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
        .timestamp(LocalDateTime.now())
        .build();
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
}

@ExceptionHandler(MultipartException.class)
public ResponseEntity<ErrorResponse> handleMultipartException(
        MultipartException ex) {
    ErrorResponse error = ErrorResponse.builder()
        .message("Lỗi khi xử lý file upload")
        .status(HttpStatus.BAD_REQUEST.value())
        .timestamp(LocalDateTime.now())
        .build();
    return ResponseEntity.badRequest().body(error);
}
```

### Frontend Error Handling

```javascript
// Error handler utility
export const handleUploadError = (error) => {
  if (error.response) {
    const status = error.response.status
    const message = error.response.data?.message || 'Có lỗi xảy ra'
    
    switch (status) {
      case 400:
        toastService.error('Lỗi', message)
        break
      case 413:
        toastService.error('Lỗi', 'File quá lớn (tối đa 5MB)')
        break
      case 500:
        toastService.error('Lỗi', 'Lỗi server. Vui lòng thử lại sau')
        break
      default:
        toastService.error('Lỗi', message)
    }
  } else if (error.request) {
    toastService.error('Lỗi', 'Không thể kết nối đến server')
  } else {
    toastService.error('Lỗi', 'Có lỗi không xác định')
  }
}
```

---

## 📝 Best Practices

1. **Luôn validate file trước khi upload**
   - Kiểm tra size (max 5MB)
   - Kiểm tra type (chỉ JPG, PNG, WEBP)
   - Kiểm tra extension

2. **Sử dụng lazy loading cho images**
   - Giảm initial load time
   - Tối ưu bandwidth

3. **Sử dụng optimized URLs**
   - Thumbnail cho list view
   - Medium cho detail view
   - Large cho fullscreen

4. **Error handling đầy đủ**
   - Hiển thị message rõ ràng
   - Fallback images
   - Retry mechanism

5. **Cleanup resources**
   - Revoke object URLs sau khi dùng
   - Xóa ảnh trên Cloudinary khi xóa product

---

**Tác giả**: Sneakery Store Team  
**Cập nhật**: 2024  
**Version**: 1.0

