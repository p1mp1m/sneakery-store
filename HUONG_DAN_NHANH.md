# ⚡ Hướng Dẫn Nhanh - Làm Việc với Git Branch

## 🎯 Cho Ai?

File này dành cho **4 thành viên trong nhóm**:
- 👨‍💼 **Thơ**
- 👨‍💻 **Tuấn Anh**
- 👨‍💼 **Dương**
- 👨‍💼 **Phú**

---

## 📦 Bước 1: Clone dự án (Lần đầu tiên)

```bash
git clone https://github.com/p1mp1m/sneakery-store.git
cd sneakery-store
```

---

## 🎨 Bước 2: Chuyển sang branch của bạn

**Chạy lệnh tương ứng với tên bạn:**

```bash
# Nếu bạn là Thơ
git checkout member-tho

# Nếu bạn là Tuấn Anh
git checkout member-tuan-anh

# Nếu bạn là Dương
git checkout member-duong

# Nếu bạn là Phú
git checkout member-phu
```

---

## 💻 Quy trình làm việc hàng ngày

### ⏰ **Mỗi sáng (Trước khi code):**

```bash
# 1. Cập nhật code mới nhất từ main
git checkout main
git pull origin main

# 2. Về branch của bạn
git checkout member-[tên-bạn]

# 3. Merge code mới vào branch của bạn
git merge main

# 4. Push lên GitHub (nếu có thay đổi)
git push origin member-[tên-bạn]
```

### ⌨️ **Trong khi code:**

```bash
# Code bình thường, không cần chạy lệnh gì
```

### 🏁 **Cuối ngày (Sau khi code xong):**

```bash
# 1. Xem những gì đã sửa
git status

# 2. Thêm tất cả file
git add .

# 3. Lưu lại với ghi chú
git commit -m "Mô tả những gì đã làm"

# 4. Đẩy lên GitHub
git push origin member-[tên-bạn]
```

---

## 🆘 Các tình huống thường gặp

### ❓ Muốn xem đang ở branch nào?

```bash
git branch
```

### ❓ Muốn xem những file nào đã sửa?

```bash
git status
```

### ❓ Đã sửa file A, nhưng muốn quay lại như cũ?

```bash
git checkout -- tên-file.txt
```

### ❓ Bị conflict khi merge?

1. Mở file bị conflict trong VS Code
2. Tìm các dòng có `<<<<<<<`, `=======`, `>>>>>>>`
3. Sửa lại code cho đúng
4. Xóa các dòng `<<<<<<<`, `=======`, `>>>>>>>`
5. Chạy:
```bash
git add .
git commit -m "Giải quyết conflict"
git push origin member-[tên-bạn]
```

---

## 📋 Lệnh Git quan trọng

| Lệnh | Dùng khi nào? |
|------|---------------|
| `git status` | Muốn xem file nào đã sửa |
| `git branch` | Muốn biết đang ở branch nào |
| `git checkout [tên-branch]` | Muốn chuyển sang branch khác |
| `git add .` | Sau khi sửa code xong |
| `git commit -m "Mô tả"` | Muốn lưu lại những gì đã làm |
| `git push origin [tên-branch]` | Muốn đẩy code lên GitHub |
| `git pull origin main` | Muốn kéo code mới từ main |
| `git merge main` | Muốn cập nhật branch của bạn với code mới |

---

## ✅ Checklist trước khi push code

- [ ] Đã test code xong (không có lỗi đỏ)
- [ ] Đã chạy `git status` để kiểm tra file
- [ ] Đã chạy `git add .`
- [ ] Đã chạy `git commit -m "Mô tả rõ ràng"`
- [ ] Đang ở đúng branch của mình (`member-[tên-bạn]`)
- [ ] Đã chạy `git push origin member-[tên-bạn]`

---

## 🎯 Lưu ý quan trọng

1. ⚠️ **Đừng commit trực tiếp vào branch `main`**
2. ✅ **Luôn làm việc trên branch của mình**
3. ✅ **Thường xuyên push code lên GitHub** (ít nhất 1 lần/ngày)
4. ✅ **Test code trước khi push**
5. ✅ **Commit với message rõ ràng**

---

## 📞 Cần giúp đỡ?

📖 Đọc file **HUONG_DAN_BRANCH.md** để biết chi tiết hơn.

🎥 Hoặc search Google: "git tutorial" + tình huống của bạn

---

**🌿 Chúc bạn làm việc hiệu quả!**

