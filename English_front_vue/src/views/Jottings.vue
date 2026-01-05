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
            <h1 class="text-xl font-semibold text-gray-900">积累本</h1>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-600">
              共 {{ totalItems }} 条记录
            </span>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-4xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- 标签页 -->
        <div class="bg-white rounded-lg shadow">
          <div class="border-b border-gray-200">
            <nav class="-mb-px flex">
              <button
                v-for="(tab, index) in tabs"
                :key="index"
                @click="setActiveTab(index)"
                :class="[
                  'py-4 px-6 text-sm font-medium border-b-2 transition-colors',
                  activeTab === index
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                ]"
              >
                {{ tab.name }}
              </button>
            </nav>
          </div>

          <div class="p-6">
            <!-- 批量操作栏 -->
            <div v-if="hasSelectedItems" class="mb-4 p-3 bg-blue-50 rounded-lg flex justify-between items-center">
              <span class="text-sm text-blue-700">
                已选择 {{ selectedItems.length }} 项
              </span>
              <div class="space-x-2">
                <Button @click="batchMarkAsReviewed" variant="primary" size="sm">
                  标记已掌握
                </Button>
                <Button @click="batchDelete" variant="danger" size="sm">
                  批量删除
                </Button>
                <Button @click="clearSelection" variant="secondary" size="sm">
                  取消选择
                </Button>
              </div>
            </div>

            <!-- 加载状态 -->
            <div v-if="loading" class="flex justify-center py-8">
              <Loading size="lg" text="加载积累中..." />
            </div>

            <!-- 积累列表 -->
            <div v-else-if="jottings.length > 0" class="space-y-4">
              <div
                v-for="item in jottings"
                :key="item.id"
                :class="[
                  'border rounded-lg p-4 transition-colors',
                  selectedItems.includes(item.id)
                    ? 'border-blue-500 bg-blue-50'
                    : 'border-gray-200 hover:border-gray-300',
                  item.reviewed ? 'opacity-60' : ''
                ]"
              >
                <div class="flex items-start space-x-3">
                  <input
                    type="checkbox"
                    :checked="selectedItems.includes(item.id)"
                    class="mt-1 h-4 w-4 text-blue-600 rounded"
                    @change="toggleSelection(item.id)"
                  />
                  
                  <div class="flex-1">
                    <div class="flex items-center justify-between mb-2">
                      <span :class="[
                        'px-2 py-1 text-xs rounded-full',
                        item.type === 1 
                          ? 'bg-blue-100 text-blue-800' 
                          : 'bg-green-100 text-green-800'
                      ]">
                        {{ item.type === 1 ? '单词' : '句子' }}
                      </span>
                      
                      <div class="flex items-center space-x-2">
                        <span v-if="item.reviewed" class="px-2 py-1 text-xs bg-gray-100 text-gray-600 rounded-full">
                          已掌握
                        </span>
                        <Button @click="editItem(item)" variant="ghost" size="sm">
                          编辑
                        </Button>
                        <Button @click="deleteItem(item)" variant="ghost" size="sm" class="text-red-600 hover:text-red-700">
                          删除
                        </Button>
                      </div>
                    </div>
                    
                    <div class="space-y-2">
                      <div class="text-lg font-medium text-gray-900">
                        {{ getContent(item) }}
                      </div>
                      <div class="text-gray-600">
                        {{ item.chinese }}
                      </div>
                      <div class="text-sm text-gray-500">
                        {{ formatDate(item.createTime) }}
                      </div>
                    </div>
                    
                    <div v-if="!item.reviewed" class="mt-3">
                      <Button @click="markAsReviewed(item)" variant="primary" size="sm">
                        标记已掌握
                      </Button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="text-center py-12">
              <svg class="w-12 h-12 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
              </svg>
              <p class="text-gray-500 mb-4">暂无{{ tabs[activeTab].name }}积累</p>
              <p class="text-sm text-gray-400">点击右下角的"+"按钮添加积累</p>
            </div>

            <!-- 分页 -->
            <div v-if="totalPages > 1" class="mt-6 flex justify-center">
              <div class="flex space-x-2">
                <Button 
                  @click="goToPage(currentPage - 1)" 
                  :disabled="currentPage <= 1"
                  variant="secondary" 
                  size="sm"
                >
                  上一页
                </Button>
                
                <span class="flex items-center px-3 py-1 text-sm text-gray-600">
                  {{ currentPage }} / {{ totalPages }}
                </span>
                
                <Button 
                  @click="goToPage(currentPage + 1)" 
                  :disabled="currentPage >= totalPages"
                  variant="secondary" 
                  size="sm"
                >
                  下一页
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑模态框 -->
    <div v-if="editingItem" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">编辑积累</h3>
        
        <form @submit.prevent="submitEdit">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                {{ editingItem.type === 1 ? '单词' : '句子' }}
              </label>
              <input 
                v-model="editForm.content"
                type="text" 
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">中文翻译</label>
              <textarea 
                v-model="editForm.chinese"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              ></textarea>
            </div>
          </div>
          
          <div class="flex justify-end space-x-3 mt-6">
            <Button @click="closeEditModal" type="button" variant="secondary">
              取消
            </Button>
            <Button type="submit" variant="primary" :disabled="editLoading">
              {{ editLoading ? '保存中...' : '保存' }}
            </Button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { jottingService } from '@/services/jotting.service'
import { useAuthStore } from '@/stores/auth'
import Button from '@/components/common/Button.vue'
import Loading from '@/components/common/Loading.vue'

const router = useRouter()
const toast = useToast()
const authStore = useAuthStore()

// 状态
const loading = ref(false)
const editLoading = ref(false)
const jottings = ref([])
const selectedItems = ref([])
const editingItem = ref(null)
const activeTab = ref(0) // 0-句子, 1-单词（默认显示句子，因为数据中主要是句子）
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

// 标签页配置
const tabs = [
  { name: '句子', value: 0 },
  { name: '单词', value: 1 }
]

// 编辑表单
const editForm = reactive({
  content: '',
  chinese: ''
})

// 计算属性
const hasSelectedItems = computed(() => selectedItems.value.length > 0)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

// 方法
const goBack = () => {
  router.push('/')
}

const setActiveTab = (tab) => {
  activeTab.value = tab
  selectedItems.value = []
  currentPage.value = 1
  fetchJottings()
}

const fetchJottings = async () => {
  try {
    loading.value = true
    
    const params = {
      userId: authStore.user.id,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    
    const response = await jottingService.getJottingList(params)
    
    // 后端返回的数据结构是 {total, records}
    const data = response.data
    if (data && data.records) {
      // 根据当前标签页过滤数据
      const allJottings = data.records
      jottings.value = allJottings.filter(item => item.type === tabs[activeTab.value].value)
      totalItems.value = jottings.value.length
    } else {
      jottings.value = []
      totalItems.value = 0
    }
  } catch (error) {
    console.error('加载积累失败:', error)
    toast.error('加载积累失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const toggleSelection = (itemId) => {
  const index = selectedItems.value.indexOf(itemId)
  if (index === -1) {
    selectedItems.value.push(itemId)
  } else {
    selectedItems.value.splice(index, 1)
  }
}

const clearSelection = () => {
  selectedItems.value = []
}

const getContent = (item) => {
  // 根据数据结构，word字段可能为null，sentence字段包含内容
  if (item.type === 1) {
    // 单词类型，但实际数据中word可能为null，sentence包含单词内容
    return item.word || item.sentence || '未知内容'
  } else {
    // 句子类型
    return item.sentence || '未知内容'
  }
}

const markAsReviewed = async (item) => {
  try {
    await jottingService.markAsReviewed(authStore.user.id, item.id)
    item.reviewed = 1
    toast.success('标记成功')
  } catch (error) {
    toast.error('标记失败：' + error.message)
  }
}

const batchMarkAsReviewed = async () => {
  try {
    const promises = selectedItems.value.map(id => {
      const item = jottings.value.find(j => j.id === id)
      if (item && !item.reviewed) {
        return jottingService.markAsReviewed(authStore.user.id, id)
      }
    })
    
    await Promise.all(promises.filter(Boolean))
    
    // 更新本地状态
    selectedItems.value.forEach(id => {
      const item = jottings.value.find(j => j.id === id)
      if (item) {
        item.reviewed = 1
      }
    })
    
    clearSelection()
    toast.success('批量标记成功')
  } catch (error) {
    toast.error('批量标记失败：' + error.message)
  }
}

const deleteItem = async (item) => {
  if (!confirm('确定要删除这条积累吗？')) return
  
  try {
    await jottingService.deleteJotting({
      id: item.id,
      userId: authStore.user.id
    })
    
    // 从列表中移除
    const index = jottings.value.findIndex(j => j.id === item.id)
    if (index !== -1) {
      jottings.value.splice(index, 1)
      totalItems.value--
    }
    
    toast.success('删除成功')
  } catch (error) {
    toast.error('删除失败：' + error.message)
  }
}

const batchDelete = async () => {
  if (!confirm('确定要删除选中的积累吗？')) return
  
  try {
    const deleteData = selectedItems.value.map(id => ({
      id,
      userId: authStore.user.id
    }))
    
    await jottingService.batchDeleteJotting(deleteData)
    
    // 从列表中移除
    jottings.value = jottings.value.filter(item => !selectedItems.value.includes(item.id))
    totalItems.value -= selectedItems.value.length
    
    clearSelection()
    toast.success('批量删除成功')
  } catch (error) {
    toast.error('批量删除失败：' + error.message)
  }
}

const editItem = (item) => {
  editingItem.value = item
  editForm.content = getContent(item)
  editForm.chinese = item.chinese
}

const closeEditModal = () => {
  editingItem.value = null
  editForm.content = ''
  editForm.chinese = ''
}

const submitEdit = async () => {
  try {
    editLoading.value = true
    
    const updateData = {
      id: editingItem.value.id,
      userId: authStore.user.id,
      chinese: editForm.chinese
    }
    
    if (editingItem.value.type === 1) {
      updateData.word = editForm.content
    } else {
      updateData.sentence = editForm.content
    }
    
    await jottingService.updateJotting(updateData)
    
    // 更新本地状态
    const index = jottings.value.findIndex(j => j.id === editingItem.value.id)
    if (index !== -1) {
      if (editingItem.value.type === 1) {
        jottings.value[index].word = editForm.content
      } else {
        jottings.value[index].sentence = editForm.content
      }
      jottings.value[index].chinese = editForm.chinese
    }
    
    closeEditModal()
    toast.success('更新成功')
  } catch (error) {
    toast.error('更新失败：' + error.message)
  } finally {
    editLoading.value = false
  }
}

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    fetchJottings()
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  // 处理后端返回的日期格式 (createTime)
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchJottings()
})
</script>