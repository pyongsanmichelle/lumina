---
description: Nuxt.js (Nuxt 4) のディレクトリ構造、TypeScriptの記述規約、フロントバリデーション、テストルール
globs: "frontend/**/*"
---
# フロントエンド設計・実装原則

## 1. 基本ルール
- Nuxt 4 (v4.4.7) の標準構造（`app/` ディレクトリ主軸）に厳格に従う。
- TypeScriptにおいて `any` 型の使用は完全に禁止とする（`unknown` と型ガードを使用）。
- 画面入力バリデーションは、フォーム送信時および入力時にリアルタイムで実行する。

## 2. ディレクトリ構成
```text
frontend/
├── app/                  # 主要ソースの集約（components/, composables/, layouts/, pages/, app.vue）
├── server/               # Nuxtのサーバーサイド処理（Nitro: api/, middleware/）
├── public/               # 静的アセット
├── nuxt.config.ts
└── package.json
```

## 3. テストコード
- **Vitest** + **Vue Test Utils** を使用。`composables/` や汎用 `components/` を優先。
- 外部API（BFF）との通信は、必ず `msw` や Vitest の機能でモック化する。

## 4. ビルド・カバレッジ自動化ルール（Vitest）
- **自動実行と連動**: `package.json` の `build` スクリプトは、必ず `vitest run --coverage`（テストとカバレッジ集計）が成功した後にのみ、Nuxtのプロダクションビルド（`nuxt build`）が実行されるようにチェーンすること（例: `"build": "vitest run --coverage && nuxi build"`）。
- **品質ゲート**: カバレッジのしきい値（statements/branches/functions/lines）を **80%** に設定し、下回った場合はビルドプロセスを異常終了（Exit Code 1）させる設定を `vitest.config.ts` に含めること。
- **配置先**: カバレッジレポートは `coverage/` ディレクトリに出力し、Git管理からは除外（`.gitignore` に追加）すること。

## 5. OpenAPIによる型・クライアントの自動生成
- **手動型定義の禁止**: BFFのレスポンスを受けるためのTypeScriptのインターフェースや、`$fetch` をラップした通信ロジックを手動で作成することを禁止する。
- **Nuxt連携**: OpenAPI（またはBFFが公開するエンドポイント定義）から、`openapi-typescript` などのツールを用いて、Nuxt 4が直接利用できる型安全なAPIクライアント（Composables等）を自動生成する仕組みをスクリプト（`package.json`）に用意すること。