<template>
  <div class="min-h-screen bg-[#F8FAFC] text-slate-800 font-sans selection:bg-blue-100 selection:text-blue-900">
    <!-- 顶部导航栏 -->
    <header class="sticky top-0 z-40 bg-white/80 backdrop-blur-xl border-b border-slate-200/60">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16 sm:h-20">
          <!-- Logo区域 -->
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg shadow-blue-500/20 text-white">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z" />
              </svg>
            </div>
            <div class="hidden sm:block">
              <h1 class="text-xl font-bold text-slate-900 tracking-tight">轻风智语</h1>
              <p class="text-[10px] font-medium text-slate-400 uppercase tracking-widest">BreezeWise</p>
            </div>
          </div>

          <!-- 右侧功能区 -->
          <div class="flex items-center gap-3 sm:gap-6">
            <!-- 学习天数 (PC端显示) -->
            <div class="hidden md:flex items-center gap-2 px-4 py-2 bg-slate-50 rounded-full border border-slate-100">
              <div class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></div>
              <span class="text-sm text-slate-600">已坚持学习 <span class="font-bold text-slate-900">{{ stats.studyDays || 0 }}</span> 天</span>
            </div>

            <!-- 用户头像 -->
            <div class="flex items-center gap-3 pl-3 sm:border-l border-slate-200 relative" ref="userMenuRef">
              <div class="text-right hidden sm:block">
                <p class="text-sm font-semibold text-slate-900">{{ getGreeting() }}</p>
                <p class="text-xs text-slate-500 max-w-[100px] truncate">{{ displayName }}</p>
              </div>
              <button 
                @click="toggleUserMenu"
                class="relative group"
              >
                <div class="w-10 h-10 sm:w-11 sm:h-11 rounded-full border-2 border-white shadow-md overflow-hidden bg-slate-100 transition-transform transform group-hover:scale-105 group-active:scale-95">
                  <img
                    v-if="userAvatar && !avatarLoadError"
                    :src="userAvatar"
                    :alt="displayName"
                    class="w-full h-full object-cover"
                    @error="handleAvatarError"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center bg-indigo-100 text-indigo-600 font-bold text-lg">
                    {{ getUserInitial() }}
                  </div>
                </div>
              </button>

              <!-- 用户菜单 -->
              <transition name="fade">
                <div
                  v-if="showUserMenu"
                  class="absolute top-14 right-0 w-44 bg-white rounded-2xl shadow-xl border border-slate-100 py-2 z-50"
                >
                  <button @click="goToProfile" class="w-full px-4 py-2 text-left text-sm text-slate-600 hover:bg-slate-50 flex items-center gap-2">
                    <svg class="w-4 h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5.121 17.804A13.937 13.937 0 0112 15c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 10-6 0 3 3 0 006 0z"/></svg>
                    个人中心
                  </button>
                  <button @click="openLogoutConfirm" class="w-full px-4 py-2 text-left text-sm text-rose-600 hover:bg-rose-50 flex items-center gap-2">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
                    退出登录
                  </button>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      
      <!-- 第一部分：欢迎与核心状态 (Bento Grid) -->
      <section class="grid grid-cols-1 lg:grid-cols-12 gap-6">
        
        <!-- 欢迎卡片 -->
        <div class="lg:col-span-8 relative overflow-hidden rounded-[2rem] bg-gradient-to-br from-indigo-600 via-blue-600 to-blue-700 text-white shadow-xl shadow-indigo-500/20 p-8 sm:p-10 flex flex-col gap-8 min-h-[240px]">
          <!-- 装饰背景 -->
          <div class="absolute top-0 right-0 -mt-10 -mr-10 w-64 h-64 bg-white/10 rounded-full blur-3xl"></div>
          <div class="absolute bottom-0 left-0 -mb-10 -ml-10 w-40 h-40 bg-purple-500/20 rounded-full blur-2xl"></div>
          
          <div class="relative z-10 space-y-3">
            <p class="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-white/15 text-xs uppercase tracking-[0.3em] text-white/80 w-max">
              今日激励
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-300 animate-pulse"></span>
            </p>
            <h2 class="text-3xl sm:text-4xl md:text-5xl font-bold tracking-tight leading-tight">
              {{ getWelcomeMessage() }}
            </h2>
            <p class="text-indigo-100 text-base sm:text-lg max-w-2xl font-medium opacity-90">
              保持热爱，奔赴山海。今天的目标完成了吗？
            </p>
          </div>

          <div class="relative z-10 flex flex-wrap gap-3 sm:gap-4 w-full">
             <button 
              @click="currentBook ? goToWordLearning() : goToBooks()"
              class="px-8 py-3.5 bg-white text-indigo-600 rounded-xl font-bold text-base sm:text-lg shadow-lg hover:shadow-xl hover:bg-indigo-50 transition-all duration-300 transform hover:-translate-y-0.5 flex items-center gap-2"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {{ currentBook ? '开始学习' : '选择课本' }}
            </button>
            <button 
              @click="goToChecklist"
              class="px-8 py-3 bg-indigo-800/40 backdrop-blur-md border border-white/20 text-white rounded-xl font-semibold text-base sm:text-lg hover:bg-indigo-800/60 transition-all duration-300 flex items-center gap-2"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
              </svg>
              查看计划
            </button>
          </div>

          <!-- 学习时长卡片 -->
          <div class="relative z-10 w-full grid grid-cols-1 sm:grid-cols-2 gap-3 sm:gap-4">
            <div class="flex flex-col justify-between rounded-2xl border border-white/20 bg-white/15 backdrop-blur-lg p-4 sm:p-5 shadow-lg shadow-indigo-900/20 hover:shadow-xl transition-all duration-300">
              <div class="flex items-center justify-between text-white/80 text-xs uppercase tracking-[0.3em]">
                今日学习
                <button 
                  class="text-white/70 hover:text-white transition-colors"
                  @click="refreshStudyDuration"
                  :title="studyStatStore.loading ? '更新中' : '刷新学习时长'"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" :class="{ 'animate-spin': studyStatStore.loading }">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                  </svg>
                </button>
              </div>
              <div class="mt-6">
                <p class="text-4xl font-bold text-white drop-shadow-sm">{{ studyStatStore.todayDisplay }}</p>
                <p class="mt-2 text-sm text-white/80 flex items-center gap-1">
                  <span class="w-1.5 h-1.5 rounded-full bg-emerald-300 animate-pulse"></span>
                  持续保持节奏
                </p>
              </div>
            </div>
            <div class="flex flex-col justify-between rounded-2xl border border-white/20 bg-gradient-to-br from-white/30 via-white/10 to-transparent backdrop-blur-lg p-4 sm:p-5 shadow-lg shadow-indigo-900/10 hover:shadow-xl transition-all duration-300">
              <div class="text-xs uppercase tracking-[0.3em] text-slate-900/70 flex items-center gap-2">
                累计学习
                <span class="px-2 py-0.5 rounded-full bg-white/60 text-[10px] font-semibold text-slate-700">终身</span>
              </div>
              <div class="mt-6">
                <p class="text-4xl font-bold text-slate-900">{{ studyStatStore.totalDisplay }}</p>
                <p class="mt-2 text-sm text-slate-600 flex items-center gap-2">
                  <span class="inline-flex items-center text-[10px] font-semibold text-indigo-600 bg-white/70 px-2 py-0.5 rounded-full">
                    {{ stats.studyDays || 0 }} 天坚持
                  </span>
                  <span v-if="studyStatStore.lastUpdateTime" class="text-slate-500 text-xs">
                    更新：{{ formatUpdateTime(studyStatStore.lastUpdateTime) }}
                  </span>
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- 课本状态卡片 (重新设计 - 紧凑信息) -->
        <div class="lg:col-span-4 bg-white rounded-[2rem] p-6 shadow-sm border border-slate-100 flex flex-col gap-6 h-full hover:shadow-lg transition-shadow duration-300">
          
          <!-- 头部：标题与切换 -->
          <div class="flex justify-between items-start">
            <div class="flex-1 mr-4 cursor-pointer space-y-1" @click="showBookSelector = true">
              <div class="text-xs font-bold text-slate-400 tracking-wider uppercase">Current Book</div>
              <h3 class="text-lg sm:text-xl font-bold text-slate-800 line-clamp-2 leading-snug" :title="currentBook ? (currentBook.bookName || currentBook.name) : ''">
                {{ currentBook ? (currentBook.bookName || currentBook.name) : '未选择课本' }}
              </h3>
              <p v-if="currentBook && currentBook.level" class="text-xs text-slate-500 flex items-center gap-2">
                <span class="inline-flex items-center px-2 py-1 rounded-full bg-slate-100 text-slate-600">{{ currentBook.level }}</span>
                <span>{{ currentBook.unit || '未设置单元' }}</span>
              </p>
            </div>
            <button 
              @click.stop="showBookSelector = true"
              class="flex-shrink-0 p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-xl transition-all active:scale-95"
              title="切换课本"
            >
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
              </svg>
            </button>
          </div>

          <!-- 内容区域 -->
          <div class="space-y-5" v-if="currentBook && currentBook.id">
            <!-- 进度数值 -->
            <div class="flex items-end gap-2">
              <span class="text-4xl font-bold text-slate-900 leading-none">{{ progressPercent }}</span>
              <span class="text-sm font-medium text-slate-400">%</span>
              <span class="text-xs px-2 py-0.5 rounded-full bg-blue-50 text-blue-600 font-semibold">学习进度</span>
            </div>
            
            <!-- 进度条 -->
            <div class="space-y-2">
              <div class="h-2 w-full bg-slate-100 rounded-full overflow-hidden">
              <div 
                class="h-full bg-blue-600 rounded-full transition-all duration-1000 ease-out" 
                :style="{ width: `${progressPercent}%` }"
              ></div>
              </div>
              <div class="flex justify-between text-xs text-slate-400 font-medium">
                <span>已完成 {{ masteredCount }} 项</span>
                <span>剩余 {{ remainingCount }}</span>
              </div>
            </div>

            <div class="flex gap-3">
              <button 
                @click="showBookSelector = true"
                class="flex-1 py-3 rounded-2xl border border-slate-200 text-sm font-semibold text-slate-600 hover:border-blue-200 hover:text-blue-600 transition-colors"
              >
                切换课本
              </button>
              <button 
                @click="currentBook ? goToWordLearning() : goToBooks()"
                class="flex-1 py-3 rounded-2xl bg-blue-600 text-white text-sm font-semibold shadow-sm hover:bg-blue-700 transition-colors"
              >
                继续学习
              </button>
            </div>

            <!-- 今日单词表现 -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
              <div class="rounded-2xl border border-slate-100 bg-gradient-to-br from-sky-50 to-white p-4 flex flex-col gap-2 shadow-sm">
                <div class="flex items-center justify-between text-xs font-semibold text-sky-600 uppercase tracking-widest">
                  今日掌握单词
                  <span class="px-2 py-0.5 rounded-full bg-white text-sky-600">{{ stats.todayMasteredWords || 0 }} 个</span>
                </div>
                <p class="text-3xl font-bold text-slate-900">{{ stats.todayMasteredWords || 0 }}</p>
              </div>
              <div class="rounded-2xl border border-slate-100 bg-gradient-to-br from-rose-50 to-white p-4 flex flex-col gap-2 shadow-sm">
                <div class="flex items-center justify-between text-xs font-semibold text-rose-600 uppercase tracking-widest">
                  今日错词数量
                  <span class="px-2 py-0.5 rounded-full bg-white text-rose-600">{{ stats.todayErrorWords || 0 }} 个</span>
                </div>
                <p class="text-3xl font-bold text-slate-900">{{ stats.todayErrorWords || 0 }}</p>
              </div>
            </div>

            <!-- 课本词汇概览 -->
            <div class="rounded-2xl border border-slate-100 bg-slate-50/60 p-4 sm:p-5 shadow-inner">
              <div class="flex items-center justify-between mb-4">
                <p class="text-sm font-semibold text-slate-600 flex items-center gap-2">
                  <span class="w-1.5 h-1.5 rounded-full bg-blue-500"></span>
                  课本词汇概览
                </p>
                <span class="text-xs text-slate-400">实时更新</span>
              </div>
              <div class="grid grid-cols-3 gap-3 text-center">
                <div class="bg-white rounded-xl py-3 px-2 border border-white/60 shadow-sm">
                  <p class="text-xs text-slate-400">总词汇</p>
                  <p class="text-2xl font-bold text-slate-900 mt-1">{{ stats.totalWords || 0 }}</p>
                </div>
                <div class="bg-white rounded-xl py-3 px-2 border border-white/60 shadow-sm">
                  <p class="text-xs text-emerald-500">已掌握</p>
                  <p class="text-2xl font-bold text-emerald-600 mt-1">{{ stats.masteredWords || 0 }}</p>
                </div>
                <div class="bg-white rounded-xl py-3 px-2 border border-white/60 shadow-sm">
                  <p class="text-xs text-rose-500">错词</p>
                  <p class="text-2xl font-bold text-rose-600 mt-1">{{ stats.errorWords || 0 }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="flex-1 flex flex-col items-center justify-center min-h-[120px] text-slate-400 cursor-pointer" @click="showBookSelector = true">
            <svg class="w-10 h-10 mb-2 text-slate-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z" />
            </svg>
            <p class="text-sm">点击选择课本</p>
          </div>

        </div>
      </section>

      <!-- 第二部分：功能入口 (学习中心 - 扩展版) -->
      <section>
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-2xl font-bold text-slate-900">学习中心</h2>
          <div class="flex gap-2">
            <!-- 预留顶部操作区 -->
          </div>
        </div>

        <div class="space-y-8">
          <!-- 单词专区 -->
          <div>
            <h3 class="text-sm font-bold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
              单词训练
            </h3>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
              <!-- 单词学习 -->
              <div 
                @click="currentBook ? goToWordLearning() : goToBooks()"
                class="group cursor-pointer bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 relative overflow-hidden"
              >
                <div class="relative z-10">
                  <div class="w-12 h-12 rounded-2xl bg-emerald-100 text-emerald-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">单词学习</h3>
                  <p class="text-xs text-slate-500 mb-3">核心词汇记忆</p>
                  <div class="inline-flex items-center text-[10px] font-semibold text-emerald-700 bg-emerald-50 px-2 py-1 rounded-md">
                    {{ stats.masteredWords || 0 }} 已掌握
                  </div>
                </div>
              </div>

              <!-- 单词复习 -->
              <div 
                @click="currentBook ? goToWordReview() : goToBooks()"
                class="group cursor-pointer bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 relative overflow-hidden"
              >
                <div class="relative z-10">
                  <div class="w-12 h-12 rounded-2xl bg-blue-100 text-blue-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">单词复习</h3>
                  <p class="text-xs text-slate-500 mb-3">巩固记忆曲线</p>
                  <div class="inline-flex items-center text-[10px] font-semibold text-blue-700 bg-blue-50 px-2 py-1 rounded-md">
                    智能复习
                  </div>
                </div>
              </div>

              <!-- 单词听写 -->
              <div 
                @click="currentBook ? goToWordDictation() : goToBooks()"
                class="group cursor-pointer bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 relative overflow-hidden"
              >
                <div class="relative z-10">
                  <div class="w-12 h-12 rounded-2xl bg-purple-100 text-purple-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11a7 7 0 01-7 7m0 0a7 7 0 01-7-7m7 7v4m0 0H8m4 0h4m-4-8a3 3 0 01-3-3V5a3 3 0 116 0v6a3 3 0 01-3 3z" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">单词听写</h3>
                  <p class="text-xs text-slate-500 mb-3">听音辨义拼写</p>
                  <div class="inline-flex items-center text-[10px] font-semibold text-purple-700 bg-purple-50 px-2 py-1 rounded-md">
                    强化听力
                  </div>
                </div>
              </div>

              <!-- 错词本 -->
              <div 
                @click="currentBook ? goToErrorWordBook() : goToBooks()"
                class="group cursor-pointer bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 relative overflow-hidden"
              >
                <div class="relative z-10">
                  <div class="w-12 h-12 rounded-2xl bg-rose-100 text-rose-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">错词本</h3>
                  <p class="text-xs text-slate-500 mb-3">攻克薄弱环节</p>
                  <div class="inline-flex items-center text-[10px] font-semibold text-rose-700 bg-rose-50 px-2 py-1 rounded-md">
                    {{ stats.errorWords || 0 }} 个错词
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 句子专区 -->
          <div>
            <h3 class="text-sm font-bold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
              <span class="w-1.5 h-1.5 rounded-full bg-indigo-500"></span>
              句子训练
            </h3>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
              <!-- 句子学习 -->
              <div 
                @click="goToSentenceLearning"
                class="group cursor-pointer bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 relative overflow-hidden"
              >
                <div class="relative z-10">
                  <div class="w-12 h-12 rounded-2xl bg-indigo-100 text-indigo-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">句子学习</h3>
                  <p class="text-xs text-slate-500 mb-3">场景例句掌握</p>
                  <div class="inline-flex items-center text-[10px] font-semibold text-indigo-700 bg-indigo-50 px-2 py-1 rounded-md">
                    {{ stats.masteredSentences || 0 }} 已掌握
                  </div>
                </div>
              </div>

              <!-- 句子听写 -->
              <div 
                @click="goToSentenceDictation"
                class="group cursor-pointer bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 relative overflow-hidden"
              >
                <div class="relative z-10">
                  <div class="w-12 h-12 rounded-2xl bg-violet-100 text-violet-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">句子听写</h3>
                  <p class="text-xs text-slate-500 mb-3">整句听力训练</p>
                  <div class="inline-flex items-center text-[10px] font-semibold text-violet-700 bg-violet-50 px-2 py-1 rounded-md">
                    进阶提升
                  </div>
                </div>
              </div>

              <!-- 错句本 -->
              <div 
                @click="goToErrorSentenceBook"
                class="group cursor-pointer bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 relative overflow-hidden"
              >
                <div class="relative z-10">
                  <div class="w-12 h-12 rounded-2xl bg-orange-100 text-orange-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">错句本</h3>
                  <p class="text-xs text-slate-500 mb-3">句子查漏补缺</p>
                  <div class="inline-flex items-center text-[10px] font-semibold text-orange-700 bg-orange-50 px-2 py-1 rounded-md">
                    {{ stats.errorSentences || 0 }} 个错句
                  </div>
                </div>
              </div>

              <!-- 积累本 -->
              <div 
                @click="goToJottings"
                class="group cursor-pointer bg-white rounded-3xl p-6 border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 relative overflow-hidden"
              >
                <div class="relative z-10">
                  <div class="w-12 h-12 rounded-2xl bg-teal-100 text-teal-600 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </div>
                  <h3 class="text-lg font-bold text-slate-900 mb-1">积累本</h3>
                  <p class="text-xs text-slate-500 mb-3">个人知识库</p>
                  <div class="inline-flex items-center text-[10px] font-semibold text-teal-700 bg-teal-50 px-2 py-1 rounded-md">
                    随手记
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 第三部分：数据详情与激励 (Grid) -->
      <section class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        <!-- 数据概览 -->
        <div class="lg:col-span-2 bg-white rounded-[2rem] border border-slate-100 shadow-sm p-6 sm:p-8">
          <div class="flex items-center justify-between mb-8">
            <h3 class="text-xl font-bold text-slate-900">数据概览</h3>
            <button @click="refreshStats" class="text-slate-400 hover:text-blue-500 transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
            </button>
          </div>
          
          <div class="grid grid-cols-2 md:grid-cols-3 gap-4 md:gap-8">
            <!-- 单词数据 -->
            <div class="space-y-4">
              <p class="text-sm font-semibold text-slate-500 uppercase tracking-wider">Words</p>
              <div class="space-y-3">
                <div class="flex justify-between items-center">
                  <span class="text-sm text-slate-600">总数</span>
                  <span class="font-bold text-slate-900">{{ stats.totalWords || 0 }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-emerald-600">已掌握</span>
                  <span class="font-bold text-emerald-600">{{ stats.masteredWords || 0 }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-rose-500">需复习</span>
                  <span class="font-bold text-rose-500">{{ stats.errorWords || 0 }}</span>
                </div>
              </div>
            </div>

            <!-- 句子数据 -->
             <div class="space-y-4">
              <p class="text-sm font-semibold text-slate-500 uppercase tracking-wider">Sentences</p>
              <div class="space-y-3">
                <div class="flex justify-between items-center">
                  <span class="text-sm text-slate-600">总数</span>
                  <span class="font-bold text-slate-900">{{ stats.totalSentences || 0 }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-indigo-600">已掌握</span>
                  <span class="font-bold text-indigo-600">{{ stats.masteredSentences || 0 }}</span>
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-orange-500">需复习</span>
                  <span class="font-bold text-orange-500">{{ stats.errorSentences || 0 }}</span>
                </div>
              </div>
            </div>

            <!-- 图表/进度圆环 (简单CSS实现) -->
            <div class="col-span-2 md:col-span-1 flex flex-col items-center justify-center p-4 bg-slate-50 rounded-2xl">
              <div class="relative w-24 h-24 mb-3">
                <svg class="w-full h-full transform -rotate-90">
                  <circle cx="48" cy="48" r="40" stroke="currentColor" stroke-width="8" fill="transparent" class="text-slate-200" />
                  <circle cx="48" cy="48" r="40" stroke="currentColor" stroke-width="8" fill="transparent" 
                    :stroke-dasharray="251.2"
                    :stroke-dashoffset="251.2 * (1 - progressPercent / 100)"
                    class="text-blue-500 transition-all duration-1000 ease-out"
                    stroke-linecap="round"
                  />
                </svg>
                <div class="absolute inset-0 flex items-center justify-center flex-col">
                  <span class="text-xl font-bold text-slate-900">{{ progressPercent }}%</span>
                </div>
              </div>
              <p class="text-xs text-slate-500 font-medium">总体进度</p>
            </div>
          </div>
        </div>

        <!-- 激励语卡片 -->
        <div class="lg:col-span-1 bg-slate-900 text-white rounded-[2rem] shadow-xl p-8 flex flex-col justify-between relative overflow-hidden min-h-[260px]">
           <div class="absolute top-0 right-0 w-32 h-32 bg-blue-500/30 rounded-full blur-2xl -mt-8 -mr-8"></div>
           <div class="absolute bottom-0 left-0 w-24 h-24 bg-purple-500/30 rounded-full blur-2xl -mb-4 -ml-4"></div>
           
           <div class="relative z-10">
             <div class="flex justify-between items-center mb-6">
                <span class="px-2 py-1 bg-white/10 rounded-md text-[10px] font-bold tracking-widest uppercase">Inspiration</span>
                <div class="flex gap-2">
                  <button @click="previousSlide" class="text-white/50 hover:text-white transition-colors">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
                  </button>
                  <button @click="nextSlide" class="text-white/50 hover:text-white transition-colors">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" /></svg>
                  </button>
                </div>
             </div>
             
             <div class="flex-1 flex items-center">
                <Transition name="fade" mode="out-in">
                  <div v-if="quotes.length > 0" :key="currentIndex" class="w-full">
                    <p class="text-xl font-serif leading-relaxed mb-4">"{{ quotes[currentIndex]?.content }}"</p>
                    <p class="text-sm text-white/60 text-right">— {{ quotes[currentIndex]?.author }}</p>
                  </div>
                  <div v-else class="w-full text-center">
                    <p class="text-lg font-medium">Loading...</p>
                  </div>
                </Transition>
             </div>
           </div>
        </div>

      </section>

    </main>

    <!-- 弹窗组件保持原有逻辑 -->
    <!-- 退出确认弹窗 -->
    <div v-if="showLogoutConfirm" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4">
      <div class="w-full max-w-sm rounded-3xl bg-white shadow-2xl p-6 animate-scale-in">
        <div class="flex items-center gap-4 mb-4">
          <div class="w-12 h-12 rounded-full bg-red-50 text-red-500 flex items-center justify-center shrink-0">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </div>
          <div>
            <h3 class="text-lg font-bold text-slate-900">确认退出？</h3>
            <p class="text-sm text-slate-500">退出后需要重新登录。</p>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button @click="closeLogoutConfirm" class="px-5 py-2.5 rounded-xl text-slate-600 font-medium hover:bg-slate-100 transition-colors">取消</button>
          <button @click="confirmLogout" class="px-5 py-2.5 rounded-xl bg-red-500 text-white font-medium hover:bg-red-600 shadow-lg shadow-red-500/30 transition-colors">退出登录</button>
        </div>
      </div>
    </div>

    <!-- 课本选择弹窗 (优化版) -->
    <div v-if="showBookSelector" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4">
      <div class="w-full max-w-2xl rounded-3xl bg-white shadow-2xl p-6 flex flex-col max-h-[85vh] animate-scale-in">
        <div class="flex justify-between items-center mb-6">
          <div>
            <h3 class="text-2xl font-bold text-slate-900">选择课本</h3>
            <p class="text-sm text-slate-500 mt-1">选择一本教材开始你的学习之旅</p>
          </div>
          <button @click="showBookSelector = false" class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100 transition-colors">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        
        <div class="flex-1 overflow-y-auto custom-scrollbar p-1">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div 
              v-for="book in books" 
              :key="book.id"
              @click="selectBook(book)"
              :class="[
                'p-5 rounded-2xl border-2 cursor-pointer transition-all duration-300 relative overflow-hidden group',
                currentBook?.id === book.id 
                  ? 'border-blue-500 bg-blue-50/50 shadow-md ring-2 ring-blue-500/20' 
                  : 'border-slate-100 hover:border-blue-300 hover:shadow-lg hover:-translate-y-1 bg-white'
              ]"
            >
              <div class="flex justify-between items-start mb-3">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-100 to-indigo-100 text-blue-600 flex items-center justify-center font-bold text-lg">
                  {{ (book.bookName || book.name || '?').charAt(0) }}
                </div>
                <div v-if="currentBook?.id === book.id" class="text-blue-500">
                  <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" /></svg>
                </div>
              </div>
              
              <h4 class="font-bold text-slate-900 mb-1 line-clamp-1" :title="book.bookName || book.name">{{ book.bookName || book.name }}</h4>
              <p class="text-xs text-slate-500 mb-4 line-clamp-2 min-h-[2.5em]">{{ book.description || '暂无描述信息' }}</p>
              
              <div class="flex items-center gap-4 text-xs font-medium text-slate-400 bg-slate-50 p-2 rounded-lg">
                <span class="flex items-center gap-1">
                  <div class="w-1.5 h-1.5 rounded-full bg-emerald-400"></div>
                  {{ book.wordCount || 0 }} 词
                </span>
                <span class="flex items-center gap-1">
                  <div class="w-1.5 h-1.5 rounded-full bg-rose-400"></div>
                  {{ book.errorWordCount || 0 }} 错
                </span>
              </div>
            </div>
            
            <!-- 添加新课本卡片 -->
            <div 
              @click="goToBooks"
              class="p-5 rounded-2xl border-2 border-dashed border-slate-200 cursor-pointer hover:border-blue-400 hover:bg-blue-50/30 transition-all duration-300 flex flex-col items-center justify-center text-slate-400 gap-3 min-h-[180px]"
            >
              <div class="w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center group-hover:bg-blue-100 transition-colors">
                <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
              </div>
              <span class="font-medium">添加更多课本</span>
            </div>
          </div>
          
          <div v-if="books.length === 0" class="text-center py-10">
            <p class="text-slate-400 mb-4">还没有任何课本，快去添加吧！</p>
            <button @click="goToBooks" class="px-6 py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors">前往课本管理</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import { useRouter } from 'vue-router'
import { useMotivation } from '@/composables/useMotivation'
import { useStudyStatStore } from '@/stores/studyStat'

const { logout } = useAuth()
const authStore = useAuthStore()
const bookStore = useBookStore()
const router = useRouter()
const studyStatStore = useStudyStatStore()

// 激励文案相关
const {
  quotes,
  currentIndex,
  loading,
  isPlaying,
  fetchQuotes,
  goToSlide,
  toggleAutoPlay,
  startAutoPlay
} = useMotivation()

// 添加手动切换方法
const nextSlide = () => {
  if (quotes.value.length > 1) {
    const nextIndex = (currentIndex.value + 1) % quotes.value.length
    goToSlide(nextIndex)
  }
}

const previousSlide = () => {
  if (quotes.value.length > 1) {
    const prevIndex = currentIndex.value === 0 ? quotes.value.length - 1 : currentIndex.value - 1
    goToSlide(prevIndex)
  }
}

// 用户信息
const user = computed(() => authStore.user)
const currentBook = computed(() => bookStore.currentBook)
const books = computed(() => bookStore.books)
const displayName = computed(() => user.value?.nickname || user.value?.username || '未命名用户')

const userAvatar = computed(() => {
  const avatar = user.value?.avatar
  if (avatar && avatar.startsWith('http')) {
    return avatar
  }
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://192.168.43.106:8080'
  return avatar ? `${baseUrl}${avatar}` : ''
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
const avatarLoadError = ref(false)
const showLogoutConfirm = ref(false)
const showBookSelector = ref(false)
const showUserMenu = ref(false)
const userMenuRef = ref(null)

// 学习统计数据
const stats = ref({
  totalWords: 0,
  masteredWords: 0,
  errorWords: 0,
  totalSentences: 0,
  masteredSentences: 0,
  errorSentences: 0,
  studyDays: 0
})

// 进度计算 - 基于总体掌握情况
const progress = computed(() => {
  const totalItems = (stats.value.totalWords || 0) + (stats.value.totalSentences || 0)
  const masteredItems = (stats.value.masteredWords || 0) + (stats.value.masteredSentences || 0)
  if (totalItems === 0) return 0
  return (masteredItems / totalItems) * 100
})
const progressPercent = computed(() => Math.round(progress.value))
const masteredCount = computed(() => (stats.value.masteredWords || 0) + (stats.value.masteredSentences || 0))
const remainingCount = computed(() => {
  const total = (stats.value.totalWords || 0) + (stats.value.totalSentences || 0)
  return Math.max(total - masteredCount.value, 0)
})

// 获取单词掌握率
const getWordMasteryRate = () => {
  if (stats.value.totalWords === 0) return 0
  return Math.round((stats.value.masteredWords / stats.value.totalWords) * 100)
}

// 获取句子掌握率
const getSentenceMasteryRate = () => {
  if (stats.value.totalSentences === 0) return 0
  return Math.round((stats.value.masteredSentences / stats.value.totalSentences) * 100)
}

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

// 获取欢迎信息
const getWelcomeMessage = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息'
  if (hour < 12) return '新的一天，新的开始'
  if (hour < 18) return '下午时光，继续加油'
  return '晚上好，坚持就是胜利'
}

// 获取用户名首字母
const getUserInitial = () => {
  const name = user.value?.nickname || user.value?.username || 'U'
  return name.charAt(0).toUpperCase()
}

const refreshStudyDuration = async () => {
  await studyStatStore.refreshStats()
}

const formatUpdateTime = (time) => {
  if (!time) return ''
  const now = new Date()
  const updateTime = new Date(time)
  const diffMs = now - updateTime
  const diffMinutes = Math.floor(diffMs / (1000 * 60))
  if (diffMinutes < 1) return '刚刚'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  const diffHours = Math.floor(diffMinutes / 60)
  if (diffHours < 24) return `${diffHours}小时前`
  return updateTime.toLocaleDateString()
}

// 加载学习统计数据
const loadLearningStats = async () => {
  if (!authStore.user?.id) {
    return
  }
  
  try {
    const bookId = currentBook.value?.id
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://192.168.43.106:8080'
    const url = bookId 
      ? `${baseUrl}/api/english/stats/${authStore.user.id}?bookId=${bookId}`
      : `${baseUrl}/api/english/stats/${authStore.user.id}`
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    
    if (response.ok) {
      const data = await response.json()
      if (data.code === 1 && data.data) {
        const statsData = data.data
        stats.value = {
          totalWords: statsData.totalWords || 0,
          masteredWords: statsData.masteredWords || 0,
          errorWords: statsData.errorWords || 0,
          totalSentences: statsData.totalSentences || 0,
          masteredSentences: statsData.masteredSentences || 0,
          errorSentences: statsData.errorSentences || 0,
          studyDays: statsData.studyDays || 0
        }
      }
    }
    
  } catch (error) {
    console.error('加载学习统计失败:', error)
    stats.value = {
      totalWords: 0,
      masteredWords: 0,
      errorWords: 0,
      totalSentences: 0,
      masteredSentences: 0,
      errorSentences: 0,
      studyDays: 0
    }
  }
}

// 刷新统计数据
const refreshStats = async () => {
  await loadLearningStats()
}

// 导航方法
const goToBooks = () => {
  router.push('/books')
}

const goToChecklist = () => {
  router.push('/checklist')
}

const goToJottings = () => {
  router.push('/jottings')
}

const goToGame = () => {
  router.push('/word/game')
}

const goToWordOption = () => {
  router.push('/word/option')
}

const goToWordLearning = () => {
  router.push('/word/learning')
}

const goToWordReview = () => {
  router.push('/word/review')
}

const goToWordDictation = () => {
  router.push('/word/dictation')
}

const goToErrorWordBook = () => {
  router.push('/word/error-book')
}

const goToSentenceLearning = () => {
  router.push('/sentence/learning')
}

const goToSentenceDictation = () => {
  router.push('/sentence/dictation')
}

const goToErrorSentenceBook = () => {
  router.push('/sentence/error-book')
}

const handleAvatarError = () => {
  avatarLoadError.value = true
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const closeUserMenu = () => {
  showUserMenu.value = false
}

const handleClickOutside = (event) => {
  if (!userMenuRef.value) return
  if (!userMenuRef.value.contains(event.target)) {
    closeUserMenu()
  }
}

const openLogoutConfirm = () => {
  closeUserMenu()
  showLogoutConfirm.value = true
}

const closeLogoutConfirm = () => {
  showLogoutConfirm.value = false
}

const confirmLogout = async () => {
  closeLogoutConfirm()
  await logout()
}

// 课本选择方法
const goToProfile = () => {
  closeUserMenu()
  router.push('/profile')
}

const selectBook = async (book) => {
  try {
    await bookStore.selectBook(book)
    showBookSelector.value = false
    // stats数据由watch(currentBook)自动加载
  } catch (error) {
    console.error('选择课本失败:', error)
  }
}

onMounted(async () => {
  document.addEventListener('click', handleClickOutside)
  // 初始化课本信息
  try {
    await bookStore.fetchBooks()
    bookStore.initializeBook()
  } catch (error) {
    console.error('初始化课本信息失败:', error)
  }
  
  await studyStatStore.init()

  // 加载激励文案
  await fetchQuotes()
  if (quotes.value.length > 1) {
    startAutoPlay()
  }
  
  // 手动触发一次stats加载
  if (currentBook.value) {
    await loadLearningStats()
  }
})

// 监听当前课本变化
watch(currentBook, async (newBook) => {
  if (newBook) {
    await loadLearningStats()
  }
}, { immediate: false })
</script>

<style scoped>
/* 激励语过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 弹窗动画 */
@keyframes scale-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.animate-scale-in {
  animation: scale-in 0.2s ease-out forwards;
}

/* 自定义滚动条 */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 20px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: #94a3b8;
}
</style>
