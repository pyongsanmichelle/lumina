import { describe, it, expect, vi, beforeEach } from 'vitest';

describe('useLogout', () => {
  let formAppendChildSpy: ReturnType<typeof vi.spyOn>;
  let formSubmitSpy: ReturnType<typeof vi.spyOn>;
  let formElement: HTMLFormElement | null;

  beforeEach(() => {
    vi.clearAllMocks();
    document.cookie = 'XSRF-TOKEN=mock-csrf-token; path=/';
    formElement = null;

    // document.createElement をスパイして、form要素の振る舞いを追跡できるようにする
    formAppendChildSpy = vi.spyOn(document.body, 'appendChild').mockImplementation((node) => node);
    formSubmitSpy = vi.fn();
    vi.spyOn(document, 'createElement').mockImplementation(
      (tagName: string, options?: ElementCreationOptions) => {
        const element = document.createElement(tagName, options);
        if (tagName === 'form') {
          formElement = element as HTMLFormElement;
          // form.submit() をモック化（実際のナビゲーションは発生させない）
          vi.spyOn(element as HTMLFormElement, 'submit').mockImplementation(formSubmitSpy);
        }
        return element;
      }
    );
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('hiddenフォームを生成し、POST /bff/logout に submit すること', async () => {
    const { useLogout } = await import('~/composables/useLogout');
    const { logout } = useLogout();
    logout();

    expect(formElement).not.toBeNull();
    expect(formElement!.method).toBe('POST');
    expect(formElement!.action).toContain('/bff/logout');
    expect(formElement!.style.display).toBe('none');
    expect(formSubmitSpy).toHaveBeenCalledTimes(1);
  });

  it('hidden input（name=_csrf）に XSRF-TOKEN Cookie の値が設定されること', async () => {
    const { useLogout } = await import('~/composables/useLogout');
    const { logout } = useLogout();
    logout();

    expect(formElement).not.toBeNull();
    const csrfInput = formElement!.querySelector('input[name="_csrf"]') as HTMLInputElement;
    expect(csrfInput).not.toBeNull();
    expect(csrfInput.type).toBe('hidden');
    expect(csrfInput.value).toBe('mock-csrf-token');
  });

  it('フォームが document.body に追加されてから submit されること', async () => {
    const { useLogout } = await import('~/composables/useLogout');
    const { logout } = useLogout();
    logout();

    expect(formAppendChildSpy).toHaveBeenCalledWith(formElement);
    // submit は appendChild の後に呼ばれる
    const appendCallOrder = formAppendChildSpy.mock.invocationCallOrder[0];
    const submitCallOrder = formSubmitSpy.mock.invocationCallOrder[0];
    expect(appendCallOrder).toBeLessThan(submitCallOrder!);
  });

  it('XSRF-TOKEN Cookie がない場合でも空文字として処理すること', async () => {
    // Cookie を空にする
    document.cookie = 'XSRF-TOKEN=; max-age=0';

    const { useLogout } = await import('~/composables/useLogout');
    const { logout } = useLogout();
    logout();

    expect(formElement).not.toBeNull();
    const csrfInput = formElement!.querySelector('input[name="_csrf"]') as HTMLInputElement;
    expect(csrfInput.value).toBe('');
  });
});
