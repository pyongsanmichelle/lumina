# リポジトリー構成

初期スピードと整合性（API/環境/CI）を重視して モノレポ とする。

- lumina
  - apps/
    - frontend/          # Nuxt (SSG/SPA)
    - bff/               # Spring Boot Kotlin (REST)
    - backend/           # Spring Boot Kotlin (REST)
    - batch/             # Spring Batch Kotlin
  - infra/
    - cdk/               # CloudFront/S3/ALB/ECS/RDS/EventBridge...etc
  - local/
    - docker-compose.yml
    - .env
  - .devcontainer/
    - devcontainer.json
    - Dockerfile
  - docs/