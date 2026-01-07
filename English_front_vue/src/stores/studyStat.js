import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { studyService } from '@/services/study.service'

export const useStudyStatStore = defineStore('studyStat', () => {
  // 状态
  const todaySec = ref(0)
  const totalSec = ref(0)
  const loading = ref(false)
  const lastUpdateTime = ref(null)
  
  // 计算属性
  const todayDisplay = computed(() => formatDuration(todaySec.value))
  const totalDisplay = computed(() => formatDuration(totalSec.value))
  
  // 格式化时长显示
  const formatDuration = (seconds) => {
    if (seconds <= 0) return '0h0min'
    
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    return `${hours}h${minutes}min`
  }
  
  // 动作
  const refreshStats = async () => {
    try {
      loading.value = true
      const result = await studyService.getTodayStats()
      
      todaySec.value = result.dayTotalSec || 0
      totalSec.value = result.totalSec || 0
      lastUpdateTime.value = new Date()
      
      console.log('学习统计已刷新:', result)
    } catch (error) {
      console.error('刷新学习统计失败:', error)
    } finally {
      loading.value = false
    }
  }
  
  const updateStats = (newStats) => {
    if (newStats) {
      todaySec.value = newStats.dayTotalSec || 0
      totalSec.value = newStats.totalSec || 0
      lastUpdateTime.value = new Date()
      
      console.log('学习统计已更新:', newStats)
    }
  }
  
  // 初始化时获取统计数据
  const init = async () => {
    await refreshStats()
  }
  
  return {
    // 状态
    todaySec,
    totalSec,
    loading,
    lastUpdateTime,
    
    // 计算属性
    todayDisplay,
    totalDisplay,
    
    // 动作
    refreshStats,
    updateStats,
    init,
    formatDuration
  }
})
