<template>
  <div class="wishlist-page">
    <el-card class="wishlist-container">
      <template #header>
        <div class="card-header">
          <el-icon><Star /></el-icon>
          <span>Danh sách yêu thích</span>
        </div>
      </template>

      <div v-if="wishlistItems.length === 0" class="empty-wishlist">
        <el-empty description="Danh sách yêu thích của bạn đang trống">
          <template #image>
            <svg width="120" height="120" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5C22 12.27 18.6 15.36 13.45 20.03L12 21.35Z" fill="#e0e0e0"/>
            </svg>
          </template>
        </el-empty>
        <el-button type="primary" @click="goToHome">Bắt đầu mua sắm</el-button>
      </div>

      <div v-else>
        <!-- Wishlist Grid -->
        <div class="wishlist-grid">
          <div 
            v-for="item in wishlistItems" 
            :key="item.id" 
            class="wishlist-item"
          >
            <div class="item-image">
              <img :src="item.imageUrl" :alt="item.name" />
              <div class="item-actions">
                <el-button 
                  type="danger" 
                  :icon="Delete" 
                  circle 
                  size="small"
                  @click="removeFromWishlist(item)"
                />
                <el-button 
                  type="primary" 
                  :icon="ShoppingCart" 
                  circle 
                  size="small"
                  @click="addToCart(item)"
                />
              </div>
            </div>
            
            <div class="item-details">
              <h3 class="item-name">{{ item.name }}</h3>
              <p class="item-brand">{{ item.brandName }}</p>
              <div class="item-price">
                <span class="current-price">{{ formatCurrency(item.price) }}</span>
                <span v-if="item.originalPrice && item.originalPrice > item.price" class="original-price">
                  {{ formatCurrency(item.originalPrice) }}
                </span>
              </div>
              
              <div class="item-variants" v-if="item.variants && item.variants.length > 0">
                <el-select v-model="item.selectedVariant" placeholder="Chọn size" size="small">
                  <el-option
                    v-for="variant in item.variants"
                    :key="variant.id"
                    :label="`Size ${variant.size} - ${variant.color}`"
                    :value="variant.id"
                  />
                </el-select>
              </div>
              
              <div class="item-stock" v-if="item.stock !== undefined">
                <el-tag 
                  :type="item.stock > 0 ? 'success' : 'danger'"
                  size="small"
                >
                  {{ item.stock > 0 ? `Còn ${item.stock} sản phẩm` : 'Hết hàng' }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="wishlist-actions">
          <el-button @click="clearWishlist" :disabled="wishlistItems.length === 0">
            <el-icon><Delete /></el-icon>
            Xóa tất cả
          </el-button>
          <el-button type="primary" @click="addAllToCart" :disabled="wishlistItems.length === 0">
            <el-icon><ShoppingCart /></el-icon>
            Thêm tất cả vào giỏ hàng
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Star, Delete, ShoppingCart } from '@element-plus/icons-vue';

const router = useRouter();

// Mock data for wishlist items
const wishlistItems = ref([
  {
    id: 1,
    name: 'Nike Air Force 1 Low',
    brandName: 'Nike',
    imageUrl: 'https://via.placeholder.com/300x300',
    price: 2500000,
    originalPrice: 3000000,
    stock: 5,
    selectedVariant: 1,
    variants: [
      { id: 1, size: 40, color: 'Trắng' },
      { id: 2, size: 41, color: 'Trắng' },
      { id: 3, size: 42, color: 'Trắng' }
    ]
  },
  {
    id: 2,
    name: 'Adidas Ultraboost 21',
    brandName: 'Adidas',
    imageUrl: 'https://via.placeholder.com/300x300',
    price: 3500000,
    stock: 3,
    selectedVariant: 4,
    variants: [
      { id: 4, size: 39, color: 'Đen' },
      { id: 5, size: 40, color: 'Đen' },
      { id: 6, size: 41, color: 'Đen' }
    ]
  },
  {
    id: 3,
    name: 'Jordan 1 Retro High',
    brandName: 'Jordan',
    imageUrl: 'https://via.placeholder.com/300x300',
    price: 4500000,
    stock: 0,
    selectedVariant: 7,
    variants: [
      { id: 7, size: 40, color: 'Đỏ' },
      { id: 8, size: 41, color: 'Đỏ' }
    ]
  }
]);

const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

const removeFromWishlist = (item) => {
  ElMessageBox.confirm(
    `Bạn có chắc chắn muốn xóa "${item.name}" khỏi danh sách yêu thích?`,
    'Xác nhận',
    {
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy',
      type: 'warning',
    }
  ).then(() => {
    const index = wishlistItems.value.findIndex(i => i.id === item.id);
    if (index > -1) {
      wishlistItems.value.splice(index, 1);
      ElMessage.success(`Đã xóa "${item.name}" khỏi danh sách yêu thích`);
    }
  }).catch(() => {
    ElMessage.info('Đã hủy thao tác');
  });
};

const addToCart = (item) => {
  if (item.stock === 0) {
    ElMessage.warning('Sản phẩm này đã hết hàng');
    return;
  }
  
  ElMessage.success(`Đã thêm "${item.name}" vào giỏ hàng`);
  // Here you would typically dispatch an action to add the item to your cart store/backend
};

const addAllToCart = () => {
  const availableItems = wishlistItems.value.filter(item => item.stock > 0);
  
  if (availableItems.length === 0) {
    ElMessage.warning('Tất cả sản phẩm trong danh sách yêu thích đều hết hàng');
    return;
  }
  
  ElMessage.success(`Đã thêm ${availableItems.length} sản phẩm vào giỏ hàng`);
  // Here you would typically dispatch an action to add all available items to your cart store/backend
};

const clearWishlist = () => {
  ElMessageBox.confirm(
    'Bạn có chắc chắn muốn xóa tất cả sản phẩm khỏi danh sách yêu thích?',
    'Xác nhận',
    {
      confirmButtonText: 'Xóa tất cả',
      cancelButtonText: 'Hủy',
      type: 'warning',
    }
  ).then(() => {
    wishlistItems.value = [];
    ElMessage.success('Đã xóa tất cả sản phẩm khỏi danh sách yêu thích');
  }).catch(() => {
    ElMessage.info('Đã hủy thao tác');
  });
};

const goToHome = () => {
  router.push('/');
};
</script>

<style scoped>
.wishlist-page {
  padding: 20px;
  padding-top: 90px; /* Space for fixed navbar */
  max-width: 1200px;
  margin: 20px auto;
}

.wishlist-container {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.empty-wishlist {
  text-align: center;
  padding: 50px 0;
}

.empty-wishlist .el-empty {
  margin-bottom: 30px;
}

.wishlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.wishlist-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  background: white;
}

.wishlist-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.item-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.wishlist-item:hover .item-image img {
  transform: scale(1.05);
}

.item-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.wishlist-item:hover .item-actions {
  opacity: 1;
}

.item-details {
  padding: 15px;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.item-brand {
  font-size: 14px;
  color: #909399;
  margin: 0 0 12px 0;
}

.item-price {
  margin-bottom: 12px;
}

.current-price {
  font-size: 18px;
  font-weight: 700;
  color: #e74c3c;
}

.original-price {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
  margin-left: 8px;
}

.item-variants {
  margin-bottom: 12px;
}

.item-variants .el-select {
  width: 100%;
}

.item-stock {
  margin-top: 8px;
}

.wishlist-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
  border-top: 1px solid #ebeef5;
}

/* Responsive Design */
@media (max-width: 768px) {
  .wishlist-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 15px;
  }
  
  .wishlist-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .wishlist-actions .el-button {
    width: 200px;
  }
}

@media (max-width: 480px) {
  .wishlist-page {
    padding: 10px;
  }
  
  .wishlist-grid {
    grid-template-columns: 1fr;
  }
  
  .item-image {
    height: 180px;
  }
}
</style>
