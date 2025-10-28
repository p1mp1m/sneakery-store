# 🚀 GitHub Commands & Tips & Tricks

> **📌 Tài liệu tổng hợp các lệnh Git/GitHub và mẹo hữu ích cho team Sneakery Store**

---

## 📋 Mục lục

1. [Lệnh Git Cơ bản](#lệnh-git-cơ-bản)
2. [Lệnh GitHub Nâng cao](#lệnh-github-nâng-cao)
3. [Tips & Tricks Hay Ho](#tips--tricks-hay-ho)
4. [Shortcuts & Alias](#shortcuts--alias)
5. [Troubleshooting](#troubleshooting)

---

## 💻 Lệnh Git Cơ bản

### Kiểm tra và Cập nhật

```bash
# Kiểm tra branch hiện tại
git branch

# Xem tất cả branch (local + remote)
git branch -a

# Xem trạng thái file đã thay đổi
git status

# Xem chi tiết thay đổi
git diff

# Xem lịch sử commit
git log
git log --oneline          # Dạng ngắn gọn
git log --graph            # Có đồ thị
git log --all --oneline    # Tất cả branch

# Cập nhật code mới nhất
git fetch origin           # Lấy thông tin từ remote
git pull origin main       # Fetch + merge vào main
git pull --rebase origin main  # Pull với rebase (sạch hơn)

# Dọn dẹp repository
git gc --prune=now         # Dọn dẹp objects không cần
git gc --aggressive        # Dọn dẹp triệt để
```

### Branch Management

```bash
# Tạo và chuyển sang branch mới
git checkout -b feature/login-page
git checkout -b fix/bug-payment

# Chuyển sang branch
git checkout main
git checkout feature/login-page

# Xem branch nào đang tracking branch nào
git branch -vv

# Xóa branch local
git branch -d feature/old-feature      # Safe delete (chỉ xóa nếu đã merge)
git branch -D feature/old-feature      # Force delete (xóa luôn)

# Xóa branch remote
git push origin --delete feature/old-feature

# Rename branch
git branch -m old-name new-name

# Mới trong Git 2.23+
git switch main           # Chuyển branch
git switch -c new-branch  # Tạo mới branch
```

### Commit và Push

```bash
# Add files
git add filename.js       # Thêm 1 file
git add src/             # Thêm thư mục
git add .                 # Thêm tất cả file thay đổi
git add -A                # Thêm tất cả (kể cả deleted files)
git add -p                # Interactive add (chọn từng phần)

# Commit
git commit -m "feat: thêm tính năng X"
git commit -am "fix: sửa bug"  # Add và commit trong 1 lệnh (chỉ cho file đã track)

# Push
git push                              # Push branch hiện tại
git push origin feature/login-page    # Push branch cụ thể
git push -u origin feature/new        # Push và set upstream (lần đầu)
git push --force-with-lease          # Force push (an toàn hơn force)

# Amend commit
git commit --amend -m "message mới"  # Sửa commit cuối cùng
git commit --amend --no-edit          # Chỉ thêm file vào commit cuối
```

### Undo và Reset

```bash
# Undo file chưa commit (khôi phục về trạng thái cuối)
git checkout -- filename.js
git restore filename.js               # Git 2.23+

# Unstage file (bỏ khỏi staging area)
git reset HEAD filename.js
git restore --staged filename.js      # Git 2.23+

# Undo commit nhưng giữ lại code (soft reset)
git reset --soft HEAD~1

# Undo commit và bỏ code ra khỏi staging (mixed reset)
git reset HEAD~1
git reset --mixed HEAD~1

# Undo commit và xóa code (hard reset - CẨN THẬN!)
git reset --hard HEAD~1

# Undo nhiều commit
git reset --soft HEAD~3  # Undo 3 commits gần nhất

# Xóa uncommitted changes
git clean -fd             # Xóa untracked files và directories
git clean -n              # Preview (xem sẽ xóa gì)

# Revert commit đã push (tạo commit mới để hoàn tác)
git revert COMMIT_HASH
git revert HEAD           # Revert commit cuối cùng
```

### Merge và Rebase

```bash
# Merge branch vào branch hiện tại
git merge feature/login-page
git merge --no-ff feature/login-page  # Không tạo fast-forward

# Rebase (làm cho history thẳng hơn)
git rebase main                        # Rebase branch hiện tại lên main
git rebase --interactive HEAD~5        # Rebase 5 commits cuối (có thể edit)
git rebase --continue                  # Tiếp tục sau khi resolve conflict
git rebase --abort                     # Hủy rebase

# Pull với rebase (thay vì merge)
git pull --rebase origin main
```

### Stash (Tạm lưu thay đổi)

```bash
# Lưu thay đổi tạm thời
git stash
git stash save "message mô tả"

# Xem danh sách stash
git stash list

# Khôi phục stash mới nhất
git stash pop              # Apply và xóa
git stash apply            # Apply nhưng giữ stash

# Khôi phục stash cụ thể
git stash pop stash@{0}
git stash apply stash@{1}

# Xem stash
git stash show
git stash show -p          # Xem chi tiết

# Xóa stash
git stash drop              # Xóa mới nhất
git stash clear             # Xóa tất cả

# Lưu cả untracked files
git stash -u
git stash --include-untracked
```

### Conflict Resolution

```bash
# Khi có conflict, Git sẽ báo
git status                 # Xem file nào bị conflict

# Trong file conflict, tìm:
<<<<<<< HEAD
// Code của bạn
=======
// Code từ branch khác
>>>>>>> branch-name

# Sau khi sửa xong:
git add .
git commit -m "fix: resolve conflict"

# Hoặc nếu merge:
git merge --continue

# Cancel merge
git merge --abort
```

---

## 🔥 Lệnh GitHub Nâng cao

### View và Search

```bash
# Xem commit cụ thể
git show COMMIT_HASH
git show HEAD             # Commit cuối cùng

# Xem file tại commit cụ thể
git show COMMIT_HASH:path/to/file.js

# Tìm commit chứa thay đổi
git log -S "keyword"      # Tìm commit thay đổi chứa keyword
git log --grep="keyword"   # Tìm commit message chứa keyword
git log --author="name"    # Tìm commit của author
git log --since="2024-01-01"

# Tìm file đã bị xóa
git log --all --full-history -- file.js
git log --diff-filter=D --summary

# Xem reflog (lịch sử tất cả thao tác)
git reflog                 # Xem tất cả
git reflog HEAD~5          # 5 thao tác gần nhất
```

### Remote Management

```bash
# Xem remote
git remote -v
git remote show origin

# Thêm remote
git remote add upstream https://github.com/owner/repo

# Cập nhật remote URL
git remote set-url origin NEW_URL

# Fetch từ remote cụ thể
git fetch upstream

# Pull từ remote khác
git pull upstream main

# Push lên remote khác
git push upstream main
```

### Tags

```bash
# Tạo tag
git tag v1.0.0
git tag -a v1.0.0 -m "Release version 1.0.0"

# Xem tags
git tag
git tag -l "v1.0*"         # Filter tags

# Push tags
git push origin v1.0.0
git push origin --tags     # Push tất cả tags

# Xóa tag
git tag -d v1.0.0          # Local
git push origin --delete v1.0.0  # Remote
```

### Cherry-pick

```bash
# Copy commit từ branch khác
git cherry-pick COMMIT_HASH
git cherry-pick COMMIT_HASH1 COMMIT_HASH2  # Nhiều commits

# Cherry-pick với conflict
git cherry-pick --abort   # Hủy
git cherry-pick --continue # Tiếp tục sau khi resolve
```

### Blame và Log

```bash
# Xem ai đã sửa từng dòng
git blame filename.js
git blame -L 10,20 filename.js  # Chỉ dòng 10-20

# Xem thay đổi theo thời gian
git log --all --oneline --graph --decorate
```

---

## 💡 Tips & Tricks Hay Ho

### 1. Git Aliases (Shortcuts)

Thêm vào `~/.gitconfig` hoặc `.gitconfig`:

```bash
# Thiết lập alias
git config --global alias.st status
git config --global alias.br branch
git config --global alias.co checkout
git config --global alias.ci commit
git config --global alias.unstage 'restore --staged'
git config --global alias.last 'log -1 HEAD'
git config --global alias.visual '!gitk'

# Shortcuts hay dùng
git config --global alias.lg "log --oneline --decorate --all --graph"
git config --global alias.stats "log --stat"
git config --global alias.who "blame"

# Sau khi setup, dùng:
git st         # = git status
git br         # = git branch
git co main    # = git checkout main
git lg         # = đẹp như in
```

### 2. Ignore Large Files

```bash
# Tìm file lớn trong git history
git rev-list --objects --all | git cat-file --batch-check='%(objecttype) %(objectname) %(objectsize) %(rest)' | sed -n 's/^blob //p' | sort -rn -k2 | head -20

# Xóa file lớn khỏi history (dùng git filter-repo)
# ⚠️ Cẩn thận, backup trước!
```

### 3. Làm Việc với Fork

```bash
# Thêm remote upstream
git remote add upstream https://github.com/ORIGINAL_OWNER/REPO.git

# Fetch từ upstream
git fetch upstream

# Merge upstream/main vào fork/main
git checkout main
git merge upstream/main

# Push lên fork
git push origin main
```

### 4. Undo Almost Anything

```bash
# Undo local uncommitted changes
git checkout -- .

# Undo last commit, keep changes
git reset HEAD~1

# Undo last commit, lose changes
git reset --hard HEAD~1

# Undo merge chưa push
git reset --hard origin/main  # ⚠️ CẨN THẬN!

# Undo push (phải force push sau)
git reset --hard HEAD~1
git push --force-with-lease  # Force push safely
```

### 5. Working with Multiple Files

```bash
# Xem file nào đã thay đổi
git status

# Xem diff của file cụ thể
git diff path/to/file.js

# Xem diff của nhiều commits
git diff HEAD~5 HEAD

# Xem stats thay đổi
git diff --stat
git diff HEAD~5 --stat
```

### 6. Performance Tips

```bash
# Tăng hiệu năng với sparse-checkout (chỉ clone phần cần)
git clone --filter=blob:none --sparse URL
cd repo
git sparse-checkout init --cone
git sparse-checkout set subfolder

# Shallow clone (không lấy hết history)
git clone --depth 1 URL

# Partial clone
git clone --filter=blob:none URL
git clone --filter=tree:0 URL
```

### 7. Diff Tricks

```bash
# Xem diff colorized
git diff --color

# Xem diff giữa 2 commit
git diff COMMIT1 COMMIT2

# Xem diff của 1 file cụ thể giữa 2 commit
git diff COMMIT1 COMMIT2 -- path/to/file.js

# Xem thống kê diff
git diff --stat

# So sánh với branch khác
git diff main..feature/new-feature
```

### 8. Bisect (Tìm Bug)

```bash
# Bắt đầu bisect
git bisect start

# Đánh dấu commit buggy (có bug)
git bisect bad

# Đánh dấu commit good (không bug)
git bisect good COMMIT_HASH_GOOD

# Git sẽ checkout về commit giữa, bạn test và đánh dấu
git bisect bad  # Nếu buggy
git bisect good # Nếu OK

# Tiếp tục cho đến khi tìm ra commit gây bug

# Kết thúc
git bisect reset
```

### 9. Interactive Rebase

```bash
# Interactive rebase 5 commits gần nhất
git rebase -i HEAD~5

# Các option trong editor:
# pick   = giữ nguyên commit
# reword = giữ nguyên code, sửa message
# edit   = dừng lại để sửa code
# squash = gộp với commit trước
# drop   = xóa commit
```

### 10. File History

```bash
# Xem lịch sử thay đổi của file
git log -p path/to/file.js
git log --follow -p path/to/file.js  # Theo dõi cả khi file bị rename

# Xem file tại thời điểm cụ thể
git show COMMIT_HASH:path/to/file.js

# Copy file từ commit cụ thể
git show COMMIT_HASH:path/to/file.js > newfile.js
```

### 11. Amend Tricks

```bash
# Thêm file vào commit cuối cùng (chưa push)
git add forgotten-file.js
git commit --amend --no-edit

# Sửa commit message
git commit --amend -m "message mới"

# Thêm file và giữ nguyên message
git add file.js
git commit --amend --no-edit
```

### 12. Pull Request Best Practices

```bash
# Update PR với code mới nhất từ main
git checkout feature/your-branch
git fetch origin
git rebase origin/main
# Fix conflicts nếu có
git push --force-with-lease
```

### 13. Working Directory Cleanup

```bash
# Dọn dẹp hoàn toàn
git clean -fdx              # Force, directories, ignored files
git reset --hard            # Reset về commit cuối
git gc --prune=now          # Dọn repository

# Preview trước khi dọn
git clean -n               # Preview files sẽ bị xóa
git clean -dn              # Preview cả directories
```

### 14. Find và Search

```bash
# Tìm text trong toàn bộ project
git grep "search text"

# Tìm trong file cụ thể
git grep "text" -- path/

# Tìm với context
git grep -C 3 "text"       # 3 dòng context
git grep -n "text"         # Show line numbers

# Tìm case insensitive
git grep -i "TEXT"

# Tìm trong specific file types
git grep "text" -- "*.js"
```

### 15. Submodule

```bash
# Clone với submodules
git clone --recursive URL

# Update submodules
git submodule update --remote

# Add submodule
git submodule add URL path/to/submodule
```

---

## 🎯 Shortcuts & Alias

### Tạo file alias riêng

Tạo file `.bashrc` hoặc `.zshrc`:

```bash
# Thêm vào file config
alias gs='git status'
alias ga='git add'
alias gaa='git add -A'
alias gc='git commit'
alias gp='git push'
alias gpl='git pull'
alias gco='git checkout'
alias gcb='git checkout -b'
alias gb='git branch'
alias gd='git diff'
alias gl='git log --oneline --graph --all'
alias gst='git stash'
alias gstp='git stash pop'
alias gsta='git stash apply'
```

Sau đó reload:
```bash
source ~/.bashrc  # hoặc source ~/.zshrc
```

---

## 🆘 Troubleshooting

### Lỗi thường gặp và cách fix

#### 1. "Your branch is behind 'origin/main'"

```bash
git fetch origin
git pull origin main
# hoặc
git pull --rebase origin main
```

#### 2. "Failed to push"

```bash
# Update local trước
git pull origin main

# Resolve conflicts nếu có
# Sau đó push lại
git push
```

#### 3. "Updates were rejected"

```bash
# Có người đã push trước bạn
git pull --rebase origin main
git push
```

#### 4. Muốn undo commit đã push (public)

```bash
# Không dùng reset --hard!
# Dùng revert để tạo commit mới
git revert COMMIT_HASH
git push
```

#### 5. Đẩy nhầm file nhạy cảm

```bash
# Xóa file khỏi history (dùng BFG Repo-Cleaner hoặc git filter-branch)
# Hoặc đổi secret key ngay lập tức!

# Sử dụng git filter-repo (khuyên dùng)
pip install git-filter-repo
git filter-repo --path file-with-secret.txt --invert-paths
git push --force
```

#### 6. Repository quá lớn

```bash
# Clone chỉ branch main
git clone --single-branch --branch main URL

# Clone với depth 1
git clone --depth 1 URL

# Clean up
git gc --aggressive --prune=now
```

#### 7. Quên commit message

```bash
git commit --amend -m "message mới"

# Nếu đã push
git push --force-with-lease
```

---

## 🚀 Advanced Workflows

### Feature Branch Workflow hoàn chỉnh

```bash
# 1. Update main
git checkout main
git pull origin main

# 2. Tạo feature branch
git checkout -b feature/new-feature

# 3. Work và commit
git add .
git commit -m "feat: ..."
git push -u origin feature/new-feature

# 4. Tiếp tục work
git add .
git commit -m "fix: ..."
git push

# 5. Update với main mới nhất
git fetch origin
git rebase origin/main  # hoặc merge
git push --force-with-lease

# 6. Sau khi PR merged
git checkout main
git pull origin main
git branch -d feature/new-feature
git push origin --delete feature/new-feature
```

### Hotfix Workflow

```bash
# Tạo hotfix từ main
git checkout main
git pull origin main
git checkout -b hotfix/critical-bug-fix

# Fix và commit
# ...

# Merge ngay vào main
git checkout main
git merge hotfix/critical-bug-fix
git tag v1.0.1
git push origin main --tags

# Merge vào develop (nếu có)
git checkout develop
git merge hotfix/critical-bug-fix
git push origin develop

# Cleanup
git branch -d hotfix/critical-bug-fix
```

---

## 📊 Statistics & Analysis

```bash
# Xem contributor stats
git shortlog -sn

# Xem thống kê thay đổi theo author
git log --author="Name" --pretty=tformat: --numstat

# Xem file nào thay đổi nhiều nhất
git log --stat --summary

# Xem commits theo thời gian
git log --date=short --pretty=format:"%h %ad %s" --all

# Xem chart contribution
git log --author="Name" --format="%ai" | cut -d' ' -f1 | sort | uniq -c
```

---

## 🎓 Pro Tips

1. **Luôn pull trước khi bắt đầu làm việc**
```bash
git checkout main && git pull origin main
```

2. **Commit thường xuyên, push định kỳ**
```bash
git add .
git commit -m "feat: ..."
git push  # Push sau mỗi vài commits
```

3. **Dùng `--force-with-lease` thay vì `--force`**
```bash
git push --force-with-lease  # An toàn hơn
```

4. **Review code của chính mình trước khi PR**
```bash
git diff origin/main
```

5. **Giữ commit message rõ ràng và theo convention**
```bash
git commit -m "feat: thêm tính năng X
- Chi tiết 1
- Chi tiết 2"
```

---

<div align="center">

**✨ Happy Git-ing! ✨**

Made with ❤️ by Sneakery Team

</div>

