<template>
  <div class="study-time-card bg-white rounded-lg shadow-md p-4 md:p-6">
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-2">
        <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
          <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
        </div>
        <h3 class="text-lg font-semibold text-gray-800">学习时长统计</h3>
      </div>
      <button 
        @click="refreshStats" 
        :disabled="studyStatStore.loading"
        class="p-2 text-gray-400 hover:text-gray-600 transition-colors"
      >
        <svg 
          class="w-4 h-4" 
          :class="{ 'animate-spin': studyStatStore.loading }"
          fill="none" 
          stroke="currentColor" 
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
        </svg>
      </button>
    </div>

    <div class="space-y-4">
      <!-- 今日学习时长 -->
      <div class="bg-gradient-to-r from-blue-50 to-blue-100 rounded-lg p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-blue-600 font-medium">今日学习时长</p>
            <p class="text-2xl font-bold text-blue-800 mt-1">
              {{ studyStatStore.todayDisplay }}
            </p>
          </div>
          <div class="text-blue-400">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
            </svg>
          </div>
        </div>
      </div>

      <!-- 累计学习时长 -->
      <div class="bg-gradient-to-r from-green-50 to-green-100 rounded-lg p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-green-600 font-medium">累计学习时长</p>
            <p class="text-2xl font-bold text-green-800 mt-1">
              {{ studyStatStore.totalDisplay }}
            </p>
            <p class="text-xs text-green-500 mt-1">注册以来</p>
          </div>
          <div class="text-green-400">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 当前会话状态 -->
    <div v-if="studyTrackerStore.isActive" class="mt-4 bg-orange-50 border border-orange-200 rounded-lg p-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <div class="w-3 h-3 bg-orange-400 rounded-full animate-pulse"></div>
          <span class="text-sm text-orange-700 font-medium">正在学习中</span>
        </div>
        <span class="text-sm font-mono text-orange-800">
          {{ studyTrackerStore.formattedElapsed }}
        </span>
      </div>
      <p class="text-xs text-orange-600 mt-1">
        场景: {{ studyTrackerStore.studyScene }}
      </p>
    </div>

    <!-- 加载状态 -->
    <div v-if="studyStatStore.loading" class="mt-4 bg-blue-50 border border-blue-200 rounded-lg p-3">
      <div class="flex items-center space-x-2">
        <div class="w-4 h-4 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
        <span class="text-sm text-blue-700">正在更新统计数据...</span>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-if="error" class="mt-4 bg-red-50 border border-red-200 rounded-lg p-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <svg class="w-4 h-4 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <span class="text-sm text-red-700">{{ error }}</span>
        </div>
        <button @click="handleRetry" class="text-xs text-red-600 hover:text-red-800 underline">
          重试
        </button>
      </div>
    </div>

    <!-- 最后更新时间 -->
    <div v-if="studyStatStore.lastUpdateTime" class="mt-3 text-xs text-gray-400 text-center">
      最后更新: {{ formatUpdateTime(studyStatStore.lastUpdateTime) }}
    </div>
  </div>
</template>

<script setup>
import { useStudyStatStore } from '@/stores/studyStat'
import { useStudyTrackerStore } from '@/stores/studyTracker'
import { onMounted, ref } from 'vue'

const studyStatStore = useStudyStatStore()
const studyTrackerStore = useStudyTrackerStore()
const error = ref('')

const refreshStats = async () => {
  try {
    error.value = ''
    await studyStatStore.refreshStats()
  } catch (err) {
    console.error('刷新统计数据失败:', err)
    error.value = err.message || '获取学习统计失败，请稍后重试'
  }
}

const handleRetry = () => {
  refreshStats()
}

const formatUpdateTime = (time) => {
  if (!time) return ''
  
  const now = new Date()
  const updateTime = new Date(time)
  const diffMs = now - updateTime
  const diffMinutes = Math.floor(diffMs / (1000 * 60))
  
  if (diffMinutes < 1) return '刚刚'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  
  const diffHours = Math.floor(diffMinutes / 60)
  if (diffHours < 24) return `${diffHours}小时前`
  
  return updateTime.toLocaleDateString()
}

// 组件挂载时初始化统计数据
onMounted(async () => {
  try {
    await studyStatStore.init()
  } catch (err) {
    console.error('初始化学习统计失败:', err)
    error.value = '加载学习统计失败，请刷新页面重试'
  }
})
</script>

<style scoped>
.study-time-card {
  transition: all 0.3s ease;
}

.study-time-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

/* 移动端适配 */
@media (max-width: 640px) {
  .study-time-card {
    margin: 0 -1rem;
    border-radius: 0;
  }
}
</style>
