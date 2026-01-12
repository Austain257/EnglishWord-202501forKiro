<template>
  <div class="min-h-screen bg-[#F8FAFC] text-slate-800 font-sans selection:bg-blue-100 selection:text-blue-900 relative overflow-hidden">
    <div class="absolute inset-x-0 top-0 h-60 bg-gradient-to-b from-white via-white/80 to-transparent pointer-events-none"></div>
    <!-- 顶部导航栏 -->
    <nav class="relative z-40 px-4 sm:px-6 lg:px-10 h-16 sm:h-20 flex items-center justify-between">
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
          <h1 class="text-lg font-semibold text-slate-900 tracking-tight">单词复习</h1>
          <p class="flex items-center gap-1 text-xs text-slate-500">
            <span class="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
            两轮模式 · 记忆强化
          </p>
        </div>
      </div>

      <div class="flex items-center gap-2 sm:gap-3">
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/70 rounded-xl border border-white/70 text-xs font-semibold text-slate-500">
          <span class="text-slate-400">首选</span>
          <span class="text-blue-600">第一轮</span>
          <span class="text-slate-400">·</span>
          <span class="text-violet-600">第二轮</span>
        </div>
        <div class="flex items-center gap-2 px-3 py-1.5 bg-slate-900 text-white rounded-xl text-xs font-semibold">
          <span>AI 推荐</span>
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5-5 5M6 12h12" /></svg>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <main class="relative z-10 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12">
      <!-- 页面标题和描述 -->
      <div class="text-center mb-8 sm:mb-12">
        <h2 class="text-2xl sm:text-3xl font-bold text-slate-900 mb-4">选择复习模式</h2>
        <p class="text-slate-600 text-lg max-w-2xl mx-auto">
          根据记忆科学原理，选择最适合当前学习阶段的复习方式，让词汇记忆更加牢固
        </p>
      </div>

      <!-- 复习模式选择卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 sm:gap-8">
        <!-- 第一轮复习 -->
        <div 
          @click="goToFirstReview"
          class="group cursor-pointer bg-white rounded-3xl p-8 sm:p-10 border border-slate-100 shadow-sm hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 relative overflow-hidden"
        >
          <!-- 背景装饰 -->
          <div class="absolute inset-0 bg-gradient-to-br from-emerald-50 to-blue-50 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          
          <div class="relative z-10">
            <!-- 图标 -->
            <div class="w-16 h-16 rounded-3xl bg-emerald-100 text-emerald-600 flex items-center justify-center mb-6 group-hover:scale-110 group-hover:bg-emerald-200 transition-all duration-300">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
            </div>

            <!-- 标题和副标题 -->
            <h3 class="text-2xl font-bold text-slate-900 mb-3 group-hover:text-emerald-700 transition-colors">
              第一轮复习
            </h3>
            <p class="text-slate-500 text-lg mb-6 leading-relaxed">
              思考与回想，刺激记忆单元的生成
            </p>

            <!-- 特点标签 -->
            <div class="flex flex-wrap gap-2 mb-6">
              <span class="inline-flex items-center text-xs font-semibold text-emerald-700 bg-emerald-50 px-3 py-1.5 rounded-full">
                记忆激活
              </span>
              <span class="inline-flex items-center text-xs font-semibold text-emerald-700 bg-emerald-50 px-3 py-1.5 rounded-full">
                认知强化
              </span>
            </div>

            <!-- 进入按钮 -->
            <div class="flex items-center text-emerald-600 font-semibold group-hover:text-emerald-700 transition-colors">
              <span class="mr-2">开始复习</span>
              <svg class="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
              </svg>
            </div>
          </div>
        </div>

        <!-- 第二轮复习 -->
        <div 
          @click="goToSecondReview"
          class="group cursor-pointer bg-white rounded-3xl p-8 sm:p-10 border border-slate-100 shadow-sm hover:shadow-2xl hover:-translate-y-2 transition-all duration-300 relative overflow-hidden"
        >
          <!-- 背景装饰 -->
          <div class="absolute inset-0 bg-gradient-to-br from-violet-50 to-pink-50 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          
          <div class="relative z-10">
            <!-- 图标 -->
            <div class="w-16 h-16 rounded-3xl bg-violet-100 text-violet-600 flex items-center justify-center mb-6 group-hover:scale-110 group-hover:bg-violet-200 transition-all duration-300">
              <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
              </svg>
            </div>

            <!-- 标题和副标题 -->
            <h3 class="text-2xl font-bold text-slate-900 mb-3 group-hover:text-violet-700 transition-colors">
              第二轮复习
            </h3>
            <p class="text-slate-500 text-lg mb-6 leading-relaxed">
              二轮复习，兴趣所在，模式随机
            </p>

            <!-- 特点标签 -->
            <div class="flex flex-wrap gap-2 mb-6">
              <span class="inline-flex items-center text-xs font-semibold text-violet-700 bg-violet-50 px-3 py-1.5 rounded-full">
                趣味互动
              </span>
              <span class="inline-flex items-center text-xs font-semibold text-violet-700 bg-violet-50 px-3 py-1.5 rounded-full">
                随机模式
              </span>
            </div>

            <!-- 进入按钮 -->
            <div class="flex items-center text-violet-600 font-semibold group-hover:text-violet-700 transition-colors">
              <span class="mr-2">随机挑战</span>
              <svg class="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习建议 -->
      <div class="mt-12 p-6 bg-slate-50 rounded-2xl border border-slate-100">
        <div class="flex items-start gap-4">
          <div class="w-10 h-10 rounded-xl bg-amber-100 text-amber-600 flex items-center justify-center flex-shrink-0">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h4 class="text-lg font-bold text-slate-900 mb-2">学习建议</h4>
            <p class="text-slate-600 leading-relaxed">
              建议先进行「第一轮复习」巩固基础记忆，然后通过「第二轮复习」的趣味模式加深印象。
              根据艾宾浩斯遗忘曲线，定期复习能有效提升长期记忆效果。
            </p>
          </div>
        </div>
      </div>
    </main>
    <!-- 页面跳转丝滑动画 -->
    <transition name="silky-fade">
      <div v-if="isNavigating" class="fixed inset-0 z-50 pointer-events-none">
        <div class="absolute inset-0 bg-gradient-to-br from-white via-white/90 to-blue-50 opacity-0 silky-overlay"></div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isNavigating = ref(false)

// 返回首页
const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const navigateWithSilkyTransition = (target) => {
  if (isNavigating.value) return
  isNavigating.value = true
  window.setTimeout(() => {
    router.push(target)
  }, 320)
}

// 第一轮复习 - 直接跳转到 WordReview
const goToFirstReview = () => {
  navigateWithSilkyTransition('/word/review/first')
}

// 第二轮复习 - 随机跳转算法
const goToSecondReview = () => {
  const randomTarget = Math.random() < 0.5 ? '/word/option' : '/word/game'
  navigateWithSilkyTransition(randomTarget)
}
</script>

<style scoped>
/* 响应式优化 */
@media (max-width: 640px) {
  .grid {
    gap: 1rem;
  }
  
  .rounded-3xl {
    border-radius: 1.5rem;
  }
  
  .p-8 {
    padding: 1.5rem;
  }
}

.silky-overlay {
  animation: silkyOverlay 0.6s ease forwards;
}

@keyframes silkyOverlay {
  0% {
    opacity: 0;
    transform: scale(1);
  }
  60% {
    opacity: 0.65;
  }
  100% {
    opacity: 0.85;
    transform: scale(1.02);
  }
}

.silky-fade-enter-active,
.silky-fade-leave-active {
  transition: opacity 0.4s ease;
}
.silky-fade-enter-from,
.silky-fade-leave-to {
  opacity: 0;
}
</style>
