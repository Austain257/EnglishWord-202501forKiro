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
            <span class="font-bold text-blue-600">{{ totalChecklists }}</span>
            <span class="text-slate-400">项</span>
          </div>

          <button
            @click="refreshList"
            class="flex items-center gap-2 px-3 sm:px-4 py-2 text-sm font-semibold text-slate-600 border border-slate-200 rounded-xl bg-white/80 hover:bg-slate-100 transition-all"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            <span class="hidden sm:inline">刷新清单</span>
            <span class="sm:hidden text-sm">刷新</span>
          </button>

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

      <!-- 友好弹窗 -->
      <div
        v-if="message.popup"
        class="fixed inset-0 z-50 flex items-center justify-center px-4 sm:px-6"
      >
        <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm" @click="message.popup = false"></div>
        <div class="relative w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 space-y-4">
          <div class="flex items-start gap-3">
            <div class="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-600 flex items-center justify-center">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="text-slate-800 text-sm leading-relaxed">
              {{ message.text }}
            </div>
          </div>
          <div class="flex justify-end">
            <button
              class="px-4 py-2 rounded-xl bg-emerald-500 text-white font-semibold shadow-sm hover:bg-emerald-600 transition-colors"
              @click="message.popup = false"
            >
              好的
            </button>
          </div>
        </div>
      </div>

      <main class="flex-1 flex flex-col max-w-5xl mx-auto w-full px-4 sm:px-6 lg:px-8 pb-10 space-y-6">
        <!-- 标签切换 -->
        <div class="rounded-2xl bg-white/90 backdrop-blur shadow-xl border border-slate-100 overflow-hidden">
          <div class="p-2 flex gap-2">
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
                <span
                  v-if="getTabCount(index)"
                  class="text-xs font-bold px-2 py-0.5 rounded-full"
                  :class="activeTab === index ? 'bg-white/20 text-white' : 'bg-blue-100 text-blue-600'"
                >
                  {{ getTabCount(index) }}
                </span>
              </div>
            </button>
          </div>
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
                :class="[
                  'relative rounded-2xl border transition-all duration-300 group bg-slate-50/80 hover:bg-white hover:-translate-y-1',
                  selectedItems.includes(item.id) ? 'border-blue-400 shadow-lg shadow-blue-500/20' : 'border-slate-200',
                  isChecklistItemLocked(item) ? 'opacity-70' : ''
                ]"
              >
                <div class="p-5">
                  <div class="flex items-center gap-4">
                    <div class="flex items-center">
                      <input
                        type="checkbox"
                        :checked="selectedItems.includes(item.id)"
                        class="w-5 h-5 text-blue-600 rounded-full border-2 border-slate-300 focus:ring-blue-500 disabled:opacity-40 disabled:cursor-not-allowed"
                        :disabled="isChecklistItemLocked(item)"
                        :title="isChecklistItemLocked(item) ? '该清单对应轮次已完成' : '选择该清单'"
                        @click.stop
                        @change="toggleSelection(item.id)"
                      />
                    </div>
                    <div
                      :class="[
                        'flex-1 space-y-4 transition-opacity',
                        isChecklistItemLocked(item) ? 'cursor-not-allowed opacity-60' : 'cursor-pointer'
                      ]"
                      :aria-disabled="isChecklistItemLocked(item)"
                      @click="handleCardClick(item)"
                    >
                      <div class="flex flex-col gap-3">
                        <div class="flex items-center justify-between">
                          <div class="flex flex-wrap items-center gap-2">
                            <span
                              class="px-3 py-1 text-xs font-semibold rounded-full"
                              :class="item.alreadyReviewed ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'"
                            >
                              {{ item.alreadyReviewed ? '已复习' : '待复习' }}
                            </span>
                            <span
                              v-if="showRoundBadge(item)"
                              class="px-3 py-1 text-xs font-semibold rounded-full bg-indigo-50 text-indigo-600"
                            >
                              {{ getReviewRound(item) }}
                            </span>
                          </div>
                          <button
                            class="p-2 text-emerald-500 hover:text-emerald-600 transition-opacity opacity-0 group-hover:opacity-100"
                            @click.stop="editItem(item)"
                            title="编辑该清单"
                          >
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v10a2 2 0 002 2h11a2 2 0 002-2V7a2 2 0 00-2-2h-2m-6 0a2 2 0 014 0" />
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 3h6v6m-11 6l9-9" />
                            </svg>
                          </button>
                        </div>

                        <p :class="['text-base sm:text-lg font-semibold leading-relaxed', item.alreadyReviewed ? 'text-slate-400 line-through' : 'text-slate-900']">
                          {{ item.learningRecord }}
                        </p>

                        <div class="flex flex-wrap items-center justify-between gap-3 text-xs sm:text-sm text-slate-500">
                          <div class="flex items-center gap-2 text-slate-500">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                            <span>{{ getDaysFromNow(item.createTime) }}</span>
                          </div>
                          <div class="flex items-center gap-2 text-slate-400 text-xs">
                            <span>创建于 {{ new Date(item.createTime).toLocaleDateString('zh-CN') }}</span>
                            <span v-if="item.updateTime">· 更新于 {{ new Date(item.updateTime).toLocaleDateString('zh-CN') }}</span>
                          </div>
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
      <div class="bg-white rounded-3xl shadow-2xl w-full max-w-lg sm:max-h-none max-h-[90vh] overflow-hidden animate-scale-in">
        <header class="bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-5 sm:px-6 py-4 flex items-center justify-between">
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

        <form @submit.prevent="submitForm" class="p-5 sm:p-6 space-y-6 max-h-[75vh] sm:max-h-none overflow-y-auto">
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

          <div v-if="isWordType" class="grid gap-4 sm:grid-cols-2">
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-2">关联课本</label>
              <select
                v-model="form.bookId"
                class="w-full px-4 py-3 border-2 border-slate-200 rounded-2xl bg-white focus:outline-none focus:border-blue-500"
              >
                <option value="" disabled>请选择课本</option>
                <option
                  v-for="book in books"
                  :key="book.id"
                  :value="book.id"
                >
                  {{ book.bookName || book.name || `课本 #${book.id}` }}
                </option>
              </select>
              <p class="mt-1 text-xs text-slate-400">如无可选课本，请先前往课本管理新增。</p>
            </div>

            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-2">单词范围</label>
              <div class="flex flex-col gap-2 sm:flex-row sm:items-center sm:gap-3">
                <input
                  v-model.number="form.startId"
                  type="number"
                  min="1"
                  class="w-full sm:flex-1 px-4 py-3 border-2 border-slate-200 rounded-2xl focus:outline-none focus:border-blue-500"
                  placeholder="起始 ID"
                />
                <span class="text-slate-400 text-sm text-center sm:w-auto">至</span>
                <input
                  v-model.number="form.endId"
                  type="number"
                  min="1"
                  class="w-full sm:flex-1 px-4 py-3 border-2 border-slate-200 rounded-2xl focus:outline-none focus:border-blue-500"
                  placeholder="结束 ID"
                />
              </div>
              <p class="mt-1 text-xs text-slate-400">需为正整数，且结束 ID 大于起始 ID。</p>
            </div>
          </div>

          <div v-else class="rounded-2xl border-2 border-dashed border-slate-200 p-4 text-sm text-slate-500 bg-slate-50/60">
            句子 / 听力类型会自动绑定系统默认课本与范围，无需手动填写，提交后会自动进入今日清单。
          </div>

          <div v-if="isWordType">
            <label class="block text-sm font-semibold text-slate-600 mb-2">关联记录 ID（可选）</label>
            <input
              v-model="form.recordIds"
              type="text"
              class="w-full px-4 py-3 border-2 border-slate-200 rounded-2xl focus:outline-none focus:border-blue-500 focus:bg-white transition-all"
              placeholder="例如 101,102,103 或 101-120"
            />
            <p class="mt-1 text-xs text-slate-400">留空将默认保存为“起始ID-结束ID”。</p>
          </div>
          <div v-else class="rounded-2xl border border-slate-100 bg-slate-50/80 px-4 py-3 text-xs text-slate-500">
            句子/听力默认记录信息即可，无需额外填写记录 ID。
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
import { useBookStore } from '@/stores/book'
import { useAuthStore } from '@/stores/auth'
import { useWordStudyStore } from '@/stores/wordStudy'
import { sentenceService } from '@/services/sentence.service'

const router = useRouter()
const checklistStore = useChecklistStore()
const bookStore = useBookStore()
const authStore = useAuthStore()
const wordStudyStore = useWordStudyStore()
const noChecklistHintShown = ref(false)
const emptyFriendlyText = '太好了，今日没有待学习清单，可以好好休息一下，这段时间也累了吧，晚上跑跑步，吃些好吃的吧~'
const message = reactive({
  text: '',
  type: 'info',
  popup: false
})

const showMessage = (text, type = 'info') => {
  message.text = text
  message.type = type
  message.popup = false
  setTimeout(() => {
    message.text = ''
  }, 3200)
}

const showPopupMessage = (text) => {
  message.text = text
  message.type = 'info'
  message.popup = true
}

const showEmptyPopupIfNeeded = () => {
  const total = checklistStore.checklists?.length || 0
  if (total === 0 && !noChecklistHintShown.value) {
    showPopupMessage(emptyFriendlyText)
    noChecklistHintShown.value = true
  }
}

const tabs = [
  { name: '句子', value: 0 },
  { name: '单词', value: 1 },
  { name: '听力', value: 2 }
]

const showAddModal = ref(false)
const editingItem = ref(null)
const formLoading = ref(false)
const reviewIntervals = [0, 1, 2, 4, 7, 15, 30]
const DISPLAY_ROUND_OFFSET = 3  // 第三轮对应第0天
const autoSelectLoading = ref(false)

const form = reactive({
  learningRecord: '',
  type: 1,
  bookId: null,
  startId: 1,
  endId: 10,
  recordIds: ''
})

const loading = computed(() => checklistStore.loading)
const activeTab = computed(() => checklistStore.activeTab)
const filteredChecklists = computed(() => checklistStore.filteredChecklists)
const totalChecklists = computed(() => (checklistStore.checklists?.length ?? 0))
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

const books = computed(() => bookStore.books || [])
const isWordType = computed(() => form.type === 1)
const isSentenceType = computed(() => form.type === 0)
const isNavigatingToReview = ref(false)

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
  form.bookId = bookStore.currentBook?.id || books.value[0]?.id || null
  form.startId = 1
  form.endId = 10
  form.recordIds = ''
}

const getTabCount = (index) => {
  if (!checklistStore.checklists) return 0
  return checklistStore.checklists.filter(item => item.type === tabs[index].value).length
}

const setActiveTab = async (tab) => {
  await checklistStore.setActiveTab(tab)
  clearSelection()
  // 切换后重新做自动标记（单词/句子）
  await autoMarkReviewedByRecordIds()
  await autoMarkSentenceByRecordIds()
}

const toggleSelection = (itemId) => {
  checklistStore.toggleSelection(itemId)
}

const goToWordReview = async (item) => {
  if (!authStore.user?.id) {
    showMessage('请先登录后再开始复习', 'error')
    return
  }
  if (!item.bookId || !item.startId || !item.endId) {
    showMessage('该清单缺少完整的课本范围，无法跳转', 'error')
    return
  }
  if (isNavigatingToReview.value) return
  isNavigatingToReview.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 180))
    await router.push({
      name: 'WordReviewOther',
      query: {
        userId: authStore.user.id,
        bookId: item.bookId,
        startId: item.startId,
        endId: item.endId,
        recordIds: item.recordIds || '',
        source: 'checklist'
      }
    })
  } catch (error) {
    showMessage('跳转复习页面失败：' + (error?.message || '请稍后重试'), 'error')
  } finally {
    setTimeout(() => {
      isNavigatingToReview.value = false
    }, 320)
  }
}

const goToSentenceDictation = async (item) => {
  if (!authStore.user?.id) {
    showMessage('请先登录后再开始句子听写', 'error')
    return
  }
  if (!item.startId || !item.endId) {
    showMessage('该清单缺少完整的句子范围，无法跳转', 'error')
    return
  }
  if (isNavigatingToReview.value) return
  isNavigatingToReview.value = true
  try {
    await new Promise((resolve) => setTimeout(resolve, 180))
    await router.push({
      name: 'SentenceDictation',
      query: {
        userId: authStore.user.id,
        start_id: item.startId,
        end_id: item.endId,
        recordIds: item.recordIds || ''
      }
    })
  } catch (error) {
    showMessage('跳转句子听写失败：' + (error?.message || '请稍后重试'), 'error')
  } finally {
    setTimeout(() => {
      isNavigatingToReview.value = false
    }, 320)
  }
}

const handleCardClick = (item) => {
  if (item.alreadyReviewed) {
    showMessage('该清单已标记完成', 'info')
    return
  }
  if (item.type === 1) {
    goToWordReview(item)
  } else if (item.type === 0) {
    goToSentenceDictation(item)
  } else {
    toggleSelection(item.id)
  }
}

const clearSelection = () => {
  checklistStore.clearSelection()
}

const markSelectedAsCompleted = async () => {
  try {
    await checklistStore.markSelectedAsCompleted()
    showMessage('已标记为复习完成', 'success')
  } catch (error) {
    showMessage('标记失败：' + (error?.message || '请稍后重试'), 'error')
  }
}

const batchDelete = async () => {
  if (!selectedItems.value.length) return
  if (!confirm('确定要删除选中的清单吗？')) return
  try {
    await checklistStore.batchDeleteChecklists(selectedItems.value.map(id => id.toString()))
    showMessage('删除成功', 'success')
  } catch (error) {
    showMessage('删除失败：' + (error?.message || '请稍后重试'), 'error')
  }
}

const editItem = (item) => {
  editingItem.value = item
  showAddModal.value = true
  form.learningRecord = item.learningRecord
  form.type = item.type
  form.bookId = item.bookId || bookStore.currentBook?.id || null
  form.startId = item.startId || 1
  form.endId = item.endId || 10
  form.recordIds = item.recordIds || ''
}

const closeModal = () => {
  showAddModal.value = false
  editingItem.value = null
  form.learningRecord = ''
  form.type = tabs[activeTab.value]?.value ?? 1
  form.bookId = bookStore.currentBook?.id || books.value[0]?.id || null
  form.startId = 1
  form.endId = 10
  form.recordIds = ''
}

const submitForm = async () => {
  try {
    formLoading.value = true
    if (isWordType.value) {
      if (!form.bookId) {
        showMessage('请先选择关联课本', 'error')
        formLoading.value = false
        return
      }
      if (form.startId <= 0 || form.endId <= 0 || form.startId >= form.endId) {
        showMessage('请输入正确的单词范围（起始需小于结束）', 'error')
        formLoading.value = false
        return
      }
    }
    const normalizedRecordIds = isWordType.value ? form.recordIds?.trim() || `${form.startId}-${form.endId}` : null
    const payload = {
      learningRecord: form.learningRecord,
      type: form.type,
      bookId: isWordType.value ? Number(form.bookId) : undefined,
      startId: isWordType.value ? Number(form.startId) : undefined,
      endId: isWordType.value ? Number(form.endId) : undefined,
      recordIds: normalizedRecordIds
    }
    const data = {
      ...payload
    }
    if (editingItem.value) {
      await checklistStore.updateChecklist({ ...editingItem.value, ...data })
      showMessage('更新成功', 'success')
    } else {
      await checklistStore.addChecklist(data)
      showMessage('添加成功', 'success')
    }
    closeModal()
  } catch (error) {
    showMessage('保存失败：' + (error?.message || '请稍后重试'), 'error')
  } finally {
    formLoading.value = false
  }
}

const getBookName = (bookId) => {
  if (!bookId) return '未绑定课本'
  const match = books.value.find((book) => book.id === Number(bookId))
  return match?.bookName || match?.name || `课本 #${bookId}`
}

const computeRoundFromCreateTime = (createTime) => {
  if (!createTime) return DISPLAY_ROUND_OFFSET
  const maxRound = 9
  const createDate = new Date(createTime)
  const now = new Date()
  createDate.setHours(0, 0, 0, 0)
  now.setHours(0, 0, 0, 0)
  const diffDays = Math.max(0, Math.floor((now - createDate) / (1000 * 3600 * 24)))
  const stageIndex = reviewIntervals.findIndex((day) => diffDays <= day)
  const round =
    stageIndex === -1
      ? DISPLAY_ROUND_OFFSET + reviewIntervals.length
      : DISPLAY_ROUND_OFFSET + stageIndex
  return Math.min(maxRound, Math.max(1, round))
}

const getReviewRound = (item) => {
  if (!item) return ''
  // 单词、句子统一按时间推算当前轮次
  if (item.type === 1 || item.type === 0) {
    const round = computeRoundFromCreateTime(item.createTime)
    return `第${round}轮`
  }
  // 听力：不显示
  return ''
}

const getDaysFromNow = (createTime) => {
  if (!createTime) return ''
  const createDate = new Date(createTime)
  const now = new Date()
  createDate.setHours(0, 0, 0, 0)
  now.setHours(0, 0, 0, 0)
  const daysDiff = Math.floor((now - createDate) / (1000 * 3600 * 24))
  return `距今${daysDiff}天`
}

const formatRange = (item) => {
  if (!item.startId || !item.endId) return '范围未设置'
  return `${item.startId} - ${item.endId}`
}

const refreshList = async () => {
  try {
    await checklistStore.resetSelected()
    await checklistStore.fetchChecklists()
    await autoMarkReviewedByRecordIds()
    await autoMarkSentenceByRecordIds()
    showMessage('学习清单已刷新', 'success')
    showEmptyPopupIfNeeded()
  } catch (error) {
    showMessage('刷新失败：' + (error?.message || '请稍后重试'), 'error')
  }
}

onMounted(async () => {
  try {
    if (!bookStore.books.length) {
      await bookStore.fetchBooks()
    }
    form.bookId = bookStore.currentBook?.id || books.value[0]?.id || null
    await checklistStore.fetchChecklists()
    await autoMarkReviewedByRecordIds()
    await loadSentenceRounds()
    showEmptyPopupIfNeeded()
  } catch (error) {
    showMessage('加载清单失败：' + (error?.message || '请稍后重试'), 'error')
  }
})

const parseRecordIds = (text) => {
  if (!text || typeof text !== 'string') return []
  const matches = text.match(/\d+/g) || []
  const unique = [...new Set(matches.map((n) => Number(n)).filter((n) => Number.isFinite(n) && n > 0))]
  return unique
}

const getTargetRoundNumber = (item) => {
  const label = getReviewRound(item) || ''
  const match = label.match(/第(\d+)轮/)
  return match ? Number(match[1]) : null
}

const isChecklistItemLocked = (item) => {
  if (!item) return false
  const targetRound = getTargetRoundNumber(item)
  if (!targetRound) return Boolean(item.alreadyReviewed)
  const fieldCamel = `round${targetRound}ReviewTime`
  const fieldSnake = `round_${targetRound}_review_time`
  if (item[fieldCamel] || item[fieldSnake]) {
    return true
  }
  return Boolean(item.alreadyReviewed)
}

const isRoundCompleted = (record, round) => {
  if (!record || !round) return false
  const camel = `round${round}ReviewTime`
  const snake = `round_${round}_review_time`
  return Boolean(record[camel] || record[snake])
}

const autoMarkReviewedByRecordIds = async () => {
  if (!authStore.user?.id) return
  const list = filteredChecklists.value || []
  const wordItems = list.filter((item) => item.type === 1 && item.recordIds)
  if (!wordItems.length) return

  try {
    autoSelectLoading.value = true
    const doneIds = []
    const undoIds = []
    for (const item of wordItems) {
      const recordIds = parseRecordIds(item.recordIds)
      const targetRound = getTargetRoundNumber(item)
      if (!recordIds.length || !targetRound) continue

      try {
        const res = await wordStudyStore.getRecordsByIds({ userId: authStore.user.id, recordIds })
        const records = Array.isArray(res) ? res : res?.data || []
        if (!records.length) {
          if (item.alreadyReviewed) {
            undoIds.push(item.id)
          }
          continue
        }
        const finished = records.every((record) => isRoundCompleted(record, targetRound))
        if (finished) {
          doneIds.push(item.id)
        } else if (item.alreadyReviewed) {
          undoIds.push(item.id)
        }
      } catch (error) {
        console.warn('自动标记复习状态失败', error)
      }
    }

    const doneIdStrings = doneIds.map((i) => i?.toString()).filter(Boolean)
    const undoIdStrings = undoIds.map((i) => i?.toString()).filter(Boolean)

    if (doneIdStrings.length) {
      await checklistStore.setReview(doneIdStrings)
    }
    if (undoIdStrings.length) {
      for (const id of undoIdStrings) {
        try {
          const original = checklistStore.checklists.find((c) => String(c.id) === String(id))
          if (original) {
            await checklistStore.updateChecklist({ ...original, alreadyReviewed: 0 })
          }
        } catch (error) {
          console.warn('取消复习标记失败', error)
        }
      }
    }
    if (doneIds.length || undoIds.length) {
      await checklistStore.fetchChecklists()
    }
  } catch (error) {
    console.error('自动标记失败', error)
  } finally {
    autoSelectLoading.value = false
  }
}

const showRoundBadge = (item) => {
  if (!item) return false
  if (item.type === 1) {
    const round = getTargetRoundNumber(item)
    return round !== null && round >= 3
  }
  if (item.type === 0) {
    return true
  }
  return false
}

const autoMarkSentenceByRecordIds = async () => {
  if (!authStore.user?.id) return
  const list = checklistStore.checklists || []
  const sentenceItems = list.filter((item) => item.type === 0 && item.recordIds)
  if (!sentenceItems.length) return

  try {
    const doneIds = []
    const undoIds = []
    for (const item of sentenceItems) {
      const recordIds = parseRecordIds(item.recordIds)
      const targetRound = getTargetRoundNumber(item)
      if (!recordIds.length || !targetRound) continue
      try {
        const res = await sentenceService.getRecordsByIds({
          userId: authStore.user.id,
          recordIds
        })
        const records = Array.isArray(res?.data) ? res.data : res?.data?.data || []
        if (!records.length) {
          if (item.alreadyReviewed) undoIds.push(item.id)
          continue
        }
        const finished = records.every((record) => isRoundCompleted(record, targetRound))
        if (finished) {
          doneIds.push(item.id)
        } else if (item.alreadyReviewed) {
          undoIds.push(item.id)
        }
      } catch (error) {
        console.warn('加载句子复习状态失败', error)
      }
    }

    if (doneIds.length) {
      await checklistStore.setReview(doneIds)
    }
    if (undoIds.length) {
      for (const id of undoIds) {
        try {
          const original = checklistStore.checklists.find((c) => c.id === id)
          await checklistStore.updateChecklist({ ...original, alreadyReviewed: 0 })
        } catch (error) {
          console.warn('取消句子复习标记失败', error)
        }
      }
    }
    if (doneIds.length || undoIds.length) {
      await checklistStore.fetchChecklists()
    }
  } catch (error) {
    console.error('句子自动标记失败', error)
  }
}

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
