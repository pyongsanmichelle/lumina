<template>
  <header
    class="fixed top-0 right-0 h-14 flex items-center justify-between px-6 border-b bg-white"
    style="left: 240px; border-color: #e2e8f0; z-index: 40"
  >
    <div class="flex items-center gap-2">
      <span class="text-xs" style="color: #94a3b8">管理システム</span>
      <span class="text-xs" style="color: #cbd5e1">/</span>
      <span class="text-sm font-semibold" style="color: #0f172a">{{ pageTitle }}</span>
    </div>

    <div class="flex items-center gap-3">
      <!-- Search -->
      <div class="relative hidden md:block">
        <input
          type="text"
          placeholder="クイック検索..."
          class="text-sm pl-8 pr-3 py-1.5 rounded-lg border outline-none transition-all"
          style="border-color: #e2e8f0; background: #f8fafc; color: #0f172a; width: 200px"
        />
        <span class="absolute left-2.5 top-1/2 -translate-y-1/2 text-xs" style="color: #94a3b8">🔍</span>
      </div>

      <!-- Notification -->
      <button class="relative w-9 h-9 rounded-lg flex items-center justify-center transition-colors border" style="border-color: #e2e8f0; background: #f8fafc">
        <span class="text-base">🔔</span>
        <span class="absolute top-1.5 right-1.5 w-2 h-2 rounded-full" style="background: #ef4444" />
      </button>

      <!-- Help -->
      <button class="w-9 h-9 rounded-lg flex items-center justify-center transition-colors border text-sm" style="border-color: #e2e8f0; background: #f8fafc; color: #64748b">
        ?
      </button>

      <!-- Avatar -->
      <div class="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-semibold cursor-pointer" style="background: #2563eb">
        山
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { SamplePage } from '~/types/sample'

interface Props {
  currentPage: SamplePage
}

const props = defineProps<Props>()

const pageTitles: Record<SamplePage, string> = {
  dashboard: 'ダッシュボード',
  list: '受注一覧',
  detail: '受注詳細',
  form: '新規登録 / 編集',
}

const pageTitle = computed<string>(() => {
  const page: SamplePage = props.currentPage
  return pageTitles[page]
})
</script>