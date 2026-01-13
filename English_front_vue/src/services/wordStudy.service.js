// 文件路径: src/services/wordStudy.service.js
import { api } from './api'

export const wordStudyService = {
  // 开始学习会话
  startSession(request) {
    return api.post('/api/word-study/start', request)
  },
  
  // 结束学习会话
  endSession(sessionId, request) {
    return api.post(`/api/word-study/end/${sessionId}`, request)
  },
  
  // 检查复习状态
  checkReviewStatus(userId) {
    return api.get(`/api/word-study/review-status/${userId}`)
  },
  
  // 标记复习完成
  markReviewComplete(request) {
    return api.post('/api/word-study/review/complete', request)
  },
  
  // 批量获取学习记录
  getRecordsByIds(request) {
    return api.post('/api/word-study/records/batch', request)
  },
  
  // 获取今日学习记录
  getTodayRecords(userId) {
    return api.get(`/api/word-study/today-records/${userId}`)
  },
  
  // 检查锁定状态
  checkLockStatus(userId) {
    return api.get(`/api/study-lock/check/${userId}`)
  },
  
  // 获取待完成复习任务
  getPendingReviews(userId) {
    return api.get(`/api/study-lock/pending-reviews/${userId}`)
  },

  // 获取用户最新完成的学习记录
  getLatestRecord(userId, bookId) {
    const config = {}
    if (bookId) {
      config.params = { bookId }
    }
    return api.get(`/api/word-study/latest-record/${userId}`, config)
  }
}
