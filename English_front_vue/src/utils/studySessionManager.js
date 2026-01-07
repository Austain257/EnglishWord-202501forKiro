import { useStudyTrackerStore } from '@/stores/studyTracker'
import { useAuthStore } from '@/stores/auth'

class StudySessionManager {
  constructor() {
    this.studyTracker = null
    this.authStore = null
    this.initialized = false
  }

  init() {
    if (this.initialized) return
    
    this.studyTracker = useStudyTrackerStore()
    this.authStore = useAuthStore()
    
    this.setupEventListeners()
    this.initialized = true
    
    console.log('学习会话管理器已初始化')
  }

  setupEventListeners() {
    // 页面可见性变化
    document.addEventListener('visibilitychange', this.handleVisibilityChange.bind(this))
    
    // 页面即将卸载
    window.addEventListener('beforeunload', this.handleBeforeUnload.bind(this))
    
    // 网络状态变化
    window.addEventListener('online', this.handleOnline.bind(this))
    window.addEventListener('offline', this.handleOffline.bind(this))
    
    // 监听auth store的logout事件
    this.watchAuthLogout()
  }

  handleVisibilityChange() {
    if (this.studyTracker) {
      this.studyTracker.handleVisibilityChange()
    }
  }

  handleBeforeUnload(event) {
    if (this.studyTracker && this.studyTracker.sessionId) {
      // 处理意外退出
      this.studyTracker.handleUnexpectedExit()
      
      // 某些浏览器可能显示确认对话框
      const message = '您有正在进行的学习会话，确定要离开吗？'
      event.returnValue = message
      return message
    }
  }

  handleOnline() {
    if (this.studyTracker) {
      this.studyTracker.handleOnline()
    }
  }

  handleOffline() {
    if (this.studyTracker) {
      this.studyTracker.handleOffline()
    }
  }

  watchAuthLogout() {
    // 监听authStore的状态变化，如果用户登出则结束学习会话
    if (this.authStore) {
      // 这里可以使用watch或其他方式监听authStore的变化
      const originalLogout = this.authStore.logout
      this.authStore.logout = (...args) => {
        // 在logout前结束学习会话
        if (this.studyTracker && this.studyTracker.sessionId) {
          this.studyTracker.handleUnexpectedExit()
        }
        return originalLogout.apply(this.authStore, args)
      }
    }
  }

  destroy() {
    if (!this.initialized) return
    
    document.removeEventListener('visibilitychange', this.handleVisibilityChange.bind(this))
    window.removeEventListener('beforeunload', this.handleBeforeUnload.bind(this))
    window.removeEventListener('online', this.handleOnline.bind(this))
    window.removeEventListener('offline', this.handleOffline.bind(this))
    
    this.initialized = false
    
    console.log('学习会话管理器已销毁')
  }
}

// 导出单例实例
export const studySessionManager = new StudySessionManager()
