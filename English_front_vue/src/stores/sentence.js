import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { sentenceService } from '@/services/sentence.service'
import { useAuthStore } from '@/stores/auth'

export const useSentenceStore = defineStore('sentence', () => {
  // 状态
  const sentences = ref([])
  const currentIndex = ref(0)
  const showChinese = ref(true)
  const learningRange = ref({ start: 1, end: 30 })
  const loading = ref(false)
  
  // 计算属性
  const currentSentence = computed(() => sentences.value[currentIndex.value] || null)
  const progress = computed(() => {
    if (sentences.value.length === 0) return 0
    return Math.round(((currentIndex.value + 1) / sentences.value.length) * 100)
  })
  const hasNext = computed(() => currentIndex.value < sentences.value.length - 1)
  const hasPrev = computed(() => currentIndex.value > 0)
  const totalSentences = computed(() => sentences.value.length)
  
  // 动作
  const fetchSentences = async (range = null) => {
    try {
      loading.value = true
      const authStore = useAuthStore()
      
      if (!authStore.user?.id) {
        throw new Error('用户未登录')
      }
      
      const params = {
        userId: authStore.user.id
      }
      
      const response = await sentenceService.getSentenceList(params)
      const allSentences = response.data || []
      
      // 处理范围过滤
      const currentRange = range || learningRange.value
      
      // 如果指定了范围，进行切片（注意：start是从1开始的，需要转换为0-based index）
      if (currentRange && currentRange.start && currentRange.end) {
        // 确保范围有效
        const start = Math.max(0, currentRange.start - 1)
        const end = Math.min(allSentences.length, currentRange.end)
        
        if (start < allSentences.length) {
          sentences.value = allSentences.slice(start, end)
        } else {
          sentences.value = []
        }
        
        // 更新 stored range
        learningRange.value = { ...currentRange }
      } else {
        sentences.value = allSentences
      }
      
      currentIndex.value = 0
      
      return response
    } catch (error) {
      console.error('获取句子列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  const nextSentence = () => {
    if (hasNext.value) {
      currentIndex.value++
      showChinese.value = true // 重置显示状态
    }
  }
  
  const prevSentence = () => {
    if (hasPrev.value) {
      currentIndex.value--
      showChinese.value = true // 重置显示状态
    }
  }
  
  const goToSentence = (index) => {
    if (index >= 0 && index < sentences.value.length) {
      currentIndex.value = index
      showChinese.value = true
    }
  }
  
  const toggleChinese = () => {
    showChinese.value = !showChinese.value
  }
  
  const markCurrentAsGrasped = async () => {
    if (!currentSentence.value) return
    
    try {
      await sentenceService.markAsGrasped(currentSentence.value.id)
      
      // 更新本地状态
      if (currentSentence.value) {
        currentSentence.value.isGrasp = 1
      }
      
      return { success: true }
    } catch (error) {
      console.error('标记已掌握失败:', error)
      throw error
    }
  }
  
  const markCurrentAsError = async () => {
    if (!currentSentence.value) return
    
    try {
      await sentenceService.markAsError(currentSentence.value.id)
      
      // 更新本地状态
      if (currentSentence.value) {
        currentSentence.value.isGrasp = 2
        currentSentence.value.errorTimes = (currentSentence.value.errorTimes || 0) + 1
      }
      
      return { success: true }
    } catch (error) {
      console.error('标记错句失败:', error)
      throw error
    }
  }
  
  const setLearningRange = (range) => {
    learningRange.value = range
  }
  
  const resetState = () => {
    sentences.value = []
    currentIndex.value = 0
    showChinese.value = true
    learningRange.value = { start: 1, end: 30 }
  }
  
  return {
    // 状态
    sentences,
    currentIndex,
    showChinese,
    learningRange,
    loading,
    
    // 计算属性
    currentSentence,
    progress,
    hasNext,
    hasPrev,
    totalSentences,
    
    // 动作
    fetchSentences,
    nextSentence,
    prevSentence,
    goToSentence,
    toggleChinese,
    markCurrentAsGrasped,
    markCurrentAsError,
    setLearningRange,
    resetState
  }
})