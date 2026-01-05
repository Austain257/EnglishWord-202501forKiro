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
            <h1 class="text-xl font-semibold text-gray-900">句子听写</h1>
          </div>
          <div class="flex items-center space-x-4">
            <div class="flex items-center space-x-2">
              <span class="text-sm text-gray-600">模式:</span>
              <select 
                v-model="dictationMode" 
                class="text-sm border border-gray-300 rounded px-2 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="en2zh">英译汉</option>
                <option value="zh2en">汉译英</option>
              </select>
            </div>
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
          label="听写进度"
        />
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <Loading />
      </div>

      <!-- 听写卡片 -->
      <div v-else-if="currentSentence" class="mb-6">
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
                  <span class="text-green-700 font-medium">回答正确！</span>
                </div>
                <!-- 彩带动画效果 -->
                <div class="confetti-container">
                  <div class="confetti"></div>
                  <div class="confetti"></div>
                  <div class="confetti"></div>
                  <div class="confetti"></div>
                  <div class="confetti"></div>
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
                  <div v-if="errorHighlight" class="text-xs text-gray-600">
                    <span class="font-medium">错误提示：</span>{{ errorHighlight }}
                  </div>
                </div>
              </div>
            </div>

            <!-- 句子统计信息 -->
            <div class="flex justify-center items-center space-x-6 mb-6 text-sm">
              <div class="flex items-center space-x-1">
                <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                </svg>
                <span class="text-gray-600">学习次数: {{ currentSentence.times || 0 }}</span>
              </div>
              
              <div v-if="currentSentence.isGrasp === 2" class="flex items-center space-x-1">
                <svg class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
                </svg>
                <span class="text-red-600">错误次数: {{ currentSentence.errorTimes || 0 }}</span>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex justify-center space-x-3">
            <Button 
              v-if="!showResult"
              @click="skipSentence"
              variant="outline"
              size="sm"
            >
              跳过
            </Button>
            
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

      <!-- 听写统计 -->
      <div v-if="totalSentences > 0" class="bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">听写统计</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div class="text-center">
            <div class="text-2xl font-bold text-blue-600">{{ totalSentences }}</div>
            <div class="text-sm text-gray-600">总句子数</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-green-600">{{ correctCount }}</div>
            <div class="text-sm text-gray-600">正确数</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-red-600">{{ errorCount }}</div>
            <div class="text-sm text-gray-600">错误数</div>
          </div>
          <div class="text-center">
            <div class="text-2xl font-bold text-purple-600">{{ Math.round((correctCount / Math.max(correctCount + errorCount, 1)) * 100) }}%</div>
            <div class="text-sm text-gray-600">正确率</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 范围选择模态框 -->
    <div v-if="showRangeModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">选择听写范围</h3>
        
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
import ProgressBar from '@/components/learning/ProgressBar.vue'

const router = useRouter()
const sentenceStore = useSentenceStore()
const { showToast } = useToast()

// 响应式数据
const dictationMode = ref('en2zh') // en2zh: 英译汉, zh2en: 汉译英
const userAnswer = ref('')
const showResult = ref(false)
const isCorrect = ref(false)
const errorHighlight = ref('')
const showRangeModal = ref(false)
const tempRange = ref({ start: 1, end: 30 })
const correctCount = ref(0)
const errorCount = ref(0)

// 计算属性
const loading = computed(() => sentenceStore.loading)
const currentSentence = computed(() => sentenceStore.currentSentence)
const currentIndex = computed(() => sentenceStore.currentIndex)
const totalSentences = computed(() => sentenceStore.totalSentences)
const progress = computed(() => sentenceStore.progress)
const hasNext = computed(() => sentenceStore.hasNext)
const learningRange = computed(() => sentenceStore.learningRange)

const correctAnswer = computed(() => {
  if (!currentSentence.value) return ''
  return dictationMode.value === 'en2zh' ? currentSentence.value.chinese : currentSentence.value.sentence
})

// 方法
const goBack = () => {
  router.back()
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

const checkAnswer = async () => {
  if (!userAnswer.value.trim()) {
    showToast('请输入答案', 'warning')
    return
  }

  const correct = correctAnswer.value.toLowerCase().trim()
  const user = userAnswer.value.toLowerCase().trim()
  
  // 简单的答案比较（可以根据需要优化）
  isCorrect.value = correct === user || correct.includes(user) || user.includes(correct)
  
  if (!isCorrect.value) {
    // 生成错误提示
    errorHighlight.value = generateErrorHint(user, correct)
    
    // 标记为错句
    try {
      await sentenceStore.markCurrentAsError()
      errorCount.value++
    } catch (error) {
      console.error('标记错句失败:', error)
    }
  } else {
    // 标记为已掌握
    try {
      await sentenceStore.markCurrentAsGrasped()
      correctCount.value++
    } catch (error) {
      console.error('标记已掌握失败:', error)
    }
  }
  
  showResult.value = true
  
  // 如果正确，3秒后自动进入下一句
  if (isCorrect.value) {
    setTimeout(() => {
      if (showResult.value && isCorrect.value) {
        nextSentence()
      }
    }, 3000)
  }
}

const generateErrorHint = (userAnswer, correctAnswer) => {
  // 简单的错误提示生成
  if (userAnswer.length < correctAnswer.length * 0.5) {
    return '答案太短，请检查是否遗漏内容'
  } else if (userAnswer.length > correctAnswer.length * 1.5) {
    return '答案太长，请检查是否有多余内容'
  } else {
    return '请仔细检查拼写和语法'
  }
}

const showAnswer = () => {
  showResult.value = true
  isCorrect.value = false
  errorHighlight.value = '已查看答案'
}

const skipSentence = () => {
  nextSentence()
}

const retryAnswer = () => {
  userAnswer.value = ''
  showResult.value = false
  isCorrect.value = false
  errorHighlight.value = ''
}

const nextSentence = () => {
  if (hasNext.value) {
    sentenceStore.nextSentence()
    resetAnswerState()
  } else {
    // 完成所有句子
    showToast(`听写完成！正确率：${Math.round((correctCount.value / Math.max(correctCount.value + errorCount.value, 1)) * 100)}%`, 'success')
    goBack()
  }
}

const resetAnswerState = () => {
  userAnswer.value = ''
  showResult.value = false
  isCorrect.value = false
  errorHighlight.value = ''
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
  resetAnswerState()
  correctCount.value = 0
  errorCount.value = 0
  await loadSentences(tempRange.value)
  showToast(`已切换到句子 ${tempRange.value.start}-${tempRange.value.end}`, 'success')
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  tempRange.value = { ...learningRange.value }
}

// 生命周期
onMounted(async () => {
  // 初始化临时范围
  tempRange.value = { ...learningRange.value }
  
  // 加载句子
  await loadSentences()
})

onUnmounted(() => {
  // 重置状态
  sentenceStore.resetState()
})
</script>

<style scoped>
/* 彩带动画效果 */
.confetti-container {
  position: relative;
  height: 20px;
  overflow: hidden;
}

.confetti {
  position: absolute;
  width: 4px;
  height: 4px;
  background: linear-gradient(45deg, #ff6b6b, #4ecdc4, #45b7d1, #96ceb4, #feca57);
  animation: confetti-fall 2s ease-out forwards;
}

.confetti:nth-child(1) {
  left: 10%;
  animation-delay: 0s;
  background-color: #ff6b6b;
}

.confetti:nth-child(2) {
  left: 30%;
  animation-delay: 0.2s;
  background-color: #4ecdc4;
}

.confetti:nth-child(3) {
  left: 50%;
  animation-delay: 0.4s;
  background-color: #45b7d1;
}

.confetti:nth-child(4) {
  left: 70%;
  animation-delay: 0.6s;
  background-color: #96ceb4;
}

.confetti:nth-child(5) {
  left: 90%;
  animation-delay: 0.8s;
  background-color: #feca57;
}

@keyframes confetti-fall {
  0% {
    transform: translateY(-20px) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: translateY(40px) rotate(360deg);
    opacity: 0;
  }
}
</style>