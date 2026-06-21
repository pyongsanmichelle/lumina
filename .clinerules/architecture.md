---
description: プロジェクト全体の基本アーキテクチャ、技術スタック、および制約事項を定義するルール
globs: "*"
alwaysApply: true
---
# WEB業績管理システム - 全体設計憲法

## 1. 技術スタック
- **フロントエンド**: Nuxt.js (v4.4.7 / TypeScript)
- **BFF**: Spring Boot (v4.0.6 / Java 25 / Gradle v9.5.1)
- **API**: Spring Boot (v4.0.6 / Java 25 / Gradle v9.5.1)
- **データベース**: PostgreSQL (v18) + Flyway (v12.8.1)
- **認証基盤**: Keycloak (OIDC連携)
- **インフラ**: WSL2 + Docker Container (モノレポ構成)

## 2. 厳格な制約事項（🚨最重要）
- `frontend/` は必ず `bff/` とのみ通信すること。`api/` を直接呼び出してはならない。
- `bff/` から PostgreSQL への直接接続は完全に禁止とする。データ操作はすべて `api/` を経由させる。

## 3. ログ出力（JSON構造化定義）
- バックエンドのログは、原則として JSON形式 で標準出力に構造化出力すること。
- 項目には `timestamp`, `level`, `service_name`, `trace_id`（フロント・BFF・APIを横断する共通ID）, `user_id`, `logger`, `message`, `exception` を含める。

## 4. OpenAPI & サービス間連携ルール
- **API仕様の単一真実源（Single Source of Truth）**: APIサーバー（`api/`）が提供するすべてのエンドポイントは、OpenAPI 3.0/3.1 形式の定義ファイル（`api-spec.yaml` など）で管理する。
- **WireMockによる並行開発**: APIの実装が未完了であっても、フロントエンド・BFFの開発をブロックさせないため、`api-spec.yaml` から WireMock（Dockerコンテナ）用のスタブ（モック定義）を自動生成、または手動配置し、独立して開発・検証ができる環境を `docker/` 内に構築すること。
- **通信フローのシミュレーション**: BFFからAPI、NuxtからBFFの通信テストにおいて、外部依存を排除するためにWireMockを積極的に活用すること。