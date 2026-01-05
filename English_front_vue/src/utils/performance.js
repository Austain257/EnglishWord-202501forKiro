/**
 * 性能监控工具
 */

// 性能指标收集器
class PerformanceMonitor {
  constructor() {
    this.metrics = new Map()
    this.observers = new Map()
    this.init()
  }

  init() {
    // 监听页面加载性能
    if (typeof window !== 'undefined' && 'performance' in window) {
      this.observePageLoad()
      this.observeLCP()
      this.observeFID()
      this.observeCLS()
    }
  }

  // 监听页面加载性能
  observePageLoad() {
    window.addEventListener('load', () => {
      const navigation = performance.getEntriesByType('navigation')[0]
      if (navigation) {
        this.recordMetric('pageLoad', {
          domContentLoaded: navigation.domContentLoadedEventEnd - navigation.domContentLoadedEventStart,
          loadComplete: navigation.loadEventEnd - navigation.loadEventStart,
          totalTime: navigation.loadEventEnd - navigation.fetchStart
        })
      }
    })
  }

  // 监听最大内容绘制 (LCP)
  observeLCP() {
    if ('PerformanceObserver' in window) {
      const observer = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        const lastEntry = entries[entries.length - 1]
        this.recordMetric('LCP', lastEntry.startTime)
      })
      
      observer.observe({ entryTypes: ['largest-contentful-paint'] })
      this.observers.set('LCP', observer)
    }
  }

  // 监听首次输入延迟 (FID)
  observeFID() {
    if ('PerformanceObserver' in window) {
      const observer = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach(entry => {
          this.recordMetric('FID', entry.processingStart - entry.startTime)
        })
      })
      
      observer.observe({ entryTypes: ['first-input'] })
      this.observers.set('FID', observer)
    }
  }

  // 监听累积布局偏移 (CLS)
  observeCLS() {
    if ('PerformanceObserver' in window) {
      let clsValue = 0
      const observer = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach(entry => {
          if (!entry.hadRecentInput) {
            clsValue += entry.value
            this.recordMetric('CLS', clsValue)
          }
        })
      })
      
      observer.observe({ entryTypes: ['layout-shift'] })
      this.observers.set('CLS', observer)
    }
  }

  // 记录自定义指标
  recordMetric(name, value) {
    const timestamp = Date.now()
    if (!this.metrics.has(name)) {
      this.metrics.set(name, [])
    }
    this.metrics.get(name).push({ value, timestamp })
    
    // 在开发环境下输出性能指标
    if (process.env.NODE_ENV === 'development') {
      console.log(`[Performance] ${name}:`, value)
    }
  }

  // 获取指标
  getMetric(name) {
    return this.metrics.get(name) || []
  }

  // 获取所有指标
  getAllMetrics() {
    const result = {}
    this.metrics.forEach((value, key) => {
      result[key] = value
    })
    return result
  }

  // 清理观察器
  cleanup() {
    this.observers.forEach(observer => observer.disconnect())
    this.observers.clear()
  }
}

// 创建全局实例
export const performanceMonitor = new PerformanceMonitor()

// 性能测量装饰器
export function measurePerformance(name) {
  return function(target, propertyKey, descriptor) {
    const originalMethod = descriptor.value
    
    descriptor.value = async function(...args) {
      const startTime = performance.now()
      
      try {
        const result = await originalMethod.apply(this, args)
        const endTime = performance.now()
        performanceMonitor.recordMetric(`${name}_duration`, endTime - startTime)
        return result
      } catch (error) {
        const endTime = performance.now()
        performanceMonitor.recordMetric(`${name}_error_duration`, endTime - startTime)
        throw error
      }
    }
    
    return descriptor
  }
}

// 组件性能监控 Hook
export function usePerformanceMonitor(componentName) {
  const startTime = performance.now()
  
  const recordRender = () => {
    const endTime = performance.now()
    performanceMonitor.recordMetric(`${componentName}_render`, endTime - startTime)
  }
  
  const recordMount = () => {
    const endTime = performance.now()
    performanceMonitor.recordMetric(`${componentName}_mount`, endTime - startTime)
  }
  
  return {
    recordRender,
    recordMount,
    recordMetric: (name, value) => performanceMonitor.recordMetric(`${componentName}_${name}`, value)
  }
}

// 资源加载监控
export function monitorResourceLoading() {
  if ('PerformanceObserver' in window) {
    const observer = new PerformanceObserver((list) => {
      const entries = list.getEntries()
      entries.forEach(entry => {
        if (entry.initiatorType === 'img') {
          performanceMonitor.recordMetric('image_load_time', entry.duration)
        } else if (entry.initiatorType === 'script') {
          performanceMonitor.recordMetric('script_load_time', entry.duration)
        } else if (entry.initiatorType === 'css') {
          performanceMonitor.recordMetric('css_load_time', entry.duration)
        }
      })
    })
    
    observer.observe({ entryTypes: ['resource'] })
    return observer
  }
}

// 内存使用监控
export function getMemoryUsage() {
  if ('memory' in performance) {
    return {
      used: performance.memory.usedJSHeapSize,
      total: performance.memory.totalJSHeapSize,
      limit: performance.memory.jsHeapSizeLimit
    }
  }
  return null
}

// 网络状态监控
export function getNetworkInfo() {
  if ('connection' in navigator) {
    const connection = navigator.connection
    return {
      effectiveType: connection.effectiveType,
      downlink: connection.downlink,
      rtt: connection.rtt,
      saveData: connection.saveData
    }
  }
  return null
}

// 性能报告生成
export function generatePerformanceReport() {
  const metrics = performanceMonitor.getAllMetrics()
  const memory = getMemoryUsage()
  const network = getNetworkInfo()
  
  return {
    timestamp: new Date().toISOString(),
    metrics,
    memory,
    network,
    userAgent: navigator.userAgent,
    url: window.location.href
  }
}