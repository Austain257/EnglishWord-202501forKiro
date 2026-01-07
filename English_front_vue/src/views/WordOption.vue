<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans overflow-hidden relative selection:bg-blue-100">
    <!-- 动态背景 -->
    <div class="absolute inset-0 z-0 overflow-hidden pointer-events-none">
      <div class="absolute top-[-10%] right-[-5%] w-[40%] h-[40%] bg-blue-200/30 rounded-full blur-[100px] animate-blob"></div>
      <div class="absolute bottom-[10%] left-[-10%] w-[35%] h-[35%] bg-indigo-200/30 rounded-full blur-[100px] animate-blob animation-delay-2000"></div>
    </div>

    <!-- 顶部导航 -->
    <nav class="relative z-40 px-4 sm:px-6 lg:px-10 h-16 sm:h-20 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button 
          @click="router.back()"
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
                  <div class="h-full bg-blue-500 transition-all duration-300" :style="{ width: `${(currentIndex / totalGameWords) * 100}%` }"></div>
                </div>

                <div class="mb-2 text-sm font-bold text-blue-500/80 tracking-widest uppercase">Word {{ currentIndex + 1 }} / {{ totalGameWords }}</div>
                
                <h3 class="text-4xl md:text-6xl font-black text-slate-800 mb-4 break-all animate-scale-in">
                  {{ gameMode === 'word-to-chinese' ? currentWord?.word : currentWord?.chinese }}
                </h3>
                
                <div class="flex items-center gap-3 text-slate-500 text-xl font-serif italic mb-8">
                  <span>{{ currentWord?.phonetic }}</span>
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
            
            <div class="flex gap-4 w-full">
              <button 
                @click="router.push('/')"
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
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useWordStore } from '@/stores/word'
import { storeToRefs } from 'pinia'

const router = useRouter()
const wordStore = useWordStore()
const { words } = storeToRefs(wordStore)

const difficulties = [
  { label: '简单', value: 'easy', time: 90, count: 10 },
  { label: '普通', value: 'normal', time: 60, count: 15 },
  { label: '困难', value: 'hard', time: 45, count: 20 }
]

const modes = [
  { label: '英译中', value: 'word-to-chinese' },
  { label: '中译英', value: 'chinese-to-word' }
]

const difficulty = ref('normal')
const gameMode = ref('word-to-chinese')
const gameActive = ref(false)
const isGameOver = ref(false)
const timeLeft = ref(60)
const score = ref(0)
const currentIndex = ref(0)
const currentWord = ref(null)
const currentOptions = ref([])
const showResult = ref(false)
const selectedOptionId = ref(null)
const lastResult = ref(null)
const correctCount = ref(0)
const wrongCount = ref(0)
const gameWords = ref([])

let timerInterval = null

const totalGameWords = computed(() => {
  const diff = difficulties.find(d => d.value === difficulty.value)
  return diff ? diff.count : 15
})

const gameOverTitle = computed(() => {
  if (timeLeft.value <= 0) return '时间到！'
  return '挑战完成！'
})

const gameOverMessage = computed(() => {
  const acc = accuracy.value
  if (acc >= 90) return '太棒了！你的词汇量令人惊叹！'
  if (acc >= 60) return '表现不错，继续加油！'
  return '别灰心，熟能生巧！'
})

const accuracy = computed(() => {
  const total = correctCount.value + wrongCount.value
  if (total === 0) return 0
  return Math.round((correctCount.value / total) * 100)
})

const initGameData = async () => {
  if (words.value.length === 0) {
    try {
      await wordStore.fetchWords({ start: 1, end: 100 })
    } catch (e) {
      console.error('Failed to fetch words', e)
    }
  }
}

const startGame = async () => {
  await initGameData()
  if (words.value.length < 4) {
    alert('单词数量不足，无法开始挑战')
    return
  }
  
  const diff = difficulties.find(d => d.value === difficulty.value)
  timeLeft.value = diff.time
  
  const allWords = [...words.value].sort(() => Math.random() - 0.5)
  gameWords.value = allWords.slice(0, totalGameWords.value)
  
  gameActive.value = true
  isGameOver.value = false
  score.value = 0
  correctCount.value = 0
  wrongCount.value = 0
  currentIndex.value = 0
  lastResult.value = null
  
  startTimer()
  loadQuestion()
}

const startTimer = () => {
  if (timerInterval) clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 0) {
      endGame()
    }
  }, 1000)
}

const switchMode = (mode) => {
  if (gameMode.value === mode) return
  gameMode.value = mode
  if (gameActive.value) {
    loadQuestion()
  }
}

const loadQuestion = () => {
  showResult.value = false
  selectedOptionId.value = null
  lastResult.value = null
  
  if (currentIndex.value >= gameWords.value.length) {
    endGame()
    return
  }
  
  currentWord.value = gameWords.value[currentIndex.value]
  generateOptions()
}

const generateOptions = () => {
  const options = new Set()
  options.add(currentWord.value)
  
  while (options.size < 4) {
    const randomWord = words.value[Math.floor(Math.random() * words.value.length)]
    if (randomWord.id !== currentWord.value.id) {
      options.add(randomWord)
    }
  }
  
  currentOptions.value = Array.from(options).sort(() => Math.random() - 0.5)
}

const handleOptionClick = (option) => {
  if (showResult.value) return
  
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
    currentIndex.value++
    loadQuestion()
  }, 1000)
}

const restartGame = () => {
  startGame()
}

const endGame = () => {
  gameActive.value = false
  isGameOver.value = true
  clearInterval(timerInterval)
}

const playAudio = (text) => {
  if (!text) return
  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'en-US'
  window.speechSynthesis.speak(utterance)
}

const getOptionClass = (option) => {
  if (!showResult.value) return 'border-white hover:border-blue-200'
  
  if (option.id === currentWord.value.id) {
    return 'bg-emerald-50 border-emerald-500 ring-1 ring-emerald-500'
  }
  
  if (option.id === selectedOptionId.value && option.id !== currentWord.value.id) {
    return 'bg-rose-50 border-rose-500 ring-1 ring-rose-500'
  }
  
  return 'opacity-50 border-white'
}

const getOptionLetterClass = (option, idx) => {
  if (!showResult.value) {
    const colors = ['bg-blue-100 text-blue-600', 'bg-purple-100 text-purple-600', 'bg-amber-100 text-amber-600', 'bg-pink-100 text-pink-600']
    return colors[idx % 4]
  }
  
  if (option.id === currentWord.value.id) {
    return 'bg-emerald-500 text-white'
  }
  
  if (option.id === selectedOptionId.value) {
    return 'bg-rose-500 text-white'
  }
  
  return 'bg-slate-100 text-slate-400'
}

onUnmounted(() => {
  clearInterval(timerInterval)
})
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
