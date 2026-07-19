<template>
  <div>
    <FieldLabel :label="label" :field-id="fieldId" :required="required" />
    <input
      :id="fieldId"
      :value="modelValue"
      :type="type"
      :placeholder="placeholder"
      :min="min"
      :aria-invalid="!!error"
      class="w-full text-sm px-3 py-2.5 rounded-lg border outline-none transition-all"
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
  type?: string
  placeholder?: string
  required?: boolean
  error?: string
  hint?: string
  min?: number | string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  placeholder: '',
  required: false,
  error: undefined,
  hint: undefined,
  min: undefined,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const fieldId = useFieldId(props.label)
const { inputStyle, onFocus, onBlur } = useFieldState(() => props.error)

function onInput(e: Event) {
  const target = e.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>
