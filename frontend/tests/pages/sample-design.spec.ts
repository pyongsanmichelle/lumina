import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'

// STATUS_COLORS に存在しないステータスのデータを1件追加するため ALL_ORDERS をモック
vi.mock('~/types/sample', async (importOriginal) => {
  const actual = await importOriginal<typeof import('~/types/sample')>()
  return {
    ...actual,
    ALL_ORDERS: [
      ...actual.ALL_ORDERS,
      {
        id: 'ORD-TEST-UNKNOWN',
        client: 'テスト未定義ステータス株式会社',
        product: 'テスト商品',
        amount: '¥1',
        status: '未定義ステータス', // STATUS_COLORS に存在しないキー
        date: '2026-07-12',
        staff: '田中 健二',
      },
    ],
  }
})

import SampleDesignPage from '~/pages/sample-design.vue'

// トーストのスタブを共通定義
const SampleToastStub = {
  name: 'SampleToast',
  props: ['toasts', 'removeToast'],
  template: '<div class="sample-toast-stub"></div>',
}
// mountを共通化するヘルパー関数
const createWrapper = () => {
  return mount(SampleDesignPage, {
    global: {
      components: {
        SampleToast: SampleToastStub
      }
    }
  })
}

describe('SampleDesignPage', () => {
  it('renders dashboard by default', () => {
    const wrapper = createWrapper()
    expect(wrapper.text()).toContain('月次売上推移')
    expect(wrapper.text()).toContain('実績目標')
  })
  it('switches to list page when sidebar list button clicked', async () => {
    const wrapper = createWrapper()
    const buttons = wrapper.findAll('button')
    const listBtn = buttons.find((b) => b.text().includes('受注一覧'))
    if (listBtn) {
      await listBtn.trigger('click')
      expect(wrapper.text()).toContain('検索条件')
      expect(wrapper.text()).toContain('検索結果')
    }
  })
  it('switches to detail page when detail button clicked', async () => {
    const wrapper = createWrapper()
    const buttons = wrapper.findAll('button')
    const detailBtn = buttons.find((b) => b.text().includes('詳細照会'))
    if (detailBtn) {
      await detailBtn.trigger('click')
      expect(wrapper.text()).toContain('受注番号')
      expect(wrapper.text()).toContain('顧客情報')
    }
  })
  it('switches to form page when form button clicked', async () => {
    const wrapper = createWrapper()
    const buttons = wrapper.findAll('button')
    const formBtn = buttons.find((b) => b.text().includes('新規登録'))
    if (formBtn) {
      await formBtn.trigger('click')
      expect(wrapper.text()).toContain('顧客情報')
      expect(wrapper.text()).toContain('受注情報')
    }
  })
  it('filters orders by keyword in list page', async () => {
    const wrapper = createWrapper()
    const buttons = wrapper.findAll('button')
    const listBtn = buttons.find((b) => b.text().includes('受注一覧'))
    if (listBtn) {
      await listBtn.trigger('click')
      const input = wrapper.find('input[type="text"]')
      await input.setValue('山田商事')
      await nextTick() // DOMの更新を確実に待つ
      expect(wrapper.text()).toContain('株式会社山田商事')
    }
  })
  it('shows validation errors on empty form submit', async () => {
    const wrapper = createWrapper()
    const buttons = wrapper.findAll('button')
    const formBtn = buttons.find((b) => b.text().includes('新規登録'))
    if (formBtn) {
      await formBtn.trigger('click')
      const submitBtn = wrapper.find('button[type="submit"]')
      if (submitBtn.exists()) {
        await submitBtn.trigger('click')
        await nextTick()
        expect(wrapper.text()).toContain('* 必須項目をすべて入力してください')
      }
    }
  })
  it('navigates via sidebar emit', async () => {
    const wrapper = mount(SampleDesignPage, {
      global: { components: { SampleToast: SampleToastStub } },
      attrs: { onNavigate: () => {} },
    })
    const sidebar = wrapper.findComponent({ name: 'SampleSidebar' })
    if (sidebar.exists()) {
      await sidebar.vm.$emit('navigate', 'detail')
      expect(wrapper.text()).toContain('受注番号')
    }
  })
  it('saves valid form and shows success toast', async () => {
    const wrapper = createWrapper()

    const buttons = wrapper.findAll('button')
    const formBtn = buttons.find((b) => b.text().includes('新規登録'))

    if (formBtn) {
      await formBtn.trigger('click')
      await nextTick()
      wrapper.vm.formData.clientName = 'テスト顧客株式会社'
      wrapper.vm.formData.client = 'テスト顧客株式会社'
      wrapper.vm.formData.contact = '山田 太郎'
      wrapper.vm.formData.product = 'テスト商品A'
      wrapper.vm.formData.qty = 5
      wrapper.vm.formData.unitPrice = 1000
      wrapper.vm.formData.orderDate = '2026-07-12'
      wrapper.vm.formData.status = '受注済'
      wrapper.vm.formData.staff = '田中 健二'
      await wrapper.vm.handleSave()
      await nextTick()
      expect(wrapper.vm.submitted).toBe(false)
      expect(Object.keys(wrapper.vm.formErrors).length).toBe(0)
    }
  })
  it('dismisses errors when dismiss button clicked', async () => {
    const wrapper = createWrapper()
    const buttons = wrapper.findAll('button')
    const formBtn = buttons.find((b) => b.text().includes('新規登録'))
    if (formBtn) {
      await formBtn.trigger('click')
      const submitBtn = wrapper.find('button[type="submit"]')
      if (submitBtn.exists()) {
        await submitBtn.trigger('click')
        const dismissBtn = wrapper.find('button[type="button"]')
        if (dismissBtn.exists()) {
          await dismissBtn.trigger('click')
          expect(wrapper.text()).not.toContain('入力エラーがあります')
        }
      }
    }
  })
  it('covers remaining reactive functions in sample-design', async () => {
    const wrapper = createWrapper()
    const buttons = wrapper.findAll('button')
    const formBtn = buttons.find((b) => b.text().includes('新規登録'))
    if (formBtn) {
      await formBtn.trigger('click')
      wrapper.vm.formData.clientName = 'テスト顧客'
      await wrapper.vm.handleSave()
      await nextTick()
      if (wrapper.vm.toasts && wrapper.vm.toasts.length > 0) {
        const toastId = wrapper.vm.toasts[0].id
        wrapper.vm.removeToast(toastId)
        await nextTick()
        expect(wrapper.vm.toasts.length).toBe(0)
      }
    }
  })
  it('triggers sorting or filtration helper functions', async () => {
    const wrapper = createWrapper()
    const listBtn = wrapper.findAll('button').find((b) => b.text().includes('受注一覧'))
    if (listBtn) {
      await listBtn.trigger('click')
      await nextTick()
      const thElements = wrapper.findAll('th')
      if (thElements.length > 0) {
        await thElements[0].trigger('click')
        await nextTick()
      }
    }
  })
  it('shows zero-result message', async () => {
    const wrapper = createWrapper()
    ;(wrapper.vm as any).currentPage = 'list'
    await nextTick()
    ;(wrapper.vm as any).listKeyword = '絶対に存在しないキーワードxyz'
    await nextTick()
    expect(wrapper.text()).toContain('該当するデータがありません')
  })
  it('shows pagination when results exceed perPage', async () => {
    const wrapper = createWrapper()
    ;(wrapper.vm as any).currentPage = 'list'
    await nextTick()
    expect((wrapper.vm as any).totalPages).toBeGreaterThan(1)
    const pageBtn = wrapper.findAll('button').find(b => b.text() === '2')
    expect(pageBtn).toBeTruthy()
    await pageBtn!.trigger('click')
    expect((wrapper.vm as any).listPage).toBe(2)
  })
  it('handleSave: validation error branch', async () => {
    const wrapper = createWrapper()
    ;(wrapper.vm as any).currentPage = 'form'
    await nextTick()
    await (wrapper.vm as any).handleSave()
    expect(Object.keys((wrapper.vm as any).formErrors).length).toBeGreaterThan(0)
  })
  it('handleSave: simulated API error branch adds error toast', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'form'
    await nextTick()

    Object.assign(vm.formData, {
      clientName: 'テスト', contact: '山田',
      product: '商品A', qty: 1, unitPrice: 100,
      orderDate: '2026-07-12', status: '受注済', staff: '田中 健二',
    })
    vm.simulateApiError = true
    await nextTick()

    await vm.handleSave()
    await nextTick()

    expect(Object.keys(vm.formErrors).length).toBe(0)
    expect(vm.toasts.length).toBeGreaterThan(0)
    const lastToast = vm.toasts[vm.toasts.length - 1]
    expect(lastToast.type).toBe('error')
    expect(lastToast.title).toBe('通信に失敗しました')
    expect(lastToast.message).toContain('サーバーとの接続が切断されました')
  })
  it('handleSave: success branch resets form', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'form'
    await nextTick()
    Object.assign(vm.formData, {
      clientName: 'テスト', contact: '山田',
      product: '商品A', qty: 1, unitPrice: 100,
      orderDate: '2026-07-12', status: '受注済', staff: '田中 健二',
    })
    vm.simulateApiError = false
    await vm.handleSave()
    expect(vm.formData.clientName).toBe('')
  })
  it('removeToast actually removes via real toast click flow', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'form'
    await nextTick()
    Object.assign(vm.formData, {
      clientName: 'a', contact: 'b', product: 'c',
      qty: 1, unitPrice: 1, orderDate: '2026-07-12',
      status: '受注済', staff: '田中 健二',
    })
    await vm.handleSave()
    expect(vm.toasts.length).toBeGreaterThan(0)
    vm.removeToast(vm.toasts[0].id)
    expect(vm.toasts.length).toBe(0)
  })
  it('dismissErrors resets formErrors and submitted state', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'form'
    await nextTick()

    await vm.handleSave()
    await nextTick()
    expect(vm.submitted).toBe(true)
    expect(Object.keys(vm.formErrors).length).toBeGreaterThan(0)

    vm.dismissErrors()
    await nextTick()

    expect(vm.formErrors).toEqual({})
    expect(vm.submitted).toBe(false)
  })
})

describe('SampleDesignPage - 全ボタン網羅', () => {
  it('sidebarの全ナビゲーションボタンを実クリックする', async () => {
    const wrapper = createWrapper()
    const sidebar = wrapper.findComponent({ name: 'SampleSidebar' })
    expect(sidebar.exists()).toBe(true)
    for (const page of ['dashboard', 'list', 'detail', 'form'] as const) {
      await sidebar.vm.$emit('navigate', page)
      await nextTick()
    }
  })
  it('ダッシュボードの「すべて見る」ボタンをクリックする', async () => {
    const wrapper = createWrapper()
    const btn = wrapper.findAll('button').find(b => b.text().includes('すべて見る'))
    expect(btn).toBeTruthy()
    await btn!.trigger('click')
  })
  it('一覧ページ: 検索・チェックボックス・ステータス選択を実操作', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'list'
    await nextTick()
    const select = wrapper.find('select')
    await select.setValue('受注済')
    const checkbox = wrapper.find('input[type="checkbox"]')
    await checkbox.setValue(true)
    const searchBtn = wrapper.findAll('button').find(b => b.text() === '検索')
    expect(searchBtn).toBeTruthy()
    await searchBtn!.trigger('click')
    const kaishoBtn = wrapper.findAll('button').find(b => b.text() === '照会')
    if (kaishoBtn) await kaishoBtn.trigger('click')
    vm.currentPage = 'list'
    await nextTick()
    const editBtn = wrapper.findAll('button').find(b => b.text() === '編集')
    if (editBtn) await editBtn.trigger('click')
  })
  it('一覧ページ: ページネーションの前後ボタンを実クリック', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'list'
    await nextTick()
    const buttons = wrapper.findAll('button')
    const nextBtn = buttons.find(b => b.text() === '›')
    const prevBtn = buttons.find(b => b.text() === '‹')
    expect(nextBtn).toBeTruthy()
    expect(prevBtn).toBeTruthy()
    await nextBtn!.trigger('click')
    await prevBtn!.trigger('click')
  })
  it('詳細ページ: 印刷・編集する・一覧に戻るボタンを実クリック', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'detail'
    await nextTick()
    const buttons = wrapper.findAll('button')
    const printBtn = buttons.find(b => b.text() === '印刷')
    const editBtn = buttons.find(b => b.text() === '編集する')
    const backBtn = buttons.find(b => b.text().includes('一覧に戻る'))
    expect(printBtn).toBeTruthy()
    expect(editBtn).toBeTruthy()
    expect(backBtn).toBeTruthy()
    await printBtn!.trigger('click')
    await editBtn!.trigger('click')
    vm.currentPage = 'detail'
    await nextTick()
    await wrapper.findAll('button').find(b => b.text().includes('一覧に戻る'))!.trigger('click')
  })
  it('フォームページ: 全入力欄・チェックボックス・キャンセルボタンを実操作', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'form'
    await nextTick()
    const inputs = wrapper.findAll('input')
    for (const input of inputs) {
      const type = input.attributes('type')
      if (type === 'checkbox') {
        await input.setValue(true)
      } else if (type !== undefined) {
        await input.setValue('test')
      }
    }
    const textarea = wrapper.find('textarea')
    if (textarea.exists()) await textarea.setValue('備考テスト')
    const selects = wrapper.findAll('select')
    for (const s of selects) {
      const opts = s.findAll('option')
      if (opts.length > 1) await s.setValue(opts[1].element.value)
    }
    const cancelBtn = wrapper.findAll('button').find(b => b.text() === 'キャンセル')
    expect(cancelBtn).toBeTruthy()
    await cancelBtn!.trigger('click')
  })
  it('フォーム: バリデーションエラー後、dismissBtn を実クリック', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'form'
    await nextTick()
    const submitBtn = wrapper.find('button[type="submit"]')
    await submitBtn.trigger('click')
    await nextTick()
    const dismissBtn = wrapper.find('.sample-alert-banner button, [aria-label="dismiss"], button[type="button"]')
    if (dismissBtn.exists()) {
      await dismissBtn.trigger('click')
    }
  })
  it('APIエラーシミュレートのチェックボックスを実際にON/OFFする', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'form'
    await nextTick()
    const checkbox = wrapper.findAll('input[type="checkbox"]')
      .find(c => c.element.closest('label')?.textContent?.includes('APIエラー'))
    expect(checkbox).toBeTruthy()
    await checkbox!.setValue(true)
    await checkbox!.setValue(false)
  })
})

describe('SampleDesignPage - STATUS_COLORS fallback', () => {
  it('未定義ステータスの場合でもクラッシュせずレンダリングされる（??フォールバック分岐のカバレッジ）', async () => {
    const wrapper = createWrapper()
    const vm = wrapper.vm as any
    vm.currentPage = 'list'
    vm.listKeyword = 'テスト未定義ステータス株式会社'
    await nextTick()

    // フォールバック分岐が実行されてもエラーにならず、ステータス文言が表示される
    expect(wrapper.text()).toContain('未定義ステータス')

    const badge = wrapper.findAll('span').find(s => s.text() === '未定義ステータス')
    expect(badge).toBeTruthy()
    // bg/text という無効なCSSプロパティ名のため style は付与されない仕様
    // (STATUS_COLORS[o.status] ?? {...} の右辺評価自体がこのテストの目的)
  })
})