import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import SampleSidebar from './SampleSidebar.vue'

describe('SampleSidebar', () => {
  it('renders navigation items', () => {
    const wrapper = mount(SampleSidebar, {
      props: { currentPage: 'dashboard' },
    })
    expect(wrapper.text()).toContain('ダッシュボード')
    expect(wrapper.text()).toContain('受注一覧')
    expect(wrapper.text()).toContain('詳細照会')
    expect(wrapper.text()).toContain('新規登録')
  })

  it('emits navigate event when nav button clicked', async () => {
    const wrapper = mount(SampleSidebar, {
      props: { currentPage: 'dashboard' },
    })
    const buttons = wrapper.findAll('button')
    const found = buttons.filter((b) => b.text().includes('受注一覧'))
    expect(found.length).toBeGreaterThan(0)
    await found[0].trigger('click')
    expect(wrapper.emitted('navigate')).toBeTruthy()
    expect(wrapper.emitted('navigate')?.[0]).toEqual(['list'])
  })

  it('renders user info in footer', () => {
    const wrapper = mount(SampleSidebar, {
      props: { currentPage: 'dashboard' },
    })
    expect(wrapper.text()).toContain('山田 太郎')
    expect(wrapper.text()).toContain('管理者')
  })

  it('applies active styles for current page', () => {
    const wrapper = mount(SampleSidebar, {
      props: { currentPage: 'list' },
    })
    const buttons = wrapper.findAll('button')
    const listBtn = buttons.find((b) => b.text().includes('受注一覧'))
    expect(listBtn?.attributes('style')).toContain('background: #2563eb')
  })

  it('triggers hover effect on nav item mouseenter', async () => {
    const wrapper = mount(SampleSidebar, {
      props: { currentPage: 'dashboard' },
    })
    
    const buttons = wrapper.findAll('button')
    const listBtn = buttons.find((b) => b.text().includes('受注一覧'))
    
    if (listBtn) {
      await listBtn.trigger('mouseenter')
      
      // カラーコードをそのまま検証するように修正
      expect(listBtn.element.style.background).toBe('#1e293b')
      expect(listBtn.element.style.color).toBe('#e2e8f0')
    }
  })

  it('removes hover effect on nav item mouseleave', async () => {
    const wrapper = mount(SampleSidebar, {
      props: { currentPage: 'dashboard' },
    })
    
    const buttons = wrapper.findAll('button')
    const listBtn = buttons.find((b) => b.text().includes('受注一覧'))
    
    if (listBtn) {
      await listBtn.trigger('mouseenter')
      await listBtn.trigger('mouseleave')
      
      // カラーコードをそのまま検証するように修正
      expect(listBtn.element.style.background).toBe('transparent')
      expect(listBtn.element.style.color).toBe('#94a3b8')
    }
  })
})
