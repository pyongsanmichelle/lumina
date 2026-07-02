---
description: プロジェクト全体の基本アーキテクチャ、技術スタック、および制約事項を定義するルール
globs: "*"
alwaysApply: true
---
# WEB業績管理システム - 全体設計憲法

## 1. 技術スタック
- **フロントエンド**: Nuxt.js (v4.4.7 / TypeScript)
- **BFF**: Spring Boot (v4.0.6 / Kotlin 2.4.0 / Gradle v9.6.0(Kotlin DSL: build.gradle.kts)  / WebFlux+コルーチンによる非同期リアクティブ構成)
- **API**: Spring Boot (v4.0.6 / Java 25 / Gradle v9.6.0(Groovy DSL: build.gradle) / 4層クリーンアーキテクチャ構成)
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

## 5. コマンド実行・動作検証ポリシー（🚨最重要）
- ビルド・テスト・アプリケーション起動など、すべての動作検証は **WSL2上のDocker Container（Docker Compose）内** でのみ実行すること。
- ホストOS上で直接 `./gradlew`, `npm run`, `npx` 等の実行や、JDK/Node.jsのバージョン探索（`find /usr/lib/jvm` 等）を行うことは禁止する。
- コマンド実行時は必ず `docker compose exec <service>` または `docker compose run --rm <service>` の形式を用いること。
  例: `docker compose exec api ./gradlew build`
- 対象のDocker Composeサービスやコンテナが未起動の場合は、先に `docker compose up -d --build` で起動してから検証コマンドを実行すること。
- ローカル環境にインストールされているJDK/Node.jsのバージョンを前提にしたコマンドを生成・実行してはならない（バージョンはコンテナ内のものに完全に依存させる）。