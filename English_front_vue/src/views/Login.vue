<template>
  <div class="min-h-screen bg-gradient-to-br from-[#E4EEFB] via-[#D7EDE6] to-[#BFD6F5] text-slate-800 relative overflow-hidden">
    <!-- 装饰背景 -->
    <div class="pointer-events-none absolute inset-0">
      <div class="absolute -top-20 -left-24 w-72 h-72 rounded-full bg-emerald-300/40 blur-3xl"></div>
      <div class="absolute top-10 right-10 w-64 h-64 rounded-full bg-blue-300/40 blur-3xl"></div>
      <div class="absolute bottom-0 left-12 w-48 h-48 rounded-full bg-indigo-300/30 blur-3xl"></div>
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_20%_20%,rgba(255,255,255,0.18),transparent_35%),radial-gradient(circle_at_80%_0%,rgba(255,255,255,0.14),transparent_32%)]"></div>
    </div>

    <div class="relative z-10 flex flex-col min-h-screen">
      <!-- 顶部导航 -->
      <header class="w-full px-4 sm:px-6 lg:px-10 xl:px-14 2xl:px-16 py-6 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <img
            src="https://austain-java-ai-web.oss-cn-beijing.aliyuncs.com/BreezeWise_LOGO.png"
            alt="轻风智语 Logo"
            class="w-12 h-12 rounded-2xl object-cover shadow-lg shadow-blue-500/20 bg-white"
          />

          <div class="flex flex-col">
            <h1 class="text-lg sm:text-xl font-bold text-slate-900 leading-tight">轻风智语</h1>
            <p class="text-[11px] font-semibold uppercase tracking-[0.25em] text-slate-500">BreezeWise</p>
          </div>
        </div>
        <div class="hidden sm:flex items-center gap-3 text-sm text-slate-500">
          <span class="inline-flex items-center gap-1 px-3 py-1 rounded-full bg-white/80 border border-slate-200">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            AI 伴学
          </span>
          <span class="px-3 py-1 rounded-full bg-white/80 border border-slate-200">多端同步</span>
        </div>
      </header>

      <!-- 主体 -->
      <main class="flex-1 w-full px-4 sm:px-6 lg:px-10 xl:px-14 2xl:px-16 pb-10">
        <div class="grid lg:grid-cols-2 gap-10 items-center max-w-6xl mx-auto">
          <!-- 左侧叙事 -->
          <section class="space-y-8">
            <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/80 border border-emerald-100 text-xs font-bold uppercase tracking-[0.35em] text-emerald-600 shadow-sm shadow-emerald-200/50">
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
              Smart English Loop
            </div>
            <div class="space-y-4">
              <h2 class="text-3xl sm:text-4xl lg:text-5xl font-bold text-slate-900 leading-tight tracking-tight">
                AI 赋能英语学习平台<br class="hidden sm:block" /> 词汇 + 句子 + 文章全链路
              </h2>
              <p class="text-base sm:text-lg text-slate-600 leading-relaxed max-w-xl">
                从单词记忆到句子表达，再到阅读理解和听写训练，AI 帮你规划复习节奏，让坚持变简单，让进步看得见。
              </p>
            </div>

            <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
              <div class="rounded-2xl bg-white/90 shadow-sm border border-emerald-200 p-4 backdrop-blur">
                <p class="text-2xl font-bold text-emerald-600">96%</p>
                <p class="text-xs uppercase tracking-[0.25em] text-slate-500 mt-1">坚持率</p>
              </div>
              <div class="rounded-2xl bg-white/90 shadow-sm border border-blue-200 p-4 backdrop-blur">
                <p class="text-2xl font-bold text-blue-600">12K+</p>
                <p class="text-xs uppercase tracking-[0.25em] text-slate-500 mt-1">场景句子</p>
              </div>
              <div class="rounded-2xl bg-white/90 shadow-sm border border-indigo-200 p-4 backdrop-blur">
                <p class="text-2xl font-bold text-indigo-600">全终端</p>
                <p class="text-xs uppercase tracking-[0.25em] text-slate-500 mt-1">随学随练</p>
              </div>
            </div>

            
              <div class="mt-6 grid grid-cols-2 gap-4 text-xs">
                <div class="rounded-2xl border border-emerald-100 bg-white shadow-sm p-4">
                  <p class="text-lg font-bold text-emerald-700">AI 词汇助手</p>
                  <p class="mt-2 text-slate-600">错词复盘、智能复习曲线，巩固记忆。</p>
                </div>
                <div class="rounded-2xl border border-blue-100 bg-white shadow-sm p-4">
                  <p class="text-lg font-bold text-blue-700">多端同步</p>
                  <p class="mt-2 text-slate-600">PC / 平板 / 手机实时同步学习进度。</p>
                </div>
              </div>
          </section>

          <!-- 右侧表单 -->
          <section class="w-full">
            <div class="max-w-md mx-auto">
              <div class="mb-6 text-center lg:text-left space-y-2">
                <p class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/90 border border-slate-200 text-[11px] uppercase tracking-[0.35em] text-emerald-600 shadow-sm">
                  开始同步进度
                  <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                </p>
                <h3 class="text-3xl font-bold text-slate-900">登录学习空间</h3>
                <p class="text-slate-600 text-sm">接续你的单词、句子、听写与复习计划</p>
              </div>

              <Card class="bg-gradient-to-br from-[#E3F1F7]/95 via-[#E7F8F1]/95 to-[#E1EBFF]/95 border border-emerald-100 shadow-xl shadow-emerald-200/50">
                <form class="space-y-6" @submit.prevent="handleLogin">
                  <div class="space-y-2">
                    <label for="username" class="text-xs font-semibold tracking-[0.2em] text-slate-500 uppercase">用户名</label>
                    <div class="relative group">
                      <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-emerald-500/70 group-focus-within:text-emerald-600 transition">
                        <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14c3.866 0 7 1.343 7 3v2H5v-2c0-1.657 3.134-3 7-3zm0-4a4 4 0 100-8 4 4 0 000 8z" />
                        </svg>
                      </div>
                      <input
                        id="username"
                        v-model="form.username"
                        type="text"
                        required
                        class="block w-full pl-12 pr-4 py-3 rounded-2xl border border-emerald-100 bg-[#F4F9FF]/90 focus:ring-4 focus:ring-emerald-100 focus:border-emerald-500 text-slate-900 placeholder:text-slate-600 transition-all"
                        placeholder="输入用户名"
                      />
                    </div>
                  </div>

                  <div class="space-y-2">
                    <label for="password" class="text-xs font-semibold tracking-[0.2em] text-slate-500 uppercase">密码</label>
                    <div class="relative group">
                      <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none text-blue-500/70 group-focus-within:text-blue-600 transition">
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
                        class="block w-full pl-12 pr-4 py-3 rounded-2xl border border-blue-100 bg-[#F4F9FF]/90 focus:ring-4 focus:ring-blue-100 focus:border-blue-500 text-slate-900 placeholder:text-slate-600 transition-all"
                        placeholder="输入密码，开启今日学习"
                      />
                    </div>
                  </div>

                  <div class="flex items-center justify-between text-xs text-slate-500">
                    <span class="inline-flex items-center gap-1 px-2 py-1 rounded-full bg-emerald-50 text-emerald-600 border border-emerald-100">
                      <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                      已加密传输
                    </span>
                    <router-link to="/register" class="font-semibold text-indigo-600 hover:text-indigo-700 transition-colors">
                      立即注册 →
                    </router-link>
                  </div>

                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    block
                    :loading="loading"
                    class="rounded-2xl border-0 bg-gradient-to-r from-emerald-500 via-blue-500 to-indigo-500 hover:from-emerald-600 hover:to-indigo-600 shadow-lg shadow-emerald-300/50 text-base font-semibold tracking-wide"
                  >
                    <span v-if="!loading">进入学习空间</span>
                    <span v-else>登录中...</span>
                  </Button>
                </form>
              </Card>

            </div>
          </section>
        </div>
      </main>
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