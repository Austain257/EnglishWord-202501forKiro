import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '@/services/auth.service'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)
  const currentBookId = ref(localStorage.getItem('currentBookId') || null)
  const lastLoginTime = ref(null)
  
  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  
  // 动作
  const login = async (credentials) => {
    try {
      const response = await authService.login(credentials)
      const { token: newToken, user: userData } = response.data
      
      // 保存认证信息
      token.value = newToken
      user.value = userData
      lastLoginTime.value = new Date().toISOString()
      
      // 持久化到本地存储
      localStorage.setItem('token', newToken)
      localStorage.setItem('user', JSON.stringify(userData))
      localStorage.setItem('lastLoginTime', lastLoginTime.value)
      
      return response
    } catch (error) {
      throw error
    }
  }
  
  const register = async (userData) => {
    try {
      const response = await authService.register(userData)
      return response
    } catch (error) {
      throw error
    }
  }
  
  const logout = () => {
    // 清除状态
    user.value = null
    token.value = null
    currentBookId.value = null
    lastLoginTime.value = null
    
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('currentBookId')
    localStorage.removeItem('lastLoginTime')
  }
  
  const refreshToken = async () => {
    try {
      if (!token.value) {
        throw new Error('没有可刷新的令牌')
      }
      
      const response = await authService.refreshToken(token.value)
      const { token: newToken } = response.data
      
      // 更新令牌
      token.value = newToken
      localStorage.setItem('token', newToken)
      
      return response
    } catch (error) {
      // 刷新失败，清除认证信息
      logout()
      throw error
    }
  }
  
  const validateToken = async () => {
    try {
      if (!token.value) {
        return false
      }
      
      const response = await authService.validateToken()
      return response.data.valid
    } catch (error) {
      logout()
      return false
    }
  }
  
  const setCurrentBook = (bookId) => {
    currentBookId.value = bookId
    if (bookId) {
      localStorage.setItem('currentBookId', bookId.toString())
    } else {
      localStorage.removeItem('currentBookId')
    }
  }

  // 更新用户信息（并持久化），用于头像等信息变更后即时刷新
  const updateUser = (partial) => {
    if (!user.value) {
      user.value = partial
    } else {
      user.value = { ...user.value, ...partial }
    }
    localStorage.setItem('user', JSON.stringify(user.value))
  }
  
  // 初始化时恢复用户信息
  const initializeAuth = () => {
    const savedUser = localStorage.getItem('user')
    const savedLastLoginTime = localStorage.getItem('lastLoginTime')
    
    if (savedUser) {
      try {
        user.value = JSON.parse(savedUser)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        logout()
      }
    }
    
    if (savedLastLoginTime) {
      lastLoginTime.value = savedLastLoginTime
    }
  }
  
  // 初始化
  initializeAuth()
  
  return {
    // 状态
    user,
    token,
    currentBookId,
    lastLoginTime,
    
    // 计算属性
    isAuthenticated,
    
    // 动作
    login,
    register,
    logout,
    refreshToken,
    validateToken,
    setCurrentBook,
    updateUser,
    initializeAuth
  }
})