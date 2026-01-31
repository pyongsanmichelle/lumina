# Web業務システム構成案（Nuxt + Spring Boot(Kotlin) + PostgreSQL）

## 目的
- フロントエンド、BFF、バックエンド、DBで責務分離
- Spring Batchを利用したバッチ処理を想定
- 本番/ステージングはAWS（ECS + RDS）で運用
- 開発はWSL2 + Docker（DBもDocker）
- IDEはIntelliJ（可能なら.devcontainer）

---

## AWS 本番/ステージング構成（推奨）
### ネットワーク
- VPC
  - Public Subnet: ALB
  - Private Subnet: ECS Tasks, RDS PostgreSQL

### コンポーネント
- ALB（HTTPS終端、WAFは必要に応じて）
- ECS(Fargate)
  - frontend（Nuxt SSR/Nodeコンテナ）※静的化できるならS3+CloudFront推奨
  - bff（Spring Boot Kotlin）
  - backend（Spring Boot Kotlin）
  - batch（Spring Batch：Scheduled Task）
- RDS PostgreSQL（Multi-AZは本番で推奨）
- Secrets Manager（DBパスワード、JWT秘密鍵など）
- SSM Parameter Store（環境変数・設定）
- CloudWatch Logs / Metrics（監視・アラート）
- EventBridge Scheduler（バッチ起動）

---

## 開発環境（WSL2 + Docker Compose）
### 目的
- `docker compose up` でフロント/BFF/バック/DBが起動
- ホットリロード・デバッグを容易に

### 構成
- frontend: Nuxt dev server
- bff: Spring Boot + DevTools
- backend: Spring Boot + DevTools
- db: PostgreSQL
- (任意) pgAdmin

---

## 推奨する実装規約
- API契約: OpenAPI（backend）/ BFFはUIに最適化したDTOを提供
- DBマイグレーション: Flyway（CI/CDまたは起動時）
- 監視: 構造化ログ(JSON) + トレース(OTel)
- セキュリティ: Secrets Manager/SSM、IAM最小権限

---

## 代替案（最適化案）
1) Nuxtを静的配信（S3+CloudFront）に寄せる（SSR不要なら最優先推奨）
2) 小規模のうちはBFFを省略し、backendに統合（成長後にBFF追加）
3) バッチが増えるならStep Functions/AWS Batchも検討