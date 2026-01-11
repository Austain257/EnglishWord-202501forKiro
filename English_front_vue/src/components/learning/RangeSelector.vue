<template>
  <div class="range-selector">
    <!-- 触发按钮 -->
    <div class="flex items-center gap-2">
      <button 
        @click="showModal = true"
        class="flex items-center gap-2 px-4 py-2 bg-white shadow-sm border border-slate-200 rounded-xl text-sm font-medium text-slate-600 hover:text-blue-600 hover:border-blue-200 transition-all"
      >
        <span>范围: {{ currentRange }}</span>
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
      </button>
      
      <!-- 【下十个】快捷按钮 -->
      <button 
        @click="nextTenWords"
        :disabled="!canGoNext"
        class="flex items-center gap-1 px-3 py-2 bg-blue-50 text-blue-600 rounded-lg text-sm font-medium hover:bg-blue-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        title="快速切换到下10个单词"
      >
        <span>下十个</span>
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
        </svg>
      </button>
    </div>
    
    <!-- 模态框 -->
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="closeModal"></div>
      <div class="relative w-full max-w-lg bg-white rounded-3xl shadow-2xl p-6 sm:p-8">
        <!-- 标题 -->
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-slate-900">设置学习范围</h3>
          <button @click="closeModal" class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
        
        <!-- 当前课本信息 -->
        <div class="bg-blue-50 rounded-2xl p-5 mb-6 text-sm text-blue-800">
          <div class="flex justify-between mb-2">
            <span class="opacity-70">课本名称</span>
            <span class="font-bold">{{ currentBook?.bookName || '未选择' }}</span>
          </div>
          <div class="flex justify-between mb-2">
            <span class="opacity-70">总词汇量</span>
            <span class="font-bold">{{ currentBook?.wordCount || 0 }} 词</span>
          </div>
          <div class="flex justify-between">
            <span class="opacity-70">当前已选</span>
            <span class="font-bold">{{ selectedCount }} 词</span>
          </div>
        </div>
        
        <!-- 范围输入 -->
        <div class="flex gap-4 mb-6">
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">起始位置</label>
            <input 
              v-model.number="tempRange.start"
              type="number" 
              :min="1"
              :max="currentBook?.wordCount || 1000"
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-blue-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
              @input="validateRange"
            />
          </div>
          <div class="flex items-end pb-4 text-slate-300">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          </div>
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">结束位置</label>
            <input 
              v-model.number="tempRange.end"
              type="number" 
              :min="tempRange.start || 1"
              :max="currentBook?.wordCount || 1000"
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-blue-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
              @input="validateRange"
            />
          </div>
        </div>
        
        <!-- 快速选择 -->
        <div class="mb-8">
          <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">快速选择</label>
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-2">
            <button 
              v-for="(range, idx) in quickRanges" 
              :key="idx"
              @click="setQuickRange(range.start, range.end)"
              :disabled="range.end > (currentBook?.wordCount || 0)"
              class="px-3 py-2 text-xs font-medium rounded-lg border transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              :class="isCurrentRange(range) ? 'border-blue-500 bg-blue-50 text-blue-600' : 'border-slate-200 text-slate-600 hover:border-blue-300 hover:bg-blue-50 hover:text-blue-600'"
            >
              {{ range.label }}
            </button>
          </div>
        </div>
        
        <!-- 错误提示 -->
        <div v-if="rangeError" class="mb-6 p-3 bg-rose-50 border border-rose-200 rounded-xl text-rose-600 text-sm">
          {{ rangeError }}
        </div>
        
        <!-- 操作按钮 -->
        <div class="flex gap-3">
          <button 
            @click="closeModal" 
            class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors"
          >
            取消
          </button>
          <button 
            @click="applyRange" 
            :disabled="!isValidRange"
            class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-blue-600 hover:bg-blue-700 shadow-lg shadow-blue-500/20 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            确认应用
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useWordStore } from '@/stores/word'
import { useBookStore } from '@/stores/book'

const emit = defineEmits(['range-changed'])

// 状态管理
const wordStore = useWordStore()
const bookStore = useBookStore()

// 响应式数据
const showModal = ref(false)
const tempRange = ref({ start: 1, end: 50 })
const rangeError = ref('')

// 计算属性
const currentRange = computed(() => {
  return `${wordStore.learningRange.start}-${wordStore.learningRange.end}`
})

const currentBook = computed(() => bookStore.currentBook)

const canGoNext = computed(() => {
  const currentEnd = wordStore.learningRange.end
  const maxWords = currentBook.value?.wordCount || 1000
  return currentEnd + 10 <= maxWords
})

const selectedCount = computed(() => {
  return Math.max(0, tempRange.value.end - tempRange.value.start + 1)
})

const isValidRange = computed(() => {
  return tempRange.value.start >= 1 && 
         tempRange.value.end >= tempRange.value.start && 
         tempRange.value.end <= (currentBook.value?.wordCount || 1000) &&
         !rangeError.value
})

// 快速选择范围
const quickRanges = computed(() => [
  { label: '1-50', start: 1, end: 50 },
  { label: '51-100', start: 51, end: 100 },
  { label: '101-200', start: 101, end: 200 },
  { label: '201-300', start: 201, end: 300 },
  { label: '301-400', start: 301, end: 400 },
  { label: '401-500', start: 401, end: 500 },
  { label: '501-600', start: 501, end: 600 },
  { label: '601-700', start: 601, end: 700 }
])

// 【下十个】快捷功能
const nextTenWords = async () => {
  if (!canGoNext.value) {
    console.error('已经是最后的单词范围了')
    return
  }
  
  try {
    const currentRange = wordStore.learningRange
    const newRange = {
      start: currentRange.end + 1,
      end: Math.min(currentRange.end + 10, currentBook.value?.wordCount || 1000)
    }
    
    // 更新学习范围
    wordStore.setLearningRange(newRange)
    
    // 重新加载单词
    await wordStore.fetchWords(newRange)
    
    emit('range-changed', newRange)
    console.log(`已切换到范围：${newRange.start}-${newRange.end}`)
  } catch (err) {
    console.error('切换范围失败：', err.message)
  }
}

// 方法
const validateRange = () => {
  rangeError.value = ''
  
  if (tempRange.value.start < 1) {
    rangeError.value = '起始位置不能小于1'
    return
  }
  
  if (tempRange.value.end < tempRange.value.start) {
    rangeError.value = '结束位置不能小于起始位置'
    return
  }
  
  if (currentBook.value?.wordCount && tempRange.value.end > currentBook.value.wordCount) {
    rangeError.value = `结束位置不能超过课本总词汇量(${currentBook.value.wordCount})`
    return
  }
  
  if (selectedCount.value > 200) {
    rangeError.value = '单次学习范围不建议超过200个单词'
    return
  }
}

const setQuickRange = (start, end) => {
  tempRange.value.start = start
  tempRange.value.end = Math.min(end, currentBook.value?.wordCount || end)
  validateRange()
}

const isCurrentRange = (range) => {
  return tempRange.value.start === range.start && tempRange.value.end === range.end
}

const applyRange = async () => {
  if (!isValidRange.value) return
  
  try {
    // 更新store中的学习范围
    wordStore.setLearningRange(tempRange.value)
    
    // 重新加载单词
    await wordStore.fetchWords(tempRange.value)
    
    showModal.value = false
    emit('range-changed', tempRange.value)
    console.log(`已设置学习范围：${tempRange.value.start}-${tempRange.value.end}`)
  } catch (err) {
    console.error('设置学习范围失败：', err.message)
  }
}

const closeModal = () => {
  showModal.value = false
  // 重置为当前范围
  tempRange.value = { ...wordStore.learningRange }
  rangeError.value = ''
}

// 监听器
watch(() => wordStore.learningRange, (newRange) => {
  tempRange.value = { ...newRange }
}, { immediate: true })

watch(tempRange, validateRange, { deep: true })
</script>
