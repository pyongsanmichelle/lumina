# API設計書 (API Specification)

## 1. 共通仕様 (Common Specification)
* **ベースURL**: `/api/v1`
* **データ形式**: `application/json; charset=UTF-8`
* **日時フォーマット**: ISO 8601 形式 (`YYYY-MM-DDTHH:mm:ssZ` / UTC統一)

### 1.1. 楽観ロック（Optimistic Locking）の方針
* データの同時更新によるコンフリクトを防止するため、バージョン管理による楽観ロックを適用する。
* 各リソースのエンティティには `version`（数値）を保持させる。
* 更新（PUT）リクエスト時は、クライアントは取得時の `version` 値をリクエストボディに含めて送信しなければならない。
* サーバー側で保持している最新の `version` と一致しない場合は、他のユーザーによって更新されたとみなし、`412 Precondition Failed` を返却する。

### 1.2. 共通エラーレスポンス構造
エラーの種類に応じたエラーレスポンス構造やHTTPコードの判断は [例外・エラーハンドリング定義書](error_guideline.md) を参照してください。

## 2. 機能別リソース一覧 (Endpoints Index)
各機能のAPIエンドポイント詳細は、以下のリンクを参照してください。

* [ユーザー管理 API](users.md) - ユーザーの検索・照会・登録・更新・削除