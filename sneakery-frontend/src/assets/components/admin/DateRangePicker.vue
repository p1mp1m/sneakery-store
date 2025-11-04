<template>
  <div class="p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 space-y-4">
    <div class="flex flex-wrap gap-2">
      <button
        v-for="preset in presets"
        :key="preset.key"
        @click="applyPreset(preset)"
        class="px-3 py-1.5 text-sm font-medium rounded-lg transition-colors"
        :class="activePreset === preset.key 
          ? 'bg-purple-500 text-white' 
          : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'"
      >
        {{ preset.label }}
      </button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Từ ngày</label>
        <input
          v-model="startDate"
          type="date"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
          :max="endDate || today"
        />
      </div>
      <div class="flex flex-col gap-2">
        <label class="text-xs font-medium text-gray-700 dark:text-gray-300">Đến ngày</label>
        <input
          v-model="endDate"
          type="date"
          class="px-3 py-2 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded-lg text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
          :min="startDate"
          :max="today"
        />
      </div>
    </div>

    <div class="flex items-center justify-end gap-2 pt-4 border-t border-gray-200 dark:border-gray-700">
      <button 
        @click="clear" 
        class="flex items-center gap-2 px-3 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors text-sm font-medium"
      >
        <i class="material-icons text-base">clear</i>
        Xóa
      </button>
      <button 
        @click="apply" 
        class="flex items-center gap-2 px-3 py-2 bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 text-sm font-medium shadow-sm"
      >
        <i class="material-icons text-base">check</i>
        Áp dụng
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({ start: null, end: null })
  }
})

const emit = defineEmits(['update:modelValue', 'apply'])

const startDate = ref(props.modelValue.start || '')
const endDate = ref(props.modelValue.end || '')
const activePreset = ref(null)

const today = computed(() => {
  return new Date().toISOString().split('T')[0]
})

const presets = [
  {
    key: 'today',
    label: 'Hôm nay',
    getDates: () => {
      const today = new Date()
      return {
        start: today.toISOString().split('T')[0],
        end: today.toISOString().split('T')[0]
      }
    }
  },
  {
    key: 'yesterday',
    label: 'Hôm qua',
    getDates: () => {
      const yesterday = new Date()
      yesterday.setDate(yesterday.getDate() - 1)
      return {
        start: yesterday.toISOString().split('T')[0],
        end: yesterday.toISOString().split('T')[0]
      }
    }
  },
  {
    key: 'last7days',
    label: '7 ngày qua',
    getDates: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 6)
      return {
        start: start.toISOString().split('T')[0],
        end: end.toISOString().split('T')[0]
      }
    }
  },
  {
    key: 'last30days',
    label: '30 ngày qua',
    getDates: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 29)
      return {
        start: start.toISOString().split('T')[0],
        end: end.toISOString().split('T')[0]
      }
    }
  },
  {
    key: 'thisMonth',
    label: 'Tháng này',
    getDates: () => {
      const now = new Date()
      const start = new Date(now.getFullYear(), now.getMonth(), 1)
      return {
        start: start.toISOString().split('T')[0],
        end: now.toISOString().split('T')[0]
      }
    }
  }
]

const applyPreset = (preset) => {
  const dates = preset.getDates()
  startDate.value = dates.start
  endDate.value = dates.end
  activePreset.value = preset.key
  apply()
}

const apply = () => {
  emit('update:modelValue', {
    start: startDate.value,
    end: endDate.value
  })
  emit('apply', {
    start: startDate.value,
    end: endDate.value
  })
}

const clear = () => {
  startDate.value = ''
  endDate.value = ''
  activePreset.value = null
  emit('update:modelValue', { start: null, end: null })
  emit('apply', { start: null, end: null })
}
</script>




