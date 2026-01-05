import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { wordService } from '@/services/word.service'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'

export const useWordStore = defineStore('word', () => {
  // 状态
  const words = ref([])
  const currentIndex = ref(0)
  const showChinese = ref(true)
  const learningRange = ref({ start: 1, end: 50 })
  const loading = ref(false)
  
  // 计算属性
  const currentWord = computed(() => words.value[currentIndex.value] || null)
  const progress = computed(() => {
    if (words.value.length === 0) return 0
    return Math.round(((currentIndex.value + 1) / words.value.length) * 100)
  })
  const hasNext = computed(() => currentIndex.value < words.value.length - 1)
  const hasPrev = computed(() => currentIndex.value > 0)
  const totalWords = computed(() => words.value.length)
  
  // 动作
  const fetchWords = async (range = null) => {
    try {
      loading.value = true
      const authStore = useAuthStore()
      const bookStore = useBookStore()
      
      if (!authStore.user?.id) {
        throw new Error('用户未登录')
      }
      
      if (!bookStore.currentBookId) {
        throw new Error('请先选择课本')
      }
      
      const params = {
        userId: authStore.user.id,
        bookId: bookStore.currentBookId,
        start: range?.start || learningRange.value.start,
        end: range?.end || learningRange.value.end
      }
      
      const response = await wordService.getWordList(params)
      words.value = response.data || []
      currentIndex.value = 0
      
      // 更新学习范围
      if (range) {
        learningRange.value = range
      }
      
      return response
    } catch (error) {
      console.error('获取单词列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  const nextWord = () => {
    if (hasNext.value) {
      currentIndex.value++
      showChinese.value = true // 重置显示状态
    }
  }
  
  const prevWord = () => {
    if (hasPrev.value) {
      currentIndex.value--
      showChinese.value = true // 重置显示状态
    }
  }
  
  const goToWord = (index) => {
    if (index >= 0 && index < words.value.length) {
      currentIndex.value = index
      showChinese.value = true
    }
  }
  
  const toggleChinese = () => {
    showChinese.value = !showChinese.value
  }
  
  const markCurrentAsGrasped = async () => {
    if (!currentWord.value) return
    
    try {
      await wordService.markAsGrasped(currentWord.value.id)
      
      // 更新本地状态
      if (currentWord.value) {
        currentWord.value.isGrasp = 1
      }
      
      return { success: true }
    } catch (error) {
      console.error('标记已掌握失败:', error)
      throw error
    }
  }
  
  const markCurrentAsError = async () => {
    if (!currentWord.value) return
    
    try {
      await wordService.markAsError(currentWord.value.id)
      
      // 更新本地状态
      if (currentWord.value) {
        currentWord.value.isGrasp = 2
        currentWord.value.errorTimes = (currentWord.value.errorTimes || 0) + 1
      }
      
      return { success: true }
    } catch (error) {
      console.error('标记错词失败:', error)
      throw error
    }
  }
  
  const setLearningRange = (range) => {
    learningRange.value = range
  }
  
  const resetState = () => {
    words.value = []
    currentIndex.value = 0
    showChinese.value = true
    learningRange.value = { start: 1, end: 50 }
  }
  
  return {
    // 状态
    words,
    currentIndex,
    showChinese,
    learningRange,
    loading,
    
    // 计算属性
    currentWord,
    progress,
    hasNext,
    hasPrev,
    totalWords,
    
    // 动作
    fetchWords,
    nextWord,
    prevWord,
    goToWord,
    toggleChinese,
    markCurrentAsGrasped,
    markCurrentAsError,
    setLearningRange,
    resetState
  }
})