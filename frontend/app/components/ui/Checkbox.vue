<template>
  <label class="flex items-center gap-2 cursor-pointer select-none" :class="labelClass">
    <input
      type="checkbox"
      :checked="modelValue"
      :disabled="disabled"
      :aria-invalid="!!error"
      class="w-4 h-4 rounded"
      :style="checkboxStyle"
      @change="onChange"
    />
    <span class="text-sm">{{ label }}</span>
  </label>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  label: string
  modelValue: boolean
  disabled?: boolean
  error?: string
}

const props = withDefaults(defineProps<Props>(), {
  disabled: false,
  error: undefined,
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const labelClass = computed(() => {
  if (props.error) return 'text-red-600'
  if (props.disabled) return 'text-gray-400 cursor-not-allowed'
  return 'text-gray-600'
})

const checkboxStyle = computed(() => {
  if (props.error) {
    return {
      accentColor: '#ef4444',
      borderColor: '#ef4444',
    }
  }
  return {
    accentColor: '#2563eb',
  }
})

function onChange(e: Event) {
  const target = e.target as HTMLInputElement
  emit('update:modelValue', target.checked)
}
</script>