import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import { useStudyTrackerStore } from '@/stores/studyTracker'
import { studyLockGuard } from './guards'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false, title: '注册' }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true, title: '首页' }
  },
  {
    path: '/books',
    name: 'BookList',
    component: () => import('@/views/BookList.vue'),
    meta: { requiresAuth: true, title: '课本选择' }
  },
  {
    path: '/word/learning',
    name: 'WordLearning',
    component: () => import('@/views/WordLearning.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '单词学习', studyScene: 'word_learning' }
  },
  {
    path: '/word/review',
    name: 'WordReviewSelection',
    component: () => import('@/views/WordReviewSelection.vue'),
    meta: { requiresAuth: true, requiresBook: false, title: '单词复习选择' }
  },
  {
    path: '/word/review/first',
    name: 'WordReview',
    component: () => import('@/views/WordReview.vue'),
    meta: { requiresAuth: true, requiresBook: false, title: '第一轮复习', studyScene: 'word_review_first' }
  },
  {
    path: '/word/review/other',
    name: 'WordReviewOther',
    component: () => import('@/views/WordReviewOther.vue'),
    meta: { requiresAuth: true, requiresBook: false, title: '其他轮次复习', studyScene: 'word_review_other' }
  },
  {
    path: '/word/dictation',
    name: 'WordDictation',
    component: () => import('@/views/WordDictation.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '单词听写', studyScene: 'word_dictation' }
  },
  {
    path: '/word/error-book',
    name: 'ErrorWordBook',
    component: () => import('@/views/ErrorWordBook.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '错词本', studyScene: 'word_error_book' }
  },
  {
    path: '/sentence/learning',
    name: 'SentenceLearning',
    component: () => import('@/views/SentenceLearning.vue'),
    meta: { requiresAuth: true, title: '句子学习', studyScene: 'sentence_learning' }
  },
  {
    path: '/sentence/dictation',
    name: 'SentenceDictation',
    component: () => import('@/views/SentenceDictation.vue'),
    meta: { requiresAuth: true, title: '句子听写', studyScene: 'sentence_dictation' }
  },
  {
    path: '/sentence/error-book',
    name: 'ErrorSentenceBook',
    component: () => import('@/views/ErrorSentenceBook.vue'),
    meta: { requiresAuth: true, title: '错句本', studyScene: 'sentence_error_book' }
  },
  {
    path: '/checklist',
    name: 'LearningChecklist',
    component: () => import('@/views/LearningChecklist.vue'),
    meta: { requiresAuth: true, title: '学习清单' }
  },
  {
    path: '/jottings',
    name: 'Jottings',
    component: () => import('@/views/Jottings.vue'),
    meta: { requiresAuth: true, requiresBook: false, title: '积累本', studyScene: 'jottings' }
  },
  {
    path: '/word/game',
    name: 'WordGame',
    component: () => import('@/views/WordGame.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '单词泡泡龙', studyScene: 'word_game' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true, title: '个人中心' }
  },
  {
    path: '/word/option',
    name: 'WordOption',
    component: () => import('@/views/WordOption.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '单词速记挑战', studyScene: 'word_option' }
  },
  {
    path: '/system-test',
    name: 'SystemTest',
    component: () => import('@/views/SystemTest.vue'),
    meta: { requiresAuth: false, title: '系统测试' }
  },
  {
    path: '/motivation-test',
    name: 'MotivationTest',
    component: () => import('@/views/MotivationTest.vue'),
    meta: { requiresAuth: false, title: '激励文案测试' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { requiresAuth: false, title: '页面未找到' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  }
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  const bookStore = useBookStore()

  document.title = to.meta.title ? `${to.meta.title} - 轻风智语` : '轻风智语'

  // 1. 认证检查
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  // 2. 课本检查
  if (to.meta.requiresBook) {
    if (!authStore.currentBookId) {
      next('/books')
      return
    }

    if (!bookStore.currentBook) {
      try {
        if (bookStore.books.length === 0) {
          await bookStore.fetchBooks()
        } else {
          bookStore.initializeBook()
        }
      } catch (error) {
        console.error('初始化课本失败:', error)
        next('/books')
        return
      }
    }

    if (!bookStore.currentBook) {
      next('/books')
      return
    }
  }

  // 3. 登录页面检查
  if ((to.name === 'Login' || to.name === 'Register') && authStore.isAuthenticated) {
    next('/')
    return
  }

  // 4. 学习锁定检查（在认证和课本检查通过后）
  if (authStore.isAuthenticated) {
    await studyLockGuard(to, from, next)
  } else {
    next()
  }
})

// 路由后钩子：处理学习会话管理
router.afterEach((to, from) => {
  const studyTracker = useStudyTrackerStore()
  
  // 如果从学习场景离开且目标页面不是学习场景，或者学习场景发生变化，则结束当前会话
  if (from.meta?.studyScene && from.meta.studyScene !== to.meta?.studyScene) {
    studyTracker.finish('leaveRoute')
  }
  
  // 如果进入学习场景，则开始新会话
  if (to.meta?.studyScene) {
    studyTracker.start(to.meta.studyScene)
  }
})

export default router
