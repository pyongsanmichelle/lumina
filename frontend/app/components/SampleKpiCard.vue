<template>
  <div class="bg-white rounded-xl p-5 border" style="border-color: #e2e8f0">
    <div class="text-xs font-medium mb-3" style="color: #64748b">{{ label }}</div>
    <div class="text-2xl font-bold mb-2" style="color: #0f172a">{{ value }}</div>
    <div class="flex items-center gap-1.5">
      <span
        class="text-xs font-semibold px-1.5 py-0.5 rounded"
        :style="deltaStyle"
      >
        {{ deltaUp ? '▲' : '▼' }} {{ delta }}
      </span>
      <span class="text-xs" style="color: #94a3b8">{{ sub }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  label: string
  value: string
  delta: string
  deltaUp: boolean
  sub: string
}

const props = defineProps<Props>()

const deltaStyle = computed(() => ({
  background: props.deltaUp ? '#dcfce7' : '#fee2e2',
  color: props.deltaUp ? '#16a34a' : '#dc2626',
}))
</script>