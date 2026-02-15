
# Web業務システム構成（Nuxt SSG/SPA + OIDC + BFF/Backend + Batch）

## 前提
- Frontend: Nuxt.js（SSGで静的化、SPAとして動作）
- Auth: OIDC（Entra IDを主、Keycloak/Oktaへ差し替え可能）
- Token: SPAが保持し Authorization Header でAPIへ送信
- BFF: Spring Boot(Kotlin) REST
- Backend: Spring Boot(Kotlin) REST
- Batch: Spring Batch（ECS Scheduled Task）
- AWS: S3 + CloudFront / ECS(Fargate) / RDS(PostgreSQL) / ALB / EventBridge
- Dev: WSL2 + Docker Compose（DBもDocker）、Visual Studio Code + devcontainer可能なら採用

---

## AWS構成（推奨）
### 配信
- CloudFront:
  - Origin1: S3（Nuxt静的ファイル）
  - Origin2: ALB（/api/* をBFFへ）
- S3: 静的配信（OAC/OAIでS3直アクセスを遮断）

### API
- ALB（Public Subnet）
  - /api/* を ECS上のBFFへ
- ECS(Fargate)（Private Subnet）
  - bff / backend / batch
- RDS PostgreSQL（Private Subnet）

### バッチ
- EventBridge Scheduler → ECS RunTask（batchタスク定義起動）

### 運用
- Secrets Manager: DBパスワード等
- Parameter Store: issuer-uri / audience / 各種設定
- CloudWatch Logs/Metrics: 監視
- CI/CD: GitHub Actions（ECR push → ECS更新）

---

## OIDC（IdP差し替え可能にする設計）
- issuer-uri（Discovery URL）とaudienceを環境変数化
- クレーム（roles/groups/scp等）の差はアプリ側でマッピング層で吸収
- BFF/BackendはSpring Security Resource ServerとしてJWT検証

---

## 開発環境（WSL2 + Docker）
- docker-composeで
  - frontend（nuxt dev）
  - bff（spring devtools）
  - backend（spring devtools）
  - db（postgres）
  - pgadmin（任意）
- devcontainer（任意）
  - JDK/Node/Gradle等をコンテナ内に固定
