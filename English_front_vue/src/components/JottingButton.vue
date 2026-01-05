<template>
  <!-- 悬浮按钮 -->
  <div class="fixed bottom-6 right-6 z-50">
    <Button
      @click="showModal = true"
      class="w-14 h-14 rounded-full shadow-lg hover:shadow-xl transition-shadow"
      variant="primary"
    >
      <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
      </svg>
    </Button>
  </div>

  <!-- 添加记录模态框 -->
  <div v-if="showModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
      <h3 class="text-lg font-semibold text-gray-900 mb-4">添加随身记</h3>
      
      <form @submit.prevent="submitJotting">
        <div class="space-y-4">
          <!-- 类型选择 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">类型</label>
            <div class="flex space-x-4">
              <label class="flex items-center">
                <input 
                  v-model="form.type" 
                  type="radio" 
                  :value="1" 
                  class="mr-2"
                />
                单词
              </label>
              <label class="flex items-center">
                <input 
                  v-model="form.type" 
                  type="radio" 
                  :value="0" 
                  class="mr-2"
                />
                句子
              </label>
            </div>
          </div>
          
          <!-- 内容输入 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              {{ form.type === 1 ? '单词' : '句子' }}
            </label>
            <input 
              v-model="form.content"
              type="text" 
              :placeholder="form.type === 1 ? '输入英语单词' : '输入英语句子'"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
          
          <!-- 中文翻译 -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">中文翻译</label>
            <textarea 
              v-model="form.chinese"
              :placeholder="form.type === 1 ? '输入中文释义' : '输入中文翻译'"
              rows="3"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            ></textarea>
          </div>
        </div>
        
        <div class="flex justify-end space-x-3 mt-6">
          <Button @click="closeModal" type="button" variant="secondary">
            取消
          </Button>
          <Button type="submit" variant="primary" :disabled="loading">
            {{ loading ? '添加中...' : '添加' }}
          </Button>
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
import Button from '@/components/common/Button.vue'

const showModal = ref(false)
const loading = ref(false)
const toast = useToast()
const authStore = useAuthStore()

const form = reactive({
  type: 1, // 1-单词, 0-句子
  content: '',
  chinese: ''
})

const closeModal = () => {
  showModal.value = false
  // 重置表单
  form.type = 1
  form.content = ''
  form.chinese = ''
}

const submitJotting = async () => {
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
    
    toast.success('添加成功')
    closeModal()
  } catch (error) {
    toast.error('添加失败：' + error.message)
  } finally {
    loading.value = false
  }
}
</script>