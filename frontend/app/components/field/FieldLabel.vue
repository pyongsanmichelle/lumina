<template>
  <label :for="fieldId" class="flex items-center gap-1.5 text-xs font-medium mb-1.5" style="color: #475569">
    {{ label }}
    <span v-if="required" class="text-xs font-semibold px-1.5 py-0.5 rounded" style="background: #fee2e2; color: #dc2626">必須</span>
    <span v-else class="text-xs px-1.5 py-0.5 rounded" style="background: #f1f5f9; color: #94a3b8">任意</span>
  </label>
</template>

<script setup lang="ts">
interface Props {
  label: string
  fieldId: string
  required?: boolean
}

withDefaults(defineProps<Props>(), {
  required: false,
})
</script>
