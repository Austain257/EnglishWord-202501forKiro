import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { profileService } from '@/services/profile.service'
import { useToast } from '@/composables/useToast'

const createDefaultSettings = () => ({
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  learningGoal: '',
  dailyTargetMinutes: 60,
  reminderEnabled: true
})

export const useProfileStore = defineStore('profile', () => {
  const toast = useToast()

  const loading = ref(false)
  const saving = ref(false)
  const updatingPassword = ref(false)
  const summary = ref(null)
  const overview = ref(null)
  const progress = ref(null)
  const checklist = ref({ pending: [], todayTasks: [] })
  const device = ref(null)
  const achievements = ref([])
  const settings = ref(createDefaultSettings())
  const quickActions = ref([])
  const notifications = ref([])

  const studyMinutes = computed(() => {
    if (!overview.value) return 0
    return Math.round((overview.value.todayStudySeconds || 0) / 60)
  })

  const buildTrendPoints = () => {
    const points = progress.value?.trends
    if (points?.length) {
      return points
    }
    const fallback = []
    const today = new Date()
    for (let i = 6; i >= 0; i -= 1) {
      const date = new Date(today)
      date.setDate(today.getDate() - i)
      const month = `${date.getMonth() + 1}`.padStart(2, '0')
      const day = `${date.getDate()}`.padStart(2, '0')
      fallback.push({
        date: `${month}-${day}`,
        seconds: 0
      })
    }
    return fallback
  }

  const trendCategories = computed(() => {
    return buildTrendPoints().map((item) => item.date)
  })

  const trendData = computed(() => {
    return buildTrendPoints().map((item) => Math.round(item.seconds / 60))
  })

  const reminderEnabled = computed({
    get: () => !!settings.value?.reminderEnabled,
    set: (val) => {
      settings.value.reminderEnabled = val
    }
  })

  const passwordForm = ref({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })

  const applyProfileData = (data) => {
    summary.value = data.summary
    overview.value = data.overview
    progress.value = data.progress
    checklist.value = data.checklist
    device.value = data.device
    achievements.value = data.achievements || []
    quickActions.value = data.quickActions || []
    notifications.value = data.notifications || []
    settings.value = {
      ...createDefaultSettings(),
      username: data.settings?.username ?? data.summary?.username ?? '',
      nickname: data.settings?.nickname ?? data.summary?.nickname ?? '',
      email: data.settings?.email ?? data.summary?.email ?? '',
      avatar: data.settings?.avatar ?? data.summary?.avatar ?? '',
      learningGoal: data.settings?.learningGoal ?? '',
      dailyTargetMinutes: data.settings?.dailyTargetMinutes ?? 60,
      reminderEnabled: data.settings?.reminderEnabled ?? true
    }
  }

  const resetState = () => {
    summary.value = null
    overview.value = null
    progress.value = null
    checklist.value = { pending: [], todayTasks: [] }
    device.value = null
    achievements.value = []
    settings.value = createDefaultSettings()
    quickActions.value = []
    notifications.value = []
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  }

  const loadProfile = async () => {
    loading.value = true
    try {
      const { data } = await profileService.getProfile()
      applyProfileData(data)
    } catch (error) {
      console.error('加载个人中心数据失败', error)
      resetState()
      toast.error(error.message || '加载个人数据失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  const saveSettings = async () => {
    saving.value = true
    try {
      const payload = {
        ...settings.value,
        username: settings.value.username?.trim(),
        nickname: settings.value.nickname?.trim(),
        email: settings.value.email?.trim(),
        avatar: settings.value.avatar?.trim(),
        learningGoal: settings.value.learningGoal?.trim(),
        dailyTargetMinutes: Math.max(10, Number(settings.value.dailyTargetMinutes) || 60)
      }
      if (!payload.username) {
        throw new Error('用户名不能为空')
      }
      await profileService.updateSettings(payload)
      toast.success('设置已更新')
      await loadProfile()
    } catch (error) {
      console.error('更新设置失败', error)
      toast.error(error.message || '更新设置失败')
      return false
    } finally {
      saving.value = false
    }
  }

  const changePassword = async () => {
    if (updatingPassword.value) return false
    const form = passwordForm.value
    if (!form.currentPassword || !form.newPassword || !form.confirmPassword) {
      toast.error('请完整填写所有密码字段')
      return false
    }
    if (form.newPassword.length < 6) {
      toast.error('新密码至少 6 位')
      return false
    }
    if (form.newPassword !== form.confirmPassword) {
      toast.error('两次输入的新密码不一致')
      return false
    }
    updatingPassword.value = true
    try {
      await profileService.updatePassword(form)
      toast.success('密码修改成功')
      passwordForm.value = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      return true
    } catch (error) {
      console.error('修改密码失败', error)
      toast.error(error.message || '修改密码失败')
      return false
    } finally {
      updatingPassword.value = false
    }
  }

  return {
    // state
    loading,
    saving,
    updatingPassword,
    summary,
    overview,
    progress,
    checklist,
    device,
    achievements,
    settings,
    quickActions,
    notifications,
    passwordForm,

    // getters
    studyMinutes,
    trendCategories,
    trendData,
    reminderEnabled,

    // actions
    loadProfile,
    saveSettings,
    changePassword
  }
})
