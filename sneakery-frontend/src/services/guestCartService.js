import axios from 'axios';
import { API_BASE_URL } from '@/config/api';
import logger from '@/utils/logger';

const SESSION_ID_KEY = 'guest_session_id';
const GUEST_CART_KEY = 'guest_cart';

/**
 * Tạo hoặc lấy session ID từ localStorage
 */
export function generateSessionId() {
  let sessionId = localStorage.getItem(SESSION_ID_KEY);
  if (!sessionId) {
    // Tạo session ID unique
    sessionId = 'guest_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    localStorage.setItem(SESSION_ID_KEY, sessionId);
  }
  return sessionId;
}

/**
 * Lấy session ID hiện tại
 */
export function getSessionId() {
  return localStorage.getItem(SESSION_ID_KEY) || generateSessionId();
}

/**
 * Lấy guest cart từ localStorage hoặc API
 */
export async function getGuestCart() {
  const sessionId = getSessionId();
  
  try {
    // Lấy từ API
    const response = await axios.get(`${API_BASE_URL}/guest/cart`, {
      params: { sessionId }
    });
    
    // Lưu vào localStorage để cache
    localStorage.setItem(GUEST_CART_KEY, JSON.stringify(response.data));
    
    return response.data;
  } catch (error) {
    logger.error('Error fetching guest cart:', error);
    
    // Fallback: lấy từ localStorage nếu có
    const cachedCart = localStorage.getItem(GUEST_CART_KEY);
    if (cachedCart) {
      return JSON.parse(cachedCart);
    }
    
    // Trả về cart rỗng
    return {
      cartId: null,
      items: [],
      totalItems: 0,
      subTotal: 0
    };
  }
}

/**
 * Thêm sản phẩm vào guest cart
 */
export async function addToGuestCart(variantId, quantity = 1) {
  const sessionId = getSessionId();
  
  try {
    const response = await axios.post(
      `${API_BASE_URL}/guest/cart/item?sessionId=${sessionId}`,
      { variantId, quantity }
    );
    
    // Cập nhật localStorage
    localStorage.setItem(GUEST_CART_KEY, JSON.stringify(response.data));
    
    return response.data;
  } catch (error) {
    logger.error('Error adding to guest cart:', error);
    throw error;
  }
}

/**
 * Xóa sản phẩm khỏi guest cart
 */
export async function removeFromGuestCart(variantId) {
  const sessionId = getSessionId();
  
  try {
    const response = await axios.delete(
      `${API_BASE_URL}/guest/cart/item/${variantId}?sessionId=${sessionId}`
    );
    
    // Cập nhật localStorage
    localStorage.setItem(GUEST_CART_KEY, JSON.stringify(response.data));
    
    return response.data;
  } catch (error) {
    logger.error('Error removing from guest cart:', error);
    throw error;
  }
}

/**
 * Đồng bộ guest cart với backend
 */
export async function syncGuestCartWithBackend() {
  const sessionId = getSessionId();
  const cachedCart = localStorage.getItem(GUEST_CART_KEY);
  
  if (!cachedCart) {
    return await getGuestCart();
  }
  
  try {
    // Lấy từ API để đảm bảo đồng bộ
    const cart = await getGuestCart();
    return cart;
  } catch (error) {
    logger.error('Error syncing guest cart:', error);
    // Trả về cached cart nếu API fail
    return JSON.parse(cachedCart);
  }
}

/**
 * Xóa guest cart (sau khi checkout thành công hoặc đăng nhập)
 */
export function clearGuestCart() {
  localStorage.removeItem(GUEST_CART_KEY);
  localStorage.removeItem(SESSION_ID_KEY);
}

/**
 * Lấy guest cart từ localStorage (không gọi API)
 */
export function getGuestCartFromStorage() {
  const cachedCart = localStorage.getItem(GUEST_CART_KEY);
  if (cachedCart) {
    return JSON.parse(cachedCart);
  }
  return {
    cartId: null,
    items: [],
    totalItems: 0,
    subTotal: 0
  };
}

