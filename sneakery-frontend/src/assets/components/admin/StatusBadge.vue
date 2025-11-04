<template>
  <span 
    class="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full"
    :class="{
      'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400': ['active', 'approved', 'completed', 'delivered', 'in-stock'].includes(status),
      'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400': ['pending', 'in-progress', 'processing', 'scheduled', 'low-stock', 'upcoming'].includes(status),
      'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400': ['inactive', 'rejected', 'cancelled', 'failed', 'expired', 'out-of-stock'].includes(status),
      'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400': ['shipped', 'sent'].includes(status),
      'bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400': ['draft'].includes(status)
    }"
  >
    <i v-if="showIcon" class="material-icons text-sm">{{ icon }}</i>
    {{ text || status }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: {
    type: String,
    required: true,
    validator: (value) => [
      'active', 'inactive', 'pending', 'approved', 'rejected',
      'processing', 'shipped', 'delivered', 'cancelled',
      'completed', 'in-progress', 'draft', 'scheduled', 'sent',
      'failed', 'expired', 'upcoming', 'low-stock', 'out-of-stock',
      'in-stock'
    ].includes(value)
  },
  text: {
    type: String,
    default: ''
  },
  showIcon: {
    type: Boolean,
    default: true
  }
})

const icon = computed(() => {
  const icons = {
    active: 'check_circle',
    inactive: 'cancel',
    pending: 'schedule',
    approved: 'check_circle',
    rejected: 'block',
    processing: 'autorenew',
    shipped: 'local_shipping',
    delivered: 'done_all',
    cancelled: 'close',
    completed: 'check_circle',
    'in-progress': 'autorenew',
    draft: 'edit_note',
    scheduled: 'schedule_send',
    sent: 'send',
    failed: 'error',
    expired: 'event_busy',
    upcoming: 'event',
    'low-stock': 'warning',
    'out-of-stock': 'remove_shopping_cart',
    'in-stock': 'inventory'
  }
  return icons[props.status] || 'info'
})
</script>




