import { ref, onUnmounted } from 'vue'
import { motivationService } from '@/services/motivation.service'

export function useMotivation() {
  const quotes = ref([])
  const currentIndex = ref(0)
  const loading = ref(false)
  const isPlaying = ref(true)
  const intervalId = ref(null)
  
  // 轮播间隔时间（毫秒）
  const CAROUSEL_INTERVAL = 5000

  const fetchQuotes = async () => {
    try {
      loading.value = true
      const response = await motivationService.getQuoteList()
      quotes.value = response.data || []
      
      if (quotes.value.length > 0) {
        currentIndex.value = 0
      }
      
      return quotes.value
    } catch (error) {
      console.error('获取激励文案失败:', error)
      // 使用默认文案
      quotes.value = [{
        id: 'default',
        content: '学习是一场马拉松，不是百米冲刺。坚持下去，你一定能看到进步！',
        author: '学习助手'
      }]
      return quotes.value
    } finally {
      loading.value = false
    }
  }

  const goToSlide = (index) => {
    if (index >= 0 && index < quotes.value.length) {
      currentIndex.value = index
      // 重置自动播放
      if (isPlaying.value) {
        stopAutoPlay()
        startAutoPlay()
      }
    }
  }

  const nextSlide = () => {
    if (quotes.value.length > 0) {
      currentIndex.value = (currentIndex.value + 1) % quotes.value.length
    }
  }

  const prevSlide = () => {
    if (quotes.value.length > 0) {
      currentIndex.value = currentIndex.value === 0 
        ? quotes.value.length - 1 
        : currentIndex.value - 1
    }
  }

  const startAutoPlay = () => {
    if (quotes.value.length <= 1) return
    
    stopAutoPlay()
    intervalId.value = setInterval(() => {
      nextSlide()
    }, CAROUSEL_INTERVAL)
  }

  const stopAutoPlay = () => {
    if (intervalId.value) {
      clearInterval(intervalId.value)
      intervalId.value = null
    }
  }

  const toggleAutoPlay = () => {
    isPlaying.value = !isPlaying.value
    
    if (isPlaying.value) {
      startAutoPlay()
    } else {
      stopAutoPlay()
    }
  }

  // 清理定时器
  onUnmounted(() => {
    stopAutoPlay()
  })

  return {
    quotes,
    currentIndex,
    loading,
    isPlaying,
    fetchQuotes,
    goToSlide,
    nextSlide,
    prevSlide,
    startAutoPlay,
    stopAutoPlay,
    toggleAutoPlay
  }
}