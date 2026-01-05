import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { jottingService } from '@/services/jotting.service'
import { useAuthStore } from '@/stores/auth'

export const useJottingStore = defineStore('jotting', () => {
  // 状态
  const jottings = ref([])
  const activeTab = ref(1) // 1-单词, 0-句子
  const loading = ref(false)
  const pagination = ref({
    currentPage: 1,
    pageSize: 10,
    total: 0
  })
  
  // 计算属性
  const filteredJottings = computed(() => {
    return jottings.value.filter(item => item.type === activeTab.value)
  })
  
  const totalPages = computed(() => {
    return Math.ceil(pagination.value.total / pagination.value.pageSize)
  })
  
  // 动作
  const fetchJottings = async (params = {}) => {
    try {
      loading.value = true
      const authStore = useAuthStore()
      
      if (!authStore.user?.id) {
        throw new Error('用户未登录')
      }
      
      const requestParams = {
        userId: authStore.user.id,
        pageNum: params.pageNum || pagination.value.currentPage,
        pageSize: params.pageSize || pagination.value.pageSize,
        type: params.type !== undefined ? params.type : activeTab.value
      }
      
      const response = await jottingService.getJottingList(requestParams)
      
      jottings.value = response.data.list || []
      pagination.value.total = response.data.total || 0
      pagination.value.currentPage = requestParams.pageNum
      
      return response
    } catch (error) {
      console.error('获取积累列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  const addJotting = async (data) => {
    try {
      const authStore = useAuthStore()
      
      const jottingData = {
        ...data,
        userId: authStore.user.id
      }
      
      const response = await jottingService.addJotting(jottingData)
      
      // 如果是当前标签页的类型，重新获取列表
      if (data.type === activeTab.value) {
        await fetchJottings()
      }
      
      return response
    } catch (error) {
      console.error('添加积累失败:', error)
      throw error
    }
  }
  
  const updateJotting = async (data) => {
    try {
      const authStore = useAuthStore()
      
      const updateData = {
        ...data,
        userId: authStore.user.id
      }
      
      const response = await jottingService.updateJotting(updateData)
      
      // 更新本地状态
      const index = jottings.value.findIndex(item => item.id === data.id)
      if (index !== -1) {
        jottings.value[index] = { ...jottings.value[index], ...data }
      }
      
      return response
    } catch (error) {
      console.error('更新积累失败:', error)
      throw error
    }
  }
  
  const deleteJotting = async (data) => {
    try {
      const authStore = useAuthStore()
      
      const deleteData = {
        ...data,
        userId: authStore.user.id
      }
      
      const response = await jottingService.deleteJotting(deleteData)
      
      // 从本地状态中移除
      jottings.value = jottings.value.filter(item => item.id !== data.id)
      pagination.value.total = Math.max(0, pagination.value.total - 1)
      
      return response
    } catch (error) {
      console.error('删除积累失败:', error)
      throw error
    }
  }
  
  const batchDeleteJottings = async (dataArray) => {
    try {
      const authStore = useAuthStore()
      
      const deleteData = dataArray.map(item => ({
        ...item,
        userId: authStore.user.id
      }))
      
      const response = await jottingService.batchDeleteJotting(deleteData)
      
      // 从本地状态中移除
      const idsToDelete = dataArray.map(item => item.id)
      jottings.value = jottings.value.filter(item => !idsToDelete.includes(item.id))
      pagination.value.total = Math.max(0, pagination.value.total - idsToDelete.length)
      
      return response
    } catch (error) {
      console.error('批量删除积累失败:', error)
      throw error
    }
  }
  
  const markAsReviewed = async (jottingId) => {
    try {
      const authStore = useAuthStore()
      
      const response = await jottingService.markAsReviewed(authStore.user.id, jottingId)
      
      // 更新本地状态
      const index = jottings.value.findIndex(item => item.id === jottingId)
      if (index !== -1) {
        jottings.value[index].reviewed = 1
      }
      
      return response
    } catch (error) {
      console.error('标记已复习失败:', error)
      throw error
    }
  }
  
  const setActiveTab = (tab) => {
    activeTab.value = tab
    pagination.value.currentPage = 1
  }
  
  const setPage = (page) => {
    pagination.value.currentPage = page
  }
  
  const resetState = () => {
    jottings.value = []
    activeTab.value = 1
    pagination.value = {
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  }
  
  return {
    // 状态
    jottings,
    activeTab,
    loading,
    pagination,
    
    // 计算属性
    filteredJottings,
    totalPages,
    
    // 动作
    fetchJottings,
    addJotting,
    updateJotting,
    deleteJotting,
    batchDeleteJottings,
    markAsReviewed,
    setActiveTab,
    setPage,
    resetState
  }
})