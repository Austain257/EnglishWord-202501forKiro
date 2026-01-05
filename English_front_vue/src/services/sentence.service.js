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
    return api.post(`/api/english/isGrasp/${sentenceId}`)
  },
  
  // 标记句子为错句
  async markAsError(sentenceId) {
    return api.post(`/api/english/notGrasp/${sentenceId}`)
  },
  
  // 获取错句列表
  async getErrorSentences(userId) {
    return api.get(`/api/english/errorSentence/${userId}`)
  }
}