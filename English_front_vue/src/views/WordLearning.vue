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

            <div class="flex items-center gap-2 sm:gap-3">
        <!-- Progress -->
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>进度</span>
          <span class="text-blue-600 font-bold">{{ currentIndex + 1 }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ totalWords }}</span>
        </div>

        <!-- Current Range -->
         <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>当前范围</span>
          <span class="text-blue-600 font-bold">{{ wordStore.learningRange.start }}-{{ wordStore.learningRange.end }}</span>
        </div>
        
        <!-- Range Settings -->
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
      </div>
    </nav>

    <!-- 主体内容 -->
    <main class="flex-1 relative z-10 flex flex-col max-w-4xl mx-auto w-full px-4 sm:px-6 lg:px-8 pb-6">
      
      <!-- 进度条 (移动端) -->
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

      <!-- 加载状态 -->
      <div v-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[400px]">
        <div class="w-12 h-12 border-4 border-blue-100 border-t-blue-500 rounded-full animate-spin mb-4"></div>
        <p class="text-slate-400 text-sm font-medium animate-pulse">正在加载单词数据...</p>
      </div>

      <!-- 无数据状态 -->
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

      <!-- 单词卡片 -->
      <div v-else class="flex-1 flex flex-col justify-center py-4 sm:py-8">
        <div class="relative group perspective-1000 w-full max-w-2xl mx-auto">
          <!-- 卡片容器 -->
          <div 
            class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500"
          >
            <!-- 状态标签 -->
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
              <!-- 单词主体 -->
              <h2 class="text-5xl sm:text-6xl md:text-7xl font-bold text-slate-900 mb-4 tracking-tight">
                {{ currentWord.word }}
              </h2>
              
              <!-- 音标与发音 -->
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

              <!-- 释义区域 (直接显示) -->
              <div class="w-full max-w-lg mx-auto mt-6">
                <div class="text-xl sm:text-2xl text-slate-600 font-medium leading-relaxed px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100/50">
                  {{ currentWord.chinese || '暂无释义' }}
                </div>
              </div>

              <!-- 统计数据小部件 -->
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
      </div>

      <!-- 底部控制栏 -->
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

        <div class="bg-blue-50 rounded-2xl p-5 mb-6 text-sm text-blue-800 space-y-2">
           <div class="flex justify-between">
              <span class="opacity-70">课本总词汇</span>
              <span class="font-bold">{{ currentBook?.wordCount || 0 }} 词</span>
           </div>
           <div class="flex justify-between">
              <span class="opacity-70">当前已选</span>
              <span class="font-bold">{{ Math.min(rangeForm.end - rangeForm.start + 1, currentBook?.wordCount || 0) }} 词</span>
           </div>
        </div>

        <div class="flex gap-4 mb-6">
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Start</label>
            <input 
              v-model.number="rangeForm.start"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-blue-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
          <div class="flex items-end pb-4 text-slate-300">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" /></svg>
          </div>
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">End</label>
            <input 
              v-model.number="rangeForm.end"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-blue-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
        </div>

        <div class="mb-8">
           <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">Quick Select</label>
           <div class="grid grid-cols-4 gap-2">
             <button 
                v-for="(range, idx) in quickRanges" 
                :key="idx"
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
import { useToast } from '@/composables/useToast'

const router = useRouter()
const wordStore = useWordStore()
const bookStore = useBookStore()
const { success, error } = useToast()

// 状态
const showRangeModal = ref(false)
const rangeForm = ref({
  start: 1,
  end: 50
})

// 计算属性
const loading = computed(() => wordStore.loading)
const currentWord = computed(() => wordStore.currentWord)
const currentIndex = computed(() => wordStore.currentIndex)
const totalWords = computed(() => wordStore.totalWords)
const hasNext = computed(() => wordStore.hasNext)
const hasPrev = computed(() => wordStore.hasPrev)
const currentBook = computed(() => bookStore.currentBook)

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

const loadWords = async () => {
  try {
    await wordStore.fetchWords()
    if (wordStore.totalWords === 0) {
      error('当前范围内没有可学习的单词')
    } else {
      // 自动播放发音（可选，如果用户开启了自动发音配置）
      // playPronunciation()
    }
  } catch (err) {
    error('加载单词失败：' + err.message)
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
  
  try {
    await wordStore.fetchWords(rangeForm.value)
    showRangeModal.value = false
    success(`已设置范围：${rangeForm.value.start}-${rangeForm.value.end}`)
  } catch (err) {
    error('设置范围失败：' + err.message)
  }
}

const setQuickRange = (start, end) => {
  rangeForm.value.start = start
  rangeForm.value.end = Math.min(end, currentBook.value?.wordCount || end)
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  rangeForm.value = { ...wordStore.learningRange }
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

// 生命周期
onMounted(async () => {
  window.addEventListener('keydown', handleKeydown)
  
  // 初始化范围表单
  if (wordStore.learningRange) {
    rangeForm.value = { ...wordStore.learningRange }
  }
  
  // 如果没有数据，尝试加载
  if (wordStore.totalWords === 0) {
    await loadWords()
  }
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
