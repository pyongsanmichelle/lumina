<template>
  <div>
    <label :for="fieldId" class="flex items-center gap-1.5 text-xs font-medium mb-1.5" style="color: #475569">
      {{ label }}
      <span v-if="required" class="text-xs font-semibold px-1.5 py-0.5 rounded" style="background: #fee2e2; color: #dc2626">必須</span>
      <span v-else class="text-xs px-1.5 py-0.5 rounded" style="background: #f1f5f9; color: #94a3b8">任意</span>
    </label>
    <select
      :id="fieldId"
      :value="modelValue"
      :aria-invalid="!!error"
      class="w-full text-sm px-3 py-2.5 rounded-lg border outline-none transition-all appearance-none"
      :style="inputStyle"
      @change="onChange"
      @focus="onFocus"
      @blur="onBlur"
    >
      <slot />
    </select>
    <p v-if="error" role="alert" class="flex items-center gap-1 mt-1.5 text-xs font-medium" style="color: #dc2626">
      <span aria-hidden>!</span> {{ error }}
    </p>
    <p v-else-if="hint" class="mt-1 text-xs" style="color: #94a3b8">{{ hint }}</p>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface Props {
  label: string
  modelValue: string
  required?: boolean
  error?: string
  hint?: string
}

const props = withDefaults(defineProps<Props>(), {
  required: false,
  error: undefined,
  hint: undefined,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const fieldId = `field-${props.label}-${Math.random().toString(36).slice(2, 8)}`
const focused = ref(false)

const inputStyle = computed(() => {
  const hasError = !!props.error
  return {
    borderColor: hasError ? '#ef4444' : focused.value ? '#2563eb' : '#e2e8f0',
    color: '#0f172a',
    background: 'white',
    boxShadow: hasError
      ? '0 0 0 3px rgba(239,68,68,0.12)'
      : focused.value
        ? '0 0 0 3px rgba(37,99,235,0.1)'
        : undefined,
  }
})

function onChange(e: Event) {
  const target = e.target as HTMLSelectElement
  emit('update:modelValue', target.value)
}

function onFocus() {
  focused.value = true
}

function onBlur() {
  focused.value = false
}
</script>