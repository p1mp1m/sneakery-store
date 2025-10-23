<template>
  <button 
    :class="['google-button', { 'loading': loading }]"
    :disabled="loading"
    @click="handleClick"
  >
    <div class="google-icon">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M22.56 12.25C22.56 11.47 22.49 10.72 22.36 10H12V14.255H17.92C17.665 15.63 16.89 16.795 15.725 17.575V20.335H19.28C21.36 18.42 22.56 15.6 22.56 12.25Z" fill="#4285F4"/>
        <path d="M12 23C15.24 23 17.955 21.935 19.28 20.335L15.725 17.575C14.735 18.235 13.48 18.625 12 18.625C8.87 18.625 6.22 16.585 5.405 13.71H1.77V16.57C3.045 19.13 7.26 23 12 23Z" fill="#34A853"/>
        <path d="M5.405 13.71C5.205 13.07 5.09 12.395 5.09 11.7C5.09 11.005 5.205 10.33 5.405 9.69V6.83H1.77C1.035 8.195 0.64 9.735 0.64 11.35C0.64 12.965 1.035 14.505 1.77 15.87L5.405 13.71Z" fill="#FBBC05"/>
        <path d="M12 4.375C13.615 4.375 15.065 4.93 16.205 5.985L19.36 2.83C17.955 1.495 15.24 0.5 12 0.5C7.26 0.5 3.045 4.37 1.77 6.83L5.405 9.69C6.22 6.815 8.87 4.775 12 4.375Z" fill="#EA4335"/>
      </svg>
    </div>
    <span class="button-text">{{ text }}</span>
    <div class="ripple-effect" v-if="ripple"></div>
  </button>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  text: {
    type: String,
    default: 'Đăng nhập với Google'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

const ripple = ref(false)

const handleClick = (event) => {
  if (props.loading) return
  
  // Create ripple effect
  ripple.value = true
  setTimeout(() => {
    ripple.value = false
  }, 600)
  
  emit('click', event)
}
</script>

<style scoped>
.google-button {
  position: relative;
  width: 100%;
  height: 50px;
  background: #ffffff;
  border: 1px solid #dadce0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #3c4043;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: 0 1px 2px 0 rgba(60, 64, 67, 0.3), 0 1px 3px 1px rgba(60, 64, 67, 0.15);
}

.google-button:hover {
  background: #f8f9fa;
  border-color: #dadce0;
  box-shadow: 0 1px 3px 0 rgba(60, 64, 67, 0.3), 0 4px 8px 3px rgba(60, 64, 67, 0.15);
  transform: translateY(-1px);
}

.google-button:active {
  background: #f1f3f4;
  transform: translateY(0);
  box-shadow: 0 1px 2px 0 rgba(60, 64, 67, 0.3), 0 1px 3px 1px rgba(60, 64, 67, 0.15);
}

.google-button:focus {
  outline: none;
  box-shadow: 0 1px 2px 0 rgba(60, 64, 67, 0.3), 0 1px 3px 1px rgba(60, 64, 67, 0.15), 0 0 0 3px rgba(66, 133, 244, 0.12);
}

.google-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.google-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.google-button:hover .google-icon {
  transform: scale(1.1);
}

.button-text {
  font-family: 'Google Sans', 'Roboto', sans-serif;
  font-weight: 500;
  letter-spacing: 0.25px;
  transition: color 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.google-button:hover .button-text {
  color: #1a73e8;
}

.ripple-effect {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(66, 133, 244, 0.3);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  animation: ripple 0.6s ease-out;
  pointer-events: none;
}

@keyframes ripple {
  0% {
    width: 0;
    height: 0;
    opacity: 1;
  }
  100% {
    width: 200px;
    height: 200px;
    opacity: 0;
  }
}

/* Loading state */
.google-button.loading {
  pointer-events: none;
}

.google-button.loading .google-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* Responsive */
@media (max-width: 480px) {
  .google-button {
    height: 48px;
    font-size: 13px;
    gap: 10px;
  }
  
  .google-icon {
    width: 18px;
    height: 18px;
  }
}
</style>
