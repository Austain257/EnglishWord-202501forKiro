import { useWordStudyStore } from '@/stores/wordStudy'
import { useAuthStore } from '@/stores/auth'

// 路由守卫 - 学习锁定检查
/**
 * 学习功能锁定检查守卫
 * 在用户访问新词学习、游戏等功能时检查是否有未完成的复习任务
 */
export const studyLockGuard = async (to, from, next) => {
  const wordStudyStore = useWordStudyStore()
  const authStore = useAuthStore()

  // 如果用户未登录，直接放行（登录守卫会处理）
  if (!authStore.user?.id) {
    next()
    return
  }

  // 定义需要检查锁定的路由
  const lockedRoutes = [
    '/word/learning',
    '/word/game',
    '/settings',
    '/statistics'
  ]

  // 定义允许的路由（即使在锁定状态下也可以访问）
  const allowedRoutes = [
    '/word/review',
    '/jottings',
    '/home',
    '/profile',
    '/about'
  ]

  const secondReviewRoutes = ['/word/option', '/word/game']

  // 检查当前路由是否需要锁定检查
  const needsLockCheck = lockedRoutes.some(route => to.path.startsWith(route))
  
  if (!needsLockCheck) {
    next()
    return
  }

  try {
    // 检查锁定状态
    const lockStatus = await wordStudyStore.checkLockStatus(authStore.user.id)
    const pendingReviews = lockStatus.pendingReviews || []
    const hasPendingRound1 = pendingReviews.some((review) => review.pendingRound === 1)
    const isSecondReviewRoute = secondReviewRoutes.some((route) => to.path.startsWith(route))
    
    if (lockStatus.isLocked) {
      // 功能被锁定，阻止访问并显示提示
      console.log('功能被锁定，需要完成复习任务')

      // 第一轮复习已完成，仅剩第二轮时允许访问第二轮复习路线
      if (isSecondReviewRoute && !hasPendingRound1) {
        next()
        return
      }
      
      // 如果当前不在允许的路由，则跳转到复习页面
      if (!allowedRoutes.some(route => to.path.startsWith(route))) {
        next('/word/review')
        return
      }
    }
    
    // 功能未锁定或当前路由允许访问，正常通过
    next()
  } catch (error) {
    console.error('检查锁定状态失败:', error)
    // 检查失败时仍然放行，避免阻止正常使用
    next()
  }
}

/**
 * 全局路由守卫配置
 */
export const setupGuards = (router) => {
  // 在路由跳转前检查学习锁定状态
  router.beforeEach(studyLockGuard)
}
