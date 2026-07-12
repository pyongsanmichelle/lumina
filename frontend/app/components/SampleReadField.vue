<template>
  <div>
    <div class="text-xs font-medium mb-1" style="color: #64748b">{{ label }}</div>
    <div
      class="text-sm py-2 px-3 rounded-lg"
      :style="{
        background: '#f8fafc',
        color: '#1e293b',
        fontFamily: mono ? 'var(--font-mono)' : undefined,
        border: '1px solid #e2e8f0',
      }"
    >
      {{ value }}
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  label: string
  value: string
  mono?: boolean
}

defineProps<Props>()
</script>