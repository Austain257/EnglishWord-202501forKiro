import { ref, reactive } from 'vue'

const toasts = ref([])
let toastId = 0

export function useToast() {
  const addToast = (message, type = 'info', duration = 3000) => {
    const id = ++toastId
    const toast = {
      id,
      message,
      type,
      duration,
      visible: true
    }
    
    toasts.value.push(toast)
    
    if (duration > 0) {
      setTimeout(() => {
        removeToast(id)
      }, duration)
    }
    
    return id
  }
  
  const removeToast = (id) => {
    const index = toasts.value.findIndex(toast => toast.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }
  
  const success = (message, duration = 3000) => {
    return addToast(message, 'success', duration)
  }
  
  const error = (message, duration = 5000) => {
    return addToast(message, 'error', duration)
  }
  
  const warning = (message, duration = 4000) => {
    return addToast(message, 'warning', duration)
  }
  
  const info = (message, duration = 3000) => {
    return addToast(message, 'info', duration)
  }
  
  const clear = () => {
    toasts.value = []
  }
  
  // 通用的showToast函数
  const showToast = (message, type = 'info', duration = 3000) => {
    return addToast(message, type, duration)
  }
  
  return {
    toasts,
    showToast,
    success,
    error,
    warning,
    info,
    removeToast,
    clear
  }
}