<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden text-slate-800">
    <!-- 背景装饰 -->
    <div class="absolute inset-x-0 top-0 h-64 bg-gradient-to-b from-orange-50/80 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 right-0 w-80 h-80 bg-amber-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-40 -left-24 w-64 h-64 bg-yellow-100/40 rounded-full blur-3xl pointer-events-none"></div>

    <!-- 顶部导航 -->
    <nav class="relative z-10 px-4 sm:px-6 lg:px-8 h-16 sm:h-20 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button
          @click="goBack"
          class="p-2 -ml-2 text-slate-500 hover:text-slate-900 hover:bg-white/60 rounded-xl transition-all duration-200"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <div class="flex flex-col">
          <h1 class="text-lg font-bold text-slate-900 tracking-tight">其他轮次复习</h1>
          <button
            @click="toggleMode"
            class="flex items-center gap-2 text-xs text-slate-500 hover:text-slate-900 transition-colors mt-0.5"
          >
            <span class="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
            <span>{{ reviewMode === 'en2cn' ? '英译汉' : '汉译英' }}</span>
            <svg class="w-3 h-3 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
            </svg>
          </button>
        </div>
      </div>

      <div class="flex items-center gap-3">
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>当前复习轮次</span>
          <span class="font-bold text-amber-600 truncate">{{ currentRoundLabel }}</span>
        </div>
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600 max-w-[220px]">
          <span>当前课本</span>
          <span class="font-bold text-amber-600 truncate" :title="currentBookName">{{ currentBookName }}</span>
        </div>
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>当前范围</span>
          <span class="text-amber-600 font-bold">{{ rangeText }}</span>
        </div>
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>进度</span>
          <span class="text-amber-600 font-bold">{{ totalWords ? currentIndex + 1 : 0 }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ totalWords }}</span>
        </div>
        <button
          @click="openRangeModal"
          class="flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-slate-600 hover:text-amber-600 hover:border-amber-200 transition-all active:scale-95"
          title="设置范围"
        >
          <span class="hidden sm:inline">修改学习范围</span>
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </button>
      </div>
    </nav>

    <!-- 参数与提示 -->
    <main class="relative z-10 flex-1 w-full max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 pb-10">
      <div
        v-if="trackerActive"
        class="sm:hidden mb-3 px-4 py-3 rounded-2xl border border-amber-100 bg-white/80 flex items-center justify-between text-xs font-semibold text-amber-600"
      >
        <span>计时中</span>
        <span class="font-mono text-base text-slate-900">{{ trackerElapsed }}</span>
      </div>
      <div
        v-if="queryError"
        class="mt-4 px-4 py-3 rounded-2xl border border-amber-200 bg-amber-50 text-amber-800 text-sm flex items-start gap-2"
      >
        <svg class="w-5 h-5 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M12 2a10 10 0 100 20 10 10 0 000-20z" />
        </svg>
        <span>{{ queryError }}</span>
      </div>

      <!-- Loading -->
      <div
        v-if="initializing || loading"
        class="flex-1 flex flex-col items-center justify-center min-h-[360px] text-center"
      >
        <div class="w-14 h-14 border-4 border-amber-100 border-t-amber-500 rounded-full animate-spin mb-4"></div>
        <p class="text-slate-500 text-sm font-medium animate-pulse">正在加载复习数据...</p>
      </div>

      <!-- Empty -->
      <div
        v-else-if="!currentWord"
        class="flex-1 flex flex-col items-center justify-center min-h-[360px] text-center"
      >
        <div class="w-20 h-20 bg-white border border-slate-100 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
        </div>
        <h3 class="text-xl font-bold text-slate-900 mb-2">暂无可复习的单词</h3>
        <p class="text-slate-500 mb-6 max-w-xs mx-auto">请确认参数范围或点击下方按钮重新拉取数据。</p>
        <button
          @click="reloadWithSameRange"
          class="px-8 py-3 bg-gradient-to-r from-amber-500 to-orange-500 text-white rounded-xl font-semibold shadow-lg shadow-amber-400/40 hover:-translate-y-0.5 transition-all"
        >
          重新加载数据
        </button>
      </div>

      <!-- Review card -->
      <div
        v-else
        class="flex-1 flex flex-col justify-center py-6 sm:py-10"
      >
        <!-- 进度条 (移动端) -->
        <div class="sm:hidden mb-6 px-1">
          <div class="flex justify-between text-xs font-medium text-slate-500 mb-2">
            <span>当前进度</span>
            <span>{{ progressPercent }}%</span>
          </div>
          <div class="h-2 bg-slate-100 rounded-full overflow-hidden">
            <div
              class="h-full bg-amber-500 rounded-full transition-all duration-300 ease-out"
              :style="{ width: `${progressPercent}%` }"
            ></div>
          </div>
        </div>

        <div class="relative group w-full max-w-2xl mx-auto">
          <div class="relative bg-white rounded-[2.5rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500">
            <div class="absolute top-6 right-6 z-10 flex gap-2">
              <span
                v-if="currentWord.isGrasp === 1"
                class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border bg-emerald-50 text-emerald-600 border-emerald-100"
              >
                已掌握
              </span>
              <span
                v-if="currentWord.isGrasp === 2"
                class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border bg-rose-50 text-rose-600 border-rose-100"
              >
                易错词
              </span>
            </div>

            <div class="p-8 sm:p-12 flex flex-col items-center text-center">
              <!-- 英译汉 -->
              <template v-if="reviewMode === 'en2cn'">
                <h2 class="text-4xl sm:text-6xl font-bold text-slate-900 mb-4 tracking-tight">{{ currentWord.word }}</h2>
                <div class="flex items-center gap-3 mb-10">
                  <span class="text-xl text-slate-500 font-serif italic">
                    /{{ currentWord.phonetic || currentWord.pronounce || '...' }}/
                  </span>
                  <button
                    @click="playPronunciation"
                    class="p-2 rounded-full bg-amber-50 text-amber-600 hover:bg-amber-100 hover:scale-110 transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-amber-500"
                    title="播放发音 (Space)"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                    </svg>
                  </button>
                </div>

                <div class="w-full relative min-h-[88px] flex items-center justify-center">
                  <div
                    v-if="showAnswer"
                    class="w-full text-lg sm:text-2xl text-slate-700 font-medium leading-relaxed px-6 py-4 rounded-2xl bg-amber-50 border border-amber-100/50 animate-fade-in"
                  >
                    {{ currentWord.chinese || '暂无释义' }}
                  </div>
                  <button
                    v-else
                    @click="toggleAnswer"
                    class="group flex items-center gap-2 px-6 py-3 rounded-xl bg-slate-50 hover:bg-slate-100 border border-slate-200 text-slate-500 font-medium transition-all hover:scale-105 active:scale-95"
                  >
                    <svg class="w-5 h-5 text-slate-400 group-hover:text-amber-500 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                    <span>点击显示释义</span>
                  </button>
                </div>
              </template>

              <!-- 汉译英 -->
              <template v-else>
                <div class="mb-10">
                  <h2 class="text-3xl sm:text-4xl font-bold text-slate-900 leading-relaxed px-4">
                    {{ currentWord.chinese || '暂无释义' }}
                  </h2>
                </div>
                <div class="w-full relative min-h-[120px] flex items-center justify-center">
                  <div v-if="showAnswer" class="flex flex-col items-center animate-fade-in">
                    <h2 class="text-4xl sm:text-5xl font-bold text-amber-600 mb-4 tracking-tight">
                      {{ currentWord.word }}
                    </h2>
                    <div class="flex items-center gap-3">
                      <span class="text-xl text-slate-500 font-serif italic">
                        /{{ currentWord.phonetic || currentWord.pronounce || '...' }}/
                      </span>
                      <button
                        @click="playPronunciation"
                        class="p-2 rounded-full bg-amber-50 text-amber-600 hover:bg-amber-100 transition-all"
                      >
                        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                        </svg>
                      </button>
                    </div>
                  </div>
                  <button
                    v-else
                    @click="toggleAnswer"
                    class="group flex items-center gap-2 px-6 py-3 rounded-xl bg-slate-50 hover:bg-slate-100 border border-slate-200 text-slate-500 font-medium transition-all hover:scale-105 active:scale-95"
                  >
                    <svg class="w-5 h-5 text-slate-400 group-hover:text-amber-500 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                    <span>点击显示单词</span>
                  </button>
                </div>
              </template>
            </div>

            <!-- 操作按钮 -->
            <div class="bg-slate-50/50 border-t border-slate-100 p-6 flex flex-col gap-4">
              <div class="grid grid-cols-2 gap-4">
                <button
                  @click="prevWord"
                  :disabled="!hasPrev"
                  class="flex items-center justify-center gap-2 py-3.5 rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 hover:border-slate-300 active:scale-95 shadow-sm"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                  </svg>
                  上一个
                </button>
                <button
                  @click="nextWord"
                  :disabled="!hasNext"
                  class="flex items-center justify-center gap-2 py-3.5 rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed bg-gradient-to-r from-amber-500 to-orange-600 text-white shadow-lg shadow-amber-500/30 hover:shadow-amber-500/40 hover:-translate-y-0.5 active:scale-95"
                >
                  下一个
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                </button>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <button
                  @click="markAsGrasped"
                  :disabled="markingGrasped"
                  class="flex items-center justify-center gap-2 py-3 rounded-xl font-bold transition-all active:scale-95 border-2 border-emerald-100 bg-emerald-50 text-emerald-600 hover:bg-emerald-100 hover:border-emerald-200 disabled:opacity-60"
                >
                  <span v-if="!markingGrasped">标记掌握</span>
                  <span v-else class="animate-pulse">标记中...</span>
                </button>
                <button
                  @click="markAsError"
                  :disabled="markingError"
                  class="flex items-center justify-center gap-2 py-3 rounded-xl font-bold transition-all active:scale-95 border-2 border-rose-100 bg-rose-50 text-rose-600 hover:bg-rose-100 hover:border-rose-200 disabled:opacity-60"
                >
                  <span v-if="!markingError">加入错词本</span>
                  <span v-else class="animate-pulse">标记中...</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 复习记录与轮次操作（移动到单词列表下方） -->
        <div class="mt-6">
          <div
            v-if="studyRecords.length"
            class="bg-white rounded-2xl border border-slate-100 shadow-sm p-6 space-y-5"
          >
            <div class="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
              <!-- <div class="flex-1">
                <p class="text-xs font-semibold text-slate-400 uppercase tracking-widest mb-2">关联记录</p>
                <select
                  v-model.number="selectedRecordId"
                  class="w-full px-4 py-3 border border-slate-200 rounded-xl bg-slate-50 focus:outline-none focus:ring-2 focus:ring-amber-400 text-sm font-medium text-slate-700"
                >
                  <option
                    v-for="option in recordSelectOptions"
                    :key="option.value"
                    :value="option.value"
                  >
                    {{ option.label }}
                  </option>
                </select>
              </div>
              <div class="flex-1">
                <p class="text-xs font-semibold text-slate-400 uppercase tracking-widest mb-2">复习轮次</p>
                <select
                  v-model.number="selectedRound"
                  class="w-full px-4 py-3 border border-slate-200 rounded-xl bg-slate-50 focus:outline-none focus:ring-2 focus:ring-amber-400 text-sm font-medium text-slate-700"
                >
                  <option
                    v-for="option in roundOptions"
                    :key="option.value"
                    :value="option.value"
                    :disabled="option.disabled"
                  >
                    第{{ option.value }}轮{{ option.disabled ? '（待上一轮完成）' : '' }}
                  </option>
                </select>
              </div> -->
              <div class="flex-1 w-full">
                <!-- <p class="text-xs font-semibold text-slate-400 uppercase tracking-widest mb-2">操作</p> -->
                <button
                  @click="markReviewRoundComplete"
                  :disabled="!canMarkRound || markingReviewRound"
                  class="w-full px-4 py-3 rounded-xl font-semibold text-white bg-gradient-to-r from-emerald-500 to-lime-500 shadow-lg shadow-emerald-500/30 hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <span v-if="markingReviewRound">标记中...</span>
                  <span v-else>标记第 {{ selectedRound }} 轮已完成</span>
                </button>
              </div>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
              <div
                v-for="item in displayRoundStatus"
                :key="item.key"
                class="px-4 py-3 rounded-2xl border"
                :class="item.completed ? 'border-emerald-100 bg-emerald-50' : 'border-slate-200 bg-slate-50'"
              >
                <p class="text-xs font-semibold text-slate-500 mb-1">{{ item.title }}</p>
                <template v-if="item.detail?.length">
                  <p
                    v-for="sub in item.detail"
                    :key="sub.label"
                    class="text-sm font-semibold"
                    :class="sub.completed ? 'text-emerald-600' : 'text-slate-500'"
                  >
                    {{ sub.label }}：{{ sub.completed ? formatDateTime(sub.completedTime) : '未完成' }}
                  </p>
                </template>
                <p
                  v-else
                  class="text-sm font-semibold"
                  :class="item.completed ? 'text-emerald-600' : 'text-slate-500'"
                >
                  {{ item.completed ? formatDateTime(item.completedTime) : '未完成' }}
                </p>
              </div>
            </div>
          </div>
          <div
            v-else
            class="px-4 py-3 rounded-2xl border border-dashed border-amber-200 bg-amber-50 text-sm text-amber-700"
          >
            <p>
              暂未找到关联的学习记录。若来自学习清单，请确保清单的
              <span class="font-semibold">record_ids</span>
              已正确填写。
            </p>
          </div>
        </div>
      </div>
    </main>

    <!-- 范围设置模态框 -->
    <div v-if="showRangeModal" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="cancelRangeSelection"></div>
      <div class="relative w-full max-w-lg bg-white rounded-3xl shadow-2xl p-6 sm:p-8 animate-scale-in">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-slate-900">复习范围</h3>
          <button @click="cancelRangeSelection" class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="flex gap-4 mb-6">
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Start</label>
            <input
              v-model.number="rangeForm.startId"
              type="number"
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-amber-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
          <div class="flex items-end pb-4 text-slate-300">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          </div>
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">End</label>
            <input
              v-model.number="rangeForm.endId"
              type="number"
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-amber-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
        </div>

        <div class="flex gap-3">
          <button
            @click="cancelRangeSelection"
            class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors"
          >
            取消
          </button>
          <button
            @click="applyRangeSelection"
            class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-amber-500 hover:bg-amber-600 shadow-lg shadow-amber-500/20 transition-colors"
          >
            确认
          </button>
        </div>
      </div>
    </div>

    <!-- 默认参数提示 -->
    <div v-if="showFallbackDialog" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/50 backdrop-blur-sm"></div>
      <div class="relative w-full max-w-md bg-white rounded-3xl shadow-2xl p-6 sm:p-8 space-y-5">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M12 2a10 10 0 100 20 10 10 0 000-20z" />
            </svg>
          </div>
          <div>
            <h3 class="text-lg font-bold text-slate-900">未检测到复习参数</h3>
            <p class="text-sm text-slate-500 mt-1">
              本页面通常由其他功能携带复习记录参数进入。是否使用默认数据开始体验？
            </p>
          </div>
        </div>
        <div class="rounded-2xl bg-slate-50 border border-slate-100 p-4 text-sm text-slate-600 space-y-1">
          <p>默认参数：</p>
          <p>· 课本：{{ fallbackBookName }}</p>
          <p>· 课本 ID：{{ fallbackBookId || '未选择' }}</p>
          <p>· 范围：1 - 10</p>
          <p>· 用户：当前登录账号</p>
        </div>
        <div class="flex flex-col sm:flex-row gap-3">
          <button
            @click="cancelFallbackAndExit"
            class="flex-1 px-5 py-3 rounded-xl font-semibold text-slate-500 bg-slate-100 hover:bg-slate-200 transition-colors"
          >
            返回上一页
          </button>
          <button
            @click="confirmUseDefaultRange"
            class="flex-1 px-5 py-3 rounded-xl font-bold text-white bg-amber-500 hover:bg-amber-600 shadow-lg shadow-amber-500/30 transition-colors"
          >
            使用默认数据
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { wordService } from '@/services/word.service'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import { useToast } from '@/composables/useToast'
import { useStudyTrackerStore } from '@/stores/studyTracker'
import { useWordStudyStore } from '@/stores/wordStudy'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const bookStore = useBookStore()
const wordStudyStore = useWordStudyStore()
const { success, error } = useToast()
const studyTracker = useStudyTrackerStore()
const STUDY_SCENE = 'word_review_other'

const showRangeModal = ref(false)
const reviewParams = ref({
  userId: null,
  bookId: null,
  startId: null,
  endId: null,
  recordIds: ''
})
const rangeForm = ref({
  startId: 1,
  endId: 10
})
const latestRecord = ref(null)
const words = ref([])
const currentIndex = ref(0)
const reviewMode = ref('en2cn')
const showAnswer = ref(false)
const loading = ref(false)
const initializing = ref(true)
const showFallbackDialog = ref(false)
const queryError = ref('')
const markingGrasped = ref(false)
const markingError = ref(false)
const studySessionStarted = ref(false)
const startingSession = ref(false)
const studyRecords = ref([])
const selectedRecordId = ref(null)
const selectedRound = ref(1)
const markingReviewRound = ref(false)
const ROUND_TOTAL = 9

const totalWords = computed(() => words.value.length)
const currentWord = computed(() => words.value[currentIndex.value] || null)
const hasNext = computed(() => currentIndex.value < totalWords.value - 1)
const hasPrev = computed(() => currentIndex.value > 0)
const progressPercent = computed(() => (totalWords.value ? Math.round(((currentIndex.value + 1) / totalWords.value) * 100) : 0))
const rangeText = computed(() => {
  if (!reviewParams.value.startId || !reviewParams.value.endId) return '未设置'
  return `${reviewParams.value.startId}-${reviewParams.value.endId}`
})
const targetBook = computed(() => bookStore.books?.find((book) => book.id === reviewParams.value.bookId) || null)
const defaultBook = computed(() => {
  if (bookStore.currentBook) return bookStore.currentBook
  if (bookStore.books?.length) return bookStore.books[0]
  return null
})
const fallbackBookName = computed(() => defaultBook.value?.bookName || defaultBook.value?.name || '未选择课本')
const fallbackBookId = computed(() => defaultBook.value?.id || '')
const currentBookName = computed(() => targetBook.value?.bookName || targetBook.value?.name || (reviewParams.value.bookId ? `课本 ID ${reviewParams.value.bookId}` : '未指定课本'))
const trackerActive = computed(() => studyTracker.isActive)
const trackerElapsed = computed(() => studyTracker.formattedElapsed)
const selectedRecord = computed(() => studyRecords.value.find((record) => record.id === selectedRecordId.value) || null)
const roundStatus = computed(() => {
  return Array.from({ length: ROUND_TOTAL }).map((_, idx) => {
    const round = idx + 1
    const field = `round${round}ReviewTime`
    const completedTime = selectedRecord.value ? selectedRecord.value[field] : null
    return {
      round,
      completed: Boolean(completedTime),
      completedTime
    }
  })
})
const displayRoundStatus = computed(() => {
  const status = roundStatus.value
  if (!status.length) return []

  const merged = []
  const round1 = status[0]
  const round2 = status[1]
  merged.push({
    key: '1-2',
    title: '第1-2轮',
    completed: round1.completed && round2?.completed,
    completedTime: round2?.completedTime || round1.completedTime,
    detail: [
      {
        label: '第1轮',
        completed: round1.completed,
        completedTime: round1.completedTime
      },
      {
        label: '第2轮',
        completed: round2?.completed || false,
        completedTime: round2?.completedTime || null
      }
    ]
  })

  for (let i = 2; i < status.length; i++) {
    const item = status[i]
    merged.push({
      key: `round-${item.round}`,
      title: `第${item.round}轮`,
      completed: item.completed,
      completedTime: item.completedTime,
      detail: []
    })
  }

  return merged
})
const currentRoundLabel = computed(() => `第${selectedRound.value}轮`)
const recordSelectOptions = computed(() =>
  studyRecords.value.map((record) => {
    const range = record.startId && record.endId ? `${record.startId}-${record.endId}` : '范围未知'
    return {
      value: record.id,
      label: `记录 #${record.id} · ${range}`
    }
  })
)
const roundOptions = computed(() => {
  return Array.from({ length: ROUND_TOTAL }).map((_, idx) => {
    const round = idx + 1
    const prevCompleted =
      round === 1 ? true : Boolean(selectedRecord.value?.[`round${round - 1}ReviewTime`])
    return {
      value: round,
      disabled: !prevCompleted
    }
  })
})

const parsePositiveNumber = (value) => {
  if (value === null || value === undefined || value === '') return null
  const num = Number(value)
  return Number.isFinite(num) && num > 0 ? num : null
}

const parseRecordIds = (text) => {
  if (!text || typeof text !== 'string') return []
  const matches = text.match(/\d+/g) || []
  const unique = [...new Set(matches.map((item) => Number(item)).filter((num) => Number.isFinite(num) && num > 0))]
  return unique
}

const hasCompleteParams = (params) => {
  return Boolean(params.userId && params.bookId && params.startId && params.endId)
}

const ensureBooksReady = async () => {
  try {
    if (!bookStore.books?.length) {
      await bookStore.fetchBooks()
    }
  } catch (err) {
    console.error('加载课本列表失败：', err)
  }
}

const ensureTargetBookSelected = async (bookId) => {
  if (!bookId) return
  try {
    if (!bookStore.books?.length) {
      await bookStore.fetchBooks()
    }
    if (bookStore.currentBook?.id === bookId) {
      return
    }
    const target = bookStore.books?.find((book) => Number(book.id) === Number(bookId))
    if (!target) {
      throw new Error('未在列表中找到参数指定的课本')
    }
    await bookStore.selectBook(target)
  } catch (err) {
    console.error('选择课本失败：', err)
    throw err
  }
}

const extractParamsFromRoute = () => {
  return {
    userId: parsePositiveNumber(route.query.userId),
    bookId: parsePositiveNumber(route.query.bookId),
    startId: parsePositiveNumber(route.query.startId),
    endId: parsePositiveNumber(route.query.endId),
    recordIds: typeof route.query.recordIds === 'string' ? route.query.recordIds.trim() : ''
  }
}

const validateParamsRange = (params) => {
  if (!hasCompleteParams(params)) return false
  return params.startId <= params.endId
}

const loadStudyRecords = async () => {
  if (!reviewParams.value.userId) {
    studyRecords.value = []
    selectedRecordId.value = null
    latestRecord.value = null
    return
  }

  try {
    const recordIds = parseRecordIds(reviewParams.value.recordIds)
    let records = []

    if (recordIds.length) {
      records = await wordStudyStore.getRecordsByIds({
        userId: reviewParams.value.userId,
        recordIds
      })
    } else {
      const record = await wordStudyStore.getLatestRecord(reviewParams.value.userId, reviewParams.value.bookId)
      if (record) records = [record]
    }

    studyRecords.value = Array.isArray(records) ? records : []
    selectedRecordId.value = studyRecords.value[0]?.id || null
  } catch (err) {
    console.error('加载学习记录失败：', err)
    error('加载关联学习记录失败，请稍后重试')
    studyRecords.value = []
    selectedRecordId.value = null
    latestRecord.value = null
  } finally {
    latestRecord.value = selectedRecord.value || null
    updateSuggestedRound()
  }
}

const loadWords = async () => {
  if (!validateParamsRange(reviewParams.value)) {
    queryError.value = '复习参数不完整或范围不正确'
    return
  }

  try {
    loading.value = true
    queryError.value = ''

    const response = await wordService.getWordList({
      userId: reviewParams.value.userId,
      bookId: reviewParams.value.bookId,
      start: reviewParams.value.startId,
      end: reviewParams.value.endId
    })

    words.value = response.data || []
    currentIndex.value = 0
    showAnswer.value = false

    if (!words.value.length) {
      queryError.value = '当前范围内没有可复习的单词'
    }
    await maybeStartStudySession()
  } catch (err) {
    const message = err?.message || '请稍后重试'
    queryError.value = `加载单词失败：${message}`
    error(queryError.value)
  } finally {
    loading.value = false
    initializing.value = false
  }
}

const initializeFromRoute = async () => {
  initializing.value = true
  const params = extractParamsFromRoute()

  if (!hasCompleteParams(params)) {
    await ensureBooksReady()
    showFallbackDialog.value = true
    initializing.value = false
    words.value = []
    currentIndex.value = 0
    return
  }

  showFallbackDialog.value = false
  reviewParams.value = { ...params }
  rangeForm.value = {
    startId: reviewParams.value.startId,
    endId: reviewParams.value.endId
  }

  if (params.startId > params.endId) {
    queryError.value = '参数范围不合法，起始位置需小于等于结束位置'
    initializing.value = false
    return
  }

  await ensureBooksReady()
  await ensureTargetBookSelected(reviewParams.value.bookId)
  await loadStudyRecords()
  await loadWords()
}

const updateSuggestedRound = () => {
  if (!selectedRecord.value) {
    selectedRound.value = 1
    return
  }
  for (let i = 1; i <= ROUND_TOTAL; i++) {
    if (!selectedRecord.value[`round${i}ReviewTime`]) {
      selectedRound.value = i
      return
    }
  }
  selectedRound.value = ROUND_TOTAL
}

const toggleMode = () => {
  reviewMode.value = reviewMode.value === 'en2cn' ? 'cn2en' : 'en2cn'
  showAnswer.value = false
}

const nextWord = () => {
  if (hasNext.value) {
    currentIndex.value++
    showAnswer.value = false
  }
}

const prevWord = () => {
  if (hasPrev.value) {
    currentIndex.value--
    showAnswer.value = false
  }
}

const toggleAnswer = () => {
  showAnswer.value = !showAnswer.value
}

const playPronunciation = () => {
  if (!currentWord.value) return
  try {
    window.speechSynthesis?.cancel()
    const utterance = new SpeechSynthesisUtterance(currentWord.value.word)
    utterance.lang = 'en-US'
    window.speechSynthesis?.speak(utterance)
  } catch (err) {
    console.error('播放发音失败：', err)
  }
}

const markAsGrasped = async () => {
  if (!currentWord.value?.id) return
  try {
    markingGrasped.value = true
    await wordService.markAsGrasped(currentWord.value.id)
    currentWord.value.isGrasp = 1
    success('已标记为掌握')
    if (hasNext.value) nextWord()
  } catch (err) {
    error('标记失败：' + (err?.message || '请稍后重试'))
  } finally {
    markingGrasped.value = false
  }
}

const markAsError = async () => {
  if (!currentWord.value?.id) return
  try {
    markingError.value = true
    await wordService.markAsNotGrasped(currentWord.value.id)
    currentWord.value.isGrasp = 2
    success('已加入错词本')
    if (hasNext.value) nextWord()
  } catch (err) {
    error('标记失败：' + (err?.message || '请稍后重试'))
  } finally {
    markingError.value = false
  }
}

const reloadWithSameRange = () => {
  if (hasCompleteParams(reviewParams.value)) {
    loadWords()
  }
}

const canMarkRound = computed(() => {
  if (!selectedRecord.value) return false
  const option = roundOptions.value.find((opt) => opt.value === selectedRound.value)
  if (!option) return false
  return !option.disabled
})

const markReviewRoundComplete = async () => {
  if (!authStore.user?.id) {
    error('请先登录后再标记复习完成')
    router.push('/login')
    return
  }
  const selectedOption = roundOptions.value.find((opt) => opt.value === selectedRound.value)
  if (selectedOption?.disabled) {
    error('请先完成上一轮复习')
    return
  }
  const sessionIds = studyRecords.value.map((r) => r.id).filter(Boolean)
  if (!sessionIds.length) {
    error('当前没有可标记的学习记录')
    return
  }
  try {
    markingReviewRound.value = true
    await wordStudyStore.markReviewComplete({
      userId: reviewParams.value.userId || authStore.user.id,
      sessionIds,
      recordIdsText: reviewParams.value.recordIds || '',
      reviewRound: selectedRound.value
    })
    success(`第${selectedRound.value}轮复习已完成`)
    await loadStudyRecords()
  } catch (err) {
    console.error('标记复习完成失败：', err)
    error('标记复习完成失败：' + (err?.message || '请稍后重试'))
  } finally {
    markingReviewRound.value = false
  }
}

const maybeStartStudySession = async () => {
  if (studySessionStarted.value || startingSession.value) return
  if (!reviewParams.value.userId || !reviewParams.value.bookId) return

  try {
    startingSession.value = true
    await studyTracker.start(STUDY_SCENE)
    studySessionStarted.value = true
  } catch (err) {
    console.error('启动学习会话失败：', err)
    error('启动学习会话失败，请稍后重试')
  } finally {
    startingSession.value = false
  }
}

const stopStudySession = async (source = 'navigateBack') => {
  try {
    await studyTracker.finish(source)
  } catch (err) {
    console.error('结束学习会话失败：', err)
  }
}

const goBack = async () => {
  await stopStudySession('navigateBack')
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const confirmUseDefaultRange = async () => {
  if (!authStore.user?.id) {
    error('请先登录再继续')
    router.push('/login')
    return
  }

  await ensureBooksReady()
  const defaultBookId = defaultBook.value?.id
  if (!defaultBookId) {
    error('未找到默认课本，请先在首页选择课本')
    return
  }

  showFallbackDialog.value = false
  initializing.value = true

  await router.replace({
    name: 'WordReviewOther',
    query: {
      userId: authStore.user.id,
      bookId: defaultBookId,
      startId: 1,
      endId: 10
    }
  })
}

const cancelFallbackAndExit = () => {
  showFallbackDialog.value = false
  goBack()
}

const openRangeModal = () => {
  rangeForm.value = {
    startId: reviewParams.value.startId,
    endId: reviewParams.value.endId
  }
  showRangeModal.value = true
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
}

const applyRangeSelection = () => {
  const start = parsePositiveNumber(rangeForm.value.startId)
  const end = parsePositiveNumber(rangeForm.value.endId)

  if (!start || !end) {
    error('请输入起始与结束编号')
    return
  }
  if (start > end) {
    error('起始位置需小于等于结束位置')
    return
  }
  if (!reviewParams.value.userId || !reviewParams.value.bookId) {
    error('缺少用户或课本信息，无法设置范围')
    return
  }

  showRangeModal.value = false
  router.replace({
    name: route.name || 'WordReviewOther',
    query: {
      ...route.query,
      userId: reviewParams.value.userId,
      bookId: reviewParams.value.bookId,
      startId: start,
      endId: end
    }
  })
}

const formatDateTime = (value) => {
  if (!value) return '未完成'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const pad = (num) => String(num).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(
    date.getMinutes()
  )}`
}

const handleKeydown = (e) => {
  if (showFallbackDialog.value) return
  switch (e.code) {
    case 'Space':
      e.preventDefault()
      playPronunciation()
      break
    case 'Enter':
      e.preventDefault()
      toggleAnswer()
      break
    case 'ArrowRight':
    case 'ArrowDown':
      e.preventDefault()
      nextWord()
      break
    case 'ArrowLeft':
    case 'ArrowUp':
      e.preventDefault()
      prevWord()
      break
  }
}

watch(
  () => selectedRecord.value,
  () => {
    latestRecord.value = selectedRecord.value || null
    updateSuggestedRound()
  }
)

watch(
  () => route.fullPath,
  () => {
    initializeFromRoute()
  },
  { immediate: true }
)

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
  ensureBooksReady()
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  window.speechSynthesis?.cancel()
  stopStudySession('componentUnmount')
})
</script>

<style scoped>
@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fade-in 0.2s ease-out forwards;
}
</style>
