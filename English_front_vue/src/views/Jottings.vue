<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- Background Decorations -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-teal-50/80 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 -right-20 w-96 h-96 bg-green-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-40 -left-20 w-72 h-72 bg-teal-100/40 rounded-full blur-3xl pointer-events-none"></div>

    <!-- Top Navigation -->
    <nav class="sticky top-0 z-40 w-full bg-white/80 backdrop-blur-xl supports-[backdrop-filter]:bg-white/65">
      <div class="w-full px-4 sm:px-8 lg:px-16">
        <div class="flex flex-wrap items-center justify-between gap-4 py-4">
          <div class="flex items-center gap-4 min-w-0 flex-1">

            <button @click="goBack" class="p-2 -ml-2 text-slate-500 hover:text-slate-900 hover:bg-white/60 rounded-xl transition-all duration-200 shrink-0">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
            </button>
            <div class="min-w-0">
              <p class="text-[11px] uppercase tracking-[0.45em] text-slate-400">Jotting Lab</p>
              <h1 class="text-xl sm:text-2xl font-semibold text-slate-900 leading-tight">积累本</h1>
              <p class="text-sm text-slate-500 truncate">记录灵感碎片，沉淀你的专属语料</p>
            </div>
          </div>
          <div class="flex items-center gap-3 justify-end flex-1">

            <div class="flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/70 text-xs font-medium text-slate-600">
              <span>总计</span>
              <span class="text-teal-600 font-semibold">{{ totalItems }}</span>
              <span>条积累</span>
            </div>
            <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 rounded-full bg-white/70 text-xs font-medium text-slate-600">
              <span>当前</span>
              <span class="text-slate-900 font-semibold">{{ tabs[activeTab].name }}</span>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-1 relative z-10 max-w-4xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-6 sm:py-8">
      <div v-if="message.text" :class="['mb-4 px-4 py-3 rounded-xl text-sm font-semibold flex items-start gap-2 shadow-sm border',
        message.type === 'error' ? 'bg-rose-50 text-rose-700 border-rose-100' : 'bg-emerald-50 text-emerald-700 border-emerald-100']">
        <svg class="w-4 h-4 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span class="flex-1">{{ message.text }}</span>
      </div>

      <!-- Tab Navigation -->
      <div class="mb-6 sm:mb-8 flex justify-center">
        <div class="relative bg-white/80 backdrop-blur-sm rounded-xl border border-slate-200/60 p-1 flex items-center text-sm font-semibold">
          <button 
            v-for="(tab, index) in tabs"
            :key="index"
            @click="setActiveTab(index)"
            :class="['px-4 sm:px-6 py-2 rounded-lg transition-all', activeTab === index ? 'bg-white text-teal-600 shadow-sm' : 'text-slate-500 hover:text-slate-800']"
          >
            {{ tab.name }}
          </button>
        </div>
      </div>

      <!-- Batch Actions Bar -->
      <div v-if="hasSelectedItems" class="sticky top-[65px] sm:top-[81px] z-30 mb-6 p-3 bg-teal-50/80 backdrop-blur-md border border-teal-200/60 rounded-xl flex justify-between items-center animate-fade-in-down">
        <span class="text-sm font-semibold text-teal-800">
          已选择 {{ selectedItems.length }} 项
        </span>
        <div class="flex items-center gap-2">
          <button @click="batchMarkAsReviewed" class="px-3 py-1.5 text-xs font-semibold text-white bg-teal-500 hover:bg-teal-600 rounded-lg shadow-sm transition-colors">已掌握</button>
          <button @click="batchDelete" class="px-3 py-1.5 text-xs font-semibold text-rose-600 bg-rose-100 hover:bg-rose-200 rounded-lg transition-colors">删除</button>
          <button @click="clearSelection" class="p-2 text-slate-500 hover:bg-slate-200 rounded-full transition-colors">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="flex flex-col items-center justify-center py-20">
        <div class="w-10 h-10 border-4 border-teal-100 border-t-teal-500 rounded-full animate-spin mb-4"></div>
        <p class="text-slate-400 text-sm font-medium animate-pulse">正在加载积累...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="jottings.length === 0" class="text-center py-20">
        <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300 mx-auto">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path></svg>
        </div>
        <h3 class="text-xl font-bold text-slate-900 mb-2">暂无{{ tabs[activeTab].name }}积累</h3>
        <p class="text-slate-500">点击右下角的 "+" 按钮添加你的第一条积累吧！</p>
      </div>

      <!-- Jottings List -->
      <div v-else class="space-y-4">
        <div
          v-for="item in jottings"
          :key="item.id"
          @click="toggleSelection(item.id)"
          :class="[
            'relative group bg-white border rounded-2xl p-4 sm:p-5 transition-all duration-200 cursor-pointer',
            selectedItems.includes(item.id)
              ? 'border-teal-400 bg-teal-50/50 shadow-md'
              : 'border-slate-200/80 hover:border-slate-300 hover:shadow-sm',
            item.reviewed ? 'opacity-60 hover:opacity-100' : ''
          ]"
        >
          <div class="flex items-start gap-4">
            <div :class="['flex-shrink-0 w-5 h-5 mt-1 rounded-md flex items-center justify-center border', selectedItems.includes(item.id) ? 'bg-teal-500 border-teal-500' : 'border-slate-300']">
              <svg v-if="selectedItems.includes(item.id)" class="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
            </div>
            
            <div class="flex-1">
              <div class="flex justify-between items-start mb-2">
                <div class="text-lg font-semibold text-slate-900">
                  {{ getContent(item) }}
                </div>
                <div class="hidden group-hover:flex items-center gap-1 absolute top-4 right-4 bg-white/50 backdrop-blur-sm p-1 rounded-lg border border-slate-200/60">
                  <button @click.stop="editItem(item)" class="p-1.5 text-slate-500 hover:text-teal-600 hover:bg-slate-100 rounded-md transition-colors"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.5L16.732 3.732z" /></svg></button>
                  <button @click.stop="deleteItem(item)" class="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-slate-100 rounded-md transition-colors"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg></button>
                </div>
              </div>
              
              <div class="text-slate-600 mb-3">
                {{ item.chinese }}
              </div>

              <div class="flex items-center justify-between text-xs text-slate-400">
                <span>{{ formatDate(item.createTime) }}</span>
                <div v-if="!item.reviewed" @click.stop="markAsReviewed(item)" class="flex items-center gap-1.5 px-2 py-1 rounded-full bg-slate-100 hover:bg-teal-100 text-slate-500 hover:text-teal-700 transition-colors">
                  <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>
                  <span>标记为已掌握</span>
                </div>
                <div v-else class="flex items-center gap-1.5 px-2 py-1 rounded-full bg-green-50 text-green-600">
                   <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                  <span>已掌握</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="mt-8 flex justify-center">
        <div class="flex items-center gap-2">
          <button @click="goToPage(currentPage - 1)" :disabled="currentPage <= 1" class="px-3 py-2 text-sm font-semibold text-slate-600 bg-white border border-slate-200 rounded-lg shadow-sm hover:bg-slate-50 disabled:opacity-50 transition-colors">上一页</button>
          <span class="px-4 py-2 text-sm font-medium text-slate-600 bg-white border border-slate-200 rounded-lg shadow-sm">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages" class="px-3 py-2 text-sm font-semibold text-slate-600 bg-white border border-slate-200 rounded-lg shadow-sm hover:bg-slate-50 disabled:opacity-50 transition-colors">下一页</button>
        </div>
      </div>
    </main>

    <!-- Add/Edit Modal -->
    <div v-if="isModalOpen" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div @click="closeModal" class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity"></div>
      <div class="relative w-full max-w-lg bg-white rounded-3xl shadow-2xl p-6 sm:p-8 animate-scale-in">
        <h3 class="text-2xl font-bold text-slate-900 mb-6">{{ modalMode === 'add' ? '添加新积累' : '编辑积累' }}</h3>
        <form @submit.prevent="submitForm">
          <div class="space-y-5">
            <div v-if="modalMode === 'add'">
              <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">类型</label>
              <div class="flex gap-2">
                <button type="button" @click="form.type = 0" :class="['flex-1 py-3 rounded-xl font-semibold transition-colors', form.type === 0 ? 'bg-teal-500 text-white shadow-md' : 'bg-slate-100 text-slate-600 hover:bg-slate-200']">句子</button>
                <button type="button" @click="form.type = 1" :class="['flex-1 py-3 rounded-xl font-semibold transition-colors', form.type === 1 ? 'bg-teal-500 text-white shadow-md' : 'bg-slate-100 text-slate-600 hover:bg-slate-200']">单词</button>
              </div>
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">{{ form.type === 1 ? '单词' : '句子' }}</label>
              <textarea v-model="form.content" rows="3" class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-teal-500 focus:bg-white transition-all font-semibold text-slate-900" required></textarea>
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">中文翻译</label>
              <textarea v-model="form.chinese" rows="3" class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-teal-500 focus:bg-white transition-all font-semibold text-slate-900" required></textarea>
            </div>
          </div>
          <div class="flex gap-3 mt-8">
            <button @click="closeModal" type="button" class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors">取消</button>
            <button type="submit" :disabled="submitLoading" class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-teal-500 hover:bg-teal-600 shadow-lg shadow-teal-500/20 transition-colors disabled:opacity-60">{{ submitLoading ? '保存中...' : '确认保存' }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Floating Action Button -->
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'
import { jottingService } from '@/services/jotting.service'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const { success, error } = useToast()
const authStore = useAuthStore()

// State
const loading = ref(false)
const submitLoading = ref(false)
const jottings = ref([])
const selectedItems = ref([])
const isModalOpen = ref(false)
const modalMode = ref('add') // 'add' or 'edit'
const editingItemId = ref(null)
const activeTab = ref(0) // 0-句子, 1-单词
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)

const tabs = [
  { name: '句子', value: 0 },
  { name: '单词', value: 1 }
]

const form = reactive({
  type: 0,
  content: '',
  chinese: ''
})

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

// Computed
const hasSelectedItems = computed(() => selectedItems.value.length > 0)
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize.value))

// Methods
const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const setActiveTab = (tabIndex) => {
  if (activeTab.value === tabIndex) return
  activeTab.value = tabIndex
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
      pageSize: pageSize.value,
      type: tabs[activeTab.value].value
    }
    const response = await jottingService.getJottingList(params)
    const data = response.data
    if (data && data.records) {
      const filteredRecords = data.records.filter(
        item => item.type === tabs[activeTab.value].value
      )
      jottings.value = filteredRecords
      totalItems.value = data.total
    } else {
      jottings.value = []
      totalItems.value = 0
    }
  } catch (err) {
    showMessage('加载积累失败：' + (err?.message || '请稍后重试'), 'error')
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

const clearSelection = () => selectedItems.value = []

const getContent = (item) => item.type === 1 ? item.word : item.sentence

const markAsReviewed = async (item) => {
  try {
    await jottingService.markAsReviewed(authStore.user.id, item.id)
    item.reviewed = 1
    showMessage('标记成功', 'success')
  } catch (err) {
    showMessage('标记失败：' + (err?.message || '请稍后重试'), 'error')
  }
}

const batchMarkAsReviewed = async () => {
  try {
    const promises = selectedItems.value.map(id => jottingService.markAsReviewed(authStore.user.id, id))
    await Promise.all(promises)
    jottings.value.forEach(item => {
      if (selectedItems.value.includes(item.id)) item.reviewed = 1
    })
    clearSelection()
    showMessage('批量标记成功', 'success')
  } catch (err) {
    showMessage('批量标记失败：' + (err?.message || '请稍后重试'), 'error')
  }
}

const deleteItem = async (item) => {
  if (!confirm('确定要删除这条积累吗？')) return
  try {
    await jottingService.deleteJotting({ id: item.id, userId: authStore.user.id })
    await fetchJottings() // Re-fetch to update total count and pagination
    showMessage('删除成功', 'success')
  } catch (err) {
    showMessage('删除失败：' + (err?.message || '请稍后重试'), 'error')
  }
}

const batchDelete = async () => {
  if (!confirm(`确定要删除选中的 ${selectedItems.value.length} 条积累吗？`)) return
  try {
    const deleteData = selectedItems.value.map(id => ({ id, userId: authStore.user.id }))
    await jottingService.batchDeleteJotting(deleteData)
    clearSelection()
    await fetchJottings()
    showMessage('批量删除成功', 'success')
  } catch (err) {
    showMessage('批量删除失败：' + (err?.message || '请稍后重试'), 'error')
  }
}

const openAddModal = () => {
  modalMode.value = 'add'
  resetForm()
  form.type = activeTab.value
  isModalOpen.value = true
}

const editItem = (item) => {
  modalMode.value = 'edit'
  editingItemId.value = item.id
  form.type = item.type
  form.content = getContent(item)
  form.chinese = item.chinese
  isModalOpen.value = true
}

const closeModal = () => {
  isModalOpen.value = false
  editingItemId.value = null
}

const resetForm = () => {
  form.type = 0
  form.content = ''
  form.chinese = ''
}

const submitForm = async () => {
  try {
    submitLoading.value = true
    const payload = {
      userId: authStore.user.id,
      type: form.type,
      chinese: form.chinese
    }
    if (form.type === 1) {
      payload.word = form.content
    } else {
      payload.sentence = form.content
    }

    if (modalMode.value === 'edit') {
      payload.id = editingItemId.value
      await jottingService.updateJotting(payload)
    } else {
      await jottingService.addJotting(payload)
    }
    
    closeModal()
    await fetchJottings()
    showMessage(modalMode.value === 'add' ? '添加成功' : '更新成功', 'success')
  } catch (err) {
    showMessage((modalMode.value === 'add' ? '添加失败：' : '更新失败：') + (err?.message || '请稍后重试'), 'error')
  } finally {
    submitLoading.value = false
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
  return new Date(dateString).toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
}

onMounted(() => {
  fetchJottings()
})
</script>

<style scoped>
@keyframes scale-in {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-scale-in {
  animation: scale-in 0.2s ease-out forwards;
}

@keyframes fade-in-down {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-down {
  animation: fade-in-down 0.3s ease-out forwards;
}
</style>