<template>
  <div class="bg-white rounded-lg shadow p-4">
    <div class="flex justify-between items-center mb-2">
      <span class="text-sm font-medium text-gray-700">学习进度</span>
      <span class="text-sm text-gray-600">{{ current }}/{{ total }}</span>
    </div>
    
    <div class="w-full bg-gray-200 rounded-full h-3">
      <div 
        class="bg-gradient-to-r from-blue-500 to-purple-600 h-3 rounded-full transition-all duration-300 ease-out"
        :style="{ width: `${progress}%` }"
      ></div>
    </div>
    
    <div class="text-center mt-2">
      <span class="text-lg font-semibold text-gray-900">{{ progress }}%</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  current: {
    type: Number,
    default: 0
  },
  total: {
    type: Number,
    default: 0
  }
})

const progress = computed(() => {
  if (props.total === 0) return 0
  return Math.round((props.current / props.total) * 100)
})
</script>