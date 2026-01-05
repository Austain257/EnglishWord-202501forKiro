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
            <h1 class="text-xl font-semibold text-gray-900">错句本</h1>
          </div>
          <div class="flex items-center space-x-4">
            <div class="flex items-center space-x-2">
              <span class="text-sm text-gray-600">模式:</span>
              <select 
                v-model="studyMode" 
                class="text-sm border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
                @change="resetAnswerState"
              >
                <option value="review">复习模式</option>
                <option value="dictation">听写模式</option>
              </select>
            </div>
            <div v-if="studyMode === 'dictation'" class="flex items-center space-x-2">
              <span class="text-sm text-gray-600">听写:</span>
              <select 
                v-model="dictationMode" 
                class="text-sm border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
                @change="resetAnswerState"
              >
                <option value="en2zh">英译汉</option>
                <option value="zh2en">汉译英</option>
              </select>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-4xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
      <!-- 进度条 -->
      <div v-if="errorSentences.length > 0" class="mb-6">
        <ProgressBar 
          :current="currentIndex + 1" 
          :total="errorSentences.length" 
          :percentage="progress"
          label="错句复习进度"
        />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <Loading />
      </div>

      <!-- 复习模式 -->
      <div v-else-if="studyMode === 'review' && currentSentence" class="mb-6">
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
              v-if="!showChinese"
              @click="toggleChinese"
              variant="outline"
              size="sm"
            >
              显示中文
            </Button>
            
            <Button 
              @click="markAsGrasped"
              variant="success"
              size="sm"
            >
              已掌握
            </Button>
            
            <Button 
              @click="markAsError"
              variant="danger"
              size="sm"
            >
              加入错句本
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

      <!-- 听写模式 -->
      <div v-else-if="studyMode === 'dictation' && currentSentence" class="mb-6">
        <div class="bg-white rounded-lg shadow-lg p-6 max-w-2xl mx-auto">
          <!-- 题目显示区域 -->
          <div class="text-center mb-6">
            <div class="mb-4">
              <p class="text-xl font-medium text-gray-900 leading-relaxed">
                {{ dictationMode === 'en2zh' ? currentSentence.sentence : currentSentence.chinese }}
              </p>
            </div>
            
            <!-- 答案输入框 -->
            <div class="mb-4">
              <textarea
                v-model="userAnswer"
                :placeholder="dictationMode === 'en2zh' ? '请输入中文翻译...' : '请输入英文句子...'"
                class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                rows="3"
                :disabled="showResult"
                @keydown.enter.prevent="checkAnswer"
              ></textarea>
            </div>

            <!-- 结果显示区域 -->
            <div v-if="showResult" class="mb-4">
              <div v-if="isCorrect" class="p-4 bg-green-50 border border-green-200 rounded-lg">
                <div class="flex items-center justify-center mb-2">
                  <svg class="w-6 h-6 text-green-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                  </svg>
                  <span class="text-green-700 font-medium">回答正确！已标记为掌握</span>
                </div>
              </div>
              
              <div v-else class="p-4 bg-red-50 border border-red-200 rounded-lg">
                <div class="flex items-center justify-center mb-2">
                  <svg class="w-6 h-6 text-red-500 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                  </svg>
                  <span class="text-red-700 font-medium">回答错误</span>
                </div>
                
                <div class="text-left space-y-2">
                  <div>
                    <span class="text-sm font-medium text-gray-700">您的答案：</span>
                    <p class="text-red-600">{{ userAnswer }}</p>
                  </div>
                  <div>
                    <span class="text-sm font-medium text-gray-700">正确答案：</span>
                    <p class="text-green-600">{{ correctAnswer }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 句子统计信息 -->
            <div class="flex justify-center items-center space-x-6 mb-6 text-sm">
              <div class="flex items-center space-x-1">
                <svg class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
                </svg>
                <span class="text-red-600">错误次数: {{ currentSentence.errorTimes || 0 }}</span>
              </div>
              
              <div class="flex items-center space-x-1">
                <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="text-gray-600">学习次数: {{ currentSentence.times || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex justify-center space-x-3">
            <Button 
              v-if="!showResult"
              @click="showAnswer"
              variant="outline"
              size="sm"
            >
              查看答案
            </Button>
            
            <Button 
              v-if="!showResult"
              @click="checkAnswer"
              variant="primary"
              size="sm"
              :disabled="!userAnswer.trim()"
            >
              提交答案
            </Button>
            
            <Button 
              v-if="showResult && !isCorrect"
              @click="retryAnswer"
              variant="outline"
              size="sm"
            >
              重新尝试
            </Button>
            
            <Button 
              v-if="showResult"
              @click="nextSentence"
              variant="primary"
              size="sm"
            >
              {{ hasNext ? '下一句' : '完成' }}
            </Button>
          </div>
        </div>
      </div>

      <!-- 无错句提示 -->
      <div v-else-if="!loading && errorSentences.length === 0" class="text-center py-12">
        <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
        </div>
        <h3 class="text-lg font-medium text-gray-900 mb-2">太棒了！</h3>
        <p class="text-gray-600 mb-4">您目前没有错句，继续保持！</p>
        <Button @click="goBack" variant="primary">
          返回学习
        </Button>
      </div>

      <!-- 学习统计 -->
      <div v-if="errorSentences.length > 0" class="bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">错句统计</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="text-center">
            <div class="text-2xl font-bold text-red-600">{{ errorSentences.length }}</div>
            <div class="text-sm text-gray-600">错句总数</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-blue-600">{{ currentIndex + 1 }}</div>
            <div class="text-sm text-gray-600">当前位置</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-green-600">{{ masteredCount }}</div>
            <div class="text-sm text-gray-600">已掌握</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-orange-600">{{ progress }}%</div>
            <div class="text-sm text-gray-600">复习进度</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSentenceStore } from '@/stores/sentence'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import { sentenceService } from '@/services/sentence.service'
import Button from '@/components/common/Button.vue'
import Loading from '@/components/common/Loading.vue'
import SentenceCard from '@/components/learning/SentenceCard.vue'
import ProgressBar from '@/components/learning/ProgressBar.vue'

const router = useRouter()
const sentenceStore = useSentenceStore()
const authStore = useAuthStore()
const { showToast } = useToast()

// 响应式数据
const studyMode = ref('review') // review: 复习模式, dictation: 听写模式
const dictationMode = ref('en2zh') // en2zh: 英译汉, zh2en: 汉译英
const errorSentences = ref([])
const currentIndex = ref(0)
const showChinese = ref(false)
const userAnswer = ref('')
const showResult = ref(false)
const isCorrect = ref(false)
const masteredCount = ref(0)
const loading = ref(false)

// 计算属性
const currentSentence = computed(() => errorSentences.value[currentIndex.value] || null)
const hasNext = computed(() => currentIndex.value < errorSentences.value.length - 1)
const hasPrev = computed(() => currentIndex.value > 0)
const progress = computed(() => {
  if (errorSentences.value.length === 0) return 0
  return Math.round(((currentIndex.value + 1) / errorSentences.value.length) * 100)
})

const correctAnswer = computed(() => {
  if (!currentSentence.value) return ''
  return dictationMode.value === 'en2zh' ? currentSentence.value.chinese : currentSentence.value.sentence
})

// 方法
const goBack = () => {
  router.back()
}

const loadErrorSentences = async () => {
  try {
    loading.value = true
    
    if (!authStore.user?.id) {
      throw new Error('用户未登录')
    }
    
    // 使用新的错句接口
    const response = await sentenceService.getErrorSentences(authStore.user.id)
    
    if (response.success) {
      if (typeof response.data === 'string') {
        // 如果返回的是字符串消息（没有错句）
        errorSentences.value = []
        showToast(response.data, 'success')
      } else if (Array.isArray(response.data)) {
        // 如果返回的是错句数组
        errorSentences.value = response.data
        currentIndex.value = 0
        masteredCount.value = 0
        
        if (errorSentences.value.length === 0) {
          showToast('恭喜！您目前没有错句', 'success')
        }
      }
    } else {
      throw new Error(response.message || '获取错句失败')
    }
  } catch (error) {
    console.error('加载错句失败:', error)
    showToast('加载错句失败，请重试', 'error')
    errorSentences.value = []
  } finally {
    loading.value = false
  }
}

const nextSentence = () => {
  if (hasNext.value) {
    currentIndex.value++
    resetAnswerState()
    showChinese.value = studyMode.value === 'review' ? false : true
  } else {
    // 完成所有错句
    if (studyMode.value === 'dictation') {
      showToast(`听写完成！已掌握 ${masteredCount.value} 个错句`, 'success')
    } else {
      showToast(`复习完成！已掌握 ${masteredCount.value} 个错句`, 'success')
    }
    goBack()
  }
}

const prevSentence = () => {
  if (hasPrev.value) {
    currentIndex.value--
    resetAnswerState()
    showChinese.value = studyMode.value === 'review' ? false : true
  }
}

const toggleChinese = () => {
  showChinese.value = !showChinese.value
}

const markAsGrasped = async () => {
  if (!currentSentence.value) return
  
  try {
    await sentenceStore.markCurrentAsGrasped()
    
    // 更新本地状态
    currentSentence.value.isGrasp = 1
    masteredCount.value++
    
    showToast('已标记为掌握', 'success')
    
    // 自动进入下一句
    setTimeout(() => {
      nextSentence()
    }, 1000)
  } catch (error) {
    console.error('标记已掌握失败:', error)
    showToast('操作失败，请重试', 'error')
  }
}

const markAsError = async () => {
  if (!currentSentence.value) return
  
  try {
    await sentenceStore.markCurrentAsError()
    
    // 更新本地状态
    currentSentence.value.isGrasp = 2
    currentSentence.value.errorTimes = (currentSentence.value.errorTimes || 0) + 1
    
    showToast('已加入错句本', 'info')
    
    // 自动进入下一句
    setTimeout(() => {
      nextSentence()
    }, 1000)
  } catch (error) {
    console.error('标记错句失败:', error)
    showToast('操作失败，请重试', 'error')
  }
}

const checkAnswer = async () => {
  if (!userAnswer.value.trim()) {
    showToast('请输入答案', 'warning')
    return
  }

  const correct = correctAnswer.value.toLowerCase().trim()
  const user = userAnswer.value.toLowerCase().trim()
  
  // 简单的答案比较
  isCorrect.value = correct === user || correct.includes(user) || user.includes(correct)
  
  if (isCorrect.value) {
    // 自动标记为已掌握
    try {
      await sentenceStore.markCurrentAsGrasped()
      currentSentence.value.isGrasp = 1
      masteredCount.value++
    } catch (error) {
      console.error('标记已掌握失败:', error)
    }
    
    // 3秒后自动进入下一句
    setTimeout(() => {
      if (showResult.value && isCorrect.value) {
        nextSentence()
      }
    }, 3000)
  } else {
    // 增加错误次数
    try {
      await sentenceStore.markCurrentAsError()
      currentSentence.value.errorTimes = (currentSentence.value.errorTimes || 0) + 1
    } catch (error) {
      console.error('标记错句失败:', error)
    }
  }
  
  showResult.value = true
}

const showAnswer = () => {
  showResult.value = true
  isCorrect.value = false
}

const retryAnswer = () => {
  userAnswer.value = ''
  showResult.value = false
  isCorrect.value = false
}

const resetAnswerState = () => {
  userAnswer.value = ''
  showResult.value = false
  isCorrect.value = false
}

// 生命周期
onMounted(async () => {
  await loadErrorSentences()
  showChinese.value = studyMode.value === 'review' ? false : true
})

onUnmounted(() => {
  // 清理状态
})
</script>