<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
    <!-- 现代化顶部导航 -->
    <nav class="bg-white/80 backdrop-blur-md shadow-sm border-b border-white/20 sticky top-0 z-40">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center space-x-4">
            <button 
              @click="goBack" 
              class="p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all duration-200 group"
            >
              <svg class="w-5 h-5 transform group-hover:-translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
              </svg>
            </button>
            <div>
              <h1 class="text-xl md:text-2xl font-bold text-gray-900">学习清单</h1>
              <p class="text-sm text-gray-500 hidden sm:block">管理您的学习计划和进度</p>
            </div>
          </div>
          
          <div class="flex items-center space-x-3">
            <!-- 新增按钮 -->
            <button 
              @click="showAddModal = true" 
              class="inline-flex items-center px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white font-medium rounded-xl shadow-lg hover:shadow-xl hover:from-blue-600 hover:to-purple-700 transform hover:scale-105 transition-all duration-200"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
              </svg>
              <span class="hidden sm:inline">新增清单</span>
              <span class="sm:hidden">新增</span>
            </button>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <!-- 激励语句区域 - 简化版 -->
      <div class="mb-8">
        <div class="bg-white/60 backdrop-blur-sm rounded-2xl p-6 shadow-lg border border-white/20 text-center">
          <div class="text-lg md:text-xl font-semibold text-gray-800 mb-2">
            "坚持每天的小进步，成就未来的大突破"
          </div>
          <div class="text-sm text-gray-600">— 学习助手</div>
        </div>
      </div>

      <!-- 标签页导航 - 现代化设计 -->
      <div class="mb-6">
        <div class="bg-white/70 backdrop-blur-sm rounded-2xl p-2 shadow-lg border border-white/20">
          <nav class="flex space-x-1">
            <button
              v-for="(tab, index) in tabs"
              :key="index"
              @click="setActiveTab(index)"
              :class="[
                'flex-1 py-3 px-4 text-sm font-medium rounded-xl transition-all duration-200 relative overflow-hidden',
                activeTab === index
                  ? 'bg-gradient-to-r from-blue-500 to-purple-600 text-white shadow-lg transform scale-105'
                  : 'text-gray-600 hover:text-gray-900 hover:bg-white/50'
              ]"
            >
              <div class="flex items-center justify-center space-x-2">
                <!-- 图标 -->
                <svg v-if="index === 0" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
                <svg v-else-if="index === 1" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
                </svg>
                <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 14.142M8.464 15.536a5 5 0 010-7.072m-2.828-2.828a9 9 0 000 14.142"></path>
                </svg>
                <span>{{ tab.name }}</span>
                <!-- 数量徽章 -->
                <span v-if="getTabCount(index) > 0" :class="[
                  'px-2 py-0.5 text-xs rounded-full font-medium',
                  activeTab === index 
                    ? 'bg-white/20 text-white' 
                    : 'bg-blue-100 text-blue-600'
                ]">
                  {{ getTabCount(index) }}
                </span>
              </div>
            </button>
          </nav>
        </div>
      </div>

      <!-- 批量操作栏 - 移除原位置 -->
      <!-- 这里原来的批量操作栏已移到页面底部 -->

      <!-- 主内容区域 -->
      <div class="bg-white/70 backdrop-blur-sm rounded-2xl shadow-lg border border-white/20 overflow-hidden">
        <!-- 加载状态 -->
        <div v-if="loading" class="flex flex-col items-center justify-center py-16">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mb-4"></div>
          <p class="text-gray-600">加载清单中...</p>
        </div>

        <!-- 清单列表 - 卡片式设计 -->
        <div v-else-if="filteredChecklists.length > 0" class="p-6">
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div
              v-for="item in filteredChecklists"
              :key="item.id"
              :class="[
                'group relative bg-white rounded-xl p-5 shadow-md hover:shadow-xl transition-all duration-300 border-2 cursor-pointer transform hover:-translate-y-1',
                selectedItems.includes(item.id)
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-100 hover:border-blue-200',
                item.alreadyReviewed ? 'opacity-75' : ''
              ]"
              @click="toggleSelection(item.id)"
            >
              <!-- 选择框 -->
              <div class="absolute top-4 right-4">
                <input
                  type="checkbox"
                  :checked="selectedItems.includes(item.id)"
                  class="w-5 h-5 text-blue-600 rounded-lg border-2 border-gray-300 focus:ring-blue-500"
                  @click.stop
                  @change="toggleSelection(item.id)"
                />
              </div>

              <!-- 状态指示器 -->
              <div class="absolute top-4 left-4">
                <div :class="[
                  'w-3 h-3 rounded-full',
                  item.alreadyReviewed ? 'bg-green-500' : 'bg-orange-400'
                ]"></div>
              </div>

              <!-- 内容区域 -->
              <div class="mt-6">
                <h3 :class="[
                  'font-semibold text-lg mb-3 pr-8 leading-tight',
                  item.alreadyReviewed ? 'line-through text-gray-500' : 'text-gray-900'
                ]">
                  {{ item.learningRecord }}
                </h3>

                <!-- 元信息 -->
                <div class="space-y-2 mb-4">
                  <div class="flex items-center text-sm text-gray-500">
                    <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                    {{ getDaysFromNow(item.createTime, item.toDate) }}
                  </div>
                  
                  <div v-if="item.dueDate" class="flex items-center text-sm text-gray-500">
                    <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                    </svg>
                    截止: {{ formatDate(item.dueDate) }}
                  </div>
                </div>

                <!-- 状态标签和操作按钮 -->
                <div class="flex items-center justify-between">
                  <span :class="[
                    'px-3 py-1 text-xs font-medium rounded-full',
                    item.alreadyReviewed 
                      ? 'bg-green-100 text-green-700' 
                      : 'bg-orange-100 text-orange-700'
                  ]">
                    {{ item.alreadyReviewed ? '已完成' : '待完成' }}
                  </span>
                  
                  <button 
                    @click.stop="editItem(item)" 
                    class="opacity-0 group-hover:opacity-100 p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all duration-200"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 批量操作栏 - 移到清单列表底部 -->
          <div v-if="hasSelectedItems" class="mt-8 pt-6 border-t border-gray-200">
            <div class="bg-gradient-to-r from-blue-50 to-purple-50 backdrop-blur-sm rounded-2xl p-4 shadow-lg border border-blue-200/50">
              <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-3 sm:space-y-0">
                <div class="flex items-center space-x-3">
                  <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                    <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                    </svg>
                  </div>
                  <span class="text-blue-700 font-medium">
                    已选择 {{ selectedItems.length }} 项
                  </span>
                </div>
                <div class="flex flex-wrap gap-2">
                  <button 
                    @click="markSelectedAsCompleted" 
                    class="px-4 py-2 bg-green-500 text-white rounded-xl hover:bg-green-600 transition-colors text-sm font-medium shadow-md hover:shadow-lg transform hover:scale-105"
                  >
                    标记完成
                  </button>
                  <button 
                    @click="batchDelete" 
                    class="px-4 py-2 bg-red-500 text-white rounded-xl hover:bg-red-600 transition-colors text-sm font-medium shadow-md hover:shadow-lg transform hover:scale-105"
                  >
                    批量删除
                  </button>
                  <button 
                    @click="clearSelection" 
                    class="px-4 py-2 bg-gray-500 text-white rounded-xl hover:bg-gray-600 transition-colors text-sm font-medium shadow-md hover:shadow-lg transform hover:scale-105"
                  >
                    取消选择
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 - 优化设计 -->
        <div v-else class="text-center py-16">
          <div class="w-24 h-24 bg-gradient-to-br from-blue-100 to-purple-100 rounded-full flex items-center justify-center mx-auto mb-6">
            <svg class="w-12 h-12 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-gray-900 mb-2">暂无{{ tabs[activeTab].name }}清单</h3>
          <p class="text-gray-500 mb-6">开始添加您的第一个学习清单吧</p>
          <button 
            @click="showAddModal = true" 
            class="inline-flex items-center px-6 py-3 bg-gradient-to-r from-blue-500 to-purple-600 text-white font-medium rounded-xl shadow-lg hover:shadow-xl hover:from-blue-600 hover:to-purple-700 transform hover:scale-105 transition-all duration-200"
          >
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
            添加第一个清单
          </button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑清单模态框 - 现代化设计 -->
    <div v-if="showAddModal || editingItem" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md transform transition-all duration-300 scale-100">
        <!-- 模态框头部 -->
        <div class="bg-gradient-to-r from-blue-500 to-purple-600 text-white p-6 rounded-t-2xl">
          <div class="flex items-center justify-between">
            <h3 class="text-xl font-semibold">
              {{ editingItem ? '编辑清单' : '新增清单' }}
            </h3>
            <button 
              @click="closeModal" 
              class="p-2 hover:bg-white/20 rounded-lg transition-colors"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>
        </div>
        
        <!-- 模态框内容 -->
        <form @submit.prevent="submitForm" class="p-6">
          <div class="space-y-6">
            <!-- 学习内容 -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">学习内容</label>
              <textarea 
                v-model="form.learningRecord"
                placeholder="详细描述您要学习的内容..."
                rows="4"
                class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200 resize-none"
                required
              ></textarea>
            </div>
            
            <!-- 类型选择 -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">学习类型</label>
              <div class="grid grid-cols-3 gap-2">
                <button
                  v-for="(tab, index) in tabs"
                  :key="index"
                  type="button"
                  @click="form.type = tab.value"
                  :class="[
                    'p-3 rounded-xl border-2 transition-all duration-200 text-sm font-medium',
                    form.type === tab.value
                      ? 'border-blue-500 bg-blue-50 text-blue-700'
                      : 'border-gray-200 hover:border-gray-300 text-gray-600'
                  ]"
                >
                  {{ tab.name }}
                </button>
              </div>
            </div>
            
            <!-- 截止时间 -->
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">截止时间（可选）</label>
              <input 
                v-model="form.dueDate"
                type="date"
                class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
              />
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="flex space-x-3 mt-8">
            <button 
              @click="closeModal" 
              type="button" 
              class="flex-1 px-4 py-3 border-2 border-gray-200 text-gray-600 font-medium rounded-xl hover:bg-gray-50 transition-colors"
            >
              取消
            </button>
            <button 
              type="submit" 
              :disabled="formLoading"
              class="flex-1 px-4 py-3 bg-gradient-to-r from-blue-500 to-purple-600 text-white font-medium rounded-xl hover:from-blue-600 hover:to-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
            >
              {{ formLoading ? '保存中...' : '保存' }}
            </button>
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

// 获取每个标签页的数量
const getTabCount = (tabIndex) => {
  if (!checklistStore.checklists) return 0
  return checklistStore.checklists.filter(item => item.type === tabs[tabIndex].value).length
}

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

const markAllAsCompleted = async () => {
  try {
    const result = await checklistStore.markAllAsCompleted()
    if (result.message) {
      toast.success(result.message)
    } else {
      toast.success('标记完成成功')
    }
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

<style scoped>
/* 自定义动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 卡片悬停效果 */
.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}

/* 渐变背景动画 */
@keyframes gradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.animate-gradient {
  background-size: 200% 200%;
  animation: gradient 15s ease infinite;
}

/* 响应式网格调整 */
@media (max-width: 640px) {
  .grid-cols-1.md\:grid-cols-2.lg\:grid-cols-3 {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}

@media (min-width: 641px) and (max-width: 1024px) {
  .grid-cols-1.md\:grid-cols-2.lg\:grid-cols-3 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (min-width: 1025px) {
  .grid-cols-1.md\:grid-cols-2.lg\:grid-cols-3 {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 状态指示器动画 */
.status-indicator {
  position: relative;
  overflow: hidden;
}

.status-indicator::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

/* 移动端优化 */
@media (max-width: 640px) {
  .text-xl.md\:text-2xl {
    font-size: 1.25rem;
  }
  
  .p-6 {
    padding: 1rem;
  }
  
  .space-x-3 > * + * {
    margin-left: 0.5rem;
  }
  
  .space-y-6 > * + * {
    margin-top: 1rem;
  }
}

/* 平板端优化 */
@media (min-width: 641px) and (max-width: 1024px) {
  .max-w-7xl {
    max-width: 100%;
    padding: 0 1rem;
  }
}

/* 焦点样式优化 */
input:focus, textarea:focus, select:focus, button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 按钮点击效果 */
button:active {
  transform: scale(0.98);
}

/* 卡片进入动画 */
.card-enter {
  animation: fadeInUp 0.3s ease-out;
}

/* 加载状态优化 */
.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>