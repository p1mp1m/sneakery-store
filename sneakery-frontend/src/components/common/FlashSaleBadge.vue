<template>
  <div v-if="flashSale" class="flash-sale-badge">
    <!-- Flash Sale Icon & Label -->
    <div class="flash-sale-header">
      <span class="material-icons">bolt</span>
      <span class="flash-sale-label">FLASH SALE</span>
    </div>

    <!-- Discount Percentage -->
    <div class="discount-percentage">
      -{{ flashSale.discountPercent }}%
    </div>

    <!-- Countdown Timer (if active) -->
    <div v-if="isActive && timeLeft" class="countdown-timer">
      <span class="material-icons timer-icon">schedule</span>
      <span class="timer-text">{{ formattedTimeLeft }}</span>
    </div>

    <!-- Coming Soon (if upcoming) -->
    <div v-else-if="isUpcoming" class="coming-soon">
      <span class="material-icons">access_time</span>
      <span>Sắp diễn ra</span>
    </div>

    <!-- Quantity Progress (if limited) -->
    <div v-if="flashSale.quantityLimit && isActive" class="quantity-progress">
      <div class="progress-bar">
        <div 
          class="progress-fill" 
          :style="{ width: `${progressPercentage}%` }"
        ></div>
      </div>
      <span class="progress-text">
        Còn {{ remainingQuantity }}/{{ flashSale.quantityLimit }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// Props
const props = defineProps({
  flashSale: {
    type: Object,
    required: true
  }
})

// Local state
const currentTime = ref(new Date())
let intervalId = null

// Computed properties
const isActive = computed(() => {
  if (!props.flashSale) return false
  const now = currentTime.value
  const start = new Date(props.flashSale.startTime)
  const end = new Date(props.flashSale.endTime)
  return now >= start && now <= end
})

const isUpcoming = computed(() => {
  if (!props.flashSale) return false
  const now = currentTime.value
  const start = new Date(props.flashSale.startTime)
  return now < start
})

const timeLeft = computed(() => {
  if (!props.flashSale || !isActive.value) return null
  const now = currentTime.value
  const end = new Date(props.flashSale.endTime)
  const diff = end - now
  return diff > 0 ? diff : 0
})

const formattedTimeLeft = computed(() => {
  if (!timeLeft.value) return ''
  
  const seconds = Math.floor(timeLeft.value / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) {
    return `${days} ngày ${hours % 24}h`
  } else if (hours > 0) {
    return `${hours}h ${minutes % 60}m`
  } else if (minutes > 0) {
    return `${minutes}m ${seconds % 60}s`
  } else {
    return `${seconds}s`
  }
})

const remainingQuantity = computed(() => {
  if (!props.flashSale.quantityLimit) return 0
  return props.flashSale.quantityLimit - (props.flashSale.soldCount || 0)
})

const progressPercentage = computed(() => {
  if (!props.flashSale.quantityLimit) return 0
  const sold = props.flashSale.soldCount || 0
  return Math.min((sold / props.flashSale.quantityLimit) * 100, 100)
})

// Update current time every second
const startTimer = () => {
  intervalId = setInterval(() => {
    currentTime.value = new Date()
  }, 1000)
}

const stopTimer = () => {
  if (intervalId) {
    clearInterval(intervalId)
    intervalId = null
  }
}

// Lifecycle
onMounted(() => {
  startTimer()
})

onUnmounted(() => {
  stopTimer()
})
</script>

<style scoped>
.flash-sale-badge {
  position: absolute;
  top: var(--space-2);
  left: var(--space-2);
  background: linear-gradient(135deg, #ff416c 0%, #ff4b2b 100%);
  color: white;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-lg);
  box-shadow: 0 4px 12px rgba(255, 65, 108, 0.4);
  z-index: 10;
  min-width: 120px;
  animation: pulse-glow 2s infinite;
}

@keyframes pulse-glow {
  0%, 100% {
    box-shadow: 0 4px 12px rgba(255, 65, 108, 0.4);
  }
  50% {
    box-shadow: 0 4px 20px rgba(255, 65, 108, 0.8);
  }
}

.flash-sale-header {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  margin-bottom: var(--space-1);
}

.flash-sale-header .material-icons {
  font-size: 16px;
  animation: bolt-flash 1.5s infinite;
}

@keyframes bolt-flash {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

.flash-sale-label {
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  letter-spacing: 0.5px;
}

.discount-percentage {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  line-height: 1;
  margin-bottom: var(--space-2);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.countdown-timer {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  background: rgba(255, 255, 255, 0.2);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  margin-top: var(--space-2);
}

.timer-icon {
  font-size: 14px;
}

.timer-text {
  font-family: 'Courier New', monospace;
}

.coming-soon {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  background: rgba(255, 255, 255, 0.2);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  margin-top: var(--space-2);
}

.coming-soon .material-icons {
  font-size: 14px;
}

.quantity-progress {
  margin-top: var(--space-2);
}

.progress-bar {
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: var(--space-1);
}

.progress-fill {
  height: 100%;
  background: white;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  display: block;
}

/* Compact Version (for small cards) */
.flash-sale-badge.compact {
  min-width: auto;
  padding: var(--space-1) var(--space-2);
}

.flash-sale-badge.compact .discount-percentage {
  font-size: var(--text-lg);
  margin-bottom: 0;
}

.flash-sale-badge.compact .flash-sale-header {
  margin-bottom: 0;
}

.flash-sale-badge.compact .countdown-timer,
.flash-sale-badge.compact .quantity-progress {
  display: none;
}

/* Responsive */
@media (max-width: 640px) {
  .flash-sale-badge {
    min-width: 100px;
    padding: var(--space-1) var(--space-2);
  }
  
  .discount-percentage {
    font-size: var(--text-xl);
  }
  
  .flash-sale-label {
    font-size: 10px;
  }
}
</style>

