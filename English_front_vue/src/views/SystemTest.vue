<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-4xl mx-auto px-4">
      <h1 class="text-3xl font-bold text-center mb-8">系统接口测试</h1>
      
      <!-- 认证测试 -->
      <div class="bg-white rounded-lg shadow p-6 mb-6">
        <h2 class="text-xl font-semibold mb-4">认证测试</h2>
        <div class="space-y-4">
          <div>
            <button @click="testLogin" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
              测试登录
            </button>
            <div v-if="loginResult" class="mt-2 p-3 bg-gray-100 rounded">
              <pre>{{ JSON.stringify(loginResult, null, 2) }}</pre>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 课本测试 -->
      <div class="bg-white rounded-lg shadow p-6 mb-6">
        <h2 class="text-xl font-semibold mb-4">课本测试</h2>
        <div class="space-y-4">
          <div>
            <button @click="testBooks" class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
              获取课本列表
            </button>
            <div v-if="booksResult" class="mt-2 p-3 bg-gray-100 rounded">
              <pre>{{ JSON.stringify(booksResult, null, 2) }}</pre>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 单词测试 -->
      <div class="bg-white rounded-lg shadow p-6 mb-6">
        <h2 class="text-xl font-semibold mb-4">单词测试</h2>
        <div class="space-y-4">
          <div>
            <button @click="testWords" class="bg-purple-500 text-white px-4 py-2 rounded hover:bg-purple-600">
              获取单词列表
            </button>
            <div v-if="wordsResult" class="mt-2 p-3 bg-gray-100 rounded">
              <pre>{{ JSON.stringify(wordsResult, null, 2) }}</pre>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 句子测试 -->
      <div class="bg-white rounded-lg shadow p-6 mb-6">
        <h2 class="text-xl font-semibold mb-4">句子测试</h2>
        <div class="space-y-4">
          <div>
            <button @click="testSentences" class="bg-orange-500 text-white px-4 py-2 rounded hover:bg-orange-600">
              获取句子列表
            </button>
            <div v-if="sentencesResult" class="mt-2 p-3 bg-gray-100 rounded">
              <pre>{{ JSON.stringify(sentencesResult, null, 2) }}</pre>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 用户信息显示 -->
      <div class="bg-white rounded-lg shadow p-6">
        <h2 class="text-xl font-semibold mb-4">当前用户信息</h2>
        <div v-if="authStore.user" class="p-3 bg-gray-100 rounded">
          <pre>{{ JSON.stringify(authStore.user, null, 2) }}</pre>
        </div>
        <div v-else class="text-gray-500">
          未登录
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { bookService } from '@/services/book.service'
import { wordService } from '@/services/word.service'
import { sentenceService } from '@/services/sentence.service'
import { authService } from '@/services/auth.service'

const authStore = useAuthStore()

const loginResult = ref(null)
const booksResult = ref(null)
const wordsResult = ref(null)
const sentencesResult = ref(null)

const testLogin = async () => {
  try {
    const result = await authService.login({
      username: 'testuser',
      password: '123456'
    })
    loginResult.value = result
    console.log('登录结果:', result)
  } catch (error) {
    loginResult.value = { error: error.message }
    console.error('登录失败:', error)
  }
}

const testBooks = async () => {
  try {
    if (!authStore.user?.id) {
      booksResult.value = { error: '请先登录' }
      return
    }
    
    const result = await bookService.getBookList(authStore.user.id)
    booksResult.value = result
    console.log('课本列表结果:', result)
  } catch (error) {
    booksResult.value = { error: error.message }
    console.error('获取课本列表失败:', error)
  }
}

const testWords = async () => {
  try {
    if (!authStore.user?.id) {
      wordsResult.value = { error: '请先登录' }
      return
    }
    
    const result = await wordService.getWordList({
      userId: authStore.user.id,
      bookId: 1,
      start: 1,
      end: 5
    })
    wordsResult.value = result
    console.log('单词列表结果:', result)
  } catch (error) {
    wordsResult.value = { error: error.message }
    console.error('获取单词列表失败:', error)
  }
}

const testSentences = async () => {
  try {
    if (!authStore.user?.id) {
      sentencesResult.value = { error: '请先登录' }
      return
    }
    
    const result = await sentenceService.getSentenceList({
      userId: authStore.user.id,
      start: 1,
      end: 5
    })
    sentencesResult.value = result
    console.log('句子列表结果:', result)
  } catch (error) {
    sentencesResult.value = { error: error.message }
    console.error('获取句子列表失败:', error)
  }
}
</script>
</template>