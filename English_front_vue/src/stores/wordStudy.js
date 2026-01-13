// 文件路径: src/stores/wordStudy.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { wordStudyService } from '@/services/wordStudy.service'

export const useWordStudyStore = defineStore('wordStudy', () => {
  // 状态
  const currentSession = ref(null)
  const lockStatus = ref({})
  const pendingReviews = ref([])
  const loading = ref(false)
  
  // 计算属性
  const isLocked = computed(() => lockStatus.value.isLocked)
  const hasActiveSession = computed(() => currentSession.value && currentSession.value.status === 1)
  
  // 动作
  const startStudySession = async (request) => {
    try {
      loading.value = true
      const response = await wordStudyService.startSession(request)
      currentSession.value = response.data
      return response.data
    } catch (error) {
      console.error('开始学习会话失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  const endStudySession = async (sessionId, request) => {
    try {
      loading.value = true
      const response = await wordStudyService.endSession(sessionId, request)
      currentSession.value = null
      // 结束后检查锁定状态
      if (request.userId) {
        await checkLockStatus(request.userId)
      }
      return response.data
    } catch (error) {
      console.error('结束学习会话失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  const checkLockStatus = async (userId) => {
    try {
      const response = await wordStudyService.checkLockStatus(userId)
      lockStatus.value = response.data
      pendingReviews.value = response.data.pendingReviews || []
      return response.data
    } catch (error) {
      console.error('检查锁定状态失败:', error)
      throw error
    }
  }
  
  const markReviewComplete = async (request) => {
    try {
      loading.value = true
      const response = await wordStudyService.markReviewComplete(request)
      // 标记完成后重新检查锁定状态
      await checkLockStatus(request.userId)
      window.dispatchEvent(new CustomEvent('study-lock-updated'))
      return response.data
    } catch (error) {
      console.error('标记复习完成失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  const getTodayRecords = async (userId) => {
    try {
      const response = await wordStudyService.getTodayRecords(userId)
      return response.data
    } catch (error) {
      console.error('获取今日学习记录失败:', error)
      throw error
    }
  }

  const getLatestRecord = async (userId, bookId) => {
    try {
      const response = await wordStudyService.getLatestRecord(userId, bookId)
      return response.data
    } catch (error) {
      console.error('获取最新学习记录失败:', error)
      throw error
    }
  }

  const getRecordsByIds = async ({ userId, recordIds }) => {
    try {
      const response = await wordStudyService.getRecordsByIds({
        userId,
        recordIds
      })
      return response.data
    } catch (error) {
      console.error('根据ID获取学习记录失败:', error)
      throw error
    }
  }
  
  const resetState = () => {
    currentSession.value = null
    lockStatus.value = {}
    pendingReviews.value = []
  }
  
  return {
    // 状态
    currentSession,
    lockStatus,
    pendingReviews,
    loading,
    
    // 计算属性
    isLocked,
    hasActiveSession,
    
    // 动作
    startStudySession,
    endStudySession,
    checkLockStatus,
    markReviewComplete,
    getTodayRecords,
    getLatestRecord,
    getRecordsByIds,
    resetState
  }
})
