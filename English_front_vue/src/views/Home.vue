<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
    <!-- 半透明顶部导航 -->
    <nav class="bg-white/90 backdrop-blur-md shadow-sm border-b border-white/50 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-3 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between py-3 sm:py-4">
          <!-- 品牌区域 -->
          <div class="flex items-center gap-2 sm:gap-4 min-w-0 flex-1">
            <div class="w-10 h-10 sm:w-14 sm:h-14 rounded-xl sm:rounded-2xl bg-gradient-to-br from-blue-500 via-indigo-500 to-purple-600 flex items-center justify-center shadow-lg flex-shrink-0">
              <svg class="w-6 h-6 sm:w-8 sm:h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z"></path>
              </svg>
            </div>
            <div class="min-w-0 flex-1">
              <div class="flex items-center gap-2 sm:gap-3">
                <h1 class="text-lg sm:text-2xl md:text-3xl font-bold text-slate-900 tracking-tight truncate">轻风智语</h1>
                <span class="text-[9px] sm:text-[11px] md:text-xs uppercase tracking-[0.4em] text-slate-400 flex-shrink-0">Focus</span>
              </div>
              <div class="flex items-center gap-2 sm:gap-3 mt-0.5 sm:mt-1">
                <p class="text-xs sm:text-sm md:text-base font-semibold text-transparent bg-clip-text bg-gradient-to-r from-blue-500 to-purple-600 tracking-[0.35em] uppercase truncate">BreezeWise</p>
                <!-- <span class="px-3 py-1 rounded-full bg-slate-100 text-xs text-slate-600 tracking-wide">English Learning</span> -->
              </div>
            </div>
          </div>
          
          <!-- 右侧用户区域 -->
          <div class="flex items-center gap-1 sm:gap-2 lg:gap-3 flex-shrink-0">
            <!-- 学习进度 - 在小屏幕上隐藏 -->
            <div class="hidden xl:flex items-center gap-2 lg:gap-3 px-2 lg:px-4 py-1.5 lg:py-2 rounded-xl lg:rounded-2xl border border-slate-100 bg-white shadow-sm">
              <div class="p-1.5 lg:p-2 rounded-lg lg:rounded-xl bg-indigo-50 text-indigo-500">
                <svg class="w-3 h-3 lg:w-4 lg:h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 1.567-3 3.5S10.343 15 12 15s3-1.567 3-3.5S13.657 8 12 8zm0 0V5m0 10v4"></path>
                </svg>
              </div>
              <div>
                <p class="text-[10px] lg:text-xs text-slate-500">学习进度</p>
                <p class="text-xs lg:text-sm font-semibold text-slate-900">{{ progressPercent }}%</p>
              </div>
            </div>

            <!-- 问候语 - 在小屏幕上简化显示 -->
            <div class="hidden md:flex items-center gap-2 lg:gap-3 px-2 lg:px-4 py-1.5 lg:py-2 rounded-xl lg:rounded-2xl border border-slate-100 bg-white shadow-sm">
              <div class="p-1.5 lg:p-2 rounded-lg lg:rounded-xl bg-blue-50 text-blue-500">
                <svg class="w-3 h-3 lg:w-4 lg:h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8m-18 8h18"></path>
                </svg>
              </div>
              <div class="text-right">
                <p class="text-xs lg:text-sm font-medium text-gray-900">{{ getGreeting() }}</p>
                <p class="text-[10px] lg:text-xs text-gray-500 truncate max-w-[80px] lg:max-w-[120px]">{{ displayName }}</p>
              </div>
            </div>

            <!-- 手机端简化的问候语 -->
            <div class="flex md:hidden items-center gap-1 px-2 py-1 rounded-lg border border-slate-100 bg-white shadow-sm">
              <div class="p-1 rounded-md bg-blue-50 text-blue-500">
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8m-18 8h18"></path>
                </svg>
              </div>
              <div class="text-right">
                <p class="text-[10px] font-medium text-gray-900">{{ getGreeting() }}</p>
              </div>
            </div>

            <!-- 学习提醒按钮 - 在小屏幕上隐藏 -->
            <button class="hidden lg:inline-flex p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-full transition-colors" title="学习提醒">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-5 5v-5zM10.07 2.82l3.12 3.12M7.05 5.84l3.12 3.12M4.03 8.86l3.12 3.12M1.01 11.88l3.12 3.12"></path>
              </svg>
            </button>
            
            <!-- 用户头像和退出按钮 -->
            <div class="flex items-center gap-1 sm:gap-2">
              <div class="w-8 h-8 sm:w-11 sm:h-11 md:w-12 md:h-12 rounded-full border-2 border-white shadow-lg overflow-hidden bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center text-white font-semibold text-[10px] sm:text-sm">
                <img
                  v-if="userAvatar && !avatarLoadError"
                  :src="userAvatar"
                  :alt="displayName || '用户头像'"
                  class="w-full h-full object-cover"
                  @error="handleAvatarError"
                />
                <span v-else>{{ getUserInitial() }}</span>
              </div>
              <button
                @click="openLogoutConfirm"
                class="p-1.5 sm:p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-full transition-colors"
                title="退出登录"
              >
                <svg class="w-4 h-4 sm:w-5 sm:h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 退出确认弹窗 -->
    <div
      v-if="showLogoutConfirm"
      class="fixed inset-0 z-[60] flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4"
    >
      <div class="w-full max-w-sm rounded-3xl bg-white shadow-2xl p-6">
        <div class="flex items-start gap-3">
          <div class="p-3 rounded-2xl bg-red-50 text-red-500">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v4m0 4h.01M5.455 5.455l13.09 13.09M12 5c3.866 0 7 3.134 7 7 0 1.31-.374 2.536-1.02 3.58M12 19c-3.866 0-7-3.134-7-7 0-1.31.374-2.536 1.02-3.58"></path>
            </svg>
          </div>
          <div>
            <h3 class="text-lg font-semibold text-slate-900">确认退出登录？</h3>
            <p class="text-sm text-slate-500 mt-1">退出后需要重新登录才可继续学习，确认要离开当前账号吗？</p>
          </div>
        </div>
        <div class="mt-6 flex items-center justify-end gap-3">
          <button
            @click="closeLogoutConfirm"
            class="px-4 py-2 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-100 transition-colors"
          >
            取消
          </button>
          <button
            @click="confirmLogout"
            class="px-5 py-2 rounded-xl text-sm font-semibold text-white bg-gradient-to-r from-red-500 to-pink-500 shadow-lg shadow-red-500/30 hover:from-red-600 hover:to-pink-600 transition-colors"
          >
            确认退出
          </button>
        </div>
      </div>
    </div>

    <!-- 课本选择器弹窗 -->
    <div
      v-if="showBookSelector"
      class="fixed inset-0 z-[60] flex items-center justify-center bg-slate-900/40 backdrop-blur-sm px-4"
    >
      <div class="w-full max-w-md rounded-3xl bg-white shadow-2xl p-6">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-slate-900">选择课本</h3>
          <button
            @click="showBookSelector = false"
            class="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-full transition-colors"
          >
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>
        
        <div class="space-y-3 max-h-80 overflow-y-auto">
          <div
            v-for="book in books"
            :key="book.id"
            @click="selectBook(book)"
            :class="[
              'p-4 rounded-xl border-2 cursor-pointer transition-all duration-200',
              currentBook?.id === book.id
                ? 'border-blue-500 bg-blue-50'
                : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'
            ]"
          >
            <div class="flex items-center justify-between">
              <div class="flex-1">
                <h4 class="font-medium text-slate-900">{{ book.name }}</h4>
                <p class="text-sm text-slate-500 mt-1">{{ book.description || '暂无描述' }}</p>
                <div class="flex items-center gap-4 mt-2 text-xs text-slate-400">
                  <span>{{ book.wordCount || 0 }} 个单词</span>
                  <span>{{ book.errorWordCount || 0 }} 个错词</span>
                </div>
              </div>
              <div v-if="currentBook?.id === book.id" class="ml-3">
                <div class="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
                  <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                  </svg>
                </div>
              </div>
            </div>
          </div>
          
          <div v-if="books.length === 0" class="text-center py-8">
            <svg class="w-12 h-12 text-slate-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z"></path>
            </svg>
            <p class="text-slate-500">暂无课本数据</p>
            <button
              @click="goToBooks"
              class="mt-3 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
            >
              去选择课本
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 顶部概览 + 激励语句 -->
      <div class="mb-12">
        <div class="grid grid-cols-1 gap-6 lg:grid-cols-12 items-stretch">
          <!-- 学习数据统计 -->
          <section class="lg:col-span-7">
            <div class="h-full rounded-3xl bg-white/90 backdrop-blur border border-white/50 shadow-xl p-5 sm:p-6 lg:p-8 flex flex-col">
              <div class="flex flex-wrap items-center justify-between gap-3 sm:gap-4">
                <div class="flex-1 min-w-0">
                  <p class="text-[10px] sm:text-xs uppercase tracking-[0.35em] text-slate-400">Overview</p>
                  <h2 class="text-xl sm:text-2xl font-bold text-slate-900">学习概览</h2>
                  <p class="text-xs sm:text-sm text-slate-500 mt-1">我有自己的学习节奏</p>
                </div>
                
                <!-- 课本选择器和累计学习几天小组件 -->
                <div class="flex items-center gap-2">
                  
                  <!-- 累计学习几天小组件 -->
                  <div class="flex items-center gap-2 px-2 sm:px-3 py-2 rounded-xl border border-indigo-200 bg-indigo-50 shadow-sm">
                    <div class="p-1.5 rounded-lg bg-indigo-100 text-indigo-600 flex-shrink-0">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                      </svg>
                    </div>
                    <div class="min-w-0 flex-1">
                      <p class="text-xs text-indigo-600">学习天数</p>
                      <p class="text-sm font-semibold text-indigo-900">{{ stats.studyDays || 0 }} 天</p>
                    </div>
                  </div>
                  
                  <button 
                    @click="refreshStats"
                    class="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-full transition-colors"
                    title="刷新数据"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
                    </svg>
                  </button>
                </div>
              </div>

              <div class="mt-6 grid grid-cols-2 sm:grid-cols-3 gap-3 sm:gap-4">
                <div class="rounded-2xl border border-slate-100 bg-slate-50/60 p-4 sm:p-5">
                  <p class="text-xs text-slate-500 mb-1">总单词</p>
                  <p class="text-xl sm:text-2xl font-semibold text-slate-900">{{ stats.totalWords || 0 }}</p>
                </div>
                <div class="rounded-2xl border border-slate-100 bg-slate-50/60 p-4 sm:p-5">
                  <p class="text-xs text-slate-500 mb-1">掌握单词</p>
                  <p class="text-xl sm:text-2xl font-semibold text-emerald-600">{{ stats.masteredWords || 0 }}</p>
                  <span class="text-[11px] sm:text-xs text-emerald-500">{{ getWordMasteryRate() }}%</span>
                </div>
                <div class="rounded-2xl border border-slate-100 bg-slate-50/60 p-4 sm:p-5">
                  <p class="text-xs text-slate-500 mb-1">错误单词</p>
                  <p class="text-xl sm:text-2xl font-semibold text-rose-500">{{ stats.errorWords || 0 }}</p>
                </div>
                <div class="rounded-2xl border border-slate-100 bg-slate-50/60 p-4 sm:p-5">
                  <p class="text-xs text-slate-500 mb-1">总句子</p>
                  <p class="text-xl sm:text-2xl font-semibold text-indigo-600">{{ stats.totalSentences || 0 }}</p>
                </div>
                <div class="rounded-2xl border border-slate-100 bg-slate-50/60 p-4 sm:p-5">
                  <p class="text-xs text-slate-500 mb-1">掌握句子</p>
                  <p class="text-xl sm:text-2xl font-semibold text-sky-600">{{ stats.masteredSentences || 0 }}</p>
                  <span class="text-[11px] sm:text-xs text-sky-500">{{ getSentenceMasteryRate() }}%</span>
                </div>
                <div class="rounded-2xl border border-slate-100 bg-slate-50/60 p-4 sm:p-5">
                  <p class="text-xs text-slate-500 mb-1">错误句子</p>
                  <p class="text-xl sm:text-2xl font-semibold text-orange-500">{{ stats.errorSentences || 0 }}</p>
                </div>
                <!-- 课本选择器 - 替换累计学习几天的位置，保持相同样式 -->
                <div class="col-span-2 sm:col-span-3 rounded-2xl bg-gradient-to-r from-indigo-500 to-purple-600 text-white p-4 sm:p-5 flex items-center justify-between shadow-lg gap-3">
                  <div class="flex items-center gap-3 flex-1">
                    <div class="p-2 rounded-xl bg-white/20 flex-shrink-0">
                      <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253z"></path>
                      </svg>
                    </div>
                    <div class="min-w-0 flex-1">
                      <p class="text-[11px] sm:text-xs uppercase tracking-[0.3em] text-white/70">Current Book</p>
                      <p class="text-lg sm:text-xl md:text-2xl font-semibold truncate">
                        {{ currentBook?.name ? `当前课本：${currentBook.name}` : '未选择' }}
                      </p>
                    </div>
                  </div>
                  <div class="text-xs sm:text-sm text-white/80 text-right flex-shrink-0">
                    <button 
                      @click="showBookSelector = true"
                      class="px-3 py-1.5 bg-white/20 hover:bg-white/30 rounded-lg transition-colors"
                    >
                      切换课本
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- 激励语句区域 -->
          <section class="lg:col-span-5">
            <div class="h-full rounded-3xl bg-slate-900 text-white p-6 sm:p-8 relative overflow-hidden shadow-2xl">
              <div class="absolute inset-0 opacity-40 pointer-events-none">
                <div class="absolute -top-10 -right-10 w-40 h-40 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full blur-3xl"></div>
                <div class="absolute bottom-0 left-4 w-32 h-32 bg-gradient-to-tr from-cyan-400 to-blue-500 rounded-full blur-2xl"></div>
              </div>

              <div class="relative z-10 flex h-full flex-col">
                <div class="flex flex-wrap items-center justify-between gap-2 sm:gap-4">
                  <div>
                    <p class="text-[10px] sm:text-xs uppercase tracking-[0.35em] text-white/50">Inspiration</p>
                    <h3 class="text-base sm:text-lg font-semibold">今日激励</h3>
                  </div>
                  <span class="text-xs sm:text-sm text-white/60">{{ quotes.length }} 条文案</span>
                </div>

                <div class="flex-1 flex items-center justify-center text-center">
                  <div v-if="loading" class="text-center py-6">
                    <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
                    <p class="text-white/70 mt-4">正在加载激励语句...</p>
                  </div>

                  <div v-else-if="quotes.length > 0" class="w-full">
                    <div class="relative min-h-[120px] sm:min-h-[140px] flex items-center justify-center">
                      <Transition
                        v-for="(quote, index) in quotes"
                        :key="quote.id"
                        name="quote-slide"
                        mode="out-in"
                      >
                        <div v-if="index === currentIndex" class="absolute inset-0 flex items-center justify-center px-4">
                          <div class="max-w-xl mx-auto">
                            <h2 class="text-xl sm:text-2xl md:text-3xl font-semibold leading-snug text-white mb-4">
                              “{{ quote.content }}”
                            </h2>
                            <p class="text-xs sm:text-sm text-white/70">— {{ quote.author }}</p>
                          </div>
                        </div>
                      </Transition>
                    </div>
                  </div>

                  <div v-else class="w-full">
                    <h2 class="text-2xl font-semibold mb-4">“今天也要加油学习英语！”</h2>
                    <p class="text-white/70">— 坚持就是胜利</p>
                  </div>
                </div>

                <div v-if="quotes.length > 1" class="mt-6 flex flex-col sm:flex-row items-center justify-between gap-4">
                  <div class="flex items-center space-x-1.5 sm:space-x-2">
                    <button
                      v-for="(quote, index) in quotes"
                      :key="quote.id"
                      @click="goToSlide(index)"
                      :class="[
                        'h-1.5 rounded-full transition-all duration-300',
                        index === currentIndex ? 'w-6 bg-white' : 'w-3 bg-white/40 hover:bg-white/70'
                      ]"
                    />
                  </div>
                  <div class="flex items-center gap-2 sm:gap-3">
                    <button
                      @click="previousSlide"
                      class="p-2.5 sm:p-3 rounded-full border border-white/30 text-white hover:bg-white/10 transition"
                      title="上一条"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                      </svg>
                    </button>
                    <button
                      @click="toggleAutoPlay"
                      class="p-2.5 sm:p-3 rounded-full border border-white/30 text-white hover:bg-white/10 transition"
                      :title="isPlaying ? '暂停轮播' : '开始轮播'"
                    >
                      <svg v-if="isPlaying" class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                        <path d="M6 4h4v16H6V4zm8 0h4v16h-4V4z"/>
                      </svg>
                      <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                        <path d="M8 5v14l11-7z"/>
                      </svg>
                    </button>
                    <button
                      @click="nextSlide"
                      class="p-2.5 sm:p-3 rounded-full border border-white/30 text-white hover:bg-white/10 transition"
                      title="下一条"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>

        <!-- 视觉引导 - 开始学习按钮 -->
        <!-- <div class="text-center mt-8 sm:mt-10">
          <button 
            @click="currentBook ? goToWordLearning() : goToBooks()"
            class="inline-flex items-center px-8 py-4 bg-gradient-to-r from-blue-500 to-purple-600 text-white font-semibold text-lg rounded-2xl shadow-xl hover:shadow-2xl hover:from-blue-600 hover:to-purple-700 transform hover:scale-105 transition-all duration-300 group"
          >
            <svg class="w-6 h-6 mr-3 group-hover:animate-pulse" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
            </svg>
            {{ currentBook ? '开始学习' : '选择课本开始学习' }}
          </button>
        </div> -->
      </div>

      <!-- 欢迎模块 - 简约版 -->
      <div class="text-center mb-12">
        <!-- <h2 class="text-3xl md:text-4xl font-bold text-gray-900 mb-3">
          {{ getWelcomeMessage() }}
        </h2>
        <p class="text-lg text-gray-600 mb-8 max-w-2xl mx-auto">
          让每一次学习都充满成就感
        </p> -->
        
        <!-- 主要行动按钮 -->
        <div class="flex flex-col sm:flex-row gap-3 sm:gap-4 justify-center items-center mb-12 max-w-lg mx-auto">
          <button 
            @click="currentBook ? goToWordLearning() : goToBooks()"
            class="w-full sm:w-auto inline-flex items-center justify-center px-6 sm:px-10 py-4 sm:py-5 bg-gradient-to-r from-blue-500 to-purple-600 text-white font-bold text-lg sm:text-xl rounded-2xl shadow-2xl hover:shadow-3xl hover:from-blue-600 hover:to-purple-700 transform hover:scale-105 transition-all duration-300 group"
          >
            <svg class="w-6 h-6 sm:w-7 sm:h-7 mr-3 sm:mr-4 group-hover:animate-pulse" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
            </svg>
            <span class="truncate">{{ currentBook ? '开始学习' : '选择课本' }}</span>
          </button>
          
          <button 
            @click="goToWordReview"
            class="w-full sm:w-auto inline-flex items-center justify-center px-6 sm:px-8 py-4 sm:py-5 bg-white text-gray-700 font-semibold text-base sm:text-lg rounded-2xl shadow-lg hover:shadow-xl border-2 border-gray-100 hover:border-gray-200 transform hover:scale-105 transition-all duration-300"
          >
            <svg class="w-5 h-5 sm:w-6 sm:h-6 mr-2 sm:mr-3 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            <span class="truncate">复习巩固</span>
          </button>
        </div>
      </div>

      <!-- 核心功能区域 - 精简版 -->
      <div class="max-w-5xl mx-auto">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-6">

          <!-- 学习清单 -->
          <div 
            class="group cursor-pointer transform hover:scale-105 transition-all duration-300"
            @click="goToChecklist"
          >
            <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg hover:shadow-xl transition-all duration-300 border border-white/50 text-center">
              <div class="w-16 h-16 bg-gradient-to-br from-orange-400 to-orange-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-xl transition-shadow mb-4 mx-auto">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4"></path>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">学习清单</h3>
              <p class="text-sm text-gray-600">管理任务</p>
            </div>
          </div>

          <!-- 单词学习 -->
          <div 
            class="group cursor-pointer transform hover:scale-105 transition-all duration-300"
            @click="currentBook ? goToWordLearning() : goToBooks()"
          >
            <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg hover:shadow-xl transition-all duration-300 border border-white/50 text-center">
              <div class="w-16 h-16 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-xl transition-shadow mb-4 mx-auto">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">单词学习</h3>
              <p class="text-sm text-gray-600">掌握新词汇</p>
            </div>
          </div>

          <!-- 句子学习 -->
          <div 
            class="group cursor-pointer transform hover:scale-105 transition-all duration-300"
            @click="goToSentenceLearning"
          >
            <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg hover:shadow-xl transition-all duration-300 border border-white/50 text-center">
              <div class="w-16 h-16 bg-gradient-to-br from-indigo-400 to-indigo-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-xl transition-shadow mb-4 mx-auto">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">句子学习</h3>
              <p class="text-sm text-gray-600">提升表达力</p>
            </div>
          </div>

          <!-- 积累本 -->
          <div 
            class="group cursor-pointer transform hover:scale-105 transition-all duration-300"
            @click="goToJottings"
          >
            <div class="bg-white/80 backdrop-blur-sm rounded-3xl p-6 shadow-lg hover:shadow-xl transition-all duration-300 border border-white/50 text-center">
              <div class="w-16 h-16 bg-gradient-to-br from-teal-400 to-teal-600 rounded-2xl flex items-center justify-center shadow-lg group-hover:shadow-xl transition-shadow mb-4 mx-auto">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                </svg>
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">积累本</h3>
              <p class="text-sm text-gray-600">记录心得</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 今日推荐 -->
      <div class="mt-16 mb-16">
        <h3 class="text-2xl font-bold text-gray-900 mb-8 text-center">今日推荐</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <!-- 推荐卡片1 -->
          <div class="bg-white/70 backdrop-blur-sm rounded-2xl p-6 shadow-lg hover:shadow-xl transition-all duration-300 border border-white/20">
            <div class="flex items-center mb-4">
              <div class="w-12 h-12 bg-gradient-to-br from-blue-400 to-blue-600 rounded-xl flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                </svg>
              </div>
              <div>
                <h4 class="text-lg font-semibold text-gray-900">快速复习</h4>
                <p class="text-sm text-gray-600">基于记忆曲线的智能复习</p>
              </div>
            </div>
            <p class="text-gray-700 mb-4">根据您的学习历史，为您推荐最需要复习的内容</p>
            <button 
              @click="goToWordReview"
              class="w-full bg-gradient-to-r from-blue-500 to-blue-600 text-white py-2 px-4 rounded-xl hover:from-blue-600 hover:to-blue-700 transition-all duration-200 font-medium"
            >
              开始复习
            </button>
          </div>

          <!-- 推荐卡片2 -->
          <div class="bg-white/70 backdrop-blur-sm rounded-2xl p-6 shadow-lg hover:shadow-xl transition-all duration-300 border border-white/20">
            <div class="flex items-center mb-4">
              <div class="w-12 h-12 bg-gradient-to-br from-green-400 to-green-600 rounded-xl flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
                </svg>
              </div>
              <div>
                <h4 class="text-lg font-semibold text-gray-900">新词学习</h4>
                <p class="text-sm text-gray-600">探索新的词汇世界</p>
              </div>
            </div>
            <p class="text-gray-700 mb-4">学习新单词，扩展您的词汇量，提升英语水平</p>
            <button 
              @click="currentBook ? goToWordLearning() : goToBooks()"
              class="w-full bg-gradient-to-r from-green-500 to-green-600 text-white py-2 px-4 rounded-xl hover:from-green-600 hover:to-green-700 transition-all duration-200 font-medium"
            >
              学习新词
            </button>
          </div>

          <!-- 推荐卡片3 -->
          <div class="bg-white/70 backdrop-blur-sm rounded-2xl p-6 shadow-lg hover:shadow-xl transition-all duration-300 border border-white/20">
            <div class="flex items-center mb-4">
              <div class="w-12 h-12 bg-gradient-to-br from-purple-400 to-purple-600 rounded-xl flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path>
                </svg>
              </div>
              <div>
                <h4 class="text-lg font-semibold text-gray-900">听写练习</h4>
                <p class="text-sm text-gray-600">提升听力和拼写能力</p>
              </div>
            </div>
            <p class="text-gray-700 mb-4">通过听写练习，同时提升听力理解和拼写准确性</p>
            <button 
              @click="currentBook ? goToWordDictation() : goToBooks()"
              class="w-full bg-gradient-to-r from-purple-500 to-purple-600 text-white py-2 px-4 rounded-xl hover:from-purple-600 hover:to-purple-700 transition-all duration-200 font-medium"
            >
              开始听写
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import { useRouter } from 'vue-router'
import { useMotivation } from '@/composables/useMotivation'

const { logout } = useAuth()
const authStore = useAuthStore()
const bookStore = useBookStore()
const router = useRouter()

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
const avatarLoadError = ref(false)
const showLogoutConfirm = ref(false)
const showBookSelector = ref(false)

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

// 进度计算 - 基于总体掌握情况（用于其他地方可能需要）
const progress = computed(() => {
  const totalItems = (stats.value.totalWords || 0) + (stats.value.totalSentences || 0)
  const masteredItems = (stats.value.masteredWords || 0) + (stats.value.masteredSentences || 0)
  if (totalItems === 0) return 0
  return (masteredItems / totalItems) * 100
})
const progressPercent = computed(() => Math.round(progress.value))

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
  if (hour < 18) return '继续加油学习'
  return '今天学得怎么样？'
}

// 获取用户名首字母
const getUserInitial = () => {
  const name = user.value?.nickname || user.value?.username || 'U'
  return name.charAt(0).toUpperCase()
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
        // 直接使用后端返回的原始数据，不进行合并
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
    // 使用默认值
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

const goToWordLearning = () => {
  router.push('/word/learning')
}

const goToWordReview = () => {
  router.push('/word/review')
}

const goToWordDictation = () => {
  router.push('/word/dictation')
}

const goToSentenceLearning = () => {
  router.push('/sentence/learning')
}

const handleAvatarError = () => {
  avatarLoadError.value = true
}

const openLogoutConfirm = () => {
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
const selectBook = async (book) => {
  try {
    await bookStore.selectBook(book)
    showBookSelector.value = false
    // 注意：stats数据由watch(currentBook)自动加载，无需重复请求
  } catch (error) {
    console.error('选择课本失败:', error)
    // 这里可以添加错误提示
  }
}

onMounted(async () => {
  // 初始化课本信息
  try {
    await bookStore.fetchBooks()
    bookStore.initializeBook()
  } catch (error) {
    console.error('初始化课本信息失败:', error)
  }
  
  // 加载激励文案
  await fetchQuotes()
  if (quotes.value.length > 1) {
    startAutoPlay()
  }
  
  // 手动触发一次stats加载，确保初始化时有数据
  if (currentBook.value) {
    await loadLearningStats()
  }
})

// 监听当前课本变化，重新加载统计数据
watch(currentBook, async (newBook) => {
  if (newBook) {
    await loadLearningStats()
  }
}, { immediate: false })
</script>

<style scoped>
/* 激励语句轮播动画 - 增强版 */
.quote-slide-enter-active,
.quote-slide-leave-active {
  transition: all 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.quote-slide-enter-from {
  opacity: 0;
  transform: translateY(30px) scale(0.95);
}

.quote-slide-leave-to {
  opacity: 0;
  transform: translateY(-30px) scale(0.95);
}

/* 保留原有的淡入淡出动画作为备用 */
.quote-fade-enter-active,
.quote-fade-leave-active {
  transition: all 0.5s ease-in-out;
}

.quote-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.quote-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 自定义动画 */
@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}

/* 渐变背景动画 */
@keyframes gradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.animate-gradient {
  background-size: 200% 200%;
  animation: gradient 15s ease infinite;
}

/* 激励语句区域特殊效果 */
@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.animate-shimmer {
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  background-size: 200% 100%;
  animation: shimmer 3s ease-in-out infinite;
}

/* 按钮悬停效果增强 */
.group:hover .group-hover\:animate-pulse {
  animation: pulse 1s ease-in-out infinite;
}

/* 响应式字体大小调整 */
@media (max-width: 640px) {
  .text-2xl.md\:text-4xl.lg\:text-5xl {
    font-size: 1.5rem;
    line-height: 2rem;
  }
}

@media (min-width: 641px) and (max-width: 1024px) {
  .text-2xl.md\:text-4xl.lg\:text-5xl {
    font-size: 2.25rem;
    line-height: 2.5rem;
  }
}
</style>