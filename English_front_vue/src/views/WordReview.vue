<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- 背景装饰 -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-amber-50/80 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 -right-20 w-96 h-96 bg-orange-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-40 -left-20 w-72 h-72 bg-yellow-100/40 rounded-full blur-3xl pointer-events-none"></div>

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
          <h1 class="text-lg font-bold text-slate-900 tracking-tight">单词复习</h1>
          <button 
            @click="toggleMode"
            class="flex items-center gap-2 text-xs text-slate-500 hover:text-slate-900 transition-colors mt-0.5"
          >
             <span class="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
             <span>{{ reviewMode === 'en2cn' ? '英译汉' : '汉译英' }}</span>
             <svg class="w-3 h-3 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
               <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
             </svg>
          </button>
        </div>
      </div>

      <div class="flex items-center gap-3">
        <!-- 进度 -->
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>进度</span>
          <span class="text-amber-600 font-bold">{{ currentIndex + 1 }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ totalWords }}</span>
        </div>
        <!-- 范围 -->
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>当前范围</span>
          <span class="text-amber-600 font-bold">{{ currentStudyRange }}</span>
        </div>
        <!-- 范围设置 -->
        <button 
          @click="showRangeModal = true"
          class="flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-slate-600 hover:text-amber-600 hover:border-amber-200 transition-all active:scale-95"
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
      
      <!-- 复习完成标记区域 -->
      <div class="mb-5">
        <div class="rounded-xl border border-amber-100 bg-white px-4 py-3 text-sm text-slate-600">
          <div class="flex flex-wrap items-center justify-between gap-2 mb-2">
            <span class="font-semibold text-slate-800">第一轮复习</span>
            <span class="text-xs text-slate-400">范围 {{ currentStudyRange }}</span>
          </div>

          <div v-if="initializing" class="text-xs text-slate-400">复习数据加载中，请稍候...</div>

          <template v-else>
            <div v-if="round1Completed" class="text-sm text-slate-600">
              当前你已经完成新词的第一轮复习，请在规定时间内完成第二轮复习。
            </div>
            <div v-else class="flex justify-end">
              <button
                @click="markFirstRoundComplete"
                :disabled="markingRound1 || !latestRecord"
                class="px-4 py-2 rounded-lg font-semibold text-white bg-emerald-500 hover:bg-emerald-600 transition-colors disabled:opacity-60 disabled:cursor-not-allowed text-xs"
              >
                {{ markingRound1 ? '标记中...' : '标记第一轮复习完成' }}
              </button>
            </div>
          </template>
        </div>
      </div>
      
      <!-- 进度条 (移动端) -->
      <div class="sm:hidden mb-6 px-1">
        <div class="flex justify-between text-xs font-medium text-slate-500 mb-2">
          <span>当前进度</span>
          <span>{{ Math.round(((currentIndex + 1) / totalWords) * 100) }}%</span>
        </div>
        <div class="h-2 bg-slate-100 rounded-full overflow-hidden">
          <div 
            class="h-full bg-amber-500 rounded-full transition-all duration-300 ease-out"
            :style="{ width: `${((currentIndex + 1) / totalWords) * 100}%` }"
          ></div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[400px]">
        <div class="w-12 h-12 border-4 border-amber-100 border-t-amber-500 rounded-full animate-spin mb-4"></div>
        <p class="text-slate-400 text-sm font-medium animate-pulse">正在加载单词数据...</p>
      </div>

      <!-- 无数据状态 -->
      <div v-else-if="!currentWord" class="flex-1 flex flex-col items-center justify-center min-h-[400px] text-center">
        <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z" />
          </svg>
        </div>
        <h3 class="text-xl font-bold text-slate-900 mb-2">暂无复习单词</h3>
        <p class="text-slate-500 mb-8 max-w-xs mx-auto">当前范围内没有可复习的单词，请尝试调整范围或重新加载。</p>
        <button 
          @click="loadWords" 
          class="px-8 py-3 bg-amber-600 hover:bg-amber-700 text-white rounded-xl font-semibold shadow-lg shadow-amber-500/30 transition-all hover:-translate-y-0.5"
        >
          重新加载
        </button>
      </div>

      <!-- 单词复习卡片 -->
      <div v-else class="flex-1 flex flex-col justify-center py-4 sm:py-8">
        <div class="relative group w-full max-w-2xl mx-auto">
          <!-- 卡片容器 -->
          <div 
            class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500"
          >
            <!-- 状态标签 -->
            <div class="absolute top-6 right-6 z-10 flex gap-2">
              <span 
                v-if="currentWord.isGrasp === 1"
                class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border bg-emerald-50 text-emerald-600 border-emerald-100"
              >
                已掌握
              </span>
              <span 
                v-if="currentWord.isGrasp === 2"
                class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border bg-rose-50 text-rose-600 border-rose-100"
              >
                易错词
              </span>
            </div>

            <div class="p-8 sm:p-12 flex flex-col items-center text-center">
              
              <!-- 模式：英译汉 (显示英文) -->
              <template v-if="reviewMode === 'en2cn'">
                <h2 class="text-5xl sm:text-6xl md:text-7xl font-bold text-slate-900 mb-4 tracking-tight">
                  {{ currentWord.word }}
                </h2>
                
                <div class="flex items-center gap-3 mb-12">
                  <span class="text-xl text-slate-500 font-serif italic">
                    {{ currentWord.phonetic || currentWord.pronounce || '...' }}
                  </span>
                  <button 
                    @click="playPronunciation"
                    class="p-2 rounded-full bg-amber-50 text-amber-600 hover:bg-amber-100 hover:scale-110 transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
                    title="播放发音 (Space)"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                    </svg>
                  </button>
                </div>

                <div class="w-full relative min-h-[88px] flex items-center justify-center">
                  <div 
                    v-if="showAnswer"
                    class="w-full text-xl sm:text-2xl text-slate-700 font-medium leading-relaxed px-6 py-4 rounded-2xl bg-amber-50 border border-amber-100/50 animate-fade-in"
                  >
                    {{ currentWord.chinese || '暂无释义' }}
                  </div>
                  <button
                    v-else
                    @click="toggleAnswer"
                    class="group flex items-center gap-2 px-6 py-3 rounded-xl bg-slate-50 hover:bg-slate-100 border border-slate-200 text-slate-500 font-medium transition-all hover:scale-105 active:scale-95"
                  >
                    <svg class="w-5 h-5 text-slate-400 group-hover:text-amber-500 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                    <span>点击显示释义</span>
                  </button>
                </div>
              </template>

              <!-- 模式：汉译英 (显示中文) -->
              <template v-else>
                <div class="mb-12">
                   <h2 class="text-3xl sm:text-4xl font-bold text-slate-900 leading-relaxed px-4">
                     {{ currentWord.chinese || '暂无释义' }}
                   </h2>
                </div>

                <div class="w-full relative min-h-[120px] flex items-center justify-center">
                  <div 
                    v-if="showAnswer"
                    class="flex flex-col items-center animate-fade-in"
                  >
                    <h2 class="text-4xl sm:text-5xl font-bold text-amber-600 mb-4 tracking-tight">
                      {{ currentWord.word }}
                    </h2>
                    <div class="flex items-center gap-3">
                      <span class="text-xl text-slate-500 font-serif italic">
                        {{ currentWord.phonetic || currentWord.pronounce || '...' }}
                      </span>
                      <button 
                        @click="playPronunciation"
                        class="p-2 rounded-full bg-amber-50 text-amber-600 hover:bg-amber-100 transition-all"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                        </svg>
                      </button>
                    </div>
                  </div>

                  <button
                    v-else
                    @click="toggleAnswer"
                    class="group flex items-center gap-2 px-6 py-3 rounded-xl bg-slate-50 hover:bg-slate-100 border border-slate-200 text-slate-500 font-medium transition-all hover:scale-105 active:scale-95"
                  >
                    <svg class="w-5 h-5 text-slate-400 group-hover:text-amber-500 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                    <span>点击显示单词</span>
                  </button>
                </div>
              </template>

            </div>

            <!-- 操作按钮区域 - 上下两行 -->
            <div class="bg-slate-50/50 border-t border-slate-100 p-6 flex flex-col gap-4">
              <!-- 第一行：上一个 / 下一个 -->
              <div class="grid grid-cols-2 gap-4">
                <button 
                  @click="prevWord"
                  :disabled="!hasPrev"
                  class="flex items-center justify-center gap-2 py-3.5 rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 hover:border-slate-300 active:scale-95 shadow-sm"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
                  上一个
                </button>
                <button 
                  @click="nextWord"
                  :disabled="!hasNext"
                  class="flex items-center justify-center gap-2 py-3.5 rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed bg-gradient-to-r from-amber-500 to-orange-600 text-white shadow-lg shadow-amber-500/30 hover:shadow-amber-500/40 hover:-translate-y-0.5 active:scale-95"
                >
                  下一个
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" /></svg>
                </button>
              </div>

              <!-- 第二行：已掌握 / 加入错词本 -->
              <div class="grid grid-cols-2 gap-4">
                 <button 
                    @click="markAsGrasped"
                    class="flex items-center justify-center gap-2 py-3 rounded-xl font-bold transition-all active:scale-95 border-2 border-emerald-100 bg-emerald-50 text-emerald-600 hover:bg-emerald-100 hover:border-emerald-200"
                    :disabled="markingGrasped"
                 >
                    <span v-if="!markingGrasped">已掌握</span>
                    <span v-else class="animate-pulse">标记中...</span>
                 </button>
                 <button 
                    @click="markAsError"
                    class="flex items-center justify-center gap-2 py-3 rounded-xl font-bold transition-all active:scale-95 border-2 border-rose-100 bg-rose-50 text-rose-600 hover:bg-rose-100 hover:border-rose-200"
                    :disabled="markingError"
                 >
                    <span v-if="!markingError">加入错词本</span>
                    <span v-else class="animate-pulse">标记中...</span>
                 </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 范围设置模态框 -->
    <div v-if="showRangeModal" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="cancelRangeSelection"></div>
      <div class="relative w-full max-w-lg bg-white rounded-3xl shadow-2xl p-6 sm:p-8 animate-scale-in">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-slate-900">复习范围</h3>
          <button @click="cancelRangeSelection" class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>

        <div class="flex gap-4 mb-6">
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Start</label>
            <input 
              v-model.number="rangeForm.start"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-amber-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
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
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-amber-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
        </div>

        <div class="flex gap-3">
          <button @click="cancelRangeSelection" class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors">取消</button>
          <button @click="applyRange" class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-amber-500 hover:bg-amber-600 shadow-lg shadow-amber-500/20 transition-colors">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useWordStore } from '@/stores/word'
import { useBookStore } from '@/stores/book'
import { useWordStudyStore } from '@/stores/wordStudy'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const wordStore = useWordStore()
const bookStore = useBookStore()
const wordStudyStore = useWordStudyStore()
const authStore = useAuthStore()
const { success, error } = useToast()

// 状态
const showRangeModal = ref(false)
const markingGrasped = ref(false)
const markingError = ref(false)
const markingRound1 = ref(false)
const reviewMode = ref('en2cn') // 'en2cn' | 'cn2en'
const showAnswer = ref(false)
const initializing = ref(true)
const latestRecord = ref(null)
const rangeForm = ref({
  start: 1,
  end: 50
})

const currentStudyRange = computed(() => {
  return `${wordStore.learningRange.start}-${wordStore.learningRange.end}`
})
const round1Completed = computed(() => !!latestRecord.value?.round1ReviewTime)
const round1CompletedTimeText = computed(() => {
  if (!latestRecord.value?.round1ReviewTime) return ''
  return formatDateTime(latestRecord.value.round1ReviewTime)
})

// 计算属性
const loading = computed(() => wordStore.loading)
const currentWord = computed(() => wordStore.currentWord)
const currentIndex = computed(() => wordStore.currentIndex)
const totalWords = computed(() => wordStore.totalWords)
const hasNext = computed(() => wordStore.hasNext)
const hasPrev = computed(() => wordStore.hasPrev)
const currentBook = computed(() => bookStore.currentBook)

// 方法
const goBack = () => router.push('/word/review')

const ensureBookSelected = async (bookId) => {
  if (!bookId) {
    throw new Error('学习记录缺少课本信息')
  }
  if (bookStore.currentBook?.id === bookId) {
    return
  }
  if (!bookStore.books?.length) {
    await bookStore.fetchBooks()
  }
  const targetBook = bookStore.books?.find((book) => book.id === bookId)
  if (!targetBook) {
    throw new Error('未找到对应课本，请先在课本列表中选择')
  }
  await bookStore.selectBook(targetBook)
}

const loadWords = async (range = null) => {
  try {
    await wordStore.fetchWords(range)
    showAnswer.value = false
    if (wordStore.totalWords === 0) {
      error('当前范围内没有可复习的单词')
    }
  } catch (err) {
    error('加载单词失败：' + err.message)
    throw err
  }
}

const initializeFromLatestRecord = async () => {
  if (!authStore.user?.id) {
    initializing.value = false
    return
  }
  try {
    initializing.value = true
    const record = await wordStudyStore.getLatestRecord(authStore.user.id)
    if (!record) {
      error('未找到学习记录，请先完成一次学习会话')
      initializing.value = false
      return
    }
    latestRecord.value = record
    await ensureBookSelected(record.bookId)
    const range = {
      start: record.startId || 1,
      end: record.endId || 50
    }
    wordStore.setLearningRange(range)
    rangeForm.value = { ...range }
    await loadWords(range)
  } catch (err) {
    console.error('初始化复习数据失败:', err)
    error('初始化复习数据失败：' + (err.message || '请稍后重试'))
  } finally {
    initializing.value = false
  }
}

const toggleMode = () => {
  reviewMode.value = reviewMode.value === 'en2cn' ? 'cn2en' : 'en2cn'
  showAnswer.value = false
}

const nextWord = () => {
  wordStore.nextWord()
  showAnswer.value = false
}

const prevWord = () => {
  wordStore.prevWord()
  showAnswer.value = false
}

const toggleAnswer = () => {
  showAnswer.value = !showAnswer.value
}

const playPronunciation = () => {
  if (!currentWord.value) return
  const text = currentWord.value.word
  if ('speechSynthesis' in window) {
    window.speechSynthesis.cancel()
    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = 'en-US'
    window.speechSynthesis.speak(utterance)
  }
}

const markAsGrasped = async () => {
  try {
    markingGrasped.value = true
    await wordStore.markCurrentAsGrasped()
    success('已标记为掌握')
    if (hasNext.value) nextWord()
  } catch (err) {
    error('标记失败：' + err.message)
  } finally {
    markingGrasped.value = false
  }
}

const markAsError = async () => {
  try {
    markingError.value = true
    await wordStore.markCurrentAsError()
    success('已加入错词本')
    if (hasNext.value) nextWord()
  } catch (err) {
    error('标记失败：' + err.message)
  } finally {
    markingError.value = false
  }
}

const applyRange = async () => {
  if (rangeForm.value.start >= rangeForm.value.end) {
    return error('起始位置必须小于结束位置')
  }
  
  try {
    await loadWords(rangeForm.value)
    showRangeModal.value = false
    success(`已设置复习范围：${rangeForm.value.start}-${rangeForm.value.end}`)
  } catch (err) {
    // loadWords 内已有提示
  }
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  rangeForm.value = { ...wordStore.learningRange }
}

const markFirstRoundComplete = async () => {
  if (!latestRecord.value || markingRound1.value) return
  if (!authStore.user?.id) {
    error('请先登录后再标记复习完成')
    return
  }
  try {
    markingRound1.value = true
    await wordStudyStore.markReviewComplete({
      userId: authStore.user.id,
      sessionId: latestRecord.value.id,
      reviewRound: 1,
      completedTime: new Date()
    })
    latestRecord.value = {
      ...latestRecord.value,
      round1ReviewTime: new Date().toISOString()
    }
    success('第一轮复习已完成')
  } catch (err) {
    console.error('标记第一轮复习失败:', err)
    error('标记第一轮复习失败：' + (err.message || '请稍后重试'))
  } finally {
    markingRound1.value = false
  }
}

const formatDateTime = (value) => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  const pad = (num) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

// 键盘快捷键
const handleKeydown = (e) => {
  if (showRangeModal.value) return

  switch(e.code) {
    case 'Space': 
      e.preventDefault()
      playPronunciation()
      break
    case 'Enter':
      e.preventDefault()
      toggleAnswer()
      break
    case 'ArrowRight':
    case 'ArrowDown':
      e.preventDefault()
      nextWord()
      break
    case 'ArrowLeft':
    case 'ArrowUp':
      e.preventDefault()
      prevWord()
      break
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
  rangeForm.value = { ...wordStore.learningRange }
  initializeFromLatestRecord()
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  window.speechSynthesis.cancel()
})
</script>

<style scoped>
@keyframes fade-in {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in {
  animation: fade-in 0.2s ease-out forwards;
}

@keyframes scale-in {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-scale-in {
  animation: scale-in 0.2s ease-out forwards;
}
</style>
