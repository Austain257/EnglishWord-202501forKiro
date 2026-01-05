# 设计文档 - 英语学习平台

## 概述

本文档描述了英语学习平台的技术设计方案。该平台采用前后端分离架构，前端使用 Vue 3 + Pinia + Vue Router + Axios + Tailwind CSS 技术栈，后端基于 Spring Boot 提供 RESTful API。平台专注于提供系统化的英语学习体验，包括单词学习、句子学习、智能复习和知识积累等核心功能。

**后端API状态**: 后端已完全实现，包括认证系统、单词学习、句子学习、学习清单、知识积累和激励系统等所有核心功能。前端将直接调用这些已有的API接口。

### 设计目标

1. **用户体验优先**: 提供流畅、直观、响应迅速的学习界面
2. **模块化架构**: 高内聚低耦合的组件设计，便于维护和扩展
3. **性能优化**: 关键页面加载时间 < 1.5s，交互响应 < 300ms
4. **响应式设计**: 完美适配桌面、平板和移动设备
5. **可扩展性**: 预留 AI 赋能、社交学习等未来功能的扩展空间

### 技术栈

**前端**:
- Vue 3 (Composition API + `<script setup>`)
- Pinia (状态管理)
- Vue Router 4 (路由管理)
- Axios (HTTP 客户端)
- Tailwind CSS 4 (样式框架)
- Vite (构建工具)

**后端**:
- Spring Boot (已有)
- MySQL 8.0 (已有)
- RESTful API

## 架构设计

### 整体架构


```
┌─────────────────────────────────────────────────────────────┐
│                      用户界面层 (UI Layer)                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │  首页    │ │ 课本管理 │ │ 单词学习 │ │ 句子学习 │  ...  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                    路由层 (Router Layer)                      │
│  - 路由配置                                                   │
│  - 导航守卫 (认证检查)                                        │
│  - 路由懒加载                                                 │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                  状态管理层 (State Layer)                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ Auth     │ │ Book     │ │ Word     │ │ Learning │  ...  │
│  │ Store    │ │ Store    │ │ Store    │ │ Store    │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                   服务层 (Service Layer)                      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ Auth     │ │ Book     │ │ Word     │ │ Sentence │  ...  │
│  │ Service  │ │ Service  │ │ Service  │ │ Service  │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                   HTTP 客户端层 (HTTP Layer)                  │
│  - Axios 实例配置                                             │
│  - 请求/响应拦截器                                            │
│  - 错误处理                                                   │
│  - 认证令牌管理                                               │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                  后端 API (Backend API)                       │
│  Spring Boot + MySQL                                         │
│  RESTful API (http://192.168.43.106:8080)                   │
└─────────────────────────────────────────────────────────────┘
```

### 目录结构

```
English_front_vue/
├── src/
│   ├── assets/              # 静态资源
│   │   ├── images/          # 图片资源
│   │   └── styles/          # 全局样式
│   ├── components/          # 可复用组件
│   │   ├── common/          # 通用组件
│   │   │   ├── Button.vue
│   │   │   ├── Card.vue
│   │   │   ├── Loading.vue
│   │   │   └── Toast.vue
│   │   ├── layout/          # 布局组件
│   │   │   ├── Header.vue
│   │   │   ├── Sidebar.vue
│   │   │   └── Footer.vue
│   │   └── learning/        # 学习相关组件
│   │       ├── WordCard.vue
│   │       ├── SentenceCard.vue
│   │       └── ProgressBar.vue
│   ├── views/               # 页面组件
│   │   ├── Home.vue
│   │   ├── Login.vue
│   │   ├── BookList.vue
│   │   ├── WordLearning.vue
│   │   ├── WordReview.vue
│   │   ├── WordDictation.vue
│   │   ├── ErrorWordBook.vue
│   │   ├── SentenceLearning.vue
│   │   ├── ErrorSentenceBook.vue
│   │   ├── LearningChecklist.vue
│   │   └── Jottings.vue
│   ├── stores/              # Pinia 状态管理
│   │   ├── auth.js          # 认证状态
│   │   ├── book.js          # 课本状态
│   │   ├── word.js          # 单词状态
│   │   ├── sentence.js      # 句子状态
│   │   ├── checklist.js     # 学习清单状态
│   │   └── jotting.js       # 随身记状态
│   ├── services/            # API 服务层
│   │   ├── api.js           # Axios 实例配置
│   │   ├── auth.service.js
│   │   ├── book.service.js
│   │   ├── word.service.js
│   │   ├── sentence.service.js
│   │   ├── checklist.service.js
│   │   └── jotting.service.js
│   ├── router/              # 路由配置
│   │   └── index.js
│   ├── utils/               # 工具函数
│   │   ├── storage.js       # 本地存储
│   │   ├── validator.js     # 表单验证
│   │   └── helpers.js       # 辅助函数
│   ├── composables/         # 组合式函数
│   │   ├── useAuth.js
│   │   ├── useToast.js
│   │   └── useLoading.js
│   ├── App.vue              # 根组件
│   └── main.js              # 入口文件
├── public/
│   └── favicon.ico
├── index.html
├── vite.config.js
├── tailwind.config.js
└── package.json
```

## 组件与接口设计

### 核心组件

#### 1. 认证系统

**组件**: `Login.vue`, `Register.vue`

**状态管理** (`stores/auth.js`):
```javascript
{
  user: null,              // 当前用户信息
  token: null,             // JWT 令牌
  isAuthenticated: false,  // 认证状态
  lastLoginTime: null      // 最后登录时间
}
```

**API 接口** (已实现):
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/validate` - 验证令牌
- `POST /api/auth/refresh` - 刷新令牌



#### 2. 课本管理系统

**组件**: `BookList.vue`, `BookCard.vue`

**状态管理** (`stores/book.js`):
```javascript
{
  books: [],              // 课本列表
  currentBook: null,      // 当前选中的课本
  loading: false          // 加载状态
}
```

**API 接口** (已实现):
- `GET /api/english/books/{userId}` - 获取用户课本列表
- `GET /api/english/books/{bookId}/check` - 检查课本是否存在

**数据模型**:
```javascript
{
  id: number,
  userId: number,
  bookName: string,
  description: string,
  coverUrl: string,
  wordCount: number,
  visibility: 'PRIVATE' | 'PUBLIC',
  status: 0 | 1,
  createTime: string,
  updateTime: string
}
```

#### 3. 单词学习系统

**组件**: 
- `WordLearning.vue` (初学模式)
- `WordReview.vue` (复习模式)
- `WordDictation.vue` (听写模式)
- `ErrorWordBook.vue` (错词本)

**状态管理** (`stores/word.js`):
```javascript
{
  words: [],              // 单词列表
  currentIndex: 0,        // 当前单词索引
  currentWord: null,      // 当前单词
  showChinese: false,     // 是否显示中文
  learningRange: {        // 学习范围
    start: 1,
    end: 50
  },
  progress: {             // 学习进度
    learned: 0,
    total: 0
  }
}
```

**API 接口** (已实现):
- `POST /api/english/wordList` - 获取单词列表
  - 请求体: `{ userId, bookId, start, end }`
- `POST /api/english/notGrasp/{id}` - 标记为错词
- `POST /api/english/isGrasp/{id}` - 标记为已掌握

**数据模型**:
```javascript
{
  id: number,
  userId: number,
  bookId: number,
  word: string,
  chinese: string,
  pronounce: string,
  times: number,
  isGrasp: 0 | 1 | 2,     // 0-未掌握, 1-已掌握, 2-错词
  errorTimes: number,
  createTime: string,
  updateTime: string
}
```

#### 4. 句子学习系统

**组件**: 
- `SentenceLearning.vue` (生句学习)
- `SentenceDictation.vue` (句子听写)
- `ErrorSentenceBook.vue` (错句本)

**状态管理** (`stores/sentence.js`):
```javascript
{
  sentences: [],          // 句子列表
  currentIndex: 0,        // 当前句子索引
  currentSentence: null,  // 当前句子
  showChinese: true       // 是否显示中文
}
```

**API 接口** (已实现):
- `POST /api/english/sentence` - 获取句子列表
  - 请求体: `{ userId, start, end }`

**数据模型**:
```javascript
{
  id: number,
  userId: number,
  sentence: string,
  chinese: string,
  isGrasp: 0 | 1 | 2,     // 0-未掌握, 1-已掌握, 2-错句
  errorTimes: number,
  createTime: string,
  updateTime: string
}
```

#### 5. 学习清单系统

**组件**: `LearningChecklist.vue`

**状态管理** (`stores/checklist.js`):
```javascript
{
  checklists: {
    word: [],             // 单词清单
    sentence: [],         // 句子清单
    listening: []         // 听力清单
  },
  activeTab: 'word',      // 当前标签
  selectedItems: []       // 选中的项目
}
```

**API 接口** (已实现):
- `POST /api/studyRecord/addRecord` - 添加学习记录
  - 请求体: `{ userId, learningRecord, type }`
- `GET /api/studyRecord/list/{userId}?type={type}` - 获取学习清单
- `POST /api/studyRecord/update` - 更新学习记录
- `POST /api/studyRecord/delete` - 删除学习记录
  - 请求体: `string[]` (ID数组)
  - 查询参数: `userId`

**数据模型**:
```javascript
{
  id: number,
  userId: number,
  learningRecord: string,
  type: 0 | 1 | 2,        // 0-句子, 1-单词, 2-听力
  selected: 0 | 1,        // 是否选中今日复习
  alreadyReviewed: 0 | 1, // 是否已复习
  createTime: string,
  updateTime: string
}
```

#### 6. 知识积累系统（随身记）

**组件**: `Jottings.vue`, `JottingButton.vue` (悬浮按钮)

**状态管理** (`stores/jotting.js`):
```javascript
{
  jottings: [],           // 积累列表
  activeTab: 'word',      // 当前标签 (word/sentence)
  pagination: {
    page: 1,
    pageSize: 20,
    total: 0
  }
}
```

**API 接口** (已实现):
- `GET /api/jotting/list` - 获取积累列表
  - 查询参数: `userId, page, size`
- `POST /api/jotting/add` - 添加积累
- `PUT /api/jotting/update` - 更新积累
- `DELETE /api/jotting/delete` - 删除积累
- `POST /api/jotting/batchDelete` - 批量删除
- `POST /api/jotting/setReview/{userId}` - 标记为已复习

**数据模型**:
```javascript
{
  id: number,
  userId: number,
  word: string | null,
  sentence: string | null,
  chinese: string,
  type: string,           // "word" | "sentence"
  reviewed: 0 | 1,        // 是否已复习
  createTime: string,
  updateTime: string
}
```

#### 7. 激励系统

**组件**: `MotivationQuote.vue` (轮播组件)

**API 接口** (已实现):
- `GET /api/global/motivation/random` - 获取随机激励文案
- `GET /api/global/motivation/list` - 获取激励文案列表

**数据模型**:
```javascript
{
  english: string,
  chinese: string,
  author: string,
  tag: string
}
```

## 数据模型

### 前端数据流

```
用户操作 → 组件事件 → Store Action → Service API 调用 → 后端处理
                                                            ↓
用户界面 ← 组件更新 ← Store State 更新 ← Service 返回数据 ←┘
```

### 状态管理策略

1. **全局状态** (Pinia Stores):
   - 用户认证信息 (`auth`)
   - 当前课本上下文 (`book`)
   - 学习进度和状态 (`word`, `sentence`)

2. **组件本地状态** (ref/reactive):
   - UI 交互状态（展开/折叠、显示/隐藏）
   - 表单输入数据
   - 临时计算结果

3. **持久化策略**:
   - 认证令牌存储在 `localStorage`
   - 当前课本 ID 存储在 `localStorage`
   - 学习进度实时同步到后端



## HTTP 客户端设计

### Axios 配置

**文件**: `services/api.js`

```javascript
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'

// 创建 Axios 实例
const api = axios.create({
  baseURL: 'http://192.168.43.106:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    const toast = useToast()
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，清除令牌并跳转登录
          const authStore = useAuthStore()
          authStore.logout()
          router.push('/login')
          toast.error('登录已过期，请重新登录')
          break
        case 403:
          toast.error('没有权限访问该资源')
          break
        case 404:
          toast.error('请求的资源不存在')
          break
        case 500:
          toast.error('服务器错误，请稍后重试')
          break
        default:
          toast.error(error.response.data.message || '请求失败')
      }
    } else if (error.request) {
      toast.error('网络连接失败，请检查网络')
    } else {
      toast.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default api
```

### 服务层封装

每个功能模块都有对应的服务文件，封装 API 调用逻辑：

**示例**: `services/word.service.js`

```javascript
import api from './api'

export const wordService = {
  // 获取单词列表
  async getWordList(params) {
    return api.post('/api/english/wordList', params)
  },
  
  // 标记为错词
  async markAsError(wordId) {
    return api.post(`/api/english/notGrasp/${wordId}`)
  },
  
  // 标记为已掌握
  async markAsGrasped(wordId) {
    return api.post(`/api/english/isGrasp/${wordId}`)
  }
}
```

## 路由设计

### 路由配置

**文件**: `router/index.js`

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/books',
    name: 'BookList',
    component: () => import('@/views/BookList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/word/learning',
    name: 'WordLearning',
    component: () => import('@/views/WordLearning.vue'),
    meta: { requiresAuth: true, requiresBook: true }
  },
  {
    path: '/word/review',
    name: 'WordReview',
    component: () => import('@/views/WordReview.vue'),
    meta: { requiresAuth: true, requiresBook: true }
  },
  {
    path: '/word/dictation',
    name: 'WordDictation',
    component: () => import('@/views/WordDictation.vue'),
    meta: { requiresAuth: true, requiresBook: true }
  },
  {
    path: '/word/error-book',
    name: 'ErrorWordBook',
    component: () => import('@/views/ErrorWordBook.vue'),
    meta: { requiresAuth: true, requiresBook: true }
  },
  {
    path: '/sentence/learning',
    name: 'SentenceLearning',
    component: () => import('@/views/SentenceLearning.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/sentence/error-book',
    name: 'ErrorSentenceBook',
    component: () => import('@/views/ErrorSentenceBook.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/checklist',
    name: 'LearningChecklist',
    component: () => import('@/views/LearningChecklist.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/jottings',
    name: 'Jottings',
    component: () => import('@/views/Jottings.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局导航守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const bookStore = useBookStore()
  
  // 检查认证
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }
  
  // 检查是否需要选择课本
  if (to.meta.requiresBook && !bookStore.currentBook) {
    next('/books')
    return
  }
  
  next()
})

export default router
```

## 响应式设计

### 断点定义

使用 Tailwind CSS 的默认断点：

- `sm`: 640px (小型设备)
- `md`: 768px (平板)
- `lg`: 1024px (桌面)
- `xl`: 1280px (大屏幕)
- `2xl`: 1536px (超大屏幕)

### 移动优先策略

1. **基础样式**: 默认为移动设备设计
2. **渐进增强**: 使用媒体查询为更大屏幕添加样式
3. **触摸优化**: 按钮和可点击元素最小 44x44px
4. **字体缩放**: 使用相对单位 (rem, em)

### 布局适配

**桌面布局** (≥1200px):
- 侧边栏导航 + 主内容区
- 多列卡片布局
- 悬浮操作按钮

**平板布局** (768px-1199px):
- 可折叠侧边栏
- 两列卡片布局
- 底部导航栏

**移动布局** (<768px):
- 顶部导航栏 + 汉堡菜单
- 单列卡片布局
- 底部固定操作栏



## 正确性属性

*属性是一个特征或行为，应该在系统的所有有效执行中保持为真——本质上是关于系统应该做什么的形式化陈述。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*

### 属性 1: 未认证用户路由保护

*对于任何* 未认证用户和任何需要认证的路由，当用户尝试访问该路由时，系统应该重定向到登录页面

**验证需求**: 1.1

### 属性 2: 认证令牌生成

*对于任何* 有效的用户凭证，当提交登录请求时，系统应该返回有效的JWT令牌

**验证需求**: 1.2

### 属性 3: 登出状态清除

*对于任何* 已登录用户，当执行登出操作时，系统应该清除所有认证状态（令牌、用户信息）并重定向到登录页面

**验证需求**: 1.5

### 属性 4: 课本列表过滤

*对于任何* 用户，返回的课本列表应该只包含该用户拥有的课本或状态为启用(status=1)的公开课本

**验证需求**: 2.1, 2.6

### 属性 5: 课本上下文隔离

*对于任何* 在特定课本上下文中的单词操作，所有API请求应该包含正确的bookId参数

**验证需求**: 2.4

### 属性 6: 列表导航一致性

*对于任何* 包含列表的学习模式（单词、句子），点击"下一个"应该增加索引，点击"上一个"应该减少索引，且索引应该在有效范围内

**验证需求**: 3.2, 3.3, 4.5, 6.2, 6.3

### 属性 7: 学习范围过滤

*对于任何* 指定的学习范围(start, end)，加载的单词列表应该只包含索引在该范围内的单词

**验证需求**: 3.8

### 属性 8: 掌握状态更新

*对于任何* 单词或句子，当标记为已掌握时，其isGrasp字段应该更新为1；当标记为错词/错句时，isGrasp应该更新为2且errorTimes应该增加

**验证需求**: 4.3, 4.4, 8.3, 8.4, 9.3, 9.4

### 属性 9: 听写答案校对

*对于任何* 用户输入的答案和正确答案，校对结果应该准确标识出所有不匹配的字符位置

**验证需求**: 5.3

### 属性 10: 听写正确答案处理

*对于任何* 听写模式中的正确答案，系统应该自动标记为已掌握并导航到下一个项目

**验证需求**: 5.4, 8.5, 9.5

### 属性 11: 听写错误答案处理

*对于任何* 听写模式中的错误答案，系统应该标记为错词/错句并增加错误次数

**验证需求**: 5.5, 8.6, 9.6

### 属性 12: 类型过滤一致性

*对于任何* 支持类型过滤的列表（学习清单、积累本），当选择特定类型时，显示的所有项目的type字段应该与选中类型匹配

**验证需求**: 7.2, 10.5

### 属性 13: 批量操作原子性

*对于任何* 批量操作（批量标记完成、批量删除），要么所有项目都成功操作，要么所有项目都不操作

**验证需求**: 7.6, 10.7

### 属性 14: 错词本过滤

*对于任何* 错词本或错句本，显示的所有项目的isGrasp字段应该等于2

**验证需求**: 8.1, 9.1

### 属性 15: 分页数据一致性

*对于任何* 分页请求，返回的数据数量应该不超过pageSize，且所有数据应该属于正确的页码范围

**验证需求**: 10.4

### 属性 16: 全局组件可见性

*对于任何* 需要认证的路由，悬浮的随身记按钮应该在页面中可见

**验证需求**: 10.1

### 属性 17: API错误处理

*对于任何* API请求失败（4xx, 5xx），系统应该显示用户友好的错误消息而不是抛出未捕获的异常

**验证需求**: 13.1, 13.2, 13.3

### 属性 18: 数据持久化一致性

*对于任何* 状态更新操作，本地状态和后端数据应该保持一致

**验证需求**: 14.1, 14.3

### 属性 19: 响应式断点适配

*对于任何* 屏幕宽度，UI布局应该使用对应断点的样式（<768px移动端，768-1199px平板，≥1200px桌面）

**验证需求**: 12.1, 12.2, 12.3

### 属性 20: 可访问性ARIA标签

*对于任何* 交互式UI元素，应该包含适当的ARIA标签或语义化HTML标签

**验证需求**: 17.1



## 错误处理

### 错误分类

1. **网络错误**:
   - 连接超时
   - 网络中断
   - DNS解析失败

2. **HTTP错误**:
   - 401 未授权
   - 403 禁止访问
   - 404 资源不存在
   - 500 服务器错误

3. **业务错误**:
   - 数据验证失败
   - 业务规则违反
   - 资源冲突

4. **客户端错误**:
   - 表单验证失败
   - 本地存储失败
   - 浏览器兼容性问题

### 错误处理策略

#### 1. 全局错误处理

**Axios响应拦截器**:
- 统一处理HTTP错误码
- 自动刷新过期令牌
- 显示用户友好的错误消息

**Vue错误处理器**:
```javascript
app.config.errorHandler = (err, instance, info) => {
  console.error('Vue Error:', err, info)
  // 上报错误到监控系统
  // 显示错误提示
}
```

#### 2. 组件级错误处理

**错误边界**:
- 使用`onErrorCaptured`钩子捕获子组件错误
- 显示降级UI
- 防止整个应用崩溃

**示例**:
```javascript
onErrorCaptured((err, instance, info) => {
  console.error('Component Error:', err)
  showError.value = true
  return false // 阻止错误继续传播
})
```

#### 3. 异步操作错误处理

**统一的try-catch模式**:
```javascript
async function loadData() {
  loading.value = true
  error.value = null
  
  try {
    const data = await api.getData()
    // 处理数据
  } catch (err) {
    error.value = err.message
    toast.error('加载失败，请重试')
  } finally {
    loading.value = false
  }
}
```

#### 4. 表单验证错误

**客户端验证**:
- 实时验证用户输入
- 显示内联错误消息
- 防止无效数据提交

**服务端验证**:
- 处理后端返回的验证错误
- 映射错误到对应表单字段
- 显示详细错误信息

### 用户反馈机制

#### Toast通知

**使用场景**:
- 操作成功提示
- 错误消息
- 警告信息

**实现** (`composables/useToast.js`):
```javascript
export function useToast() {
  const toast = {
    success(message, duration = 3000) {
      // 显示成功提示
    },
    error(message, duration = 5000) {
      // 显示错误提示
    },
    warning(message, duration = 4000) {
      // 显示警告提示
    },
    info(message, duration = 3000) {
      // 显示信息提示
    }
  }
  
  return toast
}
```

#### 加载指示器

**使用场景**:
- API请求进行中
- 数据加载中
- 页面切换中

**类型**:
- 全屏加载遮罩
- 按钮加载状态
- 骨架屏

#### 空状态

**使用场景**:
- 列表为空
- 搜索无结果
- 功能未开放

**设计原则**:
- 清晰的说明文字
- 引导性的操作按钮
- 友好的插图

## 测试策略

### 测试金字塔

```
        /\
       /  \
      / E2E \          少量端到端测试
     /______\
    /        \
   /  集成测试 \       适量集成测试
  /____________\
 /              \
/   单元测试      \    大量单元测试
/________________\
```

### 单元测试

**测试框架**: Vitest

**测试范围**:
- 工具函数 (`utils/`)
- 组合式函数 (`composables/`)
- 服务层 (`services/`)
- Store actions 和 getters

**示例**:
```javascript
// utils/validator.test.js
import { describe, it, expect } from 'vitest'
import { validateEmail, validatePassword } from './validator'

describe('Validator', () => {
  describe('validateEmail', () => {
    it('应该接受有效的邮箱地址', () => {
      expect(validateEmail('test@example.com')).toBe(true)
      expect(validateEmail('user.name+tag@example.co.uk')).toBe(true)
    })
    
    it('应该拒绝无效的邮箱地址', () => {
      expect(validateEmail('invalid')).toBe(false)
      expect(validateEmail('@example.com')).toBe(false)
      expect(validateEmail('test@')).toBe(false)
    })
  })
})
```

### 属性测试

**测试框架**: fast-check (JavaScript属性测试库)

**测试范围**:
- 核心业务逻辑
- 数据转换函数
- 状态管理逻辑

**配置**: 每个属性测试至少运行100次迭代

**示例**:
```javascript
// stores/word.test.js
import { describe, it } from 'vitest'
import { fc } from 'fast-check'
import { useWordStore } from './word'

describe('Word Store Properties', () => {
  it('属性 6: 列表导航一致性', () => {
    fc.assert(
      fc.property(
        fc.array(fc.record({ id: fc.integer(), word: fc.string() })),
        fc.integer({ min: 0 }),
        (words, startIndex) => {
          const store = useWordStore()
          store.words = words
          store.currentIndex = startIndex % words.length
          
          const initialIndex = store.currentIndex
          store.nextWord()
          
          // 验证索引增加且在有效范围内
          return store.currentIndex === (initialIndex + 1) % words.length
        }
      ),
      { numRuns: 100 }
    )
  })
})
```

### 组件测试

**测试框架**: Vitest + Vue Test Utils

**测试范围**:
- 组件渲染
- 用户交互
- Props和Events
- 条件渲染

**示例**:
```javascript
// components/WordCard.test.js
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import WordCard from './WordCard.vue'

describe('WordCard', () => {
  it('应该显示单词的所有信息', () => {
    const word = {
      word: 'hello',
      chinese: '你好',
      pronounce: '/həˈləʊ/',
      errorTimes: 3
    }
    
    const wrapper = mount(WordCard, {
      props: { word }
    })
    
    expect(wrapper.text()).toContain('hello')
    expect(wrapper.text()).toContain('你好')
    expect(wrapper.text()).toContain('/həˈləʊ/')
    expect(wrapper.text()).toContain('3')
  })
  
  it('当isGrasp=1时应该以灰色显示错词统计', () => {
    const word = {
      word: 'test',
      chinese: '测试',
      pronounce: '/test/',
      isGrasp: 1,
      errorTimes: 2
    }
    
    const wrapper = mount(WordCard, {
      props: { word }
    })
    
    const errorDisplay = wrapper.find('.error-times')
    expect(errorDisplay.classes()).toContain('text-gray-400')
  })
})
```

### 集成测试

**测试范围**:
- 路由导航流程
- Store与Service交互
- 多组件协作

**示例**:
```javascript
// integration/word-learning.test.js
import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import WordLearning from '@/views/WordLearning.vue'

describe('Word Learning Integration', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })
  
  it('应该完整地完成单词学习流程', async () => {
    const wrapper = mount(WordLearning)
    
    // 等待数据加载
    await wrapper.vm.$nextTick()
    
    // 验证初始状态
    expect(wrapper.find('.word-card').exists()).toBe(true)
    
    // 点击下一个
    await wrapper.find('.btn-next').trigger('click')
    
    // 验证索引更新
    expect(wrapper.vm.currentIndex).toBe(1)
    
    // 标记为已掌握
    await wrapper.find('.btn-grasp').trigger('click')
    
    // 验证API调用
    // ...
  })
})
```

### E2E测试

**测试框架**: Playwright

**测试范围**:
- 关键用户流程
- 跨页面交互
- 真实浏览器环境

**示例**:
```javascript
// e2e/word-learning.spec.js
import { test, expect } from '@playwright/test'

test('完整的单词学习流程', async ({ page }) => {
  // 登录
  await page.goto('/login')
  await page.fill('[name="username"]', 'testuser')
  await page.fill('[name="password"]', 'password123')
  await page.click('button[type="submit"]')
  
  // 选择课本
  await page.goto('/books')
  await page.click('.book-card:first-child')
  
  // 进入单词学习
  await page.goto('/word/learning')
  
  // 验证单词卡片显示
  await expect(page.locator('.word-card')).toBeVisible()
  
  // 点击下一个
  await page.click('.btn-next')
  
  // 验证进度更新
  const progress = await page.locator('.progress-text').textContent()
  expect(progress).toContain('2/')
})
```

### 测试覆盖率目标

- **单元测试**: 80%+ 代码覆盖率
- **属性测试**: 100% 核心业务逻辑覆盖
- **组件测试**: 70%+ 组件覆盖率
- **集成测试**: 覆盖所有关键用户流程
- **E2E测试**: 覆盖所有主要功能模块

### 持续集成

**CI流程**:
1. 代码提交触发CI
2. 运行代码检查 (ESLint)
3. 运行单元测试
4. 运行属性测试
5. 运行组件测试
6. 生成测试覆盖率报告
7. 构建生产版本

**测试环境**:
- Node.js 18+
- 多浏览器测试 (Chrome, Firefox, Safari)
- 移动设备模拟

## 性能优化

### 代码分割

**路由懒加载**:
```javascript
const routes = [
  {
    path: '/word/learning',
    component: () => import('@/views/WordLearning.vue')
  }
]
```

**组件懒加载**:
```javascript
const HeavyComponent = defineAsyncComponent(() =>
  import('./HeavyComponent.vue')
)
```

### 资源优化

1. **图片优化**:
   - 使用WebP格式
   - 响应式图片
   - 懒加载

2. **字体优化**:
   - 字体子集化
   - 字体预加载
   - 系统字体回退

3. **CSS优化**:
   - Tailwind CSS PurgeCSS
   - 关键CSS内联
   - CSS压缩

### 缓存策略

1. **HTTP缓存**:
   - 静态资源长期缓存
   - API响应短期缓存

2. **Service Worker**:
   - 离线缓存
   - 预缓存关键资源

3. **内存缓存**:
   - Pinia状态持久化
   - 计算属性缓存

### 渲染优化

1. **虚拟滚动**:
   - 长列表优化
   - 减少DOM节点

2. **防抖和节流**:
   - 搜索输入防抖
   - 滚动事件节流

3. **组件优化**:
   - 使用`v-memo`
   - 合理使用`v-once`
   - 避免不必要的响应式

## 安全考虑

### 认证与授权

1. **JWT令牌管理**:
   - 安全存储在localStorage
   - 自动刷新机制
   - 令牌过期处理

2. **路由守卫**:
   - 全局前置守卫
   - 路由级别权限检查

### XSS防护

1. **输入验证**:
   - 客户端验证
   - 服务端验证

2. **输出转义**:
   - Vue自动转义
   - v-html谨慎使用

### CSRF防护

1. **CSRF令牌**:
   - 每个请求携带CSRF令牌
   - 服务端验证

### 数据加密

1. **传输加密**:
   - HTTPS强制
   - 敏感数据加密

2. **存储加密**:
   - 密码不明文存储
   - 敏感信息加密

## 部署策略

### 构建配置

**生产构建**:
```bash
npm run build
```

**环境变量**:
```
VITE_API_BASE_URL=http://192.168.43.106:8080
VITE_APP_TITLE=英语学习平台
```

### 部署流程

1. **构建优化**:
   - 代码压缩
   - Tree shaking
   - 资源优化

2. **静态资源部署**:
   - CDN加速
   - Gzip压缩
   - 缓存策略

3. **监控与日志**:
   - 错误监控
   - 性能监控
   - 用户行为分析

## 未来扩展

### AI赋能

1. **智能推荐**:
   - 基于学习历史推荐单词
   - 个性化学习路径

2. **语音识别**:
   - 发音评测
   - 口语练习

3. **智能纠错**:
   - 拼写纠错
   - 语法检查

### 社交学习

1. **学习小组**:
   - 创建/加入小组
   - 小组学习计划

2. **排行榜**:
   - 学习时长排行
   - 单词掌握排行

3. **成就系统**:
   - 学习徽章
   - 里程碑奖励

### 离线支持

1. **PWA**:
   - Service Worker
   - 离线缓存
   - 安装到桌面

2. **数据同步**:
   - 离线数据存储
   - 在线自动同步
   - 冲突解决

