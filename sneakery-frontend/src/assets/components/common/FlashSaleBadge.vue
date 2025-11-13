<template>
  <div v-if="flashSale" class="flash-sale-badge">
    <!-- Flash Sale Icon & Label -->
    <div class="flash-sale-header">
      <span class="material-icons">bolt</span>
      <span class="flash-sale-label">FLASH SALE</span>
    </div>

    <!-- Discount Percentage -->
    <div class="discount-percentage">
      -{{ formatDiscountPercent(flashSale.discountPercent) }}%
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

// Format discount percent (handle BigDecimal from backend)
const formatDiscountPercent = (discountPercent) => {
  if (!discountPercent) return 0
  const discount = typeof discountPercent === 'number' 
    ? discountPercent 
    : parseFloat(discountPercent) || 0
  return Math.round(discount)
}

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
  top: 12px;
  left: 12px;
  z-index: 10;
  background: linear-gradient(135deg, #f97316, #ef4444, #ec4899);
  border-radius: 12px;
  padding: 8px 12px;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
  color: white;
  font-weight: 700;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  min-width: 120px;
}

.flash-sale-badge.compact {
  top: 8px;
  left: 8px;
  padding: 6px 10px;
  font-size: 0.7rem;
  min-width: 100px;
}

.flash-sale-header {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.flash-sale-header .material-icons {
  font-size: 16px;
  animation: pulse 2s infinite;
}

.flash-sale-label {
  font-size: 0.65rem;
  font-weight: 800;
  letter-spacing: 0.1em;
}

.discount-percentage {
  font-size: 1rem;
  font-weight: 900;
  text-align: center;
  margin: 4px 0;
}

.countdown-timer {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.7rem;
  margin-top: 4px;
  padding-top: 4px;
  border-top: 1px solid rgba(255, 255, 255, 0.3);
}

.countdown-timer .timer-icon {
  font-size: 14px;
}

.coming-soon {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.7rem;
  margin-top: 4px;
  padding-top: 4px;
  border-top: 1px solid rgba(255, 255, 255, 0.3);
}

.coming-soon .material-icons {
  font-size: 14px;
}

.quantity-progress {
  margin-top: 6px;
  padding-top: 6px;
  border-top: 1px solid rgba(255, 255, 255, 0.3);
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 4px;
}

.progress-fill {
  height: 100%;
  background: white;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.65rem;
  text-align: center;
  display: block;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}
</style>

