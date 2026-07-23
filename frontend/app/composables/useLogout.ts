function getCookieValue(name: string): string {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  return match ? decodeURIComponent(match[2]!) : '';
}

/**
 * ログアウトを実行する composable。
 *
 * 従来のXHR（openapi-fetch）方式は、BFFが返す302リダイレクトをJavaScriptが
 * 追跡してしまう問題がありました（opaque redirect となりLocationヘッダも読めない）。
 * この問題を解決するため、hiddenフォームを動的に生成してブラウザのトップレベル
 * ナビゲーションとして POST /bff/logout に submit します。
 *
 * ブラウザはBFFから302 → Keycloakのend_session_endpoint → post_logout_redirect_uri
 * （フロントエンドトップ）までを自らナビゲーションするため、
 * 全てのセッションCookieが正しく破棄されます。
 *
 * CSRFトークンは、XSRF-TOKEN Cookie の値を hidden input（name="_csrf"）として
 * フォームに埋め込みます。
 * BFF側では ServerCsrfTokenRequestAttributeHandler を使用しているため、
 * 生のトークン値がそのまま検証されます。
 */
export function useLogout() {
  function logout(): void {
    const csrfToken = getCookieValue('XSRF-TOKEN');

    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/bff/logout';
    form.style.display = 'none';

    const csrfInput = document.createElement('input');
    csrfInput.type = 'hidden';
    csrfInput.name = '_csrf';
    csrfInput.value = csrfToken;
    form.appendChild(csrfInput);

    document.body.appendChild(form);
    form.submit();
    // form.submit() 後はブラウザがナビゲーションを行うため、
    // 以降のJavaScript処理は実行されない（ページ遷移する）
  }

  return {
    logout,
  };
}
