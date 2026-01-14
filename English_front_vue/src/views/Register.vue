<template>
  <div class="min-h-screen bg-gradient-to-br from-[#E4EEFB] via-[#D7EDE6] to-[#BFD6F5] text-slate-800 relative overflow-hidden">
    <!-- 背景装饰 -->
    <div class="pointer-events-none absolute inset-0">
      <div class="absolute -top-20 -left-20 w-72 h-72 rounded-full bg-emerald-300/40 blur-3xl"></div>
      <div class="absolute top-8 right-10 w-64 h-64 rounded-full bg-blue-300/35 blur-3xl"></div>
      <div class="absolute bottom-0 left-16 w-52 h-52 rounded-full bg-indigo-300/30 blur-3xl"></div>
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_18%_22%,rgba(255,255,255,0.18),transparent_35%),radial-gradient(circle_at_82%_0%,rgba(255,255,255,0.14),transparent_32%)]"></div>
    </div>

    <div class="relative z-10 flex flex-col min-h-screen">
      <!-- 顶部导航 -->
      <header class="w-full px-3 sm:px-5 lg:px-8 xl:px-12 2xl:px-14 py-4 flex items-center justify-between">
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
        <router-link
          to="/login"
          class="hidden sm:inline-flex items-center gap-2 px-3 py-2 rounded-2xl bg-white/85 border border-slate-200 text-sm text-slate-600 hover:text-indigo-600 hover:border-indigo-200 transition-colors shadow-sm"
        >
          已有账号？立即登录
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </router-link>
      </header>

      <main class="flex-1 w-full px-3 sm:px-5 lg:px-8 xl:px-12 2xl:px-14 pt-1 pb-6">
        <div class="grid lg:grid-cols-2 gap-7 items-start max-w-6xl mx-auto">
          <!-- 左侧叙事 -->
          <section class="space-y-8">
            <div class="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-white/80 border border-emerald-100 text-xs font-bold uppercase tracking-[0.35em] text-emerald-600 shadow-sm shadow-emerald-200/50">
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
              Start Journey
            </div>
            <div class="space-y-4">
              <h2 class="text-3xl sm:text-4xl lg:text-5xl font-bold text-slate-900 leading-tight tracking-tight">
                立即加入 AI 驱动的英语学习体系
              </h2>
              <p class="text-base sm:text-lg text-slate-600 leading-relaxed max-w-xl">
                词汇、句子、听写、表达一站式同步，AI 根据弱项自动安排复习任务，多端同步，实现“随学随练”。
              </p>
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div class="rounded-2xl bg-white/90 shadow-sm border border-emerald-200 p-4 backdrop-blur">
                <p class="text-2xl font-bold text-emerald-600">92%</p>
                <p class="text-xs uppercase tracking-[0.2em] text-slate-500 mt-1">坚持率</p>
                <p class="text-xs text-slate-500 mt-2">AI 规划复习提醒</p>
              </div>
              <div class="rounded-2xl bg-white/90 shadow-sm border border-blue-200 p-4 backdrop-blur">
                <p class="text-2xl font-bold text-blue-600">18K+</p>
                <p class="text-xs uppercase tracking-[0.2em] text-slate-500 mt-1">学习者</p>
                <p class="text-xs text-slate-500 mt-2">真实数据驱动推荐</p>
              </div>
            </div>

            <div class="rounded-2xl border border-emerald-100 bg-white/85 p-4 shadow-sm flex items-center gap-4">
              <span class="w-10 h-10 rounded-2xl bg-emerald-100 text-emerald-600 flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </span>
              <div>
                <p class="text-sm uppercase tracking-[0.2em] text-emerald-600">Secure Access</p>
                <p class="text-sm text-slate-600">数据加密存储，支持帐号一键注销。</p>
              </div>
            </div>
          </section>

          <!-- 右侧注册表单 -->
          <section class="w-full">
            <Card class="bg-gradient-to-br from-[#E3F1F7]/95 via-[#E7F8F1]/95 to-[#E1EBFF]/95 text-slate-900 rounded-[1.75rem] shadow-xl border border-emerald-100 w-full p-0 overflow-hidden shadow-emerald-200/50">
              <div class="p-5 sm:p-6 space-y-4">
                <div class="space-y-1.5">
                  <p class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/85 border border-slate-200 text-[11px] uppercase tracking-[0.35em] text-emerald-600 shadow-sm">
                    Create Account
                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                  </p>
                  <h3 class="text-2xl font-bold text-slate-900">完善资料，生成学习档案</h3>
                  <p class="text-sm text-slate-600">1 分钟完成注册，立即同步学习计划。</p>
                </div>

                <form class="space-y-3.5" @submit.prevent="handleRegister">
                  <div class="space-y-1.5">
                    <label for="username" class="text-xs font-semibold tracking-[0.25em] text-slate-500 uppercase">用户名</label>
                    <input
                      id="username"
                      v-model="form.username"
                      type="text"
                      required
                      class="w-full rounded-2xl border border-emerald-100 px-4 py-2.5 text-base bg-white/95 focus:outline-none focus:ring-4 focus:ring-emerald-100 focus:border-emerald-500 placeholder:text-slate-600 transition-all"
                      placeholder="用于登录与数据同步"
                    />
                  </div>

                  <div class="space-y-1.5">
                    <label for="email" class="text-xs font-semibold tracking-[0.25em] text-slate-500 uppercase">邮箱</label>
                    <input
                      id="email"
                      v-model="form.email"
                      type="email"
                      required
                      class="w-full rounded-2xl border border-blue-100 px-4 py-2.5 text-base bg-white/95 focus:outline-none focus:ring-4 focus:ring-blue-100 focus:border-blue-500 placeholder:text-slate-600 transition-all"
                      placeholder="接收学习报告与提醒"
                    />
                  </div>

                  <div class="space-y-2">
                    <label for="nickname" class="text-xs font-semibold tracking-[0.25em] text-slate-500 uppercase">学习昵称</label>
                    <input
                      id="nickname"
                      v-model="form.nickname"
                      type="text"
                      class="w-full rounded-2xl border border-amber-100 px-4 py-2.5 text-base bg-white/95 focus:outline-none focus:ring-4 focus:ring-amber-100 focus:border-amber-400 placeholder:text-slate-600 transition-all"
                      placeholder="展示给学习伙伴，可选"
                    />
                  </div>

                  <div class="space-y-2">
                    <label for="password" class="text-xs font-semibold tracking-[0.25em] text-slate-500 uppercase">设置密码</label>
                    <input
                      id="password"
                      v-model="form.password"
                      type="password"
                      required
                      class="w-full rounded-2xl border border-blue-100 px-4 py-2.5 text-base bg-white/95 focus:outline-none focus:ring-4 focus:ring-indigo-100 focus:border-indigo-500 placeholder:text-slate-600 transition-all"
                      placeholder="至少 6 位，建议含字母+数字"
                    />
                  </div>

                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    block
                    :loading="loading"
                    class="rounded-2xl border-0 bg-gradient-to-r from-emerald-500 via-blue-500 to-indigo-500 hover:from-emerald-600 hover:to-indigo-600 shadow-lg shadow-emerald-300/50 text-base font-semibold tracking-wide"
                  >
                    <span v-if="!loading">注册并开启学习</span>
                    <span v-else>创建账户中...</span>
                  </Button>
                </form>

                <div class="text-center text-sm text-slate-500">
                  已有账号？
                  <router-link to="/login" class="font-semibold text-indigo-600 hover:text-indigo-700">立即登录 →</router-link>
                </div>
              </div>
            </Card>
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

const { register } = useAuth()
const { success, error } = useToast()

const loading = ref(false)
const form = ref({
  username: '',
  email: '',
  nickname: '',
  password: ''
})

const handleRegister = async () => {
  loading.value = true
  
  try {
    const result = await register(form.value)
    
    if (result.success) {
      success('注册成功！请登录')
    } else {
      error(result.error || '注册失败')
    }
  } catch (err) {
    error('注册失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}
</script>