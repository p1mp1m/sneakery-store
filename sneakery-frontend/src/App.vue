<template>
  <router-view />
</template>

<script setup>
// App.vue chỉ cần render router-view
// Routing sẽ tự động chọn layout (AdminLayout hoặc DefaultLayout) dựa trên route
</script>

<style>
/* Element Plus Message Override - Giữ lại để override library */
.el-message {
  right: 20px !important;
  left: auto !important;
  justify-content: flex-start !important;
}

/* ===== Aurora Message v3.0 - Custom Element Plus Message Styles ===== */
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

.aurora-message .el-message__content {
  font-weight: 500;
  font-size: 0.95rem;
  letter-spacing: 0.2px;
}

.aurora-message .el-message__icon {
  margin-right: 8px;
}

.aurora-message.el-message--success {
  background: linear-gradient(145deg, #e6fff0, #c8ffdf) !important;
  border-left: 5px solid #10b981 !important;
}

.aurora-message.el-message--error {
  background: linear-gradient(145deg, #ffeaea, #ffd4d4) !important;
  border-left: 5px solid #ef4444 !important;
}

.aurora-message.el-message--warning {
  background: linear-gradient(145deg, #fff8e6, #ffeccc) !important;
  border-left: 5px solid #f59e0b !important;
}

.aurora-message.el-message--info {
  background: linear-gradient(145deg, #e6f0ff, #dbe9ff) !important;
  border-left: 5px solid #3b82f6 !important;
}

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

.aurora-message:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.18);
  transition: all 0.25s ease;
}
</style>
