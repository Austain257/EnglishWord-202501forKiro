import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { bookService } from '@/services/book.service'
import { useAuthStore } from '@/stores/auth'

export const useBookStore = defineStore('book', () => {
  // 状态
  const books = ref([])
  const currentBook = ref(null)
  const loading = ref(false)
  
  // 计算属性
  const hasBooks = computed(() => books.value.length > 0)
  const currentBookId = computed(() => currentBook.value?.id || null)
  
  // 动作
  const fetchBooks = async () => {
    try {
      loading.value = true
      const authStore = useAuthStore()
      
      if (!authStore.user?.id) {
        throw new Error('用户未登录')
      }
      
      const response = await bookService.getBookList(authStore.user.id)
      books.value = response.data || []
      
      // 如果有保存的当前课本ID，尝试恢复
      const savedBookId = authStore.currentBookId
      if (savedBookId) {
        const savedBook = books.value.find(book => book.id === parseInt(savedBookId))
        if (savedBook) {
          currentBook.value = savedBook
        }
      }
      
      return response
    } catch (error) {
      console.error('获取课本列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  const selectBook = async (book) => {
    try {
      // 验证课本是否存在
      await bookService.checkBookExists(book.id)
      
      // 设置当前课本
      currentBook.value = book
      
      // 保存到认证store
      const authStore = useAuthStore()
      authStore.setCurrentBook(book.id)
      
      return { success: true }
    } catch (error) {
      console.error('选择课本失败:', error)
      throw error
    }
  }
  
  const clearCurrentBook = () => {
    currentBook.value = null
    const authStore = useAuthStore()
    authStore.setCurrentBook(null)
  }
  
  const checkBookExists = async (bookId) => {
    try {
      const response = await bookService.checkBookExists(bookId)
      return response.data
    } catch (error) {
      console.error('检查课本存在性失败:', error)
      return false
    }
  }
  
  // 初始化课本状态
  const initializeBook = () => {
    const authStore = useAuthStore()
    const savedBookId = authStore.currentBookId
    
    if (savedBookId && books.value.length > 0) {
      const savedBook = books.value.find(book => book.id === parseInt(savedBookId))
      if (savedBook) {
        currentBook.value = savedBook
      }
    }
  }
  
  return {
    // 状态
    books,
    currentBook,
    loading,
    
    // 计算属性
    hasBooks,
    currentBookId,
    
    // 动作
    fetchBooks,
    selectBook,
    clearCurrentBook,
    checkBookExists,
    initializeBook
  }
})