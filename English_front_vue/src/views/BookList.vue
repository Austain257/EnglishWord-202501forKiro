<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <div class="text-center mb-8">
          <h1 class="text-3xl font-bold text-gray-900 mb-4">
            选择学习课本
          </h1>
          <p class="text-lg text-gray-600">
            选择一个课本开始您的英语学习
          </p>
        </div>
        
        <div v-if="message.text" :class="['mb-4 px-4 py-3 rounded-lg text-sm font-medium text-left', message.type === 'error' ? 'bg-rose-50 text-rose-700 border border-rose-100' : 'bg-emerald-50 text-emerald-700 border border-emerald-100']">
          {{ message.text }}
        </div>

        <div v-if="loading" class="flex justify-center">
          <Loading size="lg" text="加载课本中..." />
        </div>
        
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <Card
            v-for="book in books"
            :key="book.id"
            hover
            class="cursor-pointer"
            @click="selectBook(book)"
          >
            <div class="text-center">
              <div class="w-full h-48 bg-gradient-to-br from-blue-400 to-purple-500 rounded-lg mb-4 flex items-center justify-center">
                <img
                  v-if="book.coverUrl"
                  :src="book.coverUrl"
                  :alt="book.bookName"
                  class="w-full h-full object-cover rounded-lg"
                  @error="$event.target.style.display='none'"
                />
                <div v-else class="text-white text-center">
                  <svg class="w-12 h-12 mx-auto mb-2" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                  </svg>
                  <p class="text-sm font-medium">{{ book.bookName }}</p>
                </div>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">
                {{ book.bookName }}
              </h3>
              <p class="text-sm text-gray-600 mb-4">
                {{ book.description }}
              </p>
              <div class="flex justify-between text-sm text-gray-500">
                <span>词汇量: {{ book.wordCount || 0 }}</span>
                <span>{{ book.visibility === 'PUBLIC' ? '公开' : '私有' }}</span>
              </div>
            </div>
          </Card>
        </div>
        
        <div v-if="!loading && books.length === 0" class="text-center py-12">
          <p class="text-gray-500">暂无可用课本</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useBookStore } from '@/stores/book'
import Card from '@/components/common/Card.vue'
import Loading from '@/components/common/Loading.vue'

const router = useRouter()
const bookStore = useBookStore()
const message = reactive({
  text: '',
  type: 'info'
})

const showMessage = (text, type = 'info') => {
  message.text = text
  message.type = type
  setTimeout(() => {
    message.text = ''
  }, 3200)
}

// 使用store中的状态
const loading = computed(() => bookStore.loading)
const books = computed(() => bookStore.books)

const selectBook = async (book) => {
  try {
    await bookStore.selectBook(book)
    showMessage(`已选择课本：${book.bookName}`, 'success')
    router.push('/')
  } catch (err) {
    showMessage('选择课本失败：' + (err?.message || '请稍后重试'), 'error')
  }
}

onMounted(async () => {
  try {
    await bookStore.fetchBooks()
  } catch (err) {
    showMessage('加载课本失败：' + (err?.message || '请稍后重试'), 'error')
  }
})
</script>