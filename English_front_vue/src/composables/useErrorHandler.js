import { ref } from 'vue'
import { useToast } from './useToast'

// 全局错误处理组合式函数
export function useErrorHandler() {
  const { showToast } = useToast()
  const isLoading = ref(false)
  const error = ref(null)

  // 包装异步操作，自动处理错误
  const withErrorHandling = async (asyncFn, options = {}) => {
    const {
      loadingMessage = '处理中...',
      successMessage = null,
      errorMessage = null,
      showLoading = true,
      showSuccess = false,
      showError = true
    } = options

    try {
      if (showLoading) {
        isLoading.value = true
      }
      
      error.value = null
      const result = await asyncFn()
      
      if (showSuccess && successMessage) {
        showToast(successMessage, 'success')
      }
      
      return result
    } catch (err) {
      error.value = err
      
      if (showError) {
        const message = errorMessage || err.message || '操作失败'
        showToast(message, 'error')
      }
      
      throw err
    } finally {
      if (showLoading) {
        isLoading.value = false
      }
    }
  }

  // 重试机制
  const withRetry = async (asyncFn, maxRetries = 3, delay = 1000) => {
    let lastError
    
    for (let i = 0; i < maxRetries; i++) {
      try {
        return await asyncFn()
      } catch (err) {
        lastError = err
        
        if (i < maxRetries - 1) {
          // 等待后重试
          await new Promise(resolve => setTimeout(resolve, delay * (i + 1)))
        }
      }
    }
    
    throw lastError
  }

  // 清除错误状态
  const clearError = () => {
    error.value = null
  }

  return {
    isLoading,
    error,
    withErrorHandling,
    withRetry,
    clearError
  }
}