import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleBarChart from '~/components/SampleBarChart.vue'

describe('SampleBarChart', () => {
  const mockData = [
    { name: 'カテゴリA', value: 35 },
    { name: 'カテゴリB', value: 25 },
    { name: 'カテゴリC', value: 20 },
  ]

  it('renders each category name', () => {
    const wrapper = mount(SampleBarChart, {
      props: { data: mockData },
    })
    expect(wrapper.text()).toContain('カテゴリA')
    expect(wrapper.text()).toContain('カテゴリB')
    expect(wrapper.text()).toContain('カテゴリC')
  })

  it('renders percentage values', () => {
    const wrapper = mount(SampleBarChart, {
      props: { data: mockData },
    })
    expect(wrapper.text()).toContain('35%')
    expect(wrapper.text()).toContain('25%')
    expect(wrapper.text()).toContain('20%')
  })

  it('renders bar elements', () => {
    const wrapper = mount(SampleBarChart, {
      props: { data: mockData },
    })
    const bars = wrapper.findAll('.h-full.rounded-sm')
    expect(bars.length).toBe(mockData.length)
  })

  it('uses custom maxValue when provided', () => {
    const wrapper = mount(SampleBarChart, {
      props: { data: mockData, maxValue: 100 },
    })
    const bars = wrapper.findAll('.h-full.rounded-sm')
    expect(bars.length).toBe(mockData.length)
  })

  it('handles empty data gracefully', () => {
    const wrapper = mount(SampleBarChart, {
      props: { data: [] },
    })
    expect(wrapper.exists()).toBe(true)
  })
})