<template>
  <div>
    <FieldLabel :label="label" :field-id="fieldId" :required="required" />
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
    <FieldMessage :error="error" :hint="hint" />
  </div>
</template>

<script setup lang="ts">
import FieldLabel from '../field/FieldLabel.vue'
import FieldMessage from '../field/FieldMessage.vue'
import { useFieldId, useFieldState } from '~/composables/useField'

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

const fieldId = useFieldId(props.label)
const { inputStyle, onFocus, onBlur } = useFieldState(() => props.error)

function onChange(e: Event) {
  const target = e.target as HTMLSelectElement
  emit('update:modelValue', target.value)
}
</script>
