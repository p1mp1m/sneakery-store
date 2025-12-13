<template>
  <button 
    :class="['google-button', { 'loading': loading, 'disabled': loading }]"
    :disabled="loading"
    @click="handleClick"
    type="button"
  >
    <div class="google-icon-wrapper">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M22.56 12.25C22.56 11.47 22.49 10.72 22.36 10H12V14.255H17.92C17.665 15.63 16.89 16.795 15.725 17.575V20.335H19.28C21.36 18.42 22.56 15.6 22.56 12.25Z" fill="#4285F4"/>
        <path d="M12 23C15.24 23 17.955 21.935 19.28 20.335L15.725 17.575C14.735 18.235 13.48 18.625 12 18.625C8.87 18.625 6.22 16.585 5.405 13.71H1.77V16.57C3.045 19.13 7.26 23 12 23Z" fill="#34A853"/>
        <path d="M5.405 13.71C5.205 13.07 5.09 12.395 5.09 11.7C5.09 11.005 5.205 10.33 5.405 9.69V6.83H1.77C1.035 8.195 0.64 9.735 0.64 11.35C0.64 12.965 1.035 14.505 1.77 15.87L5.405 13.71Z" fill="#FBBC05"/>
        <path d="M12 4.375C13.615 4.375 15.065 4.93 16.205 5.985L19.36 2.83C17.955 1.495 15.24 0.5 12 0.5C7.26 0.5 3.045 4.37 1.77 6.83L5.405 9.69C6.22 6.815 8.87 4.775 12 4.375Z" fill="#EA4335"/>
      </svg>
    </div>
    <span class="button-text">{{ text }}</span>
    <div v-if="loading" class="loading-spinner">
      <svg class="animate-spin" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" stroke-opacity="0.25"/>
        <path fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
      </svg>
    </div>
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

const handleClick = (event) => {
  if (props.loading) return
  emit('click', event)
}
</script>

<style scoped>
.google-button {
  @apply relative w-full flex items-center justify-center gap-3 px-4 py-3 
         bg-white dark:bg-gray-700 border-2 border-gray-300 dark:border-gray-600 
         rounded-xl text-gray-700 dark:text-gray-300 
         font-medium text-sm
         transition-all duration-300 
         hover:bg-gray-50 dark:hover:bg-gray-600 
         hover:border-gray-400 dark:hover:border-gray-500
         hover:shadow-lg hover:-translate-y-0.5
         active:translate-y-0 active:shadow-md
         focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2
         disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0
         overflow-hidden;
}

.google-icon-wrapper {
  @apply flex items-center justify-center flex-shrink-0;
}

.button-text {
  @apply flex-1 text-center font-medium;
}

.loading-spinner {
  @apply flex items-center justify-center flex-shrink-0;
}

.google-button.loading .button-text {
  @apply opacity-75;
}

.google-button:active {
  @apply transform scale-[0.98];
}

/* Ripple effect on click */
.google-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.google-button:active::before {
  width: 300px;
  height: 300px;
}
</style>



