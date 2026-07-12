import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleSectionTitle from '~/components/SampleSectionTitle.vue'

describe('SampleSectionTitle', () => {
  it('renders slot content', () => {
    const wrapper = mount(SampleSectionTitle, {
      slots: { default: 'セクションタイトル' },
    })
    expect(wrapper.text()).toContain('セクションタイトル')
  })
})