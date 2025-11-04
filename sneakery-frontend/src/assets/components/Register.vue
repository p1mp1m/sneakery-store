<template>
    <div class="form-container">
      <h2>Đăng Ký Tài Khoản Mới</h2>
      <form @submit.prevent="handleRegister">
        
        <div v-if="errorMessage" class="message error-message">
          {{ errorMessage }}
        </div>
        <div v-if="successMessage" class="message success-message">
          {{ successMessage }}
        </div>
  
        <div class="form-group">
          <label for="fullName">Họ và Tên</label>
          <input type="text" id="fullName" v-model="user.fullName" required />
        </div>
  
        <div class="form-group">
          <label for="email">Email</label>
          <input type="email" id="email" v-model="user.email" required />
        </div>
  
        <div class="form-group">
          <label for="password">Mật khẩu</label>
          <input type="password" id="password" v-model="user.password" required />
        </div>
        
        <div class="form-group">
          <label for="phoneNumber">Số điện thoại (tùy chọn)</label>
          <input type="tel" id="phoneNumber" v-model="user.phoneNumber" />
        </div>
  
        <button type="submit" :disabled="loading">
          <span v-if="loading">Đang xử lý...</span>
          <span v-else>Đăng Ký</span>
        </button>
      </form>
    </div>
  </template>
  
  <script>
  import AuthService from '@/services/authService';
  
  export default {
    name: 'Register',
    data() {
      return {
        user: {
          fullName: '',
          email: '',
          password: '',
          phoneNumber: '',
        },
        loading: false,
        errorMessage: null,
        successMessage: null,
      };
    },
    methods: {
      handleRegister() {
        // Reset messages trước khi gửi yêu cầu mới
        this.errorMessage = null;
        this.successMessage = null;
        this.loading = true;
  
        AuthService.register(this.user)
          .then(response => {
            this.successMessage = response.data; // "Đăng ký người dùng thành công!"
            // Xóa form sau khi thành công
            this.user = { fullName: '', email: '', password: '', phoneNumber: '' }; 
          })
          .catch(error => {
            // Lấy thông báo lỗi từ backend
            if (error.response && error.response.data) {
                // Lỗi từ GlobalExceptionHandler (ErrorDetailsDto)
                this.errorMessage = error.response.data.message || 'Đã có lỗi không xác định xảy ra.';
            } else {
                this.errorMessage = 'Không thể kết nối đến máy chủ. Vui lòng thử lại sau.';
            }
          })
          .finally(() => {
            this.loading = false;
          });
      },
    },
  };
  </script>
  
  
