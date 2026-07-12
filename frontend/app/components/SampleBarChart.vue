<template>
  <div class="flex flex-col gap-3">
    <div v-for="(item, i) in data" :key="i" class="flex items-center gap-3">
      <span class="text-xs text-right flex-shrink-0" style="color: #64748b; width: 72px">{{ item.name }}</span>
      <div class="flex-1 h-4 rounded-sm relative" style="background: #f1f5f9">
        <div
          class="h-full rounded-sm transition-all"
          :style="{
            width: barWidth(item.value),
            background: '#2563eb',
          }"
        />
      </div>
      <span class="text-xs font-medium flex-shrink-0" style="color: #0f172a; width: 36px; text-align: right">{{ item.value }}%</span>
    </div>
  </div>
</template>

<script setup lang="ts">
interface BarItem {
  name: string
  value: number
}

interface Props {
  data: BarItem[]
  maxValue?: number
}

const props = withDefaults(defineProps<Props>(), {
  maxValue: 50,
})

function barWidth(value: number): string {
  return `${(value / props.maxValue) * 100}%`
}
</script>