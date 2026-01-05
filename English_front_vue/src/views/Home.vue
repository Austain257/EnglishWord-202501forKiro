<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <nav class="bg-white shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <h1 class="text-xl font-semibold text-gray-900">英语学习平台</h1>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-600">欢迎，{{ user?.nickname || user?.username }}</span>
            <Button @click="logout" variant="secondary" size="sm">
              退出登录
            </Button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- 激励文案区域 -->
        <div class="mb-8">
          <MotivationQuote ref="motivationRef" />
        </div>

        <!-- 欢迎信息 -->
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">
            开始您的英语学习之旅
          </h2>
          <p class="text-lg text-gray-600 mb-6">
            专业、现代、高效的英语学习平台，让学习更有趣
          </p>
        </div>

        <!-- 快速导航 -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToBooks">
            <div class="text-center p-4">
              <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z"></path>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">选择课本</h3>
              <p class="text-sm text-gray-600">选择适合您的英语课本开始学习</p>
            </div>
          </Card>

          <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToChecklist">
            <div class="text-center p-4">
              <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">学习清单</h3>
              <p class="text-sm text-gray-600">查看今日学习任务和进度</p>
            </div>
          </Card>

          <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToJottings">
            <div class="text-center p-4">
              <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">积累本</h3>
              <p class="text-sm text-gray-600">查看您的学习积累和笔记</p>
            </div>
          </Card>
        </div>

        <!-- 当前课本信息和单词学习模块 -->
        <div v-if="currentBook" class="mb-8">
          <!-- 当前课本信息 -->
          <div class="bg-white rounded-lg shadow p-6 mb-6">
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mr-4">
                  <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z"></path>
                  </svg>
                </div>
                <div>
                  <h3 class="text-lg font-semibold text-gray-900">{{ currentBook.bookName }}</h3>
                  <p class="text-sm text-gray-600">{{ currentBook.description }}</p>
                  <p class="text-xs text-gray-500 mt-1">词汇量：{{ currentBook.wordCount || 0 }} 个</p>
                </div>
              </div>
              <Button @click="goToBooks" variant="outline" size="sm">
                切换课本
              </Button>
            </div>
          </div>

          <!-- 学习模块 -->
          <div class="space-y-8">
            <!-- 单词学习模块 -->
            <div>
              <h3 class="text-xl font-semibold text-gray-900 mb-4">单词学习</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToWordLearning">
                  <div class="text-center p-4">
                    <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
                      <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z"></path>
                      </svg>
                    </div>
                    <h4 class="text-sm font-semibold text-gray-900 mb-1">初学模式</h4>
                    <p class="text-xs text-gray-600">学习新单词</p>
                  </div>
                </Card>

                <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToWordReview">
                  <div class="text-center p-4">
                    <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
                      <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                      </svg>
                    </div>
                    <h4 class="text-sm font-semibold text-gray-900 mb-1">复习模式</h4>
                    <p class="text-xs text-gray-600">复习已学单词</p>
                  </div>
                </Card>

                <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToWordDictation">
                  <div class="text-center p-4">
                    <div class="w-10 h-10 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-3">
                      <svg class="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path>
                      </svg>
                    </div>
                    <h4 class="text-sm font-semibold text-gray-900 mb-1">听写模式</h4>
                    <p class="text-xs text-gray-600">单词听写练习</p>
                  </div>
                </Card>

                <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToErrorWordBook">
                  <div class="text-center p-4">
                    <div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-3">
                      <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
                      </svg>
                    </div>
                    <h4 class="text-sm font-semibold text-gray-900 mb-1">错词本</h4>
                    <p class="text-xs text-gray-600">复习错词</p>
                  </div>
                </Card>
              </div>
            </div>

            <!-- 句子学习模块 -->
            <div>
              <h3 class="text-xl font-semibold text-gray-900 mb-4">句子学习</h3>
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToSentenceLearning">
                  <div class="text-center p-4">
                    <div class="w-10 h-10 bg-indigo-100 rounded-full flex items-center justify-center mx-auto mb-3">
                      <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                      </svg>
                    </div>
                    <h4 class="text-sm font-semibold text-gray-900 mb-1">句子学习</h4>
                    <p class="text-xs text-gray-600">学习英语句子</p>
                  </div>
                </Card>

                <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToSentenceDictation">
                  <div class="text-center p-4">
                    <div class="w-10 h-10 bg-teal-100 rounded-full flex items-center justify-center mx-auto mb-3">
                      <svg class="w-5 h-5 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2.2 2.2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                      </svg>
                    </div>
                    <h4 class="text-sm font-semibold text-gray-900 mb-1">句子听写</h4>
                    <p class="text-xs text-gray-600">句子听写练习</p>
                  </div>
                </Card>

                <Card class="hover:shadow-lg transition-shadow cursor-pointer" @click="goToErrorSentenceBook">
                  <div class="text-center p-4">
                    <div class="w-10 h-10 bg-pink-100 rounded-full flex items-center justify-center mx-auto mb-3">
                      <svg class="w-5 h-5 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
                      </svg>
                    </div>
                    <h4 class="text-sm font-semibold text-gray-900 mb-1">错句本</h4>
                    <p class="text-xs text-gray-600">复习错句</p>
                  </div>
                </Card>
              </div>
            </div>
          </div>
        </div>

        <!-- 未选择课本提示 -->
        <div v-else class="bg-white rounded-lg shadow p-8 text-center mb-8">
          <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z"></path>
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900 mb-2">开始学习之旅</h3>
          <p class="text-gray-600 mb-4">请先选择一本课本，然后就可以开始单词学习了</p>
          <Button @click="goToBooks" variant="primary">
            选择课本
          </Button>
        </div>

        <!-- 学习统计概览 -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">学习概览</h3>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div class="text-center">
              <div class="text-2xl font-bold text-blue-600">{{ stats.totalWords || 0 }}</div>
              <div class="text-sm text-gray-600">总词汇量</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-green-600">{{ stats.masteredWords || 0 }}</div>
              <div class="text-sm text-gray-600">已掌握</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-orange-600">{{ stats.errorWords || 0 }}</div>
              <div class="text-sm text-gray-600">错词数</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-purple-600">{{ stats.studyDays || 0 }}</div>
              <div class="text-sm text-gray-600">学习天数</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import { useRouter } from 'vue-router'
import Button from '@/components/common/Button.vue'
import Card from '@/components/common/Card.vue'
import MotivationQuote from '@/components/MotivationQuote.vue'

const { logout } = useAuth()
const authStore = useAuthStore()
const bookStore = useBookStore()
const router = useRouter()
const motivationRef = ref(null)

// 用户信息
const user = computed(() => authStore.user)
const currentBook = computed(() => bookStore.currentBook)

// 学习统计数据
const stats = ref({
  totalWords: 0,
  masteredWords: 0,
  errorWords: 0,
  studyDays: 0
})

// 加载学习统计数据
const loadLearningStats = async () => {
  if (!authStore.user?.id) {
    return
  }
  
  try {
    const bookId = currentBook.value?.id
    const url = bookId 
      ? `http://localhost:8080/api/english/stats/${authStore.user.id}?bookId=${bookId}`
      : `http://localhost:8080/api/english/stats/${authStore.user.id}`
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 1 && data.data) {
        const statsData = data.data
        stats.value = {
          totalWords: (statsData.totalWords || 0) + (statsData.totalSentences || 0),
          masteredWords: (statsData.masteredWords || 0) + (statsData.masteredSentences || 0),
          errorWords: (statsData.errorWords || 0) + (statsData.errorSentences || 0),
          studyDays: statsData.studyDays || 0
        }
      }
    }
    
  } catch (error) {
    console.error('加载学习统计失败:', error)
    // 使用默认值
    stats.value = {
      totalWords: 0,
      masteredWords: 0,
      errorWords: 0,
      studyDays: 0
    }
  }
}

// 导航方法
const goToBooks = () => {
  router.push('/books')
}

const goToChecklist = () => {
  router.push('/checklist')
}

const goToJottings = () => {
  router.push('/jottings')
}

// 单词学习导航方法
const goToWordLearning = () => {
  router.push('/word/learning')
}

const goToWordReview = () => {
  router.push('/word/review')
}

const goToWordDictation = () => {
  router.push('/word/dictation')
}

const goToErrorWordBook = () => {
  router.push('/word/error-book')
}

// 句子学习导航方法
const goToSentenceLearning = () => {
  router.push('/sentence/learning')
}

const goToSentenceDictation = () => {
  router.push('/sentence/dictation')
}

const goToErrorSentenceBook = () => {
  router.push('/sentence/error-book')
}

// 刷新激励文案
const refreshQuote = () => {
  if (motivationRef.value) {
    motivationRef.value.refresh()
  }
}

onMounted(async () => {
  // 初始化课本信息
  try {
    await bookStore.fetchBooks()
    // 如果有保存的课本ID，初始化课本状态
    bookStore.initializeBook()
  } catch (error) {
    console.error('初始化课本信息失败:', error)
  }
  
  // 加载用户的学习统计数据
  await loadLearningStats()
})

// 监听当前课本变化，重新加载统计数据
watch(currentBook, async (newBook) => {
  if (newBook) {
    await loadLearningStats()
  }
}, { immediate: false })
</script>