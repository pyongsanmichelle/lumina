function getCookieValue(name: string): string {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  return match ? decodeURIComponent(match[2]!) : '';
}

import { createBffAuthClient } from './useBffAuthClient';

export function useLogout() {
  const config = useRuntimeConfig();
  const client = createBffAuthClient(config.public.bffOrigin, { credentials: 'include' });

  async function logout() {
    try {
      const csrf = getCookieValue('XSRF-TOKEN');
      const { error } = await client.post('/bff/logout', {
        headers: { 'X-XSRF-TOKEN': csrf },
      });
      if (!error) {
        window.location.href = '/';
      }
    } catch {
      // バックチャネルログアウト失敗時もブラウザをルートへ遷移させる
      window.location.href = '/';
    }
  }

  return {
    logout,
  };
}
