import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { useLogout } from '~/composables/useLogout';

// useCookie の動的モック（document.cookie の変更を即座に反映する）
vi.stubGlobal('useCookie', (key: string) => {
  return {
    get value() {
      const cookieHeader = document.cookie || '';
      const cookies = cookieHeader.split('; ').reduce(
        (acc, c) => {
          if (!c) return acc;
          const [k, ...v] = c.split('=');
          acc[k.trim()] = decodeURIComponent(v.join('='));
          return acc;
        },
        {} as Record<string, string>
      );
      return cookies[key] ?? '';
    },
  };
});

describe('useLogout', () => {
  let appendChildSpy: ReturnType<typeof vi.spyOn>;
  let submitSpy: ReturnType<typeof vi.spyOn>;

  beforeEach(() => {
    // DOM状態と Cookie の初期化
    document.body.innerHTML = '';
    document.cookie = 'XSRF-TOKEN=mock-csrf-token; path=/';
    vi.clearAllMocks();

    // DOM 追加の監視
    appendChildSpy = vi.spyOn(document.body, 'appendChild');

    // form.submit() の呼び出し監視（happy-dom でのページナビゲーション抑止）
    submitSpy = vi.spyOn(HTMLFormElement.prototype, 'submit').mockImplementation(() => {});
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('hiddenフォームを生成し、POST /bff/logout に submit すること', () => {
    const { logout } = useLogout();
    logout();

    const formElement = document.querySelector('form');
    expect(formElement).not.toBeNull();
    expect(formElement!.method.toUpperCase()).toBe('POST');
    expect(formElement!.action).toContain('/bff/logout');
    expect(formElement!.style.display).toBe('none');
    expect(submitSpy).toHaveBeenCalledTimes(1);
  });

  it('hidden input（name=_csrf）に XSRF-TOKEN Cookie の値が設定されること', () => {
    const { logout } = useLogout();
    logout();

    const formElement = document.querySelector('form');
    expect(formElement).not.toBeNull();

    const csrfInput = formElement!.querySelector('input[name="_csrf"]') as HTMLInputElement;
    expect(csrfInput).not.toBeNull();
    expect(csrfInput.type).toBe('hidden');
    expect(csrfInput.value).toBe('mock-csrf-token');
  });

  it('フォームが document.body に追加されてから submit されること', () => {
    const { logout } = useLogout();
    logout();

    const formElement = document.querySelector('form');
    expect(appendChildSpy).toHaveBeenCalledWith(formElement);

    // appendChild が submit よりも前に実行されたこと（呼出順序）を検証
    const appendCallOrder = appendChildSpy.mock.invocationCallOrder[0];
    const submitCallOrder = submitSpy.mock.invocationCallOrder[0];
    expect(appendCallOrder).toBeDefined();
    expect(submitCallOrder).toBeDefined();
    expect(appendCallOrder).toBeLessThan(submitCallOrder);
  });

  it('XSRF-TOKEN Cookie がない場合でも空文字として処理すること', () => {
    // Cookie の削除
    document.cookie = 'XSRF-TOKEN=; max-age=0; path=/';

    const { logout } = useLogout();
    logout();

    const formElement = document.querySelector('form');
    expect(formElement).not.toBeNull();

    const csrfInput = formElement!.querySelector('input[name="_csrf"]') as HTMLInputElement;
    expect(csrfInput.value).toBe('');
  });
});
