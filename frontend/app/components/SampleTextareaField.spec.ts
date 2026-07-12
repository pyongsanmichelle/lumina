import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleTextareaField from './SampleTextareaField.vue'

describe('SampleTextareaField', () => {
  it('renders label and textarea with correct attributes', () => {
    const wrapper = mount(SampleTextareaField, {
      props: { label: '備考', modelValue: '', placeholder: 'メモを入力', rows: 6 },
    })
    expect(wrapper.text()).toContain('備考')
    
    const textarea = wrapper.find('textarea')
    expect(textarea.exists()).toBe(true)
    expect(textarea.attributes('placeholder')).toBe('メモを入力')
    expect(textarea.attributes('rows')).toBe('6')
  })

  it('emits update:modelValue on input', async () => {
    const wrapper = mount(SampleTextareaField, {
      props: { label: '備考', modelValue: '' },
    })
    const textarea = wrapper.find('textarea')
    await textarea.setValue('テストメモ')
    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual(['テストメモ'])
  })

  it('shows required badge when required=true', () => {
    const wrapper = mount(SampleTextareaField, {
      props: { label: '備考', modelValue: '', required: true },
    })
    expect(wrapper.text()).toContain('必須')
  })

  it('shows optional badge when required=false', () => {
    const wrapper = mount(SampleTextareaField, {
      props: { label: '備考', modelValue: '', required: false },
    })
    expect(wrapper.text()).toContain('任意')
  })

  it('shows error message and applies error styles', () => {
    const wrapper = mount(SampleTextareaField, {
      props: { label: '備考', modelValue: '', error: '入力してください' },
    })
    expect(wrapper.text()).toContain('入力してください')
    
    const textarea = wrapper.find('textarea')
    expect(textarea.element.style.borderColor).toBe('#ef4444')
    expect(textarea.element.style.boxShadow).toBe('0 0 0 3px rgba(239,68,68,0.12)')
  })

  it('shows hint when no error', () => {
    const wrapper = mount(SampleTextareaField, {
      props: { label: '備考', modelValue: '', hint: '1000文字以内' },
    })
    expect(wrapper.text()).toContain('1000文字以内')
  })

  it('updates styles on focus and blur', async () => {
    const wrapper = mount(SampleTextareaField, {
      props: { label: '備考', modelValue: '' },
    })
    const textarea = wrapper.find('textarea')

    // 1. フォーカスを当てる (onFocus の実行)
    await textarea.trigger('focus')
    expect(textarea.element.style.borderColor).toBe('#2563eb')
    expect(textarea.element.style.boxShadow).toBe('0 0 0 3px rgba(37,99,235,0.1)')

    // 2. フォーカスを外す (onBlur の実行)
    await textarea.trigger('blur')
    expect(textarea.element.style.borderColor).toBe('#e2e8f0')
  })
})