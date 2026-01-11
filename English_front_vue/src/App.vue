<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <router-view />
    <Toast />
    
    <!-- 随身记按钮 - 只在需要认证的页面显示 -->
    <JottingButton v-if="shouldShowJottingButton" />
    
    <!-- 学习锁定守卫 - 全局组件 -->
    <StudyLockGuard />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import Toast from '@/components/common/Toast.vue'
import JottingButton from '@/components/JottingButton.vue'
import StudyLockGuard from '@/components/common/StudyLockGuard.vue'

const route = useRoute()
const authStore = useAuthStore()

// 判断是否显示随身记按钮
const shouldShowJottingButton = computed(() => {
  return authStore.isAuthenticated && 
         route.meta.requiresAuth && 
         route.name !== 'Login' && 
         route.name !== 'Register'
})
</script>

<style scoped>
#app {
  font-family: 'Inter', system-ui, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
