// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  modules: ['@nuxtjs/tailwindcss'],
  components: {
    dirs: ['~/components'],
  },
  runtimeConfig: {
    // サーバーサイド専用（publicに置かない = ブラウザにバンドルさせない）
    bffInternalOrigin: process.env.BFF_INTERNAL_ORIGIN || 'http://bff:8080',
    public: {
      // ブラウザ向け公開アドレス
      bffOrigin: process.env.BFF_ORIGIN || 'http://localhost:8080',
    },
  },

  // BFF (Spring Boot) への Nitro プロキシ転送設定
  routeRules: {
    '/bff/**': {
      proxy: {
        to: `${process.env.BFF_INTERNAL_ORIGIN || 'http://bff:8080'}/bff/**`,
        fetchOptions: {
          redirect: 'manual', // ← BFFからのLocationヘッダをそのままブラウザに転送させる
        },
      },
    },
  },
});
