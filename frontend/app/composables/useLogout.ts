/**
 * OIDC / BFF パターンにおけるログアウト処理を担当する Composable。
 *
 * 【背景と設計ポイント】
 * Fetch や XHR で POST /bff/logout を呼び出すと、BFF が返す IdP (Keycloak等) の
 * end_session_endpoint への 302 リダイレクトを JavaScript 側で処理してしまい、
 * opaque redirect や CORS エラー、Cookie が破棄されない問題が発生します。
 *
 * そのため、本 Composable では hidden フォームを動的に生成してトップレベルナビゲーションとして
 * POST 送信します。ブラウザ自身にリダイレクトチェーンを追跡させることで、
 * 全てのセッション Cookie を確実に破棄します。
 */
export function useLogout() {
  // Nuxt コンテキストを正常に保持するため、Composable のトップレベルで呼び出します。
  // useCookie は Cookie の検索および URL デコードを自動で行ってくれます。
  const xsrfCookie = useCookie('XSRF-TOKEN');

  /**
   * ログアウト処理を実行します。
   */
  function logout(): void {
    // SSR (サーバーサイドレンダリング) 時のエラーガード
    // document などの DOM API アクセスによる Node.js 側のクラッシュを防ぎます。
    if (!import.meta.client) return;

    // Cookie から CSRF トークンを取得
    const xsrfToken = xsrfCookie.value;

    if (!xsrfToken) {
      console.warn(
        'XSRF-TOKEN cookie が見つかりません。BFF側でCSRF検証エラーになる可能性があります。'
      );
    }

    // 送信用 hidden フォームの動的生成
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/bff/logout';
    form.style.display = 'none';

    // CSRF トークンを hidden input として追加
    // Spring Boot 側の ServerCsrfTokenRequestAttributeHandler により `_csrf` パラメータが検証されます。
    if (xsrfToken) {
      const csrfInput = document.createElement('input');
      csrfInput.type = 'hidden';
      csrfInput.name = '_csrf';
      csrfInput.value = xsrfToken;
      form.appendChild(csrfInput);
    }

    // DOM に追加してフォームを送信
    document.body.appendChild(form);
    form.submit();
  }

  return {
    logout,
  };
}
