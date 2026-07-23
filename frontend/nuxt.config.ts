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
    // ブラウザからの /bff/** へのアクセスを、Nuxtサーバー(Nitro)が受け取り
    // 内部ネットワーク(http://bff:8080/bff/** 等)の Spring Boot へ転送します
    '/bff/**': {
      proxy: `${process.env.BFF_INTERNAL_ORIGIN || 'http://bff:8080'}/bff/**`,
    },
  },
});
