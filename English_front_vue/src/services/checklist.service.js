import api from './api'

export const checklistService = {
  // 获取学习清单列表
  async getChecklistList(userId, type = 1) {
    return api.get(`/api/studyRecord/list/${userId}`, { 
      params: { type } 
    })
  },
  
  // 添加学习清单
  async addChecklist(data) {
    return api.post('/api/studyRecord/addRecord', data)
  },
  
  // 更新学习清单
  async updateChecklist(data) {
    return api.post('/api/studyRecord/update', data)
  },
  
  // 批量删除学习清单
  async batchDeleteChecklist(userId, ids) {
    return api.post('/api/studyRecord/delete', ids, {
      params: { userId }
    })
  },

  // 设置学习清单为已复习（支持批量）
  async setReview(userId, ids) {
    return api.post(`/api/studyRecord/setReview/${userId}`, ids)
  }
}