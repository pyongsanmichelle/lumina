import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import SampleAreaChart from '~/components/SampleAreaChart.vue';

describe('SampleAreaChart', () => {
  const mockData = [
    { month: '1月', 売上: 300, 目標: 250 },
    { month: '2月', 売上: 400, 目標: 350 },
    { month: '3月', 売上: 500, 目標: 450 },
  ];

  it('renders SVG with correct width and height', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: mockData, width: 600, height: 200 },
    });
    const svg = wrapper.find('svg');
    expect(svg.exists()).toBe(true);
    expect(svg.attributes('width')).toBe('600');
    expect(svg.attributes('height')).toBe('200');
  });

  it('renders default width and height when not provided', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: mockData },
    });
    const svg = wrapper.find('svg');
    expect(svg.attributes('width')).toBe('600');
    expect(svg.attributes('height')).toBe('200');
  });

  it('renders x-axis labels for each data point', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: mockData },
    });
    expect(wrapper.text()).toContain('1月');
    expect(wrapper.text()).toContain('2月');
    expect(wrapper.text()).toContain('3月');
  });

  it('renders grid lines', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: mockData },
    });
    const lines = wrapper.findAll('line');
    expect(lines.length).toBeGreaterThan(0);
  });

  it('renders polyline for sales and target', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: mockData },
    });
    const polylines = wrapper.findAll('polyline');
    expect(polylines.length).toBe(2);
  });

  it('renders area path', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: mockData },
    });
    const paths = wrapper.findAll('path');
    expect(paths.length).toBeGreaterThan(0);
  });

  it('renders data circles (dots)', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: mockData },
    });
    const circles = wrapper.findAll('circle');
    expect(circles.length).toBe(mockData.length);
  });

  it('renders gradient defs', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: mockData },
    });
    const defs = wrapper.find('defs');
    expect(defs.exists()).toBe(true);
    const gradient = defs.find('linearGradient');
    expect(gradient.exists()).toBe(true);
  });

  it('handles empty data gracefully', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: [] },
    });
    const svg = wrapper.find('svg');
    expect(svg.exists()).toBe(true);
  });

  it('データが1件のみの場合でもクラッシュせず、中央に描画されること', () => {
    const wrapper = mount(SampleAreaChart, {
      props: {
        data: [{ month: '1月', 売上: 1000, 目標: 1200 }],
      },
    });

    // len <= 1 のとき xPos は innerW.value / 2（中央）を返す実装になっている
    const circle = wrapper.find('circle');
    expect(circle.exists()).toBe(true);
    expect(Number(circle.attributes('cx'))).not.toBeNaN();
  });

  it('データが0件の場合でもエラーにならないこと', () => {
    const wrapper = mount(SampleAreaChart, {
      props: { data: [] },
    });

    // areaPath は coords.length === 0 のとき早期リターンで空文字を返す
    const path = wrapper.find('path');
    expect(path.attributes('d')).toBe('');
  });
});
