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
            <h1 class="text-xl font-semibold text-gray-900">学习清单</h1>
          </div>
          <div class="flex items-center space-x-4">
            <Button @click="showAddModal = true" variant="primary" size="sm">
              新增清单
            </Button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-4xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- 激励文案 -->
        <div class="mb-6">
          <MotivationQuote />
        </div>

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
                <Button @click="markSelectedAsCompleted" variant="primary" size="sm">
                  标记完成
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
              <Loading size="lg" text="加载清单中..." />
            </div>

            <!-- 清单列表 -->
            <div v-else-if="filteredChecklists.length > 0" class="space-y-3">
              <div
                v-for="item in filteredChecklists"
                :key="item.id"
                :class="[
                  'flex items-center p-4 border rounded-lg cursor-pointer transition-colors',
                  selectedItems.includes(item.id)
                    ? 'border-blue-500 bg-blue-50'
                    : 'border-gray-200 hover:border-gray-300',
                  item.alreadyReviewed ? 'opacity-60' : ''
                ]"
                @click="toggleSelection(item.id)"
              >
                <input
                  type="checkbox"
                  :checked="selectedItems.includes(item.id)"
                  class="mr-4 h-4 w-4 text-blue-600 rounded"
                  @click.stop
                  @change="toggleSelection(item.id)"
                />
                
                <div class="flex-1">
                  <div class="flex items-center justify-between">
                    <h3 :class="[
                      'font-medium',
                      item.alreadyReviewed ? 'line-through text-gray-500' : 'text-gray-900'
                    ]">
                      {{ item.learningRecord }}
                    </h3>
                    <div class="flex items-center space-x-2">
                      <span :class="[
                        'px-2 py-1 text-xs rounded-full',
                        item.alreadyReviewed 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-yellow-100 text-yellow-800'
                      ]">
                        {{ item.alreadyReviewed ? '已完成' : '待完成' }}
                      </span>
                      <Button @click.stop="editItem(item)" variant="ghost" size="sm">
                        编辑
                      </Button>
                    </div>
                  </div>
                  
                  <div class="mt-2 text-sm text-gray-500">
                    <span v-if="item.dueDate">截止时间: {{ formatDate(item.dueDate) }}</span>
                    <span class="ml-4">{{ getDaysFromNow(item.createTime, item.toDate) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-else class="text-center py-12">
              <svg class="w-12 h-12 text-gray-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path>
              </svg>
              <p class="text-gray-500 mb-4">暂无{{ tabs[activeTab].name }}清单</p>
              <Button @click="showAddModal = true" variant="primary">
                添加第一个清单
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑清单模态框 -->
    <div v-if="showAddModal || editingItem" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">
          {{ editingItem ? '编辑清单' : '新增清单' }}
        </h3>
        
        <form @submit.prevent="submitForm">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">学习内容</label>
              <textarea 
                v-model="form.learningRecord"
                placeholder="输入学习内容描述"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              ></textarea>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">类型</label>
              <select 
                v-model="form.type"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option :value="0">句子</option>
                <option :value="1">单词</option>
                <option :value="2">听力</option>
              </select>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">截止时间（可选）</label>
              <input 
                v-model="form.dueDate"
                type="date"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
          
          <div class="flex justify-end space-x-3 mt-6">
            <Button @click="closeModal" type="button" variant="secondary">
              取消
            </Button>
            <Button type="submit" variant="primary" :disabled="formLoading">
              {{ formLoading ? '保存中...' : '保存' }}
            </Button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChecklistStore } from '@/stores/checklist'
import { useToast } from '@/composables/useToast'
import Button from '@/components/common/Button.vue'
import Loading from '@/components/common/Loading.vue'
import MotivationQuote from '@/components/MotivationQuote.vue'

const router = useRouter()
const checklistStore = useChecklistStore()
const toast = useToast()

// 状态
const showAddModal = ref(false)
const editingItem = ref(null)
const formLoading = ref(false)

// 标签页配置
const tabs = [
  { name: '句子', value: 0 },
  { name: '单词', value: 1 },
  { name: '听力', value: 2 }
]

// 表单数据
const form = reactive({
  learningRecord: '',
  type: 1,
  dueDate: ''
})

// 计算属性
const loading = computed(() => checklistStore.loading)
const activeTab = computed(() => checklistStore.activeTab)
const filteredChecklists = computed(() => checklistStore.filteredChecklists)
const selectedItems = computed(() => checklistStore.selectedItems)
const hasSelectedItems = computed(() => checklistStore.hasSelectedItems)

// 方法
const goBack = () => {
  router.push('/')
}

const setActiveTab = async (tab) => {
  try {
    await checklistStore.setActiveTab(tab)
  } catch (error) {
    toast.error('切换标签失败：' + error.message)
  }
}

const toggleSelection = (itemId) => {
  checklistStore.toggleSelection(itemId)
}

const clearSelection = () => {
  checklistStore.clearSelection()
}

const markSelectedAsCompleted = async () => {
  try {
    await checklistStore.markSelectedAsCompleted()
    toast.success('标记完成成功')
  } catch (error) {
    toast.error('标记完成失败：' + error.message)
  }
}

const batchDelete = async () => {
  if (!confirm('确定要删除选中的清单吗？')) return
  
  try {
    await checklistStore.batchDeleteChecklists(selectedItems.value.map(id => id.toString()))
    toast.success('删除成功')
  } catch (error) {
    toast.error('删除失败：' + error.message)
  }
}

const editItem = (item) => {
  editingItem.value = item
  form.learningRecord = item.learningRecord
  form.type = item.type
  form.dueDate = item.dueDate || ''
}

const closeModal = () => {
  showAddModal.value = false
  editingItem.value = null
  // 重置表单
  form.learningRecord = ''
  form.type = 1
  form.dueDate = ''
}

const submitForm = async () => {
  try {
    formLoading.value = true
    
    const data = {
      learningRecord: form.learningRecord,
      type: form.type,
      dueDate: form.dueDate || null
    }
    
    if (editingItem.value) {
      // 编辑
      await checklistStore.updateChecklist({
        ...editingItem.value,
        ...data
      })
      toast.success('更新成功')
    } else {
      // 新增
      await checklistStore.addChecklist(data)
      toast.success('添加成功')
    }
    
    closeModal()
  } catch (error) {
    toast.error('保存失败：' + error.message)
  } finally {
    formLoading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const getDaysFromNow = (createTime, toDate) => {
  // 如果后端已经计算了toDate，优先使用后端的计算结果
  if (typeof toDate === 'number' && toDate >= 0) {
    if (toDate === 0) {
      return '距今0天'
    } else {
      return `距今${toDate}天`
    }
  }
  
  // 如果后端没有提供toDate，则前端计算
  if (!createTime) return ''
  
  const createDate = new Date(createTime)
  const now = new Date()
  
  // 重置时间到当天的开始，只比较日期
  createDate.setHours(0, 0, 0, 0)
  now.setHours(0, 0, 0, 0)
  
  // 计算天数差
  const timeDiff = now.getTime() - createDate.getTime()
  const daysDiff = Math.floor(timeDiff / (1000 * 3600 * 24))
  
  return `距今${daysDiff}天`
}

onMounted(async () => {
  try {
    await checklistStore.fetchChecklists()
  } catch (error) {
    toast.error('加载清单失败：' + error.message)
  }
})
</script>