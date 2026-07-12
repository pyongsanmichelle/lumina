import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleReadField from './SampleReadField.vue'

describe('SampleReadField', () => {
  it('renders label and value', () => {
    const wrapper = mount(SampleReadField, {
      props: { label: '顧客名', value: '株式会社山田商事' },
    })
    expect(wrapper.text()).toContain('顧客名')
    expect(wrapper.text()).toContain('株式会社山田商事')
  })

  it('renders with mono font family when mono=true', () => {
    const wrapper = mount(SampleReadField, {
      props: { label: 'コード', value: 'CLI-001', mono: true },
    })
    expect(wrapper.text()).toContain('CLI-001')
  })
})