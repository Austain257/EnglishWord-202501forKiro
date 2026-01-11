import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import router from '@/router'

// 创建 Axios 实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://192.168.43.106:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
      config.headers['X-Token'] = authStore.token
    }
    if (authStore.user?.id) {
      config.headers['X-User-Id'] = authStore.user.id
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    // 后端返回格式: { code: 1, msg: null, data: {} }
    const { code, msg, data } = response.data
    
    if (code === 1) {
      // 成功
      return { success: true, data, message: msg }
    } else {
      // 业务错误
      const toast = useToast()
      toast.error(msg || '操作失败')
      return Promise.reject(new Error(msg || '操作失败'))
    }
  },
  async (error) => {
    const toast = useToast()
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          // 未授权，清除令牌并跳转登录
          const authStore = useAuthStore()
          authStore.logout()
          router.push('/login')
          toast.error('登录已过期，请重新登录')
          break
        case 403:
          toast.error('没有权限访问该资源')
          break
        case 404:
          toast.error('请求的资源不存在')
          break
        case 500:
          toast.error('服务器错误，请稍后重试')
          break
        default:
          toast.error(data?.msg || error.message || '请求失败')
      }
    } else if (error.request) {
      toast.error('网络连接失败，请检查网络')
    } else {
      toast.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export { api }
export default api