# データベース設計書 (Database Specification)

## 1. 概要

本プロジェクトの開発環境におけるデータベース設計を定義する。

- **採用DB**: PostgreSQL (Dockerコンテナ)
- **認証方針**: 外部IdPによる認証（OpenID Connect規格）を採用。アプリ側のDBにはパスワードなどの認証情報は保持しない。
- **タイムゾーン方針**: データベースのタイムゾーン設定は `Asia/Tokyo` とする。時系列に関するような時刻データのカラムは `WITH TIME ZONE` を指定して、内部的には `UTC` 時刻として保存する。ユーザー設定に応じたタイムゾーンへの変換・表示処理はアプリケーションのプレゼンテーション層が担う。
- **環境構成**: セキュリティとポータビリティを担保するため、**システム名、ユーザー名、パスワードなどの接続情報はすべて環境変数から注入**する。

## 2. テーブル一覧 (Schema Index)

各テーブルの詳細な定義は、以下のリンクを参照してください。

- [users (ユーザーマスター)](./users.md) - 外部IdP認証ユーザーのプロフィール管理
- [departments (部署マスター)](./departments.md) - 部署のマスター（階層構造対応）
- [employees (従業員マスター)](./employees.md) - 従業員の基本情報
- [employee_departments (従業員部署関連)](./employee_departments.md) - 従業員と部署の中間テーブル（兼務対応）
- [roles (ロールマスター)](./roles.md) - 権限ロールのマスター
- [user_roles (ユーザーロール関連)](./user_roles.md) - ユーザーとロールの中間テーブル
- [grades (等級マスター)](./grades.md) - 従業員等級のマスター（枝番対応）
- [grade_costs (等級原価条件)](./grade_costs.md) - 等級ごとの年度別原価・割増賃金・目標利益
- [positions (役職マスター)](./positions.md) - 役職のマスター
- [grade_positions (等級役職関連)](./grade_positions.md) - 等級と役職の中間テーブル
- [user_employee_links (ユーザー従業員関連)](./user_employee_links.md) - 外部IdPユーザーと社内従業員の紐付け
