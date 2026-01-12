<template>
  <div class="review-complete-section">
    <div class="review-progress mb-6">
      <div class="progress-steps flex items-center justify-center space-x-2">
        <div 
          v-for="round in 2" 
          :key="round"
          class="step flex items-center"
          :class="{
            'completed': isRoundCompleted(round),
            'current': currentRound === round,
            'pending': currentRound < round
          }"
        >
          <div class="step-circle w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-all">
            <span v-if="isRoundCompleted(round)" class="text-white">✓</span>
            <span v-else>{{ round }}</span>
          </div>
          <div class="step-label ml-2 text-sm font-medium">
            第{{ round }}轮复习
          </div>
          <div v-if="round < 2" class="step-connector w-8 h-0.5 ml-4 transition-all"></div>
        </div>
        <div class="step flex items-center ml-4" :class="{ 'completed': allRoundsCompleted }">
          <div class="step-circle w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold transition-all">
            <span v-if="allRoundsCompleted" class="text-white">🎉</span>
            <span v-else>🔓</span>
          </div>
          <div class="step-label ml-2 text-sm font-medium">
            解锁功能
          </div>
        </div>
      </div>
    </div>
    
    <div class="complete-actions">
      <button 
        @click="markComplete"
        :disabled="loading || isCurrentRoundCompleted"
        class="complete-btn w-full py-4 bg-gradient-to-r from-emerald-600 to-green-600 text-white rounded-2xl font-bold text-lg shadow-lg shadow-emerald-500/30 hover:shadow-emerald-500/40 hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
      >
        <span v-if="loading">标记中...</span>
        <span v-else-if="isCurrentRoundCompleted">✓ 已完成第{{ currentRound }}轮复习</span>
        <span v-else>标记第{{ currentRound }}轮复习已完成</span>
      </button>
      
      <div v-if="allRoundsCompleted" class="mt-4 p-4 bg-gradient-to-r from-emerald-50 to-green-50 border-2 border-emerald-200 rounded-xl">
        <div class="flex items-center justify-center text-emerald-700">
          <svg class="w-6 h-6 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-bold">所有复习任务已完成，功能已解锁！</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useWordStudyStore } from '@/stores/wordStudy'
import { useAuthStore } from '@/stores/auth'

const props = defineProps({
  sessionId: {
    type: Number,
    required: true
  },
  studyRange: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['review-completed', 'all-completed'])

// 状态管理
const wordStudyStore = useWordStudyStore()
const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const currentRound = ref(1)
const completedRounds = ref({
  1: false,
  2: false
})

// 计算属性
const isCurrentRoundCompleted = computed(() => {
  return completedRounds.value[currentRound.value]
})

const allRoundsCompleted = computed(() => {
  return completedRounds.value[1] && completedRounds.value[2]
})

// 方法
const isRoundCompleted = (round) => {
  return completedRounds.value[round]
}

const markComplete = async () => {
  if (loading.value || isCurrentRoundCompleted.value) return
  
  try {
    loading.value = true
    
    const success = await wordStudyStore.markReviewComplete({
      userId: authStore.user?.id,
      sessionId: props.sessionId,
      reviewRound: currentRound.value
      // 移除completedTime，让后端使用服务器当前时间，避免时区问题
    })
    
    if (success) {
      // 标记当前轮次为已完成
      completedRounds.value[currentRound.value] = true
      
      emit('review-completed', {
        round: currentRound.value,
        sessionId: props.sessionId
      })
      
      // 如果还有下一轮，则切换到下一轮
      if (currentRound.value < 2) {
        currentRound.value++
      } else {
        // 所有轮次完成
        emit('all-completed', {
          sessionId: props.sessionId,
          totalRounds: 2
        })
      }
      
      console.log(`第${currentRound.value - (currentRound.value < 2 ? 0 : 1)}轮复习已完成`)
    }
  } catch (error) {
    console.error('标记复习完成失败:', error)
  } finally {
    loading.value = false
  }
}

// 检查当前会话的复习完成状态
const checkCompletedRounds = async () => {
  try {
    const records = await wordStudyStore.getTodayRecords(authStore.user?.id)
    const currentSession = records.find(record => record.id === props.sessionId)
    
    if (currentSession) {
      completedRounds.value[1] = !!currentSession.round1ReviewTime
      completedRounds.value[2] = !!currentSession.round2ReviewTime
      
      // 设置当前应该进行的轮次
      if (completedRounds.value[2]) {
        currentRound.value = 2 // 全部完成
      } else if (completedRounds.value[1]) {
        currentRound.value = 2 // 进行第二轮
      } else {
        currentRound.value = 1 // 进行第一轮
      }
    }
  } catch (error) {
    console.error('检查复习状态失败:', error)
  }
}

// 生命周期
onMounted(() => {
  if (props.sessionId) {
    checkCompletedRounds()
  }
})
</script>

<style scoped>
.step.completed .step-circle {
  @apply bg-emerald-500 border-emerald-500;
}

.step.current .step-circle {
  @apply bg-blue-500 border-blue-500 text-white;
}

.step.pending .step-circle {
  @apply bg-slate-200 border-slate-300 text-slate-500;
}

.step.completed .step-connector {
  @apply bg-emerald-500;
}

.step.current .step-connector {
  @apply bg-slate-300;
}

.step.pending .step-connector {
  @apply bg-slate-200;
}

.step-label {
  @apply transition-colors;
}

.step.completed .step-label {
  @apply text-emerald-700;
}

.step.current .step-label {
  @apply text-blue-700;
}

.step.pending .step-label {
  @apply text-slate-500;
}
</style>
