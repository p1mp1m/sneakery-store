<template>
  <ErrorBoundary>
    <router-view />
  </ErrorBoundary>
</template>

<script setup>
import ErrorBoundary from '@/components/common/ErrorBoundary.vue'
// App.vue render router-view với ErrorBoundary để catch errors
// Routing sẽ tự động chọn layout (AdminLayout hoặc DefaultLayout) dựa trên route
</script>

<style>
/* Styles removed - Element Plus has been removed from the project */
</style>
