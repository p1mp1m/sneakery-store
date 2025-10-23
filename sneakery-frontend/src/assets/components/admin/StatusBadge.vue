<template>
  <span class="status-badge" :class="`status-${status}`">
    <span v-if="showIcon" class="material-icons">{{ icon }}</span>
    {{ text }}
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

<style scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1-5) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  white-space: nowrap;
  border: 1px solid;
}

.status-badge .material-icons {
  font-size: 0.875rem;
}

/* Status Colors */
.status-active,
.status-approved,
.status-completed,
.status-delivered,
.status-in-stock {
  background: var(--success-bg);
  color: var(--success-text);
  border-color: var(--success-border);
}

.status-inactive,
.status-cancelled,
.status-rejected,
.status-failed,
.status-out-of-stock {
  background: var(--error-bg);
  color: var(--error-text);
  border-color: var(--error-border);
}

.status-pending,
.status-processing,
.status-in-progress,
.status-scheduled,
.status-low-stock {
  background: var(--warning-bg);
  color: var(--warning-text);
  border-color: var(--warning-border);
}

.status-draft,
.status-upcoming,
.status-sent,
.status-shipped {
  background: var(--info-bg);
  color: var(--info-text);
  border-color: var(--info-border);
}

.status-expired {
  background: rgba(100, 116, 139, 0.1);
  color: var(--text-tertiary);
  border-color: var(--border-primary);
}

/* Animated statuses */
.status-processing .material-icons,
.status-in-progress .material-icons {
  animation: spin 2s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>

