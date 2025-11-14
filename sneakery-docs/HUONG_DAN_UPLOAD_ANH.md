# Hướng dẫn Upload Ảnh Sản Phẩm

## 📋 Tổng quan

Hệ thống hỗ trợ upload ảnh sản phẩm lên Cloudinary với các tính năng:
- ✅ Tự động resize và optimize
- ✅ Tự động tạo thumbnail/medium/large URLs
- ✅ Hỗ trợ nhiều ảnh cho 1 sản phẩm (gallery)
- ✅ Đánh dấu ảnh chính (primary)

---

## 🎨 Cách 1: Upload qua Admin Panel (UI) - HƯỚNG DẪN CHI TIẾT

### 📍 Bước 1: Truy cập Admin Panel

1. **Mở trình duyệt** và truy cập: 
   ```
   http://localhost:5173/admin/products
   ```

2. **Đăng nhập** với tài khoản Admin:
   - Email: `admin@sneakery.com`
   - Password: `password` (hoặc password bạn đã đặt)

3. Sau khi đăng nhập thành công, bạn sẽ thấy trang **"Quản lý sản phẩm"**

---

### 📍 Bước 2: Chọn sản phẩm cần thêm ảnh

**Có 2 trường hợp:**

#### Trường hợp A: Tạo sản phẩm mới
1. Click nút **"Thêm sản phẩm"** (màu tím, góc trên bên phải)
2. Điền thông tin sản phẩm cơ bản:
   - Tên sản phẩm
   - Thương hiệu (Brand)
   - Danh mục (Categories)
   - Mô tả
   - Variants (biến thể: size, màu, giá, số lượng)
3. **Lưu ý**: Bạn có thể upload ảnh ngay khi tạo mới HOẶC upload sau khi đã tạo sản phẩm

#### Trường hợp B: Thêm ảnh cho sản phẩm đã có
1. Tìm sản phẩm trong danh sách
2. Click nút **"Sửa"** (icon bút chì) ở cột "Thao tác"
3. Modal chỉnh sửa sản phẩm sẽ hiện ra

---

### 📍 Bước 3: Upload ảnh sản phẩm

Trong form sản phẩm (tạo mới hoặc chỉnh sửa), bạn sẽ thấy phần **"Hình ảnh sản phẩm"**:

#### 3.1. Upload ảnh từ máy tính

**Cách 1: Kéo thả (Drag & Drop)**
1. Mở thư mục chứa ảnh trên máy tính
2. Kéo file ảnh và thả vào vùng upload (có viền nét đứt)
3. Thả file vào, ảnh sẽ tự động được thêm vào gallery

**Cách 2: Click để chọn file**
1. Click vào vùng upload (có text "Kéo thả ảnh vào đây" hoặc "Click để chọn file")
2. Chọn file ảnh từ máy tính (có thể chọn nhiều file cùng lúc)
3. Click "Open" hoặc "Chọn"

**Lưu ý:**
- ✅ Định dạng hỗ trợ: **JPG, JPEG, PNG, WEBP**
- ✅ Kích thước tối đa: **5MB** mỗi file
- ✅ Có thể upload **nhiều ảnh cùng lúc** (khuyến nghị 3-5 ảnh/sản phẩm)

#### 3.2. Thêm ảnh từ URL (tùy chọn)

Nếu bạn có URL ảnh từ nguồn khác (ví dụ: Unsplash, Pexels):
1. Click nút **"Thêm từ URL"** hoặc icon link
2. Nhập URL ảnh vào ô input
3. Click "Thêm" hoặc Enter
4. Ảnh sẽ được thêm vào gallery

---

### 📍 Bước 4: Quản lý ảnh trong gallery

Sau khi upload, bạn sẽ thấy các ảnh hiển thị trong gallery với các tính năng:

#### 4.1. Đánh dấu ảnh chính (Primary Image)
- **Cách 1**: Click vào icon ⭐ trên ảnh
- **Cách 2**: Click checkbox "Primary" dưới ảnh
- **Lưu ý**: Chỉ có **1 ảnh chính** cho mỗi sản phẩm. Khi chọn ảnh chính mới, ảnh cũ sẽ tự động bỏ đánh dấu.

#### 4.2. Sắp xếp thứ tự hiển thị
- Kéo thả ảnh để sắp xếp thứ tự
- Ảnh đầu tiên sẽ là ảnh chính mặc định (nếu chưa có ảnh primary)

#### 4.3. Xóa ảnh
- Click icon **🗑️** (thùng rác) trên ảnh cần xóa
- Xác nhận xóa
- **Lưu ý**: Ảnh sẽ bị xóa khỏi Cloudinary và database

#### 4.4. Xem ảnh lớn
- Click vào ảnh để xem kích thước lớn
- Click ra ngoài để đóng

---

### 📍 Bước 5: Lưu sản phẩm

1. Sau khi đã upload và sắp xếp ảnh xong, scroll xuống dưới form
2. Click nút **"Lưu"** hoặc **"Save"** (màu tím)
3. Hệ thống sẽ:
   - ✅ Upload ảnh lên Cloudinary
   - ✅ Lưu URL vào database
   - ✅ Tự động sync `mainImageUrl` nếu có ảnh primary
   - ✅ Tạo các size: thumbnail (300x300), medium (800x800), large (1200x1200)

4. Đợi thông báo **"Lưu thành công"** hoặc **"Save successful"**

---

### 📍 Bước 6: Kiểm tra kết quả

1. **Kiểm tra trên Admin Panel:**
   - Ảnh hiển thị đúng trong gallery
   - Ảnh chính có icon ⭐
   - Thứ tự ảnh đúng như đã sắp xếp

2. **Kiểm tra trên Frontend:**
   - Truy cập trang sản phẩm: `http://localhost:5173/home/products/{productId}`
   - Ảnh hiển thị đúng
   - Gallery ảnh hoạt động tốt

3. **Kiểm tra trên Cloudinary:**
   - Đăng nhập Cloudinary Dashboard
   - Vào Media Library
   - Tìm folder `uploads/sanpham/{productId}/`
   - Xác nhận ảnh đã được upload

---

### 🎯 Mẹo và Best Practices

1. **Chuẩn bị ảnh trước khi upload:**
   - ✅ Kích thước khuyến nghị: 1200x1200px - 2000x2000px
   - ✅ Format: JPG (cho ảnh thường), PNG (cho ảnh có trong suốt)
   - ✅ Đặt tên file có ý nghĩa: `nike-air-max-1.jpg` thay vì `IMG_001.jpg`

2. **Số lượng ảnh:**
   - ✅ Khuyến nghị: **3-5 ảnh** cho mỗi sản phẩm
   - ✅ Ảnh đầu tiên: Tổng quan sản phẩm
   - ✅ Ảnh tiếp theo: Chi tiết, góc nhìn khác, trên người (nếu có)

3. **Ảnh chính (Primary):**
   - ✅ Luôn đánh dấu ảnh đẹp nhất làm ảnh chính
   - ✅ Ảnh chính sẽ hiển thị ở:
     - Danh sách sản phẩm
     - Trang chi tiết sản phẩm (ảnh lớn)
     - Search results
     - Related products

4. **Thứ tự hiển thị:**
   - ✅ Sắp xếp ảnh theo thứ tự logic: tổng quan → chi tiết → góc nhìn khác
   - ✅ Ảnh chính nên đặt ở vị trí đầu tiên

---

### ⚠️ Xử lý lỗi thường gặp

**Lỗi: "File quá lớn"**
- **Nguyên nhân**: File > 5MB
- **Giải pháp**: Nén ảnh trước khi upload (dùng Photoshop, TinyPNG, hoặc công cụ online)

**Lỗi: "Định dạng không hợp lệ"**
- **Nguyên nhân**: File không phải JPG/PNG/WEBP
- **Giải pháp**: Convert sang JPG hoặc PNG

**Lỗi: "Upload thất bại"**
- **Nguyên nhân**: 
  - Mất kết nối internet
  - Cloudinary credentials chưa đúng
  - Backend chưa chạy
- **Giải pháp**: 
  - Kiểm tra kết nối internet
  - Kiểm tra Cloudinary config trong `application.properties`
  - Kiểm tra backend đang chạy: `http://localhost:8080`

**Ảnh không hiển thị sau khi upload:**
- **Nguyên nhân**: 
  - Chưa lưu sản phẩm
  - Cache trình duyệt
- **Giải pháp**: 
  - Đảm bảo đã click "Lưu"
  - Refresh trang (Ctrl+F5 hoặc Cmd+Shift+R)
  - Xóa cache trình duyệt

---

## 🔧 Cách 2: Upload qua API (Postman/curl)

### API Endpoint

```
POST /api/admin/products/{productId}/images/upload
Content-Type: multipart/form-data
Authorization: Bearer {JWT_TOKEN}
```

### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | File | ✅ Yes | File ảnh cần upload (JPG, PNG, WEBP) |
| `isPrimary` | Boolean | ❌ No | Đánh dấu ảnh chính (default: false) |
| `displayOrder` | Integer | ❌ No | Thứ tự hiển thị (default: tự động) |

### Ví dụ với Postman

1. **Method**: `POST`
2. **URL**: `http://localhost:8080/api/admin/products/1/images/upload`
3. **Headers**:
   ```
   Authorization: Bearer YOUR_JWT_TOKEN
   ```
4. **Body**: 
   - Chọn `form-data`
   - Thêm key `file` (type: File), chọn file ảnh
   - Thêm key `isPrimary` (type: Text), value: `true` hoặc `false`
   - Thêm key `displayOrder` (type: Text), value: `1` (optional)

### Ví dụ với curl

```bash
curl -X POST \
  http://localhost:8080/api/admin/products/1/images/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/image.jpg" \
  -F "isPrimary=true" \
  -F "displayOrder=1"
```

### Response thành công

```json
{
  "id": 123,
  "productId": 1,
  "imageUrl": "https://res.cloudinary.com/dnznhji35/image/upload/v1234567890/uploads/sanpham/1/abc123.jpg",
  "cloudinaryPublicId": "uploads/sanpham/1/abc123",
  "thumbnailUrl": "https://res.cloudinary.com/dnznhji35/image/upload/w_300,h_300,c_limit,q_auto,f_auto/uploads/sanpham/1/abc123.jpg",
  "mediumUrl": "https://res.cloudinary.com/dnznhji35/image/upload/w_800,h_800,c_limit,q_auto,f_auto/uploads/sanpham/1/abc123.jpg",
  "largeUrl": "https://res.cloudinary.com/dnznhji35/image/upload/w_1200,h_1200,c_limit,q_auto,f_auto/uploads/sanpham/1/abc123.jpg",
  "isPrimary": true,
  "displayOrder": 1
}
```

---

## 📝 Các API khác liên quan

### 1. Lấy danh sách ảnh của sản phẩm

```
GET /api/admin/products/{productId}/images
```

**Response:**
```json
[
  {
    "id": 123,
    "imageUrl": "https://res.cloudinary.com/.../image.jpg",
    "thumbnailUrl": "https://res.cloudinary.com/.../w_300,h_300/.../image.jpg",
    "isPrimary": true,
    "displayOrder": 1
  }
]
```

### 2. Xóa ảnh

```
DELETE /api/admin/products/{productId}/images/{imageId}
```

### 3. Đặt ảnh chính

```
POST /api/admin/products/{productId}/images/{imageId}/primary
```

### 4. Thêm ảnh từ URL (không upload file)

```
POST /api/admin/products/{productId}/images
Content-Type: application/json

{
  "imageUrl": "https://example.com/image.jpg",
  "isPrimary": false,
  "displayOrder": 2
}
```

---

## ⚠️ Lưu ý

### File Requirements
- **Định dạng**: JPG, JPEG, PNG, WEBP
- **Kích thước tối đa**: 5MB
- **Khuyến nghị**: 
  - Width: 1200px - 2000px
  - Height: 1200px - 2000px
  - Format: JPG (cho ảnh thường) hoặc PNG (cho ảnh có trong suốt)

### Best Practices
1. **Tên file**: Nên đặt tên file có ý nghĩa (ví dụ: `nike-air-max-1.jpg`)
2. **Kích thước**: Upload ảnh chất lượng cao, Cloudinary sẽ tự động optimize
3. **Ảnh chính**: Mỗi sản phẩm nên có ít nhất 1 ảnh chính (primary)
4. **Số lượng**: Khuyến nghị 3-5 ảnh cho mỗi sản phẩm

### Error Handling

**Lỗi 400 - File quá lớn:**
```json
{
  "message": "Kích thước file vượt quá 5MB. Kích thước hiện tại: 6.5MB"
}
```

**Lỗi 400 - Định dạng không hợp lệ:**
```json
{
  "message": "File type không được hỗ trợ. Chỉ chấp nhận: JPG, PNG, WEBP"
}
```

**Lỗi 401 - Chưa đăng nhập:**
```json
{
  "message": "Unauthorized"
}
```

**Lỗi 404 - Sản phẩm không tồn tại:**
```json
{
  "message": "Sản phẩm không tồn tại"
}
```

---

## 🎯 Ví dụ thực tế

### Upload ảnh cho sản phẩm ID = 1

**Request:**
```bash
curl -X POST \
  http://localhost:8080/api/admin/products/1/images/upload \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -F "file=@/Users/username/Desktop/product-image.jpg" \
  -F "isPrimary=true"
```

**Response:**
```json
{
  "id": 456,
  "productId": 1,
  "imageUrl": "https://res.cloudinary.com/dnznhji35/image/upload/v1700000000/uploads/sanpham/1/product-image.jpg",
  "thumbnailUrl": "https://res.cloudinary.com/dnznhji35/image/upload/w_300,h_300,c_limit,q_auto,f_auto/v1700000000/uploads/sanpham/1/product-image.jpg",
  "mediumUrl": "https://res.cloudinary.com/dnznhji35/image/upload/w_800,h_800,c_limit,q_auto,f_auto/v1700000000/uploads/sanpham/1/product-image.jpg",
  "largeUrl": "https://res.cloudinary.com/dnznhji35/image/upload/w_1200,h_1200,c_limit,q_auto,f_auto/v1700000000/uploads/sanpham/1/product-image.jpg",
  "isPrimary": true,
  "displayOrder": 1
}
```

---

## ✅ Checklist

Trước khi upload:
- [ ] Đã đăng nhập với tài khoản Admin
- [ ] Có JWT token hợp lệ (nếu dùng API)
- [ ] File ảnh đúng định dạng (JPG/PNG/WEBP)
- [ ] File ảnh < 5MB
- [ ] Sản phẩm đã được tạo (có productId)

Sau khi upload:
- [ ] Kiểm tra ảnh hiển thị đúng trên Cloudinary
- [ ] Kiểm tra URL được lưu trong database
- [ ] Test hiển thị ảnh trên frontend
- [ ] Kiểm tra các size URLs (thumbnail, medium, large)

---

**Tác giả**: Sneakery Store Team  
**Cập nhật**: 2024  
**Version**: 1.0

