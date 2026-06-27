# データベース設計書 (Database Specification)

## 1. 概要
本プロジェクトの開発環境におけるデータベース設計を定義する。
* **採用DB**: PostgreSQL (Dockerコンテナ)
* **認証方針**: 外部IdPによる認証（OpenID Connect規格）を採用。アプリ側のDBにはパスワードなどの認証情報は保持しない。
* **タイムゾーン方針**: グローバル化を想定し、**すべてのシステム内部（DB、サーバー、API）の時間データは UTC (Coordinated Universal Time) で統一**して管理する。
* **環境構成**: セキュリティとポータビリティを担保するため、**システム名、ユーザー名、パスワードなどの接続情報はすべて環境変数から注入**する。

## 2. 接続情報と環境変数マッピング (Connection & Environment Variables)

システム（APIおよびDockerコンテナ）は、以下の環境変数を読み込んでデータベースへの接続・初期化を行う。

| 項目 (Item) | 環境変数名 (Environment Variable) | ローカル開発環境の既定値 (Default Value) | 説明 (Description) |
| :--- | :--- | :--- | :--- |
| **DBホスト名** | `DB_HOST` | `db` (Dockerサービス名) | データベースの接続先ホスト |
| **DBポート番号** | `DB_PORT` | `5432` | PostgreSQLの待受ポート |
| **データベース名** | `DB_NAME` | `lumina` | システムが使用する論理データベース名 |
| **スキーマ名** | `DB_SCHEMA` | `general` | アプリケーションが使用する分離スキーマ名 |
| **接続ユーザー名**| `DB_USER` | `luminar` | アプリケーションから接続する際の専用ユーザー |
| **接続パスワード**| `DB_PASSWORD` | `secure_dev_pass_2026` | 上記ユーザーの接続パスワード |

---

## 3. テーブル定義 (Schema)

### 3.1. `app_schema.users` (ユーザーマスター)
外部IdPで認証されたユーザーの、アプリケーション内におけるプロフィール情報を管理する。

| カラム名 (Column) | データ型 (Type) | 制約 (Constraints) | 説明 (Description) |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL / BIGINT | PRIMARY KEY | アプリ内ユーザーID (自動採番) |
| `idp_subject` | VARCHAR(255) | NOT NULL, UNIQUE | 外部IdP側におけるユーザーの一意の識別子（OIDCの `sub` クレーム値） |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | メールアドレス |
| `name` | VARCHAR(100) | NOT NULL | ユーザー名 |
| `timezone` | VARCHAR(50) | NOT NULL DEFAULT 'UTC' | ユーザーの優先タイムゾーン (例: 'Asia/Tokyo') |
| `created_at` | TIMESTAMP WITH TIME ZONE | NOT NULL | 作成日時 (UTC) |
| `updated_at` | TIMESTAMP WITH TIME ZONE | NOT NULL | 更新日時 (UTC) |

## 4. 初期データ (Seed Data)
起動時に以下の検証用データを自動投入する。
* `idp_subject`: `sso-user-uuid-0001` / `email`: `admin@example.com` / `name`: `管理者ユーザー` / `timezone`: `Asia/Tokyo`
* `idp_subject`: `sso-user-uuid-0002` / `email`: `user@example.com` / `name`: `一般ユーザー` / `timezone`: `America/New_York`