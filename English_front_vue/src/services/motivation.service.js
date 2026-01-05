import api from './api'

export const motivationService = {
  // 获取激励文案列表（用于轮播）
  async getQuoteList() {
    return api.get('/api/global/motivation/list')
  }
}