import api from './api'

export const wordService = {
  // 获取单词列表
  async getWordList(params) {
    return api.post('/api/english/wordList', {
      userId: params.userId,
      bookId: params.bookId,
      start: params.start,
      end: params.end
    })
  },
  
  // 获取错词列表
  async getErrorWordList(params) {
    return api.post('/api/english/errorWordList', {
      userId: params.userId,
      bookId: params.bookId
    })
  },
  
  // 标记单词为已掌握
  async markAsGrasped(wordId) {
    return api.post(`/api/english/isGrasp/${wordId}`)
  },
  
  // 标记单词为错词
  async markAsError(wordId) {
    return api.post(`/api/english/notGrasp/${wordId}`)
  }
}