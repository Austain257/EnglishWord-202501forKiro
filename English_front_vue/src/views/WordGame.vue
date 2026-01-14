<template>
  <div class="min-h-screen bg-[#F0F4F8] flex flex-col font-sans overflow-hidden relative selection:bg-indigo-100">
    <!-- 动态背景 -->
    <div class="absolute inset-0 z-0 overflow-hidden pointer-events-none">
      <div class="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] bg-indigo-200/40 rounded-full blur-[120px] animate-blob"></div>
      <div class="absolute top-[20%] right-[-10%] w-[40%] h-[40%] bg-purple-200/40 rounded-full blur-[120px] animate-blob animation-delay-2000"></div>
      <div class="absolute bottom-[-10%] left-[20%] w-[50%] h-[50%] bg-pink-200/40 rounded-full blur-[120px] animate-blob animation-delay-4000"></div>
    </div>

    <!-- 彩带效果 -->
    <transition-group name="confetti" tag="div" class="pointer-events-none absolute inset-0 z-40">
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
          <h1 class="text-lg font-semibold text-slate-900 tracking-tight">单词泡泡龙</h1>
          <p class="flex items-center gap-1 text-xs text-slate-500">
            <span class="inline-block w-1.5 h-1.5 rounded-full" :class="gameActive ? 'bg-emerald-500 animate-pulse' : 'bg-slate-400'"></span>
            泡泡闯关 · 模式切换
          </p>
        </div>
      </div>

      <div class="flex items-center gap-2 sm:gap-3">
        <!-- 进度 -->
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500">
          <span>进度</span>
          <span class="text-indigo-600 font-bold">{{ progressCurrent }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ progressTotal }}</span>
        </div>
        <!-- 当前课本 -->
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500 max-w-[220px]">
          <span>当前课本</span>
          <span class="text-indigo-600 font-bold truncate" :title="currentBookName">{{ currentBookName }}</span>
        </div>
        <!-- 范围 -->
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500">
          <span>当前范围</span>
          <span class="text-indigo-600 font-bold">{{ studyRange }}</span>
        </div>
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500">
          <button 
            v-for="mode in modes" 
            :key="mode.value"
            @click="switchMode(mode.value)"
            class="px-3 py-1 rounded-lg transition-all"
            :class="gameMode === mode.value ? 'bg-white text-indigo-600 shadow-sm' : 'text-slate-500 hover:text-slate-700'"
          >
            {{ mode.label }}
          </button>
        </div>
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500">
          <span>倒计时</span>
          <span class="text-indigo-600 font-bold">{{ gameActive ? timeLeft : '--' }}s</span>
        </div>
        <div class="flex items-center gap-2 px-3 py-1.5 bg-slate-900 text-white rounded-xl text-xs font-semibold">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" /></svg>
          得分 {{ score }}
        </div>
      </div>
    </nav>

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
                 <div class="h-full bg-gradient-to-r from-indigo-500 to-purple-500 transition-all duration-1000 linear" :style="{ width: `${timerPercent}%` }"></div>
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
              '--driftX': `${bubble.driftX}px`,
              '--driftY': `${bubble.driftY}px`,
              '--floatTilt': bubble.floatTilt,
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
            <div class="bg-white/95 backdrop-blur-xl px-8 py-10 sm:p-12 md:p-16 rounded-[2.5rem] shadow-2xl border border-white max-w-lg w-full text-center space-y-6">
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
              <div class="flex flex-wrap justify-center gap-3 mb-2">
                <button
                  v-for="d in difficulties"
                  :key="d.value"
                  @click="difficulty = d.value"
                  class="px-4 py-2 rounded-xl text-sm font-semibold transition-all border-2"
                  :class="difficulty === d.value ? 'border-indigo-500 bg-indigo-50 text-indigo-600' : 'border-slate-200 text-slate-500 hover:border-slate-300 bg-white'"
                >
                  {{ d.label }}
                </button>
              </div>
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
              
              <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 sm:gap-6 mb-6 sm:mb-8">
                <div class="bg-indigo-50 border border-indigo-100 p-4 sm:p-6 rounded-3xl">
                  <div class="text-xs font-bold text-indigo-400 uppercase tracking-wider mb-2">Total Score</div>
                  <div class="text-3xl sm:text-4xl font-black text-indigo-600">{{ score }}</div>
                </div>
                <div class="bg-amber-50 border border-amber-100 p-4 sm:p-6 rounded-3xl">
                  <div class="text-xs font-bold text-amber-400 uppercase tracking-wider mb-2">Best Streak</div>
                  <div class="text-3xl sm:text-4xl font-black text-amber-600">{{ bestStreak }}</div>
                </div>
                <div class="bg-emerald-50 border border-emerald-100 p-4 sm:p-6 rounded-3xl">
                  <div class="text-xs font-bold text-emerald-400 uppercase tracking-wider mb-2">Accuracy</div>
                  <div class="text-3xl sm:text-4xl font-black text-emerald-600">{{ accuracy }}%</div>
                </div>
              </div>

              <div v-if="gameResultType === 'completed' && motivationalLine" class="w-full mb-6 p-4 bg-gradient-to-r from-indigo-50 to-blue-50 border border-indigo-100 rounded-2xl">
                <p class="text-indigo-600 font-semibold">{{ motivationalLine }}</p>
              </div>

              <div v-if="gameResultType === 'completed' && latestRecord" class="w-full mb-8 p-4 bg-slate-50 border border-slate-200 rounded-2xl text-left space-y-3">
                <p class="text-slate-600 text-sm">
                  当前学习范围：<span class="font-semibold text-slate-900">{{ studyRange }}</span>
                </p>
                <template v-if="showRound2Button">
                  <p class="text-slate-700 text-sm">第二轮复习尚未标记完成，点击下方按钮即可记录。</p>
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
              
              <div class="flex flex-col sm:flex-row gap-3 sm:gap-4">
                <button 
                  @click="goBack"
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

    <transition name="fade">
      <div v-if="showTimeUpModal" class="fixed inset-0 bg-slate-900/70 backdrop-blur-sm z-50 flex items-center justify-center px-4">
        <div class="bg-white rounded-[1.75rem] p-6 max-w-md w-full text-center shadow-2xl border border-slate-100">
          <div class="w-16 h-16 mx-auto rounded-2xl bg-rose-100 text-rose-500 flex items-center justify-center mb-4">
            <svg class="w-8 h-8" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M12 6v6l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <h3 class="text-2xl font-bold text-slate-900 mb-2">时间到啦！</h3>
          <p class="text-slate-500 mb-6">
            还有 {{ remainingWordsCount }} 个单词在等你，继续挑战还是先休息一下？
          </p>
          <div class="flex flex-col gap-3">
            <button
              @click="continueGameAfterTimeout"
              class="w-full py-3 rounded-2xl bg-indigo-600 text-white font-semibold shadow-lg shadow-indigo-500/30 hover:bg-indigo-700 transition-colors"
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
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
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

// 游戏配置
const difficulties = [
  { label: '简单', value: 'easy', time: 240 },
  { label: '普通', value: 'normal', time: 180 },
  { label: '困难', value: 'hard', time: 140 }
]
const defaultTime = difficulties.find(d => d.value === 'normal')?.time || 180
const BUBBLE_COUNT = 5
const BUBBLE_PADDING = 28

const motivationalQuotes = [
  '你的坚持，正在悄悄惊艳所有人！',
  '离目标只差一步，坚持就是胜利！',
  '再接再厉，下一次会更快更准！',
  '你在用行动证明——努力的人最耀眼！'
]

const confettiColors = ['#f97316', '#facc15', '#34d399', '#60a5fa', '#a855f7', '#fb7185']

// 状态
const difficulty = ref('normal')
const gameActive = ref(false)
const isGameOver = ref(false)
const timeLeft = ref(defaultTime)
const maxTime = ref(defaultTime)
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
const initializing = ref(true)
const latestRecord = ref(null)
const gameWords = ref([])
const currentBookName = computed(() => bookStore.currentBook?.bookName || bookStore.currentBook?.name || '未选择课本')
const initialWordCount = ref(0)
const completedWordIds = ref(new Set())
const firstLearnDialog = ref(false)
const showTimeUpModal = ref(false)
const motivationalLine = ref('')
const confettiPieces = ref([])
const markingRound2 = ref(false)
const gameResultType = ref(null)
const timerExpired = ref(false)
const firstLearnDialogMessage = ref('当前课本还未学习，请前往学习中心学习。')

const totalGameWords = computed(() => initialWordCount.value || words.value.length || 0)
const progressCurrent = computed(() => completedWordIds.value.size || 0)
const progressTotal = computed(() => totalGameWords.value || 0)
const progressPercent = computed(() => {
  if (!progressTotal.value) return 0
  return Math.min((progressCurrent.value / progressTotal.value) * 100, 100)
})
const remainingWordsCount = computed(() => Math.max(totalGameWords.value - progressCurrent.value, 0))
const timerPercent = computed(() => {
  if (!maxTime.value) return 0
  return Math.max((timeLeft.value / maxTime.value) * 100, 0)
})
const studyRange = computed(() => `${wordStore.learningRange.start}-${wordStore.learningRange.end}`)
const showRound2Button = computed(() => !!latestRecord.value && !latestRecord.value.round2ReviewTime)
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
    return motivationalLine.value || '恭喜你完成本轮泡泡词汇挑战！'
  }
  if (gameResultType.value === 'timeout') {
    return remainingWordsCount.value
      ? `还有 ${remainingWordsCount.value} 个单词等待击破，随时回来继续！`
      : '时间结束，本轮挑战告一段落。'
  }
  const acc = accuracy.value
  if (acc >= 90) return '太棒了！你的反应速度与词汇量都很惊艳！'
  if (acc >= 60) return '表现不错，保持节奏继续冲刺！'
  return '别灰心，熟能生巧，下次一定更好！'
})

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

  const isInsideCenterZone = (cx, cy, radius) => {
    const dx = cx - width / 2
    const dy = cy - height / 2
    const dist = Math.sqrt(dx * dx + dy * dy)
    return dist < centerZoneRadius.value + radius + 16
  }

  const bubbleList = optionsArray.map((word, index) => {
    const radius = Math.random() * (maxRadius - minRadius) + minRadius
    let centerX = width / 2
    let centerY = height / 2
    let attempts = 0
    while (attempts < 10) {
      attempts++
      const randomAngle = Math.random() * Math.PI * 2
      const orbital = baseDistance + Math.random() * baseDistance * 0.6
      centerX = width / 2 + Math.cos(randomAngle) * orbital + (Math.random() - 0.5) * 60
      centerY = height / 2 + Math.sin(randomAngle) * orbital + (Math.random() - 0.5) * 50
      if (!isInsideCenterZone(centerX, centerY, radius)) break
    }

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
      floatX: +(Math.random() * (isMobile ? 6 : 10) * (Math.random() > 0.5 ? 1 : -1)).toFixed(1),
      floatY: +(Math.random() * (isMobile ? 6 : 12) * (Math.random() > 0.5 ? 1 : -1)).toFixed(1),
      floatDelay: +(Math.random() * 1.5).toFixed(2),
      floatDuration: +(4.5 + Math.random() * 3.5).toFixed(2),
      floatTilt: +(Math.random() * 1.6 - 0.8).toFixed(2)
    }
    clampBubblePosition(bubble)
    return bubble
  })

  resolveOverlaps(bubbleList)
  bubbleList.forEach(clampBubblePosition)

  const computeDriftLimit = (bubble, idx) => {
    let clearance = Infinity
    for (let i = 0; i < bubbleList.length; i++) {
      if (i === idx) continue
      const other = bubbleList[i]
      const dx = bubble.centerX - other.centerX
      const dy = bubble.centerY - other.centerY
      const dist = Math.sqrt(dx * dx + dy * dy) - bubble.radius - other.radius - 6
      clearance = Math.min(clearance, dist)
    }
    const dxCenter = bubble.centerX - width / 2
    const dyCenter = bubble.centerY - height / 2
    const distCenter = Math.sqrt(dxCenter * dxCenter + dyCenter * dyCenter) - centerZoneRadius.value - bubble.radius - 8
    clearance = Math.min(clearance, distCenter)
    const leftMargin = bubble.centerX - bubble.radius - BUBBLE_PADDING
    const rightMargin = width - (bubble.centerX + bubble.radius) - BUBBLE_PADDING
    const topMargin = bubble.centerY - bubble.radius - BUBBLE_PADDING
    const bottomMargin = (cardBottomLimit.value - bubble.radius - 6) - bubble.centerY
    clearance = Math.min(clearance, leftMargin, rightMargin, topMargin, bottomMargin)
    const maxDrift = Math.max(0, Math.min(clearance, isMobile ? 6 : 12))
    return maxDrift
  }

  bubbleList.forEach((bubble, idx) => {
    const driftLimit = computeDriftLimit(bubble, idx)
    const driftX = +(Math.random() * driftLimit).toFixed(1)
    const driftY = +(Math.random() * driftLimit).toFixed(1)
    bubble.driftX = driftX
    bubble.driftY = driftY
  })

  return bubbleList.map((bubble) => ({
    id: bubble.id,
    word: bubble.word,
    text: bubble.text,
    radius: bubble.radius,
    status: bubble.status,
    x: bubble.x,
    y: bubble.y,
    driftX: bubble.driftX,
    driftY: bubble.driftY,
    floatDelay: bubble.floatDelay,
    floatDuration: bubble.floatDuration,
    floatTilt: bubble.floatTilt
  }))
}

const pickOptionWords = () => {
  let guard = 0
  const options = new Set()
  options.add(currentWord.value)
  while (options.size < BUBBLE_COUNT && guard < 200) {
    guard++
    const randomWord = words.value[Math.floor(Math.random() * words.value.length)]
    if (randomWord && randomWord.id !== currentWord.value.id) {
      options.add(randomWord)
    }
  }
  return Array.from(options.values()).sort(() => Math.random() - 0.5)
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

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const ensureBookSelected = async (bookId) => {
  if (!bookId) {
    throw new Error('学习记录缺少课本信息')
  }
  if (bookStore.currentBook?.id === bookId) return
  if (!bookStore.books?.length) {
    await bookStore.fetchBooks()
  }
  const targetBook = bookStore.books.find((book) => book.id === bookId)
  if (!targetBook) {
    throw new Error('最新学习记录关联的课本不存在，请先到课本页选择')
  }
  await bookStore.selectBook(targetBook)
}

const fetchLatestRecordSafe = async (userId, bookId) => {
  const authHeader = authStore.token ? { Authorization: `Bearer ${authStore.token}` } : {}
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://192.168.0.106:8080'
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
  feedbackMessage.value = ''
  bubbles.value = []
  currentOptions.value = []
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
    // 已在弹窗提示，无需重复 toast
    return
  }
  if (words.value.length < 1) {
    showError('单词数量不足，无法开始游戏')
    return
  }

  const diff = difficulties.find((d) => d.value === difficulty.value)
  const targetTime = diff?.time || 180
  timeLeft.value = targetTime
  maxTime.value = targetTime

  const shuffled = [...words.value].sort(() => Math.random() - 0.5)
  initialWordCount.value = shuffled.length
  gameWords.value = [...shuffled]
  completedWordIds.value = new Set()

  gameActive.value = true
  isGameOver.value = false
  score.value = 0
  correctCount.value = 0
  wrongCount.value = 0
  currentStreak.value = 0
  bestStreak.value = 0
  resetRuntimeState()

  startTimer()
  await nextTick()
  updateLayoutMetrics()
  loadCurrentQuestion()
}

const restartGame = () => {
  startGame()
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

const loadCurrentQuestion = () => {
  if (!gameActive.value) return
  if (!gameWords.value.length) {
    handleAllWordsMastered()
    return
  }
  currentWord.value = gameWords.value[0]
  feedbackMessage.value = ''
  currentOptions.value = pickOptionWords()
  renderCurrentBubbles()
}

const scheduleNextWord = (isCorrect) => {
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
    loadCurrentQuestion()
  }
}

const handleBubbleClick = (bubble) => {
  if (!gameActive.value || bubble.status || !currentWord.value) return
  const isCorrect = bubble.word.id === currentWord.value.id

  if (isCorrect) {
    bubble.status = 'correct'
    score.value += 10 + currentStreak.value * 2
    correctCount.value++
    currentStreak.value++
    bestStreak.value = Math.max(bestStreak.value, currentStreak.value)
    setTimeout(() => scheduleNextWord(true), 600)
  } else {
    bubble.status = 'wrong'
    wrongCount.value++
    currentStreak.value = 0
    score.value = Math.max(0, score.value - 5)
    const correctText = gameMode.value === 'word-to-chinese' ? currentWord.value.chinese : currentWord.value.word
    feedbackMessage.value = `正确答案：${correctText}`
    setTimeout(() => scheduleNextWord(false), 1600)
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

const endGame = (resultType = 'timeout') => {
  if (timerInterval) {
    clearInterval(timerInterval)
    timerInterval = null
  }
  gameResultType.value = resultType
  gameActive.value = false
  isGameOver.value = true
  bubbles.value = []
  currentWord.value = null
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
    console.error('标记第二轮复习失败:', error)
    showError('标记第二轮复习失败：' + (error.message || '请稍后重试'))
  } finally {
    markingRound2.value = false
  }
}

const handleResize = () => {
  renderCurrentBubbles(true)
}

const confirmFirstLearn = () => {
  firstLearnDialog.value = false
  router.push('/word/learning')
}

const cancelFirstLearn = () => {
  firstLearnDialog.value = false
  goBack()
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  preloadLatestWords()
})

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
  }
  window.removeEventListener('resize', handleResize)
})

watch(gameActive, (val) => {
  if (val) {
    renderCurrentBubbles(true)
  }
})

const onDifficultyChange = (level) => {
  difficulty.value = level
}
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
  0% { 
    transform: scale(0.97) rotate(calc(var(--floatTilt, 0.5) * -0.5deg));
    box-shadow: 0 10px 25px rgba(15, 23, 42, 0.08);
  }
  50% { 
    transform: scale(1.03) rotate(calc(var(--floatTilt, 0.5) * 1deg)); 
    box-shadow: 0 14px 35px rgba(79, 70, 229, 0.12);
  }
  100% { 
    transform: scale(0.97) rotate(calc(var(--floatTilt, 0.5) * -0.3deg)); 
    box-shadow: 0 10px 25px rgba(15, 23, 42, 0.08);
  }
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
