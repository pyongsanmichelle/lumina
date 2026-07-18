import { describe, it, expect, vi, beforeEach } from 'vitest';

const SSR_BFF_ORIGIN = 'http://bff:8080';
const CSR_BFF_ORIGIN = 'http://localhost:8080';

const mockNavigateTo = vi.fn();
const mockUseRuntimeConfig = vi.fn();
const mockUseRequestHeaders = vi.fn();
const mockGetAuthMe = vi.fn();

function setupGlobalMocks() {
  vi.stubGlobal('defineNuxtRouteMiddleware', (fn: Function) => fn);
  vi.stubGlobal('useRuntimeConfig', () => mockUseRuntimeConfig());
  vi.stubGlobal('useRequestHeaders', (keys: string[]) => mockUseRequestHeaders(keys));
  vi.stubGlobal('navigateTo', (url: string, options?: { external?: boolean }) =>
    mockNavigateTo(url, options)
  );
  vi.stubGlobal('createBffAuthClient', () => ({
    GET: async () => mockGetAuthMe(),
  }));
  vi.stubGlobal('window', { location: { pathname: '/dashboard', href: '' } });
  // process は各 describe の beforeEach で設定済みのため、ここでは上書きしない
}

async function reloadMiddleware() {
  vi.resetModules();
  setupGlobalMocks();
  const mod = await import('../../app/middleware/auth.global');
  return mod.default;
}

beforeEach(() => {
  mockNavigateTo.mockReset();
  mockUseRuntimeConfig.mockReset();
  mockUseRequestHeaders.mockReset();
  mockGetAuthMe.mockReset();
  // 各テストの前提として、まず process をリセットしておく
  vi.stubGlobal('process', { server: false, client: false, env: {} });
});

describe('auth.global middleware (SSR)', () => {
  beforeEach(() => {
    // SSR環境として振る舞うように上書き
    vi.stubGlobal('process', { server: true, client: false, env: {} });

    mockUseRuntimeConfig.mockReturnValue({
      bffInternalOrigin: SSR_BFF_ORIGIN,
      public: { bffOrigin: CSR_BFF_ORIGIN },
    });
    mockUseRequestHeaders.mockReturnValue({ cookie: 'SESSION=test-session-id' });
  });

  it('/bff/auth/me が200を返す場合、リダイレクトが発生しないこと', async () => {
    mockGetAuthMe.mockResolvedValue({
      data: { authenticated: true, userId: 'user-12345', username: 'taro.yamada', roles: ['USER'] },
      error: undefined,
    });

    const mw = await reloadMiddleware();
    const to = { fullPath: '/dashboard' };
    const result = await mw(to);

    expect(result).toBeUndefined();
    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it('/bff/auth/me が401を返す場合、KeycloakログインURLへリダイレクトすること', async () => {
    mockGetAuthMe.mockResolvedValue({
      data: undefined,
      error: { status: 401 },
    });

    const mw = await reloadMiddleware();
    const to = { fullPath: '/dashboard' };
    const result = await mw(to);

    expect(mockNavigateTo).toHaveBeenCalledTimes(1);
    const calledUrl = mockNavigateTo.mock.calls[0][0];
    expect(calledUrl).toContain(`${CSR_BFF_ORIGIN}/oauth2/authorization/keycloak`);
    expect(calledUrl).toContain('redirect_uri=%2Fdashboard');
    expect(mockNavigateTo.mock.calls[0][1]).toEqual({ external: true });
  });

  it('redirect_uri が encodeURIComponent でエンコードされていること', async () => {
    mockGetAuthMe.mockResolvedValue({
      data: undefined,
      error: { status: 401 },
    });

    const mw = await reloadMiddleware();
    const to = { fullPath: '/some/path?with=query&param=value' };
    await mw(to);

    const calledUrl = mockNavigateTo.mock.calls[0][0];

    // URLSearchParams.get() は取得時に自動でデコードするため、
    // 「デコード後の値が元のパスと一致する」ことを確認することで
    // 「エンコードされた状態でURLに埋め込まれていたこと」を間接的に検証する
    const urlObj = new URL(calledUrl, CSR_BFF_ORIGIN);
    const redirectUri = urlObj.searchParams.get('redirect_uri');
    expect(redirectUri).toBe('/some/path?with=query&param=value');

    // 生のURL文字列自体にエンコード済みの記号が含まれていることも直接検証する
    expect(calledUrl).toContain(encodeURIComponent('/some/path?with=query&param=value'));
  });

  it('/bff/auth/me が401以外のエラー(500)を返す場合、リダイレクトしないこと', async () => {
    mockGetAuthMe.mockResolvedValue({
      data: undefined,
      error: { status: 500 },
    });

    const mw = await reloadMiddleware();
    const to = { fullPath: '/dashboard' };
    const result = await mw(to);

    expect(result).toBeUndefined();
    expect(mockNavigateTo).not.toHaveBeenCalled();
  });

  it('error.statusCode（status ではなく statusCode プロパティ）でも401判定できること', async () => {
    mockGetAuthMe.mockResolvedValue({
      data: undefined,
      error: { statusCode: 401 },
    });

    const mw = await reloadMiddleware();
    const to = { fullPath: '/dashboard' };
    await mw(to);

    expect(mockNavigateTo).toHaveBeenCalledTimes(1);
  });
});

describe('auth.global middleware (CSR)', () => {
  beforeEach(() => {
    // CSR環境として振る舞うように上書き
    vi.stubGlobal('process', { server: false, client: true, env: {} });

    mockUseRuntimeConfig.mockReturnValue({
      bffInternalOrigin: SSR_BFF_ORIGIN,
      public: { bffOrigin: CSR_BFF_ORIGIN },
    });
  });

  it('/bff/auth/me が200を返す場合、リダイレクトが発生しないこと', async () => {
    mockGetAuthMe.mockResolvedValue({
      data: { authenticated: true, userId: 'user-12345', username: 'taro.yamada', roles: ['USER'] },
      error: undefined,
    });

    const mw = await reloadMiddleware();
    const to = { fullPath: '/dashboard' };
    await mw(to);

    expect(window.location.href).toBe('');
  });

  it('/bff/auth/me が401を返す場合、window.location.href でリダイレクトすること', async () => {
    mockGetAuthMe.mockResolvedValue({
      data: undefined,
      error: { status: 401 },
    });

    const mw = await reloadMiddleware();
    const to = { fullPath: '/dashboard' };
    await mw(to);

    expect(window.location.href).toContain(`${CSR_BFF_ORIGIN}/oauth2/authorization/keycloak`);
    expect(window.location.href).toContain('redirect_uri=%2Fdashboard');
  });
});
