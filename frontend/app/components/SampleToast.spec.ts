import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleToast from './SampleToast.vue'

describe('SampleToast', () => {
  const mockRemoveToast = vi.fn()

  it('renders success toast with title and message', () => {
    const wrapper = mount(SampleToast, {
      props: {
        toasts: [
          { id: '1', type: 'success', title: '成功しました', message: 'データを保存しました' },
        ],
        removeToast: mockRemoveToast,
      },
    })
    expect(wrapper.text()).toContain('成功しました')
    expect(wrapper.text()).toContain('データを保存しました')
  })

  it('renders warning toast', () => {
    const wrapper = mount(SampleToast, {
      props: {
        toasts: [
          { id: '2', type: 'warning', title: '警告', message: 'セッションが切れそうです' },
        ],
        removeToast: mockRemoveToast,
      },
    })
    expect(wrapper.text()).toContain('警告')
  })

  it('renders error toast', () => {
    const wrapper = mount(SampleToast, {
      props: {
        toasts: [
          { id: '3', type: 'error', title: 'エラー', message: '通信に失敗しました' },
        ],
        removeToast: mockRemoveToast,
      },
    })
    expect(wrapper.text()).toContain('エラー')
  })

  it('renders multiple toasts', () => {
    const wrapper = mount(SampleToast, {
      props: {
        toasts: [
          { id: '1', type: 'success', title: '成功' },
          { id: '2', type: 'warning', title: '警告' },
        ],
        removeToast: mockRemoveToast,
      },
    })
    const alerts = wrapper.findAll('[role="alert"]')
    expect(alerts.length).toBe(2)
  })

  it('calls removeToast when close button clicked', async () => {
    const wrapper = mount(SampleToast, {
      props: {
        toasts: [
          { id: '1', type: 'success', title: '成功しました' },
        ],
        removeToast: mockRemoveToast,
      },
    })
    const closeBtn = wrapper.find('button[aria-label="閉じる"]')
    await closeBtn.trigger('click')
    expect(mockRemoveToast).toHaveBeenCalledWith('1')
  })

  it('renders toast without message', () => {
    const wrapper = mount(SampleToast, {
      props: {
        toasts: [
          { id: '1', type: 'success', title: '成功しました' },
        ],
        removeToast: mockRemoveToast,
      },
    })
    expect(wrapper.text()).toContain('成功しました')
    // messageがない場合、v-if="toast.message" でメッセージ行は非表示
    // メッセージ行は .text-xs.mt-0.5.leading-relaxed の組み合わせ
    const messageEl = wrapper.find('.leading-relaxed')
    expect(messageEl.exists()).toBe(false)
  })
})