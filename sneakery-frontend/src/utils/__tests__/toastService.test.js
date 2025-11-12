/**
 * Unit Tests for ToastService
 * 
 * To run these tests, install dependencies:
 * npm install -D vitest @vue/test-utils jsdom
 * 
 * Add to package.json scripts:
 * "test": "vitest",
 * "test:coverage": "vitest --coverage"
 */

import { describe, it, expect, beforeEach, vi } from 'vitest'
import toastService from '../toastService'

describe('ToastService', () => {
  beforeEach(() => {
    // Clear all toasts before each test
    toastService.clear()
  })

  describe('addToast', () => {
    it('should add a new toast with default values', () => {
      const id = toastService.addToast({})
      
      expect(id).toBeDefined()
      expect(typeof id).toBe('number')
    })

    it('should add a toast with custom properties', () => {
      const toast = {
        type: 'success',
        title: 'Test Title',
        message: 'Test Message',
        duration: 3000
      }
      
      const id = toastService.addToast(toast)
      
      let addedToast = null
      toastService.subscribe((toasts) => {
        addedToast = toasts.find(t => t.id === id)
      })
      
      expect(addedToast).toBeDefined()
      expect(addedToast.type).toBe('success')
      expect(addedToast.title).toBe('Test Title')
      expect(addedToast.message).toBe('Test Message')
      expect(addedToast.duration).toBe(3000)
      expect(addedToast.progress).toBe(100)
      expect(addedToast.isPaused).toBe(false)
    })

    it('should limit actions to max 2', () => {
      const toast = {
        actions: [
          { label: 'Action 1', onClick: () => {} },
          { label: 'Action 2', onClick: () => {} },
          { label: 'Action 3', onClick: () => {} }
        ]
      }
      
      const id = toastService.addToast(toast)
      
      let addedToast = null
      toastService.subscribe((toasts) => {
        addedToast = toasts.find(t => t.id === id)
      })
      
      expect(addedToast.actions.length).toBe(2)
    })

    it('should limit max toasts to 5', () => {
      // Add 7 toasts
      for (let i = 0; i < 7; i++) {
        toastService.addToast({ title: `Toast ${i}` })
      }
      
      let toasts = []
      toastService.subscribe((t) => {
        toasts = t
      })
      
      expect(toasts.length).toBe(5)
    })

    it('should start progress animation when duration > 0', (done) => {
      const id = toastService.addToast({ duration: 100 })
      
      setTimeout(() => {
        let toast = null
        toastService.subscribe((toasts) => {
          toast = toasts.find(t => t.id === id)
        })
        
        // Progress should have decreased
        expect(toast.progress).toBeLessThan(100)
        done()
      }, 50)
    })
  })

  describe('removeToast', () => {
    it('should remove a toast by id', () => {
      const id = toastService.addToast({ title: 'Test' })
      
      toastService.removeToast(id)
      
      let toasts = []
      toastService.subscribe((t) => {
        toasts = t
      })
      
      expect(toasts.find(t => t.id === id)).toBeUndefined()
    })

    it('should clear timeout when removing toast', () => {
      const id = toastService.addToast({ duration: 1000 })
      
      // Remove immediately
      toastService.removeToast(id)
      
      // Wait to ensure timeout was cleared
      setTimeout(() => {
        let toasts = []
        toastService.subscribe((t) => {
          toasts = t
        })
        
        expect(toasts.find(t => t.id === id)).toBeUndefined()
      }, 1100)
    })
  })

  describe('pauseToast and resumeToast', () => {
    it('should pause toast progress', (done) => {
      const id = toastService.addToast({ duration: 1000 })
      
      setTimeout(() => {
        toastService.pauseToast(id)
        
        let toast = null
        toastService.subscribe((t) => {
          toast = t.find(to => to.id === id)
        })
        
        expect(toast.isPaused).toBe(true)
        
        const progressBefore = toast.progress
        
        // Wait a bit - progress should not change
        setTimeout(() => {
          toastService.subscribe((t) => {
            toast = t.find(to => to.id === id)
          })
          
          expect(toast.progress).toBe(progressBefore)
          done()
        }, 100)
      }, 100)
    })

    it('should resume toast progress', (done) => {
      const id = toastService.addToast({ duration: 1000 })
      
      setTimeout(() => {
        toastService.pauseToast(id)
        
        const progressWhenPaused = toastService.toasts.find(t => t.id === id).progress
        
        setTimeout(() => {
          toastService.resumeToast(id)
          
          let toast = null
          toastService.subscribe((t) => {
            toast = t.find(to => to.id === id)
          })
          
          expect(toast.isPaused).toBe(false)
          
          // Progress should continue decreasing
          setTimeout(() => {
            toastService.subscribe((t) => {
              toast = t.find(to => to.id === id)
            })
            
            expect(toast.progress).toBeLessThan(progressWhenPaused)
            done()
          }, 50)
        }, 50)
      }, 100)
    })
  })

  describe('clear', () => {
    it('should remove all toasts', () => {
      toastService.addToast({ title: 'Toast 1' })
      toastService.addToast({ title: 'Toast 2' })
      toastService.addToast({ title: 'Toast 3' })
      
      toastService.clear()
      
      let toasts = []
      toastService.subscribe((t) => {
        toasts = t
      })
      
      expect(toasts.length).toBe(0)
    })
  })

  describe('success', () => {
    it('should create a success toast', () => {
      const id = toastService.success('Success Title', 'Success Message')
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.type).toBe('success')
      expect(toast.title).toBe('Success Title')
      expect(toast.message).toBe('Success Message')
    })
  })

  describe('error', () => {
    it('should create an error toast with longer duration', () => {
      const id = toastService.error('Error Title', 'Error Message')
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.type).toBe('error')
      expect(toast.title).toBe('Error Title')
      expect(toast.message).toBe('Error Message')
      expect(toast.duration).toBe(8000) // Error toasts stay longer
    })
  })

  describe('warning', () => {
    it('should create a warning toast', () => {
      const id = toastService.warning('Warning Title', 'Warning Message')
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.type).toBe('warning')
      expect(toast.title).toBe('Warning Title')
      expect(toast.message).toBe('Warning Message')
    })
  })

  describe('info', () => {
    it('should create an info toast', () => {
      const id = toastService.info('Info Title', 'Info Message')
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.type).toBe('info')
      expect(toast.title).toBe('Info Title')
      expect(toast.message).toBe('Info Message')
    })
  })

  describe('apiError', () => {
    it('should handle 400 Bad Request', () => {
      const error = {
        response: {
          status: 400,
          data: { message: 'Invalid data' }
        }
      }
      
      const id = toastService.apiError(error)
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.type).toBe('error')
      expect(toast.title).toBe('Dữ liệu không hợp lệ')
      expect(toast.message).toBe('Invalid data')
    })

    it('should handle 401 Unauthorized', () => {
      const error = {
        response: {
          status: 401
        }
      }
      
      const id = toastService.apiError(error)
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.title).toBe('Chưa đăng nhập')
      expect(toast.message).toBe('Vui lòng đăng nhập lại')
    })

    it('should handle 403 Forbidden', () => {
      const error = {
        response: {
          status: 403
        }
      }
      
      const id = toastService.apiError(error)
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.title).toBe('Không có quyền truy cập')
      expect(toast.message).toBe('Bạn không có quyền thực hiện hành động này')
    })

    it('should handle 404 Not Found', () => {
      const error = {
        response: {
          status: 404,
          data: { message: 'Resource not found' }
        }
      }
      
      const id = toastService.apiError(error)
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.title).toBe('Không tìm thấy')
      expect(toast.message).toBe('Resource not found')
    })

    it('should handle 500 Server Error', () => {
      const error = {
        response: {
          status: 500
        }
      }
      
      const id = toastService.apiError(error)
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.title).toBe('Lỗi máy chủ')
      expect(toast.message).toBe('Máy chủ đang gặp sự cố, vui lòng thử lại sau')
    })

    it('should handle network error', () => {
      const error = {
        request: {}
      }
      
      const id = toastService.apiError(error)
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.title).toBe('Lỗi kết nối')
      expect(toast.message).toBe('Không thể kết nối đến máy chủ')
    })

    it('should handle unknown error', () => {
      const error = {
        message: 'Unknown error'
      }
      
      const id = toastService.apiError(error, 'Default message')
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.title).toBe('Lỗi không xác định')
      expect(toast.message).toBe('Unknown error')
    })

    it('should use default message when no error message', () => {
      const error = {}
      
      const id = toastService.apiError(error, 'Custom default message')
      
      let toast = null
      toastService.subscribe((toasts) => {
        toast = toasts.find(t => t.id === id)
      })
      
      expect(toast.message).toBe('Custom default message')
    })
  })

  describe('subscribe', () => {
    it('should notify listeners when toasts change', () => {
      const listener = vi.fn()
      const unsubscribe = toastService.subscribe(listener)
      
      toastService.addToast({ title: 'Test' })
      
      expect(listener).toHaveBeenCalled()
      
      unsubscribe()
    })

    it('should allow unsubscribing', () => {
      const listener = vi.fn()
      const unsubscribe = toastService.subscribe(listener)
      
      unsubscribe()
      
      toastService.addToast({ title: 'Test' })
      
      // Listener should not be called after unsubscribe
      expect(listener).toHaveBeenCalledTimes(0)
    })
  })
})

