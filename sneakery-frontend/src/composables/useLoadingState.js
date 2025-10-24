/**
 * Loading State Composable
 * Quản lý loading states cho admin components
 */
import { ref, computed } from 'vue'

export function useLoadingState() {
  const loading = ref(false)
  const loadingMessage = ref('Đang tải...')
  const skeletonCount = ref(5)

  const isLoading = computed(() => loading.value)
  const message = computed(() => loadingMessage.value)

  const setLoading = (isLoading, message = 'Đang tải...') => {
    loading.value = isLoading
    loadingMessage.value = message
  }

  const startLoading = (message = 'Đang tải...') => {
    setLoading(true, message)
  }

  const stopLoading = () => {
    setLoading(false)
  }

  const withLoading = async (asyncFn, message = 'Đang tải...') => {
    try {
      startLoading(message)
      const result = await asyncFn()
      return result
    } finally {
      stopLoading()
    }
  }

  return {
    loading,
    loadingMessage,
    skeletonCount,
    isLoading,
    message,
    setLoading,
    startLoading,
    stopLoading,
    withLoading
  }
}

/**
 * Table Loading State Composable
 * Quản lý loading states cho tables
 */
export function useTableLoading() {
  const { loading, loadingMessage, setLoading, startLoading, stopLoading, withLoading } = useLoadingState()
  
  const tableLoading = ref(false)
  const paginationLoading = ref(false)
  const actionLoading = ref(new Set())

  const isTableLoading = computed(() => tableLoading.value || loading.value)
  const isPaginationLoading = computed(() => paginationLoading.value)
  const isLoading = computed(() => loading.value)
  const message = computed(() => loadingMessage.value)
  
  const isActionLoading = (actionId) => {
    return actionLoading.value.has(actionId)
  }

  const setTableLoading = (isLoading, message = 'Đang tải dữ liệu...') => {
    tableLoading.value = isLoading
    if (isLoading) {
      setLoading(true, message)
    } else {
      setLoading(false)
    }
  }

  const setPaginationLoading = (isLoading) => {
    paginationLoading.value = isLoading
  }

  const setActionLoading = (actionId, isLoading) => {
    if (isLoading) {
      actionLoading.value.add(actionId)
    } else {
      actionLoading.value.delete(actionId)
    }
  }

  const withTableLoading = async (asyncFn, message = 'Đang tải dữ liệu...') => {
    try {
      setTableLoading(true, message)
      const result = await asyncFn()
      return result
    } finally {
      setTableLoading(false)
    }
  }

  const withPaginationLoading = async (asyncFn) => {
    try {
      setPaginationLoading(true)
      const result = await asyncFn()
      return result
    } finally {
      setPaginationLoading(false)
    }
  }

  const withActionLoading = async (actionId, asyncFn) => {
    try {
      setActionLoading(actionId, true)
      const result = await asyncFn()
      return result
    } finally {
      setActionLoading(actionId, false)
    }
  }

  return {
    // Loading states
    loading,
    loadingMessage,
    tableLoading,
    paginationLoading,
    actionLoading,
    
    // Computed
    isLoading,
    message,
    isTableLoading,
    isPaginationLoading,
    isActionLoading,
    
    // Methods
    setLoading,
    setTableLoading,
    setPaginationLoading,
    setActionLoading,
    startLoading,
    stopLoading,
    withLoading,
    withTableLoading,
    withPaginationLoading,
    withActionLoading
  }
}

/**
 * Form Loading State Composable
 * Quản lý loading states cho forms
 */
export function useFormLoading() {
  const { loading, loadingMessage, setLoading, startLoading, stopLoading, withLoading } = useLoadingState()
  
  const submitting = ref(false)
  const validating = ref(false)
  const fieldLoading = ref(new Set())

  const isSubmitting = computed(() => submitting.value)
  const isValidating = computed(() => validating.value)
  
  const isFieldLoading = (fieldName) => {
    return fieldLoading.value.has(fieldName)
  }

  const setSubmitting = (isSubmitting, message = 'Đang xử lý...') => {
    submitting.value = isSubmitting
    if (isSubmitting) {
      setLoading(true, message)
    } else {
      setLoading(false)
    }
  }

  const setValidating = (isValidating) => {
    validating.value = isValidating
  }

  const setFieldLoading = (fieldName, isLoading) => {
    if (isLoading) {
      fieldLoading.value.add(fieldName)
    } else {
      fieldLoading.value.delete(fieldName)
    }
  }

  const withSubmitting = async (asyncFn, message = 'Đang xử lý...') => {
    try {
      setSubmitting(true, message)
      const result = await asyncFn()
      return result
    } finally {
      setSubmitting(false)
    }
  }

  const withValidating = async (asyncFn) => {
    try {
      setValidating(true)
      const result = await asyncFn()
      return result
    } finally {
      setValidating(false)
    }
  }

  const withFieldLoading = async (fieldName, asyncFn) => {
    try {
      setFieldLoading(fieldName, true)
      const result = await asyncFn()
      return result
    } finally {
      setFieldLoading(fieldName, false)
    }
  }

  return {
    // Loading states
    loading,
    loadingMessage,
    submitting,
    validating,
    fieldLoading,
    
    // Computed
    isLoading,
    message,
    isSubmitting,
    isValidating,
    isFieldLoading,
    
    // Methods
    setLoading,
    setSubmitting,
    setValidating,
    setFieldLoading,
    startLoading,
    stopLoading,
    withLoading,
    withSubmitting,
    withValidating,
    withFieldLoading
  }
}
