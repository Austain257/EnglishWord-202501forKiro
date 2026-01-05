<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <nav class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-14 sm:h-16">
          <div class="flex items-center">
            <Button @click="goBack" variant="ghost" size="sm" class="mr-2 sm:mr-4">
              <svg class="w-4 h-4 sm:w-5 sm:h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
              </svg>
              <span class="hidden sm:inline">返回</span>
            </Button>
            <h1 class="text-lg sm:text-xl font-semibold text-gray-900">单词学习</h1>
          </div>
          <div class="flex items-center space-x-2 sm:space-x-4">
            <Button @click="showRangeModal = true" variant="secondary" size="sm">
              <span class="hidden sm:inline">设置范围</span>
              <span class="sm:hidden">范围</span>
            </Button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-4xl mx-auto py-4 sm:py-6 px-4 sm:px-6 lg:px-8">
      <!-- 进度条 -->
      <div class="mb-4 sm:mb-6">
        <ProgressBar 
          :current="currentIndex + 1" 
          :total="totalWords" 
        />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center py-8 sm:py-12">
        <Loading size="lg" text="加载单词中..." />
      </div>

      <!-- 单词卡片 -->
      <div v-else-if="currentWord" class="mb-4 sm:mb-6">
        <WordCard 
          :word="currentWord" 
          :show-chinese="showChinese" 
        />
      </div>

      <!-- 无数据状态 -->
      <div v-else class="text-center py-8 sm:py-12">
        <p class="text-gray-500 mb-4">暂无单词数据</p>
        <Button @click="loadWords" variant="primary">
          重新加载
        </Button>
      </div>

      <!-- 控制按钮 -->
      <div v-if="!loading && currentWord" class="flex justify-center space-x-3 sm:space-x-4">
        <Button 
          @click="prevWord" 
          :disabled="!hasPrev"
          variant="secondary"
          class="flex-1 sm:flex-none"
        >
          上一个
        </Button>
        
        <Button 
          @click="nextWord" 
          :disabled="!hasNext"
          variant="primary"
          class="flex-1 sm:flex-none"
        >
          下一个
        </Button>
      </div>
    </div>

    <!-- 范围设置模态框 -->
    <div v-if="showRangeModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-lg p-4 sm:p-6 w-full max-w-md">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">设置学习范围</h3>
        
        <!-- 当前范围信息 -->
        <div class="mb-4 p-3 bg-blue-50 rounded-lg">
          <div class="text-sm text-blue-800 space-y-1">
            <div class="flex justify-between items-center">
              <span>当前学习范围:</span>
              <span class="font-medium">{{ wordStore.learningRange.start }} - {{ wordStore.learningRange.end }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span>课本总词汇量:</span>
              <span class="font-medium">{{ currentBook?.wordCount || '未知' }} 个</span>
            </div>
            <div class="flex justify-between items-center">
              <span>当前范围词汇:</span>
              <span class="font-medium">{{ Math.min(wordStore.learningRange.end - wordStore.learningRange.start + 1, currentBook?.wordCount || 0) }} 个</span>
            </div>
          </div>
        </div>
        
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">起始位置</label>
            <input 
              v-model.number="rangeForm.start"
              type="number" 
              min="1"
              :max="currentBook?.wordCount || 1000"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-base"
              placeholder="请输入起始位置"
            />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">结束位置</label>
            <input 
              v-model.number="rangeForm.end"
              type="number" 
              :min="rangeForm.start"
              :max="currentBook?.wordCount || 1000"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-base"
              placeholder="请输入结束位置"
            />
          </div>
          
          <!-- 快速选择按钮 -->
          <div class="space-y-2">
            <label class="block text-sm font-medium text-gray-700">快速选择:</label>
            <div class="grid grid-cols-2 gap-2">
              <Button @click="setQuickRange(1, 50)" variant="outline" size="sm" class="text-xs sm:text-sm">
                1-50
              </Button>
              <Button @click="setQuickRange(51, 100)" variant="outline" size="sm" class="text-xs sm:text-sm">
                51-100
              </Button>
              <Button @click="setQuickRange(101, 200)" variant="outline" size="sm" class="text-xs sm:text-sm">
                101-200
              </Button>
              <Button @click="setQuickRange(201, 300)" variant="outline" size="sm" class="text-xs sm:text-sm">
                201-300
              </Button>
            </div>
          </div>
        </div>
        
        <div class="flex flex-col sm:flex-row justify-end space-y-2 sm:space-y-0 sm:space-x-3 mt-6">
          <Button @click="cancelRangeSelection" variant="secondary" class="w-full sm:w-auto">
            取消
          </Button>
          <Button @click="applyRange" variant="primary" class="w-full sm:w-auto">
            确定
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useWordStore } from '@/stores/word'
import { useBookStore } from '@/stores/book'
import { useToast } from '@/composables/useToast'
import Button from '@/components/common/Button.vue'
import Loading from '@/components/common/Loading.vue'
import WordCard from '@/components/learning/WordCard.vue'
import ProgressBar from '@/components/learning/ProgressBar.vue'

const router = useRouter()
const wordStore = useWordStore()
const bookStore = useBookStore()
const { success, error } = useToast()

// 状态
const showRangeModal = ref(false)
const rangeForm = ref({
  start: 1,
  end: 50
})

// 计算属性
const loading = computed(() => wordStore.loading)
const currentWord = computed(() => wordStore.currentWord)
const currentIndex = computed(() => wordStore.currentIndex)
const totalWords = computed(() => wordStore.totalWords)
const showChinese = computed(() => wordStore.showChinese)
const hasNext = computed(() => wordStore.hasNext)
const hasPrev = computed(() => wordStore.hasPrev)
const currentBook = computed(() => bookStore.currentBook)

// 方法
const goBack = () => {
  router.push('/')
}

const loadWords = async () => {
  try {
    await wordStore.fetchWords()
    if (wordStore.totalWords === 0) {
      error('当前范围内没有可学习的单词')
    }
  } catch (err) {
    error('加载单词失败：' + err.message)
  }
}

const nextWord = () => {
  wordStore.nextWord()
}

const prevWord = () => {
  wordStore.prevWord()
}

const applyRange = async () => {
  if (rangeForm.value.start >= rangeForm.value.end) {
    error('起始位置必须小于结束位置')
    return
  }
  
  if (rangeForm.value.start < 1) {
    error('起始位置不能小于1')
    return
  }
  
  if (currentBook.value?.wordCount && rangeForm.value.end > currentBook.value.wordCount) {
    error(`结束位置不能超过课本总词汇量(${currentBook.value.wordCount})`)
    return
  }
  
  try {
    await wordStore.fetchWords(rangeForm.value)
    showRangeModal.value = false
    success(`已设置学习范围：${rangeForm.value.start}-${rangeForm.value.end}`)
  } catch (err) {
    error('设置范围失败：' + err.message)
  }
}

const setQuickRange = (start, end) => {
  rangeForm.value.start = start
  rangeForm.value.end = Math.min(end, currentBook.value?.wordCount || end)
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  rangeForm.value = { ...wordStore.learningRange }
}

onMounted(async () => {
  // 初始化范围表单
  rangeForm.value = { ...wordStore.learningRange }
  
  // 如果没有单词数据，则加载
  if (wordStore.totalWords === 0) {
    await loadWords()
  }
})
</script>