<template>
  <div class="min-h-screen bg-[#F8FAFC] flex flex-col relative overflow-hidden font-sans text-slate-800">
    <!-- 背景装饰 (使用紫色/靛青色调区分句子学习) -->
    <div class="absolute top-0 left-0 w-full h-96 bg-gradient-to-b from-indigo-50/80 to-transparent pointer-events-none"></div>
    <div class="absolute -top-20 -right-20 w-96 h-96 bg-purple-100/40 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute top-40 -left-20 w-72 h-72 bg-indigo-100/40 rounded-full blur-3xl pointer-events-none"></div>

    <!-- 顶部导航 -->
    <nav class="relative z-10 px-4 sm:px-6 lg:px-8 h-16 sm:h-20 flex items-center justify-between">
      <div class="flex items-center gap-4">
        <button 
          @click="goBack" 
          class="p-2 -ml-2 text-slate-500 hover:text-slate-900 hover:bg-white/60 rounded-xl transition-all duration-200"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <div class="flex flex-col">
          <h1 class="text-lg font-bold text-slate-900 tracking-tight">句子学习</h1>
          <div class="flex items-center gap-2 text-xs text-slate-500">
             <span class="inline-block w-1.5 h-1.5 rounded-full bg-indigo-500"></span>
             {{ learningRange.start }}-{{ learningRange.end }}
          </div>
        </div>
      </div>

            <div class="flex items-center gap-2 sm:gap-3">
        <!-- Progress -->
        <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>进度</span>
          <span class="text-indigo-600 font-bold">{{ currentIndex + 1 }}</span>
          <span class="text-slate-400">/</span>
          <span>{{ totalSentences }}</span>
        </div>

        <!-- Current Range -->
        <div class="hidden md:flex items-center gap-2 px-3 py-1.5 bg-white/60 backdrop-blur-sm rounded-lg border border-slate-200/60 text-xs font-medium text-slate-600">
          <span>当前范围</span>
          <span class="text-indigo-600 font-bold">{{ learningRange.start }}-{{ learningRange.end }}</span>
        </div>
        
        <!-- Range Settings -->
        <button 
          @click="showRangeModal = true"
          class="flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-slate-600 hover:text-indigo-600 hover:border-indigo-200 transition-all active:scale-95"
          title="设置范围"
        >
          <span class="hidden sm:inline">修改学习范围</span>
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
        </button>

        <!-- 下5个 -->
        <button
          @click="jumpNextFive"
          class="hidden sm:flex items-center gap-2 px-3 py-2 bg-white shadow-sm border border-slate-200/60 rounded-xl text-xs font-medium text-emerald-600 hover:text-emerald-700 hover:border-emerald-200 transition-all active:scale-95"
          title="跳到后5个"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5-5 5M6 5h4m-4 4h7m-7 4h4" />
          </svg>
          下5个
        </button>

        <!-- 完成学习 -->
        <button
          @click="completeLearning"
          class="flex items-center gap-2 px-3 py-2 bg-gradient-to-r from-emerald-600 to-teal-600 text-white rounded-xl text-xs font-semibold shadow-lg shadow-emerald-500/30 hover:shadow-emerald-500/40 transition-all active:scale-95"
          title="保存本次学习范围"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
          完成学习
        </button>
      </div>
    </nav>

    <!-- 主体内容 -->
    <main class="flex-1 relative z-10 flex flex-col max-w-4xl mx-auto w-full px-4 sm:px-6 lg:px-8 pb-6">
      
      <!-- 进度条 (移动端) -->
      <div class="sm:hidden mb-6 px-1">
        <div class="flex justify-between text-xs font-medium text-slate-500 mb-2">
          <span>当前进度</span>
          <span>{{ Math.round(((currentIndex + 1) / totalSentences) * 100) }}%</span>
        </div>
        <div class="h-2 bg-slate-100 rounded-full overflow-hidden">
          <div 
            class="h-full bg-indigo-500 rounded-full transition-all duration-300 ease-out"
            :style="{ width: `${((currentIndex + 1) / totalSentences) * 100}%` }"
          ></div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="flex-1 flex flex-col items-center justify-center min-h-[400px]">
        <div class="w-12 h-12 border-4 border-indigo-100 border-t-indigo-500 rounded-full animate-spin mb-4"></div>
        <p class="text-slate-400 text-sm font-medium animate-pulse">正在加载句子数据...</p>
      </div>

      <!-- 无数据状态 -->
      <div v-else-if="!currentSentence" class="flex-1 flex flex-col items-center justify-center min-h-[400px] text-center">
        <div class="w-20 h-20 bg-slate-50 rounded-3xl flex items-center justify-center mb-6 text-slate-300">
          <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
          </svg>
        </div>
        <h3 class="text-xl font-bold text-slate-900 mb-2">暂无句子</h3>
        <p class="text-slate-500 mb-8 max-w-xs mx-auto">当前范围内没有找到句子，请尝试调整学习范围。</p>
        <button 
          @click="showRangeModal = true" 
          class="px-8 py-3 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl font-semibold shadow-lg shadow-indigo-500/30 transition-all hover:-translate-y-0.5"
        >
          重新加载
        </button>
      </div>

      <!-- 句子卡片 -->
      <div v-else class="flex-1 flex flex-col justify-center py-4 sm:py-8">
        <div class="relative group w-full max-w-3xl mx-auto">
          <!-- 卡片容器 -->
          <div 
            class="relative bg-white rounded-[2rem] shadow-xl shadow-slate-200/60 border border-slate-100 overflow-hidden transition-all duration-500 min-h-[360px] flex flex-col"
          >
            <!-- 状态标签 -->
            <div class="absolute top-6 right-6 z-10">
              <span 
                class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border"
                :class="{
                  'bg-emerald-50 text-emerald-600 border-emerald-100': currentSentence.isGrasp === 1,
                  'bg-rose-50 text-rose-600 border-rose-100': currentSentence.isGrasp === 2,
                  'bg-slate-50 text-slate-500 border-slate-100': !currentSentence.isGrasp || currentSentence.isGrasp === 0
                }"
              >
                {{ getGraspText(currentSentence.isGrasp) }}
              </span>
            </div>

            <div class="flex-1 p-8 sm:p-12 flex flex-col items-center justify-center text-center">
              
              <!-- 句子主体 -->
              <div class="mb-8 w-full max-h-[40vh] overflow-y-auto custom-scrollbar px-2">
                <h2 class="text-2xl sm:text-3xl md:text-4xl font-medium text-slate-900 leading-relaxed tracking-tight break-words text-wrap">
                  {{ currentSentence.sentence }}
                </h2>
              </div>
              
              <!-- 播放按钮 -->
              <div class="mb-8">
                 <button 
                  @click="playPronunciation"
                  class="flex items-center gap-2 px-4 py-2 rounded-full bg-indigo-50 text-indigo-600 hover:bg-indigo-100 hover:scale-105 transition-all text-sm font-medium"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z" />
                  </svg>
                  播放发音
                </button>
              </div>

              <!-- 译文区域 (直接显示) -->
              <div class="w-full max-w-2xl">
                <div class="text-lg sm:text-xl text-slate-600 font-medium leading-relaxed px-6 py-4 rounded-2xl bg-slate-50 border border-slate-100/50 min-h-[80px] flex items-center justify-center">
                  {{ currentSentence.chinese || '暂无译文' }}
                </div>
              </div>
            </div>

            <!-- 底部统计条 -->
            <div class="bg-slate-50/50 border-t border-slate-100 px-6 py-4 flex justify-between items-center text-xs sm:text-sm text-slate-500">
               <div class="flex items-center gap-2">
                 <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                 <span>已学 {{ currentSentence.times || 0 }} 次</span>
               </div>
               <div class="flex items-center gap-2">
                 <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
                 <span>错误 {{ currentSentence.errorTimes || 0 }} 次</span>
               </div>
            </div>

          </div>
        </div>
      </div>

      <!-- 底部控制栏 -->
      <div v-if="currentSentence" class="mt-auto pt-6 flex items-center justify-center gap-3 sm:gap-6 flex-wrap sm:flex-nowrap">
        <button 
          @click="prevSentence"
          :disabled="!hasPrev"
          class="flex items-center justify-center gap-2 px-7 py-3.5 sm:px-10 sm:py-4 rounded-xl font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed text-slate-600 bg-white border border-slate-200 shadow-sm hover:bg-slate-50 hover:border-slate-300 active:scale-95 w-[46%] sm:w-[230px]"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          <span class="hidden sm:inline">上一句</span>
          <span class="sm:hidden">上一句</span>
        </button>

        <button 
          @click="nextSentence"
          :disabled="!hasNext"
          class="flex items-center justify-center gap-2 px-7 py-3.5 sm:px-10 sm:py-4 rounded-xl font-bold transition-all disabled:opacity-50 disabled:cursor-not-allowed text-white bg-gradient-to-r from-indigo-600 to-purple-600 shadow-lg shadow-indigo-500/30 hover:shadow-indigo-500/40 hover:-translate-y-0.5 active:scale-95 active:translate-y-0 w-[46%] sm:w-[230px]"
        >
          <span class="hidden sm:inline">下一句</span>
          <span class="sm:hidden">下一句</span>
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>
    </main>

    <!-- 入口范围确认弹窗 -->
    <div v-if="showEntryModal" class="fixed inset-0 z-[9999] flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/50 backdrop-blur-sm transition-opacity" @click="cancelEntryRange"></div>
      <div class="relative w-full max-w-md bg-white rounded-3xl shadow-2xl p-6 sm:p-7 animate-scale-in">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" /></svg>
          </div>
          <div>
            <p class="text-sm uppercase tracking-[0.25em] text-slate-400">Start</p>
            <h3 class="text-xl font-bold text-slate-900">默认范围 {{ entryRange.start }} - {{ entryRange.end }}</h3>
          </div>
        </div>
        <p class="text-slate-600 text-sm leading-relaxed mb-4">可在下方调整默认范围，点击确定后开始学习并记录本次 start_id。</p>

        <div class="grid grid-cols-2 gap-3 mb-5">
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Start</label>
            <input 
              v-model.number="tempRange.start"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">End</label>
            <input 
              v-model.number="tempRange.end"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 sm:grid-cols-4 gap-2 mb-5">
          <button 
            v-for="(range, idx) in quickRanges" 
            :key="idx"
            @click="setQuickRange(range.start, range.end)"
            class="px-2 py-2 text-xs font-medium rounded-lg border hover:border-indigo-300 hover:bg-indigo-50 hover:text-indigo-600 transition-colors text-slate-600 border-slate-200"
          >
            {{ range.label }}
          </button>
        </div>

        <div class="flex gap-3">
          <button @click="cancelEntryRange" class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors">取消</button>
          <button @click="confirmEntryRange" class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-indigo-600 hover:bg-indigo-700 shadow-lg shadow-indigo-500/20 transition-colors">确定</button>
        </div>
      </div>
    </div>

    <!-- 范围设置模态框 -->
    <div v-if="showRangeModal" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity" @click="cancelRangeSelection"></div>
      <div class="relative w-full max-w-lg bg-white rounded-3xl shadow-2xl p-6 sm:p-8 animate-scale-in">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-slate-900">选择学习范围</h3>
          <button @click="cancelRangeSelection" class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>

        <div class="bg-indigo-50 rounded-2xl p-5 mb-6 text-sm text-indigo-800 space-y-2">
           <div class="flex justify-between">
              <span class="opacity-70">当前进度</span>
              <span class="font-bold">{{ currentIndex + 1 }} / {{ totalSentences }}</span>
           </div>
           <div class="flex justify-between">
              <span class="opacity-70">学习区间</span>
              <span class="font-bold">{{ learningRange.start }} - {{ learningRange.end }}</span>
           </div>
        </div>

        <div class="flex gap-4 mb-6">
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">Start</label>
            <input 
              v-model.number="tempRange.start"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
          <div class="flex items-end pb-4 text-slate-300">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" /></svg>
          </div>
          <div class="flex-1">
            <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-2">End</label>
            <input 
              v-model.number="tempRange.end"
              type="number" 
              class="w-full px-4 py-3 bg-slate-50 border-2 border-slate-100 rounded-xl focus:outline-none focus:border-indigo-500 focus:bg-white transition-all font-semibold text-slate-900 text-center"
            />
          </div>
        </div>

        <div class="mb-8">
           <label class="block text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">Quick Select</label>
           <div class="grid grid-cols-4 gap-2">
             <button 
                v-for="(range, idx) in quickRanges" 
                :key="idx"
                @click="setQuickRange(range.start, range.end)"
                class="px-2 py-2 text-xs font-medium rounded-lg border hover:border-indigo-300 hover:bg-indigo-50 hover:text-indigo-600 transition-colors text-slate-600 border-slate-200"
             >
               {{ range.label }}
             </button>
           </div>
        </div>

        <div class="flex gap-3">
          <button @click="cancelRangeSelection" class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors">取消</button>
          <button @click="confirmRangeSelection" class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-indigo-600 hover:bg-indigo-700 shadow-lg shadow-indigo-500/20 transition-colors">确认应用</button>
        </div>
      </div>
    </div>

    <!-- 首次学习对话框 -->
    <div v-if="firstLearnDialog" class="fixed inset-0 z-50 flex items-center justify-center px-4">
      <div class="absolute inset-0 bg-slate-900/50 backdrop-blur-sm" @click="cancelFirstLearn"></div>
      <div class="relative w-full max-w-md bg-white rounded-3xl shadow-2xl p-6 sm:p-8 animate-scale-in">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-bold text-slate-900">开始学习句子</h3>
          <button @click="cancelFirstLearn" class="p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
          </button>
        </div>
        <p class="text-slate-600 mb-6">检测到你还没有句子学习记录，是否从 {{ firstLearnRange.start }}-{{ firstLearnRange.end }} 号句子开始？</p>
        <div class="flex gap-3">
          <button @click="cancelFirstLearn" class="flex-1 px-6 py-3.5 rounded-xl font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 transition-colors">稍后</button>
          <button @click="confirmFirstLearn" class="flex-1 px-6 py-3.5 rounded-xl font-bold text-white bg-indigo-600 hover:bg-indigo-700 shadow-lg shadow-indigo-500/20 transition-colors">开始学习</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSentenceStore } from '@/stores/sentence'
import { useAuthStore } from '@/stores/auth'
import axios from 'axios'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const sentenceStore = useSentenceStore()
const authStore = useAuthStore()
const { showToast } = useToast()

// 基础配置
const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://192.168.0.106/api'

// 状态
const showRangeModal = ref(false)
const tempRange = ref({ start: 1, end: 30 })
const entryRange = ref({ start: 1, end: 10 })
const showEntryModal = ref(false)
const pendingRange = ref(null)
const sessionStartId = ref(null)
const firstLearnDialog = ref(false)
const firstLearnRange = ref({ start: 1, end: 10 })
const initializing = ref(true)
const latestRecord = ref(null)
const totalSentenceCount = ref(0)

// 计算属性
const loading = computed(() => sentenceStore.loading)
const currentSentence = computed(() => sentenceStore.currentSentence)
const currentIndex = computed(() => sentenceStore.currentIndex)
const totalSentences = computed(() => sentenceStore.totalSentences)
const hasNext = computed(() => sentenceStore.hasNext)
const hasPrev = computed(() => sentenceStore.hasPrev)
const learningRange = computed(() => sentenceStore.learningRange)

// 快速选择范围
const quickRanges = [
  { label: '1-30', start: 1, end: 30 },
  { label: '31-60', start: 31, end: 60 },
  { label: '61-90', start: 61, end: 90 },
  { label: '91-120', start: 91, end: 120 }
]

// 辅助方法
const getGraspText = (status) => {
  switch(status) {
    case 1: return '已掌握'
    case 2: return '易错'
    default: return '学习中'
  }
}

const playPronunciation = () => {
  if (!currentSentence.value) return
  
  const text = currentSentence.value.sentence
  if ('speechSynthesis' in window) {
    window.speechSynthesis.cancel()
    const utterance = new SpeechSynthesisUtterance(text)
    utterance.lang = 'en-US'
    utterance.rate = 0.9
    window.speechSynthesis.speak(utterance)
  } else {
    showToast('您的浏览器不支持语音播放', 'error')
  }
}

// 核心功能
const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const applyPendingRange = (range) => {
  if (!range) return
  const normalized = {
    start: Math.max(1, range.start),
    end: Math.max(range.start, range.end)
  }
  if (sessionStartId.value === null) {
    sessionStartId.value = normalized.start
  }
  pendingRange.value = { ...normalized }
  sentenceStore.setLearningRange({ ...normalized })
  tempRange.value = { ...normalized }
}

const confirmEntryRange = async () => {
  if (tempRange.value.start >= tempRange.value.end) {
    return showToast('结束句子编号必须大于起始句子编号', 'error')
  }
  if (tempRange.value.start < 1) {
    return showToast('起始句子编号不能小于1', 'error')
  }
  entryRange.value = { ...tempRange.value }
  showEntryModal.value = false
  applyPendingRange(tempRange.value)
  await loadSentences(tempRange.value)
  showToast(`已开始学习句子 ${tempRange.value.start}-${tempRange.value.end}`, 'success')
}

const cancelEntryRange = () => {
  showEntryModal.value = false
  tempRange.value = { ...entryRange.value }
  goBack()
}

const loadSentences = async (range = null) => {
  try {
    const targetRange = range || pendingRange.value || learningRange.value
    await sentenceStore.fetchSentences(targetRange)
    if (sentenceStore.sentences.length === 0) {
      showToast('当前范围内没有找到句子，请尝试调整学习范围', 'warning')
    }
  } catch (error) {
    console.error('加载句子失败:', error)
    showToast('加载句子失败，请重试', 'error')
  }
}

const nextSentence = () => {
  if (hasNext.value) {
    sentenceStore.nextSentence()
  }
}

const prevSentence = () => {
  if (hasPrev.value) {
    sentenceStore.prevSentence()
  }
}

// 范围设置
const confirmRangeSelection = async () => {
  if (tempRange.value.start >= tempRange.value.end) {
    return showToast('结束句子编号必须大于起始句子编号', 'error')
  }
  if (tempRange.value.start < 1) {
    return showToast('起始句子编号不能小于1', 'error')
  }
  
  showRangeModal.value = false
  applyPendingRange(tempRange.value)
  await loadSentences(tempRange.value)
  showToast(`已切换到句子 ${tempRange.value.start}-${tempRange.value.end}`, 'success')
}

const cancelRangeSelection = () => {
  showRangeModal.value = false
  tempRange.value = { ...learningRange.value }
}

const setQuickRange = (start, end) => {
  tempRange.value.start = start
  tempRange.value.end = end
}

const jumpNextFive = async () => {
  const current = pendingRange.value || learningRange.value
  const total = totalSentenceCount.value || current.end + 5
  const nextStart = (current?.end || 0) + 1
  if (nextStart > total) {
    showToast('已经到末尾了', 'info')
    return
  }
  const nextEnd = Math.min(nextStart + 4, total)
  const nextRange = { start: nextStart, end: nextEnd }
  applyPendingRange(nextRange)
  await loadSentences(nextRange)
  showToast(`已切换到句子 ${nextRange.start}-${nextRange.end}`, 'success')
}

const fetchSentenceTotalCount = async (userId) => {
  const authHeader = authStore.token ? { Authorization: `Bearer ${authStore.token}` } : {}
  try {
    const res = await axios.post(
      `${API_BASE}/api/english/sentence`,
      { userId },
      {
        headers: authHeader,
        validateStatus: (status) => status >= 200 && status < 500
      }
    )
    const { code, data, msg } = res.data || {}
    if (code === 1 && Array.isArray(data)) return data.length
    if (code === 0 && msg?.includes('暂时还没有句子')) return 0
    return Array.isArray(data) ? data.length : 0
  } catch (err) {
    console.error('获取句子总数失败', err)
    return 0
  }
}

// 初始化：获取最新句子学习记录
const fetchLatestSentenceRecord = async (userId, bookId = 9999) => {
  const authHeader = authStore.token ? { Authorization: `Bearer ${authStore.token}` } : {}
  const url = `${API_BASE}/api/sentence-study/latest-record/${userId}`
  const config = {
    params: { bookId },
    headers: authHeader,
    validateStatus: (status) => status >= 200 && status < 500
  }
  try {
    const res = await axios.get(url, config)
    const { code, data, msg } = res.data || {}
    if (code === 1) return data
    if (code === 0 && msg?.includes('未找到学习记录')) return null
    return null
  } catch (err) {
    console.error('获取句子学习记录失败', err)
    return null
  }
}

const calculateNextRange = (record, totalCount) => {
  const previousEnd = record?.endId || 0
  const startId = Math.max(previousEnd + 1, 1)
  if (totalCount && startId > totalCount) {
    return null
  }
  const endId = totalCount ? Math.min(totalCount, startId + 4) : startId + 4
  return { start: startId, end: endId }
}

const initializeFromLatestRecord = async () => {
  if (!authStore.user?.id) {
    showToast('请先登录后再开始学习', 'error')
    initializing.value = false
    return
  }

  try {
    initializing.value = true
    sessionStartId.value = null
    // 先获取句子总数，用于计算范围
    const totalCount = await fetchSentenceTotalCount(authStore.user.id)
    totalSentenceCount.value = totalCount
    const record = await fetchLatestSentenceRecord(authStore.user.id, 9999)
    latestRecord.value = record || null

    let range
    if (!record) {
      const defaultEnd = Math.min(10, totalCount || 10)
      entryRange.value = { start: 1, end: defaultEnd }
      tempRange.value = { ...entryRange.value }
      showEntryModal.value = true
      return
    } else {
      range = calculateNextRange(record, totalCount)
      if (!range) {
        const defaultEnd = Math.min(10, totalCount || 10)
        range = { start: 1, end: defaultEnd }
        showToast('当前句子已全部学习，重新从第1-10句开始', 'info')
      }
    }

    entryRange.value = { ...(range || { start: 1, end: Math.min(10, totalCount || 10) }) }
    tempRange.value = { ...entryRange.value }
    showEntryModal.value = true
  } catch (err) {
    console.error('初始化句子学习失败', err)
    const fallbackEnd = Math.min(10, sentenceStore.totalSentences || 10)
    const fallbackRange = { start: 1, end: fallbackEnd }
    applyPendingRange(fallbackRange)
    await loadSentences(fallbackRange)
  } finally {
    initializing.value = false
  }
}

const confirmFirstLearn = async () => {
  applyPendingRange(firstLearnRange.value)
  firstLearnDialog.value = false
  await loadSentences(firstLearnRange.value)
}

const cancelFirstLearn = () => {
  firstLearnDialog.value = false
  goBack()
}

// 完成学习，写入 sentence_study_record
const completeLearning = async () => {
  if (!authStore.user?.id) {
    showToast('请先登录', 'error')
    return
  }
  const range = pendingRange.value || learningRange.value
  const startId = sessionStartId.value || range?.start || 1
  const endId = range?.end || 1
  const payload = {
    userId: authStore.user.id,
    bookId: 9999,
    startId,
    endId,
    status: 0
  }
  const authHeader = authStore.token ? { Authorization: `Bearer ${authStore.token}` } : {}
  try {
    const res = await axios.post(`${API_BASE}/api/sentence-study/record`, payload, { headers: authHeader })
    const { code, msg } = res.data || {}
    if (code === 1) {
      showToast('学习记录已保存', 'success')
      await initializeFromLatestRecord()
    } else {
      showToast(msg || '保存学习记录失败', 'error')
    }
  } catch (err) {
    console.error('保存句子学习记录失败', err)
    showToast('保存学习记录失败', 'error')
  }
}

// 键盘支持
const handleKeyPress = (event) => {
  if (showRangeModal.value) return
  
  switch (event.key) {
    case 'ArrowLeft':
      if (hasPrev.value) prevSentence()
      break
    case 'ArrowRight':
      if (hasNext.value) nextSentence()
      break
    case ' ':
      event.preventDefault()
      if (hasPrev.value) prevSentence()
      break
    case 'Enter':
      event.preventDefault()
      if (hasNext.value) nextSentence()
      break
    case 'Escape':
      goBack()
      break
  }
}

// 生命周期
onMounted(async () => {
  tempRange.value = { ...learningRange.value }
  await initializeFromLatestRecord()
  window.addEventListener('keydown', handleKeyPress)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyPress)
  window.speechSynthesis.cancel()
  sentenceStore.resetState()
})
</script>

<style scoped>
@keyframes scale-in {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}
.animate-scale-in {
  animation: scale-in 0.2s ease-out forwards;
}

/* 自定义滚动条 */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #e2e8f0;
  border-radius: 20px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background-color: #cbd5e1;
}
</style>
