import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Button from '~/components/ui/Button.vue'

describe('ui/Button', () => {
  it('renders slot content', () => {
    const wrapper = mount(Button, { slots: { default: '送信' } })
    expect(wrapper.text()).toBe('送信')
  })

  it('emits click when the button is clicked', async () => {
    const wrapper = mount(Button)
    await wrapper.find('button').trigger('click')
    expect(wrapper.emitted('click')).toHaveLength(1)
  })
})
