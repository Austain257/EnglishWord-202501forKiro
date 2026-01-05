<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-50">
    <div class="flex min-h-screen">
      <!-- 左侧系统介绍 -->
      <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-blue-600 to-indigo-700 p-12 text-white relative overflow-hidden">
        <!-- 背景装饰 -->
        <div class="absolute inset-0 opacity-10">
          <svg class="w-full h-full" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
            <defs>
              <pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse">
                <path d="M 10 0 L 0 0 0 10" fill="none" stroke="currentColor" stroke-width="0.5"/>
              </pattern>
            </defs>
            <rect width="100" height="100" fill="url(#grid)" />
          </svg>
        </div>
        
        <div class="relative z-10 flex flex-col justify-center">
          <div class="mb-8">
            <div class="w-16 h-16 bg-white bg-opacity-20 rounded-full flex items-center justify-center mb-6">
              <svg class="w-8 h-8" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2L2 7v10c0 5.55 3.84 9.74 9 11 5.16-1.26 9-5.45 9-11V7l-10-5z"/>
              </svg>
            </div>
            <h1 class="text-4xl font-bold mb-4">英语智能学习平台</h1>
            <p class="text-xl text-blue-100 mb-8">专业、现代、高效的英语学习解决方案</p>
          </div>
          
          <div class="space-y-6">
            <div class="flex items-start space-x-4">
              <div class="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
              </div>
              <div>
                <h3 class="text-lg font-semibold mb-2">智能学习路径</h3>
                <p class="text-blue-100">基于艾宾浩斯遗忘曲线，科学安排学习计划，提高记忆效率</p>
              </div>
            </div>
            
            <div class="flex items-start space-x-4">
              <div class="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </div>
              <div>
                <h3 class="text-lg font-semibold mb-2">多维度学习</h3>
                <p class="text-blue-100">涵盖单词学习、句子练习、听写训练，全方位提升英语能力</p>
              </div>
            </div>
            
            <div class="flex items-start space-x-4">
              <div class="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
              </div>
              <div>
                <h3 class="text-lg font-semibold mb-2">AI智能辅助</h3>
                <p class="text-blue-100">智能错误分析，个性化学习建议，让学习更加精准高效</p>
              </div>
            </div>
            
            <div class="flex items-start space-x-4">
              <div class="w-8 h-8 bg-white bg-opacity-20 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
              </div>
              <div>
                <h3 class="text-lg font-semibold mb-2">学习进度跟踪</h3>
                <p class="text-blue-100">详细的学习统计和进度分析，让您的学习成果一目了然</p>
              </div>
            </div>
          </div>
          
          <div class="mt-12 pt-8 border-t border-white border-opacity-20">
            <p class="text-blue-100 text-sm">
              "学习英语不再是负担，而是一种享受。让我们一起开启智能学习的新时代！"
            </p>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="flex-1 flex items-center justify-center p-8">
        <div class="max-w-md w-full space-y-8">
          <!-- 移动端系统标题 -->
          <div class="lg:hidden text-center mb-8">
            <div class="w-16 h-16 bg-gradient-to-br from-blue-600 to-indigo-700 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2L2 7v10c0 5.55 3.84 9.74 9 11 5.16-1.26 9-5.45 9-11V7l-10-5z"/>
              </svg>
            </div>
            <h1 class="text-2xl font-bold text-gray-900">英语智能学习平台</h1>
            <p class="text-gray-600 mt-2">专业、现代、高效的学习解决方案</p>
          </div>
          
          <div>
            <h2 class="text-center text-3xl font-bold text-gray-900">
              欢迎回来
            </h2>
            <p class="mt-2 text-center text-sm text-gray-600">
              请登录您的账户，继续您的学习之旅
            </p>
          </div>
          
          <Card class="shadow-xl border-0">
            <form class="space-y-6" @submit.prevent="handleLogin">
              <div>
                <label for="username" class="block text-sm font-medium text-gray-700 mb-2">
                  用户名
                </label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                    </svg>
                  </div>
                  <input
                    id="username"
                    v-model="form.username"
                    type="text"
                    required
                    class="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
                    placeholder="请输入用户名"
                  />
                </div>
              </div>
              
              <div>
                <label for="password" class="block text-sm font-medium text-gray-700 mb-2">
                  密码
                </label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                    </svg>
                  </div>
                  <input
                    id="password"
                    v-model="form.password"
                    type="password"
                    required
                    class="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
                    placeholder="请输入密码"
                  />
                </div>
              </div>
              
              <div>
                <Button
                  type="submit"
                  variant="primary"
                  size="lg"
                  block
                  :loading="loading"
                  class="bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 transform hover:scale-105 transition-all duration-200"
                >
                  <span v-if="!loading">立即登录</span>
                  <span v-else>登录中...</span>
                </Button>
              </div>
              
              <div class="text-center">
                <router-link
                  to="/register"
                  class="text-blue-600 hover:text-blue-500 text-sm font-medium hover:underline transition-colors"
                >
                  还没有账号？立即注册 →
                </router-link>
              </div>
            </form>
          </Card>
          
          <!-- 测试账号提示 -->
          <Card class="bg-gradient-to-r from-blue-50 to-indigo-50 border-blue-200 shadow-sm">
            <div class="text-center">
              <div class="flex items-center justify-center mb-2">
                <svg class="w-5 h-5 text-blue-600 mr-2" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M13 9h-2V7h2m0 10h-2v-6h2m-1-9A10 10 0 002 12a10 10 0 0010 10 10 10 0 0010-10A10 10 0 0012 2z"/>
                </svg>
                <span class="text-sm font-medium text-blue-800">快速体验</span>
              </div>
              <div class="text-sm text-blue-700 space-y-1">
                <p><span class="font-medium">测试账号:</span> testuser</p>
                <p><span class="font-medium">密码:</span> 123456</p>
              </div>
            </div>
          </Card>
          
          <!-- 功能特色简介 -->
          <div class="lg:hidden">
            <div class="grid grid-cols-2 gap-4 mt-8">
              <div class="text-center p-4 bg-white rounded-lg shadow-sm">
                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-2">
                  <svg class="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                </div>
                <h4 class="text-sm font-semibold text-gray-900">智能学习</h4>
                <p class="text-xs text-gray-600 mt-1">AI辅助学习</p>
              </div>
              
              <div class="text-center p-4 bg-white rounded-lg shadow-sm">
                <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-2">
                  <svg class="w-4 h-4 text-green-600" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                  </svg>
                </div>
                <h4 class="text-sm font-semibold text-gray-900">科学记忆</h4>
                <p class="text-xs text-gray-600 mt-1">遗忘曲线优化</p>
              </div>
            </div>
          </div>
        </div>
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