# 🌿 Hướng dẫn làm việc với Branch (Nhánh) - Dành cho nhóm

## 📋 Danh sách Branch cho từng thành viên

| Thành viên | Tên Branch |
|------------|------------|
| 🧑‍💼 **Thơ** | `member-tho` |
| 👨‍💻 **Tuấn Anh** | `member-tuan-anh` |
| 👨‍💼 **Dương** | `member-duong` |
| 👨‍💼 **Phú** | `member-phu` |

---

## 🚀 Hướng dẫn setup cho từng thành viên

### Bước 1: Clone dựta án về máy (Lần đầu tiên)

Mở **Git Bash** hoặc **PowerShell** và chạy lệnh:

```bash
# Clone dự án từ GitHub
git clone https://github.com/p1mp1m/sneakery-store.git

# Vào thư mục dự án
cd sneakery-store
```

> **💡 Lưu ý:** Chỉ cần clone **1 lần duy nhất** lần đầu tiên!

---

### Bước 2: Chuyển sang branch của bạn

Sau khi đã clone dự án, bạn cần chuyển sang branch của mình:

#### 🤔 Branch của bạn là gì?
- **Thơ**: `member-tho`
- **Tuấn Anh**: `member-tuan-anh`
- **Dương**: `member-duong`
- **Phú**: `member-phu`

#### 📝 Lệnh chuyển branch:

**Nếu bạn là Thơ:**
```bash
git checkout member-tho
```

**Nếu bạn là Tuấn Anh:**
```bash
git checkout member-tuan-anh
```

**Nếu bạn là Dương:**
```bash
git checkout member-duong
```

**Nếu bạn là Phú:**
```bash
git checkout member-phu
```

✅ **Kết quả:** Bạn sẽ thấy dòng thông báo: `Switched to branch 'member-[tên-bạn]'`

---

### Bước 3: Kiểm tra branch hiện tại

Để xem bạn đang ở branch nào:

```bash
git branch
```

✅ **Kết quả:** Branch của bạn sẽ có dấu `*` màu xanh lá

---

### Bước 4: Kéo code mới nhất từ GitHub

Trước khi bắt đầu làm việc, luôn kéo code mới nhất:

```bash
# Kéo code mới nhất
git pull origin main

# Sau đó chuyển về branch của bạn (nếu bị chuyển nhầm)
git checkout member-[tên-bạn]
```

---

## 💻 Quy trình làm việc hàng ngày

### 1️⃣ **Mở dự án**

```bash
cd sneakery-store
```

### 2️⃣ **Chuyển sang branch của bạn**

```bash
git checkout member-[tên-bạn]
```

### 3️⃣ **Kéo code mới nhất từ main**

```bash
# Cập nhật branch main
git checkout main
git pull origin main

# Chuyển lại branch của bạn
git checkout member-[tên-bạn]

# Cập nhật branch của bạn với code mới từ main
git merge main
```

### 4️⃣ **Làm việc và commit code**

Sau khi code xong một chức năng:

```bash
# Xem những file đã thay đổi
git status

# Thêm tất cả file đã sửa
git add .

# Hoặc thêm từng file
git add ten-file.java

# Commit (ghi chú những gì đã làm)
git commit -m "Thêm chức năng đăng nhập"

# Push lên GitHub
git push origin member-[tên-bạn]
```

---

## 🆚 Sự khác biệt giữa main và branch của bạn

### 🌲 Branch `main` (Nhánh chính)
- ✅ Branch **chính** của dự án
- ✅ Chứa code **hoàn chỉnh**, **đã test kỹ**
- ⚠️ **KHÔNG được commit** trực tiếp vào main
- ✅ Chỉ được merge từ các branch khác khi code đã được review

### 🌿 Branch `member-[tên]` (Branch của bạn)
- ✅ Branch **riêng** của từng người
- ✅ Tự do code, test bare bones
- ✅ Push code lên đây bất cứ lúc nào
- ✅ Sau khi code xong, tạo Pull Request để merge vào main

---

## 🔄 Cách đồng bộ code mới từ main sang branch của bạn

Khi có người khác đã push code lên main, bạn cần lấy code mới về:

```bash
# 1. Chuyển sang main
git checkout main

# 2. Kéo code mới nhất
git pull origin main

# 3. Chuyển về branch của bạn
git checkout member-[tên-bạn]

# 4. Cập nhật branch của bạn với code mới từ main
git merge main

# 5. Nếu có conflict, giải quyết conflict rồi:
git add .
git commit -m "Merge main into member-[tên-bạn]"
git push origin member-[tên-bạn]
```

---

## 🔀 Hướng dẫn Merge code vào main (Pull Request)

Sau khi bạn đã code xong một chức năng trên branch của mình:

### Cách 1: Trên GitHub (Khuyến nghị)

1. 🌐 Vào: https://github.com/p1mp1m/sneakery-store
2. 📊 Click tab **"Pull requests"**
3. 🆕 Click nút **"New pull request"**
4. 🔀 Chọn:
   - **Base:** `main`
   - **Compare:** `member-[tên-bạn]`
5. ✍️ Viết mô tả những gì đã làm
6. 👥 Tag team members để review
7. ✅ Sau khi được approve, click **"Merge pull request"**

### Cách 2: Dùng Git (Nâng cao)

```bash
# 1. Chuyển sang main
git checkout main

# 2. Pull code mới nhất
git pull origin main

# 3. Merge branch của bạn vào main
git merge member-[tên-bạn]

# 4. Push lên GitHub
git push origin main
```

> **⚠️ Chú ý:** Chỉ dùng cách 2 nếu bạn là **project lead** hoặc **code owner**!

---

## ❌ Xử lý conflict (Khi code bị trùng lặp)

### Khi nào bị conflict?

Khi 2 người cùng sửa **cùng một dòng code**, Git không biết giữ code của ai. Đó là conflict!

### Cách xử lý:

#### 1. **Git sẽ đánh dấu conflict:**

```java
<<<<<<< HEAD
// Code của bạn
=======
// Code của người khác
>>>>>>> main
```

#### 2. **Mở file bị conflict trong VS Code**

Bạn sẽ thấy các nút:
- **Accept Current Change** - Giữ code của bạn
- **Accept Incoming Change** - Giữ code của người khác
- **Accept Both Changes** - Giữ cả hai
- **Compare Changes** - So sánh cả hai

#### 3. **Chọn giải pháp phù hợp**

Thường nên:
- ✅ Giữ **cả hai** nếu code không liên quan
- ✅ Giữ **của bạn** nếu code mới của bạn tốt hơn
- ✅ Giữ **của người khác** nếu họ đã sửa đúng hơn

#### 4. **Sau khi sửa xong:**

```bash
git add .
git commit -m "Giải quyết conflict với main"
git push origin member-[tên-bạn]
```

---

## 🎯 Checklist hàng ngày

Trước khi bắt đầu code:

- [ ] ✅ Đã `git pull origin main`
- [ ] ✅ Đã `git merge main` vào branch của bạn
- [ ] ✅ Đã giải quyết tất cả conflict (nếu có)
- [ ] ✅ Code đang chạy được (build thành công)

Trước khi push code:

- [ ] ✅ Đã test code xong
- [ ] ✅ Code không có lỗi (không có error đỏ)
- [ ] ✅ Đã commit với message rõ ràng
- [ ] ✅ Đã push lên đúng branch của mình

---

## 🆘 Các lệnh Git thường dùng

| Lệnh | Ý nghĩa |
|------|---------|
| `git status` | Xem trạng thái (file nào đã sửa, thêm, xóa) |
| `git branch` | Xem danh sách branch, branch nào đang active |
| `git checkout main` | Chuyển sang branch main |
| `git checkout member-[tên]` | Chuyển sang branch của bạn |
| `git add .` | Thêm tất cả file đã sửa |
| `git commit -m "Mô tả"` | Lưu thay đổi với ghi chú |
| `git push origin [tên-branch]` | Đẩy code lên GitHub |
| `git pull origin main` | Kéo code mới từ main |
| `git merge main` | Cập nhật branch hiện tại bằng code từ main |
| `git log` | Xem lịch sử commit |

---

## 💡 Lời khuyên cho nhóm

### ✅ NÊN LÀM:
1. ✅ **Thường xuyên push code** lên branch của mình (ít nhất 1 lần/ngày)
2. ✅ **Pull code từ main** trước khi bắt đầu làm việc mới
3. ✅ **Commit với message rõ ràng**: "Thêm chức năng A", "Sửa lỗi B"
4. ✅ **Test code trước khi push**
5. ✅ **Merge code vào main** khi chức năng đã hoàn thiện

### ❌ KHÔNG NÊN LÀM:
1. ❌ Commit trực tiếp vào branch `main`
2. ❌ Push code bị lỗi (error đỏ) lên GitHub
3. ❌ Commit với message dài dòng không rõ ràng
4. ❌ Làm việc lâu ngày không push code
5. ❌ Merge code vào main mà không cho team biết

---

## 📞 Cần hỗ trợ?

Nếu gặp vấn đề, hãy:

1. 📖 Đọc lại hướng dẫn này
2. 🔍 Search trên Google: "git [vấn đề của bạn]"
3. 💬 Hỏi trực tiếp trong nhóm chat
4. 🎥 Xem video hướng dẫn Git trên YouTube

---

## 🎉 Chúc các bạn làm việc vui vẻ!

**Người tạo:** [Tên bạn]
**Ngày tạo:** [Ngày]
**Repository:** https://github.com/p1mp1m/sneakery-store

