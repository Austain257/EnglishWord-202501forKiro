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
            <h1 class="text-xl font-semibold text-gray-900">单词听写</h1>
          </div>
          <div class="flex items-center space-x-4">
            <Button @click="showRangeModal = true" variant="secondary" size="sm">
              设置范围
            </Button>
            <Button @click="toggleMode" variant="outline" size="sm">
              {{ dictationMode === 'en2cn' ? '英译汉' : '汉译英' }}
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

        <!-- 听写界面 -->
        <div v-else-if="currentWord" class="space-y-6">
          <!-- 题目显示 -->
          <div class="bg-white rounded-lg shadow-lg p-8 text-center min-h-[300px] flex flex-col justify-center">
            <div class="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              {{ getQuestionText() }}
            </div>
            
            <!-- 音标（仅英译汉模式显示） -->
            <div v-if="dictationMode === 'en2cn' && (currentWord.pronounce || currentWord.phonetic)" class="text-lg text-gray-600 mb-6">
              {{ currentWord.pronounce || currentWord.phonetic }}
            </div>
            
            <!-- 答案输入框 -->
            <div class="max-w-md mx-auto">
              <input
                ref="answerInput"
                v-model="userAnswer"
                type="text"
                :placeholder="getPlaceholderText()"
                class="w-full px-4 py-3 text-lg border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                :class="{
                  'border-red-500 bg-red-50': showResult && !isCorrect,
                  'border-green-500 bg-green-50': showResult && isCorrect
                }"
                @keyup.enter="submitAnswer"
                :disabled="showResult"
              />
              
              <!-- 答案校对结果 -->
              <div v-if="showResult" class="mt-4 text-left">
                <div class="text-sm text-gray-600 mb-2">正确答案：</div>
                <div class="text-lg font-medium text-green-600">
                  {{ getCorrectAnswer() }}
                </div>
                
                <!-- 错误高亮 -->
                <div v-if="!isCorrect && userAnswer" class="mt-2">
                  <div class="text-sm text-gray-600 mb-1">您的答案：</div>
                  <div class="text-lg" v-html="getHighlightedAnswer()"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 控制按钮 -->
          <div class="flex justify-center space-x-3 flex-wrap gap-2">
            <Button 
              @click="prevWord" 
              :disabled="!hasPrev"
              variant="secondary"
            >
              上一个
            </Button>
            
            <Button 
              v-if="!showResult"
              @click="skipWord" 
              variant="outline"
            >
              跳过
            </Button>
            
            <Button 
              v-if="!showResult"
              @click="showAnswer" 
              variant="outline"
            >
              查看答案
            </Button>
            
            <Button 
              v-if="!showResult"
              @click="submitAnswer" 
              variant="primary"
              :disabled="!userAnswer.trim()"
            >
              提交答案
            </Button>
            
            <Button 
              v-if="showResult"
              @click="nextWord" 
              :disabled="!hasNext"
              variant="primary"
            >
              下一个
            </Button>
          </div>
        </div>

        <!-- 无数据状态 -->
        <div v-else class="text-center py-12">
          <p class="text-gray-500 mb-4">暂无单词数据</p>
          <Button @click="loadWords" variant="primary">
            重新加载
          </Button>
        </div>
      </div>
    </div>

    <!-- 范围设置模态框 -->
    <div v-if="showRangeModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">设置听写范围</h3>
        
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

    <!-- 彩带动画 -->
    <div v-if="showConfetti" class="fixed inset-0 pointer-events-none z-50">
      <div class="confetti-container">
        <div v-for="i in 50" :key="i" class="confetti" :style="getConfettiStyle(i)"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useWordStore } from '@/stores/word'
import { useToast } from '@/composables/useToast'
import Button from '@/components/common/Button.vue'
import Loading from '@/components/common/Loading.vue'
import ProgressBar from '@/components/learning/ProgressBar.vue'

const router = useRouter()
const wordStore = useWordStore()
const { success, error } = useToast()

// 状态
const showRangeModal = ref(false)
const dictationMode = ref('en2cn') // 'en2cn' 英译汉, 'cn2en' 汉译英
const userAnswer = ref('')
const showResult = ref(false)
const isCorrect = ref(false)
const showConfetti = ref(false)
const answerInput = ref(null)
const rangeForm = ref({
  start: 1,
  end: 50
})

// 计算属性
const loading = computed(() => wordStore.loading)
const currentWord = computed(() => wordStore.currentWord)
const currentIndex = computed(() => wordStore.currentIndex)
const totalWords = computed(() => wordStore.totalWords)
const hasNext = computed(() => wordStore.hasNext)
const hasPrev = computed(() => wordStore.hasPrev)

// 方法
const goBack = () => {
  router.push('/')
}

const loadWords = async () => {
  try {
    await wordStore.fetchWords()
    if (wordStore.totalWords === 0) {
      error('当前范围内没有可听写的单词')
    }
  } catch (err) {
    error('加载单词失败：' + err.message)
  }
}

const toggleMode = () => {
  dictationMode.value = dictationMode.value === 'en2cn' ? 'cn2en' : 'en2cn'
  resetAnswer()
}

const getQuestionText = () => {
  if (!currentWord.value) return ''
  return dictationMode.value === 'en2cn' ? currentWord.value.word : currentWord.value.chinese
}

const getPlaceholderText = () => {
  return dictationMode.value === 'en2cn' ? '请输入中文释义' : '请输入英文单词'
}

const getCorrectAnswer = () => {
  if (!currentWord.value) return ''
  return dictationMode.value === 'en2cn' ? currentWord.value.chinese : currentWord.value.word
}

const resetAnswer = () => {
  userAnswer.value = ''
  showResult.value = false
  isCorrect.value = false
  showConfetti.value = false
  nextTick(() => {
    answerInput.value?.focus()
  })
}

const submitAnswer = () => {
  if (!userAnswer.value.trim()) return
  
  const correctAnswer = getCorrectAnswer()
  isCorrect.value = checkAnswer(userAnswer.value.trim(), correctAnswer)
  showResult.value = true
  
  if (isCorrect.value) {
    showConfetti.value = true
    success('回答正确！')
    // 自动标记为已掌握
    markAsGrasped()
    // 3秒后自动跳转到下一个
    setTimeout(() => {
      if (hasNext.value) {
        nextWord()
      }
    }, 2000)
  } else {
    error('回答错误，请查看正确答案')
    // 标记为错词
    markAsError()
  }
}

const checkAnswer = (userAnswer, correctAnswer) => {
  // 简单的字符串比对，忽略大小写和前后空格
  const normalizeAnswer = (str) => str.toLowerCase().trim().replace(/\s+/g, ' ')
  return normalizeAnswer(userAnswer) === normalizeAnswer(correctAnswer)
}

const getHighlightedAnswer = () => {
  if (!userAnswer.value || isCorrect.value) return userAnswer.value
  
  const correct = getCorrectAnswer()
  const user = userAnswer.value
  
  // 简单的字符差异高亮
  let result = ''
  const maxLen = Math.max(correct.length, user.length)
  
  for (let i = 0; i < maxLen; i++) {
    const correctChar = correct[i] || ''
    const userChar = user[i] || ''
    
    if (correctChar === userChar) {
      result += `<span class="text-green-600">${userChar}</span>`
    } else {
      result += `<span class="text-red-600 bg-red-100 px-1 rounded">${userChar || '?'}</span>`
    }
  }
  
  return result
}

const showAnswer = () => {
  showResult.value = true
  isCorrect.value = false
  // 标记为错词
  markAsError()
}

const skipWord = () => {
  nextWord()
}

const nextWord = () => {
  wordStore.nextWord()
  resetAnswer()
}

const prevWord = () => {
  wordStore.prevWord()
  resetAnswer()
}

const markAsGrasped = async () => {
  try {
    await wordStore.markCurrentAsGrasped()
  } catch (err) {
    console.error('标记已掌握失败:', err)
  }
}

const markAsError = async () => {
  try {
    await wordStore.markCurrentAsError()
  } catch (err) {
    console.error('标记错词失败:', err)
  }
}

const applyRange = async () => {
  if (rangeForm.value.start >= rangeForm.value.end) {
    error('起始位置必须小于结束位置')
    return
  }
  
  try {
    await wordStore.fetchWords(rangeForm.value)
    showRangeModal.value = false
    resetAnswer()
    success(`已设置听写范围：${rangeForm.value.start}-${rangeForm.value.end}`)
  } catch (err) {
    error('设置范围失败：' + err.message)
  }
}

const getConfettiStyle = (index) => {
  const colors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff']
  return {
    left: Math.random() * 100 + '%',
    animationDelay: Math.random() * 3 + 's',
    backgroundColor: colors[index % colors.length],
    animationDuration: (Math.random() * 3 + 2) + 's'
  }
}

onMounted(async () => {
  // 初始化范围表单
  rangeForm.value = { ...wordStore.learningRange }
  
  // 如果没有单词数据，则加载
  if (wordStore.totalWords === 0) {
    await loadWords()
  }
  
  // 聚焦输入框
  nextTick(() => {
    answerInput.value?.focus()
  })
})
</script>

<style scoped>
.confetti-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.confetti {
  position: absolute;
  width: 10px;
  height: 10px;
  background: #ff6b6b;
  animation: confetti-fall linear infinite;
}

@keyframes confetti-fall {
  0% {
    transform: translateY(-100vh) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: translateY(100vh) rotate(720deg);
    opacity: 0;
  }
}
</style>