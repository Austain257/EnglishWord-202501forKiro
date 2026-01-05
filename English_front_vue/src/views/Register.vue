<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          注册账号
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          创建您的学习账号
        </p>
      </div>
      
      <Card class="mt-8">
        <form class="space-y-6" @submit.prevent="handleRegister">
          <div>
            <label for="username" class="label">用户名</label>
            <input
              id="username"
              v-model="form.username"
              type="text"
              required
              class="input"
              placeholder="请输入用户名"
            />
          </div>
          
          <div>
            <label for="email" class="label">邮箱</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              class="input"
              placeholder="请输入邮箱"
            />
          </div>
          
          <div>
            <label for="nickname" class="label">昵称</label>
            <input
              id="nickname"
              v-model="form.nickname"
              type="text"
              class="input"
              placeholder="请输入昵称（可选）"
            />
          </div>
          
          <div>
            <label for="password" class="label">密码</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              class="input"
              placeholder="请输入密码"
            />
          </div>
          
          <div>
            <Button
              type="submit"
              variant="primary"
              size="lg"
              block
              :loading="loading"
            >
              注册
            </Button>
          </div>
          
          <div class="text-center">
            <router-link
              to="/login"
              class="text-primary-600 hover:text-primary-500 text-sm"
            >
              已有账号？立即登录
            </router-link>
          </div>
        </form>
      </Card>
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