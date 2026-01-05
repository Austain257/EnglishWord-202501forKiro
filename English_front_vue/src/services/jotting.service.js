import api from './api'

export const jottingService = {
  // 获取积累列表（分页）- 根据后端实际实现使用GET请求
  async getJottingList(params) {
    return api.get('/api/jotting/list', { 
      params: {
        userId: params.userId,
        page: params.pageNum || 1,
        size: params.pageSize || 10
      }
    })
  },
  
  // 添加积累
  async addJotting(data) {
    return api.post('/api/jotting/add', data)
  },
  
  // 更新积累
  async updateJotting(data) {
    return api.put('/api/jotting/update', data)
  },
  
  // 删除积累
  async deleteJotting(data) {
    return api.delete('/api/jotting/delete', { data })
  },
  
  // 批量删除积累
  async batchDeleteJotting(data) {
    return api.post('/api/jotting/batchDelete', data)
  },
  
  // 标记为已复习
  async markAsReviewed(userId, jottingId) {
    return api.post(`/api/jotting/setReview/${userId}`, null, {
      params: { id: jottingId }
    })
  }
}