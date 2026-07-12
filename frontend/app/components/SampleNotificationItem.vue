<template>
  <div
    class="flex items-start gap-4 px-5 py-4 transition-colors cursor-pointer"
    :style="{ borderBottom: hasBorder ? '1px solid #f8fafc' : 'none' }"
    @mouseenter="hovered = true"
    @mouseleave="hovered = false"
  >
    <div
      class="w-2 h-2 rounded-full mt-1.5 flex-shrink-0"
      :style="{ background: colorMap[type] }"
    />
    <div class="flex-1 min-w-0">
      <div class="text-sm font-medium" style="color: #1e293b">{{ title }}</div>
      <div class="text-xs mt-0.5 leading-relaxed" style="color: #64748b">{{ body }}</div>
    </div>
    <div class="text-xs flex-shrink-0 mt-0.5" style="color: #94a3b8">{{ time }}</div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  type: 'warning' | 'info' | 'success' | 'error'
  title: string
  body: string
  time: string
  hasBorder?: boolean
}

defineProps<Props>()

const colorMap: Record<string, string> = {
  warning: '#f59e0b',
  info: '#2563eb',
  success: '#22c55e',
  error: '#ef4444',
}
</script>