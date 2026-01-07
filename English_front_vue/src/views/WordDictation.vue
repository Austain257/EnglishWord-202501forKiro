<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- Background Decorations -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-cyan-50/80 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 -right-20 w-96 h-96 bg-sky-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-40 -left-20 w-72 h-72 bg-blue-100/40 rounded-full blur-3xl pointer-events-none"></div>

    <!-- Top Navigation -->
        <nav class="relative z-10 px-4 sm:px-6 lg:px-8 h-16 sm:h-20 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button @click="goBack" class="p-2 -ml-2 text-slate-500 hover:text-slate-900 hover:bg-white/60 rounded-xl transition-all duration-200">
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
        </button>
        <div class="flex flex-col">
          <h1 class="text-lg font-bold text-slate-900 tracking-tight">单词听写</h1>
          <button v-if="mainMode === 'thinking'" @click="toggleMode" class="flex items-center gap-2 text-xs text-slate-500 hover:text-slate-900 transition-colors mt-0.5">
             <span class="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
             <span>{{ mode === 'en2zh' ? '英译汉' : '汉译英' }}</span>
             <svg class="w-3 h-3 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" /></svg>
          </button>
        </div>
      </div>
                  <div class="flex items-center gap-2 sm:gap-3">
        <!-- Progress -->
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>进度</span>
          <span class="text-sky-600 font-bold">{{ currentIndex + 1 }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ totalWords }}</span>
        </div>

        <!-- Current Range -->
         <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>当前范围</span>
          <span class="text-sky-600 font-bold">{{ wordStore.learningRange.start }}-{{ wordStore.learningRange.end }}</span>
        </div>

        <div class="relative bg-white/80 backdrop-blur-sm rounded-xl border border-slate-200/60 p-1 flex items-center text-xs font-semibold">
          <button @click="setMainMode('thinking')" :class="['px-3 py-1.5 rounded-lg transition-all', mainMode === 'thinking' ? 'bg-white text-sky-600 shadow-sm' : 'text-slate-500 hover:text-slate-800']">
            思考模式
          </button>
          <button @click="setMainMode('listening')" :class="['px-3 py-1.5 rounded-lg transition-all', mainMode === 'listening' ? 'bg-white text-sky-600 shadow-sm' : 'text-slate-500 hover:text-slate-800']">
            听音模式
          </button>
        </div>
        
        <!-- Range Settings -->
        <button 
          @click="showRangeModal = true"
          class="flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-slate-600 hover:text-sky-600 hover:border-sky-200 transition-all active:scale-95"
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

    <!-- Main Content -->
    <main class="flex-1 relative z-10 flex flex-col max-w-4xl mx-auto w-full px-4 sm:px-6 lg:px-8 pb-6">
      <div v-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[400px]">
        <div class="w-12 h-12 border-4 border-sky-100 border-t-sky-500 rounded-full animate-spin mb-4"></div>
        <p class="text-slate-400 text-sm font-medium animate-pulse">正在加载单词...</p>
      </div>
      <div v-else-if="!currentWord" class="flex-1 flex flex-col items-center justify-center min-h-[400px] text-center">
        <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" /></svg>
        </div>
        <h3 class="text-xl font-bold text-slate-900 mb-2">暂无听写单词</h3>
        <p class="text-slate-500 mb-8 max-w-xs mx-auto">当前范围内没有可听写的单词，请返回首页调整范围。</p>
        <button @click="goBack" class="px-8 py-3 bg-sky-600 hover:bg-sky-700 text-white rounded-xl font-semibold shadow-lg shadow-sky-500/30 transition-all hover:-translate-y-0.5">
          返回首页
        </button>
      </div>
      <div v-else class="flex-1 flex flex-col justify-center py-4 sm:py-8">
        <div class="relative w-full max-w-2xl mx-auto">
          <div class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500">
            <div class="p-8 sm:p-12 flex flex-col items-center text-center">
              <!-- Question Area -->
              <div class="mb-8 w-full min-h-[80px] flex items-center justify-center">
                <!-- Listening Mode -->
                <div v-if="mainMode === 'listening'" class="flex flex-col items-center">
                  <button @click="playPronunciation" :disabled="isPlaying" class="w-20 h-20 rounded-full bg-sky-500 text-white shadow-lg shadow-sky-500/40 flex items-center justify-center transition-all hover:scale-105 active:scale-95 disabled:opacity-70 disabled:cursor-not-allowed">
                    <svg v-if="!isPlaying" class="w-10 h-10" fill="currentColor" viewBox="0 0 20 20"><path d="M6.3 2.841A1.5 1.5 0 004 4.11V15.89a1.5 1.5 0 002.3 1.269l9.344-5.89a1.5 1.5 0 000-2.538L6.3 2.84z" /></svg>
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
                <!-- Thinking Mode -->
                <div v-else>
                  <div v-if="mode === 'zh2en'" class="text-5xl sm:text-6xl font-bold text-slate-900 leading-relaxed px-4">{{ currentWord.chinese }}</div>
                  <div v-else class="flex flex-col items-center">
                    <h2 class="text-5xl sm:text-6xl md:text-7xl font-bold text-slate-900 mb-4 tracking-tight">{{ currentWord.word }}</h2>
                    <div class="flex items-center gap-3">
                      <span class="text-xl text-slate-500 font-serif italic">{{ currentWord.phonetic || currentWord.pronounce || '...' }}</span>
                      <button @click="playPronunciation" class="p-2 rounded-full bg-sky-50 text-sky-600 hover:bg-sky-100 hover:scale-110 transition-all">
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" /></svg>
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Input Area -->
              <div class="w-full max-w-sm mx-auto relative group/input">
                <input
                  ref="inputRef"
                  v-model="userInput"
                  type="text"
                  :placeholder="mode === 'zh2en' ? '请输入英文单词...' : '请输入中文释义...'"
                  class="w-full text-center text-2xl font-bold text-slate-800 px-6 py-4 bg-slate-50 border-2 rounded-2xl transition-all focus:outline-none focus:bg-white"
                  :class="{
                    'border-slate-100 focus:border-sky-500': !showAnswer,
                    'border-emerald-500 bg-emerald-50 text-emerald-800 animate-shake-correct': showAnswer && isCorrect,
                    'border-rose-500 bg-rose-50 text-rose-800 animate-shake-wrong': showAnswer && !isCorrect
                  }"
                  @keyup.enter="checkAnswer"
                  :disabled="showAnswer"
                />
              </div>
              
              <!-- Answer Display -->
              <div class="mt-6 min-h-[60px] flex flex-col items-center justify-center">
                <div v-if="showAnswer" class="flex flex-col items-center animate-fade-in">
                  <p class="text-2xl font-bold" :class="isCorrect ? 'text-emerald-600' : 'text-rose-600'">
                    {{ currentWord.word }}
                  </p>
                  <p class="mt-1 text-slate-500">{{ mode === 'zh2en' ? currentWord.chinese : `/` + (currentWord.phonetic || currentWord.pronounce) + `/` }}</p>
                </div>
              </div>
            </div>
            
            <!-- Action Buttons -->
            <div class="bg-slate-50/50 border-t border-slate-100 p-6">
              <div v-if="!showAnswer" class="flex gap-4">
                <button @click="handleIDontKnow" class="w-1/3 py-4 rounded-xl font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-all active:scale-95">
                  我不会
                </button>
                <button @click="checkAnswer" :disabled="!userInput.trim()" class="flex-1 py-4 rounded-xl font-bold text-white bg-sky-500 shadow-lg shadow-sky-500/30 hover:bg-sky-600 transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed">
                  检查答案
                </button>
              </div>
              <button v-else @click="nextWord" class="w-full py-4 rounded-xl font-bold text-white bg-slate-700 hover:bg-slate-800 transition-all active:scale-95">
                {{ hasNext ? '下一个' : '完成' }}
              </button>
            </div>
          </div>
        </div>
      </div>
        </main>

    <!-- Range Settings Modal -->
    <div v-if="showRangeModal" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="cancelRangeSelection"></div>
      <div class="relative w-full max-w-lg bg-white rounded-3xl shadow-2xl p-6 sm:p-8 animate-scale-in">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-slate-900">听写范围</h3>
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
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-sky-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
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
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-sky-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
        </div>

        <div class="flex gap-3">
          <button @click="cancelRangeSelection" class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors">取消</button>
          <button @click="applyRange" class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-sky-500 hover:bg-sky-600 shadow-lg shadow-sky-500/20 transition-colors">确认</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useWordStore } from '@/stores/word'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const wordStore = useWordStore()
const { success, error, info } = useToast()

const showRangeModal = ref(false)
const rangeForm = ref({
  start: 1,
  end: 50
})

const mainMode = ref('thinking') // 'thinking' or 'listening'
const mode = ref('zh2en') // 'en2zh' or 'zh2en'
const userInput = ref('')
const showAnswer = ref(false)
const isCorrect = ref(false)
const isPlaying = ref(false)
const inputRef = ref(null)

const successSound = new Audio('/sounds/success.mp3')
const errorSound = new Audio('/sounds/error.mp3')

const loading = computed(() => wordStore.loading)
const currentWord = computed(() => wordStore.currentWord)
const currentIndex = computed(() => wordStore.currentIndex)
const totalWords = computed(() => wordStore.totalWords)
const hasNext = computed(() => wordStore.hasNext)

const inputPlaceholder = computed(() => {
  if (mainMode.value === 'listening') return '听音默写...'
  if (mode.value === 'zh2en') return '输入英文单词...'
  return '输入中文释义...'
})

const answerWord = computed(() => {
  if (!currentWord.value) return ''
  return mainMode.value === 'listening' || mode.value === 'zh2en' 
    ? currentWord.value.word 
    : currentWord.value.chinese
})

const answerDetail = computed(() => {
  if (!currentWord.value) return ''
  if (mainMode.value === 'listening' || mode.value === 'zh2en') {
    return `/` + (currentWord.value.phonetic || currentWord.value.pronounce) + `/`
  } else {
    return currentWord.value.word
  }
})

const goBack = () => router.push('/')

const setMainMode = (newMainMode) => {
  mainMode.value = newMainMode
  resetState()
}

const toggleMode = () => {
  mode.value = mode.value === 'zh2en' ? 'en2zh' : 'zh2en'
  resetState()
}

const playPronunciation = () => {
  if (!currentWord.value || isPlaying.value) return
  const text = currentWord.value.word
  if ('speechSynthesis' in window) {
    isPlaying.value = true
    window.speechSynthesis.cancel()
    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = 'en-US'
    utterance.onend = () => { isPlaying.value = false }
    utterance.onerror = () => { isPlaying.value = false }
    window.speechSynthesis.speak(utterance)
  } else {
    isPlaying.value = false
    error('您的浏览器不支持语音播放')
  }
}

const checkAnswer = () => {
  if (!userInput.value.trim()) return
  
  let correctValue = ''
  if (mainMode.value === 'listening' || mode.value === 'zh2en') {
    correctValue = currentWord.value.word
  } else {
    correctValue = currentWord.value.chinese
  }

  isCorrect.value = userInput.value.trim().toLowerCase() === correctValue.trim().toLowerCase()
  showAnswer.value = true
  
  if (isCorrect.value) {
    playSuccessSound()
  } else {
    wordStore.markCurrentAsNotGrasped()
    playErrorSound()
  }
}

const handleIDontKnow = async () => {
  if (!currentWord.value) return
  try {
    await wordStore.markAsNotGrasped(currentWord.value.id)
    wordStore.markCurrentAsNotGrasped() // Also update local state
    isCorrect.value = false
    showAnswer.value = true
    playErrorSound()
  } catch (err) {
    error('标记失败: ' + err.message)
  }
}

const nextWord = () => {
  if (wordStore.hasNext) {
    wordStore.nextWord()
  } else {
    info('本组单词已完成，重新开始')
    wordStore.goToWord(0)
  }
  resetState()
}

const resetState = () => {
  userInput.value = ''
  showAnswer.value = false
  isCorrect.value = false
  
  nextTick(() => {
    inputRef.value?.focus()
    if (mainMode.value === 'listening') {
      setTimeout(playPronunciation, 300)
    }
  })
}

const playSuccessSound = () => {
  successSound.currentTime = 0
  successSound.play().catch(e => console.error("音频播放失败:", e));
}

const playErrorSound = () => {
  errorSound.currentTime = 0
  errorSound.play().catch(e => console.error("音频播放失败:", e));
}

const handleKeydown = (e) => {
  if (showRangeModal.value) return
  if (e.code === 'Space' && (mainMode.value === 'listening' || mode.value === 'en2zh')) {
    e.preventDefault()
    playPronunciation()
  }
}

const applyRange = async () => {
  if (rangeForm.value.start >= rangeForm.value.end) {
    return error('起始位置必须小于结束位置')
  }
  try {
    await wordStore.fetchWords(rangeForm.value)
    resetState()
    showRangeModal.value = false
    success(`范围已更新: ${rangeForm.value.start}-${rangeForm.value.end}`)
  } catch (err) {
    error('设置范围失败: ' + err.message)
  }
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  rangeForm.value = { ...wordStore.learningRange }
}

onMounted(async () => {
  rangeForm.value = { ...wordStore.learningRange }
  if (wordStore.totalWords === 0) {
    try {
      await wordStore.fetchWords()
    } catch (err) {
      error('加载单词失败: ' + err.message)
      goBack()
      return
    }
  }
  resetState()
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  window.speechSynthesis.cancel()
})
</script>

<style scoped>
@keyframes shake-wrong {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
  20%, 40%, 60%, 80% { transform: translateX(5px); }
}
.animate-shake-wrong {
  animation: shake-wrong 0.5s ease-in-out;
}

@keyframes shake-correct {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}
.animate-shake-correct {
  animation: shake-correct 0.3s ease-in-out;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in {
  animation: fade-in 0.3s ease-out forwards;
}

.sound-wave {
  stroke: white;
  stroke-width: 2;
  stroke-linecap: round;
}

.sound-wave-line {
  animation: wave 1.2s ease-in-out infinite alternate;
}

.sound-wave-line:nth-child(1) { animation-delay: -2.2s; }
.sound-wave-line:nth-child(2) { animation-delay: -3.4s; }
.sound-wave-line:nth-child(3) { animation-delay: -0.8s; }
.sound-wave-line:nth-child(4) { animation-delay: -1.6s; }
.sound-wave-line:nth-child(5) { animation-delay: -2.8s; }

@keyframes wave {
  0% { transform: scaleY(0.2); }
  100% { transform: scaleY(1); }
}
</style>
