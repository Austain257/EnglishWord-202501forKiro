<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- 背景装饰 -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-violet-50/80 via-blue-50/60 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 -right-20 w-96 h-96 bg-purple-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-48 -left-24 w-80 h-80 bg-indigo-100/40 rounded-full blur-3xl pointer-events-none"></div>

    <div class="relative z-10 flex flex-col min-h-screen">
      <!-- 顶部导航 -->
      <nav class="px-4 sm:px-6 lg:px-8 h-16 sm:h-20 flex items-center justify-between">
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
            <h1 class="text-lg font-bold text-slate-900 tracking-tight">错句本</h1>
            <div class="flex items-center gap-2 text-xs text-slate-500">
              <span class="inline-block w-1.5 h-1.5 rounded-full bg-violet-500"></span>
              <span>巩固薄弱句子，提升表达力</span>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-3">
          <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
            <span>进度</span>
            <span class="font-bold text-violet-600">{{ totalSentences === 0 ? 0 : currentIndex + 1 }}</span>
            <span class="text-slate-400">/</span>
            <span>{{ totalSentences }}</span>
          </div>

          <div class="relative bg-white/80 backdrop-blur-sm rounded-2xl border border-slate-200/60 p-1 flex items-center text-xs font-semibold">
            <button
              @click="setMode('learning')"
              :class="['px-3 sm:px-4 py-1.5 rounded-xl transition-all', mode === 'learning' ? 'bg-white text-violet-600 shadow-sm' : 'text-slate-500 hover:text-slate-900']"
            >
              错句学习
            </button>
            <button
              @click="setMode('dictation')"
              :class="['px-3 sm:px-4 py-1.5 rounded-xl transition-all', mode === 'dictation' ? 'bg-white text-sky-600 shadow-sm' : 'text-slate-500 hover:text-slate-900']"
            >
              错句听写
            </button>
          </div>

          <button
            @click="reload"
            class="hidden sm:flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-slate-600 hover:text-violet-600 hover:border-violet-200 transition-all active:scale-95"
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
            <span>复习进度</span>
            <span>{{ progress }}%</span>
          </div>
          <div class="h-2 bg-slate-100 rounded-full overflow-hidden">
            <div class="h-full bg-gradient-to-r from-violet-500 to-sky-500 rounded-full transition-all duration-300 ease-out" :style="{ width: `${progress}%` }"></div>
          </div>
        </div>

        <!-- 状态区域 -->
        <div v-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[360px]">
          <div class="w-12 h-12 border-4 border-violet-100 border-t-violet-500 rounded-full animate-spin mb-4"></div>
          <p class="text-slate-400 text-sm font-medium animate-pulse">正在拉取错句列表...</p>
        </div>

        <div v-else-if="!currentSentence" class="flex-1 flex flex-col items-center justify-center min-h-[360px] text-center">
          <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
            <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5h6m-3-3v6m9-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-2">当前暂无错句</h3>
          <p class="text-slate-500 mb-8 max-w-xs mx-auto">继续保持！若已做更改请重新加载或前往句子学习刷新数据。</p>
          <button
            @click="reload"
            class="px-8 py-3 bg-gradient-to-r from-violet-500 to-sky-500 hover:opacity-90 text-white rounded-xl font-semibold shadow-lg shadow-violet-300/50 transition-all hover:-translate-y-0.5"
          >
            重新加载
          </button>
        </div>

        <!-- 错句学习模式 -->
        <div v-else-if="mode === 'learning'" class="flex-1 flex flex-col justify-center py-6">
          <div class="relative group perspective-1000 w-full max-w-2xl mx-auto">
            <div class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500">
              <div class="absolute top-6 right-6">
                <span
                  class="px-3 py-1 rounded-full text-xs font-bold tracking-wide border"
                  :class="currentSentence.isGrasp === 1 ? 'bg-emerald-50 text-emerald-600 border-emerald-100' : 'bg-violet-50 text-violet-600 border-violet-100'"
                >
                  {{ currentSentence.isGrasp === 1 ? '已掌握' : '待攻克' }}
                </span>
              </div>

              <div class="p-8 sm:p-12 flex flex-col gap-10">
                <div class="space-y-6">
                  <p class="text-sm uppercase tracking-[0.4em] text-slate-400 text-center">英文句子</p>
                  <p class="text-2xl sm:text-3xl text-center font-semibold text-slate-900 leading-relaxed">
                    {{ currentSentence.sentence }}
                  </p>
                  <div class="flex justify-center">
                    <button
                      @click="playSentence"
                      class="px-4 py-2 rounded-full bg-violet-50 text-violet-600 hover:bg-violet-100 transition-all flex items-center gap-2"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                      </svg>
                      播放句子
                    </button>
                  </div>
                </div>

                <div class="space-y-3 text-center">
                  <p class="text-xs uppercase tracking-[0.4em] text-slate-400">中文释义</p>
                  <p class="text-lg sm:text-xl text-slate-600 font-medium leading-relaxed">{{ currentSentence.chinese || '暂无释义' }}</p>
                </div>

                <div class="grid grid-cols-2 gap-4 w-full max-w-sm mx-auto">
                  <div class="flex flex-col items-center p-3 bg-slate-50 rounded-xl border border-slate-100">
                    <span class="text-xs text-slate-400 uppercase font-bold tracking-wider mb-1">错误次数</span>
                    <span class="text-lg font-bold text-rose-500">{{ currentSentence.errorTimes || 0 }} 次</span>
                  </div>
                  <div class="flex flex-col items-center p-3 bg-slate-50 rounded-xl border border-slate-100">
                    <span class="text-xs text-slate-400 uppercase font-bold tracking-wider mb-1">出现次数</span>
                    <span class="text-lg font-bold text-slate-700">{{ currentSentence.times || 0 }} 次</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 控制按钮 -->
          <div class="mt-8 flex flex-col sm:flex-row items-stretch sm:items-center justify-center gap-3 sm:gap-6">
            <button
              @click="prevSentence"
              :disabled="!hasPrev"
              class="w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-4 rounded-2xl font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed text-slate-600 bg-white border border-slate-200 shadow-sm hover:bg-slate-50 active:scale-95"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
              </svg>
              上一句
            </button>

            <button
              @click="handleNotGrasped"
              class="w-full sm:flex-1 px-6 py-4 rounded-2xl font-bold text-violet-600 bg-violet-50 border border-violet-100 hover:bg-violet-100 transition-all active:scale-95"
            >
              仍需巩固
            </button>

            <button
              @click="handleGrasped"
              class="w-full sm:flex-1 px-6 py-4 rounded-2xl font-bold text-white bg-gradient-to-r from-violet-500 to-sky-500 shadow-lg shadow-violet-300/40 hover:opacity-90 transition-all active:scale-95"
            >
              标记已掌握
            </button>

            <button
              @click="nextSentence"
              :disabled="!hasNext"
              class="w-full sm:w-auto flex items-center justify-center gap-2 px-8 py-4 rounded-2xl font-bold text-white bg-slate-900 hover:bg-slate-800 transition-all disabled:opacity-40 disabled:cursor-not-allowed active:scale-95"
            >
              下一句
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 错句听写模式 -->
        <div v-else class="flex-1 flex flex-col justify-center py-6">
          <div class="relative w-full max-w-2xl mx-auto">
            <div class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500">
              <div class="p-8 sm:p-12 flex flex-col items-center text-center space-y-8">
                <div class="flex flex-col gap-3">
                  <p class="text-xs uppercase tracking-[0.4em] text-slate-400">听写模式</p>
                  <div class="bg-slate-50 rounded-2xl p-1 border border-slate-200 flex items-center gap-1">
                    <button
                      v-for="modeOption in dictationModes"
                      :key="modeOption.value"
                      @click="setDictationMode(modeOption.value)"
                      :class="[
                        'flex-1 px-4 py-2 rounded-xl text-xs font-semibold transition-all',
                        dictationMode === modeOption.value ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-900'
                      ]"
                    >
                      {{ modeOption.label }}
                    </button>
                  </div>
                </div>

                <div class="w-full min-h-[120px]">
                  <p class="text-xl font-medium text-slate-900 leading-relaxed">
                    {{ dictationMode === 'en2zh' ? currentSentence.sentence : currentSentence.chinese }}
                  </p>
                </div>

                <div class="w-full max-w-sm mx-auto relative">
                  <textarea
                    ref="inputRef"
                    v-model="userInput"
                    :placeholder="dictationMode === 'en2zh' ? '请输入中文翻译...' : '请输入英文句子...'"
                    class="w-full text-base sm:text-lg text-slate-800 px-6 py-4 bg-slate-50 border-2 rounded-2xl transition-all focus:outline-none focus:bg-white resize-none"
                    rows="3"
                    :class="{
                      'border-slate-100 focus:border-sky-500': !showAnswer,
                      'border-emerald-500 bg-emerald-50 text-emerald-700 animate-shake-correct': showAnswer && isCorrect,
                      'border-rose-500 bg-rose-50 text-rose-800 animate-shake-wrong': showAnswer && !isCorrect
                    }"
                    :disabled="showAnswer"
                  ></textarea>
                </div>

                <div class="min-h-[80px] flex flex-col items-center justify-center text-sm text-slate-500">
                  <div v-if="showAnswer" class="space-y-2 text-center">
                    <p class="text-base font-semibold" :class="isCorrect ? 'text-emerald-600' : 'text-rose-600'">
                      正确答案：{{ dictationMode === 'en2zh' ? currentSentence.chinese : currentSentence.sentence }}
                    </p>
                    <p class="text-xs text-slate-400">原句：{{ currentSentence.sentence }}</p>
                    <p class="text-xs text-slate-400">中文：{{ currentSentence.chinese }}</p>
                  </div>
                </div>
              </div>

              <div class="bg-slate-50/60 border-t border-slate-100 p-6">
                <div v-if="!showAnswer" class="flex gap-4">
                  <button
                    @click="handleDictationWrong"
                    class="w-1/3 py-4 rounded-xl font-bold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-all active:scale-95"
                  >
                    不会
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
                    @click="nextSentence"
                    class="flex-1 py-4 rounded-xl font-bold text-white bg-slate-800 hover:bg-slate-900 transition-all active:scale-95"
                  >
                    {{ hasNext ? '下一句' : '完成重练' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { sentenceService } from '@/services/sentence.service'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const authStore = useAuthStore()
const { success, error, info } = useToast()

const loading = ref(false)
const sentences = ref([])
const currentIndex = ref(0)
const mode = ref('learning')
const dictationMode = ref('en2zh')
const dictationModes = [
  { label: '英译汉', value: 'en2zh' },
  { label: '汉译英', value: 'zh2en' }
]
const userInput = ref('')
const showAnswer = ref(false)
const isCorrect = ref(false)
const isPlaying = ref(false)
const inputRef = ref(null)

const currentSentence = computed(() => sentences.value[currentIndex.value] || null)
const totalSentences = computed(() => sentences.value.length)
const hasNext = computed(() => currentIndex.value < totalSentences.value - 1)
const hasPrev = computed(() => currentIndex.value > 0)
const progress = computed(() => {
  if (totalSentences.value === 0) return 0
  return Math.round(((currentIndex.value + 1) / totalSentences.value) * 100)
})

const ensureContext = () => {
  if (!authStore.user?.id) throw new Error('用户未登录')
}

const loadErrorSentences = async () => {
  try {
    ensureContext()
    loading.value = true
    const res = await sentenceService.getErrorSentences(authStore.user.id)
    sentences.value = Array.isArray(res.data) ? res.data : []
    currentIndex.value = 0
    resetDictationState()
  } catch (err) {
    console.error(err)
    error(err.message || '获取错句列表失败')
    sentences.value = []
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

const reload = () => loadErrorSentences()

const setMode = (target) => {
  mode.value = target
  resetDictationState()
  if (target === 'dictation') {
    nextTick(() => inputRef.value?.focus())
  }
}

const setDictationMode = (target) => {
  if (dictationMode === target) return
  dictationMode.value = target
  resetDictationState()
}

const nextSentence = () => {
  if (hasNext.value) {
    currentIndex.value += 1
  } else if (totalSentences.value > 0) {
    info('本组错句已完成，重新开始循环')
    currentIndex.value = 0
  }
  resetDictationState()
}

const prevSentence = () => {
  if (hasPrev.value) {
    currentIndex.value -= 1
    resetDictationState()
  }
}

const handleRemovalAfterGrasp = () => {
  sentences.value.splice(currentIndex.value, 1)
  if (currentIndex.value >= sentences.value.length) {
    currentIndex.value = Math.max(sentences.value.length - 1, 0)
  }
  resetDictationState()
}

const handleGrasped = async () => {
  if (!currentSentence.value) return
  try {
    await sentenceService.markAsGrasped(currentSentence.value.id || currentSentence.value.sentenceId || currentSentence.value.sentenceId)
    success('已标记掌握，移出错句本')
    handleRemovalAfterGrasp()
  } catch (err) {
    console.error(err)
    error('标记失败：' + (err.message || ''))
  }
}

const handleNotGrasped = async () => {
  if (!currentSentence.value) return
  try {
    await sentenceService.markAsError(currentSentence.value.id || currentSentence.value.sentenceId || currentSentence.value.sentenceId)
    currentSentence.value.errorTimes = (currentSentence.value.errorTimes || 0) + 1
    info('已记录为未掌握，继续巩固')
  } catch (err) {
    console.error(err)
    error('标记失败：' + (err.message || ''))
  }
}

const handleDictationWrong = async () => {
  if (!currentSentence.value) return
  await handleNotGrasped()
  isCorrect.value = false
  showAnswer.value = true
}

const checkAnswer = () => {
  if (!currentSentence.value || !userInput.value.trim()) return
  const correct = dictationMode.value === 'en2zh' ? currentSentence.value.chinese : currentSentence.value.sentence
  const normalize = (text) => (text || '').replace(/\s+/g, '').toLowerCase()
  isCorrect.value = normalize(userInput.value) === normalize(correct)
  showAnswer.value = true
  if (!isCorrect.value) {
    handleNotGrasped()
  }
}

const playSentence = () => {
  if (!currentSentence.value?.sentence || isPlaying.value) return
  if (!('speechSynthesis' in window)) {
    error('当前浏览器不支持语音播放')
    return
  }
  const text = currentSentence.value.sentence
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
  () => authStore.user?.id,
  (newVal, oldVal) => {
    if (newVal && newVal !== oldVal) {
      loadErrorSentences()
    }
  }
)

onMounted(async () => {
  await loadErrorSentences()
})
</script>

<style scoped>
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