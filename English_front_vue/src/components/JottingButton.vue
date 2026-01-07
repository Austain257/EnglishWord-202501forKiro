<template>
  <!-- 悬浮按钮 -->
  <div class="fixed bottom-6 right-6 z-50">
    <button
      @click="showModal = true"
      class="w-14 h-14 rounded-full shadow-lg shadow-indigo-500/30 bg-indigo-600 text-white hover:bg-indigo-700 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 flex items-center justify-center"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
      </svg>
    </button>
  </div>

  <!-- 添加记录模态框 -->
  <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center px-4">
    <!-- 背景遮罩 -->
    <div 
      class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity"
      @click="closeModal"
    ></div>

    <!-- 弹窗主体 -->
    <div class="relative w-full max-w-lg bg-white rounded-3xl shadow-2xl p-6 sm:p-8 animate-scale-in">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-2xl font-bold text-slate-900">添加随身记</h3>
        <button 
          @click="closeModal"
          class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100 transition-colors"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
      
      <form @submit.prevent="submitJotting">
        <div class="space-y-6">
          <!-- 类型选择 -->
          <div>
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">记录类型</label>
            <div class="flex gap-3">
              <label 
                class="flex-1 cursor-pointer relative"
              >
                <input 
                  v-model="form.type" 
                  type="radio" 
                  :value="1" 
                  class="peer sr-only"
                />
                <div class="flex items-center justify-center gap-2 py-3 px-4 rounded-xl border-2 transition-all duration-200 peer-checked:border-indigo-500 peer-checked:bg-indigo-50 peer-checked:text-indigo-700 text-slate-500 border-slate-100 hover:bg-slate-50">
                  <span class="font-bold">单词</span>
                </div>
              </label>
              
              <label 
                class="flex-1 cursor-pointer relative"
              >
                <input 
                  v-model="form.type" 
                  type="radio" 
                  :value="0" 
                  class="peer sr-only"
                />
                <div class="flex items-center justify-center gap-2 py-3 px-4 rounded-xl border-2 transition-all duration-200 peer-checked:border-indigo-500 peer-checked:bg-indigo-50 peer-checked:text-indigo-700 text-slate-500 border-slate-100 hover:bg-slate-50">
                  <span class="font-bold">句子</span>
                </div>
              </label>
            </div>
          </div>
          
          <!-- 内容输入 -->
          <div>
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">
              {{ form.type === 1 ? '英文单词' : '英语句子' }}
            </label>
            <input 
              v-model="form.content"
              type="text" 
              :placeholder="form.type === 1 ? '输入想要记录的单词' : '输入想要记录的句子'"
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-semibold text-slate-900"
              required
            />
          </div>
          
          <!-- 中文翻译 -->
          <div>
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">中文翻译</label>
            <textarea 
              v-model="form.chinese"
              :placeholder="form.type === 1 ? '输入中文释义' : '输入中文翻译'"
              rows="3"
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-medium text-slate-900 resize-none"
              required
            ></textarea>
          </div>
        </div>
        
        <div class="flex gap-3 mt-8">
          <button 
            @click="closeModal" 
            type="button" 
            class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors"
          >
            取消
          </button>
          <button 
            type="submit" 
            :disabled="loading"
            class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-indigo-600 hover:bg-indigo-700 shadow-lg shadow-indigo-500/30 transition-all hover:-translate-y-0.5 disabled:opacity-70 disabled:cursor-not-allowed disabled:transform-none"
          >
            <span v-if="loading" class="flex items-center justify-center gap-2">
              <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              添加中...
            </span>
            <span v-else>确认添加</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useToast } from '@/composables/useToast'
import { jottingService } from '@/services/jotting.service'
import { useAuthStore } from '@/stores/auth'

const showModal = ref(false)
const loading = ref(false)
const { success, error: showError } = useToast()
const authStore = useAuthStore()

const form = reactive({
  type: 1, // 1-单词, 0-句子
  content: '',
  chinese: ''
})

const closeModal = () => {
  showModal.value = false
  // 重置表单
  setTimeout(() => {
    form.type = 1
    form.content = ''
    form.chinese = ''
  }, 300)
}

const submitJotting = async () => {
  if (!form.content.trim() || !form.chinese.trim()) {
    return
  }

  try {
    loading.value = true
    
    const data = {
      userId: authStore.user.id,
      type: form.type,
      chinese: form.chinese
    }
    
    // 根据类型设置对应字段
    if (form.type === 1) {
      data.word = form.content
    } else {
      data.sentence = form.content
    }
    
    await jottingService.addJotting(data)
    
    success('添加成功')
    closeModal()
  } catch (err) {
    showError('添加失败：' + err.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@keyframes scale-in {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-scale-in {
  animation: scale-in 0.2s ease-out forwards;
}
</style>
