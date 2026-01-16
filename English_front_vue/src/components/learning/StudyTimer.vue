<template>
  <div>
    <div v-if="!compact" class="study-timer-container bg-white rounded-3xl shadow-lg p-6 sm:p-8">
      <!-- 计时器显示区域 -->
      <div class="timer-display flex flex-col items-center mb-8">
        <!-- 圆形进度条 -->
        <div class="relative w-48 h-48 sm:w-56 sm:h-56">
          <svg class="w-full h-full transform -rotate-90" viewBox="0 0 200 200">
            <!-- 背景圆环 -->
            <circle
              cx="100"
              cy="100"
              r="90"
              stroke="#f1f5f9"
              stroke-width="8"
              fill="none"
            />
            <!-- 进度圆环 -->
            <circle
              cx="100"
              cy="100"
              r="90"
              stroke="#3b82f6"
              stroke-width="8"
              fill="none"
              stroke-linecap="round"
              :stroke-dasharray="circumference"
              :stroke-dashoffset="strokeDashoffset"
              class="transition-all duration-1000 ease-out"
            />
          </svg>
          
          <!-- 时间显示 -->
          <div class="absolute inset-0 flex flex-col items-center justify-center">
            <div class="text-4xl sm:text-5xl font-bold text-slate-900 mb-2">
              {{ formatTime(remainingTime) }}
            </div>
            <div class="text-sm text-slate-500 font-medium">
              {{ timerStatus === 'running' ? '剩余时间' : '学习时长' }}
            </div>
          </div>
        </div>
      </div>
      
      <!-- 学习进度信息 -->
      <div class="progress-info bg-slate-50 rounded-2xl p-4 mb-6">
        <div class="grid grid-cols-2 gap-4 text-center">
          <div>
            <div class="text-2xl font-bold text-blue-600">{{ wordsLearned }}</div>
            <div class="text-xs text-slate-500">已学单词</div>
          </div>
          <div>
            <div class="text-2xl font-bold text-emerald-600">{{ currentRange }}</div>
            <div class="text-xs text-slate-500">学习范围</div>
          </div>
        </div>
      </div>
      
      <!-- 控制按钮 -->
      <div class="timer-controls flex flex-col gap-3">
        <!-- 开始学习按钮 -->
        <button
          v-if="timerStatus === 'idle'"
          @click="startTimer"
          :disabled="!canStart"
          class="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-2xl font-bold text-lg shadow-lg shadow-blue-500/30 hover:shadow-blue-500/40 hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
        >
          开始学习 (30分钟)
        </button>
        
        <!-- 运行中的控制按钮 -->
        <div v-else-if="timerStatus === 'running'" class="flex gap-3">
          <button
            @click="pauseTimer"
            class="flex-1 py-3 bg-amber-500 hover:bg-amber-600 text-white rounded-xl font-semibold transition-colors"
          >
            暂停
          </button>
          <button
            @click="showEndConfirm = true"
            class="flex-1 py-3 bg-rose-500 hover:bg-rose-600 text-white rounded-xl font-semibold transition-colors"
          >
            提前结束
          </button>
        </div>
        
        <!-- 暂停状态的控制按钮 -->
        <div v-else-if="timerStatus === 'paused'" class="flex gap-3">
          <button
            @click="resumeTimer"
            class="flex-1 py-3 bg-emerald-500 hover:bg-emerald-600 text-white rounded-xl font-semibold transition-colors"
          >
            继续学习
          </button>
          <button
            @click="showEndConfirm = true"
            class="flex-1 py-3 bg-rose-500 hover:bg-rose-600 text-white rounded-xl font-semibold transition-colors"
          >
            结束学习
          </button>
        </div>
        
        <!-- 时间到了的结束按钮 -->
        <button
          v-else-if="timerStatus === 'finished'"
          @click="endStudySession"
          class="w-full py-4 bg-gradient-to-r from-emerald-600 to-green-600 text-white rounded-2xl font-bold text-lg shadow-lg shadow-emerald-500/30 hover:shadow-emerald-500/40 hover:-translate-y-0.5 transition-all animate-pulse"
        >
          标记并结束本轮学习
        </button>
      </div>
    </div>

    <div
      v-else
      class="rounded-2xl bg-white/90 border border-slate-100 shadow-sm p-4 sm:p-5"
    >
      <!-- 收起态 -->
      <div
        v-if="isCollapsed"
        class="flex items-center justify-between gap-3 cursor-pointer select-none"
        style="touch-action: manipulation;"
        @click.stop="toggleCollapsed(false)"
      >
        <div class="flex-1">
          <p class="text-[11px] uppercase tracking-widest text-slate-400 font-semibold mb-1">
            今日学习
          </p>
          <h3 class="text-base sm:text-xl font-bold text-slate-900">
            {{ timerLabel }}
          </h3>
          <p class="text-xs text-slate-500">
            {{ currentRange }}
          </p>
          <div class="mt-3 space-y-1">
            <div class="flex justify-between text-xs text-slate-400">
              <span>时间流逝</span>
              <span>{{ Math.round(progress) }}%</span>
            </div>
            <div class="h-2 rounded-full overflow-hidden bg-slate-100">
              <div
                class="h-full rounded-full bg-gradient-to-r from-blue-500 via-indigo-500 to-purple-500 transition-all duration-500"
                :style="{ width: `${Math.min(progress, 100)}%` }"
              ></div>
            </div>
          </div>
        </div>
        <div class="text-right">
          <p class="text-[11px] text-slate-400 mb-1">剩余</p>
          <div class="text-xl sm:text-2xl font-bold text-slate-900 tabular-nums">
            {{ formatTime(remainingTime) }}
          </div>
          <div class="mt-2 inline-flex items-center text-xs text-blue-600 font-semibold">
            展开
            <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 展开态 -->
      <div
        v-else
        class="flex flex-col gap-4 cursor-pointer select-none"
        style="touch-action: manipulation;"
        @click.stop="toggleCollapsed(true)"
      >
        <div class="flex items-start justify-between">
          <div>
            <p class="text-xs uppercase tracking-widest text-slate-400 font-semibold mb-1">
              今日学习
            </p>
            <h3 class="text-lg sm:text-xl font-bold text-slate-900">
              {{ timerLabel }}
            </h3>
            <p class="text-xs text-slate-500">
              {{ currentRange }}
            </p>
          </div>
          <div class="text-right flex items-start gap-2">
            <div>
              <p class="text-xs text-slate-400 mb-1">剩余</p>
              <div class="text-2xl font-bold text-slate-900 tabular-nums">
                {{ formatTime(remainingTime) }}
              </div>
            </div>
          </div>
        </div>

        <div class="space-y-1">
          <div class="flex justify-between text-xs text-slate-400">
            <span>进度</span>
            <span>{{ Math.round(progress) }}%</span>
          </div>
          <div class="h-2 rounded-full overflow-hidden bg-slate-100">
            <div
              class="h-full rounded-full bg-gradient-to-r from-blue-500 via-indigo-500 to-purple-500 transition-all duration-500"
              :style="{ width: `${Math.min(progress, 100)}%` }"
            ></div>
          </div>
        </div>

        <div class="flex gap-3 flex-wrap" @click.stop>
          <button
            v-if="timerStatus === 'idle'"
            @click="startTimer"
            :disabled="!canStart"
            class="flex-1 min-w-[140px] py-3 rounded-xl text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
          >
            开始学习 (30 分)
          </button>

          <template v-else-if="timerStatus === 'running'">
            <button
              @click="pauseTimer"
              class="flex-1 py-3 rounded-xl text-sm font-semibold text-white bg-amber-500 hover:bg-amber-600 transition-all"
            >
              暂停
            </button>
            <button
              @click="showEndConfirm = true"
              class="flex-1 py-3 rounded-xl text-sm font-semibold text-white bg-rose-500 hover:bg-rose-600 transition-all"
            >
              提前结束
            </button>
          </template>

          <template v-else-if="timerStatus === 'paused'">
            <button
              @click="resumeTimer"
              class="flex-1 py-3 rounded-xl text-sm font-semibold text-white bg-emerald-500 hover:bg-emerald-600 transition-all"
            >
              继续学习
            </button>
            <button
              @click="showEndConfirm = true"
              class="flex-1 py-3 rounded-xl text-sm font-semibold text-white bg-rose-500 hover:bg-rose-600 transition-all"
            >
              结束学习
            </button>
          </template>

          <button
            v-else-if="timerStatus === 'finished'"
            @click="endStudySession"
            class="flex-1 py-3 rounded-xl text-sm font-semibold text-white bg-emerald-600 hover:bg-emerald-700 transition-all animate-pulse"
          >
            标记并结束
          </button>
        </div>
      </div>
    </div>

    <!-- 结束确认弹窗 -->
    <div v-if="showEndConfirm" class="fixed inset-0 z-50 flex items-center justify-center px-4 bg-black/50">
      <div class="bg-white rounded-3xl p-6 max-w-sm w-full">
        <h3 class="text-xl font-bold text-slate-900 mb-4">确认结束学习</h3>
        <p class="text-slate-600 mb-6">确定要结束本次学习吗？学习记录将会保存。</p>
        <div class="flex gap-3">
          <button
            @click="showEndConfirm = false"
            class="flex-1 py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl font-semibold transition-colors"
          >
            取消
          </button>
          <button
            @click="confirmEndStudy"
            class="flex-1 py-3 bg-rose-500 hover:bg-rose-600 text-white rounded-xl font-semibold transition-colors"
          >
            确认结束
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useWordStudyStore } from '@/stores/wordStudy'
import { useWordStore } from '@/stores/word'
import { useBookStore } from '@/stores/book'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  studyDuration: {
    type: Number,
    default: 1800 // 30分钟
  },
  compact: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['study-started', 'study-ended', 'study-paused', 'study-resumed'])

// 状态管理
const wordStudyStore = useWordStudyStore()
const wordStore = useWordStore()
const bookStore = useBookStore()
const authStore = useAuthStore()

// 响应式数据
const timerStatus = ref('idle') // idle, running, paused, finished
const remainingTime = ref(props.studyDuration)
const startTime = ref(null)
const sessionId = ref(null)
const showEndConfirm = ref(false)
const timerInterval = ref(null)
const isCollapsed = ref(true)
const collapseGuardUntil = ref(0)

// 计算属性
const circumference = 2 * Math.PI * 90
const progress = computed(() => {
  return ((props.studyDuration - remainingTime.value) / props.studyDuration) * 100
})
const strokeDashoffset = computed(() => {
  return circumference - (progress.value / 100) * circumference
})
const canStart = computed(() => {
  return wordStore.learningRange.start && wordStore.learningRange.end
})
const wordsLearned = computed(() => {
  return Math.max(0, wordStore.currentIndex)
})
const currentRange = computed(() => {
  return `${wordStore.learningRange.start}-${wordStore.learningRange.end}`
})
const timerLabel = computed(() => {
  switch (timerStatus.value) {
    case 'running':
      return '学习计时中'
    case 'paused':
      return '学习已暂停'
    case 'finished':
      return '已完成 30 分钟'
    default:
      return '准备开始学习'
  }
})

// 方法
const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const toggleCollapsed = (next) => {
  const now = typeof performance !== 'undefined' ? performance.now() : Date.now()
  if (next && now < collapseGuardUntil.value) return
  isCollapsed.value = next
  if (!next) {
    collapseGuardUntil.value = now + 220 // 防抖，避免同一次点击触发收起
  }
}

const startTimer = async () => {
  try {
    // 调用后端API开始学习会话
    const response = await wordStudyStore.startStudySession({
      userId: authStore.user?.id,
      bookId: bookStore.currentBook?.id,
      startId: wordStore.learningRange.start,
      studyDuration: props.studyDuration
    })
    
    sessionId.value = response.id
    // 如果是继续已有会话，使用原有的开始时间；否则使用当前时间
    startTime.value = response.startTime ? new Date(response.startTime) : new Date()
    timerStatus.value = 'running'
    
    // 开始倒计时
    timerInterval.value = setInterval(() => {
      if (remainingTime.value > 0) {
        remainingTime.value--
      } else {
        // 时间到了
        timerStatus.value = 'finished'
        clearInterval(timerInterval.value)
        // 播放提示音或震动
        if ('vibrate' in navigator) {
          navigator.vibrate([200, 100, 200])
        }
      }
    }, 1000)
    
    emit('study-started', { sessionId: sessionId.value, startTime: startTime.value, isExistingSession: !!response.startTime })
    // success('学习计时已开始')
  } catch (err) {
    console.error('开始学习失败：', err.message)
    error('开始学习失败：' + err.message)
  }
}

const pauseTimer = () => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
    timerInterval.value = null
  }
  timerStatus.value = 'paused'
  emit('study-paused')
}

const resumeTimer = () => {
  timerStatus.value = 'running'
  timerInterval.value = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else {
      timerStatus.value = 'finished'
      clearInterval(timerInterval.value)
    }
  }, 1000)
  emit('study-resumed')
}

const endStudySession = async () => {
  await confirmEndStudy()
}

const confirmEndStudy = async () => {
  try {
    showEndConfirm.value = false
    
    if (timerInterval.value) {
      clearInterval(timerInterval.value)
      timerInterval.value = null
    }
    
    // 调用后端API结束学习会话
    await wordStudyStore.endStudySession(sessionId.value, {
      bookId: bookStore.currentBook?.id,
      endId: wordStore.learningRange.end,
      actualEndTime: new Date()
    })
    
    // 重置状态
    timerStatus.value = 'idle'
    remainingTime.value = props.studyDuration
    sessionId.value = null
    
    emit('study-ended')
    // success('学习会话已结束，请开始复习')
  } catch (err) {
    console.error('结束学习失败：', err.message)
    // error('结束学习失败：' + err.message)
  }
}

// 生命周期
onMounted(() => {
  // 检查是否有未完成的学习会话
  // 这里可以从store中恢复状态
})

onUnmounted(() => {
  if (timerInterval.value) {
    clearInterval(timerInterval.value)
  }
})

// 监听页面离开
window.addEventListener('beforeunload', (e) => {
  if (timerStatus.value === 'running') {
    e.preventDefault()
    e.returnValue = '您正在学习中，确定要离开吗？'
  }
})
</script>

<style scoped>
/* 动画效果 */
@keyframes pulse-ring {
  0% { transform: scale(1); opacity: 1; }
  100% { transform: scale(1.1); opacity: 0; }
}

.animate-pulse-ring {
  animation: pulse-ring 2s infinite;
}
</style>
