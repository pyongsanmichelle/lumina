import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Checkbox from '~/components/ui/Checkbox.vue'

describe('ui/Checkbox', () => {
  it('renders the label and reflects the checked state', () => {
    const wrapper = mount(Checkbox, {
      props: { label: '同意する', modelValue: true },
    })
    expect(wrapper.text()).toContain('同意する')
    const input = wrapper.find('input[type="checkbox"]')
    expect((input.element as HTMLInputElement).checked).toBe(true)
  })

  it('emits update:modelValue with the new checked state on change', async () => {
    const wrapper = mount(Checkbox, {
      props: { label: '同意する', modelValue: false },
    })
    const input = wrapper.find('input[type="checkbox"]')
    await input.setValue(true)
    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual([true])
  })

  it('applies disabled styling and attribute', () => {
    const wrapper = mount(Checkbox, {
      props: { label: '同意する', modelValue: false, disabled: true },
    })
    const input = wrapper.find('input[type="checkbox"]')
    expect(input.attributes('disabled')).toBeDefined()
    expect(wrapper.find('label').classes()).toContain('text-gray-400')
  })

  it('applies error styling, accent color and aria-invalid when an error is present', () => {
    const wrapper = mount(Checkbox, {
      props: { label: '同意する', modelValue: false, error: '必須です' },
    })
    const input = wrapper.find('input[type="checkbox"]')
    expect(input.attributes('aria-invalid')).toBe('true')
    expect((input.element as HTMLInputElement).style.accentColor).toBe('#ef4444')
    expect(wrapper.find('label').classes()).toContain('text-red-600')
  })

  it('uses the default accent color and neutral label without error or disabled', () => {
    const wrapper = mount(Checkbox, {
      props: { label: '同意する', modelValue: false },
    })
    const input = wrapper.find('input[type="checkbox"]')
    expect((input.element as HTMLInputElement).style.accentColor).toBe('#2563eb')
    expect(input.attributes('aria-invalid')).toBe('false')
    expect(wrapper.find('label').classes()).toContain('text-gray-600')
  })
})
