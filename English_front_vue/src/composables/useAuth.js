import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

export function useAuth() {
  const authStore = useAuthStore()
  const router = useRouter()
  
  const isAuthenticated = computed(() => authStore.isAuthenticated)
  const user = computed(() => authStore.user)
  const token = computed(() => authStore.token)
  
  const login = async (credentials) => {
    try {
      await authStore.login(credentials)
      router.push('/')
      return { success: true }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }
  
  const register = async (userData) => {
    try {
      await authStore.register(userData)
      router.push('/login')
      return { success: true }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }
  
  const logout = () => {
    authStore.logout()
    router.push('/login')
  }
  
  const refreshToken = async () => {
    try {
      await authStore.refreshToken()
      return { success: true }
    } catch (error) {
      logout()
      return { success: false, error: error.message }
    }
  }
  
  return {
    isAuthenticated,
    user,
    token,
    login,
    register,
    logout,
    refreshToken
  }
}