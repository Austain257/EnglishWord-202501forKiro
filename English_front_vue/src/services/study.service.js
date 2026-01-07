import api from './api'
import { useAuthStore } from '@/stores/auth'

export const studyService = {
  /**
   * 开始学习会话
   * @param {string} studyScene - 学习场景
   * @param {number} bookId - 课本ID
   * @returns {Promise<{sessionId: string, serverTime: string}>}
   */
  async startSession(studyScene, bookId) {
    const authStore = useAuthStore()
    const userId = authStore.user?.id
    
    if (!userId) {
      throw new Error('用户未登录')
    }
    
    const response = await api.post('/api/study/session/start', {
      studyScene
    }, {
      params: { userId, bookId }
    })
    return response.data
  },

  /**
   * 发送心跳维持会话
   * @param {string} sessionId - 会话ID
   * @returns {Promise<void>}
   */
  async heartbeat(sessionId) {
    const authStore = useAuthStore()
    const userId = authStore.user?.id
    
    if (!userId) {
      throw new Error('用户未登录')
    }
    
    const response = await api.post('/api/study/session/heartbeat', {
      sessionId
    }, {
      params: { userId }
    })
    return response.data
  },

  /**
   * 结束学习会话
   * @param {string} sessionId - 会话ID
   * @param {number} clientDurationSec - 客户端计算的持续时间（秒）
   * @param {string} source - 结束来源
   * @returns {Promise<{dayTotalSec: number, totalSec: number, dayTotalDisplay: string, totalDisplay: string}>}
   */
  async finishSession(sessionId, clientDurationSec, source = 'normal') {
    const authStore = useAuthStore()
    const userId = authStore.user?.id
    
    if (!userId) {
      throw new Error('用户未登录')
    }
    
    const response = await api.post('/api/study/session/finish', {
      sessionId,
      clientDurationSec,
      source
    }, {
      params: { userId }
    })
    return response.data
  },

  /**
   * 暂停学习会话（页面后台时）
   * @param {string} sessionId - 会话ID
   * @returns {Promise<void>}
   */
  async pauseSession(sessionId) {
    const authStore = useAuthStore()
    const userId = authStore.user?.id
    
    if (!userId) {
      throw new Error('用户未登录')
    }
    
    const response = await api.post('/api/study/session/pause', {
      sessionId
    }, {
      params: { userId }
    })
    return response.data
  },

  /**
   * 恢复学习会话（页面前台时）
   * @param {string} sessionId - 会话ID
   * @returns {Promise<void>}
   */
  async resumeSession(sessionId) {
    const authStore = useAuthStore()
    const userId = authStore.user?.id
    
    if (!userId) {
      throw new Error('用户未登录')
    }
    
    const response = await api.post('/api/study/session/resume', {
      sessionId
    }, {
      params: { userId }
    })
    return response.data
  },

  /**
   * 获取今日学习统计
   * @returns {Promise<{dayTotalSec: number, totalSec: number, dayTotalDisplay: string, totalDisplay: string}>}
   */
  async getTodayStats() {
    const authStore = useAuthStore()
    const userId = authStore.user?.id
    
    if (!userId) {
      throw new Error('用户未登录')
    }
    
    const response = await api.get('/api/study/stat/today', {
      params: { userId }
    })
    return response.data
  }
}
