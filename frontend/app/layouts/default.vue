<template>
  <div class="flex min-h-screen" style="background: #f8fafc">
    <Sidebar :current-page="pageName" @navigate="onNavigate" />
    <SampleHeader :current-page="pageName" />
    <main class="flex-1 overflow-y-auto" style="margin-left: 240px; padding-top: 56px">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  pageName?: string;
}>();

const pageName = computed<string>(() => props.pageName || '');

function onNavigate(page: string) {
  // レイアウト自体ではページ遷移の状態管理を持たない
  // 各ページで直接ナビゲーションを実装するか、イベントをemitする
}
</script>
