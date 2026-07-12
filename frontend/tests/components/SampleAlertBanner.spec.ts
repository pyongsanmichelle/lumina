import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleAlertBanner from '~/components/SampleAlertBanner.vue'

describe('SampleAlertBanner', () => {
  it('renders title and message for warning type', () => {
    const wrapper = mount(SampleAlertBanner, {
      props: {
        type: 'warning',
        title: '警告メッセージ',
        message: '内容を確認してください',
      },
    })
    expect(wrapper.text()).toContain('警告メッセージ')
    expect(wrapper.text()).toContain('内容を確認してください')
  })

  it('renders title for error type', () => {
    const wrapper = mount(SampleAlertBanner, {
      props: {
        type: 'error',
        title: 'エラーが発生しました',
      },
    })
    expect(wrapper.text()).toContain('エラーが発生しました')
  })

  it('dismisses banner when close button is clicked', async () => {
    const wrapper = mount(SampleAlertBanner, {
      props: {
        type: 'warning',
        title: 'テスト',
        onDismiss: () => {},
      },
    })
    expect(wrapper.isVisible()).toBe(true)
    const closeBtn = wrapper.find('button')
    await closeBtn.trigger('click')
    expect(wrapper.isVisible()).toBe(false)
  })
})