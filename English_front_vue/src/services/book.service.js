import api from './api'

export const bookService = {
  // 获取用户的课本列表
  async getBookList(userId) {
    return api.get(`/api/english/books/${userId}`)
  },
  
  // 检查课本是否存在
  async checkBookExists(bookId) {
    return api.get(`/api/english/books/${bookId}/check`)
  }
}