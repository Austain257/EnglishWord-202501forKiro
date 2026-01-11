<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- 背景装饰 -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-blue-50/80 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 -right-20 w-96 h-96 bg-indigo-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-40 -left-20 w-72 h-72 bg-blue-100/40 rounded-full blur-3xl pointer-events-none"></div>

    <!-- 顶部导航 -->
    <nav class="relative z-10 px-4 sm:px-6 lg:px-8 h-16 sm:h-20 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button 
          @click="goBack" 
          class="p-2 -ml-2 text-slate-500 hover:text-slate-900 hover:bg-white/60 rounded-xl transition-all duration-200"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <div class="flex flex-col">
          <h1 class="text-lg font-bold text-slate-900 tracking-tight">单词学习</h1>
          <div class="flex items-center gap-2 text-xs text-slate-500">
             <span class="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
             {{ currentBook?.name || '默认词书' }}
          </div>
        </div>
      </div>

      <div v-if="studyStarted" class="flex items-center gap-2 sm:gap-3">
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>进度</span>
          <span class="text-blue-600 font-bold">{{ currentIndex + 1 }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ totalWords }}</span>
        </div>

        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>当前范围</span>
          <span class="text-blue-600 font-bold">{{ wordStore.learningRange.start }}-{{ wordStore.learningRange.end }}</span>
        </div>
        
        <button 
          @click="showRangeModal = true"
          class="flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-slate-600 hover:text-blue-600 hover:border-blue-200 transition-all active:scale-95"
          title="设置范围"
        >
          <span class="hidden sm:inline">修改学习范围</span>
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </button>

        <button
          @click="jumpToNextTen"
          :disabled="!canJumpNextTen"
          class="flex items-center gap-2 px-3 py-2 rounded-xl text-xs font-semibold border transition-all active:scale-95"
          :class="canJumpNextTen 
            ? 'bg-blue-50 text-blue-600 border-blue-100 hover:bg-blue-100 hover:border-blue-200' 
            : 'bg-slate-100 text-slate-400 border-slate-100 cursor-not-allowed'"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
          <span>下十个</span>
        </button>
      </div>
    </nav>

    <main class="flex-1 relative z-10 flex flex-col max-w-4xl mx-auto w-full px-4 sm:px-6 lg:px-8 pb-6">
      <div :class="studyStarted ? 'mb-6' : 'flex-1 flex items-center justify-center py-10'">
        <div :class="studyStarted ? '' : 'w-full max-w-md mx-auto text-center space-y-4'">
          <StudyTimer 
            :compact="studyStarted"
            @study-started="onStudyStarted"
            @study-ended="onStudyEnded"
            @study-paused="onStudyPaused"
            @study-resumed="onStudyResumed"
          />
          <p v-if="!studyStarted" class="text-sm text-slate-500">
            点击“开始学习”后会自动加载当前课本的 10 个新单词，计时器也会缩小停留在页面顶部，方便随时查看剩余时间。
          </p>
        </div>
      </div>

      <template v-if="studyStarted">
        <div class="sm:hidden mb-6 px-1">
          <div class="flex justify-between text-xs font-medium text-slate-500 mb-2">
            <span>当前进度</span>
            <span>{{ Math.round(((currentIndex + 1) / totalWords) * 100) }}%</span>
          </div>
          <div class="h-2 bg-slate-100 rounded-full overflow-hidden">
            <div 
              class="h-full bg-blue-500 rounded-full transition-all duration-300 ease-out"
              :style="{ width: `${((currentIndex + 1) / totalWords) * 100}%` }"
            ></div>
          </div>
        </div>

        <div v-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[400px]">
          <div class="w-12 h-12 border-4 border-blue-100 border-t-blue-500 rounded-full animate-spin mb-4"></div>
          <p class="text-slate-400 text-sm font-medium animate-pulse">正在加载单词数据...</p>
        </div>

        <div v-else-if="!currentWord" class="flex-1 flex flex-col items-center justify-center min-h-[400px] text-center">
          <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
            <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z" />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-2">暂无单词</h3>
          <p class="text-slate-500 mb-8 max-w-xs mx-auto">当前范围内没有可学习的单词，请尝试调整范围或重新加载。</p>
          <button 
            @click="loadWords" 
            class="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-semibold shadow-lg shadow-blue-500/30 transition-all hover:-translate-y-0.5"
          >
            重新加载
          </button>
        </div>

        <div v-else class="flex-1 flex flex-col justify-center py-4 sm:py-8">
          <div class="relative group perspective-1000 w-full max-w-2xl mx-auto">
            <div 
              class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500"
            >
              <div class="absolute top-6 right-6 z-10">
                <span 
                  class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border"
                  :class="{
                    'bg-emerald-50 text-emerald-600 border-emerald-100': currentWord.isGrasp === 1,
                    'bg-rose-50 text-rose-600 border-rose-100': currentWord.isGrasp === 2,
                    'bg-slate-50 text-slate-500 border-slate-100': !currentWord.isGrasp || currentWord.isGrasp === 0
                  }"
                >
                  {{ getGraspText(currentWord.isGrasp) }}
                </span>
              </div>

              <div class="p-8 sm:p-12 md:p-16 flex flex-col items-center text-center">
                <h2 class="text-5xl sm:text-6xl md:text-7xl font-bold text-slate-900 mb-4 tracking-tight">
                  {{ currentWord.word }}
                </h2>
                
                <div class="flex items-center gap-3 mb-10">
                  <span class="text-xl text-slate-500 font-serif italic">
                    {{ currentWord.phonetic || currentWord.pronounce || '...' }}
                  </span>
                  <button 
                    @click="playPronunciation"
                    class="p-2 rounded-full bg-blue-50 text-blue-600 hover:bg-blue-100 hover:scale-110 transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                    title="播放发音 (Space)"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                    </svg>
                  </button>
                </div>

                <div class="w-full max-w-lg mx-auto mt-6">
                  <div class="text-xl sm:text-2xl text-slate-600 font-medium leading-relaxed px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100/50">
                    {{ currentWord.chinese || '暂无释义' }}
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4 mt-10 w-full max-w-sm">
                  <div class="flex flex-col items-center p-3 bg-slate-50 rounded-xl border border-slate-100">
                    <span class="text-xs text-slate-400 uppercase font-bold tracking-wider mb-1">出现次数</span>
                    <span class="text-lg font-bold text-slate-700">{{ currentWord.times || 0 }} <span class="text-xs font-normal text-slate-400">次</span></span>
                  </div>
                  <div class="flex flex-col items-center p-3 bg-slate-50 rounded-xl border border-slate-100">
                    <span class="text-xs text-slate-400 uppercase font-bold tracking-wider mb-1">错误次数</span>
                    <span class="text-lg font-bold" :class="(currentWord.errorTimes || 0) > 0 ? 'text-rose-500' : 'text-slate-700'">{{ currentWord.errorTimes || 0 }} <span class="text-xs font-normal text-slate-400">次</span></span>
                  </div>
                </div>

              </div>
            </div>
          </div>

          <div v-if="currentWord" class="mt-auto pt-6 safe-bottom-area">
            <div class="flex flex-col sm:flex-row items-stretch sm:items-center justify-center gap-3 sm:gap-6">
              <button 
                @click="prevWord"
                :disabled="!hasPrev"
                class="w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-4 sm:py-3 rounded-2xl text-base font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed text-slate-600 bg-white border border-slate-200 shadow-sm hover:bg-slate-50 hover:border-slate-300 active:scale-95"
                title="上一个 (←)"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                </svg>
                <span>上一个</span>
              </button>

              <button 
                @click="nextWord"
                :disabled="!hasNext"
                class="w-full sm:w-auto flex items-center justify-center gap-2 px-8 py-4 sm:py-3.5 rounded-2xl text-base font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed text-white bg-gradient-to-r from-blue-600 to-indigo-600 shadow-lg shadow-blue-500/30 hover:shadow-blue-500/40 hover:-translate-y-0.5 active:scale-95 active:translate-y-0"
                title="下一个 (→)"
              >
                <span>下一个</span>
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </template>
    </main>

    <!-- 范围设置模态框 (UI升级) -->
      <div v-if="showRangeModal" class="fixed inset-0 z-50 flex items-center justify-center px-4">
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="cancelRangeSelection"></div>
        <div class="relative w-full max-w-lg bg-white rounded-3xl shadow-2xl p-6 sm:p-8 animate-scale-in">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold text-slate-900">学习范围</h3>
            <button @click="cancelRangeSelection" class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
          </div>
                @click="setQuickRange(range.start, range.end)"
                class="px-2 py-2 text-xs font-medium rounded-lg border hover:border-blue-300 hover:bg-blue-50 hover:text-blue-600 transition-colors text-slate-600 border-slate-200"
             >
               {{ range.label }}
             </button>
           </div>
        </div>

        <div class="flex gap-3">
          <button @click="cancelRangeSelection" class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors">取消</button>
          <button @click="applyRange" class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-blue-600 hover:bg-blue-700 shadow-lg shadow-blue-500/20 transition-colors">确认应用</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useWordStore } from '@/stores/word'
import { useBookStore } from '@/stores/book'
import { useAuthStore } from '@/stores/auth'
import { useWordStudyStore } from '@/stores/wordStudy'
import { useToast } from '@/composables/useToast'
import StudyTimer from '@/components/learning/StudyTimer.vue'

const router = useRouter()
const wordStore = useWordStore()
const bookStore = useBookStore()
const authStore = useAuthStore()
const wordStudyStore = useWordStudyStore()
const { success, error } = useToast()

// 状态
const showRangeModal = ref(false)
const rangeForm = ref({
  start: 1,
  end: 50
})
const initializing = ref(true)
const latestRecord = ref(null)
const pendingRange = ref(null)
const studyStarted = ref(false)
const wordsLoaded = ref(false)

// 计算属性
const loading = computed(() => wordStore.loading)
const currentWord = computed(() => wordStore.currentWord)
const currentIndex = computed(() => wordStore.currentIndex)
const totalWords = computed(() => wordStore.totalWords)
const hasNext = computed(() => wordStore.hasNext)
const hasPrev = computed(() => wordStore.hasPrev)
const currentBook = computed(() => bookStore.currentBook)
const canJumpNextTen = computed(() => {
  const total = currentBook.value?.wordCount || 0
  if (!total) return false
  const currentEnd = wordStore.learningRange?.end || 0
  return currentEnd < total
})
const nextRangeLabel = computed(() => {
  if (!canJumpNextTen.value) return '已到末尾'
  const total = currentBook.value?.wordCount || 0
  const currentEnd = wordStore.learningRange?.end || 0
  const nextStart = currentEnd + 1
  const nextEnd = total ? Math.min(nextStart + 9, total) : nextStart + 9
  return `${nextStart}-${nextEnd}`
})

// 快速选择范围配置
const quickRanges = [
  { label: '1-50', start: 1, end: 50 },
  { label: '51-100', start: 51, end: 100 },
  { label: '101-200', start: 101, end: 200 },
  { label: '201-300', start: 201, end: 300 }
]

// 辅助方法：获取掌握状态文本
const getGraspText = (status) => {
  switch(status) {
    case 1: return '已掌握'
    case 2: return '易错词'
    default: return '学习中'
  }
}

// 播放发音
const playPronunciation = () => {
  if (!currentWord.value) return
  
  // 优先尝试使用 word.audioUrl (如果存在)
  // 这里假设使用 Web Speech API 作为兜底
  const text = currentWord.value.word
  if ('speechSynthesis' in window) {
    // 取消之前的发音
    window.speechSynthesis.cancel()
    
    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = 'en-US' // 默认美式英语
    utterance.rate = 0.8 // 稍慢一点更清晰
    window.speechSynthesis.speak(utterance)
  } else {
    error('您的浏览器不支持语音播放')
  }
}

// 核心功能方法
const goBack = () => router.push('/')

const applyPendingRange = (range) => {
  if (!range) return
  const normalized = {
    start: Math.max(1, range.start),
    end: Math.max(range.start, range.end)
  }
  pendingRange.value = { ...normalized }
  wordStore.setLearningRange({ ...normalized })
  rangeForm.value = { ...normalized }
  wordsLoaded.value = false
}

const calculateInitialRange = (record) => {
  const bookWordCount = currentBook.value?.wordCount || 0
  const previousEnd = record?.endId || 0
  const startId = Math.max(previousEnd + 1, 1)
  if (bookWordCount && startId > bookWordCount) {
    return null
  }
  const endId = bookWordCount ? Math.min(bookWordCount, startId + 9) : startId + 9
  return { start: startId, end: endId }
}

const initializeFromLatestRecord = async () => {
  if (!authStore.user?.id) {
    error('请先登录后再开始学习')
    initializing.value = false
    return
  }
  if (!currentBook.value?.id) {
    error('请先选择课本')
    router.push('/books')
    initializing.value = false
    return
  }
  try {
    initializing.value = true
    const record = await wordStudyStore.getLatestRecord(authStore.user.id, currentBook.value.id)
    latestRecord.value = record || null
    const range = calculateInitialRange(record)
    if (!range) {
      error('当前课本已完成全部单词，暂无法继续学习')
      pendingRange.value = null
      return
    }
    applyPendingRange(range)
  } catch (err) {
    console.error('初始化学习范围失败:', err)
    error('初始化学习范围失败：' + (err.message || '请稍后重试'))
  } finally {
    initializing.value = false
  }
}

const loadWords = async (range = null, { allowWithoutStudy = false } = {}) => {
  const targetRange = range || pendingRange.value
  if (!targetRange) return
  if (!studyStarted.value && !allowWithoutStudy) {
    error('请先点击上方“开始学习”按钮')
    return
  }
  try {
    await wordStore.fetchWords(targetRange)
    if (wordStore.totalWords === 0) {
      error('当前范围内没有可学习的单词')
    } else {
      wordsLoaded.value = true
    }
  } catch (err) {
    error('加载单词失败：' + err.message)
    throw err
  }
}

const nextWord = () => {
  if (hasNext.value) {
    wordStore.nextWord()
  }
}

const prevWord = () => {
  if (hasPrev.value) {
    wordStore.prevWord()
  }
}

// 范围设置相关
const applyRange = async () => {
  if (rangeForm.value.start >= rangeForm.value.end) {
    return error('起始位置必须小于结束位置')
  }
  if (rangeForm.value.start < 1) {
    return error('起始位置不能小于1')
  }
  if (currentBook.value?.wordCount && rangeForm.value.end > currentBook.value.wordCount) {
    return error(`结束位置不能超过课本总词汇量(${currentBook.value.wordCount})`)
  }
  
  applyPendingRange(rangeForm.value)
  showRangeModal.value = false
  success(`已设置范围：${rangeForm.value.start}-${rangeForm.value.end}`)
}

const setQuickRange = (start, end) => {
  const safeEnd = Math.min(end, currentBook.value?.wordCount || end)
  rangeForm.value.start = start
  rangeForm.value.end = safeEnd
}

const jumpToNextTen = async () => {
  if (!canJumpNextTen.value) return
  const total = currentBook.value?.wordCount || 0
  const currentEnd = wordStore.learningRange?.end || 0
  const nextStart = currentEnd + 1
  const nextEnd = total ? Math.min(nextStart + 9, total) : nextStart + 9
  const nextRange = { start: nextStart, end: nextEnd }
  applyPendingRange(nextRange)
  if (studyStarted.value) {
    try {
      await loadWords(nextRange)
      success(`范围已更新：${nextRange.start}-${nextRange.end}`)
    } catch (err) {
      // loadWords 已处理错误
    }
  } else {
    success(`范围已预设：${nextRange.start}-${nextRange.end}`)
  }
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  rangeForm.value = pendingRange.value ? { ...pendingRange.value } : { ...wordStore.learningRange }
}

// 键盘快捷键支持
const handleKeydown = (e) => {
  if (showRangeModal.value) return // 模态框打开时不响应

  switch(e.code) {
    case 'Space': // 播放发音
    case 'Enter': 
      e.preventDefault()
      playPronunciation()
      break
    case 'ArrowRight': // 下一个
    case 'ArrowDown':
      e.preventDefault()
      nextWord()
      break
    case 'ArrowLeft': // 上一个
    case 'ArrowUp':
      e.preventDefault()
      prevWord()
      break
  }
}

// StudyTimer 事件处理
const onStudyStarted = (data) => {
  console.log('学习会话已开始:', data)
  studyStarted.value = true
  if (!wordsLoaded.value && pendingRange.value) {
    loadWords(pendingRange.value)
  }
  success('30分钟学习计时已开始！')
}

const onStudyEnded = () => {
  console.log('学习会话已结束')
  studyStarted.value = false
  wordsLoaded.value = false
  wordStore.resetState()
  initializeFromLatestRecord()
  success('学习会话已结束，请开始复习！')
}

const onStudyPaused = () => {
  console.log('学习已暂停')
  // 可以在这里暂停其他相关功能
}

const onStudyResumed = () => {
  console.log('学习已恢复')
  // 可以在这里恢复其他相关功能
}

// 生命周期
onMounted(async () => {
  window.addEventListener('keydown', handleKeydown)
  await initializeFromLatestRecord()
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  window.speechSynthesis.cancel() // 离开页面停止发音
})

// 监听当前单词变化，可以做一些重置操作
watch(currentWord, () => {
  // 可以在这里做一些动画触发
})
</script>

<style scoped>
@keyframes scale-in {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-scale-in {
  animation: scale-in 0.2s ease-out forwards;
}

/* 透视效果，用于未来可能的翻转动画 */
.perspective-1000 {
  perspective: 1000px;
}

.safe-bottom-area {
  padding-bottom: max(env(safe-area-inset-bottom), 0.5rem);
}
</style>
