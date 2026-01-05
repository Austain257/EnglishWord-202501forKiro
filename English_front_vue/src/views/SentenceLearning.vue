<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <nav class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <Button @click="goBack" variant="ghost" size="sm" class="mr-4">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
              </svg>
              返回
            </Button>
            <h1 class="text-xl font-semibold text-gray-900">句子学习</h1>
          </div>
          <div class="flex items-center space-x-4">
            <Button @click="showRangeModal = true" variant="outline" size="sm">
              选择范围
            </Button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-4xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
      <!-- 进度条 -->
      <div class="mb-6">
        <ProgressBar 
          :current="currentIndex + 1" 
          :total="totalSentences" 
          :percentage="progress"
          label="句子学习进度"
        />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <Loading />
      </div>

      <!-- 句子卡片 -->
      <div v-else-if="currentSentence" class="mb-6">
        <SentenceCard 
          :sentence="currentSentence" 
          :show-chinese="showChinese"
        >
          <template #actions>
            <Button 
              @click="prevSentence" 
              :disabled="!hasPrev"
              variant="outline"
              size="sm"
            >
              上一句
            </Button>
            <Button 
              @click="nextSentence"
              :disabled="!hasNext"
              variant="primary"
              size="sm"
            >
              下一句
            </Button>
          </template>
        </SentenceCard>
      </div>

      <!-- 无句子提示 -->
      <div v-else class="text-center py-12">
        <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
          </svg>
        </div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">暂无句子</h3>
        <p class="text-gray-600 mb-4">当前范围内没有找到句子，请尝试调整学习范围</p>
        <Button @click="showRangeModal = true" variant="primary">
          选择范围
        </Button>
      </div>

      <!-- 学习统计 -->
      <div v-if="totalSentences > 0" class="bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">学习统计</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="text-center">
            <div class="text-2xl font-bold text-blue-600">{{ totalSentences }}</div>
            <div class="text-sm text-gray-600">总句子数</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-green-600">{{ currentIndex + 1 }}</div>
            <div class="text-sm text-gray-600">当前位置</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-orange-600">{{ progress }}%</div>
            <div class="text-sm text-gray-600">完成进度</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-purple-600">{{ learningRange.start }}-{{ learningRange.end }}</div>
            <div class="text-sm text-gray-600">学习范围</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 范围选择模态框 -->
    <div v-if="showRangeModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">选择学习范围</h3>
        
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">起始句子</label>
            <input 
              v-model.number="tempRange.start"
              type="number" 
              min="1"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入起始句子编号"
            >
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">结束句子</label>
            <input 
              v-model.number="tempRange.end"
              type="number" 
              :min="tempRange.start"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="请输入结束句子编号"
            >
          </div>
        </div>
        
        <div class="flex justify-end space-x-3 mt-6">
          <Button @click="cancelRangeSelection" variant="outline">
            取消
          </Button>
          <Button @click="confirmRangeSelection" variant="primary">
            确定
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSentenceStore } from '@/stores/sentence'
import { useToast } from '@/composables/useToast'
import Button from '@/components/common/Button.vue'
import Loading from '@/components/common/Loading.vue'
import SentenceCard from '@/components/learning/SentenceCard.vue'
import ProgressBar from '@/components/learning/ProgressBar.vue'

const router = useRouter()
const sentenceStore = useSentenceStore()
const { showToast } = useToast()

// 响应式数据
const showRangeModal = ref(false)
const tempRange = ref({ start: 1, end: 30 })

// 计算属性
const loading = computed(() => sentenceStore.loading)
const currentSentence = computed(() => sentenceStore.currentSentence)
const currentIndex = computed(() => sentenceStore.currentIndex)
const totalSentences = computed(() => sentenceStore.totalSentences)
const progress = computed(() => sentenceStore.progress)
const hasNext = computed(() => sentenceStore.hasNext)
const hasPrev = computed(() => sentenceStore.hasPrev)
const showChinese = computed(() => sentenceStore.showChinese)
const learningRange = computed(() => sentenceStore.learningRange)

// 方法
const goBack = () => {
  router.back()
}

const nextSentence = () => {
  sentenceStore.nextSentence()
}

const prevSentence = () => {
  sentenceStore.prevSentence()
}

const loadSentences = async (range = null) => {
  try {
    await sentenceStore.fetchSentences(range)
    if (sentenceStore.sentences.length === 0) {
      showToast('当前范围内没有找到句子，请尝试调整学习范围', 'warning')
    }
  } catch (error) {
    console.error('加载句子失败:', error)
    showToast('加载句子失败，请重试', 'error')
  }
}

const confirmRangeSelection = async () => {
  if (tempRange.value.start >= tempRange.value.end) {
    showToast('结束句子编号必须大于起始句子编号', 'error')
    return
  }
  
  if (tempRange.value.start < 1) {
    showToast('起始句子编号不能小于1', 'error')
    return
  }
  
  showRangeModal.value = false
  await loadSentences(tempRange.value)
  showToast(`已切换到句子 ${tempRange.value.start}-${tempRange.value.end}`, 'success')
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  tempRange.value = { ...learningRange.value }
}

// 键盘事件处理
const handleKeyPress = (event) => {
  if (showRangeModal.value) return
  
  switch (event.key) {
    case 'ArrowLeft':
      if (hasPrev.value) {
        prevSentence()
      }
      break
    case 'ArrowRight':
      if (hasNext.value) {
        nextSentence()
      }
      break
    case 'Escape':
      goBack()
      break
  }
}

// 生命周期
onMounted(async () => {
  // 初始化临时范围
  tempRange.value = { ...learningRange.value }
  
  // 加载句子
  await loadSentences()
  
  // 添加键盘事件监听
  window.addEventListener('keydown', handleKeyPress)
})

onUnmounted(() => {
  // 移除键盘事件监听
  window.removeEventListener('keydown', handleKeyPress)
  
  // 重置状态
  sentenceStore.resetState()
})
</script>