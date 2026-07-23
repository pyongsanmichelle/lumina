<template>
  <aside
    class="fixed left-0 top-0 h-full w-60 flex flex-col"
    style="background: #0f172a; z-index: 50"
  >
    <!-- Logo -->
    <div class="flex items-center gap-3 px-5 py-5 border-b" style="border-color: #1e293b">
      <div
        class="w-8 h-8 rounded-lg flex items-center justify-center text-white text-sm font-bold"
        style="background: #2563eb"
      >
        B
      </div>
      <div>
        <div class="text-white text-sm font-semibold leading-tight">BizAdmin</div>
        <div class="text-xs" style="color: #64748b">管理システム</div>
      </div>
    </div>

    <!-- Nav -->
    <nav class="flex-1 px-3 py-4 overflow-y-auto">
      <div v-for="group in navGroups" :key="group.name" class="mb-5">
        <div
          class="px-3 mb-2 text-xs font-semibold uppercase tracking-widest"
          style="color: #475569"
        >
          {{ group.name }}
        </div>
        <button
          v-for="item in group.items"
          :key="item.id"
          class="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 mb-0.5 text-left"
          :style="navButtonStyle(item.id === currentPage)"
          @click="$emit('navigate', item.id)"
          @mouseenter="onNavHover($event, item.id)"
          @mouseleave="onNavLeave($event, item.id)"
        >
          <span
            class="w-5 h-5 flex items-center justify-center text-base leading-none flex-shrink-0"
          >
            {{ item.icon }}
          </span>
          {{ item.label }}
        </button>
      </div>
    </nav>

    <!-- Footer -->
    <div class="px-4 py-4 border-t relative" style="border-color: #1e293b">
      <div class="flex items-center gap-3">
        <div
          class="w-8 h-8 rounded-full flex items-center justify-center text-white text-xs font-bold flex-shrink-0"
          style="background: #334155"
        >
          山田
        </div>
        <div class="flex-1 min-w-0">
          <div class="text-xs font-medium text-white truncate">山田 太郎</div>
          <div class="text-xs truncate" style="color: #64748b">管理者</div>
        </div>
        <button
          data-testid="power-button"
          class="text-xs transition-colors"
          style="color: #64748b"
          @click="showLogoutMenu = !showLogoutMenu"
          @mouseenter="onPowerHoverEnter"
          @mouseleave="onPowerHoverLeave"
        >
          ⏻
        </button>
      </div>

      <!-- Logout Popover -->
      <div
        v-if="showLogoutMenu"
        data-testid="logout-popover"
        class="absolute bottom-14 right-4 w-40 rounded-md shadow-lg"
        style="background: #1e293b; z-index: 60"
        @mouseleave="showLogoutMenu = false"
      >
        <button
          data-testid="logout-button"
          class="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
          @click="handleLogout"
        >
          <span class="text-base leading-none">→</span>
          <span>ログアウト</span>
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useLogout } from '~/composables/useLogout';
import type { SamplePage } from '~/types/sample';

interface Props {
  currentPage: SamplePage;
}

defineProps<Props>();

defineEmits<{
  navigate: [page: SamplePage];
}>();

interface NavItem {
  id: SamplePage;
  label: string;
  icon: string;
}

interface NavGroup {
  name: string;
  items: NavItem[];
}

const showLogoutMenu = ref(false);

const navGroups: NavGroup[] = [
  {
    name: 'メイン',
    items: [{ id: 'dashboard', label: 'ダッシュボード', icon: '⊞' }],
  },
  {
    name: 'データ管理',
    items: [
      { id: 'list', label: '受注一覧', icon: '≡' },
      { id: 'detail', label: '詳細照会', icon: '◉' },
      { id: 'form', label: '新規登録', icon: '+' },
    ],
  },
];

function navButtonStyle(active: boolean) {
  return {
    background: active ? '#2563eb' : 'transparent',
    color: active ? '#fff' : '#94a3b8',
  };
}

function onNavHover(e: MouseEvent, id: SamplePage) {
  const el = e.currentTarget as HTMLElement;
  if (el.style.background !== 'rgb(37, 99, 235)') {
    el.style.background = '#1e293b';
    el.style.color = '#e2e8f0';
  }
}

function onNavLeave(e: MouseEvent, id: SamplePage) {
  const el = e.currentTarget as HTMLElement;
  if (el.style.background !== 'rgb(37, 99, 235)') {
    el.style.background = 'transparent';
    el.style.color = '#94a3b8';
  }
}

function onPowerHoverEnter(e: MouseEvent) {
  const el = e.currentTarget as HTMLElement;
  el.style.color = '#94a3b8';
}

function onPowerHoverLeave(e: MouseEvent) {
  const el = e.currentTarget as HTMLElement;
  el.style.color = '#64748b';
}

function handleLogout() {
  showLogoutMenu.value = false;
  const { logout } = useLogout();
  logout();
}
</script>
