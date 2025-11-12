/**
 * Integration Tests for ToastContainer
 * 
 * To run these tests, install dependencies:
 * npm install -D vitest @vue/test-utils jsdom @testing-library/vue
 * 
 * Add to package.json scripts:
 * "test": "vitest",
 * "test:coverage": "vitest --coverage"
 */

import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ToastContainer from '../ToastContainer.vue'
import toastService from '@/utils/toastService'

describe('ToastContainer', () => {
  beforeEach(() => {
    // Clear all toasts before each test
    toastService.clear()
  })

  describe('Rendering', () => {
    it('should render toast container', () => {
      const wrapper = mount(ToastContainer)
      
      expect(wrapper.find('.toast-container').exists()).toBe(true)
    })

    it('should render toasts from toastService', async () => {
      const wrapper = mount(ToastContainer)
      
      toastService.success('Test Title', 'Test Message')
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.toast-item').exists()).toBe(true)
      expect(wrapper.text()).toContain('Test Title')
      expect(wrapper.text()).toContain('Test Message')
    })

    it('should render different toast types', async () => {
      const wrapper = mount(ToastContainer)
      
      toastService.success('Success', 'Success message')
      toastService.error('Error', 'Error message')
      toastService.warning('Warning', 'Warning message')
      toastService.info('Info', 'Info message')
      
      await wrapper.vm.$nextTick()
      
      const toasts = wrapper.findAll('.toast-item')
      expect(toasts.length).toBe(4)
      expect(toasts[0].classes()).toContain('toast-success')
      expect(toasts[1].classes()).toContain('toast-error')
      expect(toasts[2].classes()).toContain('toast-warning')
      expect(toasts[3].classes()).toContain('toast-info')
    })
  })

  describe('Progress Bar', () => {
    it('should show progress bar when duration > 0', async () => {
      const wrapper = mount(ToastContainer)
      
      toastService.success('Test', 'Message', { duration: 5000 })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.toast-progress-bar').exists()).toBe(true)
    })

    it('should not show progress bar when duration = 0', async () => {
      const wrapper = mount(ToastContainer)
      
      toastService.success('Test', 'Message', { duration: 0 })
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.toast-progress-bar').exists()).toBe(false)
    })
  })

  describe('Action Buttons', () => {
    it('should render action buttons', async () => {
      const wrapper = mount(ToastContainer)
      
      const actionCallback = vi.fn()
      toastService.success('Test', 'Message', {
        actions: [
          { label: 'Undo', onClick: actionCallback }
        ]
      })
      
      await wrapper.vm.$nextTick()
      
      const actionBtn = wrapper.find('.toast-action-btn')
      expect(actionBtn.exists()).toBe(true)
      expect(actionBtn.text()).toContain('Undo')
    })

    it('should limit actions to 2', async () => {
      const wrapper = mount(ToastContainer)
      
      toastService.success('Test', 'Message', {
        actions: [
          { label: 'Action 1', onClick: () => {} },
          { label: 'Action 2', onClick: () => {} },
          { label: 'Action 3', onClick: () => {} }
        ]
      })
      
      await wrapper.vm.$nextTick()
      
      const actionBtns = wrapper.findAll('.toast-action-btn')
      expect(actionBtns.length).toBe(2)
    })

    it('should call action callback when clicked', async () => {
      const wrapper = mount(ToastContainer)
      
      const actionCallback = vi.fn()
      toastService.success('Test', 'Message', {
        actions: [
          { label: 'Action', onClick: actionCallback }
        ]
      })
      
      await wrapper.vm.$nextTick()
      
      const actionBtn = wrapper.find('.toast-action-btn')
      await actionBtn.trigger('click')
      
      expect(actionCallback).toHaveBeenCalled()
    })

    it('should show loading state for async actions', async () => {
      const wrapper = mount(ToastContainer)
      
      const asyncAction = () => Promise.resolve()
      toastService.success('Test', 'Message', {
        actions: [
          { label: 'Action', onClick: asyncAction }
        ]
      })
      
      await wrapper.vm.$nextTick()
      
      const actionBtn = wrapper.find('.toast-action-btn')
      await actionBtn.trigger('click')
      
      await wrapper.vm.$nextTick()
      
      // Check for loading spinner
      expect(wrapper.find('.toast-action-spinner').exists()).toBe(true)
    })
  })

  describe('Pause/Resume', () => {
    it('should pause progress on mouseenter', async () => {
      const wrapper = mount(ToastContainer)
      
      const id = toastService.success('Test', 'Message', { duration: 5000 })
      
      await wrapper.vm.$nextTick()
      
      const toastItem = wrapper.find('.toast-item')
      await toastItem.trigger('mouseenter')
      
      await wrapper.vm.$nextTick()
      
      const toast = toastService.toasts.find(t => t.id === id)
      expect(toast.isPaused).toBe(true)
    })

    it('should resume progress on mouseleave', async () => {
      const wrapper = mount(ToastContainer)
      
      const id = toastService.success('Test', 'Message', { duration: 5000 })
      
      await wrapper.vm.$nextTick()
      
      const toastItem = wrapper.find('.toast-item')
      await toastItem.trigger('mouseenter')
      await toastItem.trigger('mouseleave')
      
      await wrapper.vm.$nextTick()
      
      const toast = toastService.toasts.find(t => t.id === id)
      expect(toast.isPaused).toBe(false)
    })
  })

  describe('Remove Toast', () => {
    it('should remove toast when close button is clicked', async () => {
      const wrapper = mount(ToastContainer)
      
      const id = toastService.success('Test', 'Message')
      
      await wrapper.vm.$nextTick()
      
      const closeBtn = wrapper.find('.toast-close')
      await closeBtn.trigger('click')
      
      await wrapper.vm.$nextTick()
      
      expect(toastService.toasts.find(t => t.id === id)).toBeUndefined()
    })

    it('should remove toast when toast is clicked (if closable)', async () => {
      const wrapper = mount(ToastContainer)
      
      const id = toastService.success('Test', 'Message', { closable: true })
      
      await wrapper.vm.$nextTick()
      
      const toastItem = wrapper.find('.toast-item')
      await toastItem.trigger('click')
      
      await wrapper.vm.$nextTick()
      
      expect(toastService.toasts.find(t => t.id === id)).toBeUndefined()
    })
  })

  describe('Mobile Responsiveness', () => {
    it('should detect mobile viewport', async () => {
      // Mock window.innerWidth
      Object.defineProperty(window, 'innerWidth', {
        writable: true,
        configurable: true,
        value: 500
      })
      
      const wrapper = mount(ToastContainer)
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.isMobile).toBe(true)
    })

    it('should limit displayed toasts on mobile', async () => {
      Object.defineProperty(window, 'innerWidth', {
        writable: true,
        configurable: true,
        value: 500
      })
      
      const wrapper = mount(ToastContainer)
      
      // Add 5 toasts
      for (let i = 0; i < 5; i++) {
        toastService.success(`Toast ${i}`, 'Message')
      }
      
      await wrapper.vm.$nextTick()
      
      // Should only show 3 on mobile
      expect(wrapper.vm.displayedToasts.length).toBe(3)
    })

    it('should show "Show All" button when toasts > maxMobileToasts', async () => {
      Object.defineProperty(window, 'innerWidth', {
        writable: true,
        configurable: true,
        value: 500
      })
      
      const wrapper = mount(ToastContainer)
      
      // Add 5 toasts
      for (let i = 0; i < 5; i++) {
        toastService.success(`Toast ${i}`, 'Message')
      }
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.find('.toast-show-all-btn').exists()).toBe(true)
    })
  })

  describe('Message Expansion', () => {
    it('should truncate long messages', async () => {
      const wrapper = mount(ToastContainer)
      
      const longMessage = 'a'.repeat(150)
      toastService.success('Test', longMessage)
      
      await wrapper.vm.$nextTick()
      
      const message = wrapper.find('.toast-message')
      expect(message.text()).toContain('...')
      expect(message.text()).toContain('Xem thêm')
    })

    it('should expand message when "Xem thêm" is clicked', async () => {
      const wrapper = mount(ToastContainer)
      
      const longMessage = 'a'.repeat(150)
      const id = toastService.success('Test', longMessage)
      
      await wrapper.vm.$nextTick()
      
      const expandBtn = wrapper.find('.toast-expand-btn')
      await expandBtn.trigger('click')
      
      await wrapper.vm.$nextTick()
      
      expect(wrapper.vm.expandedMessages[id]).toBe(true)
      expect(wrapper.text()).toContain('Thu gọn')
    })
  })
})

