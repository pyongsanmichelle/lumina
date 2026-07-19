<template>
  <div>
    <FieldLabel :label="label" :field-id="fieldId" :required="required" />
    <textarea
      :id="fieldId"
      :value="modelValue"
      :placeholder="placeholder"
      :rows="rows"
      :aria-invalid="!!error"
      class="w-full text-sm px-3 py-2.5 rounded-lg border outline-none transition-all resize-y"
      :style="inputStyle"
      @input="onInput"
      @focus="onFocus"
      @blur="onBlur"
    />
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
  placeholder?: string
  rows?: number
  required?: boolean
  error?: string
  hint?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '',
  rows: 5,
  required: false,
  error: undefined,
  hint: undefined,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const fieldId = useFieldId(props.label)
const { inputStyle, onFocus, onBlur } = useFieldState(() => props.error, { lineHeight: 1.6 })

function onInput(e: Event) {
  const target = e.target as HTMLTextAreaElement
  emit('update:modelValue', target.value)
}
</script>
