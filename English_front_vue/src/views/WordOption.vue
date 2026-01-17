<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans overflow-hidden relative selection:bg-blue-100">
    <!-- 动态背景 -->
    <div class="absolute inset-0 z-0 overflow-hidden pointer-events-none">
      <div class="absolute top-[-10%] right-[-5%] w-[40%] h-[40%] bg-blue-200/30 rounded-full blur-[100px] animate-blob"></div>
      <div class="absolute bottom-[10%] left-[-10%] w-[35%] h-[35%] bg-indigo-200/30 rounded-full blur-[100px] animate-blob animation-delay-2000"></div>
    </div>

    <!-- 彩带效果 -->
    <transition-group name="confetti" tag="div" class="pointer-events-none absolute inset-0 z-30">
      <div
        v-for="piece in confettiPieces"
        :key="piece.id"
        class="confetti-piece"
        :style="{
          left: piece.left + '%',
          animationDelay: piece.delay + 's',
          animationDuration: piece.duration + 's',
          backgroundColor: piece.color
        }"
      ></div>
    </transition-group>

    <!-- 顶部导航 -->
    <nav class="relative z-40 px-4 sm:px-6 lg:px-10 h-16 sm:h-20 flex items-center justify-between">
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
          <h1 class="text-lg font-semibold text-slate-900 tracking-tight">词汇挑战</h1>
          <p class="flex items-center gap-1 text-xs text-slate-500">
            <span class="inline-block w-1.5 h-1.5 rounded-full" :class="gameActive ? 'bg-emerald-500 animate-pulse' : 'bg-slate-400'"></span>
            速记模式 · 集中训练
          </p>
        </div>
      </div>

      <div class="flex items-center gap-2 sm:gap-3">
        <!-- 进度 -->
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500">
          <span>进度</span>
          <span class="text-blue-600 font-bold">{{ progressCurrent }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ progressTotal }}</span>
        </div>
        <!-- 范围 -->
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500">
          <span>当前范围</span>
          <span class="text-blue-600 font-bold">{{ studyRange }}</span>
        </div>
        <!-- 当前课本 -->
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500 max-w-[220px]">
          <span>当前课本</span>
          <span class="text-blue-600 font-bold truncate" :title="currentBookName">{{ currentBookName }}</span>
        </div>
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500">
          <span>耗时</span>
          <span class="text-blue-600 font-bold">{{ gameActive ? timeLeft : '--' }}s</span>
        </div>
        <div class="flex items-center gap-2 px-3 py-1.5 bg-slate-900 text-white rounded-xl text-xs font-semibold">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          得分 {{ score }}
        </div>
      </div>
    </nav>

    <main class="flex-1 relative w-full max-w-5xl mx-auto p-4 md:p-6 flex flex-col justify-center min-h-[600px]">
      
      <!-- 准备界面 -->
      <transition name="fade" mode="out-in">
        <div v-if="!gameActive && !isGameOver" class="flex flex-col items-center justify-center text-center w-full max-w-lg mx-auto bg-white/80 backdrop-blur-xl p-8 md:p-12 rounded-[2.5rem] shadow-2xl border border-white">
            <div class="w-20 h-20 bg-gradient-to-tr from-blue-500 to-cyan-500 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-lg shadow-blue-500/30 transform -rotate-6">
              <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            </div>
            <h2 class="text-3xl font-bold text-slate-900 mb-3">单词速记挑战</h2>
            <p class="text-slate-500 mb-8 leading-relaxed">
              在有限时间内，从选项中选出正确的释义。<br>
              挑战你的记忆极限！
            </p>
            
            <div class="w-full space-y-4">
              <div class="flex justify-center gap-4 mb-6">
                <button 
                  v-for="d in difficulties" 
                  :key="d.value"
                  @click="difficulty = d.value"
                  class="px-4 py-2 rounded-xl text-sm font-bold transition-all border-2"
                  :class="difficulty === d.value ? 'border-blue-500 bg-blue-50 text-blue-600' : 'border-slate-100 bg-white text-slate-400 hover:border-slate-200'"
                >
                  {{ d.label }}
                </button>
              </div>

              <button 
                @click="startGame"
                class="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-2xl font-bold text-lg shadow-xl shadow-blue-500/30 hover:shadow-blue-500/40 hover:-translate-y-1 transition-all active:translate-y-0"
              >
                开始挑战
              </button>
            </div>
        </div>

        <!-- 游戏界面 -->
        <div v-else-if="gameActive" class="w-full h-full flex flex-col md:flex-row gap-6 items-stretch">
            
            <!-- 左侧：单词卡片 -->
            <div class="flex-1 bg-white/80 backdrop-blur-xl rounded-[2rem] p-8 shadow-xl border border-white flex flex-col items-center justify-center text-center relative overflow-hidden group">
                <div class="absolute top-0 left-0 w-full h-2 bg-slate-100">
                  <div class="h-full bg-blue-500 transition-all duration-300" :style="{ width: `${progressPercent}%` }"></div>
                </div>

                <div class="mb-2 text-sm font-bold text-blue-500/80 tracking-widest uppercase">Word {{ displayWordIndex }} / {{ totalGameWords }}</div>
                
                <h3 class="text-4xl md:text-6xl font-black text-slate-800 mb-4 break-all animate-scale-in">
                  {{ gameMode === 'word-to-chinese' ? currentWord?.word : currentWord?.chinese }}
                </h3>
                
                <div class="flex items-center gap-3 text-slate-500 text-xl font-serif italic mb-8">
                  <span>/{{ currentWord?.pronounce }}/</span>
                  <button @click="playAudio(currentWord?.word)" class="p-2 rounded-full hover:bg-slate-100 transition-colors">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" /></svg>
                  </button>
                </div>

                <!-- 结果反馈 -->
                <div v-if="lastResult" class="absolute bottom-8 left-1/2 -translate-x-1/2 px-6 py-2 rounded-full font-bold text-white shadow-lg animate-bounce-small"
                   :class="lastResult === 'correct' ? 'bg-emerald-500' : 'bg-rose-500'">
                   {{ lastResult === 'correct' ? 'Correct!' : 'Oops!' }}
                </div>
            </div>

            <!-- 右侧：选项面板 -->
            <div class="flex-1 flex flex-col justify-center gap-4">
               <button 
                 v-for="(option, idx) in currentOptions"
                 :key="idx"
                 @click="handleOptionClick(option)"
                 :disabled="showResult"
                 class="group relative w-full p-5 bg-white/70 backdrop-blur-md border-2 border-white rounded-2xl text-left transition-all duration-200 hover:-translate-y-1 hover:shadow-lg disabled:cursor-not-allowed disabled:transform-none"
                 :class="getOptionClass(option)"
               >
                 <div class="flex items-center gap-4">
                   <div class="w-10 h-10 rounded-full flex items-center justify-center text-lg font-bold shrink-0 transition-colors"
                     :class="getOptionLetterClass(option, idx)">
                     {{ String.fromCharCode(65 + idx) }}
                   </div>
                   <span class="text-lg text-slate-700 font-medium group-hover:text-slate-900 transition-colors">
                     {{ gameMode === 'word-to-chinese' ? option.chinese : option.word }}
                   </span>
                 </div>
                 
                 <!-- 正确/错误图标 -->
                 <div v-if="showResult && option.id === currentWord.id" class="absolute right-4 top-1/2 -translate-y-1/2 text-emerald-500">
                   <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
                 </div>
               </button>
            </div>
        </div>

        <!-- 结算界面 -->
        <div v-else class="flex flex-col items-center justify-center text-center w-full max-w-lg mx-auto bg-white/90 backdrop-blur-xl p-8 md:p-12 rounded-[2.5rem] shadow-2xl border border-white animate-scale-in">
            <h2 class="text-3xl font-bold text-slate-800 mb-2">{{ gameOverTitle }}</h2>
            <p class="text-slate-500 mb-8">{{ gameOverMessage }}</p>
            
            <div class="grid grid-cols-2 gap-4 mb-8 w-full">
              <div class="bg-blue-50/50 border border-blue-100 p-4 rounded-2xl">
                <div class="text-xs font-bold text-blue-400 uppercase tracking-wider mb-1">Score</div>
                <div class="text-3xl font-black text-blue-600">{{ score }}</div>
              </div>
              <div class="bg-emerald-50/50 border border-emerald-100 p-4 rounded-2xl">
                <div class="text-xs font-bold text-emerald-500 uppercase tracking-wider mb-1">Accuracy</div>
                <div class="text-3xl font-black text-emerald-600">{{ accuracy }}%</div>
              </div>
            </div>

            <div v-if="gameResultType === 'completed' && motivationalLine" class="w-full mb-6 p-4 bg-gradient-to-r from-indigo-50 to-blue-50 border border-indigo-100 rounded-2xl">
              <p class="text-indigo-600 font-semibold">{{ motivationalLine }}</p>
            </div>

            <div v-if="gameResultType === 'completed' && latestRecord" class="w-full mb-8 p-4 bg-slate-50 border border-slate-200 rounded-2xl text-left space-y-3">
              <p class="text-slate-600 text-sm">
                当前学习记录：<span class="font-semibold text-slate-900">{{ studyRange }}</span>
              </p>
              <template v-if="showRound2Button">
                <p class="text-slate-700 text-sm">第二轮复习尚未标记完成，点击下方按钮即可同步。</p>
                <button
                  @click="markSecondRoundComplete"
                  :disabled="markingRound2"
                  class="w-full py-3 rounded-xl font-semibold text-white bg-emerald-500 hover:bg-emerald-600 transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
                >
                  {{ markingRound2 ? '标记中...' : '标记第二轮复习完成' }}
                </button>
              </template>
              <template v-else>
                <p class="text-emerald-600 font-semibold">你已经完成第二轮复习，加油哦！追梦人！</p>
              </template>
            </div>
            
            <div class="flex gap-4 w-full">
              <button 
                @click="goBack"
                class="flex-1 py-3.5 bg-slate-100 text-slate-600 rounded-xl font-bold hover:bg-slate-200 transition-colors"
              >
                返回首页
              </button>
              <button 
                @click="restartGame"
                class="flex-1 py-3.5 bg-blue-600 text-white rounded-xl font-bold shadow-lg shadow-blue-500/25 hover:bg-blue-700 transition-colors"
              >
                再来一局
              </button>
            </div>
        </div>
      </transition>
    </main>

    <transition name="fade">
      <div v-if="showTimeUpModal" class="fixed inset-0 bg-slate-900/70 backdrop-blur-sm z-50 flex items-center justify-center px-4">
        <div class="bg-white rounded-3xl p-6 max-w-md w-full text-center shadow-2xl border border-slate-100">
          <div class="w-16 h-16 mx-auto rounded-2xl bg-rose-100 text-rose-500 flex items-center justify-center mb-4">
            <svg class="w-8 h-8" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.6" d="M12 6v6l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <h3 class="text-2xl font-bold text-slate-900 mb-2">时间到啦！</h3>
          <p class="text-slate-500 mb-6">
            还有 {{ remainingWordsCount }} 个单词等待你复习，想继续完成它们吗？
          </p>
          <div class="flex flex-col gap-3">
            <button
              @click="continueGameAfterTimeout"
              class="w-full py-3 rounded-2xl bg-blue-600 text-white font-semibold shadow-lg shadow-blue-500/30 hover:bg-blue-700 transition-colors"
            >
              继续游戏
            </button>
            <button
              @click="stopAfterTimeout"
              class="w-full py-3 rounded-2xl bg-slate-100 text-slate-600 font-semibold hover:bg-slate-200 transition-colors"
            >
              结束本轮
            </button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 首次学习提示弹窗 -->
    <div v-if="firstLearnDialog" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm"></div>
      <div class="relative w-full max-w-md bg-white rounded-3xl shadow-2xl p-6 sm:p-8 space-y-5">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M12 2a10 10 0 100 20 10 10 0 000-20z" />
            </svg>
          </div>
          <div>
            <h3 class="text-lg font-bold text-slate-900">首次学习提示</h3>
            <p class="text-sm text-slate-500 mt-1">{{ firstLearnDialogMessage }}</p>
          </div>
        </div>
        <div class="flex flex-col sm:flex-row gap-3">
          <button
            @click="cancelFirstLearn"
            class="flex-1 px-5 py-3 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors"
          >
            取消
          </button>
          <button
            @click="confirmFirstLearn"
            class="flex-1 px-5 py-3 rounded-xl font-bold text-white bg-amber-500 hover:bg-amber-600 shadow-lg shadow-amber-500/30 transition-colors"
          >
            前往学习
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useWordStore } from '@/stores/word'
import { useBookStore } from '@/stores/book'
import { useAuthStore } from '@/stores/auth'
import { useWordStudyStore } from '@/stores/wordStudy'
import { useToast } from '@/composables/useToast'
import axios from 'axios'

const router = useRouter()
const wordStore = useWordStore()
const bookStore = useBookStore()
const authStore = useAuthStore()
const wordStudyStore = useWordStudyStore()
const { error: showError, success: showSuccess } = useToast()
const { words } = storeToRefs(wordStore)

const difficulties = [
  { label: '简单180s', value: 'easy', time: 180, optionCount: 4 },
  { label: '普通120s', value: 'normal', time: 120, optionCount: 5 },
  { label: '困难100s', value: 'hard', time: 100, optionCount: 6 }
]

const modes = [
  { label: '英译中', value: 'word-to-chinese' },
  { label: '中译英', value: 'chinese-to-word' }
]

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const motivationalQuotes = [
  '你的坚持，正在悄悄惊艳所有人！',
  '离目标只差一步，坚持就是胜利！',
  '再接再厉，下一次会更快更准！',
  '你在用行动证明——努力的人最耀眼！'
]

const confettiColors = ['#f97316', '#facc15', '#34d399', '#60a5fa', '#a855f7', '#fb7185']

const difficulty = ref('normal')
const gameMode = ref('word-to-chinese')
const gameActive = ref(false)
const isGameOver = ref(false)
const timeLeft = ref(60)
const score = ref(0)
const currentWord = ref(null)
const currentOptions = ref([])
const showResult = ref(false)
const selectedOptionId = ref(null)
const lastResult = ref(null)
const correctCount = ref(0)
const wrongCount = ref(0)
const gameWords = ref([])
const initializing = ref(true)
const latestRecord = ref(null)
const firstLearnDialog = ref(false)
const firstLearnDialogMessage = ref('当前课本还未学习，请前往学习中心学习。')
const completedWordIds = ref(new Set())
const initialWordCount = ref(0)
const showTimeUpModal = ref(false)
const motivationalLine = ref('')
const confettiPieces = ref([])
const markingRound2 = ref(false)
const gameResultType = ref(null)
const timerExpired = ref(false)

let timerInterval = null

const totalGameWords = computed(() => initialWordCount.value || words.value.length || 0)
const currentBookName = computed(() => bookStore.currentBook?.bookName || bookStore.currentBook?.name || '未选择课本')
const optionCount = computed(() => {
  const diff = difficulties.find(d => d.value === difficulty.value)
  return diff?.optionCount || 4
})
const studyRange = computed(() => `${wordStore.learningRange.start}-${wordStore.learningRange.end}`)
const progressCurrent = computed(() => completedWordIds.value.size || 0)
const progressTotal = computed(() => totalGameWords.value || 0)
const progressPercent = computed(() => {
  if (!progressTotal.value) return 0
  return Math.min((progressCurrent.value / progressTotal.value) * 100, 100)
})
const displayWordIndex = computed(() => {
  if (!totalGameWords.value) return 0
  return Math.min(progressCurrent.value + 1, totalGameWords.value)
})
const showRound2Button = computed(() => !!latestRecord.value && !latestRecord.value.round2ReviewTime)
const remainingWordsCount = computed(() => {
  if (!initialWordCount.value) return 0
  return Math.max(initialWordCount.value - progressCurrent.value, 0)
})

const accuracy = computed(() => {
  const total = correctCount.value + wrongCount.value
  if (total === 0) return 0
  return Math.round((correctCount.value / total) * 100)
})

const gameOverTitle = computed(() => {
  if (gameResultType.value === 'completed') return '全部掌握！'
  if (gameResultType.value === 'timeout') return '时间到！'
  return '挑战结束'
})

const gameOverMessage = computed(() => {
  if (gameResultType.value === 'completed') {
    return motivationalLine.value || '太棒了！你的词汇量令人惊叹！'
  }
  if (gameResultType.value === 'timeout') {
    return remainingWordsCount.value
      ? `还有 ${remainingWordsCount.value} 个单词等待被复习，随时可以继续冲刺！`
      : '时间用尽，本轮挑战告一段落。'
  }
  const acc = accuracy.value
  if (acc >= 90) return '太棒了！你的词汇量令人惊叹！'
  if (acc >= 60) return '表现不错，继续加油！'
  return '别灰心，熟能生巧！'
})

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
  const targetBook = bookStore.books.find((book) => book.id === bookId)
  if (!targetBook) {
    throw new Error('最新学习记录关联的课本未在列表中，请先到课本页面选择')
  }
  await bookStore.selectBook(targetBook)
}

const fetchLatestRecordSafe = async (userId, bookId) => {
  const authHeader = authStore.token ? { Authorization: `Bearer ${authStore.token}` } : {}
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'https://119.91.203.83:8080'
  try {
    const res = await axios.get(`${baseURL}/api/word-study/latest-record/${userId}`, {
      params: bookId ? { bookId } : {},
      headers: authHeader,
      validateStatus: (status) => status >= 200 && status < 500
    })
    const { code, data } = res.data || {}
    if (code === 1) return data
    if (code === 0 && res.data?.msg?.includes('未找到学习记录')) return null
    throw new Error(res.data?.msg || '获取学习记录失败')
  } catch (err) {
    const msg = err?.message || ''
    if (msg.includes('未找到学习记录')) return null
    throw err
  }
}

const preloadLatestWords = async () => {
  if (!authStore.user?.id) {
    initializing.value = false
    return
  }
  try {
    initializing.value = true

    if (!bookStore.currentBook?.id) {
      firstLearnDialogMessage.value = '请先选择课本后再开始挑战。'
      firstLearnDialog.value = true
      latestRecord.value = null
      return
    }

    const record = await fetchLatestRecordSafe(authStore.user.id, bookStore.currentBook?.id)
    if (!record) {
      firstLearnDialogMessage.value = '当前课本还未学习，请前往学习中心学习。'
      firstLearnDialog.value = true
      latestRecord.value = null
      return
    }
    
    await ensureBookSelected(record.bookId)
    const range = {
      start: record.startId || 1,
      end: record.endId || 50
    }
    await wordStore.fetchWords(range)
    latestRecord.value = record
  } catch (e) {
    console.error('加载最新学习记录失败', e)
    firstLearnDialogMessage.value = '加载学习记录失败，请前往学习中心重新开始。'
    firstLearnDialog.value = true
    latestRecord.value = null
  } finally {
    initializing.value = false
  }
}

const initGameData = async () => {
  if (initializing.value) {
    await preloadLatestWords()
    return
  }
  if (words.value.length === 0) {
    await preloadLatestWords()
  }
}

const resetRuntimeState = () => {
  showResult.value = false
  selectedOptionId.value = null
  lastResult.value = null
  motivationalLine.value = ''
  confettiPieces.value = []
  gameResultType.value = null
  showTimeUpModal.value = false
  timerExpired.value = false
}

const startGame = async () => {
  await initGameData()
  if (firstLearnDialog.value) return
  if (initializing.value) {
    showError('数据加载中，请稍候')
    return
  }
  if (!latestRecord.value) {
    // 已在弹窗提示，不再额外 toast
    return
  }
  if (words.value.length < 1) {
    showError('单词数量不足，无法开始挑战')
    return
  }

  const diff = difficulties.find(d => d.value === difficulty.value)
  timeLeft.value = diff?.time || 60

  const allWords = [...words.value].sort(() => Math.random() - 0.5)
  initialWordCount.value = allWords.length
  gameWords.value = [...allWords]
  completedWordIds.value = new Set()

  gameActive.value = true
  isGameOver.value = false
  score.value = 0
  correctCount.value = 0
  wrongCount.value = 0
  currentWord.value = null
  resetRuntimeState()

  startTimer()
  loadQuestion()
}

const startTimer = () => {
  if (timerInterval) clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 0) {
      handleTimeExpired()
    }
  }, 1000)
}

const handleTimeExpired = () => {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
  timeLeft.value = 0
  timerExpired.value = true
  showTimeUpModal.value = true
}

const continueGameAfterTimeout = () => {
  showTimeUpModal.value = false
}

const stopAfterTimeout = () => {
  showTimeUpModal.value = false
  if (!isGameOver.value) {
    endGame('timeout')
  }
}

const switchMode = (mode) => {
  if (gameMode.value === mode) return
  gameMode.value = mode
  if (gameActive.value) {
    loadQuestion()
  }
}

const loadQuestion = () => {
  if (!gameActive.value) return
  showResult.value = false
  selectedOptionId.value = null
  lastResult.value = null

  if (!gameWords.value.length) {
    handleAllWordsMastered()
    return
  }

  currentWord.value = gameWords.value[0]
  generateOptions()
}

const generateOptions = () => {
  if (!currentWord.value) return
  const desired = Math.min(optionCount.value, Math.max(words.value.length, optionCount.value))
  const options = new Map()
  options.set(currentWord.value.id, currentWord.value)

  let guard = 0
  while (options.size < desired && guard < 200) {
    guard++
    const randomWord = words.value[Math.floor(Math.random() * words.value.length)]
    if (randomWord && randomWord.id !== currentWord.value.id && !options.has(randomWord.id)) {
      options.set(randomWord.id, randomWord)
    }
  }

  currentOptions.value = Array.from(options.values()).sort(() => Math.random() - 0.5)
}

const scheduleNextQuestion = (isCorrect) => {
  const servedWord = gameWords.value.shift()
  if (!servedWord) {
    handleAllWordsMastered()
    return
  }

  if (!isCorrect) {
    let insertIndex = Math.floor(Math.random() * (gameWords.value.length + 1))
    if (gameWords.value.length > 0) {
      insertIndex = Math.max(1, insertIndex)
    }
    gameWords.value.splice(insertIndex, 0, servedWord)
  } else {
    completedWordIds.value = new Set([...completedWordIds.value, servedWord.id])
  }

  if (!gameWords.value.length) {
    handleAllWordsMastered()
  } else {
    loadQuestion()
  }
}

const handleAllWordsMastered = () => {
  if (gameResultType.value === 'completed') return
  motivationalLine.value = motivationalQuotes[Math.floor(Math.random() * motivationalQuotes.length)]
  triggerConfetti()
  endGame('completed')
}

const triggerConfetti = () => {
  const pieces = Array.from({ length: 28 }).map((_, idx) => ({
    id: Date.now() + idx,
    left: Math.random() * 100,
    delay: Math.random() * 0.3,
    duration: 2 + Math.random() * 1.5,
    color: confettiColors[Math.floor(Math.random() * confettiColors.length)]
  }))
  confettiPieces.value = pieces

  setTimeout(() => {
    confettiPieces.value = []
  }, 3500)
}

const handleOptionClick = (option) => {
  if (showResult.value || !currentWord.value) return

  showResult.value = true
  selectedOptionId.value = option.id

  const isCorrect = option.id === currentWord.value.id

  if (isCorrect) {
    score.value += 10
    correctCount.value++
    lastResult.value = 'correct'
  } else {
    wrongCount.value++
    lastResult.value = 'wrong'
  }

  setTimeout(() => {
    showResult.value = false
    scheduleNextQuestion(isCorrect)
  }, 500)
}

const restartGame = () => {
  startGame()
}

const endGame = (resultType = 'timeout') => {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
  gameResultType.value = resultType
  gameActive.value = false
  isGameOver.value = true
  currentWord.value = null
}

const playAudio = (text) => {
  if (!text) return
  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'en-US'
  window.speechSynthesis.speak(utterance)
}

const getOptionClass = (option) => {
  if (!showResult.value) return 'border-white hover:border-blue-200'

  if (option.id === currentWord.value?.id) {
    return 'bg-emerald-50 border-emerald-500 ring-1 ring-emerald-500'
  }

  if (option.id === selectedOptionId.value && option.id !== currentWord.value?.id) {
    return 'bg-rose-50 border-rose-500 ring-1 ring-rose-500'
  }

  return 'opacity-50 border-white'
}

const getOptionLetterClass = (option, idx) => {
  if (!showResult.value) {
    const colors = ['bg-blue-100 text-blue-600', 'bg-purple-100 text-purple-600', 'bg-amber-100 text-amber-600', 'bg-pink-100 text-pink-600']
    return colors[idx % 4]
  }

  if (option.id === currentWord.value?.id) {
    return 'bg-emerald-500 text-white'
  }

  if (option.id === selectedOptionId.value) {
    return 'bg-rose-500 text-white'
  }

  return 'bg-slate-100 text-slate-400'
}

const markSecondRoundComplete = async () => {
  if (!latestRecord.value || markingRound2.value) return
  try {
    markingRound2.value = true
    await wordStudyStore.markReviewComplete({
      userId: authStore.user?.id,
      sessionId: latestRecord.value.id,
      reviewRound: 2
      // 移除completedTime，让后端使用服务器时间
    })
    // 重新获取记录以获得正确的时间
    const updatedRecord = await wordStudyStore.getLatestRecord(authStore.user.id, bookStore.currentBook?.id)
    if (updatedRecord) {
      latestRecord.value = updatedRecord
    }
    showSuccess('第二轮复习已完成')
  } catch (error) {
    console.error('标记第二轮复习失败', error)
    showError('标记第二轮复习失败：' + (error.message || '请稍后重试'))
  } finally {
    markingRound2.value = false
  }
}

onMounted(() => {
  preloadLatestWords()
})

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
  }
})

const confirmFirstLearn = () => {
  firstLearnDialog.value = false
  router.push('/word/learning')
}

const cancelFirstLearn = () => {
  firstLearnDialog.value = false
  goBack()
}
</script>

<style scoped>
.animate-blob {
  animation: blob 7s infinite;
}
.animation-delay-2000 {
  animation-delay: 2s;
}
@keyframes blob {
  0% { transform: translate(0px, 0px) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0px, 0px) scale(1); }
}

.animate-scale-in {
  animation: scaleIn 0.3s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}
@keyframes scaleIn {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.animate-bounce-small {
  animation: bounceSmall 0.5s infinite;
}
@keyframes bounceSmall {
  0%, 100% { transform: translate(-50%, 0); }
  50% { transform: translate(-50%, -5px); }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
