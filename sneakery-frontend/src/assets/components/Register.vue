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
  
  <style scoped>
  /* Sử dụng lại style tương tự trang Login để đồng bộ */
  .form-container {
    max-width: 450px;
    margin: 50px auto;
    padding: 30px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  }
  h2 {
    text-align: center;
    margin-bottom: 20px;
  }
  .form-group {
    margin-bottom: 15px;
  }
  label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
  }
  input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box; /* Quan trọng để padding không làm tăng width */
  }
  button {
    width: 100%;
    padding: 12px;
    background-color: #28a745; /* Màu xanh lá cây cho đăng ký */
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
  }
  button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  .message {
    padding: 10px;
    margin-bottom: 15px;
    border-radius: 4px;
    text-align: center;
  }
  .error-message {
    color: #721c24;
    background-color: #f8d7da;
    border: 1px solid #f5c6cb;
  }
  .success-message {
    color: #155724;
    background-color: #d4edda;
    border: 1px solid #c3e6cb;
  }
  </style>