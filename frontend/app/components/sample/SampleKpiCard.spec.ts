import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleKpiCard from './SampleKpiCard.vue'

describe('SampleKpiCard', () => {
  const props = {
    label: '今月の売上',
    value: '¥48,200,000',
    delta: '+12.4%',
    deltaUp: true,
    sub: '前月比',
  }

  it('renders label, value, delta and sub text', () => {
    const wrapper = mount(SampleKpiCard, { props })
    expect(wrapper.text()).toContain('今月の売上')
    expect(wrapper.text()).toContain('¥48,200,000')
    expect(wrapper.text()).toContain('+12.4%')
    expect(wrapper.text()).toContain('前月比')
  })

  it('shows ▲ for deltaUp=true', () => {
    const wrapper = mount(SampleKpiCard, { props })
    expect(wrapper.text()).toContain('▲')
  })

  it('shows ▼ for deltaUp=false', () => {
    const wrapper = mount(SampleKpiCard, {
      props: { ...props, deltaUp: false },
    })
    expect(wrapper.text()).toContain('▼')
  })
})