import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleTextField from './SampleTextField.vue'

describe('SampleTextField', () => {
  // textarea と input のどちらでも見つけられるように汎用的なセレクタを定義
  const inputSelector = 'textarea, input'

  it('renders label and input element with correct attributes', () => {
    const wrapper = mount(SampleTextField, {
      props: { label: '商品名', modelValue: '', placeholder: '入力してください' },
    })
    expect(wrapper.text()).toContain('商品名')
    
    const inputEl = wrapper.find(inputSelector)
    expect(inputEl.exists()).toBe(true)
    expect(inputEl.attributes('placeholder')).toBe('入力してください')
  })

  it('emits update:modelValue on input', async () => {
    const wrapper = mount(SampleTextField, {
      props: { label: '商品名', modelValue: '' },
    })
    const inputEl = wrapper.find(inputSelector)
    await inputEl.setValue('テスト')
    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual(['テスト'])
  })

  it('shows error message and applies error styles', () => {
    const wrapper = mount(SampleTextField, {
      props: { label: '商品名', modelValue: '', error: '必須項目です' },
    })
    expect(wrapper.text()).toContain('必須項目です')
    
    const inputEl = wrapper.find(inputSelector)
    expect(inputEl.element.style.borderColor).toBe('#ef4444')
    expect(inputEl.element.style.boxShadow).toBe('0 0 0 3px rgba(239,68,68,0.12)')
  })

  it('shows hint when no error', () => {
    const wrapper = mount(SampleTextField, {
      props: { label: '商品名', modelValue: '', hint: '任意項目です' },
    })
    expect(wrapper.text()).toContain('任意項目です')
  })

  it('shows required badge when required=true', () => {
    const wrapper = mount(SampleTextField, {
      props: { label: '商品名', modelValue: '', required: true },
    })
    expect(wrapper.text()).toContain('必須')
  })

  it('shows optional badge when required=false', () => {
    const wrapper = mount(SampleTextField, {
      props: { label: '商品名', modelValue: '', required: false },
    })
    expect(wrapper.text()).toContain('任意')
  })

  it('updates styles on focus and blur', async () => {
    const wrapper = mount(SampleTextField, {
      props: { label: '商品名', modelValue: '' },
    })
    const inputEl = wrapper.find(inputSelector)

    // 1. フォーカスを当てる
    await inputEl.trigger('focus')
    expect(inputEl.element.style.borderColor).toBe('#2563eb')
    expect(inputEl.element.style.boxShadow).toBe('0 0 0 3px rgba(37,99,235,0.1)')

    // 2. フォーカスを外す
    await inputEl.trigger('blur')
    expect(inputEl.element.style.borderColor).toBe('#e2e8f0')
  })
})