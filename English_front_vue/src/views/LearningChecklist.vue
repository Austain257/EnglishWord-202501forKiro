<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- 背景装饰，延续学习/听写页面的氛围 -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-blue-50/80 via-slate-50/60 to-transparent pointer-events-none"></div>
    <div class="absolute -top-24 -right-24 w-80 h-80 bg-indigo-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-48 -left-24 w-72 h-72 bg-sky-100/40 rounded-full blur-3xl pointer-events-none"></div>

    <div class="relative z-10 flex flex-col min-h-screen">
      <!-- 顶部导航 -->
      <nav class="px-4 sm:px-6 lg:px-8 h-16 sm:h-20 flex items-center justify-between">
        <div class="flex items-center gap-4">
          <button
            @click="goBack"
            class="p-2 -ml-2 text-slate-500 hover:text-slate-900 hover:bg-white/60 rounded-xl transition-all duration-200"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <div class="flex flex-col">
            <h1 class="text-lg sm:text-xl font-bold text-slate-900 tracking-tight">学习清单</h1>
            <div class="flex items-center gap-2 text-xs text-slate-500">
              <span class="inline-block w-1.5 h-1.5 rounded-full" :class="tabAccentClass"></span>
              <span>{{ currentTabName }}</span>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-3">
          <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
            <span>今日计划</span>
            <span class="font-bold text-blue-600">{{ filteredChecklists.length }}</span>
            <span class="text-slate-400">项</span>
          </div>

          <button
            @click="openCreateModal"
            class="flex items-center gap-2 px-3 sm:px-4 py-2 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl shadow-lg shadow-blue-500/30 hover:shadow-blue-500/40 transition-all active:scale-95"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span class="hidden sm:inline">新增清单</span>
            <span class="sm:hidden text-sm">新增</span>
          </button>
        </div>
      </nav>

      <main class="flex-1 flex flex-col max-w-5xl mx-auto w-full px-4 sm:px-6 lg:px-8 pb-10 space-y-6">
        <!-- 标签切换 -->
        <div class="bg-white/80 backdrop-blur-sm rounded-2xl border border-white/70 shadow-sm p-2 flex gap-2">
          <button
            v-for="(tab, index) in tabs"
            :key="tab.value"
            @click="setActiveTab(index)"
            :class="[
              'flex-1 px-3 sm:px-4 py-2 rounded-xl text-sm font-semibold transition-all',
              activeTab === index
                ? 'bg-gradient-to-r from-blue-500 to-indigo-500 text-white shadow-lg'
                : 'text-slate-500 hover:text-slate-900 hover:bg-slate-50'
            ]"
          >
            <div class="flex items-center justify-center gap-2">
              <span>{{ tab.name }}</span>
              <span v-if="getTabCount(index)" class="text-xs font-bold px-2 py-0.5 rounded-full"
                :class="activeTab === index ? 'bg-white/20 text-white' : 'bg-blue-100 text-blue-600'">
                {{ getTabCount(index) }}
              </span>
            </div>
          </button>
        </div>

        <!-- 主列表 -->
        <section class="bg-white rounded-3xl border border-slate-100 shadow-xl shadow-slate-200/40 flex-1 flex flex-col">
          <div v-if="loading" class="flex-1 flex flex-col items-center justify-center py-16">
            <div class="w-12 h-12 border-4 border-blue-100 border-t-blue-500 rounded-full animate-spin mb-4"></div>
            <p class="text-slate-400 text-sm font-medium animate-pulse">正在加载清单...</p>
          </div>

          <div v-else-if="filteredChecklists.length === 0" class="flex-1 flex flex-col items-center justify-center text-center py-16 px-6">
            <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
              <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2m-6 0a2 2 0 014 0" />
              </svg>
            </div>
            <h3 class="text-xl font-bold text-slate-900 mb-2">暂无 {{ currentTabName }} 清单</h3>
            <p class="text-slate-500 mb-6 max-w-sm">点击 “新增清单” 来规划当前分类的学习内容吧。</p>
            <button
              @click="openCreateModal"
              class="px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl font-semibold shadow-lg shadow-blue-500/30 hover:-translate-y-0.5 transition-all"
            >
              立即添加
            </button>
          </div>

          <div v-else class="flex-1 flex flex-col">
            <div class="flex flex-col gap-4 p-6">
              <article
                v-for="item in filteredChecklists"
                :key="item.id"
                @click="toggleSelection(item.id)"
                :class="[
                  'relative rounded-2xl border transition-all duration-300 group cursor-pointer bg-slate-50/80 hover:bg-white hover:-translate-y-1',
                  selectedItems.includes(item.id) ? 'border-blue-400 shadow-lg shadow-blue-500/20' : 'border-slate-200'
                ]"
              >
                <div class="p-5">
                  <div class="flex items-center gap-4">
                    <div class="flex items-center">
                      <input
                        type="checkbox"
                        :checked="selectedItems.includes(item.id)"
                        class="w-5 h-5 text-blue-600 rounded-full border-2 border-slate-300 focus:ring-blue-500"
                        @click.stop
                        @change="toggleSelection(item.id)"
                      />
                    </div>
                    <div class="flex-1 space-y-4">
                      <div class="flex items-center justify-between">
                        <span
                          class="px-3 py-1 text-xs font-semibold rounded-full"
                          :class="item.alreadyReviewed ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'"
                        >
                          {{ item.alreadyReviewed ? '已复习' : '待复习' }}
                        </span>
                        <button
                          class="p-2 text-emerald-500 hover:text-emerald-600 transition-opacity opacity-0 group-hover:opacity-100"
                          @click.stop="editItem(item)"
                          title="编辑该清单"
                        >
                          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5" />
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 3h6v6m-11 6l9-9" />
                          </svg>
                        </button>
                      </div>

                      <p :class="['text-lg font-semibold leading-relaxed', item.alreadyReviewed ? 'text-slate-400 line-through' : 'text-slate-900']">
                        {{ item.learningRecord }}
                      </p>

                      <div class="space-y-2 text-sm text-slate-500">
                        <div class="flex items-center gap-2">
                          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          <span>{{ getDaysFromNow(item.createTime, item.toDate) }}</span>
                        </div>
                        <div v-if="item.dueDate" class="flex items-center gap-2">
                          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                          </svg>
                          <span>截止：{{ formatDate(item.dueDate) }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </article>
            </div>

            <!-- 批量操作 -->
            <div v-if="hasSelectedItems" class="border-t border-slate-100 px-6 py-4 bg-slate-50/60 rounded-b-3xl flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
              <div class="flex items-center gap-3 text-sm font-medium text-slate-600">
                <div class="w-8 h-8 rounded-full bg-blue-500 text-white flex items-center justify-center font-bold">
                  {{ selectedItems.length }}
                </div>
                <span>已选择清单</span>
              </div>
              <div class="flex flex-wrap gap-3">
                <button
                  class="px-4 py-2 text-sm font-semibold text-white rounded-xl shadow-sm transition-all active:scale-95 bg-emerald-500 hover:bg-emerald-600"
                  @click="markSelectedAsCompleted"
                >
                  标记已复习
                </button>
                <button
                  class="px-4 py-2 text-sm font-semibold text-white rounded-xl shadow-sm transition-all active:scale-95 bg-rose-500 hover:bg-rose-600"
                  @click="batchDelete"
                >
                  批量删除
                </button>
                <button
                  class="px-4 py-2 text-sm font-semibold text-white rounded-xl shadow-sm transition-all active:scale-95 bg-slate-500 hover:bg-slate-600"
                  @click="clearSelection"
                >
                  取消选择
                </button>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>

    <!-- 新增/编辑模态框 -->
    <div v-if="showAddModal || editingItem" class="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-40 px-4">
      <div class="bg-white rounded-3xl shadow-2xl w-full max-w-lg overflow-hidden animate-scale-in">
        <header class="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-6 py-4 flex items-center justify-between">
          <div>
            <p class="text-sm uppercase tracking-[0.3em] text-white/70">Checklist</p>
            <h3 class="text-2xl font-bold">{{ editingItem ? '编辑清单' : '新增清单' }}</h3>
          </div>
          <button @click="closeModal" class="p-2 rounded-full hover:bg-white/20 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </header>

        <form @submit.prevent="submitForm" class="p-6 space-y-6">
          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-2">学习内容</label>
            <textarea
              v-model="form.learningRecord"
              rows="4"
              placeholder="写下今天要完成的学习内容..."
              class="w-full px-4 py-3 border-2 border-slate-200 rounded-2xl focus:outline-none focus:border-blue-500 focus:bg-white transition-all resize-none"
              required
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-2">学习类型</label>
            <div class="grid grid-cols-3 gap-2">
              <button
                v-for="tab in tabs"
                :key="tab.value"
                type="button"
                @click="form.type = tab.value"
                :class="[
                  'rounded-2xl px-3 py-3 text-sm font-semibold border-2 transition-all',
                  form.type === tab.value
                    ? 'border-blue-500 bg-blue-50 text-blue-700 shadow-sm'
                    : 'border-slate-200 text-slate-500 hover:border-slate-300'
                ]"
              >
                {{ tab.name }}
              </button>
            </div>
          </div>

          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-2">截止日期（可选）</label>
            <input v-model="form.dueDate" type="date" class="w-full px-4 py-3 border-2 border-slate-200 rounded-2xl focus:outline-none focus:border-blue-500 focus:bg-white transition-all" />
          </div>

          <div class="flex flex-col sm:flex-row gap-3 pt-2">
            <button
              type="button"
              class="flex-1 px-4 py-3 rounded-2xl font-semibold text-slate-600 border-2 border-slate-200 hover:bg-slate-50 transition-colors"
              @click="closeModal"
            >
              取消
            </button>
            <button
              type="submit"
              class="flex-1 px-4 py-3 rounded-2xl text-white font-semibold bg-gradient-to-r from-blue-600 to-indigo-600 shadow-lg shadow-blue-500/30 hover:-translate-y-0.5 transition-all disabled:opacity-50"
              :disabled="formLoading"
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

const tabs = [
  { name: '句子', value: 0 },
  { name: '单词', value: 1 },
  { name: '听力', value: 2 }
]

const showAddModal = ref(false)
const editingItem = ref(null)
const formLoading = ref(false)

const form = reactive({
  learningRecord: '',
  type: 1,
  dueDate: ''
})

const loading = computed(() => checklistStore.loading)
const activeTab = computed(() => checklistStore.activeTab)
const filteredChecklists = computed(() => checklistStore.filteredChecklists)
const selectedItems = computed(() => checklistStore.selectedItems)
const hasSelectedItems = computed(() => checklistStore.hasSelectedItems)
const reviewedCount = computed(() => filteredChecklists.value.filter(item => item.alreadyReviewed).length)

const currentTabName = computed(() => tabs[activeTab.value]?.name || '学习清单')
const tabAccentClass = computed(() => {
  switch (activeTab.value) {
    case 0:
      return 'bg-rose-400'
    case 1:
      return 'bg-emerald-400'
    case 2:
      return 'bg-sky-400'
    default:
      return 'bg-slate-400'
  }
})

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const openCreateModal = () => {
  showAddModal.value = true
  editingItem.value = null
  form.learningRecord = ''
  form.type = tabs[activeTab.value]?.value ?? 1
  form.dueDate = ''
}

const getTabCount = (index) => {
  if (!checklistStore.checklists) return 0
  return checklistStore.checklists.filter(item => item.type === tabs[index].value).length
}

const setActiveTab = async (tab) => {
  try {
    await checklistStore.setActiveTab(tab)
  } catch (error) {
    toast.error('切换失败：' + error.message)
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
    toast.success('已标记为复习完成')
  } catch (error) {
    toast.error('标记失败：' + error.message)
  }
}

const batchDelete = async () => {
  if (!selectedItems.value.length) return
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
  showAddModal.value = true
  form.learningRecord = item.learningRecord
  form.type = item.type
  form.dueDate = item.dueDate || ''
}

const closeModal = () => {
  showAddModal.value = false
  editingItem.value = null
  form.learningRecord = ''
  form.type = tabs[activeTab.value]?.value ?? 1
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
      await checklistStore.updateChecklist({ ...editingItem.value, ...data })
      toast.success('更新成功')
    } else {
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
  if (typeof toDate === 'number' && toDate >= 0) {
    return `距今${toDate}天`
  }
  if (!createTime) return ''
  const createDate = new Date(createTime)
  const now = new Date()
  createDate.setHours(0, 0, 0, 0)
  now.setHours(0, 0, 0, 0)
  const daysDiff = Math.floor((now - createDate) / (1000 * 3600 * 24))
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
@keyframes scale-in {
  from {
    opacity: 0;
    transform: scale(0.96);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
.animate-scale-in {
  animation: scale-in 0.2s ease-out forwards;
}
</style>
