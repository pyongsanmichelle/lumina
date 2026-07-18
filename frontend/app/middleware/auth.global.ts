// app/middleware/auth.global.ts
export default defineNuxtRouteMiddleware(async (to) => {
  const config = useRuntimeConfig();
  const bffOrigin = config.public.bffOrigin as string;

  // SSR時: コンテナ間通信用の BFF_INTERNAL_ORIGIN を使い、
  //        ブラウザから受け取った SESSION Cookie を手動でフォワードする。
  // CSR時: ブラウザ公開アドレス BFF_ORIGIN を使い、credentials: 'include' で
  //        クロスオリジンでも SESSION Cookie を送信する。
  const client = process.server
    ? createBffAuthClient(config.bffInternalOrigin as string, {
        cookie: useRequestHeaders(['cookie']).cookie,
      })
    : createBffAuthClient(bffOrigin, {
        credentials: 'include',
      });

  const { error } = await client.GET('/bff/auth/me');

  if (!error) {
    return;
  }

  const status =
    (error as { status?: number; statusCode?: number }).status ??
    (error as { status?: number; statusCode?: number }).statusCode;

  if (status !== 401) {
    return;
  }

  const redirectUri = encodeURIComponent(to.fullPath);
  const loginUrl = `${bffOrigin}/oauth2/authorization/keycloak?redirect_uri=${redirectUri}`;

  if (process.server) {
    // SSR: navigateTo が 302 相当のリダイレクト応答を組み立てる
    return navigateTo(loginUrl, { external: true });
  }

  // CSR: ここで永遠にresolveしないPromiseを返す必要はない。
  // window.location.href への代入により、実際のページ離脱は非同期に発生するため
  // ミドルウェア自体は正常にresolveさせて問題ない。
  window.location.href = loginUrl;
});
