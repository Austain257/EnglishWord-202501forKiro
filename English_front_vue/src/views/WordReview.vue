<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <nav class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <Button @click="goBack" variant="ghost" size="sm" class="mr-4">
              <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
              </svg>
              返回
            </Button>
            <h1 class="text-xl font-semibold text-gray-900">单词复习</h1>
          </div>
          <div class="flex items-center space-x-4">
            <Button @click="showRangeModal = true" variant="secondary" size="sm">
              设置范围
            </Button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-4xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- 进度条 -->
        <div class="mb-6">
          <ProgressBar 
            :current="currentIndex + 1" 
            :total="totalWords" 
          />
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="flex justify-center py-12">
          <Loading size="lg" text="加载单词中..." />
        </div>

        <!-- 单词卡片 -->
        <div v-else-if="currentWord" class="mb-6">
          <WordCard 
            :word="currentWord" 
            :show-chinese="showChinese" 
          />
        </div>

        <!-- 无数据状态 -->
        <div v-else class="text-center py-12">
          <p class="text-gray-500 mb-4">暂无单词数据</p>
          <Button @click="loadWords" variant="primary">
            重新加载
          </Button>
        </div>

        <!-- 控制按钮 -->
        <div v-if="!loading && currentWord" class="flex justify-center space-x-3 flex-wrap gap-2">
          <Button 
            @click="prevWord" 
            :disabled="!hasPrev"
            variant="secondary"
          >
            上一个
          </Button>
          
          <Button 
            @click="toggleChinese" 
            variant="outline"
            :class="showChinese ? 'bg-blue-50 text-blue-600' : ''"
          >
            {{ showChinese ? '隐藏中文' : '显示中文' }}
          </Button>
          
          <Button 
            @click="markAsGrasped" 
            variant="success"
            :disabled="markingGrasped"
          >
            {{ markingGrasped ? '标记中...' : '已掌握' }}
          </Button>
          
          <Button 
            @click="markAsError" 
            variant="danger"
            :disabled="markingError"
          >
            {{ markingError ? '标记中...' : '加入错词本' }}
          </Button>
          
          <Button 
            @click="nextWord" 
            :disabled="!hasNext"
            variant="primary"
          >
            下一个
          </Button>
        </div>
      </div>
    </div>

    <!-- 范围设置模态框 -->
    <div v-if="showRangeModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">设置复习范围</h3>
        
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">起始位置</label>
            <input 
              v-model.number="rangeForm.start"
              type="number" 
              min="1"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">结束位置</label>
            <input 
              v-model.number="rangeForm.end"
              type="number" 
              :min="rangeForm.start"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
        
        <div class="flex justify-end space-x-3 mt-6">
          <Button @click="showRangeModal = false" variant="secondary">
            取消
          </Button>
          <Button @click="applyRange" variant="primary">
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
import { useToast } from '@/composables/useToast'
import Button from '@/components/common/Button.vue'
import Loading from '@/components/common/Loading.vue'
import WordCard from '@/components/learning/WordCard.vue'
import ProgressBar from '@/components/learning/ProgressBar.vue'

const router = useRouter()
const wordStore = useWordStore()
const { success, error } = useToast()

// 状态
const showRangeModal = ref(false)
const markingGrasped = ref(false)
const markingError = ref(false)
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

// 方法
const goBack = () => {
  router.push('/')
}

const loadWords = async () => {
  try {
    await wordStore.fetchWords()
    // 复习模式默认隐藏中文
    wordStore.showChinese = false
    if (wordStore.totalWords === 0) {
      error('当前范围内没有可复习的单词')
    }
  } catch (err) {
    error('加载单词失败：' + err.message)
  }
}

const nextWord = () => {
  wordStore.nextWord()
  // 切换到下一个单词时隐藏中文
  wordStore.showChinese = false
}

const prevWord = () => {
  wordStore.prevWord()
  // 切换到上一个单词时隐藏中文
  wordStore.showChinese = false
}

const toggleChinese = () => {
  wordStore.toggleChinese()
}

const markAsGrasped = async () => {
  try {
    markingGrasped.value = true
    await wordStore.markCurrentAsGrasped()
    success('已标记为掌握')
    
    // 自动跳转到下一个单词
    if (hasNext.value) {
      nextWord()
    }
  } catch (err) {
    error('标记失败：' + err.message)
  } finally {
    markingGrasped.value = false
  }
}

const markAsError = async () => {
  try {
    markingError.value = true
    await wordStore.markCurrentAsError()
    success('已加入错词本')
    
    // 自动跳转到下一个单词
    if (hasNext.value) {
      nextWord()
    }
  } catch (err) {
    error('标记失败：' + err.message)
  } finally {
    markingError.value = false
  }
}

const applyRange = async () => {
  if (rangeForm.value.start >= rangeForm.value.end) {
    error('起始位置必须小于结束位置')
    return
  }
  
  try {
    await wordStore.fetchWords(rangeForm.value)
    // 复习模式默认隐藏中文
    wordStore.showChinese = false
    showRangeModal.value = false
    success(`已设置复习范围：${rangeForm.value.start}-${rangeForm.value.end}`)
  } catch (err) {
    error('设置范围失败：' + err.message)
  }
}

onMounted(async () => {
  // 初始化范围表单
  rangeForm.value = { ...wordStore.learningRange }
  
  // 如果没有单词数据，则加载
  if (wordStore.totalWords === 0) {
    await loadWords()
  } else {
    // 复习模式默认隐藏中文
    wordStore.showChinese = false
  }
})
</script>