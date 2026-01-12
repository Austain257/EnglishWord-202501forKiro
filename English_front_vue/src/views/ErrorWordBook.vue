<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- 背景装饰，融合学习&听写页面 -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-rose-50/80 via-blue-50/60 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 -right-16 w-96 h-96 bg-sky-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-48 -left-24 w-80 h-80 bg-rose-100/40 rounded-full blur-3xl pointer-events-none"></div>

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
          <h1 class="text-lg font-bold text-slate-900 tracking-tight">错词本</h1>
          <div class="flex items-center gap-2 text-xs text-slate-500">
            <span class="inline-block w-1.5 h-1.5 rounded-full bg-rose-500"></span>
            <span>{{ currentBook?.name || '我的错词本' }}</span>
          </div>
        </div>
      </div>

      <div class="flex items-center gap-3">
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>进度</span>
          <span class="font-bold text-rose-600">{{ totalWords === 0 ? 0 : currentIndex + 1 }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ totalWords }}</span>
        </div>

        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600 max-w-[220px]">
          <span>当前课本</span>
          <span class="font-bold text-rose-600 truncate" :title="currentBookName">{{ currentBookName }}</span>
        </div>

        <div class="relative bg-white/80 backdrop-blur-sm rounded-2xl border border-slate-200/60 p-1 flex items-center text-xs font-semibold">
          <button
            @click="setMode('learning')"
            :class="['px-3 sm:px-4 py-1.5 rounded-xl transition-all', mode === 'learning' ? 'bg-white text-rose-600 shadow-sm' : 'text-slate-500 hover:text-slate-900']"
          >
            错词学习
          </button>
          <button
            @click="setMode('dictation')"
            :class="['px-3 sm:px-4 py-1.5 rounded-xl transition-all', mode === 'dictation' ? 'bg-white text-sky-600 shadow-sm' : 'text-slate-500 hover:text-slate-900']"
          >
            错词听写
          </button>
        </div>

        <button
          @click="reload"
          class="hidden sm:flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-slate-600 hover:text-rose-600 hover:border-rose-200 transition-all active:scale-95"
        >
          重新加载
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v6h6M20 20v-6h-6M5 19A9 9 0 0119 5" />
          </svg>
        </button>
      </div>
    </nav>

    <!-- 主体 -->
    <main class="flex-1 relative z-10 flex flex-col max-w-4xl mx-auto w-full px-4 sm:px-6 lg:px-8 pb-8">
      <!-- 进度条 (移动端) -->
      <div class="sm:hidden mb-6 px-1">
        <div class="flex justify-between text-xs font-medium text-slate-500 mb-2">
          <span>已掌握进度</span>
          <span>{{ progress }}%</span>
        </div>
        <div class="h-2 bg-slate-100 rounded-full overflow-hidden">
          <div class="h-full bg-gradient-to-r from-rose-500 to-sky-500 rounded-full transition-all duration-300 ease-out" :style="{ width: `${progress}%` }"></div>
        </div>
      </div>

      <!-- 状态区域 -->
      <div v-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[360px]">
        <div class="w-12 h-12 border-4 border-rose-100 border-t-rose-500 rounded-full animate-spin mb-4"></div>
        <p class="text-slate-400 text-sm font-medium animate-pulse">正在拉取错词列表...</p>
      </div>

      <div v-else-if="!currentWord" class="flex-1 flex flex-col items-center justify-center min-h-[360px] text-center">
        <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-3-3v6m9-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <h3 class="text-xl font-bold text-slate-900 mb-2">当前词书暂无错词</h3>
        <p class="text-slate-500 mb-8 max-w-xs mx-auto">继续保持！若已做更改请重新加载或前往学习/听写页面刷新数据。</p>
        <button
          @click="reload"
          class="px-8 py-3 bg-gradient-to-r from-rose-500 to-sky-500 hover:opacity-90 text-white rounded-xl font-semibold shadow-lg shadow-rose-300/50 transition-all hover:-translate-y-0.5"
        >
          重新加载
        </button>
      </div>

      <!-- 错词学习模式 -->
      <div v-else-if="mode === 'learning'" class="flex-1 flex flex-col justify-center py-6">
        <div class="relative group perspective-1000 w-full max-w-2xl mx-auto">
          <div class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500">
            <div class="absolute top-6 right-6">
              <span
                class="px-3 py-1 rounded-full text-xs font-bold tracking-wide border"
                :class="currentWord.isGrasp === 1 ? 'bg-emerald-50 text-emerald-600 border-emerald-100' : 'bg-rose-50 text-rose-600 border-rose-100'"
              >
                {{ currentWord.isGrasp === 1 ? '已掌握' : '待攻克' }}
              </span>
            </div>

            <div class="p-8 sm:p-12 flex flex-col items-center text-center">
              <h2 class="text-5xl sm:text-6xl font-bold text-slate-900 mb-4 tracking-tight">
                {{ currentWord.word }}
              </h2>

              <div class="flex items-center gap-3 mb-10">
                <span class="text-xl text-slate-500 font-serif italic">
                  {{ currentWord.phonetic || currentWord.pronounce || '...' }}
                </span>
                <button
                  @click="playPronunciation"
                  class="p-2 rounded-full bg-rose-50 text-rose-600 hover:bg-rose-100 hover:scale-110 transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-rose-500"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                  </svg>
                </button>
              </div>

              <div class="w-full max-w-lg mx-auto mt-4">
                <div class="text-xl sm:text-2xl text-slate-600 font-medium leading-relaxed px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100/50">
                  {{ currentWord.chinese || '暂无释义' }}
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4 mt-8 w-full max-w-sm">
                <div class="flex flex-col items-center p-3 bg-slate-50 rounded-xl border border-slate-100">
                  <span class="text-xs text-slate-400 uppercase font-bold tracking-wider mb-1">累计错误</span>
                  <span class="text-lg font-bold text-rose-500">{{ currentWord.errorTimes || 0 }} 次</span>
                </div>
                <div class="flex flex-col items-center p-3 bg-slate-50 rounded-xl border border-slate-100">
                  <span class="text-xs text-slate-400 uppercase font-bold tracking-wider mb-1">最后出现</span>
                  <span class="text-lg font-bold text-slate-700">{{ currentWord.times || 0 }} 次</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 控制按钮 -->
        <div class="mt-8 flex flex-col sm:flex-row items-stretch sm:items-center justify-center gap-3 sm:gap-6">
          <button
            @click="prevWord"
            :disabled="!hasPrev"
            class="w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-4 rounded-2xl font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed text-slate-600 bg-white border border-slate-200 shadow-sm hover:bg-slate-50 active:scale-95"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
            上一个
          </button>

          <button
            @click="handleNotGrasped"
            class="w-full sm:flex-1 px-6 py-4 rounded-2xl font-bold text-rose-600 bg-rose-50 border border-rose-100 hover:bg-rose-100 transition-all active:scale-95"
          >
            还不会
          </button>

          <button
            @click="handleGrasped"
            class="w-full sm:flex-1 px-6 py-4 rounded-2xl font-bold text-white bg-gradient-to-r from-rose-500 to-sky-500 shadow-lg shadow-rose-300/40 hover:opacity-90 transition-all active:scale-95"
          >
            标记已掌握
          </button>

          <button
            @click="nextWord"
            :disabled="!hasNext"
            class="w-full sm:w-auto flex items-center justify-center gap-2 px-8 py-4 rounded-2xl font-bold text-white bg-slate-900 hover:bg-slate-800 transition-all disabled:opacity-40 disabled:cursor-not-allowed active:scale-95"
          >
            下一个
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </div>

      <!-- 错词听写模式 -->
      <div v-else class="flex-1 flex flex-col justify-center py-6">
        <div class="relative w-full max-w-2xl mx-auto">
          <div class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500">
            <div class="p-8 sm:p-12 flex flex-col items-center text-center">
              <div class="mb-8 w-full min-h-[80px] flex items-center justify-center">
                <div class="flex flex-col items-center">
                  <button
                    @click="playPronunciation"
                    :disabled="isPlaying"
                    class="w-20 h-20 rounded-full bg-sky-500 text-white shadow-lg shadow-sky-500/40 flex items-center justify-center transition-all hover:scale-105 active:scale-95 disabled:opacity-60 disabled:cursor-not-allowed"
                  >
                    <svg v-if="!isPlaying" class="w-10 h-10" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M6.3 2.841A1.5 1.5 0 004 4.11V15.89a1.5 1.5 0 002.3 1.269l9.344-5.89a1.5 1.5 0 000-2.538L6.3 2.84z" />
                    </svg>
                    <svg v-else class="w-10 h-10 sound-wave" viewBox="0 0 24 24">
                      <line class="sound-wave-line" x1="4" y1="8" x2="4" y2="16"></line>
                      <line class="sound-wave-line" x1="8" y1="4" x2="8" y2="20"></line>
                      <line class="sound-wave-line" x1="12" y1="10" x2="12" y2="14"></line>
                      <line class="sound-wave-line" x1="16" y1="6" x2="16" y2="18"></line>
                      <line class="sound-wave-line" x1="20" y1="11" x2="20" y2="13"></line>
                    </svg>
                  </button>
                  <p class="mt-4 text-sm text-slate-400 font-medium">点击播放发音</p>
                </div>
              </div>

              <div class="w-full max-w-sm mx-auto relative">
                <input
                  ref="inputRef"
                  v-model="userInput"
                  type="text"
                  placeholder="输入你听到的单词..."
                  class="w-full text-center text-2xl font-bold text-slate-800 px-6 py-4 bg-slate-50 border-2 rounded-2xl transition-all focus:outline-none focus:bg-white"
                  :class="{
                    'border-slate-100 focus:border-sky-500': !showAnswer,
                    'border-emerald-500 bg-emerald-50 text-emerald-700 animate-shake-correct': showAnswer && isCorrect,
                    'border-rose-500 bg-rose-50 text-rose-800 animate-shake-wrong': showAnswer && !isCorrect
                  }"
                  @keyup.enter="checkAnswer"
                  :disabled="showAnswer"
                />
              </div>

              <div class="mt-6 min-h-[60px] flex flex-col items-center justify-center">
                <div v-if="showAnswer" class="flex flex-col items-center animate-fade-in">
                  <p class="text-2xl font-bold" :class="isCorrect ? 'text-emerald-600' : 'text-rose-600'">
                    {{ currentWord.word }}
                  </p>
                  <p class="mt-1 text-slate-500">{{ currentWord.chinese }}</p>
                </div>
              </div>
            </div>

            <div class="bg-slate-50/60 border-t border-slate-100 p-6">
              <div v-if="!showAnswer" class="flex gap-4">
                <button
                  @click="handleDictationWrong"
                  class="w-1/3 py-4 rounded-xl font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-all active:scale-95"
                >
                  我不会
                </button>
                <button
                  @click="checkAnswer"
                  :disabled="!userInput.trim()"
                  class="flex-1 py-4 rounded-xl font-bold text-white bg-sky-500 shadow-lg shadow-sky-500/30 hover:bg-sky-600 transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  检查答案
                </button>
              </div>
              <div v-else class="flex gap-4">
                <button
                  @click="handleGrasped"
                  class="w-1/3 py-4 rounded-xl font-bold text-white bg-emerald-500 hover:bg-emerald-600 transition-all active:scale-95"
                >
                  标记掌握
                </button>
                <button
                  @click="nextWord"
                  class="flex-1 py-4 rounded-xl font-bold text-white bg-slate-800 hover:bg-slate-900 transition-all active:scale-95"
                >
                  {{ hasNext ? '下一个' : '完成重练' }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import { wordService } from '@/services/word.service'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const authStore = useAuthStore()
const bookStore = useBookStore()
const { success, error, info } = useToast()

const loading = ref(false)
const words = ref([])
const currentIndex = ref(0)
const mode = ref('learning') // learning | dictation
const userInput = ref('')
const showAnswer = ref(false)
const isCorrect = ref(false)
const isPlaying = ref(false)
const inputRef = ref(null)

const currentWord = computed(() => words.value[currentIndex.value] || null)
const currentBookName = computed(() => bookStore.currentBook?.bookName || bookStore.currentBook?.name || '未选择课本')
const totalWords = computed(() => words.value.length)
const hasNext = computed(() => currentIndex.value < totalWords.value - 1)
const hasPrev = computed(() => currentIndex.value > 0)
const progress = computed(() => {
  if (totalWords.value === 0) return 0
  return Math.round(((currentIndex.value + 1) / totalWords.value) * 100)
})
const currentBook = computed(() => bookStore.currentBook)

const ensureContext = () => {
  if (!authStore.user?.id) throw new Error('用户未登录')
  if (!bookStore.currentBookId) throw new Error('请先选择词书')
}

const loadErrorWords = async () => {
  try {
    ensureContext()
    loading.value = true
    const res = await wordService.getErrorWordList({
      userId: authStore.user.id,
      bookId: bookStore.currentBookId
    })
    words.value = Array.isArray(res.data) ? res.data : []
    currentIndex.value = 0
    resetDictationState()
  } catch (err) {
    console.error(err)
    error(err.message || '获取错词列表失败')
    words.value = []
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const reload = () => loadErrorWords()

const setMode = (target) => {
  mode.value = target
  resetDictationState()
  if (target === 'dictation') {
    nextTick(() => inputRef.value?.focus())
  }
}

const nextWord = () => {
  if (hasNext.value) {
    currentIndex.value += 1
  } else if (totalWords.value > 0) {
    info('本组错词已完成，重新开始循环')
    currentIndex.value = 0
  }
  resetDictationState()
}

const prevWord = () => {
  if (hasPrev.value) {
    currentIndex.value -= 1
    resetDictationState()
  }
}

const handleRemovalAfterGrasp = () => {
  words.value.splice(currentIndex.value, 1)
  if (currentIndex.value >= words.value.length) {
    currentIndex.value = Math.max(words.value.length - 1, 0)
  }
  resetDictationState()
}

const handleGrasped = async () => {
  if (!currentWord.value) return
  try {
    await wordService.markAsGrasped(currentWord.value.id)
    success('已标记掌握，移出错词本')
    handleRemovalAfterGrasp()
  } catch (err) {
    console.error(err)
    error('标记失败：' + (err.message || ''))
  }
}

const handleNotGrasped = async () => {
  if (!currentWord.value) return
  try {
    await wordService.markAsNotGrasped(currentWord.value.id)
    currentWord.value.errorTimes = (currentWord.value.errorTimes || 0) + 1
    info('已记录为未掌握，继续复习')
  } catch (err) {
    console.error(err)
    error('标记失败：' + (err.message || ''))
  }
}

const handleDictationWrong = async () => {
  if (!currentWord.value) return
  await handleNotGrasped()
  isCorrect.value = false
  showAnswer.value = true
}

const checkAnswer = async () => {
  if (!currentWord.value || !userInput.value.trim()) return
  const answer = currentWord.value.word?.trim().toLowerCase()
  isCorrect.value = userInput.value.trim().toLowerCase() === answer
  showAnswer.value = true
  if (!isCorrect.value) {
    await handleNotGrasped()
  }
}

const playPronunciation = () => {
  if (!currentWord.value || isPlaying.value) return
  if (!('speechSynthesis' in window)) {
    error('当前浏览器不支持语音播放')
    return
  }

  const text = currentWord.value.word
  window.speechSynthesis.cancel()
  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'en-US'
  isPlaying.value = true
  utterance.onend = () => { isPlaying.value = false }
  utterance.onerror = () => { isPlaying.value = false }
  window.speechSynthesis.speak(utterance)
}

const resetDictationState = () => {
  userInput.value = ''
  showAnswer.value = false
  isCorrect.value = false
  nextTick(() => {
    if (mode.value === 'dictation') {
      inputRef.value?.focus()
    }
  })
}

watch(
  () => bookStore.currentBookId,
  (newVal, oldVal) => {
    if (newVal && newVal !== oldVal) {
      loadErrorWords()
    }
  }
)

onMounted(async () => {
  await loadErrorWords()
})
</script>

<style scoped>
.sound-wave-line {
  stroke: white;
  stroke-width: 2;
  stroke-linecap: round;
  animation: wave 0.8s infinite ease-in-out;
}
.sound-wave-line:nth-child(2) { animation-delay: 0.1s; }
.sound-wave-line:nth-child(3) { animation-delay: 0.2s; }
.sound-wave-line:nth-child(4) { animation-delay: 0.3s; }
.sound-wave-line:nth-child(5) { animation-delay: 0.4s; }

@keyframes wave {
  0%, 100% { transform: scaleY(0.6); }
  50% { transform: scaleY(1.2); }
}

.animate-shake-correct {
  animation: shake-correct 0.4s ease;
}

.animate-shake-wrong {
  animation: shake-wrong 0.4s ease;
}

@keyframes shake-correct {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  50% { transform: translateX(4px); }
  75% { transform: translateX(-2px); }
}

@keyframes shake-wrong {
  0%, 100% { transform: translateX(0); }
  20% { transform: translateX(-6px); }
  40% { transform: translateX(6px); }
  60% { transform: translateX(-4px); }
  80% { transform: translateX(4px); }
}
</style>
