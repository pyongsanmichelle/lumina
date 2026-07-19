import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import AppButton from '~/components/AppButton.vue'

describe('AppButton', () => {
  it('renders slot content', () => {
    const wrapper = mount(AppButton, { slots: { default: 'クリック' } })
    expect(wrapper.text()).toBe('クリック')
  })

  it('emits click when the button is clicked', async () => {
    const wrapper = mount(AppButton)
    await wrapper.find('button').trigger('click')
    expect(wrapper.emitted('click')).toHaveLength(1)
  })
})
