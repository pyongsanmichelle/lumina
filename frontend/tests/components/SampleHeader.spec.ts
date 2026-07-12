import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleHeader from '~/components/SampleHeader.vue'

describe('SampleHeader', () => {
  it('renders page title for dashboard', () => {
    const wrapper = mount(SampleHeader, {
      props: { currentPage: 'dashboard' },
    })
    expect(wrapper.text()).toContain('ダッシュボード')
  })

  it('renders page title for list', () => {
    const wrapper = mount(SampleHeader, {
      props: { currentPage: 'list' },
    })
    expect(wrapper.text()).toContain('受注一覧')
  })

  it('renders search input', () => {
    const wrapper = mount(SampleHeader, {
      props: { currentPage: 'dashboard' },
    })
    const input = wrapper.find('input[type="text"]')
    expect(input.exists()).toBe(true)
    expect(input.attributes('placeholder')).toBe('クイック検索...')
  })

  it('renders notification and help buttons', () => {
    const wrapper = mount(SampleHeader, {
      props: { currentPage: 'dashboard' },
    })
    expect(wrapper.text()).toContain('🔔')
    expect(wrapper.text()).toContain('?')
  })

  it('renders avatar', () => {
    const wrapper = mount(SampleHeader, {
      props: { currentPage: 'dashboard' },
    })
    expect(wrapper.text()).toContain('山')
  })
})
