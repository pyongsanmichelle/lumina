import { describe, it, expect, vi, beforeEach } from 'vitest';

// openapi-fetch の createClient をモック化し、
// 実際のHTTPクライアントは生成せず「どんな引数で呼ばれたか」だけを検証する
const mockCreateClient = vi.fn();

vi.mock('openapi-fetch', () => ({
  default: (...args: unknown[]) => mockCreateClient(...args),
}));

// モックのセットアップ後にインポートする
const { createBffAuthClient } = await import('../../app/composables/useBffAuthClient');

describe('createBffAuthClient', () => {
  beforeEach(() => {
    mockCreateClient.mockReset();
  });

  it('SSR想定: cookie を渡した場合、headers.cookie が組み立てられること', () => {
    createBffAuthClient('http://bff:8080', { cookie: 'SESSION=test-session-id' });

    expect(mockCreateClient).toHaveBeenCalledTimes(1);
    expect(mockCreateClient).toHaveBeenCalledWith({
      baseUrl: 'http://bff:8080',
      credentials: undefined,
      headers: { cookie: 'SESSION=test-session-id' },
    });
  });

  it('CSR想定: credentials を渡した場合、そのまま createClient へ渡されること', () => {
    createBffAuthClient('http://localhost:8080', { credentials: 'include' });

    expect(mockCreateClient).toHaveBeenCalledTimes(1);
    expect(mockCreateClient).toHaveBeenCalledWith({
      baseUrl: 'http://localhost:8080',
      credentials: 'include',
      headers: undefined,
    });
  });

  it('options を省略した場合、headers と credentials がともに undefined になること', () => {
    createBffAuthClient('http://localhost:8080');

    expect(mockCreateClient).toHaveBeenCalledTimes(1);
    expect(mockCreateClient).toHaveBeenCalledWith({
      baseUrl: 'http://localhost:8080',
      credentials: undefined,
      headers: undefined,
    });
  });

  it('cookie が空文字の場合、headers が undefined になること（意図しない空ヘッダ付与を防止）', () => {
    createBffAuthClient('http://bff:8080', { cookie: '' });

    expect(mockCreateClient).toHaveBeenCalledWith({
      baseUrl: 'http://bff:8080',
      credentials: undefined,
      headers: undefined,
    });
  });

  it('baseUrl が呼び出しごとに異なる値でも正しく反映されること', () => {
    createBffAuthClient('http://bff:8080');
    createBffAuthClient('http://localhost:8080');

    expect(mockCreateClient).toHaveBeenNthCalledWith(
      1,
      expect.objectContaining({ baseUrl: 'http://bff:8080' })
    );
    expect(mockCreateClient).toHaveBeenNthCalledWith(
      2,
      expect.objectContaining({ baseUrl: 'http://localhost:8080' })
    );
  });

  it('createClient の戻り値がそのまま呼び出し元に返されること', () => {
    const fakeClient = { GET: vi.fn(), POST: vi.fn() };
    mockCreateClient.mockReturnValue(fakeClient);

    const result = createBffAuthClient('http://bff:8080');

    expect(result).toBe(fakeClient);
  });
});
