<template>
  <div class="min-h-screen bg-slate-950 text-slate-50 relative overflow-hidden">
    <div class="absolute inset-0">
      <div class="absolute inset-0 bg-[radial-gradient(circle_at_top,_rgba(88,80,255,0.35),_rgba(2,6,23,0.95))]"></div>
      <div class="absolute inset-y-0 right-0 w-1/2 bg-gradient-to-br from-violet-500/20 via-sky-500/10 to-transparent blur-3xl"></div>
      <div class="absolute -bottom-16 -left-16 w-72 h-72 bg-indigo-500/20 blur-3xl rounded-full"></div>
    </div>

    <div class="relative z-10 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10 flex flex-col min-h-screen">
      <header class="flex items-center justify-between mb-8">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-2xl bg-white/10 border border-white/20 flex items-center justify-center">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6l6 3-6 3-6-3 6-3zM6 9l-1.5.75A2 2 0 003 11.5v5l9 4 9-4v-5a2 2 0 00-1.5-1.75L18 9" />
            </svg>
          </div>
          <div>
            <p class="text-xs uppercase tracking-[0.35em] text-slate-300">BreezeWise</p>
            <h1 class="text-xl font-semibold">轻风智语</h1>
          </div>
        </div>
        <router-link
          to="/login"
          class="hidden sm:inline-flex items-center gap-2 text-sm text-slate-300 hover:text-white transition-colors"
        >
          已有账号？立即登录
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </router-link>
      </header>

      <main class="flex-1 flex flex-col justify-center">
        <div class="grid lg:grid-cols-2 gap-8 items-center">
          <!-- 左侧简介（紧凑） -->
          <section class="space-y-6">
            <div class="space-y-3">
              <p class="inline-flex items-center gap-2 text-xs uppercase tracking-[0.3em] text-white/70">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
                Start Journey
              </p>
              <h2 class="text-4xl font-bold leading-snug text-white">
                立即加入 AI 驱动的英语学习体系
              </h2>
              <p class="text-sm text-slate-300 leading-relaxed">
                词汇、句子、听写、表达一站式同步，平台会根据你的弱项自动安排复习任务，多端同步，实现“随学随练”。
              </p>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div class="rounded-2xl border border-white/10 bg-white/5 p-4 backdrop-blur">
                <p class="text-3xl font-bold text-white">92%</p>
                <p class="text-xs uppercase tracking-[0.2em] text-slate-300 mt-2">坚持率</p>
                <p class="text-xs text-slate-400 mt-2">AI 规划复习提醒</p>
              </div>
              <div class="rounded-2xl border border-white/10 bg-white/5 p-4 backdrop-blur">
                <p class="text-3xl font-bold text-white">18K+</p>
                <p class="text-xs uppercase tracking-[0.2em] text-slate-300 mt-2">学习者</p>
                <p class="text-xs text-slate-400 mt-2">真实数据驱动推荐</p>
              </div>
            </div>

            <div class="rounded-2xl border border-white/10 bg-white/5 p-5 backdrop-blur flex items-center gap-4">
              <span class="w-10 h-10 rounded-full bg-emerald-500/20 text-emerald-200 flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </span>
              <div>
                <p class="text-sm uppercase tracking-[0.2em] text-white/60">Secure Access</p>
                <p class="text-sm text-slate-300">数据加密存储，支持帐号一键注销。</p>
              </div>
            </div>
          </section>

          <!-- 右侧注册表单 -->
          <section>
            <Card class="bg-white/95 text-slate-900 rounded-[1.75rem] shadow-xl border-white/70 w-full p-0 overflow-hidden">
              <div class="p-8 space-y-6">
                <div class="space-y-1">
                  <p class="text-xs uppercase tracking-[0.35em] text-slate-400">Create Account</p>
                  <h3 class="text-2xl font-bold text-slate-900">完善资料，生成学习档案</h3>
                  <p class="text-sm text-slate-500">1分钟内完成注册，立刻同步学习计划。</p>
                </div>

                <form class="space-y-5" @submit.prevent="handleRegister">
                  <div class="space-y-2">
                    <label for="username" class="text-xs uppercase tracking-[0.3em] text-slate-400">用户名</label>
                    <input
                      id="username"
                      v-model="form.username"
                      type="text"
                      required
                      class="w-full rounded-2xl border border-slate-200 px-4 py-3 text-base focus:outline-none focus:ring-2 focus:ring-violet-200"
                      placeholder="用于登录与数据同步"
                    />
                  </div>

                  <div class="space-y-2">
                    <label for="email" class="text-xs uppercase tracking-[0.3em] text-slate-400">邮箱</label>
                    <input
                      id="email"
                      v-model="form.email"
                      type="email"
                      required
                      class="w-full rounded-2xl border border-slate-200 px-4 py-3 text-base focus:outline-none focus:ring-2 focus:ring-sky-200"
                      placeholder="接收学习报告与提醒"
                    />
                  </div>

                  <div class="space-y-2">
                    <label for="nickname" class="text-xs uppercase tracking-[0.3em] text-slate-400">学习昵称</label>
                    <input
                      id="nickname"
                      v-model="form.nickname"
                      type="text"
                      class="w-full rounded-2xl border border-slate-200 px-4 py-3 text-base focus:outline-none focus:ring-2 focus:ring-amber-200"
                      placeholder="展示给学习伙伴，可选"
                    />
                  </div>

                  <div class="space-y-2">
                    <label for="password" class="text-xs uppercase tracking-[0.3em] text-slate-400">设置密码</label>
                    <input
                      id="password"
                      v-model="form.password"
                      type="password"
                      required
                      class="w-full rounded-2xl border border-slate-200 px-4 py-3 text-base focus:outline-none focus:ring-2 focus:ring-rose-200"
                      placeholder="至少 6 位，建议含字母+数字"
                    />
                  </div>

                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    block
                    :loading="loading"
                    class="rounded-2xl border-0 bg-gradient-to-r from-violet-500 via-indigo-500 to-sky-500 hover:from-violet-600 hover:to-sky-600 shadow-lg shadow-violet-400/40 text-base font-semibold tracking-wide"
                  >
                    <span v-if="!loading">注册并开启学习</span>
                    <span v-else>创建账户中...</span>
                  </Button>
                </form>

                <div class="text-center text-sm text-slate-400">
                  已有账号？
                  <router-link to="/login" class="font-semibold text-violet-600 hover:text-violet-500">立即登录 →</router-link>
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