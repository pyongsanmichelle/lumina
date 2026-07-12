# 認証・認可アーキテクチャ仕様 (Keycloak OIDC連携)

## 1. 基本方針 (BFF パターン)
セキュリティリスクを最小限に抑えるため、ブラウザ（Nuxt.js）にはアクセストークン（JWT）を直接露出させない「BFF（Backend For Frontend）パターン」を採用する。

## 2. 各コンポーネントの責務

### 2.1. フロントエンド (Nuxt.js)
- **トークン非保持**: アクセストークンやリフレッシュトークンは一切保持しない。
- **セッション管理**: BFFが発行するセッション Cookie (`HttpOnly`, `Secure`, `SameSite=Strict` 推奨) を用いて認証状態を維持する。
- **ログイン要求**: 未認証時（HTTP 401応答時など）は、BFFのログインエンドポイント（例: `/login`）へリダイレクトする。

### 2.2. BFF (Spring Boot / Kotlin / WebFlux)
- **OIDC クライアント**: Keycloak に対する OIDC クライアント（Relying Party）として振る舞う。
- **認証フロー制御**: `Authorization Code Flow` を実行し、Keycloak からアクセストークン、IDトークン、リフレッシュトークンを取得する。
- **セッション管理**: 取得したトークンをBFF側のセッション（インメモリ、または Redis 等）に紐付けて管理し、フロントエンドにはセッション Cookie を発行する。
- **Token Relay**: フロントエンドからの API リクエストを API サーバーへプロキシする際、セッションからアクセストークンを取り出し、`Authorization: Bearer <token>` ヘッダとして付与（Token Relay）する。

### 2.3. API サーバー (Spring Boot / Java / 4層クリーンアーキテクチャ)
- **ステートレス**: セッションは持たず、リクエストごとにトークンを検証する（Resource Server として振る舞う）。
- **トークン検証**: BFFから渡された JWT アクセストークンの署名（JWKSによる検証）、有効期限、発行者(Issuer)の検証を行う。
- **認可 (Authorization)**: JWT 内のカスタムクレーム（Keycloak の Client Roles または Realm Roles）を読み取り、エンドポイントごとに必要な権限（例: `@PreAuthorize("hasRole('ADMIN')")`）を判定する。

## 3. 認証シーケンス概要

```mermaid
sequenceDiagram
    autonumber
    
    participant Browser as フロントエンド<br>(Nuxt.js / Browser)
    participant BFF as BFF<br>(Spring Boot / Kotlin)
    participant Keycloak as Keycloak<br>(認証基盤)
    participant API as APIサーバー<br>(Spring Boot / Java)

    Note over Browser, Keycloak: 【認証フロー（Authorization Code Flow）】

    Browser->>BFF: 画面アクセス (未認証)
    BFF-->>Browser: HTTP 401 Unauthorized または ログイン画面へリダイレクト
    Browser->>BFF: ログイン要求 (/oauth2/authorization/keycloak 等)
    BFF-->>Browser: Keycloakのログイン画面へリダイレクト
    
    Browser->>Keycloak: ユーザーID/パスワード等で認証
    Keycloak-->>Browser: 認証成功 (Authorization Codeを付与してリダイレクト)
    
    Browser->>BFF: コールバックURLへアクセス (Authorization Code送信)
    BFF->>Keycloak: トークン取得要求 (Codeを送信)
    Keycloak-->>BFF: トークン一式を返却 (ID, Access, Refresh)
    
    Note over BFF: トークンをBFF側のセッション<br>(メモリ/Redis等) に保存
    
    BFF-->>Browser: セッションCookie発行 (HttpOnly, Secure)<br>& トップ画面等へリダイレクト

    Note over Browser, API: 【APIアクセスフロー（Token Relay）】

    Browser->>BFF: 業務APIリクエスト (Cookie付与)
    Note over BFF: Cookieからセッションを特定し、<br>アクセストークンを取り出す
    BFF->>API: APIリクエスト (Authorization: Bearer <token> 付与)
    Note over API: JWTの署名・有効期限・権限(Role)を検証
    API-->>BFF: 業務データを返却
    BFF-->>Browser: フロントエンド向けにデータを返却
```