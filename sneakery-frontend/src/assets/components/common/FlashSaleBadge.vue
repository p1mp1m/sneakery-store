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




