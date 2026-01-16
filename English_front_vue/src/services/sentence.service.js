import api from './api'

export const sentenceService = {
  // 获取句子列表
  async getSentenceList(params) {
    return api.post('/api/english/sentence', {
      userId: params.userId
    })
  },
  
  // 标记句子为已掌握
  async markAsGrasped(sentenceId) {
    return api.post(`/api/english/sentenceIsGrasp/${sentenceId}`)
  },
  
  // 标记句子为错句
  async markAsError(sentenceId) {
    return api.post(`/api/english/sentenceNotGrasp/${sentenceId}`)
  },
  
  // 获取错句列表
  async getErrorSentences(userId) {

    return api.get(`/api/english/errorSentence/${userId}`)
  },

  // 根据ID列表获取句子学习记录
  async getRecordsByIds(payload) {
    return api.post('/api/sentence-study/records/batch', payload)
  },

  // 标记句子复习轮次完成
  async markReviewComplete(payload) {
    return api.post('/api/sentence-study/review/complete', payload)
  },

  // 获取最新完成的句子学习记录
  async getLatestRecord(userId, bookId) {
    const config = {}
    if (bookId) {
      config.params = { bookId }
    }
    return api.get(`/api/sentence-study/latest-record/${userId}`, config)
  }
}