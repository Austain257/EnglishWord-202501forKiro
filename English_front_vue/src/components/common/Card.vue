<template>
  <div :class="cardClasses">
    <div v-if="$slots.header" class="card-header">
      <slot name="header"></slot>
    </div>
    
    <div class="card-body">
      <slot></slot>
    </div>
    
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  shadow: {
    type: String,
    default: 'sm',
    validator: (value) => ['none', 'sm', 'md', 'lg'].includes(value)
  },
  padding: {
    type: String,
    default: 'md',
    validator: (value) => ['none', 'sm', 'md', 'lg'].includes(value)
  },
  hover: {
    type: Boolean,
    default: false
  },
  bordered: {
    type: Boolean,
    default: true
  }
})

const cardClasses = computed(() => {
  const baseClasses = 'bg-white rounded-lg'
  
  const shadowClasses = {
    none: '',
    sm: 'shadow-sm',
    md: 'shadow-md',
    lg: 'shadow-lg'
  }
  
  const paddingClasses = {
    none: '',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8'
  }
  
  const borderClass = props.bordered ? 'border border-gray-200' : ''
  const hoverClass = props.hover ? 'hover:shadow-card-hover transition-shadow duration-200' : ''
  
  return [
    baseClasses,
    shadowClasses[props.shadow],
    paddingClasses[props.padding],
    borderClass,
    hoverClass
  ].filter(Boolean).join(' ')
})
</script>

<style scoped>
.card-header {
  @apply pb-4 border-b border-gray-200 mb-4;
}

.card-footer {
  @apply pt-4 border-t border-gray-200 mt-4;
}
</style>