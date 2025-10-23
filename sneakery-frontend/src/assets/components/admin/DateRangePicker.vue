<template>
  <div class="date-range-picker">
    <div class="quick-filters">
      <button
        v-for="preset in presets"
        :key="preset.key"
        @click="applyPreset(preset)"
        class="preset-btn"
        :class="{ 'active': activePreset === preset.key }"
      >
        {{ preset.label }}
      </button>
    </div>

    <div class="date-inputs">
      <div class="date-input-group">
        <label>Từ ngày</label>
        <input
          v-model="startDate"
          type="date"
          class="form-control"
          :max="endDate || today"
        />
      </div>
      <div class="date-input-group">
        <label>Đến ngày</label>
        <input
          v-model="endDate"
          type="date"
          class="form-control"
          :min="startDate"
          :max="today"
        />
      </div>
    </div>

    <div class="actions">
      <button @click="clear" class="btn btn-secondary btn-sm">
        <i class="material-icons">clear</i>
        Xóa
      </button>
      <button @click="apply" class="btn btn-primary btn-sm">
        <i class="material-icons">check</i>
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

<style scoped>
.date-range-picker {
  background: var(--dark-bg-card, rgba(30, 41, 59, 0.6));
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-xl);
  padding: var(--space-5);
  backdrop-filter: blur(10px);
}

.quick-filters {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
  padding-bottom: var(--space-4);
  border-bottom: 1px solid var(--border-primary);
}

.preset-btn {
  padding: var(--space-2) var(--space-4);
  background: transparent;
  border: 1px solid var(--border-primary);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: var(--transition-fast);
}

.preset-btn:hover {
  background: var(--gradient-purple-soft);
  border-color: var(--accent-primary);
}

.preset-btn.active {
  background: var(--gradient-primary);
  border-color: var(--accent-primary);
  color: white;
}

.date-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
}

.date-input-group label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  margin-bottom: var(--space-2);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-2);
}

@media (max-width: 640px) {
  .quick-filters {
    flex-direction: column;
  }
  
  .preset-btn {
    width: 100%;
  }
  
  .date-inputs {
    grid-template-columns: 1fr;
  }
}
</style>

