<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      leave-active-class="transition-all duration-200 ease-in"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="modelValue"
        class="modal-backdrop"
        @click.self="handleBackdropClick"
        @keydown.esc="handleEscape"
        role="dialog"
        :aria-modal="true"
        :aria-labelledby="titleId"
        :aria-describedby="descriptionId"
      >
        <Transition
          enter-active-class="transition-all duration-300 ease-out"
          leave-active-class="transition-all duration-200 ease-in"
          enter-from-class="opacity-0 scale-95 translate-y-4"
          enter-to-class="opacity-100 scale-100 translate-y-0"
          leave-from-class="opacity-100 scale-100 translate-y-0"
          leave-to-class="opacity-0 scale-95 translate-y-4"
        >
          <div
            v-if="modelValue"
            ref="modalRef"
            :class="['modal', modalClass]"
            :style="modalStyle"
            role="document"
          >
            <!-- Header -->
            <div v-if="showHeader" :class="['modal-header', headerClass]">
              <div class="flex items-center gap-3">
                <div v-if="icon" class="modal-icon">
                  <i class="material-icons">{{ icon }}</i>
                </div>
                <div>
                  <h2 v-if="title" :id="titleId" class="modal-title">{{ title }}</h2>
                  <p v-if="subtitle" class="modal-subtitle">{{ subtitle }}</p>
                </div>
              </div>
              <button
                v-if="closable"
                @click="handleClose"
                @keydown.enter="handleClose"
                @keydown.space.prevent="handleClose"
                class="modal-close"
                :aria-label="closeLabel || 'Đóng'"
                type="button"
              >
                <i class="material-icons" aria-hidden="true">close</i>
              </button>
            </div>

            <!-- Body -->
            <div :class="['modal-body', bodyClass]">
              <slot>
                <p v-if="description" :id="descriptionId" class="modal-description">{{ description }}</p>
              </slot>
            </div>

            <!-- Footer -->
            <div v-if="showFooter" :class="['modal-footer', footerClass]">
              <slot name="footer">
                <button
                  v-if="showCancel"
                  @click="handleCancel"
                  @keydown.enter="handleCancel"
                  @keydown.space.prevent="handleCancel"
                  class="modal-btn-secondary"
                  :aria-label="cancelLabel || 'Hủy'"
                  type="button"
                >
                  {{ cancelLabel || 'Hủy' }}
                </button>
                <button
                  v-if="showConfirm"
                  @click="handleConfirm"
                  @keydown.enter="handleConfirm"
                  @keydown.space.prevent="handleConfirm"
                  class="modal-btn-primary"
                  :aria-label="confirmLabel || 'Xác nhận'"
                  :disabled="confirmDisabled"
                  type="button"
                >
                  {{ confirmLabel || 'Xác nhận' }}
                </button>
              </slot>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useFocusManagement } from '@/composables/useFocusManagement'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  subtitle: {
    type: String,
    default: ''
  },
  description: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  closable: {
    type: Boolean,
    default: true
  },
  closeOnBackdrop: {
    type: Boolean,
    default: true
  },
  closeOnEscape: {
    type: Boolean,
    default: true
  },
  showHeader: {
    type: Boolean,
    default: true
  },
  showFooter: {
    type: Boolean,
    default: true
  },
  showCancel: {
    type: Boolean,
    default: true
  },
  showConfirm: {
    type: Boolean,
    default: true
  },
  cancelLabel: {
    type: String,
    default: 'Hủy'
  },
  confirmLabel: {
    type: String,
    default: 'Xác nhận'
  },
  closeLabel: {
    type: String,
    default: 'Đóng'
  },
  confirmDisabled: {
    type: Boolean,
    default: false
  },
  modalClass: {
    type: String,
    default: ''
  },
  headerClass: {
    type: String,
    default: ''
  },
  bodyClass: {
    type: String,
    default: ''
  },
  footerClass: {
    type: String,
    default: ''
  },
  modalStyle: {
    type: Object,
    default: () => ({})
  },
  size: {
    type: String,
    default: 'md', // 'sm', 'md', 'lg', 'xl', 'full'
    validator: (value) => ['sm', 'md', 'lg', 'xl', 'full'].includes(value)
  }
})

const emit = defineEmits(['update:modelValue', 'close', 'cancel', 'confirm'])

const modalRef = ref(null)
const { trapFocus, restoreFocus, setupModal } = useFocusManagement()

const titleId = computed(() => props.title ? 'modal-title' : undefined)
const descriptionId = computed(() => props.description ? 'modal-description' : undefined)

let cleanupFocus = null

const handleClose = () => {
  emit('update:modelValue', false)
  emit('close')
  if (cleanupFocus) {
    cleanupFocus()
    cleanupFocus = null
  }
}

const handleCancel = () => {
  emit('cancel')
  handleClose()
}

const handleConfirm = () => {
  emit('confirm')
}

const handleBackdropClick = () => {
  if (props.closeOnBackdrop) {
    handleClose()
  }
}

const handleEscape = (event) => {
  if (props.closeOnEscape && event.key === 'Escape') {
    handleClose()
  }
}

watch(() => props.modelValue, async (isOpen) => {
  if (isOpen) {
    await nextTick()
    if (modalRef.value) {
      cleanupFocus = trapFocus(modalRef.value)
      setupModal(modalRef.value)
    }
    // Prevent body scroll
    document.body.style.overflow = 'hidden'
  } else {
    if (cleanupFocus) {
      cleanupFocus()
      cleanupFocus = null
    }
    restoreFocus()
    // Restore body scroll
    document.body.style.overflow = ''
  }
})

onUnmounted(() => {
  if (cleanupFocus) {
    cleanupFocus()
  }
  document.body.style.overflow = ''
})
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  z-index: var(--z-modal-backdrop);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-md);
}

.modal {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-2xl);
  max-width: 32rem;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  z-index: var(--z-modal);
}

.dark .modal {
  background: var(--bg-primary-dark);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-xl);
  border-bottom: var(--border-width-thin) solid var(--border-color-light);
  background: var(--gradient-purple-light);
}

.dark .modal-header {
  background: var(--gradient-purple-dark);
  border-color: var(--border-color-light-dark);
}

.modal-icon {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: var(--radius-lg);
  background: var(--gradient-purple);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: var(--shadow-purple);
}

.modal-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--text-primary);
  margin: 0;
}

.dark .modal-title {
  color: var(--text-primary-dark);
}

.modal-subtitle {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin-top: var(--spacing-xs);
  margin: 0;
}

.dark .modal-subtitle {
  color: var(--text-secondary-dark);
}

.modal-close {
  width: 2rem;
  height: 2rem;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.modal-close:hover {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

.dark .modal-close:hover {
  background: var(--bg-secondary-dark);
  color: var(--text-primary-dark);
}

.modal-close:focus-visible {
  outline: 3px solid var(--color-purple-400);
  outline-offset: 2px;
}

.modal-body {
  padding: var(--spacing-xl);
  overflow-y: auto;
  flex: 1;
}

.modal-description {
  color: var(--text-secondary);
  line-height: var(--leading-relaxed);
}

.dark .modal-description {
  color: var(--text-secondary-dark);
}

.modal-footer {
  padding: var(--spacing-xl);
  border-top: var(--border-width-thin) solid var(--border-color-light);
  background: var(--bg-secondary);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}

.dark .modal-footer {
  background: var(--bg-secondary-dark);
  border-color: var(--border-color-light-dark);
}

.modal-btn-primary {
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--gradient-purple);
  color: white;
  border: none;
  border-radius: var(--radius-xl);
  font-weight: var(--font-semibold);
  font-size: var(--text-base);
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-purple);
}

.modal-btn-primary:hover:not(:disabled) {
  box-shadow: var(--shadow-purple-lg);
  transform: scale(1.02);
  filter: brightness(1.05);
}

.modal-btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.modal-btn-primary:focus-visible {
  outline: 3px solid var(--color-purple-400);
  outline-offset: 2px;
}

.modal-btn-secondary {
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-primary);
  color: var(--text-primary);
  border: var(--border-width-thin) solid var(--border-color-medium);
  border-radius: var(--radius-xl);
  font-weight: var(--font-semibold);
  font-size: var(--text-base);
  cursor: pointer;
  transition: all var(--transition-base);
}

.dark .modal-btn-secondary {
  background: var(--bg-primary-dark);
  color: var(--text-primary-dark);
  border-color: var(--border-color-medium-dark);
}

.modal-btn-secondary:hover {
  background: var(--bg-secondary);
  border-color: var(--color-purple-300);
}

.dark .modal-btn-secondary:hover {
  background: var(--bg-secondary-dark);
  border-color: var(--color-purple-600);
}

.modal-btn-secondary:focus-visible {
  outline: 3px solid var(--color-purple-400);
  outline-offset: 2px;
}

/* Size variants */
.modal[data-size="sm"] {
  max-width: 24rem;
}

.modal[data-size="lg"] {
  max-width: 42rem;
}

.modal[data-size="xl"] {
  max-width: 56rem;
}

.modal[data-size="full"] {
  max-width: 90vw;
  max-height: 90vh;
}

@media (max-width: 640px) {
  .modal {
    max-width: 100%;
    margin: var(--spacing-md);
    max-height: calc(100vh - 2rem);
  }
}

@media (prefers-reduced-motion: reduce) {
  .modal,
  .modal-backdrop {
    transition: none;
  }
}
</style>

