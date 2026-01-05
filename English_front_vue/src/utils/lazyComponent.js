import { defineAsyncComponent, h } from 'vue'
import Loading from '@/components/common/Loading.vue'

/**
 * 创建懒加载组件
 * @param {Function} loader - 组件加载函数
 * @param {Object} options - 配置选项
 * @returns {Object} Vue异步组件
 */
export function createLazyComponent(loader, options = {}) {
  const {
    loadingComponent = Loading,
    errorComponent = null,
    delay = 200,
    timeout = 30000,
    suspensible = false,
    onError = null
  } = options

  return defineAsyncComponent({
    loader,
    loadingComponent,
    errorComponent,
    delay,
    timeout,
    suspensible,
    onError: (error, retry, fail, attempts) => {
      console.warn(`组件加载失败 (尝试 ${attempts} 次):`, error)
      
      if (onError) {
        onError(error, retry, fail, attempts)
      } else {
        // 默认重试逻辑：最多重试3次
        if (attempts <= 3) {
          retry()
        } else {
          fail()
        }
      }
    }
  })
}

/**
 * 预加载组件
 * @param {Function} loader - 组件加载函数
 * @returns {Promise} 预加载Promise
 */
export function preloadComponent(loader) {
  return loader().catch(error => {
    console.warn('组件预加载失败:', error)
    return null
  })
}

/**
 * 批量预加载组件
 * @param {Array} loaders - 组件加载函数数组
 * @returns {Promise} 批量预加载Promise
 */
export function preloadComponents(loaders) {
  return Promise.allSettled(
    loaders.map(loader => preloadComponent(loader))
  )
}

/**
 * 路由级别的懒加载组件
 * @param {Function} loader - 组件加载函数
 * @returns {Object} Vue异步组件
 */
export function createLazyRoute(loader) {
  return createLazyComponent(loader, {
    delay: 100,
    timeout: 10000,
    loadingComponent: {
      render() {
        return h('div', { class: 'min-h-screen bg-gray-50 flex items-center justify-center' }, [
          h('div', { class: 'text-center' }, [
            h('div', { class: 'animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4' }),
            h('p', { class: 'text-gray-600' }, '页面加载中...')
          ])
        ])
      }
    },
    errorComponent: {
      render() {
        return h('div', { class: 'min-h-screen bg-gray-50 flex items-center justify-center' }, [
          h('div', { class: 'text-center' }, [
            h('div', { class: 'w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4' }, [
              h('svg', { 
                class: 'w-8 h-8 text-red-600', 
                fill: 'none', 
                stroke: 'currentColor', 
                viewBox: '0 0 24 24' 
              }, [
                h('path', { 
                  'stroke-linecap': 'round', 
                  'stroke-linejoin': 'round', 
                  'stroke-width': '2', 
                  d: 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z' 
                })
              ])
            ]),
            h('h3', { class: 'text-lg font-medium text-gray-900 mb-2' }, '页面加载失败'),
            h('p', { class: 'text-gray-600 mb-4' }, '请检查网络连接后重试'),
            h('button', { 
              class: 'px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors',
              onClick: () => window.location.reload()
            }, '重新加载')
          ])
        ])
      }
    }
  })
}