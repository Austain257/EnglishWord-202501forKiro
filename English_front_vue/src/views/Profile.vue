<template>
  <div class="min-h-screen bg-[#F4F6FB] pb-16">
    <!-- 顶部概要 -->
    <section class="relative bg-gradient-to-br from-indigo-600 via-blue-600 to-blue-500 text-white">
      <div class="absolute inset-0 opacity-30 bg-[url('https://www.transparenttextures.com/patterns/white-diamond.png')]"></div>
      <div class="relative max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10 flex flex-col gap-8">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
          <div class="flex items-center gap-5">
            <div class="w-20 h-20 rounded-2xl bg-white/20 border border-white/30 overflow-hidden flex items-center justify-center text-3xl font-bold">
              <img
                v-if="summary?.avatar"
                :src="formatAvatar(summary.avatar)"
                alt="avatar"
                class="w-full h-full object-cover"
                @error="avatarError = true"
              />
              <span v-else>{{ userInitial }}</span>
            </div>
            <div>
              <p class="text-sm uppercase tracking-[0.4em] text-white/70">Personal Hub</p>
              <h1 class="text-3xl font-bold tracking-tight">{{ summary?.nickname || summary?.username || '未命名' }}</h1>
              <p class="text-white/80 mt-2 flex flex-wrap gap-4 text-sm">
                <span class="inline-flex items-center gap-2">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  已坚持 {{ overview?.streakDays || 0 }} 天
                </span>
                <span class="inline-flex items-center gap-2" v-if="summary?.joinDate">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 4h10m-9 4h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/></svg>
                  加入于 {{ summary.joinDate }}
                </span>
              </p>
            </div>
          </div>
          <div class="flex flex-wrap gap-3">
            <button @click="router.push('/word/learning')" class="btn-primary px-5 py-2 rounded-xl bg-white text-indigo-600 font-semibold shadow-lg hover:-translate-y-0.5 transition-all">
              开始学习
            </button>
            <button @click="router.push('/checklist')" class="px-5 py-2 rounded-xl border border-white/40 text-white/90 hover:bg-white/10">
              今日任务
            </button>
            <button @click="showProfileModal = true" class="px-5 py-2 rounded-xl border border-white/40 text-white hover:bg-white/10">
              编辑信息
            </button>
            <button @click="showPasswordModal = true" class="px-5 py-2 rounded-xl border border-white/40 text-white hover:bg-white/10">
              修改密码
            </button>
          </div>
        </div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-3 sm:gap-4">
          <div v-for="card in overviewCards" :key="card.title" class="bg-white/10 border border-white/20 rounded-2xl p-4 backdrop-blur-lg shadow-lg">
            <p class="text-sm uppercase tracking-[0.3em] text-white/70">{{ card.title }}</p>
            <p class="text-3xl font-bold mt-3">{{ card.value }}</p>
            <p class="text-xs text-white/70 mt-2">{{ card.subtitle }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 主体 -->
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 mt-10 space-y-8">
      <!-- 统计 & 趋势 -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 bg-white rounded-3xl shadow-sm border border-slate-100 p-6">
          <div class="flex items-center justify-between mb-6">
            <div>
              <h2 class="text-lg font-bold text-slate-900">学习趋势</h2>
              <p class="text-sm text-slate-500">最近 7 天学习时长（分钟）</p>
            </div>
            <span class="text-sm font-semibold text-indigo-600">{{ progress?.weeklyPlanProgress || 0 }}% 达成</span>
          </div>
          <div v-if="trendData.length" class="flex items-end gap-3 h-48">
            <div
              v-for="(value, index) in trendData"
              :key="index"
              class="flex-1 flex flex-col items-center gap-2"
            >
              <div class="w-full bg-indigo-100 rounded-xl flex items-end justify-center h-full">
                <div
                  class="w-full bg-gradient-to-t from-indigo-500 to-blue-400 rounded-xl"
                  :style="{ height: `${Math.max(value, 4)}%` }"
                ></div>
              </div>
              <p class="text-xs text-slate-500">{{ trendCategories[index] }}</p>
            </div>
          </div>
          <p v-else class="text-sm text-slate-400 text-center py-10">暂无趋势数据</p>
        </div>
        <div class="bg-white rounded-3xl shadow-sm border border-slate-100 p-6 flex flex-col gap-4">
          <h2 class="text-lg font-bold text-slate-900 flex items-center gap-2">
            每日目标
            <span class="px-2 py-0.5 rounded-full bg-indigo-50 text-indigo-600 text-xs font-semibold">{{ settings.dailyTargetMinutes }} 分钟</span>
          </h2>
          <div>
            <p class="text-4xl font-bold text-slate-900">{{ studyMinutes }}</p>
            <p class="text-sm text-slate-500">今日累计学习时长（分钟）</p>
          </div>
          <div>
            <p class="text-sm font-semibold text-slate-600 mb-2">学习提醒</p>
            <label class="inline-flex items-center gap-3 text-sm font-medium text-slate-600">
              <input type="checkbox" v-model="profileStore.reminderEnabled" class="w-4 h-4 rounded border-slate-300 text-indigo-600 focus:ring-indigo-500">
              每日 {{ settings.dailyTargetMinutes }} 分钟提醒
            </label>
          </div>
          <button @click="handleSaveSettings" class="w-full py-3 rounded-2xl bg-indigo-600 text-white font-semibold shadow-sm hover:bg-indigo-700 transition-colors disabled:opacity-60" :disabled="profileStore.saving">
            {{ profileStore.saving ? '保存中...' : '保存设置' }}
          </button>
        </div>
      </div>

      <!-- Checklist & 快捷操作 -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 bg-white rounded-3xl shadow-sm border border-slate-100 p-6">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h2 class="text-lg font-bold text-slate-900">复习清单</h2>
              <p class="text-sm text-slate-500">来自学习清单的待办任务</p>
            </div>
            <button @click="router.push('/checklist')" class="text-sm font-semibold text-indigo-600 hover:text-indigo-800">查看清单</button>
          </div>
          <div
            v-if="checklist.pending?.length"
            class="space-y-4 max-h-40 overflow-y-auto pr-1 custom-scroll"
          >
            <div
              v-for="item in checklist.pending"
              :key="item.id"
              class="rounded-2xl border border-slate-100 p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3"
            >
              <div>
                <p class="font-semibold text-slate-900">{{ item.title }}</p>
                <p class="text-sm text-slate-500 mt-1">计划日期：{{ item.dueDate || '未设置' }}</p>
              </div>
              <span class="px-3 py-1 rounded-full text-xs font-semibold" :class="item.alreadyReviewed ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'">
                {{ item.alreadyReviewed ? '已复习' : '待复习' }}
              </span>
            </div>
          </div>
          <p v-else class="text-sm text-slate-400">今日暂无待复习任务。</p>
        </div>
        <div class="bg-white rounded-3xl shadow-sm border border-slate-100 p-6">
          <h2 class="text-lg font-bold text-slate-900 mb-4">快捷操作</h2>
          <div class="grid grid-cols-2 gap-3">
            <button
              v-for="action in quickActions"
              :key="action.label"
              class="rounded-2xl border border-slate-100 p-4 flex flex-col gap-2 items-start hover:border-indigo-200 hover:bg-indigo-50/30 transition"
              @click="handleQuickAction(action)"
            >
              <span class="text-xs font-semibold text-slate-500 uppercase tracking-wide">{{ action.label }}</span>
              <span class="text-sm text-indigo-600">前往</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 成就 + 通知 -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 bg-white rounded-3xl shadow-sm border border-slate-100 p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-bold text-slate-900">学习成就</h2>
            <p class="text-sm text-slate-500">点亮更多徽章，记录成长足迹</p>
          </div>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div
              v-for="achievement in achievements"
              :key="achievement.title"
              class="rounded-2xl border border-slate-100 p-4 flex items-center gap-4"
            >
              <div class="w-12 h-12 rounded-2xl bg-gradient-to-br from-indigo-100 to-blue-100 flex items-center justify-center">
                <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
              </div>
              <div class="flex-1">
                <p class="font-semibold text-slate-900">{{ achievement.title }}</p>
                <p class="text-xs text-slate-500">{{ achievement.description }}</p>
                <div class="mt-2 h-2 bg-slate-100 rounded-full overflow-hidden">
                  <div class="h-full bg-indigo-500 rounded-full" :style="{ width: `${achievementProgress(achievement)}%` }"></div>
                </div>
              </div>
              <span class="text-xs font-semibold" :class="achievement.unlocked ? 'text-emerald-600' : 'text-slate-400'">
                {{ achievement.currentValue }}/{{ achievement.targetValue }}
              </span>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-3xl shadow-sm border border-slate-100 p-6">
          <div class="flex items-center justify-between mb-4">
            <h2 class="text-lg font-bold text-slate-900">通知</h2>
            <button class="text-xs font-semibold text-indigo-600" @click="refreshData">刷新</button>
          </div>
          <div class="space-y-4 max-h-20 overflow-y-auto pr-1 custom-scroll">
            <div v-for="notice in notifications" :key="notice.title" class="rounded-2xl border border-slate-100 p-4">
              <p class="text-sm font-semibold text-slate-900">{{ notice.title }}</p>
              <p class="text-sm text-slate-500 mt-1">{{ notice.content }}</p>
              <p class="text-xs text-slate-400 mt-2">{{ formatDateTime(notice.time) }}</p>
            </div>
            <p v-if="!notifications.length" class="text-sm text-slate-400 text-center">暂无通知</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="profileStore.loading" class="fixed inset-0 bg-white/60 backdrop-blur-sm flex flex-col items-center justify-center z-50">
      <div class="w-12 h-12 border-4 border-indigo-100 border-t-indigo-500 rounded-full animate-spin mb-4"></div>
      <p class="text-slate-500 text-sm">正在加载个人中心...</p>
    </div>

    <!-- 编辑信息弹窗 -->
    <div v-if="showProfileModal" class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl shadow-xl w-full max-w-2xl p-6 space-y-5">
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-xl font-bold text-slate-900">编辑个人信息</h3>
            <p class="text-sm text-slate-500 mt-1">更新昵称、头像、学习目标等内容</p>
          </div>
          <button class="text-slate-400 hover:text-slate-600" @click="showProfileModal = false">
            ✕
          </button>
        </div>
        <div class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <label class="flex flex-col gap-2 text-sm text-slate-600">
              用户名
              <input type="text" v-model="profileStore.settings.username" class="form-input" placeholder="用户名" />
            </label>
            <label class="flex flex-col gap-2 text-sm text-slate-600">
              昵称
              <input type="text" v-model="profileStore.settings.nickname" class="form-input" placeholder="昵称" />
            </label>
          </div>
          <label class="flex flex-col gap-2 text-sm text-slate-600">
            邮箱
            <input type="email" v-model="profileStore.settings.email" class="form-input" placeholder="example@email.com" />
          </label>
          <label class="flex flex-col gap-2 text-sm text-slate-600">
            头像
            <div class="flex flex-col gap-3 md:flex-row md:items-center md:gap-4">
              <div class="w-16 h-16 rounded-full border border-slate-200 overflow-hidden bg-slate-50 flex items-center justify-center shrink-0">
                <img
                  v-if="previewAvatarSrc && !previewError"
                  :src="formatAvatar(previewAvatarSrc)"
                  alt="avatar preview"
                  class="w-full h-full object-cover"
                  @error="previewError = true"
                />
                <span v-else class="text-slate-400 text-base">{{ userInitial }}</span>
              </div>
              <input
                type="text"
                v-model="profileStore.settings.avatar"
                class="form-input"
                placeholder="https://..."
                @input="previewError = false"
              />
            </div>
          </label>
          <label class="flex flex-col gap-2 text-sm text-slate-600">
            学习目标
            <textarea v-model="profileStore.settings.learningGoal" class="form-textarea" rows="3" placeholder="想达成的学习目标"></textarea>
          </label>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <label class="flex flex-col gap-2 text-sm text-slate-600">
              每日目标（分钟）
              <input type="number" min="10" step="5" v-model.number="profileStore.settings.dailyTargetMinutes" class="form-input" />
            </label>
            <label class="flex flex-col gap-2 text-sm text-slate-600">
              学习提醒
              <div class="flex items-center gap-3">
                <button
                  type="button"
                  @click="profileStore.reminderEnabled = !profileStore.reminderEnabled"
                  class="w-12 h-6 rounded-full transition-colors flex items-center px-1 shadow-inner"
                  :class="profileStore.reminderEnabled ? 'bg-indigo-500 justify-end' : 'bg-slate-300 justify-start'"
                >
                  <span class="w-5 h-5 rounded-full bg-white shadow transition-transform"></span>
                </button>
                <span class="text-sm text-slate-600">{{ profileStore.reminderEnabled ? '开启' : '关闭' }}</span>
              </div>
            </label>
          </div>
        </div>
        <div class="flex justify-end gap-3">
          <button class="px-5 py-2 rounded-2xl border border-slate-200 text-slate-600 hover:bg-slate-50" @click="showProfileModal = false">
            取消
          </button>
          <button
            class="px-6 py-2 rounded-2xl bg-indigo-600 text-white font-semibold shadow-sm hover:bg-indigo-700 transition-colors disabled:opacity-60"
            :disabled="profileStore.saving"
            @click="handleSaveSettings"
          >
            {{ profileStore.saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <div v-if="showPasswordModal" class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl shadow-xl w-full max-w-lg p-6 space-y-5">
        <div class="flex items-center justify-between">
          <div>
            <h3 class="text-xl font-bold text-slate-900">修改密码</h3>
            <p class="text-sm text-slate-500 mt-1">请输入当前密码与新密码</p>
          </div>
          <button class="text-slate-400 hover:text-slate-600" @click="showPasswordModal = false">✕</button>
        </div>
        <div class="space-y-4">
          <label class="flex flex-col gap-2 text-sm text-slate-600">
            当前密码
            <input type="password" v-model="profileStore.passwordForm.currentPassword" class="form-input" placeholder="请输入当前密码" />
          </label>
          <label class="flex flex-col gap-2 text-sm text-slate-600">
            新密码
            <input type="password" v-model="profileStore.passwordForm.newPassword" class="form-input" placeholder="至少 6 位字符" />
          </label>
          <label class="flex flex-col gap-2 text-sm text-slate-600">
            确认新密码
            <input type="password" v-model="profileStore.passwordForm.confirmPassword" class="form-input" placeholder="再次输入新密码" />
          </label>
        </div>
        <div class="flex justify-end gap-3">
          <button class="px-5 py-2 rounded-2xl border border-slate-200 text-slate-600 hover:bg-slate-50" @click="showPasswordModal = false">
            取消
          </button>
          <button
            class="px-6 py-2 rounded-2xl bg-slate-900 text-white font-semibold shadow-sm hover:bg-slate-800 transition-colors disabled:opacity-60"
            :disabled="profileStore.updatingPassword"
            @click="handleChangePassword"
          >
            {{ profileStore.updatingPassword ? '修改中...' : '更新密码' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useProfileStore } from '@/stores/profile'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const profileStore = useProfileStore()
const authStore = useAuthStore()
const avatarError = ref(false)
const showProfileModal = ref(false)
const showPasswordModal = ref(false)
const previewError = ref(false)

onMounted(async () => {
  await profileStore.loadProfile()
})

const summary = computed(() => profileStore.summary)
const overview = computed(() => profileStore.overview)
const checklist = computed(() => profileStore.checklist || {})
const achievements = computed(() => profileStore.achievements || [])
const quickActions = computed(() => profileStore.quickActions || [])
const notifications = computed(() => profileStore.notifications || [])
const settings = computed(() => profileStore.settings || {})
const trendData = computed(() => profileStore.trendData)
const trendCategories = computed(() => profileStore.trendCategories)
const studyMinutes = computed(() => profileStore.studyMinutes)
const previewAvatarSrc = computed(() => {
  const inputVal = profileStore.settings?.avatar?.trim()
  if (inputVal) {
    return inputVal
  }
  return summary.value?.avatar || ''
})

const userInitial = computed(() => {
  const name = summary.value?.nickname || summary.value?.username || authStore.user?.nickname || authStore.user?.username || 'U'
  return name.charAt(0).toUpperCase()
})

const overviewCards = computed(() => [
  { title: '今日学习', value: formatMinutes(overview.value?.todayStudySeconds || 0), subtitle: '分钟' },
  { title: '累计学习', value: formatHours(overview.value?.totalStudySeconds || 0), subtitle: '小时' },
  { title: '错词本', value: overview.value?.errorWordCount || 0, subtitle: '待复习单词' },
  { title: '错句本', value: overview.value?.errorSentenceCount || 0, subtitle: '待复习句子' }
])

const formatMinutes = (seconds) => Math.round(seconds / 60)
const formatHours = (seconds) => Math.round(seconds / 3600)

const formatDateTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const achievementProgress = (achievement) => {
  if (!achievement?.targetValue) return 0
  return Math.min(100, Math.round((achievement.currentValue / achievement.targetValue) * 100))
}

const handleQuickAction = (action) => {
  if (!action?.route) return
  router.push(action.route)
}

const handleSaveSettings = async () => {
  const success = await profileStore.saveSettings()
  if (success !== false) {
    showProfileModal.value = false
    previewError.value = false
  }
}

const handleChangePassword = async () => {
  const success = await profileStore.changePassword()
  if (success !== false) {
    showPasswordModal.value = false
  }
}

watch(showProfileModal, (val) => {
  if (val) {
    previewError.value = false
  }
})

const refreshData = async () => {
  await profileStore.loadProfile()
}

const formatAvatar = (avatar) => {
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return `${import.meta.env.VITE_API_BASE_URL || 'http://192.168.43.106:8080'}${avatar}`
}
</script>

<style scoped>
.btn-primary {
  @apply inline-flex items-center justify-center;
}

.custom-scroll {
  scrollbar-width: thin;
  scrollbar-color: #c7d2fe transparent;
}
.custom-scroll::-webkit-scrollbar {
  width: 6px;
}
.custom-scroll::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scroll::-webkit-scrollbar-thumb {
  background-color: #c7d2fe;
  border-radius: 9999px;
}

.form-input {
  @apply w-full rounded-2xl border border-slate-200 px-4 py-2.5 focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition;
}

.form-textarea {
  @apply w-full rounded-2xl border border-slate-200 px-4 py-2.5 focus:ring-2 focus:ring-indigo-200 focus:border-indigo-500 transition;
}
</style>
