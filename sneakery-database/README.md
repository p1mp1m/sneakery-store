# 💾 Sneakery Database

## 🚀 Quick Start

### Tạo database trong **2 bước đơn giản**:

1. **Tạo Schema** - Execute `1_CREATE_SCHEMA.sql`
2. **Insert Dữ liệu** - Execute `2_INSERT_DATA.sql`

✅ **Done!** Database sẵn sàng với 5 users + 21 products + dữ liệu demo đầy đủ

---

## 📁 Files

| File | Mô tả | Thời gian |
|------|-------|-----------|
| `1_CREATE_SCHEMA.sql` | Tạo 25 tables + indexes + views + procedures | ~5-10s |
| `2_INSERT_DATA.sql` | Insert users + products + orders + reviews | ~10-15s |
| `README_SETUP.md` | Hướng dẫn chi tiết đầy đủ | - |

---

## 🔑 Login Info

### Test Accounts
| Role | Email | Password |
|------|-------|----------|
| **Admin** | `testadmin@sneakery.com` | `admin123` |
| **User** | `testuser@sneakery.com` | `user123` |

### Customer Accounts (Demo)
| Name | Email | Password |
|------|-------|----------|
| Nguyễn Văn A | `customer1@example.com` | `admin123` |
| Trần Thị B | `customer2@example.com` | `admin123` |
| Lê Văn C | `customer3@example.com` | `admin123` |

---

## 📊 Dữ liệu sau khi setup

### 👤 Users & Accounts
- ✅ 5 users total (2 test accounts + 3 customers)
- ✅ Mỗi user có địa chỉ mặc định

### 🏷️ Products & Catalog
- ✅ 6 brands (Nike, Adidas, Puma, New Balance, Converse, Vans)
- ✅ 10 categories (nested set hierarchy)
- ✅ 21 products (đa dạng brands)
- ✅ 60+ product variants (sizes, colors, prices)
- ✅ Product images

### 💰 Promotions
- ✅ 8 flash sales (active & upcoming)
- ✅ 12 coupons (general & brand/category specific)

### 🛒 Orders & Engagement
- ✅ 6 orders với trạng thái khác nhau (delivered, processing, confirmed, pending)
- ✅ 23 reviews với ratings (4-5 stars)
- ✅ Loyalty points transactions
- ✅ 19 notifications (read + unread)
- ✅ 16 wishlist items
- ✅ Product statistics (views, orders, ratings)

---

## 📖 Chi tiết

Xem file `README_SETUP.md` để có hướng dẫn đầy đủ về:
- Database schema (25 tables)
- Verify queries
- Troubleshooting
- Next steps

---

**🎉 Database ready for demo!**
