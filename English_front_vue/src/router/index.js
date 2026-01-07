import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

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
    meta: { requiresAuth: true, requiresBook: true, title: '单词学习' }
  },
  {
    path: '/word/review',
    name: 'WordReview',
    component: () => import('@/views/WordReview.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '单词复习' }
  },
  {
    path: '/word/dictation',
    name: 'WordDictation',
    component: () => import('@/views/WordDictation.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '单词听写' }
  },
  {
    path: '/word/error-book',
    name: 'ErrorWordBook',
    component: () => import('@/views/ErrorWordBook.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '错词本' }
  },
  {
    path: '/sentence/learning',
    name: 'SentenceLearning',
    component: () => import('@/views/SentenceLearning.vue'),
    meta: { requiresAuth: true, title: '句子学习' }
  },
  {
    path: '/sentence/dictation',
    name: 'SentenceDictation',
    component: () => import('@/views/SentenceDictation.vue'),
    meta: { requiresAuth: true, title: '句子听写' }
  },
  {
    path: '/sentence/error-book',
    name: 'ErrorSentenceBook',
    component: () => import('@/views/ErrorSentenceBook.vue'),
    meta: { requiresAuth: true, title: '错句本' }
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
    meta: { requiresAuth: true, title: '积累本' }
  },
  {
    path: '/word/game',
    name: 'WordGame',
    component: () => import('@/views/WordGame.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '单词泡泡龙' }
  },
  {
    path: '/word/option',
    name: 'WordOption',
    component: () => import('@/views/WordOption.vue'),
    meta: { requiresAuth: true, requiresBook: true, title: '单词速记挑战' }
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

  document.title = to.meta.title ? `${to.meta.title} - 英语学习平台` : '英语学习平台'

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }

  if (to.meta.requiresBook && !authStore.currentBookId) {
    next('/books')
    return
  }

  if ((to.name === 'Login' || to.name === 'Register') && authStore.isAuthenticated) {
    next('/')
    return
  }

  next()
})

export default router
