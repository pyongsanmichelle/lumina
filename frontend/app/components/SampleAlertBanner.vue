<template>
  <div
    v-if="!dismissed"
    class="flex items-start gap-3 px-4 py-3 rounded-xl border"
    :style="bannerStyle"
  >
    <div class="flex-1 min-w-0">
      <div class="text-sm font-semibold" :style="{ color: titleColor }">{{ title }}</div>
      <div v-if="message" class="text-xs mt-0.5 leading-relaxed" :style="{ color: titleColor, opacity: 0.8 }">{{ message }}</div>
    </div>
    <button
      v-if="onDismiss"
      class="flex-shrink-0 text-xs leading-none transition-opacity opacity-50 hover:opacity-100"
      :style="{ color: titleColor }"
      aria-label="閉じる"
      @click="dismissed = true; onDismiss()"
    >
      ✕
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Props {
  type: 'error' | 'warning'
  title: string
  message?: string
  onDismiss?: () => void
}

const props = defineProps<Props>()
const dismissed = ref(false)

const config: Record<'error' | 'warning', { bg: string; border: string; title: string }> = {
  error: { bg: '#fef2f2', border: '#fecaca', title: '#991b1b' },
  warning: { bg: '#fffbeb', border: '#fde68a', title: '#92400e' },
}

function getType(): 'error' | 'warning' {
  return props.type
}

const bannerStyle = computed(() => {
  const c = config[getType()]
  return {
    background: c.bg,
    borderColor: c.border,
  }
})

const titleColor = computed(() => config[getType()].title)
</script>