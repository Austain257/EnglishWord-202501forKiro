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
            <h1 class="text-xl font-semibold text-gray-900">错词本</h1>
          </div>
          <div class="flex items-center space-x-4">
            <Button @click="toggleMode" variant="outline" size="sm">
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path v-if="currentMode === 'review'" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path>
                <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              {{ currentMode === 'review' ? '复习模式' : '听写模式' }}
            </Button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-4xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- 统计信息 -->
        <div class="bg-white rounded-lg shadow p-6 mb-6">
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div class="text-center">
              <div class="text-2xl font-bold text-red-600">{{ totalErrorWords }}</div>
              <div class="text-sm text-gray-600">错词总数</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-blue-600">{{ currentIndex + 1 }}</div>
              <div class="text-sm text-gray-600">当前位置</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-orange-600">{{ getErrorTimes() }}</div>
              <div class="text-sm text-gray-600">错误次数</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-green-600">{{ masteredToday }}</div>
              <div class="text-sm text-gray-600">今日掌握</div>
            </div>
          </div>
        </div>

        <!-- 进度条 -->
        <div class="mb-6">
          <ProgressBar 
            :current="currentIndex + 1" 
            :total="totalErrorWords" 
          />
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="flex justify-center py-12">
          <Loading size="lg" text="加载错词中..." />
        </div>

        <!-- 复习模式 -->
        <div v-else-if="currentMode === 'review' && currentWord" class="space-y-6">
          <WordCard 
            :word="currentWord" 
            :show-chinese="showChinese" 
          />
          
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
              @click="markAsNotGrasped" 
              variant="danger"
              :disabled="markingNotGrasped"
            >
              {{ markingNotGrasped ? '标记中...' : '未掌握' }}
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

        <!-- 听写模式 -->
        <div v-else-if="currentMode === 'dictation' && currentWord" class="space-y-6">
          <div class="bg-white rounded-lg shadow p-6 max-w-2xl mx-auto">
            <!-- 顶部信息栏 -->
            <div class="flex justify-between items-center mb-6 pb-4 border-b border-gray-100">
              <div class="text-sm text-gray-500">
                第 {{ currentIndex + 1 }} / {{ totalErrorWords }} 个
              </div>
              <div class="flex items-center space-x-4 text-sm text-gray-500">
                <span>错误 {{ currentWord.errorTimes || 0 }} 次</span>
                <span>频次 {{ currentWord.times || 0 }}</span>
              </div>
            </div>

            <!-- 中文释义 -->
            <div class="text-center mb-6">
              <div class="text-xl text-gray-800 font-medium mb-4">
                {{ currentWord.chinese }}
              </div>
              
              <!-- 发音按钮 -->
              <button 
                @click="playPronunciation" 
                class="inline-flex items-center px-4 py-2 bg-blue-50 hover:bg-blue-100 text-blue-600 rounded-lg transition-colors"
              >
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 14.142M6.343 6.343L4.93 4.93A1 1 0 003.515 6.343l1.414 1.414L6.343 6.343zM12 2v20"></path>
                </svg>
                播放发音
              </button>
            </div>

            <!-- 输入区域 -->
            <div class="mb-6">
              <input
                v-model="dictationInput"
                @keyup.enter="checkDictation"
                type="text"
                placeholder="请输入英文单词..."
                class="w-full px-4 py-3 text-lg border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-center"
                :class="showDictationResult ? (dictationCorrect ? 'border-green-500 bg-green-50' : 'border-red-500 bg-red-50') : ''"
                :disabled="showDictationResult"
                autofocus
              />
              <div v-if="!showDictationResult" class="text-center mt-2 text-sm text-gray-400">
                输入完成后按 Enter 键检查答案
              </div>
            </div>

            <!-- 结果显示 -->
            <div v-if="showDictationResult" class="mb-6">
              <div v-if="dictationCorrect" class="bg-green-50 border border-green-200 rounded-lg p-4 text-center">
                <div class="text-green-600 font-medium mb-1">✓ 回答正确！</div>
                <div class="text-sm text-green-600">继续保持</div>
              </div>
              
              <div v-else class="bg-red-50 border border-red-200 rounded-lg p-4">
                <div class="text-center text-red-600 font-medium mb-3">✗ 答案错误</div>
                <div class="grid grid-cols-2 gap-3 text-sm">
                  <div>
                    <div class="text-gray-600 mb-1">您的答案</div>
                    <div class="font-medium text-red-600">{{ dictationInput }}</div>
                  </div>
                  <div>
                    <div class="text-gray-600 mb-1">正确答案</div>
                    <div class="font-medium text-green-600">{{ currentWord.word }}</div>
                  </div>
                </div>
                <div class="text-center mt-3 text-sm text-gray-600">
                  音标：{{ currentWord.pronounce }}
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="flex justify-center space-x-3">
              <Button 
                v-if="!showDictationResult"
                @click="checkDictation" 
                variant="primary"
                :disabled="!dictationInput.trim()"
              >
                检查答案
              </Button>
              
              <template v-if="showDictationResult">
                <Button 
                  v-if="dictationCorrect"
                  @click="markAsGrasped" 
                  variant="success"
                  :disabled="markingGrasped"
                >
                  {{ markingGrasped ? '标记中...' : '已掌握' }}
                </Button>
                
                <Button 
                  v-if="!dictationCorrect"
                  @click="markAsNotGrasped" 
                  variant="danger"
                  :disabled="markingNotGrasped"
                >
                  {{ markingNotGrasped ? '标记中...' : '需要复习' }}
                </Button>
                
                <Button 
                  @click="nextDictationWord" 
                  :disabled="!hasNext"
                  variant="primary"
                >
                  {{ hasNext ? '下一个' : '完成' }}
                </Button>
              </template>
            </div>

            <!-- 底部导航 -->
            <div class="flex justify-between items-center mt-6 pt-4 border-t border-gray-100">
              <Button 
                @click="prevWord" 
                :disabled="!hasPrev"
                variant="ghost"
                size="sm"
              >
                ← 上一个
              </Button>
              
              <!-- 简单进度条 -->
              <div class="flex-1 mx-4">
                <div class="w-full h-1 bg-gray-200 rounded-full">
                  <div 
                    class="h-1 bg-blue-500 rounded-full transition-all duration-300"
                    :style="{ width: `${((currentIndex + 1) / totalErrorWords) * 100}%` }"
                  ></div>
                </div>
              </div>
              
              <Button 
                @click="nextWord" 
                :disabled="!hasNext"
                variant="ghost"
                size="sm"
              >
                下一个 →
              </Button>
            </div>
          </div>
        </div>

        <!-- 无数据状态 -->
        <div v-else class="text-center py-12">
          <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">太棒了！</h3>
          <p class="text-gray-500 mb-4">您的错词本是空的，继续保持！</p>
          <Button @click="goBack" variant="primary">
            返回首页
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useWordStore } from '@/stores/word'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import { useToast } from '@/composables/useToast'
import { wordService } from '@/services/word.service'
import Button from '@/components/common/Button.vue'
import Loading from '@/components/common/Loading.vue'
import WordCard from '@/components/learning/WordCard.vue'
import ProgressBar from '@/components/learning/ProgressBar.vue'

const router = useRouter()
const wordStore = useWordStore()
const authStore = useAuthStore()
const bookStore = useBookStore()
const { success, error } = useToast()

// 状态
const loading = ref(false)
const currentMode = ref('review') // 'review' 或 'dictation'
const errorWords = ref([])
const currentIndex = ref(0)
const showChinese = ref(false)
const masteredToday = ref(0)

// 标记状态
const markingGrasped = ref(false)
const markingNotGrasped = ref(false)

// 听写模式状态
const dictationInput = ref('')
const showDictationResult = ref(false)
const dictationCorrect = ref(false)

// 计算属性
const currentWord = computed(() => errorWords.value[currentIndex.value] || null)
const totalErrorWords = computed(() => errorWords.value.length)
const hasNext = computed(() => currentIndex.value < errorWords.value.length - 1)
const hasPrev = computed(() => currentIndex.value > 0)

// 方法
const goBack = () => {
  router.push('/')
}

const getErrorTimes = () => {
  return currentWord.value?.errorTimes || 0
}

const loadErrorWords = async () => {
  try {
    loading.value = true
    
    if (!authStore.user?.id) {
      throw new Error('用户未登录')
    }
    
    // 如果没有当前课本，尝试初始化
    if (!bookStore.currentBookId) {
      console.log('没有当前课本，尝试初始化...')
      try {
        await bookStore.fetchBooks()
        bookStore.initializeBook()
      } catch (initError) {
        console.error('初始化课本失败:', initError)
      }
    }
    
    if (!bookStore.currentBookId) {
      throw new Error('请先选择课本')
    }
    
    console.log('开始加载错词，用户ID:', authStore.user.id, '课本ID:', bookStore.currentBookId)
    
    // 获取错词列表（isGrasp=2的单词）
    const params = {
      userId: authStore.user.id,
      bookId: bookStore.currentBookId
    }
    
    const response = await wordService.getErrorWordList(params)
    console.log('错词接口响应:', response)
    errorWords.value = response.data || []
    currentIndex.value = 0
    
  } catch (err) {
    console.error('加载错词失败:', err)
    error('加载错词失败：' + err.message)
  } finally {
    loading.value = false
  }
}

const toggleMode = () => {
  currentMode.value = currentMode.value === 'review' ? 'dictation' : 'review'
  // 重置听写状态
  resetDictationState()
}

const resetDictationState = () => {
  dictationInput.value = ''
  showDictationResult.value = false
  dictationCorrect.value = false
}

const playPronunciation = () => {
  if (currentWord.value?.word) {
    // 使用浏览器的语音合成API
    const utterance = new SpeechSynthesisUtterance(currentWord.value.word)
    utterance.lang = 'en-US'
    utterance.rate = 0.8
    speechSynthesis.speak(utterance)
  }
}

const checkDictation = () => {
  if (!dictationInput.value.trim() || !currentWord.value) return
  
  const userInput = dictationInput.value.trim().toLowerCase()
  const correctAnswer = currentWord.value.word.toLowerCase()
  
  dictationCorrect.value = userInput === correctAnswer
  showDictationResult.value = true
  
  if (!dictationCorrect.value) {
    // 自动标记为未掌握
    setTimeout(() => {
      markAsNotGrasped()
    }, 1000)
  }
}

const nextDictationWord = () => {
  if (hasNext.value) {
    currentIndex.value++
    resetDictationState()
  }
}

const nextWord = () => {
  if (hasNext.value) {
    currentIndex.value++
    showChinese.value = false
  }
}

const prevWord = () => {
  if (hasPrev.value) {
    currentIndex.value--
    showChinese.value = false
  }
}

const toggleChinese = () => {
  showChinese.value = !showChinese.value
}

const markAsGrasped = async () => {
  if (!currentWord.value) return
  
  try {
    markingGrasped.value = true
    await wordService.markAsGrasped(currentWord.value.id)
    
    // 从错词列表中移除
    errorWords.value.splice(currentIndex.value, 1)
    masteredToday.value++
    
    // 调整当前索引
    if (currentIndex.value >= errorWords.value.length && errorWords.value.length > 0) {
      currentIndex.value = errorWords.value.length - 1
    }
    
    success('已标记为掌握')
    
  } catch (err) {
    error('标记失败：' + err.message)
  } finally {
    markingGrasped.value = false
  }
}

const markAsNotGrasped = async () => {
  if (!currentWord.value) return
  
  try {
    markingNotGrasped.value = true
    await wordService.markAsError(currentWord.value.id)
    
    // 更新错误次数
    if (currentWord.value) {
      currentWord.value.errorTimes = (currentWord.value.errorTimes || 0) + 1
    }
    
    success('已标记为未掌握')
    
    // 自动跳转到下一个单词
    if (hasNext.value) {
      nextWord()
    }
    
  } catch (err) {
    error('标记失败：' + err.message)
  } finally {
    markingNotGrasped.value = false
  }
}

onMounted(async () => {
  await loadErrorWords()
})
</script>