import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleSelectField from './SampleSelectField.vue'

describe('SampleSelectField', () => {
  it('renders label and select', () => {
    const wrapper = mount(SampleSelectField, {
      props: { label: 'ステータス', modelValue: '' },
      slots: { default: '<option value="">選択</option>' },
    })
    expect(wrapper.text()).toContain('ステータス')
    const select = wrapper.find('select')
    expect(select.exists()).toBe(true)
  })

  it('emits update:modelValue on change', async () => {
    const wrapper = mount(SampleSelectField, {
      props: { label: 'ステータス', modelValue: '' },
      slots: { default: '<option value="">選択</option><option value="active">有効</option>' },
    })
    const select = wrapper.find('select')
    await select.setValue('active')
    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual(['active'])
  })

  it('shows required badge when required=true', () => {
    const wrapper = mount(SampleSelectField, {
      props: { label: 'ステータス', modelValue: '', required: true },
      slots: { default: '<option value="">選択</option>' },
    })
    expect(wrapper.text()).toContain('必須')
  })

  it('shows error message', () => {
    const wrapper = mount(SampleSelectField, {
      props: { label: 'ステータス', modelValue: '', error: '選択してください' },
      slots: { default: '<option value="">選択</option>' },
    })
    expect(wrapper.text()).toContain('選択してください')
  })

  it('updates styles on focus and blur', async () => {
    const wrapper = mount(SampleSelectField, {
      props: { label: 'ステータス', modelValue: '' },
      slots: { default: '<option value="">選択</option>' },
    })
    const select = wrapper.find('select')

    // 1. フォーカスを当てる (onFocus の実行)
    await select.trigger('focus')
    expect(select.element.style.borderColor).toBe('#2563eb')
    // 【修正】スペースを詰めて、Receivedの形式に完全に一致させます
    expect(select.element.style.boxShadow).toBe('0 0 0 3px rgba(37,99,235,0.1)')

    // 2. フォーカスを外す (onBlur の実行)
    await select.trigger('blur')
    expect(select.element.style.borderColor).toBe('#e2e8f0')
  })

  it('applies error styles when error prop is provided', () => {
    const wrapper = mount(SampleSelectField, {
      props: { label: 'ステータス', modelValue: '', error: 'エラーがあります' },
      slots: { default: '<option value="">選択</option>' },
    })
    const select = wrapper.find('select')
    
    // エラー時のスタイル（赤系 #ef4444）が最優先で適用されているか検証
    expect(select.element.style.borderColor).toBe('#ef4444')
  })

  it('shows hint message when hint prop is provided and no error exists', () => {
    const wrapper = mount(SampleSelectField, {
      props: { label: 'ステータス', modelValue: '', hint: '※いずれか1つ選択' },
      slots: { default: '<option value="">選択</option>' },
    })
    
    // ヒントテキストが表示されているか検証
    expect(wrapper.text()).toContain('※いずれか1つ選択')
  })
})
