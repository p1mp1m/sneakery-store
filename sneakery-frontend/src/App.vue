<template>
  <ErrorBoundary>
    <router-view />
  </ErrorBoundary>
  <!-- Notification Container - Global, available on all routes (outside ErrorBoundary to ensure it always renders) -->
  <NotificationContainer />
</template>

<script setup>
import ErrorBoundary from '@/components/common/ErrorBoundary.vue'
import NotificationContainer from '@/components/NotificationContainer.vue'
// App.vue render router-view với ErrorBoundary để catch errors
// Routing sẽ tự động chọn layout (AdminLayout hoặc DefaultLayout) dựa trên route
// NotificationContainer được thêm ở đây để đảm bảo nó luôn được render trên mọi route
</script>

<style>
/* Styles removed - Element Plus has been removed from the project */
</style>
