<template>
  <!-- 全局锁定遮罩 -->
  <div v-if="shouldShowOverlay" class="fixed inset-0 z-50 flex items-center justify-center px-4 bg-slate-900/80 backdrop-blur-sm">
    <div class="bg-white rounded-3xl shadow-2xl p-6 sm:p-8 max-w-md w-full">
      <!-- 警告图标 -->
      <div class="flex justify-center mb-6">
        <div class="w-20 h-20 bg-amber-100 rounded-full flex items-center justify-center">
          <svg class="w-10 h-10 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
          </svg>
        </div>
      </div>
      
      <!-- 标题和描述 -->
      <div class="text-center mb-8">
        <h3 class="text-2xl font-bold text-slate-900 mb-3">复习提醒</h3>
        <p class="text-slate-600 leading-relaxed">
          {{ lockStatus.message || '您有未完成的复习任务，请先完成第一、二轮复习！' }}
        </p>
      </div>
      
      <!-- 待完成复习列表 -->
      <div v-if="pendingReviews.length > 0" class="mb-8">
        <h4 class="text-sm font-bold text-slate-400 uppercase tracking-wider mb-3">待完成复习</h4>
        <div class="space-y-3">
          <div 
            v-for="review in pendingReviews" 
            :key="review.sessionId"
            class="flex items-center justify-between p-3 bg-slate-50 rounded-xl"
          >
            <div>
              <div class="font-semibold text-slate-900">{{ review.range }}</div>
              <div class="text-xs text-slate-500">{{ formatDate(review.createTime) }}</div>
            </div>
            <div class="text-xs font-bold text-amber-600 bg-amber-100 px-2 py-1 rounded-full">
              第{{ review.pendingRound }}轮
            </div>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="space-y-3">
        <button 
          @click="goToReview"
          class="w-full py-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-2xl font-bold text-lg shadow-lg shadow-blue-500/30 hover:shadow-blue-500/40 hover:-translate-y-0.5 transition-all"
        >
          进入复习中心
        </button>
        
        <button 
          v-if="lockStatus.allowedActions?.includes('JOTTINGS')"
          @click="goToJottings"
          class="w-full py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl font-semibold transition-colors"
        >
          使用积累本
        </button>
      </div>
      
      <!-- 功能限制说明 -->
      <div class="mt-6 p-3 bg-rose-50 border border-rose-200 rounded-xl">
        <div class="text-xs font-bold text-rose-600 mb-1">功能限制说明</div>
        <div class="text-xs text-rose-600">
          完成复习前，新词学习、游戏等功能将被锁定
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWordStudyStore } from '@/stores/wordStudy'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const wordStudyStore = useWordStudyStore()
const authStore = useAuthStore()

// 响应式数据
const lockStatus = ref({})
const pendingReviews = ref([])
const checkInterval = ref(null)

const allowedPaths = ['/word/review', '/jottings']
const secondReviewPaths = ['/word/option', '/word/game']

// 计算属性
const isLocked = computed(() => lockStatus.value.isLocked)
const hasPendingRound1 = computed(() => pendingReviews.value.some((review) => review.pendingRound === 1))
const dynamicAllowedPaths = computed(() => {
  if (!isLocked.value) return allowedPaths
  if (!hasPendingRound1.value) {
    return [...allowedPaths, ...secondReviewPaths]
  }
  return allowedPaths
})
const shouldShowOverlay = computed(() => {
  if (!isLocked.value) return false
  return !dynamicAllowedPaths.value.some((path) => route.path.startsWith(path))
})

// 方法
const checkLockStatus = async () => {
  try {
    if (!authStore.user?.id) return
    
    const response = await wordStudyStore.checkLockStatus(authStore.user.id)
    lockStatus.value = response
    
    if (response.pendingReviews) {
      pendingReviews.value = response.pendingReviews
    }
  } catch (error) {
    console.error('检查锁定状态失败:', error)
  }
}

const goToReview = () => {
  router.push('/word/review')
}

const goToJottings = () => {
  router.push('/jottings')
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const handleLockUpdated = () => {
  checkLockStatus()
}

// 生命周期
onMounted(() => {
  checkLockStatus()
  // 每30秒检查一次锁定状态
  checkInterval.value = setInterval(checkLockStatus, 30000)
  window.addEventListener('study-lock-updated', handleLockUpdated)
})

onUnmounted(() => {
  if (checkInterval.value) {
    clearInterval(checkInterval.value)
  }
  window.removeEventListener('study-lock-updated', handleLockUpdated)
})

// 暴露方法给父组件
defineExpose({
  checkLockStatus
})
</script>
