<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- 背景装饰 -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-indigo-50/80 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 -right-20 w-96 h-96 bg-violet-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-40 -left-20 w-72 h-72 bg-purple-100/40 rounded-full blur-3xl pointer-events-none"></div>

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
          <h1 class="text-lg font-bold text-slate-900 tracking-tight">句子听写</h1>
          <button 
            @click="toggleMode"
            class="flex items-center gap-2 text-xs text-slate-500 hover:text-slate-900 transition-colors mt-0.5"
          >
             <span class="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
             <span>{{ dictationMode === 'zh2en' ? '汉译英' : '英译汉' }}</span>
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
          <span class="text-indigo-600 font-bold">{{ currentIndex + 1 }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ totalSentences }}</span>
        </div>

        <!-- 当前范围 -->
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>当前范围</span>
          <span class="text-indigo-600 font-bold">{{ sentenceStore.learningRange.start }}-{{ sentenceStore.learningRange.end }}</span>
        </div>

        <button 
          @click="showRangeModal = true"
          class="flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-slate-600 hover:text-indigo-600 hover:border-indigo-200 transition-all active:scale-95"
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
          <span>{{ Math.round(((currentIndex + 1) / totalSentences) * 100) }}%</span>
        </div>
        <div class="h-2 bg-slate-100 rounded-full overflow-hidden">
          <div 
            class="h-full bg-indigo-500 rounded-full transition-all duration-300 ease-out"
            :style="{ width: `${((currentIndex + 1) / totalSentences) * 100}%` }"
          ></div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[400px]">
        <div class="w-12 h-12 border-4 border-indigo-100 border-t-indigo-500 rounded-full animate-spin mb-4"></div>
        <p class="text-slate-400 text-sm font-medium animate-pulse">正在加载句子数据...</p>
      </div>

      <!-- 无数据状态 -->
      <div v-else-if="!currentSentence" class="flex-1 flex flex-col items-center justify-center min-h-[400px] text-center">
        <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
          </svg>
        </div>
        <h3 class="text-xl font-bold text-slate-900 mb-2">暂无听写句子</h3>
        <p class="text-slate-500 mb-8 max-w-xs mx-auto">当前范围内没有可听写的句子，请尝试调整范围或重新加载。</p>
        <button 
          @click="loadSentences" 
          class="px-8 py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-semibold shadow-lg shadow-indigo-500/30 transition-all hover:-translate-y-0.5"
        >
          重新加载
        </button>
      </div>

      <!-- 听写卡片 -->
      <div v-else class="flex-1 flex flex-col justify-center py-4 sm:py-8">
        <div class="relative group w-full max-w-3xl mx-auto">
          <!-- 卡片容器 -->
          <div 
            class="relative bg-white rounded-[2rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500"
          >
            <div class="p-8 sm:p-12 flex flex-col items-center text-center">
              <!-- 题目显示 -->
              <div class="mb-8 w-full">
                <div class="flex items-center justify-center gap-2 mb-4">
                  <span class="text-sm font-bold text-indigo-500 uppercase tracking-wider">QUESTION</span>
                  <!-- 仅在英译汉模式下显示播放按钮 -->
                  <button 
                    v-if="dictationMode === 'en2zh'"
                    @click="playPronunciation"
                    class="p-1.5 rounded-full bg-indigo-50 text-indigo-500 hover:bg-indigo-100 hover:text-indigo-600 transition-colors"
                    title="播放发音"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" /></svg>
                  </button>
                </div>
                
                <div class="max-h-[30vh] overflow-y-auto custom-scrollbar px-2">
                   <h2 class="text-2xl sm:text-3xl font-medium text-slate-900 leading-relaxed break-words">
                     {{ getQuestionText() }}
                   </h2>
                </div>
              </div>

              <!-- 输入区域 -->
              <div class="w-full max-w-xl mx-auto relative group/input">
                 <textarea
                    ref="answerInput"
                    v-model="userAnswer"
                    rows="3"
                    :placeholder="getPlaceholderText()"
                    class="w-full px-6 py-4 text-lg font-medium bg-slate-50 border-2 rounded-2xl transition-all focus:outline-none focus:bg-white resize-none"
                    :class="{
                      'border-slate-100 focus:border-indigo-500 text-slate-800': !showResult,
                      'border-emerald-500 bg-emerald-50 text-emerald-800': showResult && isCorrect,
                      'border-rose-500 bg-rose-50 text-rose-800': showResult && !isCorrect
                    }"
                    @keydown.enter.prevent="submitAnswer"
                    :disabled="showResult"
                  ></textarea>
              </div>

              <!-- 结果反馈 -->
              <div v-if="showResult" class="mt-8 w-full max-w-xl animate-scale-in">
                 <div v-if="isCorrect" class="flex flex-col items-center text-emerald-600">
                    <div class="w-12 h-12 bg-emerald-100 rounded-full flex items-center justify-center mb-3">
                       <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
                    </div>
                    <p class="font-bold text-lg">回答正确！</p>
                 </div>
                 <div v-else class="flex flex-col items-center w-full">
                    <div class="flex items-center gap-2 text-rose-600 mb-4">
                       <div class="w-8 h-8 bg-rose-100 rounded-full flex items-center justify-center">
                          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M6 18L18 6M6 6l12 12" /></svg>
                       </div>
                       <span class="font-bold">回答错误</span>
                    </div>
                    
                    <div class="bg-slate-50 px-6 py-4 rounded-xl border border-slate-100 w-full text-left">
                       <p class="text-xs text-slate-400 uppercase font-bold tracking-wider mb-2">CORRECT ANSWER</p>
                       <div class="flex items-start justify-between gap-4">
                         <p class="text-lg font-medium text-slate-800 break-words flex-1">{{ getCorrectAnswer() }}</p>
                         <button 
                            @click="playPronunciation"
                            class="p-1.5 rounded-full bg-indigo-50 text-indigo-500 hover:bg-indigo-100 transition-colors shrink-0"
                            title="播放发音"
                          >
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" /></svg>
                          </button>
                       </div>
                    </div>
                 </div>
              </div>

            </div>
          </div>
        </div>
      </div>

      <!-- 底部控制栏 -->
      <div class="mt-auto pt-6 flex items-center justify-center gap-3 sm:gap-4 flex-wrap px-4">
         <!-- 上一个按钮 -->
         <button 
           @click="prevSentence"
           :disabled="!hasPrev"
           class="px-6 py-3 rounded-xl font-semibold text-slate-600 bg-white border border-slate-200 hover:bg-slate-50 transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
         >
           上一个
         </button>

         <template v-if="!showResult">
            <!-- 还不会 -->
            <button 
               @click="markDontKnow"
               class="px-6 py-3 rounded-xl font-semibold text-rose-600 bg-white border border-rose-200 hover:bg-rose-50 transition-all active:scale-95"
            >
               还不会
            </button>
            <button 
               @click="showAnswer"
               class="px-6 py-3 rounded-xl font-semibold text-slate-600 bg-white border border-slate-200 hover:bg-slate-50 transition-all active:scale-95"
            >
               查看答案
            </button>
            <button 
               @click="submitAnswer"
               :disabled="!userAnswer.trim()"
               class="px-8 py-3 rounded-xl font-bold text-white bg-indigo-600 shadow-lg shadow-indigo-500/30 hover:bg-indigo-500 hover:-translate-y-0.5 transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
            >
               提交答案
            </button>
         </template>
         <template v-else>
            <button 
               v-if="!isCorrect"
               @click="retry"
               class="px-6 py-3 rounded-xl font-semibold text-slate-600 bg-white border border-slate-200 hover:bg-slate-50 transition-all active:scale-95"
            >
               重试
            </button>
            <button 
               @click="nextSentence"
               class="px-8 py-3 rounded-xl font-bold text-white bg-indigo-600 shadow-lg shadow-indigo-500/30 hover:bg-indigo-500 hover:-translate-y-0.5 transition-all active:scale-95"
            >
               {{ hasNext ? '下一个' : '完成' }}
            </button>
         </template>
      </div>
    </main>

    <!-- 范围设置模态框 -->
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
              v-model.number="tempRange.start"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
          <div class="flex items-end pb-4 text-slate-300">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" /></svg>
          </div>
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">End</label>
            <input 
              v-model.number="tempRange.end"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
        </div>

        <div class="flex gap-3">
          <button @click="cancelRangeSelection" class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors">取消</button>
          <button @click="confirmRangeSelection" class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-indigo-600 hover:bg-indigo-700 shadow-lg shadow-indigo-500/20 transition-colors">确认</button>
        </div>
      </div>
    </div>

    <!-- 庆祝彩带 -->
    <div v-if="showConfetti" class="fixed inset-0 pointer-events-none z-50 overflow-hidden">
       <div v-for="n in 30" :key="n" class="confetti" :style="getConfettiStyle(n)"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSentenceStore } from '@/stores/sentence'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const sentenceStore = useSentenceStore()
const { success, error, showToast } = useToast()

// 状态
const showRangeModal = ref(false)
const dictationMode = ref('zh2en') // 默认 'zh2en'
const userAnswer = ref('')
const showResult = ref(false)
const isCorrect = ref(false)
const showConfetti = ref(false)
const answerInput = ref(null)
const tempRange = ref({ start: 1, end: 30 })

// 计算属性
const loading = computed(() => sentenceStore.loading)
const currentSentence = computed(() => sentenceStore.currentSentence)
const currentIndex = computed(() => sentenceStore.currentIndex)
const totalSentences = computed(() => sentenceStore.totalSentences)
const hasNext = computed(() => sentenceStore.hasNext)
const hasPrev = computed(() => sentenceStore.hasPrev)

// 方法
const goBack = () => router.push('/')

const loadSentences = async (range = null) => {
  try {
    await sentenceStore.fetchSentences(range)
    if (sentenceStore.totalSentences === 0) {
      showToast('当前范围内没有可听写的句子', 'warning')
    }
  } catch (err) {
    showToast('加载句子失败：' + err.message, 'error')
  }
}

const toggleMode = () => {
  dictationMode.value = dictationMode.value === 'en2zh' ? 'zh2en' : 'en2zh'
  resetState()
}

const getQuestionText = () => {
  if (!currentSentence.value) return ''
  // zh2en (汉译英): 显示中文
  // en2zh (英译汉): 显示英文
  return dictationMode.value === 'zh2en' ? currentSentence.value.chinese : currentSentence.value.sentence
}

const getPlaceholderText = () => {
  return dictationMode.value === 'zh2en' ? '输入英文句子' : '输入中文翻译'
}

const getCorrectAnswer = () => {
  if (!currentSentence.value) return ''
  return dictationMode.value === 'zh2en' ? currentSentence.value.sentence : currentSentence.value.chinese
}

const playPronunciation = () => {
  if (!currentSentence.value) return
  const text = currentSentence.value.sentence
  if ('speechSynthesis' in window) {
    window.speechSynthesis.cancel()
    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = 'en-US'
    window.speechSynthesis.speak(utterance)
  }
}

const submitAnswer = async () => {
  if (!userAnswer.value.trim()) return
  
  const correct = getCorrectAnswer()
  // 去除标点和首尾空格进行比较
  const cleanUser = userAnswer.value.trim().toLowerCase().replace(/[.,?!。，？！]/g, '')
  const cleanCorrect = correct.trim().toLowerCase().replace(/[.,?!。，？！]/g, '')
  
  // 简单模糊匹配
  const passed = cleanUser === cleanCorrect
  
  isCorrect.value = passed
  showResult.value = true
  
  if (passed) {
     triggerConfetti()
     await sentenceStore.markCurrentAsGrasped()
     // 自动下一题
     setTimeout(() => {
        if (showResult.value && isCorrect.value && hasNext.value) {
           nextSentence()
        }
     }, 1500)
  } else {
     await sentenceStore.markCurrentAsError()
  }
}

const showAnswer = async () => {
   showResult.value = true
   isCorrect.value = false
   await sentenceStore.markCurrentAsError()
}

const markDontKnow = async () => {
  try {
    await sentenceStore.markCurrentAsError()
    showToast('已加入错句本', 'info')
    if (hasNext.value) {
      nextSentence()
    } else {
      success('恭喜完成本组听写！')
      goBack()
    }
  } catch (err) {
    showToast('操作失败：' + err.message, 'error')
  }
}

const retry = () => {
   showResult.value = false
   isCorrect.value = false
   userAnswer.value = ''
   nextTick(() => answerInput.value?.focus())
}

const nextSentence = () => {
   if (hasNext.value) {
      sentenceStore.nextSentence()
      resetState()
   } else {
      success('恭喜完成本组听写！')
      goBack()
   }
}

const prevSentence = () => {
  if (hasPrev.value) {
    sentenceStore.prevSentence()
    resetState()
  }
}

const resetState = () => {
   userAnswer.value = ''
   showResult.value = false
   isCorrect.value = false
   showConfetti.value = false
   nextTick(() => answerInput.value?.focus())
}

const confirmRangeSelection = async () => {
  if (tempRange.value.start >= tempRange.value.end) {
    return showToast('起始位置必须小于结束位置', 'error')
  }
  
  try {
    await sentenceStore.fetchSentences(tempRange.value)
    showRangeModal.value = false
    resetState()
    success(`范围已更新：${tempRange.value.start}-${tempRange.value.end}`)
  } catch (err) {
    showToast('设置范围失败：' + err.message, 'error')
  }
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  tempRange.value = { ...sentenceStore.learningRange }
}

const triggerConfetti = () => {
   showConfetti.value = true
   setTimeout(() => { showConfetti.value = false }, 2000)
}

const getConfettiStyle = (i) => {
   const colors = ['#6366f1', '#8b5cf6', '#d946ef', '#f43f5e', '#10b981']
   const left = Math.random() * 100 + '%'
   const delay = Math.random() * 0.5 + 's'
   const duration = Math.random() * 1 + 1.5 + 's'
   const bg = colors[Math.floor(Math.random() * colors.length)]
   return {
      left,
      animationDelay: delay,
      animationDuration: duration,
      backgroundColor: bg
   }
}

// 键盘事件
const handleKeydown = (e) => {
   if (showRangeModal.value) return
   if (e.key === 'Escape') goBack()
}

onMounted(async () => {
  window.addEventListener('keydown', handleKeydown)
  tempRange.value = { ...sentenceStore.learningRange }
  
  if (sentenceStore.totalSentences === 0) {
    await loadSentences()
  }
  
  nextTick(() => answerInput.value?.focus())
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  window.speechSynthesis.cancel()
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

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}

.confetti {
   position: absolute;
   top: -10px;
   width: 10px;
   height: 10px;
   border-radius: 4px;
   animation: fall linear forwards;
}

@keyframes fall {
   to {
      transform: translateY(100vh) rotate(720deg);
   }
}
</style>
