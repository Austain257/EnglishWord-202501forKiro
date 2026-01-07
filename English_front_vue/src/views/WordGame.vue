<template>
  <div class="min-h-screen bg-[#F0F4F8] flex flex-col font-sans overflow-hidden relative selection:bg-indigo-100">
    <!-- 动态背景 -->
    <div class="absolute inset-0 z-0 overflow-hidden pointer-events-none">
      <div class="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] bg-indigo-200/40 rounded-full blur-[120px] animate-blob"></div>
      <div class="absolute top-[20%] right-[-10%] w-[40%] h-[40%] bg-purple-200/40 rounded-full blur-[120px] animate-blob animation-delay-2000"></div>
      <div class="absolute bottom-[-10%] left-[20%] w-[50%] h-[50%] bg-pink-200/40 rounded-full blur-[120px] animate-blob animation-delay-4000"></div>
    </div>

    <!-- 顶部导航 (加大尺寸，两端对齐) -->
    <header class="relative z-40 bg-white/70 backdrop-blur-xl border-b border-white/40 shadow-sm h-20 md:h-24">
      <div class="w-full h-full px-4 sm:px-8 md:px-12 flex justify-between items-center">
        <!-- 左侧：返回与标题 -->
        <div class="flex items-center gap-6">
          <button 
            @click="router.back()"
            class="group p-3 hover:bg-white rounded-full transition-all duration-300 shadow-sm hover:shadow-md border border-transparent hover:border-slate-100 text-slate-600 active:scale-95"
          >
            <svg class="w-6 h-6 group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </button>
          <span class="text-2xl font-black text-slate-800 tracking-tight">
            单词泡泡龙
          </span>
        </div>
        
        <!-- 中间：模式切换 (胶囊样式优化) -->
        <div v-if="!isGameOver" class="absolute left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2 hidden md:block">
           <div class="flex items-center bg-slate-100/80 p-1.5 rounded-full border border-white shadow-inner">
            <button 
              v-for="mode in modes" 
              :key="mode.value"
              @click="switchMode(mode.value)"
              class="px-6 py-2.5 rounded-full text-sm font-bold transition-all duration-300 flex items-center gap-2"
              :class="gameMode === mode.value ? 'bg-white text-indigo-600 shadow-md transform scale-105' : 'text-slate-500 hover:text-slate-700 hover:bg-slate-200/50'"
            >
              {{ mode.label }}
            </button>
          </div>
        </div>

        <!-- 右侧：状态展示 -->
        <div class="flex items-center gap-6">
           <div class="flex flex-col items-end">
             <span class="text-xs font-bold text-slate-400 uppercase tracking-wider">Score</span>
             <span class="text-2xl font-black text-indigo-600 font-mono">{{ score }}</span>
           </div>
           
           <!-- 移动端模式切换 (仅小屏幕显示) -->
           <div class="md:hidden flex items-center bg-slate-100 p-1 rounded-lg">
             <button @click="switchMode(gameMode === 'word-to-chinese' ? 'chinese-to-word' : 'word-to-chinese')" class="px-3 py-1 text-xs font-bold text-slate-600">
               切换模式
             </button>
           </div>
        </div>
      </div>
    </header>

    <main class="flex-1 relative w-full h-full overflow-hidden flex flex-col p-3 sm:p-4 md:p-8">
      <!-- 游戏主容器 -->
      <div 
        ref="gameContainer" 
        class="relative flex-1 w-full max-w-6xl mx-auto h-full min-h-[420px] sm:min-h-[520px] rounded-[2rem] sm:rounded-[3rem] bg-white/30 border border-white/40 shadow-2xl backdrop-blur-sm overflow-hidden"
      >
        
        <!-- 中心单词区域 (重设计) -->
        <div 
          v-if="gameActive"
          class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-30 pointer-events-none"
        >
          <!-- 排斥场 -->
          <div class="relative flex flex-col items-center justify-center pointer-events-auto group">
            <!-- 呼吸光环 -->
            <div class="absolute inset-0 bg-indigo-500/10 rounded-[2.5rem] blur-2xl transform scale-110 group-hover:scale-125 transition-transform duration-700 animate-pulse-slow"></div>
            
            <!-- 主卡片 -->
            <div 
              ref="centerCardRef"
              class="relative bg-white/85 backdrop-blur-2xl border border-white/80 shadow-[0_8px_40px_rgba(0,0,0,0.08)] rounded-[1.75rem] px-6 py-6 sm:px-12 sm:py-10 md:px-16 md:py-12 min-w-[220px] sm:min-w-[280px] md:min-w-[320px] max-w-[85vw] sm:max-w-[70vw] md:max-w-[50vw] text-center transform transition-all duration-500"
            >
              
              <!-- 倒计时进度环绕 (顶部) -->
              <div class="absolute top-0 left-6 right-6 sm:left-8 sm:right-8 h-[3px] sm:h-1 bg-slate-100 rounded-full overflow-hidden mt-3 sm:mt-4">
                 <div class="h-full bg-gradient-to-r from-indigo-500 to-purple-500 transition-all duration-1000 linear" :style="{ width: `${(timeLeft / TOTAL_TIME) * 100}%` }"></div>
              </div>

              <h3 class="text-2xl sm:text-4xl md:text-5xl font-black text-slate-800 tracking-tight mb-2 sm:mb-3 mt-4 sm:mt-6 drop-shadow-sm leading-snug break-words px-2">
                {{ gameMode === 'word-to-chinese' ? currentWord?.word : currentWord?.chinese }}
              </h3>
              
              <div class="h-6 sm:h-8 flex items-center justify-center">
                <p v-if="gameMode === 'word-to-chinese'" class="text-slate-500 font-serif italic text-lg sm:text-xl font-medium">
                  {{ currentWord?.phonetic || '' }}
                </p>
              </div>

              <!-- 装饰元素 -->
              <div class="absolute -bottom-3 left-1/2 transform -translate-x-1/2 w-16 h-1 bg-slate-200 rounded-full"></div>
            </div>

            <!-- 提示信息 (悬浮气泡风格) -->
             <transition name="pop">
              <div v-if="feedbackMessage" class="absolute -bottom-24 z-50">
                <div class="px-8 py-4 bg-slate-800 text-white rounded-2xl shadow-xl text-lg font-bold animate-bounce-small flex items-center gap-2">
                  <span class="w-2 h-2 rounded-full bg-rose-500"></span>
                  {{ feedbackMessage }}
                </div>
              </div>
            </transition>
          </div>
        </div>

        <!-- 泡泡层 -->
        <div class="absolute inset-0 z-20">
          <div 
            v-for="bubble in bubbles" 
            :key="bubble.id"
            @click="handleBubbleClick(bubble)"
            class="absolute rounded-full flex items-center justify-center text-center cursor-pointer select-none shadow-lg border border-white/60 backdrop-blur-md transition-all duration-200 animate-floating"
            :class="[
              bubble.status === 'correct' ? 'bg-emerald-400 text-white z-50 ring-4 ring-emerald-200 scale-110 shadow-emerald-500/30' : 
              bubble.status === 'wrong' ? 'bg-rose-400 text-white animate-shake ring-4 ring-rose-200 shadow-rose-500/30' : 
              'bg-gradient-to-b from-white/90 to-white/70 text-slate-700 hover:scale-105 hover:shadow-xl hover:from-white hover:to-white/90'
            ]"
            :style="{
              top: `${bubble.y}px`,
              left: `${bubble.x}px`,
              width: `${bubble.radius * 2}px`,
              height: `${bubble.radius * 2}px`,
              '--floatX': `${bubble.floatX}px`,
              '--floatY': `${bubble.floatY}px`,
              animationDelay: `${bubble.floatDelay}s`,
              animationDuration: `${bubble.floatDuration}s`,
              willChange: 'transform'
            }"
          >
            <div class="relative z-10 w-full px-2 break-words" 
                 :style="{ 
                   fontSize: `${Math.max(12, bubble.radius / 2.8)}px`,
                   fontWeight: '700',
                   lineHeight: '1.2'
                 }">
              {{ bubble.text }}
            </div>
            
            <!-- 泡泡高光质感 -->
            <div class="absolute inset-0 rounded-full shadow-[inset_-10px_-10px_20px_rgba(0,0,0,0.02),inset_10px_10px_20px_rgba(255,255,255,0.8)] pointer-events-none"></div>
            <div class="absolute top-[15%] left-[18%] w-[20%] h-[12%] bg-white/80 rounded-[100%] rotate-[-45deg] pointer-events-none blur-[1px]"></div>
          </div>
        </div>


          <!-- 准备界面 -->
        <transition name="fade" mode="out-in">
          <div v-if="!gameActive && !isGameOver" class="absolute inset-0 z-50 flex items-center justify-center px-4 sm:px-8 bg-white/40 backdrop-blur-md">
            <div class="bg-white/95 backdrop-blur-xl px-8 py-10 sm:p-12 md:p-16 rounded-[2.5rem] shadow-2xl border border-white max-w-lg w-full text-center">
              <div class="w-16 h-16 sm:w-20 sm:h-20 bg-gradient-to-tr from-indigo-500 to-purple-600 rounded-3xl flex items-center justify-center mx-auto mb-6 sm:mb-8 shadow-xl shadow-indigo-500/30 transform -rotate-6 hover:rotate-0 transition-transform duration-500">
                <svg class="w-12 h-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h2 class="text-2xl sm:text-3xl md:text-4xl font-black text-slate-800 mb-4 tracking-tight">准备好了吗？</h2>
              <p class="text-slate-500 text-base sm:text-lg mb-8 sm:mb-10 leading-relaxed font-medium">
                在泡泡海洋中寻找正确答案。<br>
                保持连击，冲击高分！
              </p>
              <button
                @click="startGame"
                class="w-full py-4 sm:py-5 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-2xl font-bold text-lg sm:text-xl shadow-xl shadow-indigo-500/30 hover:shadow-indigo-500/50 hover:-translate-y-1 transition-all active:scale-95"
              >
                立即开始
              </button>
            </div>
          </div>

          <div v-else-if="isGameOver" class="absolute inset-0 z-50 flex items-center justify-center px-4 sm:px-8 bg-white/40 backdrop-blur-md">
            <div class="bg-white/95 backdrop-blur-xl px-8 py-10 sm:p-12 rounded-[2.5rem] shadow-2xl border border-white max-w-lg w-full text-center animate-scale-in">
              <h2 class="text-2xl sm:text-3xl md:text-4xl font-black text-slate-800 mb-2">{{ gameOverTitle }}</h2>
              <p class="text-slate-500 text-base sm:text-lg mb-8 sm:mb-10 font-medium">{{ gameOverMessage }}</p>
              
              <div class="grid grid-cols-2 gap-4 sm:gap-6 mb-8 sm:mb-10">
                <div class="bg-indigo-50 border border-indigo-100 p-4 sm:p-6 rounded-3xl">
                  <div class="text-xs font-bold text-indigo-400 uppercase tracking-wider mb-2">Total Score</div>
                  <div class="text-3xl sm:text-4xl font-black text-indigo-600">{{ score }}</div>
                </div>
                <div class="bg-amber-50 border border-amber-100 p-4 sm:p-6 rounded-3xl">
                  <div class="text-xs font-bold text-amber-400 uppercase tracking-wider mb-2">Best Streak</div>
                  <div class="text-3xl sm:text-4xl font-black text-amber-600">{{ bestStreak }}</div>
                </div>
              </div>
              
              <div class="flex flex-col sm:flex-row gap-3 sm:gap-4">
                <button 
                  @click="router.push('/')"
                  class="flex-1 py-3.5 bg-slate-100 text-slate-600 rounded-2xl font-bold text-base sm:text-lg hover:bg-slate-200 transition-colors"
                >
                  返回首页
                </button>
                <button 
                  @click="restartGame"
                  class="flex-1 py-3.5 bg-indigo-600 text-white rounded-2xl font-bold text-base sm:text-lg shadow-lg shadow-indigo-500/30 hover:bg-indigo-700 transition-colors"
                >
                  再玩一次
                </button>
              </div>
            </div>
          </div>
        </transition>

      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useWordStore } from '@/stores/word'
import { storeToRefs } from 'pinia'

const router = useRouter()
const wordStore = useWordStore()
const { words } = storeToRefs(wordStore)

// 游戏配置
const TOTAL_TIME = 60
const BUBBLE_COUNT = 5
const BUBBLE_PADDING = 28

// 状态
const gameActive = ref(false)
const isGameOver = ref(false)
const timeLeft = ref(TOTAL_TIME)
const score = ref(0)
const correctCount = ref(0)
const wrongCount = ref(0)
const currentStreak = ref(0)
const bestStreak = ref(0)
const gameMode = ref('word-to-chinese')
const bubbles = ref([])
const currentWord = ref(null)
const currentOptions = ref([])
const feedbackMessage = ref('')
const gameContainer = ref(null)
const centerCardRef = ref(null)
const centerZoneRadius = ref(220)
const centerCardBounds = ref({ width: 0, height: 0 })
const containerSize = ref({ width: 0, height: 0 })
const cardBottomLimit = ref(0)

// 定时器
let timerInterval = null

const modes = [
  { label: '英译中', value: 'word-to-chinese' },
  { label: '中译英', value: 'chinese-to-word' }
]

const switchMode = (mode) => {
  if (gameMode.value === mode) return
  gameMode.value = mode
  if (gameActive.value) {
    renderCurrentBubbles()
  }
}

const formatBubbleText = (word) => {
  const text = gameMode.value === 'word-to-chinese' ? word.chinese : word.word
  const isMobile = (containerSize.value.width || window.innerWidth) < 640
  const maxLen = isMobile ? 6 : 10
  return text.length > maxLen ? `${text.slice(0, maxLen)}…` : text
}

const updateLayoutMetrics = () => {
  if (!gameContainer.value) return
  const width = gameContainer.value.clientWidth
  const height = gameContainer.value.clientHeight
  containerSize.value = { width, height }

  if (centerCardRef.value) {
    const cardWidth = centerCardRef.value.offsetWidth
    const cardHeight = centerCardRef.value.offsetHeight
    centerCardBounds.value = { width: cardWidth, height: cardHeight }
    centerZoneRadius.value = Math.max(cardWidth, cardHeight) / 2 + 40
    cardBottomLimit.value = height / 2 + cardHeight / 2 - 6
  } else {
    const fallback = Math.min(width, height) / 4
    centerZoneRadius.value = fallback
    cardBottomLimit.value = height / 2 + fallback / 2
  }
}

const clampBubblePosition = (bubble) => {
  const width = containerSize.value.width || window.innerWidth
  const height = containerSize.value.height || window.innerHeight
  bubble.centerX = Math.min(Math.max(bubble.centerX, bubble.radius + BUBBLE_PADDING), width - bubble.radius - BUBBLE_PADDING)
  const maxY = Math.min(cardBottomLimit.value - bubble.radius - 6, height - bubble.radius - BUBBLE_PADDING)
  const minY = bubble.radius + BUBBLE_PADDING
  bubble.centerY = Math.min(Math.max(bubble.centerY, minY), maxY)
  bubble.x = bubble.centerX - bubble.radius
  bubble.y = bubble.centerY - bubble.radius
}

const resolveOverlaps = (list) => {
  for (let iter = 0; iter < 8; iter++) {
    let moved = false
    for (let i = 0; i < list.length; i++) {
      for (let j = i + 1; j < list.length; j++) {
        const b1 = list[i]
        const b2 = list[j]
        const dx = b1.centerX - b2.centerX
        const dy = b1.centerY - b2.centerY
        const dist = Math.sqrt(dx * dx + dy * dy) || 0.0001
        const minDist = b1.radius + b2.radius + 10
        if (dist < minDist) {
          const shift = (minDist - dist) / 2 + 0.5
          const nx = dx / dist
          const ny = dy / dist
          b1.centerX += nx * shift
          b1.centerY += ny * shift
          b2.centerX -= nx * shift
          b2.centerY -= ny * shift
          clampBubblePosition(b1)
          clampBubblePosition(b2)
          moved = true
        }
      }
    }
    if (!moved) break
  }
}

const layoutBubbles = (optionsArray) => {
  if (!gameContainer.value || !optionsArray.length) return []
  const width = containerSize.value.width || gameContainer.value.clientWidth || window.innerWidth
  const height = containerSize.value.height || gameContainer.value.clientHeight || window.innerHeight
  const isMobile = width < 640
  const minRadius = isMobile ? 30 : 44
  const maxRadius = isMobile ? 42 : 60
  const baseDistance = centerZoneRadius.value + maxRadius + 24
  const angleSpan = Math.PI * 1.6
  const startAngle = -Math.PI / 2 - angleSpan / 2

  const bubbleList = optionsArray.map((word, index) => {
    const radius = Math.random() * (maxRadius - minRadius) + minRadius
    const ratio = optionsArray.length === 1 ? 0.5 : index / (optionsArray.length - 1 || 1)
    const baseAngle = startAngle + ratio * angleSpan
    const finalAngle = baseAngle + (Math.random() - 0.5) * 0.35
    const orbitRadius = baseDistance + (Math.random() - 0.5) * 60
    let centerX = width / 2 + Math.cos(finalAngle) * orbitRadius
    let centerY = height / 2 + Math.sin(finalAngle) * orbitRadius

    const topCap = height / 2 - centerCardBounds.value.height / 2 - radius - 12
    const allowedMaxY = Math.min(topCap, cardBottomLimit.value - radius - 8)
    centerY = Math.min(centerY, allowedMaxY)

    const bubble = {
      id: `${word.id}-${index}-${Date.now()}`,
      word,
      text: formatBubbleText(word),
      radius,
      centerX,
      centerY,
      status: null,
      floatX: +(Math.random() * (isMobile ? 8 : 14) * (Math.random() > 0.5 ? 1 : -1)).toFixed(1),
      floatY: +(Math.random() * (isMobile ? 8 : 16) * (Math.random() > 0.5 ? 1 : -1)).toFixed(1),
      floatDelay: +(Math.random() * 1.5).toFixed(2),
      floatDuration: +(4.5 + Math.random() * 3.5).toFixed(2)
    }
    clampBubblePosition(bubble)
    return bubble
  })

  resolveOverlaps(bubbleList)
  bubbleList.forEach(clampBubblePosition)

  return bubbleList.map((bubble) => ({
    id: bubble.id,
    word: bubble.word,
    text: bubble.text,
    radius: bubble.radius,
    status: bubble.status,
    x: bubble.x,
    y: bubble.y,
    floatX: bubble.floatX,
    floatY: bubble.floatY,
    floatDelay: bubble.floatDelay,
    floatDuration: bubble.floatDuration
  }))
}

const pickOptionWords = () => {
  const options = new Set()
  options.add(currentWord.value)
  while (options.size < BUBBLE_COUNT) {
    const randomWord = words.value[Math.floor(Math.random() * words.value.length)]
    if (randomWord?.id !== currentWord.value.id) {
      options.add(randomWord)
    }
  }
  return Array.from(options).sort(() => Math.random() - 0.5)
}

const renderCurrentBubbles = (immediate = false) => {
  if (!gameActive.value || !currentWord.value || currentOptions.value.length === 0) return
  const action = () => {
    updateLayoutMetrics()
    bubbles.value = layoutBubbles(currentOptions.value)
  }
  if (immediate) action()
  else nextTick(action)
}

// 游戏逻辑保持不变，UI层面已升级
const gameOverTitle = computed(() => timeLeft.value <= 0 ? '时间到！' : '挑战完成！')
const gameOverMessage = computed(() => {
  const total = correctCount.value + wrongCount.value
  if (total === 0) return '好像还没有进入状态？'
  const accuracy = Math.round((correctCount.value / total) * 100)
  if (accuracy >= 90) return '太棒了！你的反应速度和词汇量简直完美！'
  if (accuracy >= 60) return '表现不错，继续保持这个节奏！'
  return '别灰心，熟能生巧，下次一定更好！'
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
    alert('单词数量不足，无法开始游戏')
    return
  }
  
  gameActive.value = true
  isGameOver.value = false
  timeLeft.value = TOTAL_TIME
  score.value = 0
  correctCount.value = 0
  wrongCount.value = 0
  currentStreak.value = 0
  bestStreak.value = 0
  feedbackMessage.value = ''
  currentOptions.value = []
  
  if (timerInterval) clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    timeLeft.value--
    if (timeLeft.value <= 0) {
      endGame()
    }
  }, 1000)
  
  await nextTick()
  updateLayoutMetrics()
  nextRound()
}

const restartGame = () => {
  startGame()
}

const endGame = () => {
  gameActive.value = false
  isGameOver.value = true
  clearInterval(timerInterval)
  bubbles.value = []
  currentOptions.value = []
}

const nextRound = () => {
  if (!gameActive.value) return
  const availableWords = words.value
  const randomIndex = Math.floor(Math.random() * availableWords.length)
  currentWord.value = availableWords[randomIndex]
  feedbackMessage.value = ''
  currentOptions.value = Array.from(new Set([currentWord.value, ...pickOptionWords().slice(1)]))
  renderCurrentBubbles()
}

const handleBubbleClick = (bubble) => {
  if (bubble.status) return 
  const isCorrect = bubble.word.id === currentWord.value.id
  
  if (isCorrect) {
    bubble.status = 'correct'
    score.value += 10 + currentStreak.value * 2
    correctCount.value++
    currentStreak.value++
    if (currentStreak.value > bestStreak.value) bestStreak.value = currentStreak.value
    setTimeout(() => nextRound(), 500)
  } else {
    bubble.status = 'wrong'
    wrongCount.value++
    currentStreak.value = 0
    score.value = Math.max(0, score.value - 5)
    const correctText = gameMode.value === 'word-to-chinese' ? currentWord.value.chinese : currentWord.value.word
    feedbackMessage.value = `正确答案: ${correctText}`
    setTimeout(() => nextRound(), 1600)
  }
}

const handleResize = () => {
  renderCurrentBubbles(true)
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  clearInterval(timerInterval)
  window.removeEventListener('resize', handleResize)
})

watch(gameActive, (val) => {
  if (val) {
    renderCurrentBubbles(true)
  }
})
</script>

<style scoped>
@keyframes blob {
  0% { transform: translate(0px, 0px) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0px, 0px) scale(1); }
}
.animate-blob {
  animation: blob 7s infinite;
}
.animation-delay-2000 {
  animation-delay: 2s;
}
.animation-delay-4000 {
  animation-delay: 4s;
}

.animate-pulse-slow {
  animation: pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px) rotate(-5deg); }
  75% { transform: translateX(5px) rotate(5deg); }
}
.animate-shake {
  animation: shake 0.4s ease-in-out;
}

.animate-scale-in {
  animation: scaleIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes scaleIn {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.animate-bounce-small {
  animation: bounceSmall 2s infinite;
}

@keyframes bounceSmall {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

@keyframes floatDrift {
  0% { transform: translate(0px, 0px) scale(1); }
  25% { transform: translate(var(--floatX, 4px) * 0.4, var(--floatY, 6px) * 0.4) scale(1.01); }
  50% { transform: translate(calc(var(--floatX, 4px)), calc(var(--floatY, 6px))) scale(1.02); }
  75% { transform: translate(calc(var(--floatX, 4px) * -0.3), calc(var(--floatY, 6px) * -0.3)) scale(0.99); }
  100% { transform: translate(0px, 0px) scale(1); }
}

.animate-floating {
  animation-name: floatDrift;
  animation-timing-function: ease-in-out;
  animation-iteration-count: infinite;
  animation-direction: alternate;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.pop-enter-active {
  animation: popIn 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.pop-leave-active {
  transition: opacity 0.2s;
}
.pop-leave-to {
  opacity: 0;
}

@keyframes popIn {
  0% { opacity: 0; transform: scale(0.5) translateY(20px); }
  100% { opacity: 1; transform: scale(1) translateY(0); }
}
</style>
