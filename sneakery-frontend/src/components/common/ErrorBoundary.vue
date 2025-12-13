<template>
  <div v-if="hasError" class="error-boundary">
    <div class="max-w-md mx-auto mt-16 p-8 bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-red-200 dark:border-red-800">
      <div class="text-center">
        <div class="w-16 h-16 mx-auto mb-4 bg-red-100 dark:bg-red-900/30 rounded-full flex items-center justify-center">
          <i class="material-icons text-red-600 dark:text-red-400 text-4xl">error_outline</i>
        </div>
        
        <h2 class="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">
          Đã xảy ra lỗi
        </h2>
        
        <p class="text-gray-600 dark:text-gray-400 mb-6">
          {{ errorMessage }}
        </p>
        
        <div class="flex gap-3 justify-center">
          <button
            @click="handleRetry"
            class="px-6 py-3 bg-purple-600 text-white rounded-lg font-semibold hover:bg-purple-700 transition-colors focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2"
          >
            <i class="material-icons align-middle mr-2">refresh</i>
            Thử lại
          </button>
          
          <button
            @click="handleGoHome"
            class="px-6 py-3 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg font-semibold hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2"
          >
            <i class="material-icons align-middle mr-2">home</i>
            Về trang chủ
          </button>
        </div>
        
        <details v-if="showDetails" class="mt-6 text-left">
          <summary class="cursor-pointer text-sm text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300">
            Chi tiết lỗi
          </summary>
          <pre class="mt-2 p-4 bg-gray-100 dark:bg-gray-900 rounded text-xs text-gray-800 dark:text-gray-200 overflow-auto">{{ errorDetails }}</pre>
        </details>
      </div>
    </div>
  </div>
  
  <slot v-else />
</template>

<script setup>
import { ref, onErrorCaptured, provide } from 'vue';
import { useRouter } from 'vue-router';
import logger from '@/utils/logger';

const router = useRouter();
const hasError = ref(false);
const errorMessage = ref('Có vấn đề xảy ra khi tải trang này. Vui lòng thử lại sau.');
const errorDetails = ref('');
const showDetails = ref(false);

// Capture errors from child components
onErrorCaptured((err, instance, info) => {
  logger.error('ErrorBoundary caught error:', err, info);
  
  hasError.value = true;
  errorMessage.value = err.message || 'Đã xảy ra lỗi không mong muốn';
  errorDetails.value = `${err.toString()}\n\nComponent: ${instance?.$?.type?.name || 'Unknown'}\n\nInfo: ${info}`;
  
  // Show details in development mode
  if (import.meta.env.DEV) {
    showDetails.value = true;
  }
  
  // Return false to prevent the error from propagating further
  return false;
});

const handleRetry = () => {
  hasError.value = false;
  errorMessage.value = '';
  errorDetails.value = '';
  showDetails.value = false;
  
  // Force re-render by reloading the route
  router.go(0);
};

const handleGoHome = () => {
  router.push('/home');
};

// Provide error handler for child components to report errors
provide('reportError', (error) => {
  logger.error('Component reported error:', error);
  hasError.value = true;
  errorMessage.value = error.message || 'Đã xảy ra lỗi';
  errorDetails.value = error.toString();
});
</script>

<style scoped>
.error-boundary {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

