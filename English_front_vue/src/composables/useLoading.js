import { ref } from 'vue'

export function useLoading(initialState = false) {
  const loading = ref(initialState)
  
  const setLoading = (state) => {
    loading.value = state
  }
  
  const startLoading = () => {
    loading.value = true
  }
  
  const stopLoading = () => {
    loading.value = false
  }
  
  const withLoading = async (asyncFn) => {
    try {
      startLoading()
      const result = await asyncFn()
      return result
    } finally {
      stopLoading()
    }
  }
  
  return {
    loading,
    setLoading,
    startLoading,
    stopLoading,
    withLoading
  }
}