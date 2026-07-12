<template>
  <div
    aria-live="polite"
    class="fixed flex flex-col gap-2.5"
    style="top: 72px; right: 24px; z-index: 200; pointer-events: none"
  >
    <div
      v-for="toast in toasts"
      :key="toast.id"
      style="pointer-events: auto"
    >
      <div
        role="alert"
        aria-live="polite"
        class="flex items-start gap-3 px-4 py-3.5 rounded-xl shadow-lg max-w-sm w-full"
        :style="toastContainerStyle(toast.type)"
      >
        <div
          class="w-6 h-6 rounded-full flex items-center justify-center flex-shrink-0 text-white text-xs font-bold mt-0.5"
          :style="{ background: toastConfig[toast.type].iconBg }"
        >
          {{ toastConfig[toast.type].icon }}
        </div>
        <div class="flex-1 min-w-0">
          <div class="text-sm font-semibold" :style="{ color: toastConfig[toast.type].title }">
            {{ toast.title }}
          </div>
          <div v-if="toast.message" class="text-xs mt-0.5 leading-relaxed" :style="{ color: toastConfig[toast.type].title, opacity: 0.8 }">
            {{ toast.message }}
          </div>
        </div>
        <button
          class="flex-shrink-0 text-xs leading-none mt-0.5 transition-opacity opacity-50 hover:opacity-100"
          :style="{ color: toastConfig[toast.type].title }"
          aria-label="閉じる"
          @click="removeToast(toast.id)"
        >
          ✕
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ToastData } from '~/types/sample'

interface Props {
  toasts: ToastData[]
  removeToast: (id: string) => void
}

defineProps<Props>()

const toastConfig: Record<ToastData['type'], { bg: string; border: string; icon: string; iconBg: string; title: string }> = {
  success: {
    bg: '#f0fdf4',
    border: '#bbf7d0',
    icon: '✓',
    iconBg: '#22c55e',
    title: '#15803d',
  },
  warning: {
    bg: '#fffbeb',
    border: '#fde68a',
    icon: '!',
    iconBg: '#f59e0b',
    title: '#92400e',
  },
  error: {
    bg: '#fef2f2',
    border: '#fecaca',
    icon: '×',
    iconBg: '#ef4444',
    title: '#991b1b',
  },
}

function toastContainerStyle(type: ToastData['type']) {
  const c = toastConfig[type]
  return {
    background: c.bg,
    border: `1px solid ${c.border}`,
  }
}
</script>

<style scoped>
@keyframes toast-in {
  from { opacity: 0; transform: translateX(16px) scale(0.97); }
  to   { opacity: 1; transform: translateX(0) scale(1); }
}
[role="alert"] {
  animation: toast-in 0.25s ease-out;
}
</style>