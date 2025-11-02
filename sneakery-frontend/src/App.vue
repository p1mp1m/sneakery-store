<template>
  <router-view />
</template>

<script setup>
// App.vue chỉ cần render router-view
// Routing sẽ tự động chọn layout (AdminLayout hoặc DefaultLayout) dựa trên route
</script>

<style>
/* Global styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.el-message {
  right: 20px !important;
  left: auto !important;
  justify-content: flex-start !important;
}

/* ===== Aurora Message v3.0 ===== */
.aurora-message {
  position: fixed !important;
  right: 24px !important;
  left: auto !important;
  top: 20px !important;
  margin: 0 !important;
  min-width: 260px;
  border-radius: 14px !important;
  backdrop-filter: blur(12px) saturate(160%) !important;
  background: linear-gradient(
    145deg,
    rgba(255, 255, 255, 0.85),
    rgba(245, 247, 255, 0.65)
  ) !important;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
  color: #222 !important;
  animation: slideAurora 0.35s cubic-bezier(0.16, 1, 0.3, 1);
}

/* ===== Kiểu chữ và spacing ===== */
.aurora-message .el-message__content {
  font-weight: 500;
  font-size: 0.95rem;
  letter-spacing: 0.2px;
}

.aurora-message .el-message__icon {
  margin-right: 8px;
}

/* ===== Kiểu loại thông báo ===== */
.aurora-message.el-message--success {
  background: linear-gradient(145deg, #e6fff0, #c8ffdf);
  border-left: 5px solid #10b981;
}

.aurora-message.el-message--error {
  background: linear-gradient(145deg, #ffeaea, #ffd4d4);
  border-left: 5px solid #ef4444;
}

.aurora-message.el-message--warning {
  background: linear-gradient(145deg, #fff8e6, #ffeccc);
  border-left: 5px solid #f59e0b;
}

.aurora-message.el-message--info {
  background: linear-gradient(145deg, #e6f0ff, #dbe9ff);
  border-left: 5px solid #3b82f6;
}

/* ===== Hiệu ứng xuất hiện ===== */
@keyframes slideAurora {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.96);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ===== Hover nhẹ ===== */
.aurora-message:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.18);
  transition: all 0.25s ease;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen",
    "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f8fafc;
}

#app {
  min-height: 100vh;
}

/* CSS Variables */
:root {
  --primary-color: #667eea;
  --primary-hover: #5a67d8;
  --primary-light: #e6f0ff;
  --text-primary: #2d3748;
  --text-secondary: #4a5568;
  --text-tertiary: #718096;
  --bg-primary: #ffffff;
  --bg-secondary: #f7fafc;
  --bg-tertiary: #edf2f7;
  --bg-card: #ffffff;
  --border-light: #e2e8f0;
  --border-medium: #cbd5e0;
  --border-dark: #a0aec0;
  --white: #ffffff;
  --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.1);
  --shadow-xl: 0 20px 25px rgba(0, 0, 0, 0.1);
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --radius-full: 50%;
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-6: 24px;
  --space-8: 32px;
  --space-12: 48px;
  --text-xs: 12px;
  --text-sm: 14px;
  --text-base: 16px;
  --text-lg: 18px;
  --text-xl: 20px;
  --text-2xl: 24px;
  --text-3xl: 30px;
  --font-normal: 400;
  --font-medium: 500;
  --font-semibold: 600;
  --font-bold: 700;
  --leading-tight: 1.25;
  --leading-normal: 1.5;
  --leading-relaxed: 1.625;
  --transition-fast: 0.15s ease;
  --transition-normal: 0.3s ease;
  --z-dropdown: 1000;
  --z-fixed: 100;
}
</style>
