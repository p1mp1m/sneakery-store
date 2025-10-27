# 🔄 Hướng dẫn Git Workflow - Sneakery Store

## 📖 Mục lục

1. [Giới thiệu](#giới-thiệu)
2. [Các branch chính](#các-branch-chính)
3. [Workflow cơ bản](#workflow-cơ-bản)
4. [Các lệnh Git thường dùng](#các-lệnh-git-thường-dùng)
5. [Pull Request Process](#pull-request-process)
6. [Quy tắc commit message](#quy-tắc-commit-message)
7. [Xử lý lỗi thường gặp](#xử-lý-lỗi-thường-gặp)

---

## 🎯 Giới thiệu

Document này hướng dẫn các thành viên trong nhóm làm việc với Git theo chuẩn **Git Flow** để đảm bảo code luôn sạch và dễ quản lý.

> **💡 Lưu ý:** Luôn làm việc trên branch riêng của mình, KHÔNG BAO GIỜ commit trực tiếp vào `main` branch!

---

## 🌿 Các branch chính

### `main` - Branch production
- ✅ Branch chính, luôn sẵn sàng để deploy
- 🔒 **Protected branch** - không thể push trực tiếp
- 🔴 Chỉ được merge thông qua Pull Request đã được review

### `develop` - Branch development (nếu có)
- Branch để phát triển tính năng mới
- Tất cả feature branch sẽ merge vào đây

### `feature/*` - Branch tính năng
- Tạo từ `develop` hoặc `main`
- Tên: `feature/tên-tính-năng` (vd: `feature/user-login`)
- Dùng để phát triển tính năng mới

### `docs/*` - Branch tài liệu
- Tên: `docs/tên-tài-liệu` (vd: `docs/huong-dan-su-dung`)
- Dùng để chỉnh sửa README, tài liệu

### `bugfix/*` - Branch sửa lỗi
- Tên: `bugfix/mô-tả-lỗi`
- Dùng để sửa lỗi cấp độ cao

### `hotfix/*` - Branch sửa lỗi khẩn cấp
- Tạo từ `main`
- Dùng cho lỗi production cần sửa ngay

---

## 🚀 Workflow cơ bản

### **Bước 1: Clone repository**

```bash
# Clone repo về máy (chỉ làm 1 lần đầu tiên)
git clone https://github.com/p1mp1m/sneakery-store.git
cd sneakery-store
```

---

### **Bước 2: Kiểm tra branch hiện tại**

```bash
# Xem branch đang ở
git branch

# Xem tất cả branch (bao gồm remote)
git branch -a
```

---

### **Bước 3: Cập nhật code mới nhất từ GitHub**

```bash
# Chuyển sang main
git checkout main

# Lấy code mới nhất từ GitHub
git pull origin main
```

---

### **Bước 4: Tạo branch mới cho công việc của bạn**

```bash
# Tạo và chuyển sang branch mới
git checkout -b feature/tên-tính-năng

# Ví dụ:
git checkout -b feature/user-authentication
git checkout -b docs/update-readme
git checkout -b bugfix/fix-login-error
```

> **📌 Tên branch phải:**
> - Viết thường
> - Dùng dấu gạch ngang `-` thay cho space
> - Ngione, dễ hiểu
> - Bắt đầu bằng prefix (feature/, docs/, bugfix/, hotfix/)

---

### **Bước 5: Làm việc và commit**

```bash
# 1. Xem trạng thái các file đã thay đổi
git status

# 2. Thêm file vào staging area
git add tên-file.js        # Thêm 1 file
git add .                   # Thêm tất cả file đã thay đổi
git add folder/             # Thêm cả thư mục

# 3. Commit với message rõ ràng
git commit -m "feat: thêm chức năng đăng nhập"

# Hoặc commit với message dài
git commit -m "feat: thêm chức năng đăng nhập người dùng

- Thêm form đăng nhập với email và password
- Thêm validation cho input
- Thêm thông báo lỗi khi đăng nhập sai"
```

> **⚠️ Lưu ý:** 
> - Commit thường xuyên, mỗi commit nên làm 1 việc
> - Không commit file nhạy cảm (password, API key)
> - Không commit `node_modules`, `.env`

---

### **Bước 6: Push branch lên GitHub**

```bash
# Push branch mới lên GitHub (lần đầu tiên)
git push -u origin feature/tên-tính-năng

# Push các commit tiếp theo (sau lần đầu)
git push
```

---

### **Bước 7: Tạo Pull Request**

1. Vào GitHub: https://github.com/p1mp1m/sneakery-store
2. Click nút **"New Pull Request"**
3. Chọn:
   - **base:** `main` (nhánh sẽ merge vào)
   - **compare:** `feature/tên-tính-năng` (nhánh của bạn)
4. Điền tiêu đề và mô tả:
   - Tiêu đề: Ngon gọn, mô tả thay đổi
   - Mô tả: Chi tiết về những gì đã làm
5. Click **"Create Pull Request"**

---

### **Bước 8: Chờ review và merge**

- ✅ Đợi ít nhất 1 reviewer approve
- ✅ Kiểm tra các check (test, build) đã pass
- ✅ Sau khi được approve, nhấn **"Merge Pull Request"**
- ✅ Xóa branch sau khi đã merge xong

---

### **Bước 9: Cập nhật local và xóa branch cũ**

```bash
# Chuyển sang main
git checkout main

# Lấy code mới nhất (bao gồm commit vừa merge)
git pull origin main

# Xóa branch local đã merge
git branch -d feature/tên-tính-năng

# Xóa branch trên GitHub (nếu chưa tự xóa)
git push origin --delete feature/tên-tính-năng
```

---

## 📝 Các lệnh Git thường dùng

### Kiểm tra trạng thái

```bash
git status                    # Xem file nào đã thay đổi
git log                       # Xem lịch sử commit
git log --oneline             # Xem commit dạng ngắn gọn
git diff                      # Xem thay đổi chưa commit
git diff HEAD~1               # Xem thay đổi của commit gần nhất
```

### Quản lý branch

```bash
git branch                    # Liệt kê branch local
git branch -a                 # Liệt kê tất cả branch
git checkout tên-branch       # Chuyển sang branch khác
git checkout -b tên-branch    # Tạo mới và chuyển sang branch
git branch -d tên-branch      # Xóa branch local
git merge tên-branch          # Merge branch vào branch hiện tại
```

### Làm việc với remote

```bash
git fetch                     # Lấy thông tin branch từ remote
git pull                      # Fetch + merge
git push                      # Push commit lên remote
git push -u origin branch-name # Push branch mới lần đầu
git remote -v                 # Xem remote URL
```

### Undo/Reset

```bash
# Undo file chưa commit
git checkout -- tên-file      # Khôi phục file về trạng thái cuối cùng

# Undo commit (giữ lại thay đổi)
git reset --soft HEAD~1       # Undo commit nhưng giữ lại trong staging

# Undo commit (xóa thay đổi)
git reset --hard HEAD~1       # ⚠️ Cẩn thận: Xóa hết thay đổi

# Sửa commit message gần nhất
git commit --amend -m "message mới"
```

---

## 🔀 Pull Request Process

### Checklist trước khi tạo PR

- [ ] Code đã chạy được local
- [ ] Không có conflict với main branch
- [ ] Đã test các tính năng mới
- [ ] Commit message rõ ràng, đúng format
- [ ] Không có file nhạy cảm trong commit
- [ ] Đã thêm description cho PR

### Template PR

```markdown
## 📝 Mô tả
[Tóm tắt ngắn gọn về những gì PR này làm]

## 🔄 Thay đổi
- [ ] Tính năng mới
- [ ] Sửa lỗi
- [ ] Cập nhật tài liệu
- [ ] Refactor code

## 🧪 Cách test
[Liệt kê các bước để test các thay đổi]

## 📸 Screenshots (nếu có)
[Thêm screenshot nếu có thay đổi UI]

## ✅ Checklist
- [ ] Code đã được test
- [ ] Đã test trên local
- [ ] Không có conflict
- [ ] Đã review code của mình
```

### Merge options

1. **Create a merge commit** (khuyến nghị)
   - Giữ lại toàn bộ lịch sử commit
   - Dùng khi có nhiều commit

2. **Squash and merge**
   - Gộp tất cả commit thành 1 commit
   - Dùng khi có nhiều commit nhỏ, không cần giữ lại

3. **Rebase and merge**
   - Tạo lịch sử tuyến tính
   - Dùng khi muốn lịch sử commit sạch sẽ

---

## 📋 Quy tắc commit message

### Format chuẩn

```
<type>: <subject>

<body> (tùy chọn)

<footer> (tùy chọn)
```

### Types

| Type | Khi nào dùng | Ví dụ |
|------|--------------|-------|
| `feat` | Thêm tính năng mới | `feat: thêm chức năng đăng nhập` |
| `fix` | Sửa lỗi bug | `fix: sửa lỗi không login được` |
| `docs` | Thay đổi tài liệu | `docs: cập nhật hướng dẫn cài đặt` |
| `style` | Format code, không ảnh hưởng logic | `style: sửa format code theo ESLint` |
| `refactor` | Refactor code | `refactor: tối ưu hàm xử lý đơn hàng` |
| `test` | Thêm test | `test: thêm unit test cho AuthService` |
| `chore` | Cập nhật config, dependency | `chore: cập nhật package.json` |
| `perf` | Cải thiện performance | `perf: tối ưu query database` |

### Ví dụ tốt

```bash
# Commit message tốt
feat: thêm chức năng giỏ hàng

- Thêm button "Thêm vào giỏ"
- Thêm trang giỏ hàng
- Thêm API lưu giỏ hàng vào database

fix: sửa lỗi hiển thị giá sản phẩm

# Commit message tốt (ngắn)
docs: cập nhật README.md

# Commit message KHÔNG TỐT ❌
sửa code
fix bug
update
commit
```

---

## ⚠️ Xử lý lỗi thường gặp

### 1. Lỗi: "Your branch is behind"

```bash
# Nguyên nhân: Remote có commit mới mà local chưa có

# Cách sửa:
git pull origin main
```

### 2. Lỗi: Conflict khi pull/merge

```bash
# Nguyên nhân: Cùng 1 dòng code bị sửa ở 2 nơi khác nhau

# Cách sửa:
# 1. Mở file bị conflict
# 2. Tìm các dòng có <<<<<<< HEAD, =======, >>>>>>>
# 3. Xóa các dòng marker và chọn code đúng
# 4. Lưu file
# 5. Commit
git add .
git commit -m "fix: resolve conflict"
```

### 3. Lỗi: "Changes not staged for commit"

```bash
# Nguyên nhân: File đã sửa nhưng chưa được add

# Cách sửa:
git add .
git commit -m "message"

# Hoặc nếu muốn bỏ thay đổi:
git checkout -- tên-file
```

### 4. Lỗi: "Untracked files"

```bash
# Nguyên nhân: File mới tạo chưa được add

# Cách sửa:
git add tên-file
# hoặc
git add .
```

### 5. Push sai branch

```bash
# Nguyên nhân: Push nhầm vào branch khác

# Cách sửa:
# 1. Undo commit local
git reset --soft HEAD~1

# 2. Chuyển sang branch đúng
git checkout branch-đúng

# 3. Commit lại
git add .
git commit -m "message"

# 4. Push
git push
```

### 6. Muốn sửa commit message cuối cùng

```bash
git commit --amend -m "message mới"
git push --force-with-lease  # Cẩn thận: chỉ dùng trên branch của mình!
```

---

## 🎓 Best Practices

### ✅ Nên làm

- ✅ Tạo branch riêng cho mỗi feature/bugfix
- ✅ Commit thường xuyên với message rõ ràng
- ✅ Pull code mới nhất trước khi bắt đầu làm việc
- ✅ Test code trước khi commit
- ✅ Review code của chính mình trước khi tạo PR
- ✅ Giữ branch main luôn sạch và có thể deploy

### ❌ Không nên làm

- ❌ Commit trực tiếp vào main branch
- ❌ Commit file `node_modules`, `.env`, `.idea`
- ❌ Commit với message không rõ ràng
- ❌ Push code lên main bằng mọi giá
- ❌ Commit quá nhiều file trong 1 lần
- ❌ Bỏ qua review process

---

## 📚 Tài liệu tham khảo

- [Git Official Documentation](https://git-scm.com/doc)
- [GitHub Flow](https://guides.github.com/introduction/flow/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Atlassian Git Tutorials](https://www.atlassian.com/git/tutorials)

---

## 💬 Cần hỗ trợ?

Nếu gặp vấn đề khi làm việc với Git:

1. Đọc lại document này
2. Kiểm tra phần "Xử lý lỗi thường gặp"
3. Google với từ khóa: `git [lỗi bạn gặp]`
4. Hỏi team qua Slack/Discord
5. Liên hệ: [thông tin liên hệ của team]

---

<div align="center">

**✨ Happy Coding! ✨**

Made with ❤️ by Sneakery Team

</div>

