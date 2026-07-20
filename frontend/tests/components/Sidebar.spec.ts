import { describe, it, expect, vi, beforeEach } from 'vitest';
import { mount } from '@vue/test-utils';
import Sidebar from '~/components/Sidebar.vue';
import { useLogout } from '~/composables/useLogout';

vi.mock('~/composables/useLogout', () => ({
  useLogout: vi.fn(),
}));

describe('Sidebar', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('初期表示時にポップオーバーが非表示であること', () => {
    const wrapper = mount(Sidebar, {
      props: { currentPage: 'dashboard' },
    });
    expect(wrapper.find('[data-testid="logout-popover"]').exists()).toBe(false);
  });

  it('⏻ボタンクリックでポップオーバーが表示されること', async () => {
    const wrapper = mount(Sidebar, {
      props: { currentPage: 'dashboard' },
    });
    const powerButton = wrapper.find('button:contains("⏻")');
    await powerButton.trigger('click');
    expect(wrapper.find('[data-testid="logout-popover"]').exists()).toBe(true);
  });

  it('「ログアウト」クリックで useLogout が呼ばれること', async () => {
    const mockLogout = vi.fn().mockResolvedValue(undefined);
    (useLogout as any).mockReturnValue({ logout: mockLogout });
    const wrapper = mount(Sidebar, {
      props: { currentPage: 'dashboard' },
    });
    const powerButton = wrapper.find('button:contains("⏻")');
    await powerButton.trigger('click');
    const logoutButton = wrapper.find('button:contains("ログアウト")');
    await logoutButton.trigger('click');
    expect(mockLogout).toHaveBeenCalledTimes(1);
  });
});
