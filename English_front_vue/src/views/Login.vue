<template>
  <div class="min-h-screen relative overflow-hidden bg-slate-900 text-slate-50">
    <!-- 背景 -->
    <div class="absolute inset-0">
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_top,_rgba(88,80,255,0.35),_rgba(15,23,42,0.9))]"></div>
      <div class="absolute inset-y-0 right-0 w-1/2 bg-gradient-to-br from-violet-500/20 via-sky-500/10 to-transparent blur-3xl"></div>
      <div class="absolute -top-32 -left-20 w-80 h-80 rounded-full bg-sky-400/20 blur-3xl"></div>
    </div>

    <div class="relative z-10 flex flex-col min-h-screen px-4 py-6 sm:px-8 lg:px-12">
      <header class="flex items-center justify-between mb-10">
        <div class="flex items-center gap-3">
          <div class="h-12 w-12 rounded-2xl bg-white/10 backdrop-blur border border-white/20 flex items-center justify-center">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6l6 3-6 3-6-3 6-3zM6 9l-1.5.75A2 2 0 003 11.5v5l9 4 9-4v-5a2 2 0 00-1.5-1.75L18 9" />
            </svg>
          </div>
          <div>
            <p class="text-xs uppercase tracking-[0.3em] text-slate-300">BreezeWise</p>
            <h1 class="text-xl font-semibold text-white">轻风智语</h1>
          </div>
        </div>
        <div class="hidden sm:flex items-center gap-4 text-slate-300 text-sm">
          <span class="inline-flex items-center gap-1">
            <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span> 在线 AI 协助
          </span>
          <span>全天候学习支持</span>
        </div>
      </header>

      <div class="flex-1 grid lg:grid-cols-2 gap-10 items-center">
        <!-- 左侧叙事区 -->
        <section class="space-y-8">
          <div class="space-y-4">
            <p class="inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-[0.3em] text-white/70">
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
              Smart Learning Loop
            </p>
            <h2 class="text-4xl lg:text-5xl font-bold text-white leading-tight tracking-tight">
              让英语学习<br class="hidden sm:block" />
              更沉浸、更高效、更自驱
            </h2>
            <p class="text-base sm:text-lg text-slate-300 leading-relaxed max-w-xl">
              结合 AI 纠错、智能复习与场景化练习，把词汇、句子、听写与表达全部串联。随时掌握学习节奏，让进步可视化。
            </p>
          </div>

          <div class="grid grid-cols-2 sm:grid-cols-3 gap-4">
            <div class="rounded-2xl bg-white/5 border border-white/10 p-4 backdrop-blur">
              <p class="text-3xl font-bold text-white">96%</p>
              <p class="text-xs uppercase tracking-[0.25em] text-slate-300 mt-2">坚持率</p>
            </div>
            <div class="rounded-2xl bg-white/5 border border-violet-200/20 p-4 backdrop-blur">
              <p class="text-3xl font-bold text-white">12K+</p>
              <p class="text-xs uppercase tracking-[0.25em] text-slate-300 mt-2">句子素材</p>
            </div>
            <div class="rounded-2xl bg-white/5 border border-sky-200/20 p-4 backdrop-blur">
              <p class="text-3xl font-bold text-white">24/7</p>
              <p class="text-xs uppercase tracking-[0.25em] text-slate-300 mt-2">AI守护</p>
            </div>
          </div>

          <div class="space-y-4">
            <div class="flex items-start gap-3">
              <span class="w-8 h-8 rounded-full bg-emerald-500/20 border border-emerald-400/30 flex items-center justify-center text-emerald-200">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </span>
              <div>
                <p class="text-sm uppercase tracking-[0.2em] text-white/60 mb-1">Adaptive Path</p>
                <p class="text-lg font-semibold text-white">自适应学习路径</p>
                <p class="text-sm text-slate-300">AI 跟踪你的薄弱项与学习频率，实时调配复习日程与练习强度。</p>
              </div>
            </div>

            <div class="flex items-start gap-3">
              <span class="w-8 h-8 rounded-full bg-sky-500/20 border border-sky-400/30 flex items-center justify-center text-sky-200">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c3.866 0 7 1.343 7 3v5c0 1.657-3.134 3-7 3s-7-1.343-7-3v-5c0-1.657 3.134-3 7-3z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 11V9c0-1.657 3.134-3 7-3s7 1.343 7 3v2" />
                </svg>
              </span>
              <div>
                <p class="text-sm uppercase tracking-[0.2em] text-white/60 mb-1">Immersive Practice</p>
                <p class="text-lg font-semibold text-white">沉浸式听说读写</p>
                <p class="text-sm text-slate-300">单词、句子、听写、表达四大能力一次练透，让输出更自信。</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 右侧表单 -->
        <section class="w-full">
          <div class="max-w-md mx-auto">
            <div class="mb-6 text-center lg:text-left space-y-3">
              <p class="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-white/5 border border-white/10 text-xs uppercase tracking-[0.35em] text-slate-200">
                Ready to start
              </p>
              <div>
                <h3 class="text-3xl font-bold text-white">登录您的学习空间</h3>
                <p class="text-slate-300 text-sm mt-2">输入账号即可同步进度、领取专属学习计划</p>
              </div>
            </div>

            <Card class="bg-white/95 border-white/40 shadow-2xl backdrop-blur-md">
              <form class="space-y-6" @submit.prevent="handleLogin">
                <div class="space-y-2">
                  <label for="username" class="text-xs uppercase tracking-[0.3em] text-slate-500">用户名</label>
                  <div class="relative group">
                    <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 group-focus-within:text-violet-500 transition">
                      <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14c3.866 0 7 1.343 7 3v2H5v-2c0-1.657 3.134-3 7-3zm0-4a4 4 0 100-8 4 4 0 000 8z" />
                      </svg>
                    </div>
                    <input
                      id="username"
                      v-model="form.username"
                      type="text"
                      required
                      class="block w-full pl-11 pr-4 py-3 rounded-2xl border border-slate-200 bg-white focus:ring-4 focus:ring-violet-100 focus:border-violet-500 text-slate-900 placeholder:text-slate-400 transition-all"
                      placeholder="输入您的学习账号"
                    />
                  </div>
                </div>

                <div class="space-y-2">
                  <label for="password" class="text-xs uppercase tracking-[0.3em] text-slate-500">密码</label>
                  <div class="relative group">
                    <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-slate-400 group-focus-within:text-sky-500 transition">
                      <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 11c1.657 0 3-1.343 3-3V6a3 3 0 00-6 0v2c0 1.657 1.343 3 3 3z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 11h14a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2z" />
                      </svg>
                    </div>
                    <input
                      id="password"
                      v-model="form.password"
                      type="password"
                      required
                      class="block w-full pl-11 pr-4 py-3 rounded-2xl border border-slate-200 bg-white focus:ring-4 focus:ring-sky-100 focus:border-sky-500 text-slate-900 placeholder:text-slate-400 transition-all"
                      placeholder="输入密码，开启今日学习"
                    />
                  </div>
                </div>

                <div class="flex items-center justify-between text-xs text-slate-500">
                  <span class="inline-flex items-center gap-1">
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
                    已加密传输
                  </span>
                  <router-link to="/register" class="font-medium text-violet-600 hover:text-violet-500 transition-colors">
                    立即注册 →
                  </router-link>
                </div>

                <Button
                  type="submit"
                  variant="primary"
                  size="lg"
                  block
                  :loading="loading"
                  class="rounded-2xl border-0 bg-gradient-to-r from-violet-500 via-indigo-500 to-sky-500 hover:from-violet-600 hover:to-sky-600 shadow-lg shadow-violet-400/40 text-base font-semibold tracking-wide"
                >
                  <span v-if="!loading">进入学习空间</span>
                  <span v-else>登录中...</span>
                </Button>

              </form>
            </Card>

            <div class="mt-6 grid grid-cols-2 gap-4 text-xs text-slate-200">
              <div class="rounded-2xl border border-white/10 bg-white/5 p-4 backdrop-blur">
                <p class="text-2xl font-bold text-white">AI 词汇助手</p>
                <p class="mt-2 text-slate-300">错词即刻复盘，牢记每一次错误。</p>
              </div>
              <div class="rounded-2xl border border-white/10 bg-white/5 p-4 backdrop-blur">
                <p class="text-2xl font-bold text-white">多端同步</p>
                <p class="mt-2 text-slate-300">PC / 平板 / 手机，一份进度随学随看。</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuth } from '@/composables/useAuth'
import { useToast } from '@/composables/useToast'
import Card from '@/components/common/Card.vue'
import Button from '@/components/common/Button.vue'

const { login } = useAuth()
const { success, error } = useToast()

const loading = ref(false)
const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  
  try {
    const result = await login(form.value)
    
    if (result.success) {
      success('登录成功！')
    } else {
      error(result.error || '登录失败')
    }
  } catch (err) {
    error('登录失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}
</script>