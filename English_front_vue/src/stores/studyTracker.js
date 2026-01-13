import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { studyService } from '@/services/study.service'
import { useAuthStore } from './auth'
import { useStudyStatStore } from './studyStat'
import { useToast } from '@/composables/useToast'

export const useStudyTrackerStore = defineStore('studyTracker', () => {
  const toast = useToast()
  
  // 状态
  const sessionId = ref(null)
  const studyScene = ref(null)
  const startTime = ref(null)
  const elapsedMs = ref(0)
  const timerId = ref(null)
  const heartbeatId = ref(null)
  const paused = ref(false)
  const lastVisibility = ref(document.visibilityState)
  
  // 计算属性
  const isActive = computed(() => !!sessionId.value && !paused.value)
  const formattedElapsed = computed(() => {
    const totalSeconds = Math.floor(elapsedMs.value / 1000)
    const hours = Math.floor(totalSeconds / 3600)
    const minutes = Math.floor((totalSeconds % 3600) / 60)
    const seconds = totalSeconds % 60
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  })
  
  // 动作
  const start = async (scene) => {
    try {
      const authStore = useAuthStore()
      
      if (!authStore.currentBookId) {
        console.error('没有选择课本，无法开始学习会话')
        return
      }
      
      // 如果已有会话，先结束
      if (sessionId.value) {
        await finish('switchScene')
      }
      
      // 开始新会话
      const result = await studyService.startSession(scene, authStore.currentBookId)
      
      sessionId.value = result.sessionId
      studyScene.value = scene
      startTime.value = new Date(result.serverTime || Date.now())
      elapsedMs.value = 0
      paused.value = false
      
      // 启动计时器
      timerId.value = setInterval(() => {
        if (!paused.value) {
          elapsedMs.value += 1000
        }
      }, 1000)
      
      // 启动心跳
      startHeartbeat()
      
      console.log(`学习会话已开始: ${scene}, sessionId: ${sessionId.value}`)
    } catch (error) {
      console.error('开始学习会话失败:', error)
      toast.error('开始学习会话失败')
    }
  }
  
  const pause = (reason = 'manual') => {
    if (timerId.value) {
      paused.value = true
      console.log(`学习会话已暂停: ${reason}`)
    }
  }
  
  const resume = () => {
    if (sessionId.value && paused.value) {
      paused.value = false
      // 发送一次心跳确认恢复
      if (sessionId.value) {
        sendHeartbeat()
      }
      console.log('学习会话已恢复')
    }
  }
  
  const finish = async (source = 'normal') => {
    if (!sessionId.value) {
      return
    }
    
    try {
      // 清理定时器
      if (timerId.value) {
        clearInterval(timerId.value)
        timerId.value = null
      }
      
      stopHeartbeat()
      
      // 计算客户端时长
      const clientDurationSec = Math.round(elapsedMs.value / 1000)
      
      // 调用结束接口
      const result = await studyService.finishSession(sessionId.value, clientDurationSec, source)
      
      // 更新统计数据
      const studyStatStore = useStudyStatStore()
      studyStatStore.updateStats(result)
      
      console.log(`学习会话已结束: ${source}, 时长: ${clientDurationSec}秒`)
      
      // 重置状态
      reset()
      
      return result
    } catch (error) {
      console.error('结束学习会话失败:', error)
      toast.error('结束学习会话失败')
      // 即使失败也要重置状态，避免残留
      reset()
    }
  }
  
  const sendFinishKeepAlive = (finishData) => {
    const authStore = useAuthStore()
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://192.168.0.106:8080'
    const url = `${baseUrl}/api/study/session/finish?userId=${authStore.user?.id}`
    let success = false

    try {
      if (navigator.sendBeacon) {
        const blob = new Blob([JSON.stringify(finishData)], { type: 'application/json' })
        success = navigator.sendBeacon(url, blob)
      }

      if (!success && typeof fetch === 'function') {
        fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(finishData),
          keepalive: true
        }).catch((error) => {
          console.error('fetch keepalive 发送结束请求失败:', error)
        })
        success = true
      }

      if (!success) {
        const xhr = new XMLHttpRequest()
        xhr.open('POST', url, false)
        xhr.setRequestHeader('Content-Type', 'application/json')
        xhr.send(JSON.stringify(finishData))
        success = xhr.status >= 200 && xhr.status < 300
      }
    } catch (error) {
      console.error('发送结束请求失败:', error)
    }

    return success
  }

  const handleUnexpectedExit = () => {
    if (!sessionId.value) {
      return
    }
    
    const clientDurationSec = Math.round(elapsedMs.value / 1000)
    const finishData = {
      sessionId: sessionId.value,
      clientDurationSec,
      source: 'unexpectedExit'
    }
    
    sendFinishKeepAlive(finishData)
    
    reset()
  }
  
  const startHeartbeat = () => {
    // 每60秒发送一次心跳
    heartbeatId.value = setInterval(() => {
      if (sessionId.value && !paused.value) {
        sendHeartbeat()
      }
    }, 60000)
  }
  
  const stopHeartbeat = () => {
    if (heartbeatId.value) {
      clearInterval(heartbeatId.value)
      heartbeatId.value = null
    }
  }
  
  const sendHeartbeat = async () => {
    if (!sessionId.value) {
      return
    }
    
    try {
      await studyService.heartbeat(sessionId.value)
    } catch (error) {
      console.error('发送心跳失败:', error)
    }
  }
  
  const reset = () => {
    sessionId.value = null
    studyScene.value = null
    startTime.value = null
    elapsedMs.value = 0
    paused.value = false
    
    if (timerId.value) {
      clearInterval(timerId.value)
      timerId.value = null
    }
    
    stopHeartbeat()
  }
  
  // 页面可见性变化处理
  const handleVisibilityChange = async () => {
    const currentVisibility = document.visibilityState
    
    if (currentVisibility === 'hidden' && lastVisibility.value === 'visible') {
      // 页面隐藏，暂停前端计时并通知后端暂停
      pause('visibility')
      if (sessionId.value) {
        try {
          await studyService.pauseSession(sessionId.value)
          console.log('已通知后端暂停会话')
        } catch (error) {
          console.error('通知后端暂停会话失败:', error)
        }
      }
    } else if (currentVisibility === 'visible' && lastVisibility.value === 'hidden') {
      // 页面恢复，延迟3秒后恢复计时并通知后端恢复（避免频繁切换）
      setTimeout(async () => {
        if (document.visibilityState === 'visible' && sessionId.value) {
          try {
            await studyService.resumeSession(sessionId.value)
            resume()
            console.log('已通知后端恢复会话')
          } catch (error) {
            console.error('通知后端恢复会话失败:', error)
            resume() // 即使后端调用失败，也恢复前端计时
          }
        }
      }, 3000)
    }
    
    lastVisibility.value = currentVisibility
  }
  
  // 网络状态变化处理
  const handleOnline = async () => {
    if (sessionId.value && paused.value) {
      try {
        await studyService.resumeSession(sessionId.value)
      } catch (error) {
        console.error('网络恢复后通知后端失败:', error)
      } finally {
        resume()
      }
    }
  }

  const handleOffline = () => {
    pause('offline')
    if (sessionId.value) {
      studyService.pauseSession(sessionId.value).catch((error) => {
        console.error('网络断开后通知后端暂停失败:', error)
      })
    }
    
    // 如果60秒内未恢复网络，自动结束会话
    setTimeout(() => {
      if (!navigator.onLine && sessionId.value) {
        finish('networkTimeout')
      }
    }, 60000)
  }
  
  return {
    // 状态
    sessionId,
    studyScene,
    startTime,
    elapsedMs,
    paused,
    
    // 计算属性
    isActive,
    formattedElapsed,
    
    // 动作
    start,
    pause,
    resume,
    finish,
    handleUnexpectedExit,
    handleVisibilityChange,
    handleOnline,
    handleOffline,
    reset
  }
})
