<template>
  <div :class="containerClasses">
    <div :class="spinnerClasses">
      <svg
        class="animate-spin h-full w-full"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
      >
        <circle
          class="opacity-25"
          cx="12"
          cy="12"
          r="10"
          stroke="currentColor"
          stroke-width="4"
        ></circle>
        <path
          class="opacity-75"
          fill="currentColor"
          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
        ></path>
      </svg>
    </div>
    
    <p v-if="text" :class="textClasses">{{ text }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg', 'xl'].includes(value)
  },
  text: {
    type: String,
    default: ''
  },
  overlay: {
    type: Boolean,
    default: false
  },
  color: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'gray', 'white'].includes(value)
  }
})

const containerClasses = computed(() => {
  const baseClasses = 'flex flex-col items-center justify-center'
  const overlayClass = props.overlay ? 'fixed inset-0 bg-black bg-opacity-50 z-50' : ''
  
  return [baseClasses, overlayClass].filter(Boolean).join(' ')
})

const spinnerClasses = computed(() => {
  const sizeClasses = {
    sm: 'h-4 w-4',
    md: 'h-8 w-8',
    lg: 'h-12 w-12',
    xl: 'h-16 w-16'
  }
  
  const colorClasses = {
    primary: 'text-primary-600',
    gray: 'text-gray-600',
    white: 'text-white'
  }
  
  return [
    sizeClasses[props.size],
    colorClasses[props.color]
  ].join(' ')
})

const textClasses = computed(() => {
  const colorClasses = {
    primary: 'text-gray-600',
    gray: 'text-gray-600',
    white: 'text-white'
  }
  
  return [
    'mt-2 text-sm font-medium',
    colorClasses[props.color]
  ].join(' ')
})
</script>