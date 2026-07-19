<template>
  <p v-if="error" role="alert" class="flex items-center gap-1 mt-1.5 text-xs font-medium" style="color: #dc2626">
    <span aria-hidden>!</span> {{ error }}
  </p>
  <p v-else-if="hint" class="mt-1 text-xs" style="color: #94a3b8">{{ hint }}</p>
</template>

<script setup lang="ts">
interface Props {
  error?: string
  hint?: string
}

withDefaults(defineProps<Props>(), {
  error: undefined,
  hint: undefined,
})
</script>
