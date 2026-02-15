# Web業務システム構成案

## 構想

- FE、BFF、BE、DB で責務分離
  - FE: Frontend
    - Nuxt.js
    - SSG（Static Site Generation）
    - SPA
  - BFF: Backend for Frontend
    - Spring Web
    - Kotlin
    - Gradle
  - BE: Backend
    - Spring Boot
    - Kotlin
    - Gradle
- Batch
  - Spring Batch
  - Kotlin
- DB
  - PostgreSQL
  - Flyway
- 環境
  - 本番 / ステージング
    - AWS
      - VPC
        - Public Subnet
          - ALB
            - /api/* を ECS 上の BFF へ （リバースプロキシ）
        - Private Subnet
          - ECS Tasks
            - BFF / BE / Batch
          - RDS PostgreSQL
      - FE: CloudFront + S3
      - BFF: ECS（Fargate）
      - BE: ECS（Fargate）
      - DB: RDS
      - Batch: EventBridge Scheduler → ECS RunTask（batchタスク定義起動）
      - Secrets Manager
        - DB パスワード等を管理
      - SSM Parameter Store
        - issuer-uri / audience / 各種設定
      - CloudWatch Logs / Metrics
        - 監視
  - 開発
    - WSL2 + Docker（WEB / BFF / API / DB）
    - IDE
      - Visual Studio Code
      - Dev Containers
- 認証
  - OIDC
    - Token
      - SPA が保持し Authorization Header で API へ送信
  - Microsoft Entra ID
    - ※ Keycloak、Okta等に差し替え可能とする
- CI/CD
  - GitHub Actions（ECR push → ECS 更新）

---

## 1. 全体方針

- Frontend は SSR を使わず、静的生成（SSG）して S3 へ配置し、CloudFront で配信する
- API は BFF（REST）を入口にし、Backend へ内部通信する
- 認証は OIDC（Authorization Code + PKCE）を前提とし、BFF / Backend は JWT 検証する
- バッチはSpring Batchを ECS タスクとして実行し、EventBridge Schedulerで起動する
- DB は RDS PostgreSQL（Private）に配置する

---

## 2. コンポーネント

### Frontend

- Nuxt SSG 成果物を S3 へ配置
- CloudFront を通して配信
- /api/* は CloudFront の別オリジンで ALB へルーティング

### BFF (Spring Boot Kotlin / REST)

- ブラウザからの API 窓口
- OIDC トークン(JWT)検証（Resource Server）
- UI 向け DTO に整形/集約
- Backend を呼び出し

### Backend (Spring Boot Kotlin)

- ドメインロジック
- DB アクセス
- 必要に応じて JWT 検証

### Batch (Spring Batch)

- ECS Scheduled Task（EventBridge Scheduler → ECS RunTask）
- 実行ログは CloudWatch
- ジョブメタデータは PostgreSQL に保存

### DB (RDS PostgreSQL)

- Private subnet に配置
- 本番は Multi-AZ
- マイグレーションは Flyway

---

## 3. OIDC（IdP差し替え可能設計）

- issuer-uri（OIDC Discovery URL）と audience を環境変数化
- BFF / BE は Spring Security Resource Server として JWT 検証
- IdP 固有のクレーム（roles/groups/scpなど）はアプリ側でマッピング層を持つ
- これにより Entra ID / Keycloak / Okta などを設定変更で切り替え可能とする

---

## 4. 環境分離

- 本番 / ステージング は AWS アカウント分離

---

## 5. 開発環境（WSL2 + Docker Compose）

- FE: nuxt dev server
- BFF: spring boot + devtools
- BE: spring boot + devtools
- DB: postgres
