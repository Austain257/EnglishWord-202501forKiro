<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="text-center">
      <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
        <svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
        </svg>
      </div>
      <h1 class="text-4xl font-bold text-gray-900 mb-4">404</h1>
      <h2 class="text-xl font-semibold text-gray-700 mb-4">页面未找到</h2>
      <p class="text-gray-600 mb-8">抱歉，您访问的页面不存在或已被移除。</p>
      <Button @click="goHome" variant="primary">
        返回首页
      </Button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import Button from '@/components/common/Button.vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>