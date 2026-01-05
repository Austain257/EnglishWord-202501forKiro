<template>
  <div class="bg-gradient-to-r from-blue-500 to-purple-600 text-white p-6 rounded-lg shadow-lg relative overflow-hidden">
    <div v-if="loading" class="text-center">
      <Loading />
    </div>
    <div v-else-if="quotes.length > 0" class="text-center">
      <!-- 轮播内容 -->
      <div class="relative h-32 md:h-36">
        <Transition
          v-for="(quote, index) in quotes"
          :key="quote.id"
          name="quote-fade"
          mode="out-in"
        >
          <div
            v-if="index === currentIndex"
            class="absolute inset-0 flex flex-col justify-center"
          >
            <blockquote class="text-lg md:text-xl font-medium mb-3 leading-relaxed px-4">
              "{{ quote.content }}"
            </blockquote>
            <footer class="text-sm text-blue-200">
              — {{ quote.author }}
            </footer>
          </div>
        </Transition>
      </div>
      
      <!-- 轮播指示器 -->
      <div v-if="quotes.length > 1" class="flex justify-center space-x-2 mt-4">
        <button
          v-for="(quote, index) in quotes"
          :key="quote.id"
          @click="goToSlide(index)"
          :class="[
            'w-2 h-2 rounded-full transition-all duration-300',
            index === currentIndex 
              ? 'bg-white' 
              : 'bg-white bg-opacity-50 hover:bg-opacity-75'
          ]"
          :aria-label="`跳转到第${index + 1}条文案`"
        />
      </div>
      
      <!-- 播放/暂停按钮 -->
      <button
        v-if="quotes.length > 1"
        @click="toggleAutoPlay"
        class="absolute top-4 right-4 p-2 rounded-full bg-white bg-opacity-20 hover:bg-opacity-30 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50"
        :title="isPlaying ? '暂停轮播' : '开始轮播'"
        :aria-label="isPlaying ? '暂停轮播' : '开始轮播'"
      >
        <svg v-if="isPlaying" class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
          <path d="M6 4h4v16H6V4zm8 0h4v16h-4V4z"/>
        </svg>
        <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
          <path d="M8 5v14l11-7z"/>
        </svg>
      </button>
      
      <!-- 手动切换按钮（移动端友好） -->
      <div v-if="quotes.length > 1" class="absolute inset-y-0 left-0 right-0 flex items-center justify-between px-2 pointer-events-none">
        <button
          @click="prevSlide"
          class="p-2 rounded-full bg-white bg-opacity-20 hover:bg-opacity-30 transition-all duration-200 pointer-events-auto focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50"
          :aria-label="'上一条文案'"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
            <path d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/>
          </svg>
        </button>
        <button
          @click="nextSlide"
          class="p-2 rounded-full bg-white bg-opacity-20 hover:bg-opacity-30 transition-all duration-200 pointer-events-auto focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50"
          :aria-label="'下一条文案'"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24">
            <path d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"/>
          </svg>
        </button>
      </div>
    </div>
    <div v-else class="text-center">
      <p class="text-lg">今天也要加油学习英语！</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useMotivation } from '@/composables/useMotivation'
import Loading from '@/components/common/Loading.vue'

const {
  quotes,
  currentIndex,
  loading,
  isPlaying,
  fetchQuotes,
  goToSlide,
  nextSlide,
  prevSlide,
  startAutoPlay,
  toggleAutoPlay
} = useMotivation()

const refresh = async () => {
  await fetchQuotes()
  if (quotes.value.length > 1) {
    startAutoPlay()
  }
}

onMounted(() => {
  refresh()
})

// 暴露刷新方法给父组件
defineExpose({
  refresh
})
</script>

<style scoped>
.quote-fade-enter-active,
.quote-fade-leave-active {
  transition: all 0.5s ease-in-out;
}

.quote-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.quote-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 移动端优化 */
@media (max-width: 640px) {
  .quote-fade-enter-from,
  .quote-fade-leave-to {
    transform: translateX(20px);
  }
}
</style>