<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-4xl mx-auto px-4">
      <h1 class="text-3xl font-bold text-center mb-8">激励文案轮播测试</h1>
      
      <!-- 激励文案组件 -->
      <div class="mb-8">
        <MotivationQuote ref="motivationRef" />
      </div>
      
      <!-- 控制按钮 -->
      <div class="text-center space-x-4">
        <button
          @click="refreshQuote"
          class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
        >
          刷新文案
        </button>
        
        <button
          @click="goBack"
          class="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 transition-colors"
        >
          返回首页
        </button>
      </div>
      
      <!-- 说明文字 -->
      <div class="mt-8 bg-white rounded-lg p-6 shadow">
        <h2 class="text-xl font-semibold mb-4">功能说明</h2>
        <ul class="space-y-2 text-gray-700">
          <li>• 激励文案每5秒自动轮播</li>
          <li>• 点击右上角播放/暂停按钮控制轮播</li>
          <li>• 点击左右箭头手动切换文案</li>
          <li>• 点击底部圆点跳转到指定文案</li>
          <li>• 支持PC端和移动端响应式设计</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import MotivationQuote from '@/components/MotivationQuote.vue'

const router = useRouter()
const motivationRef = ref(null)

const refreshQuote = () => {
  if (motivationRef.value) {
    motivationRef.value.refresh()
  }
}

const goBack = () => {
  router.push('/')
}
</script>