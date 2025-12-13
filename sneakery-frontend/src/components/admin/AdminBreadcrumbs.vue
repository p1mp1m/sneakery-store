<template>
  <nav v-if="breadcrumbs.length > 0" class="flex items-center space-x-2 text-sm" aria-label="Breadcrumb">
    <ol class="flex items-center space-x-2">
      <li v-for="(crumb, index) in breadcrumbs" :key="index" class="flex items-center">
        <router-link
          v-if="crumb.path && index < breadcrumbs.length - 1"
          :to="crumb.path"
          class="text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 transition-colors flex items-center gap-1"
        >
          <i v-if="index === 0" class="material-icons text-base">home</i>
          <span>{{ crumb.title }}</span>
        </router-link>
        <span
          v-else
          class="text-gray-900 dark:text-gray-100 font-medium flex items-center gap-1"
          :aria-current="index === breadcrumbs.length - 1 ? 'page' : undefined"
        >
          <i v-if="index === 0" class="material-icons text-base">home</i>
          <span>{{ crumb.title }}</span>
        </span>
        <i
          v-if="index < breadcrumbs.length - 1"
          class="material-icons text-gray-400 dark:text-gray-500 text-sm ml-2"
        >
          chevron_right
        </i>
      </li>
    </ol>
  </nav>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const breadcrumbs = computed(() => {
  const crumbs = [];
  const pathSegments = route.path.split('/').filter(Boolean);

  // Always start with Dashboard
  if (pathSegments[0] === 'admin') {
    crumbs.push({
      title: 'Dashboard',
      path: '/admin/dashboard'
    });
  }

  // Map route paths to breadcrumb titles
  const routeMap = {
    'dashboard': 'Dashboard',
    'products': 'Sản phẩm',
    'product-variants': 'Biến thể',
    'orders': 'Đơn hàng',
    'users': 'Người dùng',
    'brands': 'Thương hiệu',
    'categories': 'Danh mục',
    'materials': 'Chất liệu',
    'shoesoles': 'Loại đế giày',
    'reviews': 'Đánh giá',
    'flash-sales': 'Flash Sale',
    'discounts': 'Giảm giá',
    'returns': 'Trả hàng',
    'warranty': 'Bảo hành',
    'analytics': 'Phân tích',
    'notifications': 'Thông báo',
    'settings': 'Cài đặt',
    'profile': 'Hồ sơ',
    'change-password': 'Đổi mật khẩu',
    'activity-logs': 'Nhật ký',
    'inventory': 'Kho hàng',
    'loyalty': 'Điểm thưởng',
    'payments': 'Thanh toán',
    'email-templates': 'Mẫu email',
    'sales': 'Bán hàng (POS)'
  };

  // Build breadcrumbs from path segments
  let currentPath = '';
  pathSegments.forEach((segment, index) => {
    if (segment === 'admin') {
      currentPath = '/admin';
      return;
    }

    currentPath += `/${segment}`;

    // Check if it's a dynamic route (e.g., :id)
    if (segment.match(/^\d+$/)) {
      // It's an ID, use route meta title or fallback
      const title = route.meta?.title || `Chi tiết #${segment}`;
      crumbs.push({
        title,
        path: index === pathSegments.length - 1 ? null : currentPath
      });
    } else {
      const title = routeMap[segment] || segment.charAt(0).toUpperCase() + segment.slice(1);
      crumbs.push({
        title,
        path: index === pathSegments.length - 1 ? null : currentPath
      });
    }
  });

  return crumbs;
});
</script>

<style scoped>
nav {
  min-height: 1.5rem;
}
</style>

