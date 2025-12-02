/**
 * API Configuration
 * Centralized API URL management
 */

// Get API URL from environment variable or use default
const getApiUrl = () => {
  // In development, Vite proxy will handle /api requests
  // In production, use environment variable or default
  if (import.meta.env.DEV) {
    // Development: Use proxy (no need for full URL)
    return '';
  }
  
  // Production: Use environment variable or default
  return import.meta.env.VITE_API_URL || 'http://localhost:8080';
};

export const API_BASE_URL = getApiUrl();

// API Endpoints
export const API_ENDPOINTS = {
  // Auth
  AUTH: {
    LOGIN: '/api/auth/login',
    REGISTER: '/api/auth/register',
    LOGOUT: '/api/auth/logout',
    PROFILE: '/api/auth/profile',
    CHANGE_PASSWORD: '/api/auth/change-password',
    FORGOT_PASSWORD: '/api/auth/forgot-password',
    RESET_PASSWORD: '/api/auth/reset-password',
  },

  // Products
  PRODUCTS: {
    BASE: '/api/products',
    SEARCH: '/api/products/search',
    BY_ID: (id) => `/api/products/${id}`,
    BY_SLUG: (slug) => `/api/products/slug/${slug}`,
    CATEGORIES: '/api/products/categories',
    CATEGORIES_GROUPS: '/api/products/categories/groups',
    BRANDS: '/api/products/brands',
    RELATED: (id) => `/api/products/${id}/related`,
  },
  
  // Admin Products
  ADMIN_PRODUCTS: {
    BASE: '/api/admin/products',
    BY_ID: (id) => `/api/admin/products/${id}`,
    BRANDS: '/api/admin/brands',
    CATEGORIES: '/api/admin/categories',
    MATERIALS: '/api/admin/materials',
    SOLES: '/api/admin/soles',
    REVIEWS: '/api/admin/reviews',
  },
  
  // Cart
  CART: {
    BASE: '/api/cart',
    ITEM: '/api/cart/item',
    GUEST_CHECKOUT: '/api/guest/checkout',
  },
  
  // Wishlist
  WISHLIST: {
    BASE: '/api/wishlist',
  },
  
  // Orders
  ORDERS: {
    BASE: '/api/orders',
    BY_ID: (id) => `/api/orders/${id}`,
    CHECKOUT: '/api/orders/checkout',
    VALIDATE_COUPON: (code) => `/api/orders/coupons/validate/${code}`,
    ACTIVE_COUPONS: '/api/orders/coupons/active',
    CANCEL: (id) => `/api/orders/${id}/cancel`,
  },

  //Payments
  PAYMENT: {
    USER: {
      VNPAY: "/api/payment/vnpay/create",
      MOMO: "/api/payment/momo/create"
    },

    GUEST: {
      VNPAY: "/api/guest/payment/vnpay/create",
      MOMO: "/api/guest/payment/momo/create"
    }
    },
  
  // Flash Sales
  FLASH_SALES: {
    BASE: '/api/flash-sales',
    ACTIVE: '/api/flash-sales/active',
    BY_PRODUCT: (productId) => `/api/flash-sales/product/${productId}`,
  },
  
  // Admin Flash Sales
  ADMIN_FLASH_SALES: {
    BASE: '/api/admin/flash-sales',
    BY_ID: (id) => `/api/admin/flash-sales/${id}`,
  },
  
  // Loyalty
  LOYALTY: {
    BASE: '/api/loyalty',
  },

  SHIPPING: {
    CALCULATE: "/api/shipping/calculate",
  },
  
  // Notifications
  NOTIFICATIONS: {
    BASE: '/api/notifications',
  },
  
  // Size Charts
  SIZE_CHARTS: {
    BASE: '/api/size-charts',
  },
  
  // Reviews
  REVIEWS: {
    APPROVED: '/api/reviews/approved',
    BY_PRODUCT: (productId) => `/api/products/${productId}/reviews`,
  },
  
  // Newsletter
  NEWSLETTER: {
    SUBSCRIBE: '/api/newsletter/subscribe',
    UNSUBSCRIBE: '/api/newsletter/unsubscribe',
  },
};

// Helper function to build full URL
export const buildApiUrl = (endpoint) => {
  if (endpoint.startsWith('http')) {
    return endpoint; // Already a full URL
  }
  
  if (API_BASE_URL && !endpoint.startsWith('/api')) {
    return `${API_BASE_URL}${endpoint}`;
  }
  
  // In development with proxy, just return the endpoint
  return endpoint;
};

export default {
  API_BASE_URL,
  API_ENDPOINTS,
  buildApiUrl,
};

