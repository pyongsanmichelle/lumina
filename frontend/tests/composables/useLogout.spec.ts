import { describe, it, expect, vi, beforeEach } from 'vitest';

vi.mock('~/composables/useBffAuthClient', () => ({
  createBffAuthClient: vi.fn(() => ({
    post: vi.fn(),
  })),
}));

describe('useLogout', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    document.cookie = 'XSRF-TOKEN=mock-csrf-token; path=/';
    delete (window as any).location;
    (window as any).location = { href: '' };
  });

  it('POST /logout を呼び出し X-XSRF-TOKEN ヘッダに Cookie 値を設定すること', async () => {
    const mockPost = vi.fn().mockResolvedValue({ error: null });
    const { createBffAuthClient } = await import('~/composables/useBffAuthClient');
    (createBffAuthClient as any).mockReturnValue({ post: mockPost });

    const { useLogout } = await import('~/composables/useLogout');
    const { logout } = useLogout();
    await logout();

    expect(mockPost).toHaveBeenCalledWith('/bff/logout', {
      headers: { 'X-XSRF-TOKEN': 'mock-csrf-token' },
    });
  });

  it('204 受信時に window.location.href が "/" になること', async () => {
    const mockPost = vi.fn().mockResolvedValue({ error: null });
    const { createBffAuthClient } = await import('~/composables/useBffAuthClient');
    (createBffAuthClient as any).mockReturnValue({ post: mockPost });

    const { useLogout } = await import('~/composables/useLogout');
    const { logout } = useLogout();
    await logout();

    expect((window as any).location.href).toBe('/');
  });

  it('エラー時も window.location.href が "/" になること', async () => {
    const mockPost = vi.fn().mockRejectedValue(new Error('network error'));
    const { createBffAuthClient } = await import('~/composables/useBffAuthClient');
    (createBffAuthClient as any).mockReturnValue({ post: mockPost });

    const { useLogout } = await import('~/composables/useLogout');
    const { logout } = useLogout();
    await logout();

    expect((window as any).location.href).toBe('/');
  });
});
