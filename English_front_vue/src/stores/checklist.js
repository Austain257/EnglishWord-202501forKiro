import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { checklistService } from '@/services/checklist.service'
import { useAuthStore } from '@/stores/auth'

export const useChecklistStore = defineStore('checklist', () => {
  // 状态
  const checklists = ref([])
  const activeTab = ref(1) // 0-句子, 1-单词, 2-听力
  const selectedItems = ref([])
  const loading = ref(false)
  
  // 计算属性
  const filteredChecklists = computed(() => {
    return checklists.value.filter(item => item.type === activeTab.value)
  })
  
  const hasSelectedItems = computed(() => selectedItems.value.length > 0)
  
  // 动作
  const fetchChecklists = async (type = null) => {
    try {
      loading.value = true
      const authStore = useAuthStore()
      
      if (!authStore.user?.id) {
        throw new Error('用户未登录')
      }
      
      // 如果没有指定type，使用当前activeTab的值
      const requestType = type !== null ? type : activeTab.value
      
      const response = await checklistService.getChecklistList(authStore.user.id, requestType)
      checklists.value = response.data || []
      
      return response
    } catch (error) {
      console.error('获取学习清单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  const addChecklist = async (data) => {
    try {
      const authStore = useAuthStore()
      
      const checklistData = {
        ...data,
        userId: authStore.user.id
      }
      
      const response = await checklistService.addChecklist(checklistData)
      
      // 重新获取列表
      await fetchChecklists()
      
      return response
    } catch (error) {
      console.error('添加学习清单失败:', error)
      throw error
    }
  }
  
  const updateChecklist = async (data) => {
    try {
      const authStore = useAuthStore()
      
      const updateData = {
        ...data,
        userId: authStore.user.id
      }
      
      const response = await checklistService.updateChecklist(updateData)
      
      // 更新本地状态
      const index = checklists.value.findIndex(item => item.id === data.id)
      if (index !== -1) {
        checklists.value[index] = { ...checklists.value[index], ...data }
      }
      
      return response
    } catch (error) {
      console.error('更新学习清单失败:', error)
      throw error
    }
  }
  
  const batchDeleteChecklists = async (ids) => {
    try {
      const authStore = useAuthStore()
      
      const response = await checklistService.batchDeleteChecklist(authStore.user.id, ids)
      
      // 从本地状态中移除已删除的项目
      checklists.value = checklists.value.filter(item => !ids.includes(item.id.toString()))
      
      // 清空选中项
      selectedItems.value = []
      
      return response
    } catch (error) {
      console.error('批量删除学习清单失败:', error)
      throw error
    }
  }
  
  const toggleSelection = (itemId) => {
    const index = selectedItems.value.indexOf(itemId)
    if (index === -1) {
      selectedItems.value.push(itemId)
    } else {
      selectedItems.value.splice(index, 1)
    }
  }
  
  const selectAll = () => {
    selectedItems.value = filteredChecklists.value.map(item => item.id)
  }
  
  const clearSelection = () => {
    selectedItems.value = []
  }
  
  const setActiveTab = async (tab) => {
    activeTab.value = tab
    clearSelection()
    // 切换标签时重新获取对应类型的数据
    await fetchChecklists(tab)
  }
  
  const markSelectedAsCompleted = async () => {
    try {
      const promises = selectedItems.value.map(id => {
        const item = checklists.value.find(c => c.id === id)
        if (item) {
          return updateChecklist({
            ...item,
            alreadyReviewed: 1
          })
        }
      })
      
      await Promise.all(promises.filter(Boolean))
      clearSelection()
      
      return { success: true }
    } catch (error) {
      console.error('批量标记完成失败:', error)
      throw error
    }
  }
  
  return {
    // 状态
    checklists,
    activeTab,
    selectedItems,
    loading,
    
    // 计算属性
    filteredChecklists,
    hasSelectedItems,
    
    // 动作
    fetchChecklists,
    addChecklist,
    updateChecklist,
    batchDeleteChecklists,
    toggleSelection,
    selectAll,
    clearSelection,
    setActiveTab,
    markSelectedAsCompleted
  }
})