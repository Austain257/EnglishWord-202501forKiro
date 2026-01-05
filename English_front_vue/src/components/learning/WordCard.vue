<template>
  <div class="bg-white rounded-lg shadow-lg p-4 sm:p-6 md:p-8 text-center min-h-[300px] sm:min-h-[350px] md:min-h-[400px] flex flex-col justify-center">
    <div v-if="word" class="space-y-4 sm:space-y-6">
      <!-- 单词 -->
      <div class="text-2xl sm:text-3xl md:text-4xl lg:text-5xl font-bold text-gray-900 break-words">
        {{ word.word }}
      </div>
      
      <!-- 音标 -->
      <div v-if="word.pronounce || word.phonetic" class="text-base sm:text-lg text-gray-600">
        {{ word.pronounce || word.phonetic }}
      </div>
      
      <!-- 中文释义 -->
      <div v-if="showChinese && word.chinese" class="text-lg sm:text-xl text-gray-700 px-2 sm:px-4 leading-relaxed">
        {{ word.chinese }}
      </div>
      
      <!-- 学习统计 -->
      <div class="flex justify-center space-x-4 sm:space-x-6 md:space-x-8 text-sm">
        <div class="text-center">
          <div class="text-base sm:text-lg font-semibold text-blue-600">{{ word.times || 0 }}</div>
          <div class="text-gray-500 text-xs sm:text-sm">学习次数</div>
        </div>
        
        <!-- 错词次数 -->
        <div class="text-center">
          <div 
            class="text-base sm:text-lg font-semibold"
            :class="{
              'text-gray-400': word.isGrasp === 1,
              'text-red-600': word.isGrasp !== 1
            }"
          >
            {{ word.errorTimes || 0 }}
          </div>
          <div 
            class="text-xs sm:text-sm"
            :class="{
              'text-gray-400': word.isGrasp === 1,
              'text-gray-500': word.isGrasp !== 1
            }"
          >
            错词次数
          </div>
        </div>
        
        <!-- 掌握状态 -->
        <div class="text-center">
          <div 
            class="text-base sm:text-lg font-semibold"
            :class="{
              'text-green-600': word.isGrasp === 1,
              'text-red-600': word.isGrasp === 2,
              'text-gray-600': word.isGrasp === 0
            }"
          >
            {{ getGraspStatusText(word.isGrasp) }}
          </div>
          <div class="text-gray-500 text-xs sm:text-sm">掌握状态</div>
        </div>
      </div>
    </div>
    
    <div v-else class="text-gray-500">
      暂无单词数据
    </div>
  </div>
</template>

<script setup>
defineProps({
  word: {
    type: Object,
    default: null
  },
  showChinese: {
    type: Boolean,
    default: true
  }
})

const getGraspStatusText = (isGrasp) => {
  switch (isGrasp) {
    case 1:
      return '已掌握'
    case 2:
      return '错词'
    default:
      return '学习中'
  }
}
</script>