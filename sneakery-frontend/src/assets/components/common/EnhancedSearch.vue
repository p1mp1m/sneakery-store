<template>
  <div class="enhanced-search">
    <div class="search-wrapper">
      <div class="search-input-container">
        <svg class="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M21 21L16.65 16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <input
          type="text"
          v-model="searchQuery"
          @input="handleSearch"
          @focus="showSuggestions = true"
          @blur="handleBlur"
          placeholder="Tìm kiếm sản phẩm, thương hiệu..."
          class="search-input"
        />
        <button v-if="searchQuery" @click="clearSearch" class="clear-btn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <line x1="18" y1="6" x2="6" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            <line x1="6" y1="6" x2="18" y2="18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>

      <!-- Suggestions Dropdown -->
      <Transition name="dropdown">
        <div v-if="showSuggestions && (suggestions.length > 0 || searchQuery)" class="suggestions-dropdown">
          <!-- Quick Filters -->
          <div v-if="!searchQuery" class="quick-filters">
            <h3 class="filter-title">Tìm kiếm phổ biến:</h3>
            <div class="filter-tags">
              <button
                v-for="tag in popularSearches"
                :key="tag"
                @click="searchTag(tag)"
                class="filter-tag"
              >
                {{ tag }}
              </button>
            </div>
          </div>

          <!-- Search Suggestions -->
          <div v-if="searchQuery" class="search-suggestions">
            <div v-if="suggestions.length > 0" class="suggestion-group">
              <h3 class="suggestion-title">Sản phẩm</h3>
              <div
                v-for="item in suggestions"
                :key="item.id"
                @click="selectSuggestion(item)"
                class="suggestion-item"
              >
                <img :src="item.imageUrl || '/placeholder-image.png'" :alt="item.name" class="suggestion-image" />
                <div class="suggestion-info">
                  <span class="suggestion-brand">{{ item.brandName }}</span>
                  <span class="suggestion-name">{{ item.name }}</span>
                  <span class="suggestion-price">{{ formatCurrency(item.price) }}</span>
                </div>
              </div>
            </div>

            <!-- No Results -->
            <div v-else class="no-results">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M21 21L15 15M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <p>Không tìm thấy sản phẩm</p>
            </div>

            <!-- View All Results -->
            <router-link v-if="searchQuery" :to="`/products?search=${searchQuery}`" class="view-all-link">
              Xem tất cả kết quả cho "{{ searchQuery }}"
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </router-link>
          </div>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { debounce } from '@/utils/debounce';
import axios from 'axios';

const router = useRouter();

const searchQuery = ref('');
const suggestions = ref([]);
const showSuggestions = ref(false);
const isLoading = ref(false);

const popularSearches = ['Nike Air Max', 'Adidas Ultraboost', 'Jordan 1', 'Running Shoes', 'Basketball'];

const handleSearch = debounce(async () => {
  if (searchQuery.value.length < 2) {
    suggestions.value = [];
    return;
  }

  isLoading.value = true;
  try {
    const response = await axios.get('http://localhost:8080/api/products', {
      params: {
        page: 0,
        size: 5,
        search: searchQuery.value
      }
    });
    
    suggestions.value = response.data.content || [];
  } catch (error) {
    console.error('Error fetching suggestions:', error);
    suggestions.value = [];
  } finally {
    isLoading.value = false;
  }
}, 300);

const handleBlur = () => {
  // Delay để cho phép click vào suggestion
  setTimeout(() => {
    showSuggestions.value = false;
  }, 200);
};

const clearSearch = () => {
  searchQuery.value = '';
  suggestions.value = [];
  showSuggestions.value = false;
};

const selectSuggestion = (product) => {
  router.push(`/products/${product.id}`);
  showSuggestions.value = false;
  searchQuery.value = '';
};

const searchTag = (tag) => {
  searchQuery.value = tag;
  handleSearch();
};

const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
</script>

<style scoped>
.enhanced-search {
  position: relative;
  max-width: 600px;
  width: 100%;
}

.search-wrapper {
  position: relative;
}

.search-input-container {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-full);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.search-input-container:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(167, 139, 250, 0.1);
}

.search-icon {
  position: absolute;
  left: var(--space-4);
  color: var(--text-muted);
  pointer-events: none;
}

.search-input {
  flex: 1;
  padding: var(--space-3) var(--space-4) var(--space-3) var(--space-12);
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-size: var(--text-base);
  outline: none;
}

.search-input::placeholder {
  color: var(--text-muted);
}

.clear-btn {
  padding: var(--space-2);
  background: transparent;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
}

.clear-btn:hover {
  color: var(--text-primary);
}

.suggestions-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: var(--space-2);
  background: var(--bg-primary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-xl);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  max-height: 500px;
  overflow-y: auto;
  z-index: 1000;
}

.quick-filters {
  padding: var(--space-4);
}

.filter-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-3);
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.filter-tag {
  padding: var(--space-2) var(--space-3);
  background: var(--bg-secondary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-full);
  color: var(--text-primary);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.filter-tag:hover {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.search-suggestions {
  max-height: 400px;
  overflow-y: auto;
}

.suggestion-group {
  padding: var(--space-4);
}

.suggestion-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-muted);
  margin-bottom: var(--space-3);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.suggestion-item:hover {
  background: var(--bg-secondary);
}

.suggestion-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.suggestion-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.suggestion-brand {
  font-size: var(--text-xs);
  color: var(--text-muted);
  text-transform: uppercase;
}

.suggestion-name {
  font-size: var(--text-sm);
  color: var(--text-primary);
  font-weight: var(--font-medium);
}

.suggestion-price {
  font-size: var(--text-sm);
  color: var(--primary-color);
  font-weight: var(--font-bold);
}

.no-results {
  text-align: center;
  padding: var(--space-8);
  color: var(--text-muted);
}

.no-results svg {
  margin-bottom: var(--space-2);
  opacity: 0.5;
}

.view-all-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  padding: var(--space-4);
  border-top: 1px solid var(--border-light);
  color: var(--primary-color);
  font-weight: var(--font-semibold);
  text-decoration: none;
  transition: all var(--transition-normal);
}

.view-all-link:hover {
  background: var(--bg-secondary);
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 768px) {
  .enhanced-search {
    max-width: 100%;
  }

  .suggestions-dropdown {
    max-height: 400px;
  }
}
</style>

