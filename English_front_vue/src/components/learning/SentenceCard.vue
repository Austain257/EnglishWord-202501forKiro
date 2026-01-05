<template>
  <div class="bg-white rounded-lg shadow-lg p-4 sm:p-6 max-w-2xl mx-auto">
    <!-- 句子内容区域 -->
    <div class="text-center mb-4 sm:mb-6">
      <!-- 英文句子 -->
      <div class="mb-3 sm:mb-4">
        <p class="text-base sm:text-lg md:text-xl font-medium text-gray-900 leading-relaxed px-2">
          {{ sentence.sentence }}
        </p>
      </div>
      
      <!-- 中文翻译 -->
      <div v-if="showChinese" class="mb-3 sm:mb-4">
        <p class="text-sm sm:text-base md:text-lg text-gray-600 leading-relaxed px-2">
          {{ sentence.chinese }}
        </p>
      </div>
      
      <!-- 隐藏中文时的占位 -->
      <div v-else class="mb-3 sm:mb-4">
        <p class="text-sm sm:text-base md:text-lg text-gray-400 italic px-2">
          点击"显示中文"查看翻译
        </p>
      </div>
    </div>
    
    <!-- 句子统计信息 -->
    <div class="flex flex-col sm:flex-row justify-center items-center space-y-2 sm:space-y-0 sm:space-x-6 mb-4 sm:mb-6 text-xs sm:text-sm">
      <div class="flex items-center space-x-1">
        <svg class="w-3 h-3 sm:w-4 sm:h-4 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
        </svg>
        <span class="text-gray-600">学习次数: {{ sentence.times || 0 }}</span>
      </div>
      
      <div v-if="sentence.isGrasp !== 0" class="flex items-center space-x-1">
        <svg class="w-3 h-3 sm:w-4 sm:h-4" :class="sentence.isGrasp === 1 ? 'text-green-500' : 'text-red-500'" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path v-if="sentence.isGrasp === 1" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
        </svg>
        <span :class="sentence.isGrasp === 1 ? 'text-green-600' : 'text-red-600'">
          {{ sentence.isGrasp === 1 ? '已掌握' : `错误次数: ${sentence.errorTimes || 0}` }}
        </span>
      </div>
    </div>
    
    <!-- 操作按钮区域 -->
    <div class="flex flex-col sm:flex-row justify-center space-y-2 sm:space-y-0 sm:space-x-3">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  sentence: {
    type: Object,
    required: true,
    default: () => ({
      id: 0,
      sentence: '',
      chinese: '',
      times: 0,
      isGrasp: 0,
      errorTimes: 0
    })
  },
  showChinese: {
    type: Boolean,
    default: true
  }
})
</script>