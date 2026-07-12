<template>
  <div class="flex min-h-screen" style="background: #f8fafc">
    <SampleSidebar :current-page="currentPage" @navigate="onNavigate" />
    <SampleHeader :current-page="currentPage" />
    <main class="flex-1 overflow-y-auto" style="margin-left: 240px; padding-top: 56px">
      <div class="p-6 max-w-screen-xl">
        <div v-if="showDashboard">
          <!-- KPI Cards -->
          <div class="grid gap-4 mb-6" style="grid-template-columns: repeat(4, 1fr)">
            <SampleKpiCard
              v-for="kpi in KPI_DATA"
              :key="kpi.label"
              :label="kpi.label"
              :value="kpi.value"
              :delta="kpi.delta"
              :delta-up="kpi.deltaUp"
              :sub="kpi.sub"
            />
          </div>

          <!-- Charts Row -->
          <div class="grid gap-4 mb-6" style="grid-template-columns: 2fr 1fr">
            <div class="bg-white rounded-xl p-5 border" style="border-color: #e2e8f0">
              <div class="flex items-center justify-between mb-5">
                <div>
                  <div class="text-sm font-semibold" style="color: #0f172a">月次売上推移</div>
                  <div class="text-xs mt-0.5" style="color: #94a3b8">2024年 1月 〜 7月</div>
                </div>
                <div class="flex items-center gap-4 text-xs" style="color: #64748b">
                  <span class="flex items-center gap-1.5"><span class="w-2.5 h-2.5 rounded-full inline-block" style="background: #2563eb" />実績</span>
                  <span class="flex items-center gap-1.5"><span class="w-2.5 h-2.5 rounded-full inline-block" style="background: #e2e8f0" />目標</span>
                </div>
              </div>
              <div class="overflow-x-auto">
                <SampleAreaChart :data="SALES_DATA" width="550" height="200" />
              </div>
            </div>
            <div class="bg-white rounded-xl p-5 border" style="border-color: #e2e8f0">
              <div class="text-sm font-semibold mb-1" style="color: #0f172a">カテゴリ別構成比</div>
              <div class="text-xs mb-5" style="color: #94a3b8">売上シェア（%）</div>
              <SampleBarChart :data="CATEGORY_DATA" />
            </div>
          </div>

          <!-- Notifications -->
          <div class="bg-white rounded-xl border" style="border-color: #e2e8f0">
            <div class="flex items-center justify-between px-5 py-4 border-b" style="border-color: #f1f5f9">
              <div class="text-sm font-semibold" style="color: #0f172a">最近のお知らせ</div>
              <button class="text-xs font-medium transition-colors" style="color: #2563eb">すべて見る →</button>
            </div>
            <div>
              <SampleNotificationItem
                v-for="(n, i) in NOTIFICATIONS"
                :key="n.id"
                :type="n.type"
                :title="n.title"
                :body="n.body"
                :time="n.time"
                :has-border="i < NOTIFICATIONS.length - 1"
              />
            </div>
          </div>
        </div>

        <div v-if="showList">
          <!-- Search Form -->
          <div class="bg-white rounded-xl border p-5 mb-5" style="border-color: #e2e8f0">
            <div class="text-sm font-semibold mb-4" style="color: #0f172a">検索条件</div>
            <div class="grid gap-4" style="grid-template-columns: 1fr 1fr 1fr auto">
              <div>
                <label class="block text-xs font-medium mb-1.5" style="color: #475569">キーワード</label>
                <input
                  v-model="listKeyword"
                  type="text"
                  placeholder="受注番号・顧客名・商品名"
                  class="w-full text-sm px-3 py-2 rounded-lg border outline-none transition-all"
                  style="border-color: #e2e8f0; color: #0f172a"
                />
              </div>
              <div>
                <label class="block text-xs font-medium mb-1.5" style="color: #475569">ステータス</label>
                <select
                  v-model="listStatus"
                  class="w-full text-sm px-3 py-2 rounded-lg border outline-none appearance-none"
                  style="border-color: #e2e8f0; color: #0f172a; background: white"
                >
                  <option value="">すべて</option>
                  <option value="受注済">受注済</option>
                  <option value="出荷済">出荷済</option>
                  <option value="完了">完了</option>
                  <option value="キャンセル">キャンセル</option>
                </select>
              </div>
              <div class="flex flex-col justify-end pb-0.5">
                <label class="flex items-center gap-2 text-sm cursor-pointer" style="color: #475569">
                  <input v-model="listIncludeCancel" type="checkbox" class="w-4 h-4 rounded" style="accent-color: #2563eb" />
                  キャンセル含む
                </label>
              </div>
              <div class="flex flex-col justify-end">
                <button
                  class="px-6 py-2 rounded-lg text-sm font-semibold text-white"
                  style="background: #2563eb"
                  @click="listPage = 1"
                >
                  検索
                </button>
              </div>
            </div>
          </div>

          <!-- Table -->
          <div class="bg-white rounded-xl border overflow-hidden" style="border-color: #e2e8f0">
            <div class="flex items-center justify-between px-5 py-3.5 border-b" style="border-color: #f1f5f9">
              <div class="text-sm font-semibold" style="color: #0f172a">
                検索結果
                <span class="ml-2 text-xs font-normal" style="color: #94a3b8">{{ filteredOrders.length }}件</span>
              </div>
              <button class="flex items-center gap-1.5 px-4 py-1.5 rounded-lg text-xs font-semibold text-white" style="background: #2563eb" @click="currentPage = 'form'">
                + 新規登録
              </button>
            </div>
            <div class="overflow-x-auto">
              <table class="w-full text-sm">
                <thead>
                  <tr style="background: #f8fafc">
                    <th v-for="h in tableHeaders" :key="h" class="text-left px-4 py-3 text-xs font-semibold" style="color: #64748b; border-bottom: 1px solid #e2e8f0">{{ h }}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(o, i) in pagedOrders"
                    :key="o.id"
                    class="transition-colors cursor-pointer"
                    :style="{ borderBottom: i < pagedOrders.length - 1 ? '1px solid #f8fafc' : 'none' }"
                  >
                    <td class="px-4 py-3.5"><span class="font-mono text-xs font-medium" style="color: #2563eb">{{ o.id }}</span></td>
                    <td class="px-4 py-3.5 font-medium" style="color: #1e293b">{{ o.client }}</td>
                    <td class="px-4 py-3.5" style="color: #475569; max-width: 200px"><span class="truncate block">{{ o.product }}</span></td>
                    <td class="px-4 py-3.5 font-semibold tabular-nums" style="color: #0f172a">{{ o.amount }}</td>
                    <td class="px-4 py-3.5 text-xs" style="color: #64748b">{{ o.staff }}</td>
                    <td class="px-4 py-3.5">
                      <span class="inline-block text-xs font-semibold px-2.5 py-1 rounded-full" :style="statusBadgeStyle(o.status)">
                        {{ o.status }}
                      </span>
                    </td>
                    <td class="px-4 py-3.5 text-xs tabular-nums" style="color: #64748b">{{ o.date }}</td>
                    <td class="px-4 py-3.5">
                      <div class="flex gap-2">
                        <button class="text-xs px-2.5 py-1 rounded border" style="border-color: #e2e8f0; color: #475569" @click="currentPage = 'detail'">照会</button>
                        <button class="text-xs px-2.5 py-1 rounded border" style="border-color: #e2e8f0; color: #475569" @click="currentPage = 'form'">編集</button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="pagedOrders.length === 0">
                    <td :colspan="tableHeaders.length" class="px-4 py-12 text-center text-sm" style="color: #94a3b8">該当するデータがありません</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <!-- Pagination -->
            <div v-if="totalPages > 1" class="flex items-center justify-between px-5 py-3.5 border-t" style="border-color: #f1f5f9">
              <div class="text-xs" style="color: #94a3b8">
                {{ (listPage - 1) * perPage + 1 }} 〜 {{ Math.min(listPage * perPage, filteredOrders.length) }} 件 / 全 {{ filteredOrders.length }} 件
              </div>
              <div class="flex items-center gap-1">
                <button :disabled="listPage === 1" class="w-8 h-8 flex items-center justify-center rounded text-xs" :style="{ color: listPage === 1 ? '#cbd5e1' : '#475569', border: '1px solid #e2e8f0' }" @click="listPage = Math.max(1, listPage - 1)">‹</button>
                <button
                  v-for="p in totalPagesArray"
                  :key="p"
                  class="w-8 h-8 flex items-center justify-center rounded text-xs font-medium"
                  :style="{ background: p === listPage ? '#2563eb' : 'transparent', color: p === listPage ? '#fff' : '#475569', border: `1px solid ${p === listPage ? '#2563eb' : '#e2e8f0'}` }"
                  @click="listPage = p"
                >{{ p }}</button>
                <button :disabled="listPage === totalPages" class="w-8 h-8 flex items-center justify-center rounded text-xs" :style="{ color: listPage === totalPages ? '#cbd5e1' : '#475569', border: '1px solid #e2e8f0' }" @click="listPage = Math.min(totalPages, listPage + 1)">›</button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="showDetail">
          <!-- Detail page -->
          <div class="space-y-5">
            <div class="flex items-center justify-between">
              <button class="flex items-center gap-1.5 text-sm" style="color: #64748b" @click="currentPage = 'list'">← 一覧に戻る</button>
              <div class="flex items-center gap-2">
                <button class="px-4 py-2 rounded-lg text-sm border" style="border-color: #e2e8f0; color: #475569">印刷</button>
                <button class="px-5 py-2 rounded-lg text-sm font-semibold text-white" style="background: #2563eb" @click="currentPage = 'form'">編集する</button>
              </div>
            </div>
            <div class="bg-white rounded-xl border px-6 py-5" style="border-color: #e2e8f0">
              <div class="flex items-start justify-between">
                <div>
                  <div class="text-xs font-medium mb-1" style="color: #94a3b8">受注番号</div>
                  <div class="text-2xl font-bold" style="color: #0f172a; font-family: var(--font-mono)">{{ ORDER_DETAIL.id }}</div>
                </div>
                <div class="flex items-center gap-3">
                  <div class="text-right text-xs" style="color: #94a3b8">
                    <div>登録：{{ ORDER_DETAIL.created }}</div>
                    <div>更新：{{ ORDER_DETAIL.updated }}</div>
                  </div>
                  <span class="inline-block text-sm font-semibold px-4 py-1.5 rounded-full" style="background: #eff6ff; color: #2563eb">{{ ORDER_DETAIL.status }}</span>
                </div>
              </div>
            </div>
            <div class="bg-white rounded-xl border px-6 py-5" style="border-color: #e2e8f0">
              <SampleSectionTitle>顧客情報</SampleSectionTitle>
              <div class="grid gap-4" style="grid-template-columns: 1fr 1fr 1fr">
                <SampleReadField label="顧客コード" :value="ORDER_DETAIL.clientCode" mono />
                <SampleReadField label="顧客名" :value="ORDER_DETAIL.client" />
                <SampleReadField label="担当者名" :value="ORDER_DETAIL.contact" />
                <SampleReadField label="電話番号" :value="ORDER_DETAIL.phone" />
                <SampleReadField label="メールアドレス" :value="ORDER_DETAIL.email" />
              </div>
            </div>
            <div class="bg-white rounded-xl border px-6 py-5" style="border-color: #e2e8f0">
              <SampleSectionTitle>受注情報</SampleSectionTitle>
              <div class="grid gap-4 mb-4" style="grid-template-columns: 2fr 1fr 1fr 1fr">
                <SampleReadField label="商品名" :value="ORDER_DETAIL.product" />
                <SampleReadField label="数量" :value="ORDER_DETAIL.qty" />
                <SampleReadField label="単価" :value="ORDER_DETAIL.unitPrice" />
                <SampleReadField label="小計" :value="ORDER_DETAIL.amount" />
              </div>
              <div class="grid gap-4" style="grid-template-columns: 1fr 1fr 1fr 1fr">
                <SampleReadField label="消費税（10%）" :value="ORDER_DETAIL.tax" />
                <SampleReadField label="合計金額" :value="ORDER_DETAIL.total" />
                <SampleReadField label="受注日" :value="ORDER_DETAIL.orderDate" />
                <SampleReadField label="納品予定日" :value="ORDER_DETAIL.deliveryDate" />
              </div>
            </div>
            <div class="bg-white rounded-xl border px-6 py-5" style="border-color: #e2e8f0">
              <SampleSectionTitle>その他</SampleSectionTitle>
              <div class="grid gap-4 mb-4" style="grid-template-columns: 1fr 2fr">
                <SampleReadField label="担当者" :value="ORDER_DETAIL.staff" />
              </div>
              <div>
                <div class="text-xs font-medium mb-1" style="color: #64748b">備考</div>
                <div class="text-sm py-3 px-3 rounded-lg leading-relaxed" style="background: #f8fafc; color: #1e293b; border: 1px solid #e2e8f0; min-height: 80px">{{ ORDER_DETAIL.note }}</div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="showForm">
          <form @submit.prevent="handleSave" noValidate class="space-y-5">
            <div class="flex items-center justify-between">
              <button type="button" class="flex items-center gap-1.5 text-sm" style="color: #64748b" @click="currentPage = 'list'">← 一覧に戻る</button>
              <label class="flex items-center gap-2 text-xs cursor-pointer select-none" style="color: #94a3b8">
                <input v-model="simulateApiError" type="checkbox" style="accent-color: #ef4444" />
                APIエラーをシミュレート
              </label>
            </div>

            <SampleAlertBanner
              v-if="submitted && Object.keys(formErrors).length > 0"
              type="error"
              :title="`${Object.keys(formErrors).length}件の入力エラーがあります`"
              message="すべての必須項目を正しく入力してから保存してください。"
              :on-dismiss="dismissErrors"
            />
            <SampleAlertBanner
              type="warning"
              title="セッションの期限が近づいています"
              message="あと10分でセッションが切れます。作業中のデータを保存してください。"
            />

            <div class="bg-white rounded-xl border px-6 py-5" style="border-color: #e2e8f0">
              <SampleSectionTitle>顧客情報</SampleSectionTitle>
              <div class="grid gap-4" style="grid-template-columns: 1fr 2fr 1fr">
                <SampleTextField label="顧客コード" v-model="formData.clientCode" hint="既存の顧客は自動補完されます" />
                <SampleTextField label="顧客名" required v-model="formData.clientName" :error="formErrors.clientName" />
                <SampleTextField label="担当者名" required v-model="formData.contact" :error="formErrors.contact" />
                <SampleTextField label="電話番号" type="tel" v-model="formData.phone" />
                <div style="grid-column: span 2">
                  <SampleTextField label="メールアドレス" type="email" v-model="formData.email" :error="formErrors.email" />
                </div>
              </div>
            </div>
            <div class="bg-white rounded-xl border px-6 py-5" style="border-color: #e2e8f0">
              <SampleSectionTitle>受注情報</SampleSectionTitle>
              <div class="grid gap-4 mb-4" style="grid-template-columns: 3fr 1fr 1fr">
                <SampleTextField label="商品名" required v-model="formData.product" :error="formErrors.product" />
                <SampleTextField label="数量" required type="number" v-model="formData.qty" :error="formErrors.qty" :min="1" />
                <SampleTextField label="単価（税抜）" required type="number" v-model="formData.unitPrice" :error="formErrors.unitPrice" hint="円単位で入力" :min="0" />
              </div>
              <div class="grid gap-4" style="grid-template-columns: 1fr 1fr 1fr 1fr">
                <SampleTextField label="受注日" required type="date" v-model="formData.orderDate" :error="formErrors.orderDate" />
                <SampleTextField label="納品予定日" type="date" v-model="formData.deliveryDate" :error="formErrors.deliveryDate" />
                <SampleSelectField label="ステータス" required v-model="formData.status" :error="formErrors.status">
                  <option value="">選択してください</option>
                  <option value="受注済">受注済</option>
                  <option value="出荷済">出荷済</option>
                  <option value="完了">完了</option>
                  <option value="キャンセル">キャンセル</option>
                </SampleSelectField>
                <SampleSelectField label="担当者" required v-model="formData.staff" :error="formErrors.staff">
                  <option value="">選択してください</option>
                  <option value="田中 健二">田中 健二</option>
                  <option value="鈴木 明子">鈴木 明子</option>
                  <option value="高橋 裕子">高橋 裕子</option>
                </SampleSelectField>
              </div>
            </div>
            <div class="bg-white rounded-xl border px-6 py-5" style="border-color: #e2e8f0">
              <SampleSectionTitle>備考・メモ</SampleSectionTitle>
              <SampleTextareaField label="備考" v-model="formData.note" placeholder="特記事項・納品時の注意点などを入力してください" rows="5" />
            </div>
            <div class="bg-white rounded-xl border px-6 py-4 flex items-center justify-between" style="border-color: #e2e8f0">
              <div class="text-xs" style="color: #94a3b8"><span class="font-semibold" style="color: #ef4444">*</span> 必須項目をすべて入力してください</div>
              <div class="flex items-center gap-3">
                <button type="button" class="px-6 py-2.5 rounded-lg text-sm border" style="border-color: #e2e8f0; color: #475569" @click="currentPage = 'list'">キャンセル</button>
                <button type="submit" class="px-8 py-2.5 rounded-lg text-sm font-semibold text-white" style="background: #2563eb">保存する</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    </main>
    <SampleToast :toasts="toasts" :remove-toast="removeToast" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  type SamplePage,
  type FormValues,
  type FormErrors,
  KPI_DATA,
  SALES_DATA,
  CATEGORY_DATA,
  NOTIFICATIONS,
  ALL_ORDERS,
  ORDER_DETAIL,
  STATUS_COLORS,
  EMPTY_FORM_VALUES,
  validateForm,
} from '~/types/sample'
import { useSampleToast } from '~/composables/useSampleToast'

const currentPage = ref<SamplePage>('dashboard')

const showDashboard = computed(() => currentPage.value === 'dashboard')
const showList = computed(() => currentPage.value === 'list')
const showDetail = computed(() => currentPage.value === 'detail')
const showForm = computed(() => currentPage.value === 'form')

function onNavigate(page: SamplePage) {
  currentPage.value = page
}

// List page state
const listKeyword = ref('')
const listStatus = ref('')
const listIncludeCancel = ref(false)
const listPage = ref(1)
const perPage = 5
const tableHeaders = ['受注番号', '顧客名', '商品', '金額', '担当者', 'ステータス', '受注日', '操作']

const filteredOrders = computed(() => {
  return ALL_ORDERS.filter((o) => {
    if (listStatus.value && o.status !== listStatus.value) return false
    if (!listIncludeCancel.value && o.status === 'キャンセル') return false
    if (
      listKeyword.value &&
      !o.client.includes(listKeyword.value) &&
      !o.id.includes(listKeyword.value) &&
      !o.product.includes(listKeyword.value)
    )
      return false
    return true
  })
})

const totalPages = computed(() => Math.ceil(filteredOrders.value.length / perPage))
const totalPagesArray = computed(() => {
  const arr: number[] = []
  for (let i = 1; i <= totalPages.value; i++) {
    arr.push(i)
  }
  return arr
})

const pagedOrders = computed(() => {
  const start = (listPage.value - 1) * perPage
  return filteredOrders.value.slice(start, start + perPage)
})

// Form page state
const formData = ref<FormValues>({ ...EMPTY_FORM_VALUES })
const formErrors = ref<FormErrors>({})
const submitted = ref(false)
const simulateApiError = ref(false)

function dismissErrors() {
  formErrors.value = {}
  submitted.value = false
}

function handleSave() {
  submitted.value = true
  const errs = validateForm(formData.value)
  formErrors.value = errs

  if (Object.keys(errs).length > 0) {
    addToast({
      type: 'error',
      title: '入力エラーがあります',
      message: '赤くハイライトされた項目を確認してください。',
    })
    return
  }

  if (simulateApiError.value) {
    addToast({
      type: 'error',
      title: '通信に失敗しました',
      message: 'サーバーとの接続が切断されました。再度お試しください。',
    })
    return
  }

  addToast({
    type: 'success',
    title: 'データを保存しました',
    message: '受注情報が正常に登録されました。',
  })
  formData.value = { ...EMPTY_FORM_VALUES }
  formErrors.value = {}
  submitted.value = false
}

// Toast
const { toasts, addToast, removeToast } = useSampleToast()

// STATUS_COLORS の {bg, text} を実際のCSSプロパティ名にマッピングする
function statusBadgeStyle(status: string): { background: string; color: string } {
  const c = STATUS_COLORS[status] ?? { bg: '#f1f5f9', text: '#64748b' }
  return {
    background: c.bg,
    color: c.text,
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}
</style>